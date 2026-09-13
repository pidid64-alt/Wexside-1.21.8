#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec2 uResolution;
uniform vec2 uMouse;
uniform vec2 uMouseVelocity;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uAlpha;
uniform float uLightMode;

out vec4 FragColor;

const float TAU = 6.28318530718;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p) {
    return vec2(hash12(p), hash12(p + vec2(37.17, 91.71)));
}

vec3 screenBlend(vec3 base, vec3 light, float amount) {
    vec3 s = 1.0 - (1.0 - base) * (1.0 - light);
    return mix(base, s, sat(amount));
}

mat2 rot2(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, -s, s, c);
}

vec3 auroraRibbon(vec2 uv, float aspect, float time, float baseY, float amp, float freq, float drift, float depth, float gain, float seed) {
    float x = uv.x * aspect;
    float yc = baseY
        + sin(x * freq + time * drift + seed) * amp
        + sin(x * freq * 2.31 - time * drift * 0.57 + seed * 2.7) * amp * 0.42
        + sin(x * freq * 0.47 + time * drift * 0.23 + seed * 5.9) * amp * 0.66;
    float dy = uv.y - yc;
    float crest = exp(-dy * dy * 900.0);
    float tail = sat(dy / depth);
    float curtain = smoothstep(-0.012, 0.02, dy) * (1.0 - tail) * (1.0 - tail);
    float rays = 0.58 + 0.42 * sin(x * 24.0 + time * 0.50 + sin(x * 8.7 - time * 0.19 + seed) * 1.9);
    float raysFine = 0.80 + 0.20 * sin(x * 61.0 - time * 0.34 + seed * 7.7);
    float band = crest * 1.05 + curtain * rays * raysFine * 0.85;

    vec3 ice = vec3(0.659, 0.925, 1.0);
    vec3 mint = vec3(0.498, 0.969, 0.878);
    vec3 deep = vec3(0.243, 0.490, 1.0);
    vec3 col = mix(mint, ice, sat(crest));
    col = mix(deep, col, 1.0 - tail * 0.85);
    col = screenBlend(col, uAccentTop, 0.08);
    return col * band * gain;
}

vec4 crystalLayer(vec2 uv, float aspect, float time, float density, float fallSpeed, float size, float spawnRate, float spinSpeed, float phase) {
    vec2 moving;
    moving.y = uv.y - time * fallSpeed;
    moving.x = uv.x + sin(time * (0.20 + phase * 0.13) + uv.y * 2.4 + phase * TAU) * 0.014;
    vec2 scaled = vec2(moving.x * aspect, moving.y) * density + vec2(phase * 47.0, phase * 31.0);
    vec2 cell = floor(scaled);
    vec2 local = fract(scaled) - 0.5;

    float r1 = hash12(cell + phase * 113.0);
    float r2 = hash12(cell + phase * 113.0 + vec2(31.41, 27.18));
    float r3 = hash12(cell + phase * 113.0 + vec2(71.31, 13.37));
    float alive = step(1.0 - spawnRate, r1);

    local -= (vec2(r2, r3) - 0.5) * 0.46;
    local.x += sin(time * (0.5 + r3 * 0.8) + r2 * TAU) * 0.055;
    local = rot2(r2 * TAU + time * spinSpeed * (r3 - 0.5)) * local;

    float rad = size * (0.62 + 0.76 * r3);
    vec2 p = local / max(rad, 0.0001);
    float arm0 = max(0.0, 1.0 - abs(p.y) * 9.0) * max(0.0, 1.0 - abs(p.x) * 1.05);
    float arm1 = max(0.0, 1.0 - abs(dot(p, vec2(-0.8660254, 0.5))) * 9.0) * max(0.0, 1.0 - abs(dot(p, vec2(0.5, 0.8660254))) * 1.05);
    float arm2 = max(0.0, 1.0 - abs(dot(p, vec2(0.8660254, 0.5))) * 9.0) * max(0.0, 1.0 - abs(dot(p, vec2(-0.5, 0.8660254))) * 1.05);
    float core = 1.0 - smoothstep(0.0, 0.22, length(p));
    float star = sat(arm0 + arm1 + arm2) * 0.85 + core * 0.90;

    float twinkle = 0.55 + 0.45 * sin(time * (1.3 + r2 * 2.8) + r1 * TAU);
    vec3 col = mix(vec3(0.78, 0.92, 1.0), vec3(0.95, 0.99, 1.0), r3);
    col = screenBlend(col, uAccentTop, 0.10);
    return vec4(col, star * alive * twinkle);
}

vec3 starfield(vec2 uv, float aspect, float time, float scale, float threshold, float gain, float seed) {
    vec2 p = vec2(uv.x * aspect, uv.y) * scale + vec2(seed * 13.7, seed * 5.8);
    vec2 cell = floor(p);
    vec2 rnd = hash22(cell + seed);
    float lum = hash12(cell + vec2(seed * 9.3, seed * 2.1));
    float alive = step(threshold, lum);
    vec2 pos = vec2(0.5) + (rnd - 0.5) * 0.72;
    float d = length(p - cell - pos);
    float core = 1.0 - smoothstep(0.0, 0.055 + 0.045 * rnd.y, d);
    float twinkle = 0.55 + 0.45 * sin(time * (0.6 + rnd.x * 1.9) + rnd.y * TAU);
    vec3 tint = mix(vec3(0.72, 0.85, 1.0), vec3(1.0), rnd.y * 0.6);
    return tint * core * core * alive * twinkle * gain;
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time = uTime;
    vec2 mouse = clamp(uMouse, vec2(0.0), vec2(1.0));
    vec2 parallax = (mouse - 0.5) * vec2(aspect, 1.0);

    vec3 top = vec3(0.008, 0.014, 0.040);
    vec3 bottom = vec3(0.028, 0.052, 0.120);
    vec3 bg = mix(top, bottom, smoothstep(0.0, 1.0, uv.y));
    bg = mix(bg, vec3(0.243, 0.490, 1.0) * 0.16, smoothstep(0.72, 1.05, uv.y) * 0.35);
    bg = screenBlend(bg, uAccentBottom * 0.5, 0.03);

    vec3 stars = starfield(uv + parallax * 0.0014, aspect, time, 26.0, 0.92, 0.55, 3.1)
               + starfield(uv + parallax * 0.0024, aspect, time, 44.0, 0.94, 0.35, 8.7);
    stars *= smoothstep(1.0, 0.45, uv.y);
    vec3 color = screenBlend(bg, stars, 0.80);

    vec3 a1 = auroraRibbon(uv + parallax * 0.0030, aspect, time, 0.26, 0.045, 2.1, 0.16, 0.30, 1.0, 1.3);
    vec3 a2 = auroraRibbon(uv + parallax * 0.0018, aspect, time, 0.44, 0.060, 1.4, 0.11, 0.38, 0.52, 7.9);
    color = screenBlend(color, a2, 0.50);
    color = screenBlend(color, a1, 0.62);

    vec4 c1 = crystalLayer(uv + parallax * 0.0020, aspect, time, 6.4, 0.030, 0.070, 0.14, 0.10, 0.31);
    vec4 c2 = crystalLayer(uv + parallax * 0.0044, aspect, time, 4.2, 0.052, 0.100, 0.18, 0.16, 0.77);
    color = screenBlend(color, c1.rgb * sat(c1.a), 0.60);
    color = screenBlend(color, c2.rgb * sat(c2.a), 0.85);

    vec2 centered = (uv - 0.5) * vec2(aspect, 1.0);
    float vig = 1.0 - smoothstep(0.36, 1.24, length(centered));
    color *= 0.76 + vig * 0.24;
    color += (hash12(vScreen + fract(time) * 8192.0) - 0.5) * vec3(0.0016);

    float glintEnergy = sat(c1.a) + sat(c2.a);
    float alpha = clamp((0.80 + sat(glintEnergy) * 0.05) * sat(uAlpha), 0.0, 0.85);
    FragColor = vec4(clamp(color, 0.0, 1.0), alpha);
}
