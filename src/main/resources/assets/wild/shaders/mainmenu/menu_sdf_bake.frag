#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uAtlas;
uniform vec4 uGlyphUv;
uniform vec2 uQuadSize;
uniform float uPadPx;
uniform float uRangePx;
uniform float uMaskRange;

out vec4 FragColor;

float median3(float a, float b, float c) {
    return max(min(a, b), min(max(a, b), c));
}

void main() {
    vec2 cellSize = max(uQuadSize - uPadPx * 2.0, vec2(1.0));
    vec2 cellLocal = vLocal - uPadPx;
    vec2 inner = cellLocal / cellSize;
    vec2 uv = mix(uGlyphUv.xy, uGlyphUv.zw, clamp(inner, 0.0, 1.0));
    vec3 s = texture(uAtlas, uv).rgb;
    float sd = median3(s.r, s.g, s.b) - 0.5;
    vec2 beyond = max(abs(cellLocal - cellSize * 0.5) - cellSize * 0.5, vec2(0.0));
    float outsideCell = length(beyond);
    float distancePx = sd * uRangePx - outsideCell;
    float trust = smoothstep(-0.495, -0.400, sd);
    distancePx = mix(-uMaskRange, distancePx, trust);
    float encoded = clamp(distancePx / max(uMaskRange, 1.0) * 0.5 + 0.5, 0.0, 1.0);
    FragColor = vec4(encoded, encoded, encoded, 1.0);
}
