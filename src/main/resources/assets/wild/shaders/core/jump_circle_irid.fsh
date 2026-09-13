#version 150

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

layout(std140) uniform JumpCircle {
    vec4 u_Params;
};

#define u_IridSpeed u_Params.x
#define u_Brightness u_Params.y
#define u_Opacity u_Params.z

in vec3 vViewPos;
in vec3 vNormal;
in vec2 vUv;
in vec4 vColor;
in vec4 vClip;

out vec4 fragColor;

vec3 srgb_to_linear(vec3 c) {
    return vec3(
        c.r <= 0.04045 ? c.r / 12.92 : pow((c.r + 0.055) / 1.055, 2.4),
        c.g <= 0.04045 ? c.g / 12.92 : pow((c.g + 0.055) / 1.055, 2.4),
        c.b <= 0.04045 ? c.b / 12.92 : pow((c.b + 0.055) / 1.055, 2.4)
    );
}

vec3 linear_to_srgb(vec3 c) {
    c = clamp(c, 0.0, 1.0);
    return vec3(
        c.r <= 0.0031308 ? c.r * 12.92 : 1.055 * pow(c.r, 0.4166666666666667) - 0.055,
        c.g <= 0.0031308 ? c.g * 12.92 : 1.055 * pow(c.g, 0.4166666666666667) - 0.055,
        c.b <= 0.0031308 ? c.b * 12.92 : 1.055 * pow(c.b, 0.4166666666666667) - 0.055
    );
}

vec3 linear_srgb_to_oklab(vec3 c) {
    float l = 0.4122214708 * c.r + 0.5363325363 * c.g + 0.0514459929 * c.b;
    float m = 0.2119034982 * c.r + 0.6806995451 * c.g + 0.1073969566 * c.b;
    float s = 0.0883024619 * c.r + 0.2817188376 * c.g + 0.6299787005 * c.b;
    float l_ = sign(l) * pow(abs(l), 0.333333333333);
    float m_ = sign(m) * pow(abs(m), 0.333333333333);
    float s_ = sign(s) * pow(abs(s), 0.333333333333);
    return vec3(
        0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_,
        1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_,
        0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_
    );
}

vec3 oklab_to_linear_srgb(vec3 c) {
    float l_ = c.x + 0.3963377774 * c.y + 0.2158037573 * c.z;
    float m_ = c.x - 0.1055613458 * c.y - 0.0638541728 * c.z;
    float s_ = c.x - 0.0894841775 * c.y - 1.2914855480 * c.z;
    float l = l_ * l_ * l_;
    float m = m_ * m_ * m_;
    float s = s_ * s_ * s_;
    return vec3(
        4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
        -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
        -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
    );
}

vec3 oklab_mix(vec3 colA, vec3 colB, float t) {
    vec3 labA = linear_srgb_to_oklab(colA);
    vec3 labB = linear_srgb_to_oklab(colB);
    return oklab_to_linear_srgb(mix(labA, labB, t));
}

vec3 oklab_mix_srgb(vec3 colA, vec3 colB, float t) {
    return linear_to_srgb(oklab_mix(srgb_to_linear(colA), srgb_to_linear(colB), clamp(t, 0.0, 1.0)));
}

float wild_sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float wild_hash21(vec2 p) {
    p = fract(p * vec2(127.1, 311.7));
    p += dot(p, p + 34.45);
    return fract(p.x * p.y);
}

float wild_vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(
        mix(wild_hash21(i),                  wild_hash21(i + vec2(1.0, 0.0)), u.x),
        mix(wild_hash21(i + vec2(0.0, 1.0)), wild_hash21(i + vec2(1.0, 1.0)), u.x),
        u.y
    );
}

float wild_fbm(vec2 p) {
    mat2 rot = mat2(0.866, -0.5, 0.5, 0.866);
    float v = 0.0;
    float a = 0.55;
    v += wild_vnoise(p) * a;       p = rot * p * 2.05 + vec2(11.31, 7.43);   a *= 0.55;
    v += wild_vnoise(p) * a;       p = rot * p * 2.13 + vec2(-3.78, 5.29);   a *= 0.55;
    v += wild_vnoise(p) * a;
    return v;
}

vec3 wild_tone(vec3 c) {
    float m = max(max(c.r, c.g), c.b);
    return c / (1.0 + m * 0.52);
}

mat2 wild_rotMat(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, -s, s, c);
}

vec3 wild_hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec2 localUv = vUv * 2.0 - 1.0;
    float r = length(localUv);

    float t = GameTime * 220.0;
    float life = wild_sat(vColor.a);
    float lifeEase = life * life * (3.0 - 2.0 * life);
    float decay = 1.0 - life;

    float spawnPhase = smoothstep(0.0, 0.55, lifeEase);
    float stablePhase = smoothstep(0.45, 0.92, life);
    float dyingPhase = 1.0 - smoothstep(0.05, 0.50, life);
    float deepDeath = 1.0 - smoothstep(0.0, 0.20, life);

    float radius = 0.62;
    float thickPulse = 1.0 + 0.085 * sin(t * 0.18);
    float thicknessBase = mix(0.018, 0.058, lifeEase);
    float thickness = thicknessBase * thickPulse;

    float rd = r - radius;
    float sdf = abs(rd) - thickness;
    float aaW = max(fwidth(r), 0.0008) * 1.25;
    float ringMask = 1.0 - smoothstep(-aaW, aaW, sdf);
    float outsideMask = 1.0 - ringMask;

    float ringCore = exp(-(rd * 22.0) * (rd * 22.0));
    float ringGlow = exp(-(rd * 8.0) * (rd * 8.0));

    float outsideD = max(sdf, 0.0);
    float bloomTight = exp(-pow(outsideD * 7.5, 1.5)) * outsideMask;
    float bloomWide = exp(-pow(outsideD * 3.6, 1.35)) * outsideMask;

    float visibility = 1.0 - smoothstep(0.82, 1.04, r);
    bloomTight *= visibility;
    bloomWide *= visibility;

    vec2 dir = localUv / max(r, 1e-4);
    float ang = atan(dir.y, dir.x);

    mat2 R1 = wild_rotMat(t * 0.05);
    mat2 R2 = wild_rotMat(-t * 0.085);
    vec2 dir1 = R1 * dir;
    vec2 dir2 = R2 * dir;

    float rayN1 = wild_fbm(dir1 * 4.0);
    float rayN2 = wild_fbm(dir2 * 7.5 + vec2(5.31, -2.18));
    float rayN3 = wild_vnoise(dir1 * 13.0 + vec2(0.0, t * 0.04));
    float rayMix = wild_sat(rayN1 * 0.55 + rayN2 * 0.32 + rayN3 * 0.18);
    float rays = smoothstep(0.30, 0.85, rayMix) * bloomWide * lifeEase * 0.85;

    float sweepA = wild_sat(0.5 + 0.5 * sin(ang * 4.0 + t * 0.38 + sin(t * 0.10) * 0.6));
    float sweepB = wild_sat(0.5 + 0.5 * sin(ang * 7.0 - t * 0.27));
    float sweep = (smoothstep(0.55, 1.0, sweepA) + smoothstep(0.65, 1.0, sweepB) * 0.65) * ringCore * stablePhase;

    float twN1 = wild_fbm(dir1 * 14.0 + vec2(t * 0.14, 0.0));
    float twN2 = wild_fbm(dir2 * 22.0 + vec2(0.0, t * 0.18));
    float twinkle = smoothstep(0.55, 0.95, twN1 * 0.6 + twN2 * 0.5) * ringCore * stablePhase;

    float distortStrength = 0.10 * lifeEase;
    vec2 baseOffset = dir * distortStrength * ringGlow;
    float fieldScale = 3.0;
    vec2 baseField = localUv * fieldScale + vec2(t * 0.040, -t * 0.025);
    float fieldR = wild_fbm(baseField + baseOffset * 1.30 * fieldScale);
    float fieldG = wild_fbm(baseField * 1.07 + baseOffset * 0.95 * fieldScale + vec2(5.31, -2.18));
    float fieldB = wild_fbm(baseField * 0.93 + baseOffset * 0.55 * fieldScale + vec2(-3.27, 4.62));
    vec3 swirl = vec3(fieldR, fieldG, fieldB);
    float swirlMid = (fieldR + fieldG + fieldB) * (1.0 / 3.0);
    float chroma = wild_sat(length(swirl - vec3(swirlMid)) * 1.65);

    vec3 themeBase = max(vColor.rgb, vec3(0.06));
    float themeLum = dot(themeBase, vec3(0.299, 0.587, 0.114));
    vec3 themeHot = mix(vec3(themeLum), themeBase, 1.20) + themeBase * 0.12;
    vec3 themeSoft = oklab_mix_srgb(themeBase, vec3(1.0), 0.20);

    vec3 ringPaint = themeSoft * (0.82 + swirlMid * 0.22);
    vec3 ringHot = themeHot * (1.00 + swirlMid * 0.18);
    vec3 ringColor = oklab_mix_srgb(ringPaint, ringHot, ringCore);
    vec3 haloColor = themeSoft * 0.55 + themeHot * 0.18;
    vec3 rayColor = themeHot * (0.70 + chroma * 0.35);

    // Бегущая по кругу светящаяся радужная полоса.
    // GameTime в MC — это доля игрового дня (0..1 за ~20 мин), т.е. растёт
    // очень медленно (~0.0008/сек). Поэтому здесь нужен большой множитель:
    // GameTime * 1600 даёт примерно полтора оборота полосы в секунду.
    float bandClock = GameTime * 1600.0 * max(u_IridSpeed, 0.0);
    float angN = ang / 6.2831853 + 0.5;                 // угол -> [0, 1] по кольцу
    float bandPos = fract(bandClock);                   // позиция головы полосы
    float bandDelta = angN - bandPos;
    bandDelta -= floor(bandDelta + 0.5);                // ближайшее расстояние по кругу [-0.5, 0.5]

    // Хвост кометы: спереди узкое ядро, сзади длинный шлейф.
    float trail = bandDelta <= 0.0 ? -bandDelta : bandDelta * 3.2;
    float bandCore = exp(-pow(abs(bandDelta) / 0.05, 2.0));
    float bandGlow = exp(-pow(trail / 0.22, 1.6));

    // Радуга бежит вдоль полосы и циклится во времени.
    float bandHue = fract(bandClock * 0.35 + angN);
    vec3 bandRainbow = wild_hsv2rgb(vec3(bandHue, 0.60, 1.0));
    vec3 bandColor = oklab_mix_srgb(themeHot, bandRainbow, 0.75);

    // Полоса живёт только на самом кольце и только в стабильной фазе жизни.
    float bandShape = (ringMask * 0.6 + ringCore * 1.0 + bloomTight * 0.35) * stablePhase;
    float bandMask = (bandCore + bandGlow * 0.5) * bandShape;
    float bandAlpha = wild_sat(bandCore + bandGlow * 0.45) * bandShape;

    vec3 color = ringColor * ringMask * 0.40
               + ringHot * ringCore * 0.28
               + ringHot * sweep * 0.32
               + ringHot * twinkle * 0.42
               + haloColor * bloomTight * 0.20
               + haloColor * bloomWide * 0.13
               + rayColor * rays * 0.34
               + swirl * ringGlow * 0.05
               + vec3(chroma * 0.20, chroma * 0.06, chroma * 0.18) * ringCore
               + bandColor * bandMask * 1.6
               + vec3(1.0) * bandCore * bandShape * 0.45;

    color = wild_tone(color * u_Brightness);

    float dBand1 = wild_fbm(localUv * 3.4 + vec2(t * 0.038, -t * 0.022));
    float dBand2 = wild_fbm(localUv * 6.8 + vec2(-t * 0.045, t * 0.030));
    float dCombo = mix(dBand1, dBand2, 0.40);

    float dissolveAmount = smoothstep(0.0, 0.85, dyingPhase);
    float dThr = mix(-0.05, 0.65, dissolveAmount);
    float dissolveMask = smoothstep(dThr - 0.35, dThr + 0.12, dCombo);
    float dissolve = mix(1.0, dissolveMask, dissolveAmount);

    float crackBand = wild_fbm(dir1 * 2.6 + vec2(t * 0.025, decay * 1.4));
    float crackInfluence = smoothstep(0.20, 0.85, dyingPhase) * 0.85;
    float crackMask = mix(1.0, smoothstep(0.20, 0.70, crackBand + 0.18), crackInfluence);

    float emberShimmer = mix(1.0, 0.55 + wild_fbm(dir2 * 6.0 + vec2(t * 0.10, 0.0)) * 0.65, deepDeath);

    float ringAlpha = ringMask * 0.42 + ringCore * 0.30 + sweep * 0.18 + twinkle * 0.30 + bandAlpha * 0.85;
    float haloAlpha = bloomTight * 0.16 + bloomWide * 0.18;
    float rayAlpha = rays * 0.30;

    float alpha = wild_sat(ringAlpha + haloAlpha + rayAlpha) * lifeEase * visibility * dissolve * crackMask * emberShimmer * u_Opacity;

    if (alpha <= 0.003) discard;

    fragColor = vec4(color, alpha) * ColorModulator;
}
