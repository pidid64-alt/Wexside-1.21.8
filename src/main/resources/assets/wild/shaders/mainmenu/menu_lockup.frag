#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uMask;
uniform sampler2D uShadow;
uniform sampler2D uBlur;
uniform sampler2D uSharp;
uniform vec2 uViewport;
uniform vec2 uMaskSize;
uniform vec2 uBlurSize;
uniform vec2 uSharpSize;
uniform vec2 uSourceScale;
uniform vec2 uSharpScale;
uniform float uMaskRange;
uniform vec2 uPointer;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uEntry;
uniform float uPointerActive;
uniform float uSignature;
uniform float uSignatureLead;
uniform vec4 uInkRect;
uniform float uLightMode;
uniform vec2 uLockupMetrics;

out vec4 FragColor;

const float TAU = 6.28318531;

const float AA_WIDTH = 0.52;
const float AA_FLOOR = 0.42;

const float BEVEL_CAP_RATIO = 0.068;
const float BEVEL_MIN_PX = 2.0;
const float BEVEL_PROBE = 2.0;
const float BEVEL_FLOOR_PX = 0.35;
const float WALL_SHARP = 1.28;
const float SLOPE_MAX = 1.62;

const float LENS_PX = 5.2;
const float CHROMA_PX = 1.5;

const vec3 CHROME_SKY = vec3(0.938, 0.948, 0.992);
const vec3 CHROME_HORIZON = vec3(0.604, 0.622, 0.706);
const vec3 CHROME_GROUND = vec3(0.168, 0.170, 0.226);
const float CHROME_ACCENT_SKY = 0.245;
const float CHROME_ACCENT_HORIZON = 0.300;
const float CHROME_ACCENT_GROUND = 0.440;
const float CHROME_BEND = 0.620;
const float CHROME_H0 = 0.455;
const float CHROME_H1 = 0.865;
const float CHROME_LOW_SPAN = 0.340;
const float CHROME_REF_SCALE = 0.700;
const float CHROME_REF_BIAS = 0.200;
const float CHROME_DEPTH = 0.155;
const float CHROME_FACET_BEND = 0.620;
const float ENV_GAIN = 0.600;
const float SHEEN_GAIN = 0.240;
const float ANISO_SQUASH = 0.360;
const float ANISO_BREATHE = 0.280;
const float ANISO_RATE = 0.2371;

const float GRAIN_COARSE_FREQ = 9.0;
const float GRAIN_COARSE_AMP = 0.052;
const float GRAIN_COARSE_STRETCH = 0.62;
const float GRAIN_FINE_FREQ = 23.0;
const float GRAIN_FINE_AMP = 0.020;
const float GRAIN_FINE_STRETCH = 0.74;
const float GRAIN_FINE_MIN_PX = 2.2;

const float FACET_CAP_RATIO = 0.160;
const float FACET_MIN_PX = 7.0;
const float FACET_JITTER = 0.58;
const float FACET_TILT = 0.104;
const float FACET_DRIFT = 0.085;
const float FACET_DRIFT_A = 0.1091;
const float FACET_DRIFT_B = 0.1367;
const float FACET_BLEND_PX = 0.5;
const float FACET_WEIGHT_KNEE = 0.55;
const float FACET_ANGLE_COS = 0.81780;
const float FACET_ANGLE_SIN = 0.57550;

const vec3 LIGHT_COLOR = vec3(1.000, 0.988, 0.968);
const vec3 LIGHT_BASE = vec3(-0.4600, -0.7800, 0.4300);
const float LIGHT_DRIFT_RATE = 0.1743;
const float LIGHT_DRIFT_SWING = 0.205;
const float LIGHT_POINTER_SWING = 0.300;

const float RIM_WIDTH_CAP = 0.027;
const float RIM_WIDTH_MIN = 1.05;
const float WALL_SHADE_GAIN = 0.220;

const float CHAMFER_TIGHT = 1.550;
const float CHAMFER_FLOOR = 0.100;
const float CHAMFER_LIT = 0.260;
const float CHAMFER_SKY = 0.200;
const float CHAMFER_ACCENT = 0.220;

const float SPEC_POWER = 38.0;
const float SPEC_GAIN = 0.360;
const float SPEC_SHOULDER = 0.600;
const float SWEEP_PERIOD = 8.7;
const float SWEEP_WIDTH = 0.16;
const float SWEEP_SKEW = 0.32;
const float SWEEP_PERIOD_2 = 13.3;
const float SWEEP_WIDTH_2 = 0.285;
const float SWEEP_SKEW_2 = -0.190;
const float SWEEP_SECOND = 0.620;
const float SWEEP_GAIN = 0.260;
const float FRESNEL_GAIN = 0.230;

const float CONTOUR_SIGMA_CAP = 1.05;
const float CONTOUR_AMBIENT = 0.10;
const float CONTOUR_WAVES = 3.0;
const float CONTOUR_SHIMMER_SPEED = 0.29;
const float CONTOUR_SHIMMER_AMOUNT = 0.0;
const float CONTOUR_RIM_GAIN = 0.240;
const float CONTOUR_HALO_GAIN = 0.16;
const float CONTOUR_SSS_GAIN = 0.06;

const float ENTRY_EASE = 2.6;
const float ENTRY_WIPE = 0.35;
const float ENTRY_FORM_FLOOR = 0.30;

const float SHADOW_GAIN = 0.520;
const float SHADOW_SHAPE = 2.050;
const float SHADOW_GAIN_LIGHT = 0.860;
const float SHADOW_SHAPE_LIGHT = 1.550;

const vec3 AURA_COOL = vec3(0.84, 0.89, 1.00);
const vec3 AURA_CORE = vec3(1.000, 0.982, 0.952);
const float CORONA_GAIN = 0.620;
const float CORONA_SHAPE = 1.300;
const float CORONA_RATE = 0.9317;
const float CORONA_BREATHE = 0.115;
const float BLOOM_GAIN = 0.300;
const float BLOOM_SHAPE = 2.550;
const float BLOOM_RATE = 0.5731;
const float BLOOM_BREATHE = 0.185;
const float AURA_CEIL = 0.720;
const float AURA_CORE_MIX = 0.550;
const float AURA_LIGHT_SCALE = 0.420;
const float NEON_PEAK = 0.88;

const float INK_LAMP_SIGMA = 0.22;
const float INK_GLOW_RATIO = 0.46;
const float INK_GLOW_GAIN = 0.58;
const float INK_RULE_GAP_PX = 4.6;
const float INK_RULE_HALF_PX = 0.75;
const float INK_RULE_SOFT_PX = 3.2;
const float INK_RULE_GAIN = 0.82;
const float INK_RULE_SOFT_GAIN = 0.28;

float sq(float x) {
    return x * x;
}

float pow5(float x) {
    float x2 = x * x;
    return x2 * x2 * x;
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

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0) + hash12(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

vec3 accentAt(vec3 top, vec3 bottom, float t) {
    return mix(top, bottom, clamp(t, 0.0, 1.0));
}

vec3 themeNeon(float t, vec3 top, vec3 bottom) {
    vec3 c = mix(top, bottom, clamp(t, 0.0, 1.0));
    float peak = max(max(c.r, c.g), c.b);
    return c * (peak > 1.0e-4 ? max(1.0, NEON_PEAK / peak) : 1.0);
}

vec2 maskUv(vec2 mv) {
    return clamp(vec2(mv.x, 1.0 - mv.y), vec2(0.0), vec2(1.0));
}

float glassAt(vec2 mv) {
    return (texture(uMask, maskUv(mv)).r - 0.5) * 2.0 * uMaskRange;
}

float inkAt(vec2 mv) {
    return (texture(uMask, maskUv(mv)).g - 0.5) * 2.0 * uMaskRange;
}

vec2 softAt(vec2 mv) {
    return texture(uShadow, maskUv(mv)).rg;
}

vec2 region() {
    return uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
}

vec3 frosted(vec2 uv) {
    vec2 texel = 1.0 / max(uBlurSize, vec2(1.0));
    vec2 r = region();
    return texture(uBlur, clamp(uv * r, texel * 0.5, r - texel * 0.5)).rgb;
}

vec3 crisp(vec2 uv) {
    vec2 texel = 1.0 / max(uSharpSize, vec2(1.0));
    vec2 r = uSharpScale.x > 0.0 && uSharpScale.y > 0.0 ? uSharpScale : region();
    return texture(uSharp, clamp(uv * r, texel * 0.5, r - texel * 0.5)).rgb;
}

vec3 addLight(vec3 base, vec3 light) {
    return base + light * max(vec3(0.0), vec3(1.0) - base);
}

float rangeFade(float distance) {
    float t = 1.0 - smoothstep(0.58, 0.97, distance / max(uMaskRange, 1.0));
    return t * t;
}

void voronoiFacet(vec2 p, out vec3 dist, out vec2 id1, out vec2 id2, out vec2 id3) {
    vec2 cell = floor(p);
    vec2 fr = p - cell;
    dist = vec3(64.0, 96.0, 128.0);
    id1 = cell;
    id2 = cell + vec2(1.0, 0.0);
    id3 = cell + vec2(0.0, 1.0);
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 g = vec2(float(x), float(y));
            vec2 o = hash22(cell + g);
            vec2 d = fr - (g + 0.5 + FACET_JITTER * (o - 0.5));
            float dist2 = dot(d, d);
            if (dist2 < dist.x) {
                dist.z = dist.y;
                id3 = id2;
                dist.y = dist.x;
                id2 = id1;
                dist.x = dist2;
                id1 = cell + g;
            } else if (dist2 < dist.y) {
                dist.z = dist.y;
                id3 = id2;
                dist.y = dist2;
                id2 = cell + g;
            } else if (dist2 < dist.z) {
                dist.z = dist2;
                id3 = cell + g;
            }
        }
    }
    dist = sqrt(dist);
}

vec2 facetSlope(vec2 id) {
    vec2 h = hash22(id * 0.4173 + 7.31);
    vec2 s = h * 2.0 - 1.0;
    s += FACET_DRIFT * vec2(sin(uTime * FACET_DRIFT_A + id.x * 1.93 + id.y * 0.71),
                            cos(uTime * FACET_DRIFT_B + id.y * 2.57 - id.x * 1.13));
    return s;
}

void main() {
    vec2 muv = clamp(vUv, 0.0, 1.0);
    vec2 texel = 1.0 / max(uMaskSize, vec2(1.0));
    float unit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;
    float presence = clamp(uPointerActive, 0.0, 1.0);
    float reveal = clamp(uSignature, 0.0, 1.0);
    float maskRange = max(uMaskRange, 1.0);
    float cap = max(uLockupMetrics.x, 1.0);
    vec2 pixelPos = muv * uMaskSize;

    float entryT = clamp(uEntry, 0.0, 1.0);
    float entrySpan = clamp(pixelPos.x / max(uMaskSize.x, 1.0), 0.0, 1.0);
    float entryLocal = clamp(entryT * (1.0 + ENTRY_WIPE) - ENTRY_WIPE * entrySpan, 0.0, 1.0);
    float entry = 1.0 - pow(1.0 - entryLocal, ENTRY_EASE);
    float entryForm = ENTRY_FORM_FLOOR + (1.0 - ENTRY_FORM_FLOOR) * entry;

    float dGlass = glassAt(muv);
    float dInk = inkAt(muv);
    vec2 soft = softAt(muv);

    vec2 inkHalf = max(uInkRect.zw * 0.5, vec2(1.0));
    vec2 inkCentre = uInkRect.xy + inkHalf;
    float lampX = uPointer.x + uSignatureLead;
    float lampSigma = max(uInkRect.z * INK_LAMP_SIGMA, 8.0);
    float lamp = exp(-sq((pixelPos.x - lampX) / lampSigma));
    float lampBand = 1.0 - smoothstep(inkHalf.y * 2.6, inkHalf.y * 6.2, abs(pixelPos.y - inkCentre.y));
    float lampField = lamp * lampBand * reveal;

    float ruleY = uInkRect.y + uInkRect.w + INK_RULE_GAP_PX * unit;
    float ruleDist = abs(pixelPos.y - ruleY);
    float ruleSpan = 1.0 - smoothstep(inkHalf.x * 0.92, inkHalf.x * 1.12, abs(pixelPos.x - inkCentre.x));

    float nearGlass = step(-maskRange * 0.98, dGlass);
    float nearInk = max(step(-maskRange * 0.98, dInk),
                        step(ruleDist, INK_RULE_SOFT_PX * unit * 3.0) * ruleSpan) * step(0.0005, lampField);
    float halo = max(soft.x, soft.y);

    if (nearGlass + nearInk < 0.5 && halo <= 0.0015) {
        discard;
    }

    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    float glyphY = clamp((pixelPos.y - (uLockupMetrics.y - cap)) / max(cap, 1.0), 0.0, 1.0);
    vec3 accent = accentAt(uAccentTop, uAccentBottom, glyphY);
    vec3 accentHot = themeNeon(glyphY, uAccentTop, uAccentBottom);

    vec3 accum = vec3(0.0);
    float accumAlpha = 0.0;

    float coverage = 0.0;
    if (nearGlass > 0.5) {
        float fw = max(length(vec2(dFdx(dGlass), dFdy(dGlass))), 1.0e-4);
        float aa = max(fw * AA_WIDTH, AA_FLOOR);
        coverage = clamp(dGlass / (aa * 2.0) + 0.5, 0.0, 1.0);

        if (coverage > 0.0015) {
            float dr = glassAt(muv + vec2(texel.x, 0.0));
            float dl = glassAt(muv - vec2(texel.x, 0.0));
            float du = glassAt(muv + vec2(0.0, texel.y));
            float dd = glassAt(muv - vec2(0.0, texel.y));
            vec2 grad = vec2(dr - dl, du - dd);
            float gradLen = length(grad);
            float gradFloor = max(uMaskRange * (2.0 / 255.0), 1.0e-5);
            vec2 nDir = gradLen > 1.0e-5 ? -grad / gradLen : vec2(0.0, -1.0);
            float gradFade = smoothstep(gradFloor * 0.5, gradFloor * 1.5, gradLen);
            vec2 n = nDir * gradFade;
            vec2 nUv = vec2(n.x, -n.y);

            float inside = max(dGlass, 0.0);
            float bevelBase = max(cap * BEVEL_CAP_RATIO, BEVEL_MIN_PX);
            float probeStep = bevelBase * BEVEL_PROBE;
            float reach = inside + probeStep;
            float dOpp = glassAt(muv - nDir * (probeStep * texel));
            float probeHalf = 0.5 * (reach + clamp(dOpp, -reach, reach));
            float halfWidth = max(mix(inside, probeHalf, gradFade), 0.0);
            float bevel = max(min(bevelBase, halfWidth), BEVEL_FLOOR_PX);
            float shoulder = pow(clamp(1.0 - inside / bevel, 0.0, 1.0), WALL_SHARP);

            float facetCell = max(cap * FACET_CAP_RATIO, FACET_MIN_PX * unit);
            vec2 fpr = vec2(FACET_ANGLE_COS * pixelPos.x - FACET_ANGLE_SIN * pixelPos.y,
                            FACET_ANGLE_SIN * pixelPos.x + FACET_ANGLE_COS * pixelPos.y);
            vec2 fpc = fpr / facetCell;
            vec3 facetDist;
            vec2 idNear;
            vec2 idMid;
            vec2 idFar;
            voronoiFacet(fpc, facetDist, idNear, idMid, idFar);
            float edgeMid = (facetDist.y - facetDist.x) * 0.5;
            float edgeFar = (facetDist.z - facetDist.x) * 0.5;
            float blendWidth = max(fwidth(edgeMid) * FACET_BLEND_PX, FACET_BLEND_PX * 0.55 / facetCell);
            float wMid = 1.0 - smoothstep(0.0, blendWidth, edgeMid);
            float wFar = 1.0 - smoothstep(0.0, blendWidth, edgeFar);
            vec2 plateRaw = (facetSlope(idNear) + facetSlope(idMid) * wMid + facetSlope(idFar) * wFar)
                    / (1.0 + wMid + wFar);
            vec2 plate = vec2(FACET_ANGLE_COS * plateRaw.x + FACET_ANGLE_SIN * plateRaw.y,
                              -FACET_ANGLE_SIN * plateRaw.x + FACET_ANGLE_COS * plateRaw.y);
            float facetWeight = smoothstep(0.0, FACET_WEIGHT_KNEE, 1.0 - shoulder);
            vec2 slopeFacet = plate * (FACET_TILT * facetWeight);
            vec2 slopeBevel = n * (shoulder * SLOPE_MAX * entryForm);
            vec3 N = normalize(vec3(slopeBevel + slopeFacet, 1.0));
            vec3 V = vec3(0.0, 0.0, 1.0);
            float fresnel = pow5(1.0 - clamp(N.z, 0.0, 1.0));

            float edgeLens = exp(-inside / max(bevel, 1.0));
            vec2 lens = nUv * (edgeLens * (1.0 + LENS_PX) * entryForm) * unit / max(uViewport, vec2(1.0));
            vec2 chroma = nUv * (edgeLens * CHROMA_PX * entryForm) * unit / max(uViewport, vec2(1.0));
            vec3 frost = frosted(uv - lens);
            vec3 sharp = crisp(uv - lens * 0.5);
            vec3 backdrop = mix(frost, sharp, 0.14);
            vec3 disp = vec3(frosted(uv - lens + chroma).r, frost.g, frosted(uv - lens - chroma).b);
            vec3 split = disp + (sharp - frost) * 0.14;
            backdrop = mix(backdrop, split, edgeLens * 0.55);

            vec2 grainUv = pixelPos / max(cap, 1.0);
            float fineFreq = min(GRAIN_FINE_FREQ, cap / GRAIN_FINE_MIN_PX);
            float lustre = vnoise(grainUv * vec2(GRAIN_COARSE_FREQ, GRAIN_COARSE_FREQ * GRAIN_COARSE_STRETCH) + 11.3) - 0.5;
            float fine = vnoise(grainUv * vec2(fineFreq * GRAIN_FINE_STRETCH, fineFreq) + 37.7) - 0.5;
            float grain = 1.0 + lustre * GRAIN_COARSE_AMP + fine * GRAIN_FINE_AMP;

            float refY = clamp(glyphY * CHROME_REF_SCALE + CHROME_REF_BIAS
                    + n.y * CHROME_BEND * shoulder
                    + slopeFacet.y * CHROME_FACET_BEND, 0.0, 1.0);
            vec3 chromeSky = mix(CHROME_SKY, accentAt(uAccentTop, uAccentBottom, 0.0), CHROME_ACCENT_SKY);
            vec3 chromeHorizon = mix(CHROME_HORIZON, accentAt(uAccentTop, uAccentBottom, CHROME_H0), CHROME_ACCENT_HORIZON);
            vec3 chromeGround = mix(CHROME_GROUND, accentAt(uAccentTop, uAccentBottom, 1.0), CHROME_ACCENT_GROUND);
            float horizonLow = smoothstep(CHROME_H0 - CHROME_LOW_SPAN, CHROME_H0 + 0.02, refY);
            float horizonHigh = smoothstep(CHROME_H0, CHROME_H1, refY);
            vec3 metal = mix(chromeSky, chromeHorizon, horizonLow);
            metal = mix(metal, chromeGround, horizonHigh);
            metal *= grain * (1.0 - CHROME_DEPTH * smoothstep(0.20, 1.0, glyphY) * 0.62);
            vec3 body = addLight(metal, backdrop * ENV_GAIN * (0.34 + 0.66 * shoulder));

            float swing = sin(uTime * LIGHT_DRIFT_RATE) * LIGHT_DRIFT_SWING
                    + clamp(uPointer.x / max(uMaskSize.x, 1.0) - 0.5, -0.5, 0.5) * LIGHT_POINTER_SWING * presence;
            float cs = cos(swing);
            float sn = sin(swing);
            vec3 L = normalize(vec3(LIGHT_BASE.x * cs - LIGHT_BASE.y * sn,
                                    LIGHT_BASE.x * sn + LIGHT_BASE.y * cs,
                                    LIGHT_BASE.z));
            vec3 H = normalize(L + V);
            float nl = clamp(dot(N, L), 0.0, 1.0);

            float rimWidth = max(max(cap * RIM_WIDTH_CAP, RIM_WIDTH_MIN * unit), 1.0);
            float rimAa = max(fw * 0.60, 0.35);
            float rimBand = 1.0 - smoothstep(rimWidth - rimAa, rimWidth + rimAa, abs(dGlass));

            float anisoSquash = ANISO_SQUASH * (1.0 + ANISO_BREATHE * sin(uTime * ANISO_RATE + 0.63));
            vec3 Naniso = normalize(vec3(N.x, N.y * anisoSquash, N.z));
            float nhA = clamp(dot(Naniso, H), 0.0, 1.0);
            float sweepPhase = fract(uTime / SWEEP_PERIOD) * (1.0 + 2.0 * SWEEP_WIDTH) - SWEEP_WIDTH;
            float sweepCoord = muv.x + (muv.y - 0.5) * SWEEP_SKEW;
            float sweepA = exp(-sq((sweepCoord - sweepPhase) / SWEEP_WIDTH));
            float sweepPhase2 = fract(uTime / SWEEP_PERIOD_2 + 0.37) * (1.0 + 2.0 * SWEEP_WIDTH_2) - SWEEP_WIDTH_2;
            float sweepCoord2 = muv.x + (muv.y - 0.5) * SWEEP_SKEW_2;
            float sweepB = exp(-sq((sweepCoord2 - sweepPhase2) / SWEEP_WIDTH_2));
            float sweep = clamp(sweepA + sweepB * SWEEP_SECOND, 0.0, 1.45);
            float specWeight = (1.0 - SPEC_SHOULDER) + SPEC_SHOULDER * shoulder;
            float spec = pow(nhA, SPEC_POWER) * (SPEC_GAIN + SWEEP_GAIN * sweep) * specWeight;

            float wallShape = pow(shoulder, CHAMFER_TIGHT);
            float upFace = clamp(-n.y, 0.0, 1.0);
            float chamfer = wallShape * (CHAMFER_FLOOR + CHAMFER_LIT * nl + CHAMFER_SKY * upFace);
            vec3 chamferColor = mix(LIGHT_COLOR, accentHot, CHAMFER_ACCENT);

            body = addLight(body, mix(LIGHT_COLOR, accent, 0.30) * sweep * SHEEN_GAIN * (0.40 + 0.60 * shoulder));
            body *= 1.0 - wallShape * clamp(-dot(N, L), 0.0, 1.0) * WALL_SHADE_GAIN;
            body = addLight(body, chamferColor * chamfer);
            body = addLight(body, mix(LIGHT_COLOR, accentHot, 0.34) * spec);
            body = addLight(body, mix(AURA_COOL, vec3(1.0), 0.45) * fresnel * FRESNEL_GAIN * shoulder);

            vec2 toPointer = pixelPos - uPointer;
            float contourSigma = max(cap * CONTOUR_SIGMA_CAP, 8.0);
            float crest = exp(-dot(toPointer, toPointer) / sq(contourSigma));
            float phase = fract((pixelPos.x * 0.7 + pixelPos.y * 0.7) / max(cap * 3.0, 1.0));
            float travel = sin(TAU * (phase * CONTOUR_WAVES - uTime * CONTOUR_SHIMMER_SPEED));
            float field = (CONTOUR_AMBIENT + (1.0 - CONTOUR_AMBIENT) * crest)
                    * (1.0 + CONTOUR_SHIMMER_AMOUNT * travel) * presence;
            vec3 neon = themeNeon(clamp(muv.y, 0.0, 1.0), uAccentTop, uAccentBottom);
            body = addLight(body, neon * rimBand * field * CONTOUR_RIM_GAIN);
            body = addLight(body, neon * exp(-inside / max(cap * 0.16, 1.0)) * field * CONTOUR_SSS_GAIN);

            if (uLightMode > 0.5) {
                float inkLow = smoothstep(0.224, 0.452, refY);
                float inkHigh = smoothstep(0.404, 0.697, refY);
                vec3 lightSky = mix(vec3(0.548, 0.556, 0.622), accentAt(uAccentTop, uAccentBottom, 0.0), 0.235);
                vec3 lightHorizon = mix(vec3(0.302, 0.308, 0.366), accentAt(uAccentTop, uAccentBottom, CHROME_H0), 0.250);
                vec3 lightGround = mix(vec3(0.082, 0.082, 0.120), accentAt(uAccentTop, uAccentBottom, 1.0), 0.265);
                vec3 graphite = mix(lightSky, lightHorizon, inkLow);
                graphite = mix(graphite, lightGround, inkHigh);
                graphite *= grain;
                graphite = addLight(graphite, backdrop * 0.180 * shoulder);
                graphite *= 1.0 - wallShape * clamp(-dot(N, L), 0.0, 1.0) * 0.280;
                graphite = addLight(graphite, chamferColor * chamfer * 0.980);
                graphite = addLight(graphite, mix(LIGHT_COLOR, accentHot, 0.30) * spec * 0.780);
                graphite = addLight(graphite, mix(LIGHT_COLOR, accent, 0.30) * sweep * SHEEN_GAIN * 0.62 * (0.40 + 0.60 * shoulder));
                graphite = addLight(graphite, neon * rimBand * field * 0.300);
                body = graphite;
            }

            float aIn = coverage * 0.985;
            accum += body * aIn;
            accumAlpha += aIn;
        }
    }

    float outsideMask = 1.0 - coverage;
    if (outsideMask > 0.0015 && halo > 0.0015) {
        float tight = clamp(soft.x, 0.0, 1.0);
        float wide = clamp(soft.y, 0.0, 1.0);

        float coronaBreathe = 1.0 + CORONA_BREATHE * sin(uTime * CORONA_RATE);
        float bloomBreathe = 1.0 + BLOOM_BREATHE * sin(uTime * BLOOM_RATE + 1.37);
        float corona = pow(tight, CORONA_SHAPE) * CORONA_GAIN * coronaBreathe;
        float bloomWide = pow(wide, BLOOM_SHAPE) * BLOOM_GAIN * bloomBreathe;

        float pointerLift = 1.0;
        if (nearGlass > 0.5) {
            vec2 toPointer = pixelPos - uPointer;
            pointerLift = 1.0 + CONTOUR_HALO_GAIN * exp(-dot(toPointer, toPointer) / sq(max(cap * CONTOUR_SIGMA_CAP, 8.0))) * presence;
        }

        float auraRaw = max(corona + bloomWide, 0.0) * pointerLift * outsideMask;
        float aura = (1.0 - exp(-auraRaw)) * AURA_CEIL;

        vec3 auraAccent = themeNeon(glyphY, uAccentTop, uAccentBottom);
        float coreMix = clamp(pow(tight, 1.6), 0.0, 1.0);
        vec3 glowColor = mix(auraAccent, mix(auraAccent, AURA_CORE, AURA_CORE_MIX), coreMix);

        float shadeShape = uLightMode > 0.5 ? SHADOW_SHAPE_LIGHT : SHADOW_SHAPE;
        float shadeGain = uLightMode > 0.5 ? SHADOW_GAIN_LIGHT : SHADOW_GAIN;
        float shade = pow(wide, shadeShape) * shadeGain * outsideMask;
        vec3 shadeColor = uLightMode > 0.5
                ? mix(vec3(0.300, 0.292, 0.340), accentAt(uAccentTop, uAccentBottom, 1.0), 0.180)
                : mix(vec3(0.008, 0.008, 0.016), accentAt(uAccentTop, uAccentBottom, 1.0) * 0.120, 0.500);

        if (uLightMode > 0.5) {
            aura *= AURA_LIGHT_SCALE;
            glowColor = mix(glowColor, auraAccent, 0.600);
        }

        aura = clamp(aura, 0.0, 1.0);
        float free = 1.0 - min(accumAlpha, 1.0);
        float glowAlpha = aura * free;
        accum += glowColor * glowAlpha;
        accumAlpha += glowAlpha;

        float shadeAlpha = clamp(shade, 0.0, 1.0) * free * (1.0 - aura);
        accum += shadeColor * shadeAlpha;
        accumAlpha += shadeAlpha;
    }

    if (nearInk > 0.5) {
        float outsideInk = max(-dInk, 0.0);
        float glowSigma = max(maskRange * INK_GLOW_RATIO, 2.0);
        float shape = exp(-sq(outsideInk) / sq(glowSigma)) * rangeFade(outsideInk);

        float ruleCore = 1.0 - smoothstep(INK_RULE_HALF_PX * unit - 0.4, INK_RULE_HALF_PX * unit + 0.4, ruleDist);
        float ruleSoft = exp(-sq(ruleDist) / sq(max(INK_RULE_SOFT_PX * unit, 1.6)));
        float rule = (ruleCore * INK_RULE_GAIN + ruleSoft * INK_RULE_SOFT_GAIN) * ruleSpan;

        float heat = clamp(lamp * 1.15, 0.0, 1.0);
        vec3 inkBase = themeNeon(clamp((pixelPos.x - uInkRect.x) / max(uInkRect.z, 1.0), 0.0, 1.0), uAccentTop, uAccentBottom);
        vec3 tint = mix(inkBase, mix(inkBase, AURA_CORE, 0.58), heat * heat);
        float alphaInk = clamp(shape * INK_GLOW_GAIN + rule, 0.0, 1.0) * lampField * pow(reveal, 0.75);

        if (uLightMode > 0.5) {
            tint = mix(inkBase, vec3(0.16, 0.14, 0.20), 0.46);
            alphaInk *= 0.72;
        }

        if (alphaInk > 1.0e-4) {
            float free = 1.0 - min(accumAlpha, 1.0);
            accum += tint * alphaInk * free;
            accumAlpha += alphaInk * free;
        }
    }

    float alpha = clamp(accumAlpha, 0.0, 1.0) * entry;
    if (alpha <= 0.0025) {
        discard;
    }
    vec3 straight = accum * entry / max(alpha, 1.0e-4);
    straight += triangularNoise(vScreen, fract(uTime * 0.43)) * (1.0 / 255.0);
    FragColor = vec4(straight, alpha);
}
