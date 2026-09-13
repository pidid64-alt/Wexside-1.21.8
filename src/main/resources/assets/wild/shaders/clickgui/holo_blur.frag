#version 330 core

in vec2 vUv;
out vec4 fragColor;

uniform sampler2D uScene;
uniform vec2  uResolution;
uniform float uTime;
uniform vec2  uMouse;
uniform float uIntensity;
uniform float uBlurMax;
uniform float uTint;
uniform float uMouseInfluence;
uniform float uNoiseScale;
uniform float uFlowSpeed;
uniform float uClarityRadius;
uniform float uContrast;
uniform float uVignette;
uniform float uBrightness;
uniform float uSaturation;
uniform float uEntry;
uniform vec2  uEntryCenter;

const float PI = 3.14159265;

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float amp = 0.5;
    for (int i = 0; i < 5; i++) {
        v += amp * vnoise(p);
        p *= 2.07;
        amp *= 0.5;
    }
    return v;
}

vec2 sceneUv(vec2 uv) {
    return vec2(uv.x, 1.0 - uv.y);
}

vec3 sampleVarBlur(vec2 uv, float radiusPx) {
    if (radiusPx < 0.6) return texture(uScene, sceneUv(uv)).rgb;
    vec2 texelSize = 1.0 / max(uResolution, vec2(1.0));
    vec3 sum = vec3(0.0);
    float wsum = 0.0;
    const int TAPS = 14;
    const float GA = 2.39996323;
    float jitter = hash21(gl_FragCoord.xy + uTime * 0.01) * GA;
    for (int i = 0; i < TAPS; i++) {
        float t = (float(i) + 0.5) / float(TAPS);
        float r = sqrt(t);
        float theta = float(i) * GA + jitter;
        vec2 offset = vec2(cos(theta), sin(theta)) * r * radiusPx * texelSize;
        float w = 1.0 - r * 0.42;
        sum += texture(uScene, sceneUv(uv + offset)).rgb * w;
        wsum += w;
    }
    return sum / max(wsum, 1e-4);
}

vec3 chromatic(vec2 uv, float strength) {
    vec2 dir = (uv - vec2(0.5)) * strength;
    vec2 texelSize = 1.0 / max(uResolution, vec2(1.0));
    float r = texture(uScene, sceneUv(uv + dir * texelSize * 6.0)).r;
    float g = texture(uScene, sceneUv(uv)).g;
    float b = texture(uScene, sceneUv(uv - dir * texelSize * 6.0)).b;
    return vec3(r, g, b);
}

void main() {
    vec2 uv = vUv;

    float aspect = uResolution.x / max(uResolution.y, 1.0);
    vec2 p = vec2(uv.x * aspect, uv.y);

    float entry = clamp(uEntry, 0.0, 1.0);
    float entryReveal = smoothstep(0.0, 0.85, entry);
    float entryPulse = sin(entry * PI);
    float entrySettle = smoothstep(0.18, 1.0, entry);
    float entryEarly = 1.0 - smoothstep(0.0, 0.45, entry);
    float entryLate = smoothstep(0.55, 1.0, entry);

    vec2 entryCenter = vec2(clamp(uEntryCenter.x, 0.0, 1.0) * aspect, clamp(uEntryCenter.y, 0.0, 1.0));
    vec2 entryDelta = p - entryCenter;
    float entryDist = length(entryDelta);

    float revealRadius = entryReveal * 1.95;
    float revealSoftness = 0.45 + (1.0 - entryReveal) * 0.55;
    float revealStrength = 1.0 - smoothstep(max(0.0, revealRadius - revealSoftness), revealRadius, entryDist);

    float scaleAnim = mix(1.55, 1.0, entrySettle);
    p = entryCenter + (p - entryCenter) * scaleAnim;

    vec2 mouseP = vec2(clamp(uMouse.x, 0.0, 1.0) * aspect, clamp(uMouse.y, 0.0, 1.0));
    vec2 toMouse = p - mouseP;
    float mDist = length(toMouse);

    float pull = exp(-mDist * 5.5) * uMouseInfluence;
    p -= normalize(toMouse + vec2(1e-4)) * pull;

    vec2 flow1 = vec2( uTime * uFlowSpeed * 0.085,  uTime * uFlowSpeed * 0.063);
    vec2 flow2 = vec2(-uTime * uFlowSpeed * 0.057,  uTime * uFlowSpeed * 0.078);

    float n1 = fbm(p * uNoiseScale + flow1);
    float n2 = fbm(p * (uNoiseScale * 1.55) + flow2 + vec2(n1) * 0.45);
    float mask = mix(n1, n2, 0.55);

    float lo = clamp(0.5 - uContrast * 0.32, 0.05, 0.49);
    float hi = clamp(0.5 + uContrast * 0.36, 0.51, 0.95);
    mask = smoothstep(lo, hi, mask);

    float clarity = exp(-mDist * (1.6 / max(uClarityRadius, 0.05))) * 0.7;
    mask = clamp(mask - clarity, 0.0, 1.0);

    mask *= revealStrength * entrySettle;

    float blurR = mask * uBlurMax;
    vec3 col = sampleVarBlur(uv, blurR);

    vec3 chromaCol = chromatic(uv, 0.65);
    col = mix(col, chromaCol, entryEarly * 0.45 * revealStrength);

    float luma = dot(col, vec3(0.299, 0.587, 0.114));
    col = mix(vec3(luma), col, 1.0 + uSaturation * entrySettle + entryPulse * 0.45);

    if (uTint > 0.001) {
        vec3 dir = vec3(0.10, 1.55, 3.10);
        vec3 shimmer = 0.5 + 0.5 * cos(uTime * 0.42 + mask * 4.20 + dir);
        float pulseTint = 1.0 + entryPulse * 0.85;
        float strength = uTint * (0.45 + 0.55 * mask) * pulseTint;
        vec3 tinted = col * (0.92 + shimmer * 0.55);
        col = mix(col, tinted, clamp(strength, 0.0, 1.3));
    }

    float pulseBright = 1.0 + entryPulse * 0.65;
    col *= (1.0 + mask * uBrightness * 0.55 * pulseBright);

    float bloomFade = exp(-entryDist * 1.4);
    vec3 bloomColor = 0.5 + 0.5 * cos(uTime * 0.45 + entryDist * 1.8 + vec3(0.20, 1.45, 3.00));
    col += bloomColor * bloomFade * entryPulse * 0.55 * revealStrength;

    float rimWidth = 0.10 + (1.0 - entryReveal) * 0.08;
    float rimInner = smoothstep(revealRadius - rimWidth, revealRadius - rimWidth * 0.3, entryDist);
    float rimOuter = 1.0 - smoothstep(revealRadius, revealRadius + rimWidth * 0.6, entryDist);
    float rim = rimInner * rimOuter;
    vec3 rimSpectrum = 0.55 + 0.45 * cos(uTime * 0.55 + entryDist * 4.0 + vec3(0.05, 1.40, 2.90));
    col += rimSpectrum * rim * (1.05 + entryPulse * 0.95);

    float rimGhost = smoothstep(revealRadius - rimWidth * 2.4, revealRadius - rimWidth * 0.6, entryDist)
                   * (1.0 - smoothstep(revealRadius - rimWidth * 0.6, revealRadius, entryDist));
    col += rimSpectrum * 0.22 * rimGhost * entryPulse;

    if (uVignette > 0.001) {
        vec2 vc = uv - 0.5;
        float vig = 1.0 - dot(vc, vc) * (uVignette * 1.6);
        col *= clamp(vig, 0.55, 1.0);
    }

    float bandPos = entryReveal * 1.95;
    float band = exp(-((entryDist - bandPos) * 5.5) * ((entryDist - bandPos) * 5.5));
    col += vec3(0.85, 0.90, 1.0) * band * entryPulse * 0.40;

    float baseAlpha = clamp(uIntensity, 0.0, 1.0);
    float spatialAlpha = mix(0.0, 1.0, revealStrength);
    fragColor = vec4(col, baseAlpha * spatialAlpha);
}
