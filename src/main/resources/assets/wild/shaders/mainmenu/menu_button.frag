#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uBackground;
uniform sampler2D uSharp;
uniform vec2 uViewport;
uniform vec2 uTextureSize;
uniform vec2 uSharpSize;
uniform vec2 uSourceScale;
uniform vec2 uSharpScale;
uniform vec4 uButton;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform vec2 uLocalMouse;
uniform vec2 uPointerLocal;
uniform float uPointerValid;
uniform float uBackdrop;
uniform float uRadius;
uniform float uTime;
uniform float uHover;
uniform float uMagnet;
uniform float uPress;
uniform float uEntry;
uniform float uFlash;
uniform float uWave;
uniform float uScale;
uniform float uSeed;
uniform float uLightMode;

out vec4 FragColor;

const float BODY_ALPHA = 0.70;
const float BODY_ALPHA_HOVER = 0.10;
const vec3 BODY_VEIL = vec3(0.0392, 0.0372, 0.0640);
const float BODY_TRANSMIT = 0.30;
const float VEIL_ACCENT = 0.085;
const float VEIL_TOP_LIFT = 1.34;
const float VEIL_BOTTOM_SINK = 0.74;

const float FACET_CELL_RATIO = 1.360;
const float FACET_AMP = 0.1620;
const float FACET2_CELL_RATIO = 0.628;
const float FACET2_AMP = 0.0620;
const float FACET3_CELL_RATIO = 0.272;
const float FACET3_AMP = 0.0165;
const float FACET_ROT2 = 2.39996;
const float FACET_ROT3 = 1.10715;
const float FACET_AA = 0.55;
const float FACET_MIN_CELL_PX = 5.0;

const float GLINT_WIDTH_PX = 0.90;
const float GLINT_WIDE_PX = 3.60;
const float GLINT_VERTEX_TAPER = 0.72;
const float GLINT_SPARSITY = 0.36;
const float GLINT_TWINKLE = 0.34;
const float GLINT_TWINKLE_RATE = 0.83;
const float GLINT_TOL = 0.098;
const float GLINT_ALONG = 0.270;
const float GLINT_SPAN = 1.60;
const float GLINT_GAIN = 1.00;
const float GLINT_REACH = 2.60;
const float GLINT_REACH_MIN_PX = 132.0;
const float GLINT_WHITE = 0.30;
const float GLINT_CEIL = 0.85;
const float GLINT_LIGHT_MODE = 0.62;
const float GLINT_LIGHT_INK = 0.86;

const float DOME_X = 0.052;
const float DOME_Y = 0.116;

const float EDGE_ROLL_PX = 2.00;
const float EDGE_ROLL_SLOPE = 0.52;

const float REFRACT_PX = 6.4;
const float REFRACT_MACRO = 0.62;

const float TILT_NORM = 0.235;
const float CHIP_FLAT = 1.00;
const float CHIP_STEEP = 0.64;
const float SHINE_FLAT = 0.82;
const float SHINE_STEEP = 1.44;

const float FIRE_HUE_SPREAD = 1.12;
const float FIRE_DISPERSION = 0.88;
const float FIRE_CORE_WHITE = 0.10;

const float LIGHT_HEIGHT_PX = 172.0;
const float LIGHT_REACH_PX = 760.0;
const float SPEC_SHINE_SOFT = 30.0;
const float SPEC_SHINE_GEM = 64.0;
const float SPEC_TIGHT_NORM = 1.72;
const float SPEC_GAIN = 0.430;
const float SPEC_REST = 0.165;
const float KEY_SHINE = 30.0;
const float KEY_GAIN = 0.172;
const float KEY_TILT = 0.430;
const float KEY_AZ = 2.2340;
const float KEY_REST = 0.62;
const float FRESNEL_GAIN = 0.185;
const float SHEEN_GAIN = 0.088;
const float SHEEN_DIR_X = 0.9063;
const float SHEEN_DIR_Y = -0.4226;

const float IDLE_RATE = 0.2150;
const float IDLE_SWING = 0.7400;
const float IDLE_BAND = 0.7200;
const float IDLE_GAIN = 0.1350;

const float WAVE_WIDTH_RATIO = 1.48;
const float WAVE_EASE = 1.36;
const float WAVE_OVERSHOOT = 1.30;
const float WAVE_SPEC = 1.05;
const float WAVE_FIRE = 0.230;

const float SSS_ENTRY_RATIO = 0.115;
const float SSS_DEPTH_PX = 18.0;
const float SSS_GAIN = 0.300;
const float SSS_REST = 0.070;

const float RIM_WIDTH_PX = 1.10;
const float RIM_FLOOR = 0.325;
const float RIM_LIT = 0.640;
const float RIM_FLASH = 0.330;
const float RIM_SHADE = 0.240;

const float OUTER_SIGMA_PX = 9.0;
const float OUTER_GAIN = 0.300;
const float FLASH_SPEC = 0.72;
const float FLASH_FACET = 0.235;

const float LIGHT_BODY_WHITE = 0.68;
const float LIGHT_SPEC_GAIN = 1.02;
const float LIGHT_ABSORB_FRESNEL = 1.80;
const float LIGHT_ABSORB_KEY = 0.40;
const float LIGHT_ABSORB_SSS = 0.26;
const float LIGHT_ABSORB_FACET = 0.115;
const float LIGHT_ABSORB_CEIL = 0.58;

float sq(float x) {
    return x * x;
}

float pow5(float x) {
    float a = x * x;
    return a * a * x;
}

vec3 hueRotate(vec3 c, float a) {
    const vec3 k = vec3(0.5773503, 0.5773503, 0.5773503);
    float cs = cos(a);
    float sn = sin(a);
    return max(c * cs + cross(k, c) * sn + k * dot(k, c) * (1.0 - cs), vec3(0.0));
}

vec2 hash22(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx + p3.yz) * p3.zy);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0));
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

vec3 roundBoxField(vec2 p, vec2 b, float r) {
    r = min(r, min(b.x, b.y));
    vec2 s = vec2(p.x < 0.0 ? -1.0 : 1.0, p.y < 0.0 ? -1.0 : 1.0);
    vec2 q = abs(p) - b + r;
    if (max(q.x, q.y) > 0.0) {
        vec2 m = max(q, vec2(0.0));
        float l = length(m);
        vec2 g = l > 1.0e-5 ? m / l : vec2(0.0, 1.0);
        return vec3(s * g, l - r);
    }
    vec2 g = q.x > q.y ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    return vec3(s * g, max(q.x, q.y) - r);
}

vec2 cellTilt(vec3 c, float seed, float amp) {
    vec2 h = hash22(c.xy * 1.617 + vec2(c.z * 7.331, c.z * 3.797) + seed);
    float a = h.x * 6.2831853;
    return vec2(cos(a), sin(a)) * (amp * (0.34 + 0.66 * h.y));
}

vec2 tessSlope(vec2 q, float cellPx, float amp, float seed) {
    vec3 u = vec3(dot(q, vec2(1.0, 0.0)),
                  dot(q, vec2(0.5, 0.8660254)) * 0.937,
                  dot(q, vec2(-0.5, 0.8660254)) * 1.081) / max(cellPx, 1.0e-4) + seed;
    vec3 c = floor(u);
    vec3 g = u - c - 0.5;
    vec3 sg = vec3(g.x < 0.0 ? -1.0 : 1.0, g.y < 0.0 ? -1.0 : 1.0, g.z < 0.0 ? -1.0 : 1.0);
    vec3 aa = clamp(fwidth(u) * FACET_AA, vec3(0.002), vec3(0.44));
    vec3 t = 0.5 * smoothstep(vec3(0.5) - aa, vec3(0.5), abs(g));
    vec2 acc = vec2(0.0);
    for (int i = 0; i < 8; i++) {
        vec3 pick = vec3(float(i & 1), float((i >> 1) & 1), float((i >> 2) & 1));
        vec3 w = mix(vec3(1.0) - t, t, pick);
        acc += cellTilt(c + pick * sg, seed, amp) * (w.x * w.y * w.z);
    }
    return acc;
}

vec2 tessSlopeCrease(vec2 q, float cellPx, float amp, float seed,
                     out vec2 jump0, out vec2 jump1, out vec2 jump2,
                     out vec3 ridge, out vec3 spark) {
    vec3 u = vec3(dot(q, vec2(1.0, 0.0)),
                  dot(q, vec2(0.5, 0.8660254)) * 0.937,
                  dot(q, vec2(-0.5, 0.8660254)) * 1.081) / max(cellPx, 1.0e-4) + seed;
    vec3 c = floor(u);
    vec3 g = u - c - 0.5;
    vec3 sg = vec3(g.x < 0.0 ? -1.0 : 1.0, g.y < 0.0 ? -1.0 : 1.0, g.z < 0.0 ? -1.0 : 1.0);
    vec3 du = max(fwidth(u), vec3(1.0e-5));
    vec3 aa = clamp(du * FACET_AA, vec3(0.002), vec3(0.44));
    vec3 t = 0.5 * smoothstep(vec3(0.5) - aa, vec3(0.5), abs(g));
    vec2 acc = vec2(0.0);
    vec2 j0 = vec2(0.0);
    vec2 j1 = vec2(0.0);
    vec2 j2 = vec2(0.0);
    for (int i = 0; i < 8; i++) {
        vec3 pick = vec3(float(i & 1), float((i >> 1) & 1), float((i >> 2) & 1));
        vec3 w = mix(vec3(1.0) - t, t, pick);
        vec2 tilt = cellTilt(c + pick * sg, seed, amp);
        float weight = w.x * w.y * w.z;
        vec3 side = pick * 2.0 - 1.0;
        acc += tilt * weight;
        j0 += tilt * (weight * side.x);
        j1 += tilt * (weight * side.y);
        j2 += tilt * (weight * side.z);
    }
    jump0 = j0;
    jump1 = j1;
    jump2 = j2;

    vec3 edgePx = (vec3(0.5) - abs(g)) / du;
    vec3 sqPx = edgePx * edgePx;
    vec3 narrow = exp(-sqPx / sq(GLINT_WIDTH_PX));
    vec3 wide = exp(-sqPx / sq(GLINT_WIDE_PX));
    vec3 corner = vec3(max(wide.y, wide.z), max(wide.x, wide.z), max(wide.x, wide.y));
    ridge = narrow * max(vec3(1.0) - GLINT_VERTEX_TAPER * corner, vec3(0.0));

    vec3 bIdx = floor(u + 0.5);
    vec2 h0 = hash22(vec2(bIdx.x * 1.731 + c.y * 7.113, c.z * 3.917 + seed));
    vec2 h1 = hash22(vec2(c.x * 5.219 + bIdx.y * 1.977, c.z * 2.663 + seed * 1.31));
    vec2 h2 = hash22(vec2(c.x * 3.371 + c.y * 6.229, bIdx.z * 2.111 + seed * 0.79));
    vec3 phase = vec3(h0.y, h1.y, h2.y) * 6.2831853;
    spark = smoothstep(vec3(GLINT_SPARSITY), vec3(1.0), vec3(h0.x, h1.x, h2.x));
    spark *= vec3(1.0) - GLINT_TWINKLE * (vec3(0.5) - 0.5 * cos(uTime * GLINT_TWINKLE_RATE + phase));
    return acc;
}

float creaseSpark(vec2 mirror, vec2 base, vec2 j, float ridge, float spark) {
    float weight = ridge * spark;
    float jl = length(j);
    if (weight <= 0.0015 || jl <= 1.0e-5) {
        return 0.0;
    }
    vec2 axis = j / jl;
    vec2 d = mirror - base;
    float along = dot(d, axis);
    float perp = d.x * axis.y - d.y * axis.x;
    float over = max(abs(along) - jl * GLINT_SPAN, 0.0);
    return weight * exp(-(sq(perp / GLINT_TOL) + sq(over / GLINT_ALONG)));
}

vec2 rot2(vec2 v, float a) {
    float cs = cos(a);
    float sn = sin(a);
    return vec2(cs * v.x - sn * v.y, sn * v.x + cs * v.y);
}

vec3 fireRamp(float x, vec3 c0, vec3 c1, vec3 c2, vec3 c3) {
    float t = abs(fract(x * 0.5) * 2.0 - 1.0);
    float s = t * 3.0;
    vec3 c = s < 1.0 ? mix(c0, c1, s)
           : (s < 2.0 ? mix(c1, c2, s - 1.0) : mix(c2, c3, s - 2.0));
    float peak = max(max(c.r, c.g), c.b);
    return c * (peak > 1.0e-4 ? 0.96 / peak : 1.0);
}

vec2 domeSlope(vec2 p, vec2 halfSize) {
    vec2 e = clamp(p / max(halfSize, vec2(1.0)), vec2(-1.0), vec2(1.0));
    return vec2(e.x * abs(e.x) * DOME_X, e.y * abs(e.y) * DOME_Y);
}

vec2 region() {
    return uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
}

vec3 blurred(vec2 uv) {
    vec2 texel = 1.0 / max(uTextureSize, vec2(1.0));
    vec2 r = region();
    return texture(uBackground, clamp(uv * r, texel * 0.5, r - texel * 0.5)).rgb;
}

vec3 crisp(vec2 uv) {
    vec2 texel = 1.0 / max(uSharpSize, vec2(1.0));
    vec2 r = uSharpScale.x > 0.0 && uSharpScale.y > 0.0 ? uSharpScale : region();
    return texture(uSharp, clamp(uv * r, texel * 0.5, r - texel * 0.5)).rgb;
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

void main() {
    vec2 size = max(uButton.zw, vec2(1.0));
    vec2 half_ = size * 0.5;
    vec2 local = vLocal - uButton.xy;
    float scale = max(uScale, 0.001);
    vec2 p = (local - half_) / scale;

    float unit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;
    float radius = min(uRadius, min(half_.x, half_.y));

    vec3 field = roundBoxField(p, half_, radius);
    vec2 nOut = field.xy;
    float minHalf = min(half_.x, half_.y);
    float anchorDepth = max(-field.z, 0.0);
    float anchorFade = 1.0 - smoothstep(0.0, max(radius, 1.0), anchorDepth);
    vec2 edgePoint = p - clamp(field.z, -minHalf, minHalf) * nOut * anchorFade;
    float perimeter = 2.0 * (size.x + size.y);
    float dS = field.z * scale;
    float aa = max(length(vec2(dFdx(dS), dFdy(dS))) * 0.70, 0.55);
    float coverage = 1.0 - smoothstep(-aa, aa, dS);
    float dIn = max(-dS, 0.0);
    float outside = max(dS, 0.0);
    float ad = abs(dS);

    float entryRaw = clamp(uEntry, 0.0, 1.0);
    float entry = entryRaw * entryRaw * entryRaw * (entryRaw * (entryRaw * 6.0 - 15.0) + 10.0);
    if (entry <= 0.0) {
        discard;
    }

    float hover = clamp(uHover, 0.0, 1.0);
    float magnet = clamp(uMagnet, 0.0, 1.0);
    float press = clamp(uPress, 0.0, 1.0);
    float flash = clamp(uFlash, 0.0, 1.0);
    float live = max(hover, magnet * 0.55);

    float vertical = clamp((p.y + half_.y) / max(size.y, 1.0), 0.0, 1.0);
    vec3 accent = accentAt(vertical);
    vec3 accentHot = accentNeon(vertical, 0.94);

    vec2 lightPos = uPointerValid > 0.5 ? uPointerLocal : (uLocalMouse - 0.5) * size;
    vec2 crestDelta = edgePoint - lightPos;
    float crestSigma = max(perimeter * SSS_ENTRY_RATIO, 42.0);
    float crest = exp(-dot(crestDelta, crestDelta) / sq(crestSigma));

    float bleedLimit = max(min(uButton.x, uButton.y), 1.0);
    float quadFade = 1.0 - smoothstep(bleedLimit * 0.42, bleedLimit * 0.94, outside);
    float outerBand = exp(-sq(outside / max(OUTER_SIGMA_PX * unit, 2.6))) * quadFade;
    float outerDrive = hover * OUTER_GAIN * (0.24 + 0.76 * crest) + flash * 0.24;
    if (uLightMode > 0.5) {
        outerDrive *= 0.45;
    }
    vec3 outerColor = mix(vec3(0.80, 0.85, 1.0), accentHot, 0.78);

    if (coverage <= 0.0) {
        float loneAlpha = outerBand * outerDrive * entry;
        if (loneAlpha <= 0.003) {
            discard;
        }
        vec3 lone = outerColor + triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
        FragColor = vec4(lone, min(loneAlpha, 1.0));
        return;
    }

    vec2 toLight2 = lightPos - p;
    float lightDist2 = dot(toLight2, toLight2);
    float lightDist = sqrt(lightDist2);
    float atten = 1.0 / (1.0 + lightDist2 / sq(LIGHT_REACH_PX));
    vec3 L = normalize(vec3(toLight2, LIGHT_HEIGHT_PX * (1.0 - press * 0.22)));
    vec3 V = vec3(0.0, 0.0, 1.0);
    vec3 H = normalize(L + V);

    float facetAngle = uSeed * 0.73 + 0.41;
    vec2 fq = rot2(p, facetAngle);
    float facetSeed = uSeed * 13.71 + 4.13;
    float cellBase = max(size.y, 8.0);

    float cell1 = max(cellBase * FACET_CELL_RATIO, FACET_MIN_CELL_PX * unit);
    float cell2 = max(cellBase * FACET2_CELL_RATIO, FACET_MIN_CELL_PX * unit);
    float cell3 = max(cellBase * FACET3_CELL_RATIO, FACET_MIN_CELL_PX * unit);

    vec2 creaseJump0;
    vec2 creaseJump1;
    vec2 creaseJump2;
    vec3 creaseRidge;
    vec3 creaseSpark3;
    vec2 slopeGem = tessSlopeCrease(fq, cell1, FACET_AMP, facetSeed,
            creaseJump0, creaseJump1, creaseJump2, creaseRidge, creaseSpark3);
    slopeGem += tessSlope(rot2(fq, FACET_ROT2), cell2, FACET2_AMP, facetSeed * 2.317 + 27.9);
    slopeGem += tessSlope(rot2(fq, FACET_ROT3), cell3, FACET3_AMP, facetSeed * 5.113 + 61.7);
    float creaseSquash = 1.0 - press * 0.26;
    slopeGem *= creaseSquash;
    creaseJump0 *= creaseSquash;
    creaseJump1 *= creaseSquash;
    creaseJump2 *= creaseSquash;

    vec2 slopeDome = domeSlope(p, half_);
    float edgeRoll = exp(-dIn / max(EDGE_ROLL_PX * unit, 1.0));
    vec2 slopeEdge = nOut * (EDGE_ROLL_SLOPE * edgeRoll);

    vec2 slope = slopeGem + slopeDome + slopeEdge;
    vec3 N = normalize(vec3(slope, 1.0));
    vec3 Nmacro = normalize(vec3(slopeDome + slopeEdge + slopeGem * REFRACT_MACRO, 1.0));

    float tiltN = clamp(length(slopeGem) / TILT_NORM, 0.0, 1.0);
    float chipGain = mix(CHIP_FLAT, CHIP_STEEP, tiltN * tiltN);
    float shineMul = mix(SHINE_FLAT, SHINE_STEEP, tiltN);

    float wave01 = clamp(uWave, 0.0, 1.0);
    float waveEase = wave01 * (WAVE_EASE - (WAVE_EASE - 1.0) * wave01);
    float waveWidth = max(minHalf * WAVE_WIDTH_RATIO, 22.0 * unit);
    float waveReach = length(abs(lightPos) + half_) + waveWidth * WAVE_OVERSHOOT;
    float waveFront = waveEase * waveReach;
    float waveTail = 1.0 - wave01 * wave01 * wave01;
    float wave = exp(-sq((lightDist - waveFront) / waveWidth)) * waveTail * hover;

    float facetReveal = clamp(0.34 + 0.66 * crest * (0.34 + 0.66 * live), 0.0, 1.0);
    float shine = mix(SPEC_SHINE_SOFT, SPEC_SHINE_GEM, facetReveal) * shineMul;
    float specNorm = mix(1.0, SPEC_TIGHT_NORM, facetReveal);
    float ndh = clamp(dot(N, H), 0.0, 1.0);
    float spec = pow(ndh, shine) * specNorm * chipGain;
    float fresnel = pow5(1.0 - clamp(N.z, 0.0, 1.0));

    float keyAz = KEY_AZ + IDLE_SWING * sin(uTime * IDLE_RATE + uSeed * 2.399);
    vec3 Lkey = normalize(vec3(cos(keyAz) * KEY_TILT, sin(keyAz) * KEY_TILT * 0.46 - 0.26, 0.90));
    vec3 Hkey = normalize(Lkey + V);
    float keySpec = pow(max(dot(N, Hkey), 0.0), KEY_SHINE * shineMul) * chipGain;

    float idleSweep = sin(uTime * IDLE_RATE * 0.6180 + uSeed * 1.713);
    float idleBand = exp(-sq((p.x / max(half_.x, 1.0) - idleSweep) / IDLE_BAND));
    float idleDrift = IDLE_GAIN * idleBand * (0.28 + 0.72 * tiltN) * (1.0 - 0.55 * live);

    vec2 mirror = H.xy / max(H.z, 0.05);
    float glintReach = max(minHalf * GLINT_REACH, GLINT_REACH_MIN_PX * unit);
    float glintPool = exp(-lightDist2 / sq(glintReach));
    float glintDrive = glintPool * (0.22 + 0.78 * live) + wave * 0.55 + flash * 0.30;
    float creaseSum = creaseSpark(mirror, slope, creaseJump0, creaseRidge.x, creaseSpark3.x)
            + creaseSpark(mirror, slope, creaseJump1, creaseRidge.y, creaseSpark3.y)
            + creaseSpark(mirror, slope, creaseJump2, creaseRidge.z, creaseSpark3.z);
    float glint = min(creaseSum * glintDrive * GLINT_GAIN, GLINT_CEIL);

    vec2 formE = clamp(p / max(half_, vec2(1.0)), vec2(-1.0), vec2(1.0));
    float formT = dot(formE, vec2(SHEEN_DIR_X, SHEEN_DIR_Y)) * 0.5 + 0.5;
    float formSheen = SHEEN_GAIN * (0.30 + 0.70 * smoothstep(0.05, 0.95, formT));

    vec3 fireCool = hueRotate(uAccentTop, -FIRE_HUE_SPREAD);
    vec3 fireWarm = hueRotate(uAccentBottom, FIRE_HUE_SPREAD);
    float disp = FIRE_DISPERSION * (1.0 - ndh);
    float fireX = dot(slopeGem, vec2(3.10, 2.20)) + length(slopeGem) * 1.80 + disp;
    vec3 fireCol = fireRamp(fireX, fireCool, uAccentTop, uAccentBottom, fireWarm);

    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    vec2 refract2 = vec2(Nmacro.x, -Nmacro.y) * REFRACT_PX * unit / max(uViewport, vec2(1.0));
    vec3 frost = blurred(uv - refract2);
    vec3 backdrop = uBackdrop > 0.5 ? mix(frost, crisp(uv - refract2 * 0.42), 0.12) : frost;

    vec3 veilTint = mix(BODY_VEIL, accent, VEIL_ACCENT);
    float depthRamp = mix(VEIL_TOP_LIFT, VEIL_BOTTOM_SINK, vertical);
    vec3 body;
    float bodyAlpha;
    vec3 lightTint;
    float modeGain;
    if (uLightMode > 0.5) {
        body = mix(backdrop, vec3(0.982, 0.982, 0.993), LIGHT_BODY_WHITE);
        body = mix(body, accent, hover * 0.055);
        body *= mix(1.02, 0.965, vertical);
        bodyAlpha = 0.76 + hover * 0.06;
        lightTint = vec3(1.0);
        modeGain = LIGHT_SPEC_GAIN;
    } else {
        body = backdrop * BODY_TRANSMIT + veilTint * depthRamp;
        bodyAlpha = BODY_ALPHA + hover * BODY_ALPHA_HOVER;
        lightTint = vec3(1.000, 0.988, 0.972);
        modeGain = 1.0;
    }

    float specDrive = SPEC_REST + hover * 0.78 + magnet * 0.22 + flash * FLASH_SPEC + wave * WAVE_SPEC;
    vec3 specColor = mix(fireCol, mix(lightTint, accentHot, 0.62), 0.18);
    specColor = mix(specColor, vec3(1.0), FIRE_CORE_WHITE * clamp(spec, 0.0, 1.0) * clamp(spec, 0.0, 1.0));
    if (uLightMode > 0.5) {
        specColor = mix(specColor, lightTint, 0.34);
    }
    float specTerm = spec * SPEC_GAIN * specDrive * (0.06 + 0.94 * atten * atten) * modeGain;
    float keyTerm = keySpec * (KEY_GAIN * (KEY_REST + (1.0 - KEY_REST) * live) + idleDrift);
    float fresnelTerm = fresnel * FRESNEL_GAIN;
    float sssDepth = exp(-dIn / max(SSS_DEPTH_PX * unit, 4.0));
    float sssTerm = crest * (0.18 + 0.82 * sssDepth) * (SSS_REST + SSS_GAIN * live);
    float waveTerm = wave * WAVE_FIRE * (0.22 + 0.78 * tiltN);
    float flashTerm = flash * FLASH_FACET * (0.15 + 0.85 * tiltN);

    if (uLightMode > 0.5) {
        vec3 ink = mix(vec3(0.128, 0.132, 0.190), accent, 0.34);
        float absorb = clamp(fresnelTerm * LIGHT_ABSORB_FRESNEL
                + keyTerm * LIGHT_ABSORB_KEY
                + sssTerm * LIGHT_ABSORB_SSS
                + tiltN * tiltN * LIGHT_ABSORB_FACET, 0.0, LIGHT_ABSORB_CEIL);
        body = mix(body, ink, absorb);
        body = addLight(body, specColor * specTerm);
        body = addLight(body, mix(lightTint, accentHot, 0.40) * (waveTerm * 0.55 + flashTerm * 0.55));
    } else {
        body = addLight(body, specColor * specTerm);
        body = addLight(body, mix(fireCol, accentHot, 0.34) * keyTerm);
        body = addLight(body, mix(vec3(0.78, 0.83, 0.98), accentHot, 0.70) * fresnelTerm);
        body = addLight(body, accentHot * sssTerm);
        body = addLight(body, mix(fireCol, lightTint, 0.30) * waveTerm);
        body = addLight(body, mix(fireCol, accentHot, 0.30) * flashTerm);
    }

    vec3 glintColor = mix(mix(fireCol, accentHot, 0.42), vec3(1.0), GLINT_WHITE);
    if (uLightMode > 0.5) {
        vec3 glintInk = mix(accentAt(vertical), fireCol, 0.34) * GLINT_LIGHT_INK;
        body = mix(body, glintInk, clamp(glint * GLINT_LIGHT_MODE, 0.0, 1.0));
    } else {
        body = addLight(body, glintColor * glint);
    }

    body = addLight(body, mix(vec3(0.86, 0.90, 1.0), accentHot, 0.34) * formSheen);
    body *= 1.0 - press * 0.12;
    body = addLight(body, lightTint * (flash * 0.048));

    float rimWidth = max(RIM_WIDTH_PX * unit, 0.85);
    float rimBand = exp(-sq(ad / rimWidth));
    vec2 keyOffset = lightPos / max(half_, vec2(1.0));
    vec2 keyDir2 = keyOffset / max(length(keyOffset), 1.0);
    float facing = clamp(dot(nOut, keyDir2), -1.0, 1.0);
    float rimLight = RIM_FLOOR + RIM_LIT * live * crest + RIM_FLASH * flash + 0.30 * wave;
    vec3 rimColor = mix(vec3(0.76, 0.82, 0.98), accentHot, 0.64 + 0.24 * live);
    if (uLightMode > 0.5) {
        rimColor = mix(vec3(0.20, 0.21, 0.28), accent, 0.30);
    }

    vec3 surface = body;
    float alphaIn = coverage * bodyAlpha;
    float rimAlpha = coverage * clamp(rimBand * rimLight, 0.0, 1.0);
    surface = mix(surface, rimColor, clamp(rimAlpha / max(alphaIn + rimAlpha, 1.0e-4), 0.0, 1.0));
    surface *= 1.0 - rimBand * clamp(-facing, 0.0, 1.0) * RIM_SHADE * (0.35 + 0.65 * live);
    float aIn = clamp(alphaIn + rimAlpha * (1.0 - alphaIn), 0.0, 1.0);

    float outerAlpha = outerBand * outerDrive * (1.0 - coverage);
    vec3 color = surface * aIn + outerColor * outerAlpha * (1.0 - aIn);
    float alpha = aIn + outerAlpha * (1.0 - aIn);

    alpha *= entry;
    if (alpha <= 0.003) {
        discard;
    }
    vec3 straight = color * entry / max(alpha, 1.0e-4);
    straight += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    FragColor = vec4(straight, clamp(alpha, 0.0, 1.0));
}
