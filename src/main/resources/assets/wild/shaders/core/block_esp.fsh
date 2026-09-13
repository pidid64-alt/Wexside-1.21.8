#version 150

layout(std140) uniform BlockEsp {
    mat4 EspView;
    vec4 EspCamera;
    vec4 EspFade;
    vec4 EspStyle;
    vec4 EspTune;
    vec4 EspRange;
    vec4 EspLocal;
    vec4 EspSurface;
};

#define AnimClock    EspCamera.w
#define FadeStart    EspFade.x
#define FadeEnd      EspFade.y
#define MasterAlpha  EspFade.z
#define EventClock   EspFade.w
#define EdgeGain     EspStyle.x
#define VolumeGain   EspStyle.y
#define FresnelGain  EspStyle.z
#define NearFade     EspStyle.w
#define PulseDepth   EspTune.x
#define OccludeDim   EspTune.y
#define LineWidth    EspTune.z
#define Saturation   EspTune.w
#define NearEnd      EspRange.x
#define FarStart     EspRange.y
#define EmberFloor   EspRange.z
#define EmberScale   EspRange.w
#define LocalOrigin  EspLocal.xyz
#define DetailGain   EspLocal.w
#define VeinGain     EspSurface.x
#define FilmGain     EspSurface.y
#define SparkGain    EspSurface.z
#define PulseGain    EspSurface.w

in vec2 vFace;
in vec3 vTint;
in vec3 vView;
in float vRise;
flat in vec3 vNormal;
flat in float vPhase;
flat in float vEvent;
flat in int vFlags;

out vec4 fragColor;

const float TAU = 6.2831853072;

// The CPU snaps the noise origin to a 256 block lattice and every lattice field below wraps on
// exactly that period, so the whole surface pattern is glued to the world: it never swims with the
// camera, and crossing a snap boundary is bit-for-bit invisible. Keeping the sampled coordinate
// small is also what keeps the float hashes crisp - a raw world coordinate destroys them.
const float SNAP_BLOCKS = 256.0;

// Feature scales in lattice cells per block, and the matching wrap period (SNAP_BLOCKS * scale).
const float F_LOW = 0.375;
const float W_LOW = 96.0;
const float F_VEIN = 1.0;
const float W_VEIN = 256.0;
const float F_SPARK = 2.5;
const float W_SPARK = 640.0;

// AnimClock wraps every 3600 s. Every drift velocity is a multiple of 256/3600 blocks per second
// and every periodic phase below completes a whole number of cycles in 3600 s, so the hourly wrap
// produces no discontinuity anywhere in the stack.
const float DRIFT_UNIT = 0.0711111111;
const float BREATH_RATE = 1.2496557;
const float PULSE_RATE = 0.085;

const vec3 KEY_DIR = vec3(0.5613, 0.7786, 0.2806);
const vec3 FILL_DIR = vec3(-0.4187, 0.2991, -0.8574);

const float VEIN_HALF = 0.030;
const float VEIN_THREAD = 0.018;
const float VEIN_BLOOM = 0.110;
const float FILM_BASE = 0.30;
const float SPARK_RADIUS = 0.19;

// Dave Hoskins hash - no sin(), no bit tricks, stable on every driver at #version 150.
vec3 espHash33(vec3 p) {
    p = fract(p * vec3(0.1031, 0.1030, 0.0973));
    p += dot(p, p.yxz + 33.33);
    return fract((p.xxy + p.yxx) * p.zyx);
}

vec3 espGrad(vec3 cell, float period) {
    vec3 h = espHash33(mod(cell, vec3(period))) * 2.0 - 1.0;
    return h * inversesqrt(max(dot(h, h), 1.0e-3));
}

// Perlin gradient noise with exact analytic derivatives. The derivative is what lets every edge
// derived from this field be antialiased and band limited without calling fwidth() inside the
// distance branches, which would be undefined behaviour.
vec4 espNoised(vec3 x, float period) {
    vec3 i = floor(x);
    vec3 w = x - i;

    vec3 u = w * w * w * (w * (w * 6.0 - 15.0) + 10.0);
    vec3 du = 30.0 * w * w * (w * (w - 2.0) + 1.0);

    vec3 ga = espGrad(i, period);
    vec3 gb = espGrad(i + vec3(1.0, 0.0, 0.0), period);
    vec3 gc = espGrad(i + vec3(0.0, 1.0, 0.0), period);
    vec3 gd = espGrad(i + vec3(1.0, 1.0, 0.0), period);
    vec3 ge = espGrad(i + vec3(0.0, 0.0, 1.0), period);
    vec3 gf = espGrad(i + vec3(1.0, 0.0, 1.0), period);
    vec3 gg = espGrad(i + vec3(0.0, 1.0, 1.0), period);
    vec3 gh = espGrad(i + vec3(1.0, 1.0, 1.0), period);

    float va = dot(ga, w);
    float vb = dot(gb, w - vec3(1.0, 0.0, 0.0));
    float vc = dot(gc, w - vec3(0.0, 1.0, 0.0));
    float vd = dot(gd, w - vec3(1.0, 1.0, 0.0));
    float ve = dot(ge, w - vec3(0.0, 0.0, 1.0));
    float vf = dot(gf, w - vec3(1.0, 0.0, 1.0));
    float vg = dot(gg, w - vec3(0.0, 1.0, 1.0));
    float vh = dot(gh, w - vec3(1.0, 1.0, 1.0));

    float k1 = vb - va;
    float k2 = vc - va;
    float k3 = ve - va;
    float k4 = va - vb - vc + vd;
    float k5 = va - vc - ve + vg;
    float k6 = va - vb - ve + vf;
    float k7 = -va + vb + vc - vd + ve - vf - vg + vh;

    float value = va + k1 * u.x + k2 * u.y + k3 * u.z
                + k4 * u.x * u.y + k5 * u.y * u.z + k6 * u.z * u.x
                + k7 * u.x * u.y * u.z;

    vec3 slope = ga
               + u.x * (gb - ga) + u.y * (gc - ga) + u.z * (ge - ga)
               + u.x * u.y * (ga - gb - gc + gd)
               + u.y * u.z * (ga - gc - ge + gg)
               + u.z * u.x * (ga - gb - ge + gf)
               + u.x * u.y * u.z * (-ga + gb + gc - gd + ge - gf - gg + gh)
               + du * vec3(k1 + k4 * u.y + k6 * u.z + k7 * u.y * u.z,
                           k2 + k5 * u.z + k4 * u.x + k7 * u.z * u.x,
                           k3 + k6 * u.x + k5 * u.y + k7 * u.x * u.y);

    return vec4(value, slope);
}

vec3 espDrift(vec3 blocksPerSecond, float scale, float period) {
    return mod(AnimClock * blocksPerSecond * scale, vec3(period));
}

// Jimenez interleaved gradient noise shaped to a triangular PDF - the correct dither for an 8 bit
// target. It is fixed per screen pixel, so it never flickers.
float espTpdf(vec2 fragment) {
    float o = fract(52.9829189 * fract(dot(fragment, vec2(0.06711056, 0.00583715)))) * 2.0 - 1.0;
    return sign(o) * (1.0 - sqrt(max(0.0, 1.0 - abs(o))));
}

void main() {
    float viewDistance = length(vView);
    vec3 toCamera = vView / max(viewDistance, 1e-4);
    vec3 normal = normalize(vNormal);
    vec3 absNormal = abs(normal);
    float facing = clamp(dot(normal, toCamera), 0.0, 1.0);

    // Camera snapped world position. Both terms are small, so this is exact where a raw world
    // coordinate would already have lost the low bits the detail layers live in.
    vec3 world = LocalOrigin - vView;
    vec3 ddx = dFdx(world);
    vec3 ddy = dFdy(world);
    float footprint = max(length(ddx), length(ddy));

    float rich = 1.0 - smoothstep(NearEnd, FarStart, viewDistance);
    float ember = 1.0 - rich;

    float detailLod = DetailGain * rich * (1.0 - smoothstep(0.55, 1.60, footprint));
    float veinLod = detailLod * (1.0 - smoothstep(0.16, 0.52, footprint));
    float sparkLod = detailLod * (1.0 - smoothstep(0.05, 0.20, footprint));

    // --- silhouette of the merged cluster ------------------------------------------------------
    int edgeMask = vFlags & 15;
    float du = max(fwidth(vFace.x), 1e-5);
    float dv = max(fwidth(vFace.y), 1e-5);
    float pixels = 1.0e9;
    float inset = 8.0;
    if ((edgeMask & 1) != 0) {
        pixels = min(pixels, vFace.x / du);
        inset = min(inset, vFace.x);
    }
    if ((edgeMask & 2) != 0) {
        pixels = min(pixels, (1.0 - vFace.x) / du);
        inset = min(inset, 1.0 - vFace.x);
    }
    if ((edgeMask & 4) != 0) {
        pixels = min(pixels, vFace.y / dv);
        inset = min(inset, vFace.y);
    }
    if ((edgeMask & 8) != 0) {
        pixels = min(pixels, (1.0 - vFace.y) / dv);
        inset = min(inset, 1.0 - vFace.y);
    }
    float line = 1.0 - smoothstep(LineWidth - 0.65, LineWidth + 0.65, pixels);
    float halo = exp(-inset * 7.0);

    // --- layer 1: base volume ------------------------------------------------------------------
    float rise = clamp(vRise, 0.0, 1.0);
    float pool = pow(mix(1.0, 0.42, rise), 0.4545);
    float core = pow(facing, 1.5);
    float veil = 0.28 + 0.72 * core;

    // --- layer 2: per face shading -------------------------------------------------------------
    float nkey = dot(normal, KEY_DIR);
    float nfill = dot(normal, FILL_DIR);
    float sky = normal.y * 0.5 + 0.5;
    // Weighted so the six faces average exactly 1.0: the volume keeps the brightness it had before
    // the rig existed, and all the rig does is redistribute it across the facets.
    float facet = 0.50
                + 0.66 * (nkey * 0.5 + 0.5)
                + 0.29 * max(nfill, 0.0)
                + 0.18 * sky;

    vec3 mirror = reflect(-toCamera, normal);
    float gloss = pow(max(dot(mirror, KEY_DIR), 0.0), 26.0);
    float sheen = pow(max(dot(mirror, FILL_DIR), 0.0), 11.0);

    // A per axis chroma identity separates perpendicular faces far more legibly than the few
    // percent of value the light rig alone can give them, and it stays inside the block's own hue.
    vec3 axisTint = vec3(1.0)
                  + absNormal.x * vec3(0.048, -0.010, -0.043)
                  + absNormal.z * vec3(-0.040, -0.006, 0.055)
                  + max(normal.y, 0.0) * vec3(0.026, 0.018, -0.010);

    // --- layer 3: fresnel rim ------------------------------------------------------------------
    float grazing = 1.0 - facing;
    float grazing2 = grazing * grazing;
    float fresnel = 0.34 * grazing2 + 0.66 * grazing2 * grazing2 * grazing;

    // --- layers 4 and 5 source fields ----------------------------------------------------------
    float veinCore = 0.0;
    float veinHalo = 0.0;
    float bodyVar = 1.0;
    float filmVar = 0.0;

    if (detailLod > 0.003) {
        vec4 low = espNoised(world * F_LOW + espDrift(vec3(1.0, -1.0, 2.0) * DRIFT_UNIT, F_LOW, W_LOW), W_LOW);
        float lowValue = low.x * 1.45;
        vec3 lowSlope = low.yzw;

        bodyVar = 1.0 + 0.16 * lowValue * detailLod;
        filmVar = lowValue * detailLod;

        if (veinLod > 0.004) {
            // Domain warp the vein lattice by the slow field's own gradient: organic, continuous
            // in time, and free because the gradient is already computed.
            vec3 veinPos = world * F_VEIN
                         + espDrift(vec3(-1.0, -2.0, 1.0) * DRIFT_UNIT, F_VEIN, W_VEIN)
                         + lowSlope * 0.38;
            vec4 vein = espNoised(veinPos, W_VEIN);

            // The carrier's own value is free marbling at exactly the scale a single cluster needs
            // to stop reading as one flat plate.
            bodyVar *= 1.0 + 0.11 * vein.x * 1.45 * veinLod;

            float signal = vein.x * 1.45;
            float ridge = 1.0 - abs(signal);

            // The field's own slope, in world blocks. Every band below is a level set of the same
            // field, so one screen-space width serves all of them.
            vec3 fieldSlope = vein.yzw * (1.45 * F_VEIN);
            float sx = dot(fieldSlope, ddx);
            float sy = dot(fieldSlope, ddy);
            float aa = max(sqrt(sx * sx + sy * sy), 1.0e-5);

            // Widen the band by the screen footprint and drop its amplitude by the same ratio:
            // the filament converges to its own average instead of aliasing into pixel noise.
            float crisp = smoothstep(1.0 - VEIN_HALF - aa, 1.0 - VEIN_HALF + aa, ridge);
            crisp *= VEIN_HALF / (VEIN_HALF + aa);

            // A second, finer thread off the same evaluation. Its level wanders with the slow
            // field instead of sitting at a fixed offset, so it crosses the main filament rather
            // than ringing it - two fixed levels of one field read as a contour map.
            float thread = 1.0 - abs(signal - (0.34 + 0.30 * lowValue));
            float fine = smoothstep(1.0 - VEIN_THREAD - aa, 1.0 - VEIN_THREAD + aa, thread);
            fine *= VEIN_THREAD / (VEIN_THREAD + aa);

            float bloom = smoothstep(1.0 - VEIN_BLOOM - aa, 1.0 - aa * 0.5, ridge);
            bloom *= VEIN_BLOOM / (VEIN_BLOOM + aa);

            // Veins are meant to be rare and noble, so a decorrelated slice of the slow field
            // decides where they run strong and where they thin out to almost nothing. The floor
            // matters: with a hard zero a whole small cluster can land in a dead patch and get no
            // veins at all.
            float rarity = 0.12 + 0.88 * smoothstep(-0.15, 0.60, lowValue * 0.7 + lowSlope.y * 0.35);

            veinCore = (crisp + fine * 0.34) * rarity * veinLod;
            veinHalo = bloom * rarity * veinLod;
        }
    }

    // --- layer 6: micro sparks -----------------------------------------------------------------
    float sparkFlash = 0.0;
    if (sparkLod > 0.004) {
        vec2 tangent;
        float depth;
        if (absNormal.x > 0.5) {
            tangent = world.zy;
            depth = world.x;
        } else if (absNormal.y > 0.5) {
            tangent = world.xz;
            depth = world.y;
        } else {
            tangent = world.xy;
            depth = world.z;
        }

        vec2 cellPos = tangent * F_SPARK;
        vec2 cellIndex = floor(cellPos);
        vec2 inCell = cellPos - cellIndex;
        vec3 seed = espHash33(mod(vec3(cellIndex, floor(depth * F_SPARK)), vec3(W_SPARK)));

        vec2 jitter = seed.yz * 0.52 + 0.24;
        float radial = length(inCell - jitter);
        float radialAA = max(footprint * F_SPARK, 1.0e-4);
        float disc = 1.0 - smoothstep(SPARK_RADIUS - radialAA, SPARK_RADIUS + radialAA, radial);
        disc *= SPARK_RADIUS / (SPARK_RADIUS + radialAA);

        // Rate quantised to whole cycles per clock wrap, so no spark ever jumps mid flash. It is
        // drawn from a channel the jitter does not use, or a spark's position would predict its
        // tempo and the field would develop a readable rhythm.
        float rate = (360.0 + floor(fract(seed.x * 37.0 + seed.z * 11.0) * 288.0)) * (1.0 / 3600.0);
        float wave = sin(fract(AnimClock * rate + seed.z) * TAU);
        float envelope = pow(max(wave, 0.0), 6.0);

        sparkFlash = step(0.80, seed.x) * disc * envelope * sparkLod * SparkGain;
    }

    // --- layer 7: rising refresh pulse ---------------------------------------------------------
    float sweep = fract(AnimClock * PULSE_RATE + vPhase);
    float head = sweep * 1.90 - 0.45;
    float offset = rise - head;
    float pulse = exp(-offset * offset * 26.0) * PulseGain * mix(0.45, 1.0, rich);

    // --- layer 5: thin film iridescence --------------------------------------------------------
    // Film thickness climbs the cluster, so the spectral order drifts under a cycle from bottom to
    // top: an oil sheen, not a spectrum. The floor under facing is load bearing - the physical
    // 1/cos path length runs away at grazing incidence and paints a full prismatic rainbow along
    // every silhouette, which is the single loudest way this layer can turn into slop.
    float film = FILM_BASE * (1.0 + 0.55 * rise + 0.30 * filmVar + 0.20 * vPhase);
    float optical = film / max(facing, 0.50);
    vec3 spectrum = 0.5 + 0.5 * cos(TAU * (optical * vec3(1.00, 0.91, 0.82) + vec3(0.0, 0.17, 0.34)));
    spectrum -= dot(spectrum, vec3(0.3333333));

    // A near neutral block has no hue for the shift to stay in kinship with, so on iron or quartz
    // the film reads as injected colour rather than as a property of the material. Anchor its
    // amplitude to the block's own chroma and those turn into a pearl shimmer instead.
    float tintChroma = max(max(vTint.r, vTint.g), vTint.b) - min(min(vTint.r, vTint.g), vTint.b);
    // Peaks at moderate grazing and backs off again at the silhouette. Letting it keep climbing
    // to edge-on is what paints prismatic stripes down every narrow foreshortened face.
    float filmShape = (0.20 + 0.80 * smoothstep(0.05, 0.60, grazing))
                    * (1.0 - 0.55 * smoothstep(0.80, 0.99, grazing));
    float filmWeight = FilmGain
                     * filmShape
                     * (0.28 + 0.72 * smoothstep(0.06, 0.40, tintChroma))
                     * mix(0.55, 1.0, rich);
    vec3 iridescence = vec3(1.0) + spectrum * filmWeight;

    // --- composition ---------------------------------------------------------------------------
    float fill = veil * pool * facet * bodyVar
               + halo * 0.58
               + pulse * 0.50;

    float lineWeight = clamp(line * EdgeGain, 0.0, 1.0) * mix(0.55, 1.0, rich);
    float bodyWeight = clamp(fill * VolumeGain, 0.0, 1.0);
    bodyWeight = mix(bodyWeight, max(bodyWeight, EmberFloor), ember);
    float hotWeight = clamp(fresnel * FresnelGain
                          + (veinCore * 0.22 + veinHalo * 0.08) * VeinGain
                          + (gloss * 0.20 + sheen * 0.18) * mix(0.35, 1.0, rich)
                          + sparkFlash * 0.25
                          + pulse * 0.28, 0.0, 1.0);

    // Energy that adds light without adding occlusion. Coverage saturates - a filament laid on top
    // of an already dense body barely moves the alpha and vanishes. Emission does not saturate the
    // same way, so the filament reads as a filament.
    float emissive = (veinCore * 1.15 + veinHalo * 0.12) * VeinGain
                   + sparkFlash * 1.20
                   + gloss * 0.62 * mix(0.35, 1.0, rich)
                   + pulse * 0.34;

    float breath = 1.0 + PulseDepth * sin(AnimClock * BREATH_RATE + vPhase * TAU);

    float known = step(0.0, vEvent);
    float dying = float((vFlags & 16) >> 4);
    float age = EventClock - vEvent;
    age += 512.0 * step(age, 0.0);
    float ignite = known * (1.0 - dying) * exp(-max(age - vPhase * 0.35, 0.0) * 3.4);
    float life = mix(1.0, clamp(1.0 - age / 0.52, 0.0, 1.0), dying * known);

    float horizon = 1.0 - smoothstep(FadeStart, FadeEnd, viewDistance);
    float pocket = smoothstep(0.0, max(NearFade, 1e-4), viewDistance);

    float presence = mix(EmberScale, 1.0, rich)
                   * breath * life * horizon * pocket * MasterAlpha * OccludeDim
                   * (1.0 + ignite * 1.9);

    float density = 1.0 - (1.0 - lineWeight) * (1.0 - bodyWeight) * (1.0 - hotWeight);
    density *= presence;

    float strength = 1.0 - exp(-density * 1.45);
    float emission = (1.0 - exp(-emissive * 1.70)) * presence * mix(0.30, 1.0, rich);
    if (strength < 0.004 && emission < 0.004) {
        discard;
    }

    vec3 base = vTint;
    float lum = dot(base, vec3(0.2126, 0.7152, 0.0722));
    base = mix(vec3(lum), base, Saturation);
    base *= mix(1.0, 0.76 / max(lum, 0.08), 0.55);
    base = max(base, vec3(0.0));
    base /= max(max(max(base.r, base.g), base.b), 1.0);

    base *= axisTint * iridescence;
    base = max(base, vec3(0.0));
    base /= max(max(max(base.r, base.g), base.b), 1.0);

    vec3 filament = mix(base, vec3(1.0), 0.40 + 0.22 * (1.0 - lum));
    float hotShare = clamp((lineWeight + hotWeight * 0.70)
            / max(lineWeight + hotWeight + bodyWeight, 1e-4), 0.0, 1.0);

    vec3 glow = mix(base, filament, hotShare) * strength * (1.0 + 0.60 * hotShare)
              + filament * emission * 0.72;

    // Hue preserving ceiling. A per channel min() would clip the strongest channel first and drag
    // every hot spot toward white, which is exactly the look this is meant to avoid.
    float peak = max(max(glow.r, glow.g), glow.b);
    glow /= max(peak, 1.0);

    // One triangular LSB on colour and alpha together keeps the premultiplied pair consistent and
    // removes every ring from the wide exponential and fresnel ramps.
    float dither = espTpdf(gl_FragCoord.xy) * (1.25 / 255.0);
    fragColor = vec4(max(glow + dither, vec3(0.0)), clamp(strength + dither, 0.0, 1.0));
}
