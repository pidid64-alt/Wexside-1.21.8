#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec4 uBody;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uPhase;
uniform float uTime;
uniform float uEntry;
uniform float uAlpha;
uniform float uSeed;
uniform float uLightMode;
uniform vec2 uPointer;

out vec4 FragColor;

const float HALO_R_NIGHT = 2.35;
const float HALO_R_DAY = 3.20;
const float QUAD_FADE_IN = 0.56;
const float REACH_FADE_LO = 0.52;

const float NIGHT_ELEV = 0.63;
const float NIGHT_ELEV_SWING = 0.13;
const float SUN_BREATH_RATE = 0.0713;
const float SUN_SWING_RATE = 0.0419;

const float TERM_SOFT_CENTRE = 0.620;
const float TERM_SOFT_LIMB = 0.980;
const float TERM_FADE_LO = 0.42;
const float TERM_BAND_W = 0.300;
const float TERM_GLOW_GAIN = 0.055;
const float EARTHSHINE = 0.260;
const vec3 TERM_GLOW = vec3(0.620, 0.740, 1.000);

const float SUV_CURVE = 0.82;
const float SUV_FLOOR = 0.30;
const float WARP_SCALE = 1.55;
const float LOW_SCALE = 2.35;
const float HIGH_SCALE = 9.60;
const float WARP_DRIFT = 0.0117;
const float LOW_DRIFT = 0.0061;
const float HIGH_DRIFT = 0.0143;
const float WARP_LOW = 0.62;
const float WARP_HIGH = 0.34;

const float MARE_LO = 0.40;
const float MARE_HI = 0.76;
const float MARE_DEPTH = 0.055;
const float MARE_MEAN = 0.26;
const float GRIT_AMP = 0.085;
const float GRAN_AMP = 0.090;
const float NETWORK_AMP = 0.095;

const float CRATER_SCALE = 3.40;
const float CRATER_PROB = 0.31;
const float CRATER_JITTER = 0.78;
const float CRATER_MIN = 0.14;
const float CRATER_MAX = 0.52;
const float CRATER_CULL = 1.22;
const float CRATER_MARE_SHIELD = 0.78;
const float CRATER_SHADOW_KEEP = 0.12;
const float RIM_POS = 0.92;
const float RIM_W = 0.20;
const float WALL_POS = 0.52;
const float WALL_W = 0.42;
const float FLOOR_W = 0.78;
const float RIM_GAIN = 0.50;
const float WALL_GAIN = 0.44;
const float RIM_ALBEDO = 0.100;
const float FLOOR_DARK = 0.085;
const float CRATER_RELIEF_BASE = 0.30;
const float CRATER_RELIEF_GRAZE = 0.42;
const float TAN_LIGHT_FADE = 0.22;

const float LIMB_DARK_FLOOR = 0.68;
const float LIMB_DARK_EXP = 0.38;
const float LIMB_BRIGHT_PEAK = 1.06;
const float LIMB_RING_W = 0.210;
const float LIMB_RING_GAIN = 0.070;
const float LIMB_RING_ACCENT = 0.30;

const float SUN_EMIT = 1.14;
const float SUN_CORE_W = 0.62;
const float SUN_CORE_GAIN = 0.20;
const vec3 SUN_CORE_TINT = vec3(1.000, 0.970, 0.880);
const vec3 SUN_WHITE = vec3(1.000, 0.965, 0.905);

const float TIGHT_NIGHT = 0.070;
const float TIGHT_DAY = 0.150;
const float WIDE_NIGHT = 0.52;
const float WIDE_DAY = 1.18;
const float WIDE_K = 6.0;
const float TIGHT_GAIN = 0.46;
const float WIDE_GAIN = 0.30;
const float NIGHT_HALO = 0.82;
const float DAY_HALO = 1.30;
const float HALO_ASYM = 0.30;
const float HALO_ASYM_FLOOR = 0.82;
const float ACCENT_IN_HALO = 0.30;
const float HALO_KNEE = 0.90;

const float RAY_RATE_A = 0.0271;
const float RAY_RATE_B = 0.0163;
const float RAY_SCALE_A = 3.15;
const float RAY_SCALE_B = 7.10;
const float RAY_LO = 0.30;
const float RAY_HI = 0.86;
const float RAY_BREATHE = 0.1830;
const float RAY_DEPTH = 0.90;
const float RAY_ONSET = 0.34;
const float RAY_FLOOR = 0.40;
const float RAY_LIFT = 1.30;
const float RAY_NEUTRAL = (1.0 - RAY_FLOOR) / RAY_LIFT;

const float POINTER_REACH = 2.60;
const float POINTER_BODY_GAIN = 0.155;
const float POINTER_HALO_GAIN = 0.42;
const float POINTER_DIR_FADE = 0.35;

const vec3 LIT_COOL = vec3(0.905, 0.930, 0.985);
const vec3 LIT_MID = vec3(1.000, 0.930, 0.868);
const vec3 LIT_WARM = vec3(1.000, 0.885, 0.612);
const vec3 DARK_COOL = vec3(0.090, 0.125, 0.245);
const vec3 DARK_MID = vec3(0.250, 0.112, 0.232);
const vec3 DARK_WARM = vec3(0.330, 0.120, 0.045);
const vec3 HALO_COOL = vec3(0.620, 0.780, 1.000);
const vec3 HALO_MID = vec3(0.980, 0.700, 0.880);
const vec3 HALO_WARM = vec3(1.000, 0.580, 0.160);

const vec3 LIGHT_SHADE_LIFT = vec3(0.430, 0.462, 0.560);
const float LIGHT_SHADE_MIX = 0.62;
const float LIGHT_BODY_TRIM = 0.97;
const float LIGHT_TIGHT_TRIM = 0.72;
const float LIGHT_WIDE_TRIM = 0.26;
const float CONTACT_W = 0.34;
const float CONTACT_GAIN = 0.30;
const vec3 LIGHT_SHADOW_COL = vec3(0.140, 0.152, 0.212);

const float DETAIL_LOD_LO = 0.88;
const float DETAIL_LOD_HI = 2.40;
const float DETAIL_REF_PX = 19.0;
const float LOD_BOLD_LO = 0.34;
const float LOD_BOLD_HI = 0.90;
const float LOD_FINE_LO = 0.55;
const float LOD_FINE_HI = 1.60;
const float DITHER_MIN_A = 0.04;

float sq(float x) {
    return x * x;
}

float bump(float x) {
    float a = 1.0 - x * x;
    return a > 0.0 ? a * a : 0.0;
}

vec2 nrm2(vec2 v) {
    float l = length(v);
    return l > 1.0e-5 ? v / l : vec2(0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx + p3.yz) * p3.zy);
}

vec4 hash42(vec2 p) {
    vec4 p4 = fract(vec4(p.xyxy) * vec4(0.1031, 0.1030, 0.0973, 0.1099));
    p4 += dot(p4, p4.wzxy + 33.33);
    return fract((p4.xxyz + p4.yzzw) * p4.zywx);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

vec2 vnoise2(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    vec2 a = hash22(i);
    vec2 b = hash22(i + vec2(1.0, 0.0));
    vec2 c = hash22(i + vec2(0.0, 1.0));
    vec2 d = hash22(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0));
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

vec3 addLight(vec3 base, vec3 light) {
    return base + light * max(vec3(0.0), vec3(1.0) - base);
}

vec3 accentAt(float t) {
    return mix(uAccentTop, uAccentBottom, clamp(t, 0.0, 1.0));
}

vec3 accentNeon(float t, float peakTarget) {
    vec3 c = accentAt(t);
    float peak = max(max(c.r, c.g), c.b);
    return c * (peak > 1.0e-4 ? peakTarget / peak : 1.0);
}

vec3 paletteArc(vec3 a, vec3 b, vec3 c, float t) {
    float it = 1.0 - t;
    return it * it * a + 2.0 * it * t * b + t * t * c;
}

vec3 hotRoll(vec3 c, vec3 top) {
    float m = max(max(c.r, c.g), c.b);
    if (m <= 1.0) {
        return c;
    }
    return mix(c / m, top, clamp((1.0 - 1.0 / m) * 1.6, 0.0, 1.0));
}

float softSat(float x, float k) {
    float d = 1.0 - k;
    return min(x, 1.0 - d * d / (max(x - k, 0.0) + d));
}

float freqLod(float rate, float lo, float hi) {
    return 1.0 - smoothstep(lo, hi, rate);
}

void main() {
    float R = max(uBody.z, 1.0);
    vec2 p = vLocal - uBody.xy;
    vec2 q = vec2(p.x, -p.y) / R;
    float r = length(q);
    float aa = max(fwidth(r), 0.28 / R);

    float master = clamp(uEntry, 0.0, 1.0) * clamp(uAlpha, 0.0, 1.0);
    float phase = clamp(uPhase, 0.0, 1.0);
    float day = smoothstep(0.0, 1.0, phase);
    float night = 1.0 - day;
    float haloR = mix(HALO_R_NIGHT, HALO_R_DAY, day);

    float halfQuad = max(uBody.w, R * 1.04);
    float fadeStart = clamp(max(QUAD_FADE_IN, R * 1.08 / halfQuad), 0.30, 0.94);
    float quadFade = 1.0 - smoothstep(fadeStart, 1.0, length(p) / halfQuad);

    if (master <= 0.0015 || quadFade <= 0.0 || r > haloR) {
        discard;
    }

    float disc = 1.0 - smoothstep(1.0 - aa, 1.0 + aa, r);
    vec2 dir = r > 1.0e-5 ? q / r : vec2(0.0, 1.0);

    float az = uTime * SUN_SWING_RATE + uSeed * 4.712;
    vec2 lightUV = nrm2(vec2(cos(az), sin(az) * 0.55 + 0.42));

    vec3 haloBase = paletteArc(HALO_COOL, HALO_MID, HALO_WARM, day);

    float vert = clamp(0.5 - q.y * 0.5, 0.0, 1.0);
    vec3 accentHot = accentNeon(vert, 0.92);

    vec2 pq = vec2(uPointer.x - uBody.x, uBody.y - uPointer.y) / R;
    float pqLen = length(pq);
    vec2 pdir = nrm2(pq);
    float pdirW = smoothstep(0.0, POINTER_DIR_FADE, pqLen);
    float prox = 1.0 / (1.0 + sq(pqLen / POINTER_REACH));

    vec3 bodyCol = vec3(0.0);
    if (disc > 0.0) {
        float rc = min(r, 1.0);
        vec2 qc = dir * rc;
        float mu = sqrt(max(1.0 - rc * rc, 0.0));
        vec3 n = vec3(qc, mu);

        float breath = sin(uTime * SUN_BREATH_RATE + uSeed * 5.117);
        float lz = clamp(mix(NIGHT_ELEV + NIGHT_ELEV_SWING * breath, 1.0, day), 0.0, 1.0);
        float lxy = sqrt(max(1.0 - lz * lz, 0.0));
        vec3 Lsun = vec3(lightUV * lxy, lz);

        float ndl = dot(n, Lsun);
        float pen = mix(TERM_SOFT_CENTRE, TERM_SOFT_LIMB, 1.0 - mu);
        float litRaw = smoothstep(-pen, pen, ndl);
        float termMix = 1.0 - smoothstep(TERM_FADE_LO, 1.0, day);
        float lit = mix(1.0, EARTHSHINE + (1.0 - EARTHSHINE) * litRaw, termMix);

        float pxUnit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;
        float detailLod = smoothstep(DETAIL_LOD_LO, DETAIL_LOD_HI, R / (DETAIL_REF_PX * pxUnit));

        vec3 litCol = paletteArc(LIT_COOL, LIT_MID, LIT_WARM, day);
        vec3 darkCol = paletteArc(DARK_COOL, DARK_MID, DARK_WARM, day);
        vec3 Lp = normalize(vec3(pdir * (0.86 * pdirW), 0.51));

        float suvDen = mu * SUV_CURVE + SUV_FLOOR;
        vec2 suv = qc / suvDen;
        float suvPx = (suvDen + SUV_CURVE * rc * rc / max(mu, 0.035))
                / (suvDen * suvDen * R);
        float lodLow = freqLod(suvPx * LOW_SCALE, LOD_BOLD_LO, LOD_BOLD_HI);
        float lodHigh = freqLod(suvPx * HIGH_SCALE, LOD_FINE_LO, LOD_FINE_HI);
        float lodCrater = freqLod(suvPx * CRATER_SCALE, LOD_FINE_LO, LOD_FINE_HI);

        vec2 warp = vnoise2(suv * WARP_SCALE + vec2(uTime * WARP_DRIFT, uSeed * 9.31)) - 0.5;
        float nLow = vnoise(suv * LOW_SCALE + warp * WARP_LOW + vec2(uTime * LOW_DRIFT, uSeed * 3.77));
        float nHigh = vnoise(suv * HIGH_SCALE + warp * WARP_HIGH + vec2(-uTime * HIGH_DRIFT, uSeed * 6.13));

        float grit = (nHigh - 0.5) * 2.0 * lodHigh;
        float mare = mix(MARE_MEAN, smoothstep(MARE_LO, MARE_HI, nLow), lodLow);

        float craterShade = 0.0;
        float craterFloor = 0.0;
        float craterAmt = night * detailLod * lodCrater
                * (1.0 - mare * CRATER_MARE_SHIELD)
                * mix(CRATER_SHADOW_KEEP, 1.0, lit);
        if (craterAmt > 0.004) {
            float tanLen = sqrt(max(1.0 - ndl * ndl, 0.0));
            vec2 tanPerp = vec2(-dir.y, dir.x);
            vec2 tanL = nrm2(dir * dot(Lsun, vec3(dir * mu, -rc))
                            + tanPerp * dot(Lsun.xy, tanPerp))
                    * smoothstep(0.0, TAN_LIGHT_FADE, tanLen);
            vec2 cuv = suv * CRATER_SCALE + uSeed * 17.31;
            vec2 ci = floor(cuv);
            vec2 cf = fract(cuv);
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    vec2 g = vec2(float(x), float(y));
                    vec4 h = hash42(ci + g);
                    if (h.w > CRATER_PROB) {
                        continue;
                    }
                    float rad = mix(CRATER_MIN, CRATER_MAX, h.z);
                    vec2 d = cf - (g + 0.5 + (h.xy - 0.5) * CRATER_JITTER);
                    float dl = length(d);
                    if (dl > rad * CRATER_CULL) {
                        continue;
                    }
                    float t = dl / rad;
                    vec2 ud = dl > 1.0e-5 ? d / dl : vec2(0.0, 1.0);
                    float side = dot(ud, tanL);
                    float rim = bump(clamp((t - RIM_POS) / RIM_W, -1.0, 1.0));
                    float wall = bump(clamp((t - WALL_POS) / WALL_W, -1.0, 1.0));
                    float bowl = bump(clamp(t / FLOOR_W, -1.0, 1.0));
                    float big = mix(0.60, 1.0, h.z);
                    craterShade += ((rim * RIM_GAIN - wall * WALL_GAIN) * side
                            + rim * RIM_ALBEDO) * big;
                    craterFloor += bowl * FLOOR_DARK * big;
                }
            }
            float relief = CRATER_RELIEF_BASE + CRATER_RELIEF_GRAZE * (1.0 - clamp(ndl, 0.0, 1.0));
            craterShade = clamp(craterShade, -0.55, 0.55) * relief * craterAmt;
            craterFloor = clamp(craterFloor, 0.0, 0.55) * craterAmt;
        }

        float moonAlbedo = 1.0 - mare * MARE_DEPTH + grit * GRIT_AMP * detailLod + craterShade - craterFloor;
        float network = 1.0 - abs(nLow * 2.0 - 1.0);
        float sunAlbedo = 1.0 + grit * GRAN_AMP * detailLod
                + (network - 0.5) * NETWORK_AMP * lodLow;
        float albedo = clamp(mix(moonAlbedo, sunAlbedo, day), 0.22, 1.45);

        float limbMoon = LIMB_DARK_FLOOR + (1.0 - LIMB_DARK_FLOOR) * pow(clamp(mu, 0.0, 1.0), LIMB_DARK_EXP);
        float limbSun = mix(1.0, LIMB_BRIGHT_PEAK, sq(1.0 - mu));
        float limbProfile = mix(limbMoon, limbSun, day);

        bodyCol = mix(darkCol, litCol, lit);
        bodyCol *= mix(1.0, albedo, 0.28 + 0.72 * lit);
        bodyCol = hotRoll(bodyCol * limbProfile * mix(1.0, SUN_EMIT, day),
                mix(vec3(1.0), SUN_WHITE, day));

        bodyCol = addLight(bodyCol, SUN_CORE_TINT * exp(-sq(rc / SUN_CORE_W)) * SUN_CORE_GAIN * day);

        float termBand = exp(-sq((litRaw - 0.5) / TERM_BAND_W)) * termMix;
        bodyCol = addLight(bodyCol, TERM_GLOW * termBand * TERM_GLOW_GAIN * night);

        float limbRing = exp(-sq((1.0 - rc) / LIMB_RING_W));
        vec3 ringCol = mix(litCol, accentHot, LIMB_RING_ACCENT);
        bodyCol = addLight(bodyCol, ringCol * limbRing * LIMB_RING_GAIN
                * mix(0.42, 1.0, lit) * mix(0.75, 0.30, day));

        float cursorWrap = sq(clamp(dot(n, Lp) * 0.5 + 0.5, 0.0, 1.0));
        vec3 cursorTint = mix(mix(vec3(0.82, 0.88, 1.00), accentHot, 0.45), litCol, day * 0.5);
        bodyCol = addLight(bodyCol, cursorTint * cursorWrap * POINTER_BODY_GAIN * prox);

        if (uLightMode > 0.5) {
            bodyCol = mix(bodyCol, LIGHT_SHADE_LIFT * mix(1.0, 1.35, day),
                    (1.0 - lit) * LIGHT_SHADE_MIX) * LIGHT_BODY_TRIM;
        }
    }

    float e = max(r - 1.0, 0.0);
    float reachFade = 1.0 - smoothstep(haloR * REACH_FADE_LO, haloR, r);
    float tight = exp(-e / mix(TIGHT_NIGHT, TIGHT_DAY, day));
    float wide = 1.0 / (1.0 + WIDE_K * sq(e / mix(WIDE_NIGHT, WIDE_DAY, day)));

    float rays = RAY_NEUTRAL;
    if (day > 0.02 && disc < 0.995 && reachFade > 0.002) {
        float ra = uTime * RAY_RATE_A + uSeed * 1.73;
        float rb = uSeed * 4.31 - uTime * RAY_RATE_B;
        float ca = cos(ra);
        float sa = sin(ra);
        float cb = cos(rb);
        float sb = sin(rb);
        vec2 dA = vec2(dir.x * ca - dir.y * sa, dir.x * sa + dir.y * ca);
        vec2 dB = vec2(dir.x * cb - dir.y * sb, dir.x * sb + dir.y * cb);
        float rA = vnoise(dA * RAY_SCALE_A + 11.73);
        float rB = vnoise(dB * RAY_SCALE_B + 27.31);
        rays = smoothstep(RAY_LO, RAY_HI, rA * 0.64 + rB * 0.36);
        rays *= 0.76 + 0.24 * sin(uTime * RAY_BREATHE + uSeed * 2.91);
    }
    float rayShape = mix(1.0, RAY_FLOOR + RAY_LIFT * rays,
            day * RAY_DEPTH * smoothstep(0.0, RAY_ONSET, e));

    float sunFacing = dot(dir, lightUV) * 0.5 + 0.5;
    float asym = mix(1.0, HALO_ASYM_FLOOR + (1.0 - HALO_ASYM_FLOOR) * sunFacing, night * HALO_ASYM);
    float haloBoost = 1.0 + POINTER_HALO_GAIN * prox
            * mix(0.5, clamp(dot(dir, pdir), 0.0, 1.0), pdirW);

    float tightPart = tight * TIGHT_GAIN;
    float widePart = wide * rayShape * WIDE_GAIN;
    vec3 haloCol = mix(haloBase, accentHot, ACCENT_IN_HALO);

    float shadowA = 0.0;
    if (uLightMode > 0.5) {
        tightPart *= LIGHT_TIGHT_TRIM;
        widePart *= LIGHT_WIDE_TRIM;
        haloCol = mix(haloCol, haloBase, 0.45);
        shadowA = exp(-sq(e / CONTACT_W)) * CONTACT_GAIN * reachFade;
    }
    float haloEnergy = (tightPart + widePart)
            * asym * reachFade * haloBoost * mix(NIGHT_HALO, DAY_HALO, day);
    float haloAlpha = softSat(max(haloEnergy, 0.0), HALO_KNEE);

    vec3 col = LIGHT_SHADOW_COL * shadowA;
    float a = shadowA;
    col = haloCol * haloAlpha + col * (1.0 - haloAlpha);
    a = haloAlpha + a * (1.0 - haloAlpha);
    col = bodyCol * disc + col * (1.0 - disc);
    a = disc + a * (1.0 - disc);

    col *= quadFade;
    a *= quadFade * master;
    if (a <= 0.0025) {
        discard;
    }
    vec3 straight = col * master / max(a, 1.0e-4);
    straight += triangularNoise(vScreen, fract(uTime * 0.41))
            * (1.0 / 255.0) / max(a, DITHER_MIN_A);
    FragColor = vec4(straight, clamp(a, 0.0, 1.0));
}
