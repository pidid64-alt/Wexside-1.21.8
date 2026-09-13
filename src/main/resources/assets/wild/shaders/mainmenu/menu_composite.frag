#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uTexture;
uniform vec2 uViewport;
uniform vec2 uTextureSize;
uniform vec2 uParallax;
uniform float uTime;
uniform float uEntry;
uniform float uClickFlash;
uniform float uLightMode;
uniform float uSakura;
uniform float uVernal;
uniform vec2 uSourceScale;
uniform float uHour;

out vec4 FragColor;

const float CA_STRENGTH = 0.0026;
const float CA_LIGHT_STRENGTH = 0.0011;
const float VIGNETTE_INNER = 0.19;
const float VIGNETTE_OUTER = 1.16;
const float VIGNETTE_DEPTH = 0.54;
const float VIGNETTE_LIGHT_DEPTH = 0.055;
const float SATURATION = 1.13;
const vec3 GRADE_GAIN = vec3(0.985, 0.982, 1.052);
const vec3 SHADOW_LIFT = vec3(0.0044, 0.0036, 0.0102);
const float SHADOW_LIFT_KNEE = 0.26;
const float HIGHLIGHT_ROLL = 0.80;

float sq(float x) {
    return x * x;
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = interleavedGradient(px + seed * 137.0);
    r = fract(r + hash12(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

vec2 sourceRegion() {
    return uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
}

vec3 rolloff(vec3 c) {
    float peak = max(c.r, max(c.g, c.b));
    if (peak < 1.0e-5) {
        return c;
    }
    float mapped = peak < HIGHLIGHT_ROLL
            ? peak
            : HIGHLIGHT_ROLL + (1.0 - HIGHLIGHT_ROLL) * (1.0 - exp(-(peak - HIGHLIGHT_ROLL) / (1.0 - HIGHLIGHT_ROLL)));
    return c * (mapped / peak);
}

vec4 sampleScene(vec2 uv) {
    vec2 region = sourceRegion();
    vec2 texel = 1.0 / max(uTextureSize, vec2(1.0));
    vec4 c = texture(uTexture, clamp(uv * region, texel * 0.5, region - texel * 0.5));
    if (c.a > 0.0001 && c.a < 0.999) {
        c.rgb /= c.a;
    }
    return c;
}

vec3 blurSample(vec2 uv, float radius) {
    vec2 px = radius / max(uTextureSize, vec2(1.0));
    vec3 c = sampleScene(uv).rgb * 0.40;
    c += sampleScene(uv + vec2(px.x, 0.0)).rgb * 0.15;
    c += sampleScene(uv - vec2(px.x, 0.0)).rgb * 0.15;
    c += sampleScene(uv + vec2(0.0, px.y)).rgb * 0.15;
    c += sampleScene(uv - vec2(0.0, px.y)).rgb * 0.15;
    return c;
}

vec3 addLight(vec3 base, vec3 light) {
    return base + light * max(vec3(0.0), vec3(1.0) - base);
}

vec3 grade(vec3 color) {
    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    color = mix(vec3(luma), color, SATURATION);
    color *= GRADE_GAIN;
    color += SHADOW_LIFT * sq(1.0 - smoothstep(0.0, SHADOW_LIFT_KNEE, luma));
    return color;
}

void main() {
    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    vec2 centered = uv - 0.5;
    float radial2 = dot(centered, centered);
    vec2 warped = uv + uParallax + centered * radial2 * 0.0014;
    float entry = smoothstep(0.0, 1.0, uEntry);
    float focus = 1.0 - entry;
    float sakura = clamp(uSakura, 0.0, 1.0);
    float vernal = clamp(uVernal, 0.0, 1.0);

    float caAmount = uLightMode > 0.5 ? CA_LIGHT_STRENGTH : CA_STRENGTH;
    vec2 caOffset = centered * radial2 * caAmount;
    vec3 sharp;
    sharp.g = sampleScene(warped).g;
    sharp.r = sampleScene(warped + caOffset).r;
    sharp.b = sampleScene(warped - caOffset).b;

    vec3 soft = blurSample(warped, 9.0 * focus + sakura * 2.2 + vernal * 1.35);
    vec3 color = mix(soft, sharp, mix(entry, entry * 0.85, max(sakura, vernal * 0.72)));

    float hour = mod(uHour, 24.0);
    float dawnW = smoothstep(4.5, 6.5, hour) * (1.0 - smoothstep(8.0, 11.0, hour));
    float dayW = smoothstep(6.0, 9.5, hour) * (1.0 - smoothstep(16.5, 20.0, hour));
    float duskW = smoothstep(16.5, 19.0, hour) * (1.0 - smoothstep(20.0, 23.0, hour));
    float nightW = clamp(1.0 - smoothstep(4.5, 7.5, hour) + smoothstep(20.5, 23.5, hour), 0.0, 1.0);
    float wSum = max(dawnW + dayW + duskW + nightW, 1.0e-4);
    vec3 skyTop = (dawnW * vec3(0.30, 0.22, 0.40)
            + dayW * vec3(0.30, 0.50, 0.78)
            + duskW * vec3(0.20, 0.15, 0.36)
            + nightW * vec3(0.012, 0.018, 0.055)) / wSum;
    vec3 skyBottom = (dawnW * vec3(0.93, 0.55, 0.38)
            + dayW * vec3(0.62, 0.76, 0.88)
            + duskW * vec3(0.85, 0.42, 0.32)
            + nightW * vec3(0.035, 0.05, 0.12)) / wSum;
    vec3 sky = mix(skyBottom, skyTop, clamp(uv.y, 0.0, 1.0));
    float circadian = mix(0.185, 0.150, clamp(uLightMode, 0.0, 1.0));
    color = mix(color, addLight(color, sky * 0.62), circadian * smoothstep(0.0, 0.5, entry));

    float aspect2d = uViewport.x / max(uViewport.y, 1.0);
    float vignette = 1.0 - smoothstep(VIGNETTE_INNER, VIGNETTE_OUTER, length(centered * vec2(aspect2d, 1.0)));
    vignette = vignette * vignette * (3.0 - 2.0 * vignette);
    float grain = hash12(vScreen + fract(uTime) * 8192.0) - 0.5;
    if (uLightMode > 0.5) {
        float vigStr = VIGNETTE_LIGHT_DEPTH + sakura * 0.060 + vernal * 0.024;
        color *= (1.0 - vigStr) + vignette * vigStr;
        color += mix(vec3(1.0, 0.88, 0.80), vec3(1.0, 0.95, 0.60), vernal) * uClickFlash * (0.030 + vernal * 0.020) * smoothstep(0.0, 0.44, entry);
        color += grain * vec3(0.0022);
    } else {
        color = grade(color);
        color *= mix(1.0 - VIGNETTE_DEPTH, 1.0, vignette);
        color += vec3(0.055, 0.058, 0.066) * uClickFlash * smoothstep(0.0, 0.44, entry);
        color += grain * vec3(0.0034);
    }
    color *= smoothstep(0.0, 0.84, uEntry);
    color = rolloff(color);
    color += triangularNoise(vScreen, fract(uTime * 0.37)) * (1.0 / 255.0);
    FragColor = vec4(max(color, vec3(0.0)), 1.0);
}
