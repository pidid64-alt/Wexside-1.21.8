#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec4 uButton;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform vec2 uLocalMouse;
uniform vec2 uPointerLocal;
uniform float uPointerValid;
uniform float uRadius;
uniform float uScale;
uniform float uDrive;
uniform float uPress;
uniform float uFlash;
uniform float uTime;
uniform float uLightMode;
uniform float uSteady;

out vec4 FragColor;

const float RIM_TIGHT_PX = 2.2;
const float RIM_MID_PX = 5.5;
const float RIM_WIDE_PX = 13.0;
const float RIM_MID_WEIGHT = 0.34;
const float RIM_WIDE_WEIGHT = 0.13;
const float RIM_GAIN = 0.33;
const float FLASH_GAIN = 0.34;
const float REACH_PX = 300.0;
const float CREST_GAIN = 0.85;
const float LIGHT_MODE_GAIN = 0.42;
const float STEADY_SHAPE = 0.86;

float sq(float x) {
    return x * x;
}

vec3 roundBoxField(vec2 p, vec2 b, float r) {
    r = min(r, min(b.x, b.y));
    vec2 s = vec2(p.x < 0.0 ? -1.0 : 1.0, p.y < 0.0 ? -1.0 : 1.0);
    vec2 q = abs(p) - b + r;
    if (max(q.x, q.y) > 0.0) {
        vec2 m = max(q, vec2(0.0));
        float l = length(m);
        vec2 g = l > 1.0e-5 ? m / l : vec2(0.0, 1.0);
        return vec3(s * g, l - r);
    }
    vec2 g = q.x > q.y ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    return vec3(s * g, max(q.x, q.y) - r);
}

vec3 accentAt(float t) {
    return mix(uAccentTop, uAccentBottom, clamp(t, 0.0, 1.0));
}

void main() {
    vec2 size = max(uButton.zw, vec2(1.0));
    vec2 half_ = size * 0.5;
    vec2 local = vLocal - uButton.xy;
    float scale = max(uScale, 0.001);
    vec2 p = (local - half_) / scale;

    float unit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;
    float radius = min(uRadius, min(half_.x, half_.y));
    vec3 field = roundBoxField(p, half_, radius);
    vec2 nOut = field.xy;
    float dS = field.z * scale;
    float outside = max(dS, 0.0);

    float drive = clamp(uDrive, 0.0, 1.0) * (1.0 - clamp(uPress, 0.0, 1.0) * 0.22);
    float flash = clamp(uFlash, 0.0, 1.0);
    float energy = drive + flash * 0.6;
    if (energy <= 0.004) {
        discard;
    }

    vec2 lightPos = uPointerValid > 0.5 ? uPointerLocal : (uLocalMouse - 0.5) * size;
    vec2 toLight = lightPos - p;
    float dist2 = dot(toLight, toLight);
    float prox = 1.0 / (1.0 + dist2 / sq(REACH_PX));

    float minHalf = min(half_.x, half_.y);
    vec2 edgePoint = p - clamp(field.z, -minHalf, minHalf) * nOut;
    vec2 crestDelta = edgePoint - lightPos;
    float perimeter = 2.0 * (size.x + size.y);
    float crestSigma = max(perimeter * 0.130, 24.0);
    float crest = exp(-dot(crestDelta, crestDelta) / sq(crestSigma));

    float tight = exp(-sq(outside / max(RIM_TIGHT_PX * unit, 1.1)));
    float mid = exp(-sq(outside / max(RIM_MID_PX * unit, 2.4)));
    float wide = exp(-sq(outside / max(RIM_WIDE_PX * unit, 6.0)));
    float rim = tight + mid * RIM_MID_WEIGHT + wide * RIM_WIDE_WEIGHT;

    float vertical = clamp((edgePoint.y + half_.y) / max(size.y, 1.0), 0.0, 1.0);
    vec3 accent = accentAt(vertical);
    vec3 color = mix(accent, vec3(0.88, 0.92, 1.0), 0.10);

    float steady = clamp(uSteady, 0.0, 1.0);
    float shaped = mix(0.34 + CREST_GAIN * crest, STEADY_SHAPE, steady);
    float reach = mix(0.42 + 0.58 * prox, 1.0, steady);
    float gain = RIM_GAIN * drive * shaped * reach
            + FLASH_GAIN * flash * (0.60 + 0.40 * prox);
    if (uLightMode > 0.5) {
        gain *= LIGHT_MODE_GAIN;
    }
    vec3 light = color * rim * gain;
    if (dot(light, vec3(0.333)) <= 0.0015) {
        discard;
    }
    FragColor = vec4(light, 0.0);
}
