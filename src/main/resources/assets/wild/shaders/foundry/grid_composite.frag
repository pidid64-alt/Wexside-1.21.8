#version 330 core

in vec2 vUv;

uniform sampler2D uTexture;
uniform vec2 uResolution;
uniform float uAlpha;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

void main() {
    vec2 uv = vUv;
    vec4 src = texture(uTexture, uv);
    vec2 p = uv - 0.5;
    p.x *= uResolution.x / max(uResolution.y, 1.0);
    float vignette = sat(1.0 - dot(p, p) * 0.46);
    fragColor = vec4(src.rgb * (0.86 + vignette * 0.14), src.a * sat(uAlpha));
}
