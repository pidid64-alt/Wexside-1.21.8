#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uDrift;
uniform float uEntry;
uniform float uPulse;
uniform float uLightMode;
uniform vec2 uLocalMouse;

out vec4 FragColor;

const vec3 LAMP_SILVER = vec3(0.86, 0.90, 1.00);
const float AURA_PEAK = 0.050;
const float AURA_BREATHE_RATE = 0.55;
const float AURA_BREATHE_DEPTH = 0.10;

float sq(float x) {
    return x * x;
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

void main() {
    float entry = smoothstep(0.0, 1.0, uEntry);
    if (entry <= 0.002) {
        discard;
    }
    vec2 c = (vUv - 0.5) * 2.0;
    float d = length(c);
    if (d > 1.0) {
        discard;
    }
    float core = exp(-sq(d * 2.1));
    float haze = exp(-sq(d * 1.15)) * 0.45;
    float breathe = 1.0 + AURA_BREATHE_DEPTH * sin(uTime * AURA_BREATHE_RATE);
    float pulse = 1.0 + clamp(uPulse, 0.0, 1.0) * 0.06;
    vec3 accent = mix(uAccentBottom, uAccentTop, 0.5);
    vec3 color = mix(LAMP_SILVER, accent, 0.20);
    float gain = AURA_PEAK * entry * breathe * pulse;
    if (uLightMode > 0.5) {
        gain *= 0.55;
        color = mix(color, vec3(1.0), 0.35);
    }
    vec3 light = color * (core + haze) * gain;
    light += interleavedGradient(vScreen) * (1.0 / 255.0);
    FragColor = vec4(light, light.g);
}
