#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uResolution;
uniform vec2 uMouse;
uniform vec2 uParallax;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uEntry;
uniform float uLightMode;
uniform vec4 uTrail[14];

out vec4 FragColor;

const int MOTE_COUNT = 34;
const float MOTE_RISE_PX = 8.0;
const float MOTE_MARGIN_PX = 32.0;
const float FOCUS_DEPTH = 0.58;
const float COC_GAIN = 5.6;
const float MOTE_GAIN = 0.052;
const float MOUSE_VEIL_GAIN = 0.030;
const float TRAIL_GAIN = 0.046;

float sq(float x) {
    return x * x;
}

vec3 srgb_to_linear(vec3 c) {
    return vec3(
        c.r <= 0.04045 ? c.r / 12.92 : pow((c.r + 0.055) / 1.055, 2.4),
        c.g <= 0.04045 ? c.g / 12.92 : pow((c.g + 0.055) / 1.055, 2.4),
        c.b <= 0.04045 ? c.b / 12.92 : pow((c.b + 0.055) / 1.055, 2.4)
    );
}

vec3 linear_to_srgb(vec3 c) {
    c = clamp(c, 0.0, 1.0);
    return vec3(
        c.r <= 0.0031308 ? c.r * 12.92 : 1.055 * pow(c.r, 0.4166666666666667) - 0.055,
        c.g <= 0.0031308 ? c.g * 12.92 : 1.055 * pow(c.g, 0.4166666667) - 0.055,
        c.b <= 0.0031308 ? c.b * 12.92 : 1.055 * pow(c.b, 0.4166666667) - 0.055
    );
}

vec3 linear_srgb_to_oklab(vec3 c) {
    float l = 0.4122214708 * c.r + 0.5363325363 * c.g + 0.0514459929 * c.b;
    float m = 0.2119034982 * c.r + 0.6806995451 * c.g + 0.1073969566 * c.b;
    float s = 0.0883024619 * c.r + 0.2817188376 * c.g + 0.6299787005 * c.b;
    float l_ = sign(l) * pow(abs(l), 0.333333333333);
    float m_ = sign(m) * pow(abs(m), 0.333333333333);
    float s_ = sign(s) * pow(abs(s), 0.333333333333);
    return vec3(
        0.2104542553 * l_ + 0.7936177850 * m_ - 0.0040720468 * s_,
        1.9779984951 * l_ - 2.4285922050 * m_ + 0.4505937099 * s_,
        0.0259040371 * l_ + 0.7827717662 * m_ - 0.8086757660 * s_
    );
}

vec3 oklab_to_linear_srgb(vec3 c) {
    float l_ = c.x + 0.3963377774 * c.y + 0.2158037573 * c.z;
    float m_ = c.x - 0.1055613458 * c.y - 0.0638541728 * c.z;
    float s_ = c.x - 0.0894841775 * c.y - 1.2914855480 * c.z;
    float l = l_ * l_ * l_;
    float m = m_ * m_ * m_;
    float s = s_ * s_ * s_;
    return vec3(
        4.0767416621 * l - 3.3077115913 * m + 0.2309699292 * s,
        -1.2684380046 * l + 2.6097574011 * m - 0.3413193965 * s,
        -0.0041960863 * l - 0.7057717662 * m + 1.7076147010 * s
    );
}

vec3 oklab_mix_srgb(vec3 colA, vec3 colB, float t) {
    return linear_to_srgb(oklab_to_linear_srgb(mix(linear_srgb_to_oklab(srgb_to_linear(colA)),
                                                   linear_srgb_to_oklab(srgb_to_linear(colB)),
                                                   clamp(t, 0.0, 1.0))));
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p) {
    return vec2(hash12(p), hash12(p + vec2(13.71)));
}

void main() {
    vec2 px = vScreen + uParallax * 0.35;
    vec2 mouse = uMouse * uResolution;
    float unit = max(min(uResolution.x, uResolution.y), 1.0) / 1080.0;
    vec3 light = vec3(0.0);

    vec3 tintFar = oklab_mix_srgb(uAccentTop, vec3(0.90, 0.93, 1.0), 0.72);
    vec3 tintNear = oklab_mix_srgb(uAccentBottom, vec3(0.96, 0.96, 1.0), 0.44);
    vec3 tintTrail = oklab_mix_srgb(uAccentTop, uAccentBottom, 0.45);
    vec3 tintCursor = oklab_mix_srgb(uAccentBottom, vec3(1.0), 0.20);

    for (int i = 0; i < MOTE_COUNT; i++) {
        float fi = float(i);
        vec2 seed = vec2(fi * 7.31, fi * 3.17);
        vec2 h = hash22(seed);
        float depth = hash12(seed + 5.3);
        float scaleD = mix(0.34, 1.0, depth);
        float spanY = uResolution.y + MOTE_MARGIN_PX * 2.0;
        float rise = uTime * MOTE_RISE_PX * unit * (0.28 + 0.72 * scaleD);
        float posX = h.x * uResolution.x
                + sin(uTime * (0.0431 + 0.0317 * h.y) + fi * 1.73) * 20.0 * unit * scaleD
                + uParallax.x * scaleD * 0.65;
        float posY = mod(h.y * spanY - rise + MOTE_MARGIN_PX, spanY) - MOTE_MARGIN_PX;
        posY += uParallax.y * scaleD * 0.65;

        vec2 delta = px - vec2(posX, posY);
        float dist = length(delta);
        float coc = abs(depth - FOCUS_DEPTH) * COC_GAIN;
        float core = mix(0.85, 2.10, scaleD) * unit;
        float radius = core * (1.0 + coc);
        if (dist > radius * 2.6) {
            continue;
        }
        float sharp = exp(-sq(dist / max(core * 0.92, 0.55)));
        float disc = (1.0 - smoothstep(radius * 0.50, radius, dist))
                * (0.62 + 0.38 * smoothstep(radius, radius * 0.60, dist));
        float shape = mix(sharp, disc, clamp(coc, 0.0, 1.0));
        float energy = 1.0 / (1.0 + coc * coc * 1.55);
        float breathe = 0.70 + 0.30 * sin(uTime * (0.2131 + 0.1709 * h.x) + fi * 2.37);
        float veil = shape * energy * breathe * (0.32 + 0.68 * scaleD);
        light += mix(tintFar, tintNear, depth) * veil * MOTE_GAIN;
    }

    float md = length(vScreen - mouse);
    float mouseVeil = exp(-sq(md / (150.0 * unit)));
    light += tintCursor * mouseVeil * MOUSE_VEIL_GAIN;

    for (int i = 0; i < 14; i++) {
        vec4 tr = uTrail[i];
        float live = tr.w * (1.0 - smoothstep(0.0, 3.2, tr.z));
        if (live <= 0.001) {
            continue;
        }
        vec2 pos = tr.xy * uResolution;
        float d = length(vScreen - pos);
        float veil = exp(-sq(d / (128.0 * unit))) * live;
        light += tintTrail * veil * TRAIL_GAIN;
    }

    light *= smoothstep(0.0, 0.9, uEntry);

    if (uLightMode > 0.5) {
        FragColor = vec4(light * 0.62, clamp(dot(light, vec3(0.333)) * 1.4, 0.0, 0.10));
        return;
    }

    float peak = max(max(light.r, light.g), light.b);
    if (peak <= 0.0015) {
        discard;
    }
    float a = clamp(peak, 0.004, 1.0);
    FragColor = vec4(light / a, a);
}
