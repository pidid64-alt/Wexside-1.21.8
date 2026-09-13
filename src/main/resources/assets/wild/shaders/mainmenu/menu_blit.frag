#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uSource;
uniform vec2 uViewport;
uniform vec2 uSourceScale;
uniform vec2 uSourceTexel;

out vec4 FragColor;

void main() {
    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    vec2 region = uSourceScale.x > 0.0 ? uSourceScale : vec2(1.0);
    vec2 texel = uSourceTexel.x > 0.0 ? uSourceTexel : vec2(0.0);
    FragColor = vec4(texture(uSource, clamp(uv * region, texel * 0.5, region - texel * 0.5)).rgb, 1.0);
}
