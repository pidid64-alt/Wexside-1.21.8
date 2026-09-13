#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uBackground;
uniform vec2 uViewport;
uniform vec2 uTextureSize;
uniform float uTime;
uniform float uAlpha;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uLightMode;

out vec4 FragColor;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

vec3 back(vec2 uv) {
    return texture(uBackground, clamp(uv, vec2(0.0), vec2(1.0))).rgb;
}

vec3 blur(vec2 uv, float radius) {
    vec2 px = radius / max(uTextureSize, vec2(1.0));
    vec3 c = back(uv) * 0.34;
    c += back(uv + vec2(px.x, 0.0)) * 0.13;
    c += back(uv - vec2(px.x, 0.0)) * 0.13;
    c += back(uv + vec2(0.0, px.y)) * 0.13;
    c += back(uv - vec2(0.0, px.y)) * 0.13;
    c += back(uv + px * vec2(0.72, 0.72)) * 0.035;
    c += back(uv + px * vec2(-0.72, 0.72)) * 0.035;
    c += back(uv + px * vec2(0.72, -0.72)) * 0.035;
    c += back(uv + px * vec2(-0.72, -0.72)) * 0.035;
    return c;
}

void main() {
    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    vec2 centered = uv - 0.5;
    float aspect = uViewport.x / max(uViewport.y, 1.0);
    float vignette = smoothstep(1.22, 0.18, length(centered * vec2(aspect, 1.0)));
    float grain = hash12(vScreen * 0.74 + vec2(uTime * 71.0, -uTime * 43.0)) - 0.5;
    float aurora = 0.5 + 0.5 * sin((uv.x * 2.2 + uv.y * 1.4) * 3.14159265 + uTime * 0.36);
    vec3 source = blur(uv + centered * dot(centered, centered) * 0.004, 11.0);
    vec3 accent = mix(uAccentBottom, uAccentTop, clamp(uv.y + aurora * 0.18, 0.0, 1.0));
    vec3 darkWash = mix(vec3(0.010, 0.013, 0.018), source * 0.28 + accent * 0.035, 0.56);
    darkWash += accent * (0.018 + aurora * 0.014);
    darkWash += vec3(grain * 0.006);
    darkWash *= 0.82 + vignette * 0.18;
    vec3 lightWash = mix(vec3(0.92, 0.95, 0.98), source * 0.18 + accent * 0.028, 0.20);
    lightWash += vec3(grain * 0.003);
    lightWash *= 0.94 + vignette * 0.06;
    vec3 color = uLightMode > 0.5 ? lightWash : darkWash;
    float alpha = (uLightMode > 0.5 ? 0.28 : 0.36) * clamp(uAlpha, 0.0, 1.0);
    FragColor = vec4(color, alpha);
}
