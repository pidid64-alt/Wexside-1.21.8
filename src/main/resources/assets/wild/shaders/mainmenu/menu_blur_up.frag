#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uSource;
uniform vec2 uSourceTexel;
uniform vec2 uSourceScale;
uniform vec2 uViewport;

out vec4 FragColor;

vec3 tap(vec2 uv, vec2 region) {
    return texture(uSource, clamp(uv, uSourceTexel * 0.5, region - uSourceTexel * 0.5)).rgb;
}

void main() {
    vec2 region = uSourceScale.x > 0.0 ? uSourceScale : vec2(1.0);
    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0)) * region;
    vec2 h = uSourceTexel * 0.5;
    vec3 c = tap(uv + vec2(-h.x * 2.0, 0.0), region);
    c += tap(uv + vec2(-h.x, h.y), region) * 2.0;
    c += tap(uv + vec2(0.0, h.y * 2.0), region);
    c += tap(uv + vec2(h.x, h.y), region) * 2.0;
    c += tap(uv + vec2(h.x * 2.0, 0.0), region);
    c += tap(uv + vec2(h.x, -h.y), region) * 2.0;
    c += tap(uv + vec2(0.0, -h.y * 2.0), region);
    c += tap(uv + vec2(-h.x, -h.y), region) * 2.0;
    FragColor = vec4(c * 0.0833333333, 1.0);
}
