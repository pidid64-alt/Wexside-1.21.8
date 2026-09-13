#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uBackground;
uniform vec2 uViewport;
uniform vec2 uTextureSize;
uniform vec2 uSourceScale;
uniform vec4 uContent;
uniform vec4 uPill;
uniform vec2 uLocalMouse;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uRadius;
uniform float uPillRadius;
uniform float uTime;
uniform float uHover;
uniform float uPillGlow;
uniform float uPillVelocity;
uniform float uEntry;
uniform float uFlash;
uniform float uLightMode;

out vec4 FragColor;

const vec3 BODY_VEIL = vec3(0.0430, 0.0416, 0.0672);
const float BODY_ALPHA = 0.62;
const float BODY_TRANSMIT = 0.30;
const float VEIL_ACCENT = 0.060;

const float TRACK_LENS_PX = 7.0;
const float TRACK_LENS_SLOPE = 0.78;
const float TRACK_LENS_CEIL = 2.40;
const float TRACK_REFRACT_PX = 3.6;
const float GROOVE_PX = 5.0;
const float GROOVE_GAIN = 0.300;
const float TRACK_RIM_PX = 1.15;
const float TRACK_RIM_FLOOR = 0.150;
const float TRACK_RIM_LIT = 0.360;

const float PILL_LENS_PX = 8.0;
const float PILL_LENS_SLOPE = 0.92;
const float PILL_LENS_CEIL = 2.80;
const float PILL_BODY = 0.230;
const float PILL_ACCENT = 0.300;
const float PILL_RIM_PX = 1.05;
const float PILL_RIM_FLOOR = 0.300;
const float PILL_TOP_GAIN = 0.300;
const float PILL_SSS_GAIN = 0.220;
const float PILL_SSS_PX = 14.0;
const float PILL_GLOW_PX = 9.0;
const float PILL_GLOW_GAIN = 0.135;
const float PILL_SMEAR_GAIN = 0.140;

float sq(float x) {
    return x * x;
}

float pow5(float x) {
    float a = x * x;
    return a * a * x;
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

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

vec2 region() {
    return uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
}

vec3 blurred(vec2 uv) {
    vec2 texel = 1.0 / max(uTextureSize, vec2(1.0));
    vec2 r = region();
    return texture(uBackground, clamp(uv * r, texel * 0.5, r - texel * 0.5)).rgb;
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

float lensSlope(float t, float base, float ceiling) {
    float shoulder = 1.0 - t;
    float profile = sqrt(max(1.0 - sq(shoulder), 0.030));
    return min(base * shoulder / profile, ceiling);
}

void main() {
    vec2 size = max(uContent.zw, vec2(1.0));
    vec2 half_ = size * 0.5;
    vec2 p = vLocal - uContent.xy - half_;

    float unit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;
    float radius = min(uRadius, min(half_.x, half_.y));
    vec3 field = roundBoxField(p, half_, radius);
    vec2 nOut = field.xy;
    float dC = field.z;
    float aa = max(length(vec2(dFdx(dC), dFdy(dC))) * 0.70, 0.55);
    float coverage = 1.0 - smoothstep(-aa, aa, dC);
    float dIn = max(-dC, 0.0);
    float ad = abs(dC);

    float entry = smoothstep(0.0, 1.0, uEntry);
    float hover = clamp(uHover, 0.0, 1.0);
    float flash = clamp(uFlash, 0.0, 1.0);
    if (coverage <= 0.001 || entry <= 0.002) {
        discard;
    }

    float vertical = clamp((p.y + half_.y) / max(size.y, 1.0), 0.0, 1.0);
    vec3 accent = accentAt(vertical);
    vec3 accentHot = accentNeon(vertical, 0.94);
    vec2 lightPos = (uLocalMouse - 0.5) * size;

    float trackWidth = max(min(TRACK_LENS_PX * unit, min(half_.y * 0.60, radius * 1.15)), 2.0);
    float tT = clamp(dIn / trackWidth, 0.0, 1.0);
    vec3 N = normalize(vec3(nOut * lensSlope(tT, TRACK_LENS_SLOPE, TRACK_LENS_CEIL), 1.0));

    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    vec2 refract2 = N.xy * TRACK_REFRACT_PX * unit / max(uViewport, vec2(1.0));
    vec3 backdrop = blurred(uv - refract2);

    vec3 body;
    float bodyAlpha;
    vec3 rimColor;
    vec3 pillTint;
    float pillBodyGain;
    if (uLightMode > 0.5) {
        body = mix(backdrop, vec3(0.984, 0.984, 0.994), 0.78);
        bodyAlpha = 0.72 + hover * 0.06;
        rimColor = mix(vec3(0.18, 0.19, 0.26), accent, 0.26);
        pillTint = vec3(1.0);
        pillBodyGain = 0.86;
    } else {
        vec3 veilTint = mix(BODY_VEIL, accent, VEIL_ACCENT);
        body = backdrop * BODY_TRANSMIT + veilTint * mix(1.22, 0.82, vertical);
        bodyAlpha = BODY_ALPHA + hover * 0.06;
        rimColor = mix(vec3(0.90, 0.94, 1.0), accentHot, 0.36 + 0.24 * hover);
        pillTint = mix(vec3(0.94, 0.96, 1.0), accentHot, PILL_ACCENT);
        pillBodyGain = PILL_BODY;
    }

    float grooveTop = exp(-dIn / max(GROOVE_PX * unit, 2.0)) * max(-nOut.y, 0.0);
    float grooveBottom = exp(-dIn / max(GROOVE_PX * unit, 2.0)) * max(nOut.y, 0.0);
    body *= 1.0 - grooveTop * GROOVE_GAIN;
    body = addLight(body, mix(vec3(0.86, 0.90, 1.0), accentHot, 0.30) * grooveBottom * GROOVE_GAIN * 0.55);

    vec2 toTrackLight = lightPos - p;
    float trackCrest = exp(-dot(toTrackLight, toTrackLight) / sq(max(size.x * 0.22, 40.0)));
    float trackRim = exp(-sq(ad / max(TRACK_RIM_PX * unit, 0.80)));
    float trackRimLight = TRACK_RIM_FLOOR + TRACK_RIM_LIT * trackCrest * (0.35 + 0.65 * hover);

    vec3 surface = body;
    float alphaIn = coverage * bodyAlpha;
    float rimAlpha = coverage * clamp(trackRim * trackRimLight, 0.0, 1.0);
    surface = mix(surface, rimColor, clamp(rimAlpha / max(alphaIn + rimAlpha, 1.0e-4), 0.0, 1.0));
    float alpha = clamp(alphaIn + rimAlpha * (1.0 - alphaIn), 0.0, 1.0);

    vec2 pillHalf = max(uPill.zw * 0.5, vec2(0.5));
    vec2 pillCenter = vec2(uPill.x + pillHalf.x - half_.x, uPill.y + pillHalf.y - half_.y);
    float pillRadius = min(uPillRadius, min(pillHalf.x, pillHalf.y));
    vec3 pillField = roundBoxField(p - pillCenter, pillHalf, pillRadius);
    vec2 pillN2 = pillField.xy;
    float dP = pillField.z;
    float pillAa = max(length(vec2(dFdx(dP), dFdy(dP))) * 0.70, 0.55);
    float pillCov = 1.0 - smoothstep(-pillAa, pillAa, dP);
    float pillIn = max(-dP, 0.0);
    float pillOut = max(dP, 0.0);

    float pillLensWidth = max(min(PILL_LENS_PX * unit, min(pillHalf.y * 0.62, pillRadius * 1.20)), 1.8);
    float tP = clamp(pillIn / pillLensWidth, 0.0, 1.0);
    vec3 pillN = normalize(vec3(pillN2 * lensSlope(tP, PILL_LENS_SLOPE, PILL_LENS_CEIL), 1.0));
    float pillFresnel = pow5(1.0 - clamp(pillN.z, 0.0, 1.0));

    float pillVertical = clamp((p.y - pillCenter.y + pillHalf.y) / max(pillHalf.y * 2.0, 1.0), 0.0, 1.0);
    vec3 pillBody = pillTint * mix(1.14, 0.82, pillVertical);
    float pillTopLit = exp(-pillIn / max(4.0 * unit, 1.6)) * max(-pillN2.y, 0.0);
    pillBody = addLight(pillBody, vec3(1.0) * pillTopLit * PILL_TOP_GAIN);
    float pillSss = exp(-pillIn / max(PILL_SSS_PX * unit, 4.0));
    pillBody = addLight(pillBody, accentHot * pillSss * PILL_SSS_GAIN * (0.40 + 0.60 * clamp(uPillGlow, 0.0, 1.0)));
    pillBody = addLight(pillBody, mix(vec3(0.90, 0.93, 1.0), accentHot, 0.45) * pillFresnel * 0.180);

    float pillRim = exp(-sq(abs(dP) / max(PILL_RIM_PX * unit, 0.78)));
    float pillA = pillCov * (pillBodyGain + pillRim * PILL_RIM_FLOOR);
    surface = mix(surface, pillBody, clamp(pillA, 0.0, 1.0));
    alpha = clamp(alpha + pillA * (1.0 - alpha) * 0.72, 0.0, 1.0);

    float pillVel = clamp(abs(uPillVelocity), 0.0, 1.0);
    float smearDir = uPillVelocity >= 0.0 ? 1.0 : -1.0;
    float smearX = (p.x - pillCenter.x) * smearDir;
    float smear = exp(-sq(max(-smearX - pillHalf.x, 0.0) / max(pillHalf.x * 0.85, 6.0)))
            * (1.0 - pillCov) * pillVel;
    float pillOuterGlow = exp(-sq(pillOut / max(PILL_GLOW_PX * unit, 3.0))) * clamp(uPillGlow, 0.0, 1.0);
    vec3 haloColor = mix(vec3(0.86, 0.90, 1.0), accentHot, 0.55);
    surface = addLight(surface, haloColor * pillOuterGlow * PILL_GLOW_GAIN * (1.0 - pillCov));
    surface = addLight(surface, haloColor * smear * PILL_SMEAR_GAIN);
    alpha = clamp(alpha + (pillOuterGlow * 0.22 + smear * 0.14) * (1.0 - alpha), 0.0, 1.0);

    surface = addLight(surface, vec3(1.0) * flash * 0.10);

    float alphaOut = coverage * alpha * entry;
    if (alphaOut <= 0.003) {
        discard;
    }
    vec3 straight = surface + interleavedGradient(vScreen) * (1.0 / 255.0);
    FragColor = vec4(straight, clamp(alphaOut, 0.0, 1.0));
}
