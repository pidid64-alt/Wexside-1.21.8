#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uMask;
uniform vec2 uMaskSize;
uniform float uMaskRange;
uniform float uTightRadius;
uniform float uWideRadius;

out vec4 FragColor;

const float TAU = 6.28318531;
const int RINGS = 4;
const int TAPS = 12;
const float RING_BIAS = 0.72;
const float EDGE_SOFT = 1.15;

float occupancy(vec2 uv) {
    float d = (texture(uMask, clamp(uv, vec2(0.0), vec2(1.0))).r - 0.5) * 2.0 * uMaskRange;
    return smoothstep(-EDGE_SOFT, EDGE_SOFT, d);
}

float softField(vec2 uv, vec2 texel, float maxRadius, float rot) {
    float acc = 0.0;
    float wsum = 0.0;
    float stepA = TAU / float(TAPS);
    vec2 rotor = vec2(cos(stepA), sin(stepA));
    for (int r = 0; r < RINGS; r++) {
        float t = (float(r) + 0.5) / float(RINGS);
        float radius = maxRadius * t;
        float weight = 1.0 - t * RING_BIAS;
        float angle = rot + float(r) * 0.6180339;
        vec2 dir = vec2(cos(angle), sin(angle));
        float ring = 0.0;
        for (int i = 0; i < TAPS; i++) {
            ring += occupancy(uv + dir * radius * texel);
            dir = vec2(dir.x * rotor.x - dir.y * rotor.y, dir.x * rotor.y + dir.y * rotor.x);
        }
        acc += (ring / float(TAPS)) * weight;
        wsum += weight;
    }
    return acc / max(wsum, 1.0e-4);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

void main() {
    vec2 uv = clamp(vUv, 0.0, 1.0);
    uv = vec2(uv.x, 1.0 - uv.y);
    vec2 texel = 1.0 / max(uMaskSize, vec2(1.0));
    float rot = interleavedGradient(uv * uMaskSize) * TAU;
    float tight = softField(uv, texel, max(uTightRadius, 1.0), rot);
    float wide = softField(uv, texel, max(uWideRadius, 1.0), rot + 1.7);
    FragColor = vec4(tight, wide, 0.0, 1.0);
}
