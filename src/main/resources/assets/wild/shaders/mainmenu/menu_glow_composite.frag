#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uGlow;
uniform sampler2D uBloom;
uniform vec2 uViewport;
uniform vec2 uGlowTexel;
uniform vec2 uBloomTexel;
uniform vec2 uSourceScale;
uniform float uBloomAmount;
uniform float uLightMode;

out vec4 FragColor;

const float GLOW_CORE_WEIGHT = 0.88;
const float GLOW_BLOOM_WEIGHT = 0.86;
const float GLOW_EXPOSURE = 1.02;
const float GLOW_LIGHT_EXPOSURE = 0.76;
const float VIGNETTE_STRENGTH = 1.0;

void main() {
    vec2 region = uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0)) * region;
    vec4 g = texture(uGlow, clamp(uv, uGlowTexel * 0.5, region - uGlowTexel * 0.5));
    vec3 b = texture(uBloom, clamp(uv, uBloomTexel * 0.5, region - uBloomTexel * 0.5)).rgb;
    vec3 raw = g.rgb * GLOW_CORE_WEIGHT + b * (GLOW_BLOOM_WEIGHT * clamp(uBloomAmount, 0.0, 1.0));
    float exposure = uLightMode > 0.5 ? GLOW_LIGHT_EXPOSURE : GLOW_EXPOSURE;
    vec3 light = vec3(1.0) - exp(-max(raw, vec3(0.0)) * exposure);
    float darken = clamp(g.a * VIGNETTE_STRENGTH, 0.0, 1.0);
    if (max(max(light.r, light.g), light.b) <= 0.0015 && darken <= 0.0015) {
        discard;
    }
    FragColor = vec4(light, darken);
}
