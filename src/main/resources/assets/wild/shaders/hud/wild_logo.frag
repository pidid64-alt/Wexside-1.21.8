#version 330 core

in vec2 vScreen;
in vec2 vBox;
in vec2 vDraw;

uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uAlpha;
uniform float uLightMode;

out vec4 fragColor;

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
        c.g <= 0.0031308 ? c.g * 12.92 : 1.055 * pow(c.g, 0.4166666666666667) - 0.055,
        c.b <= 0.0031308 ? c.b * 12.92 : 1.055 * pow(c.b, 0.4166666666666667) - 0.055
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
        -0.0041960863 * l - 0.7034186147 * m + 1.7076147010 * s
    );
}

vec3 oklab_mix(vec3 colA, vec3 colB, float t) {
    vec3 labA = linear_srgb_to_oklab(colA);
    vec3 labB = linear_srgb_to_oklab(colB);
    return oklab_to_linear_srgb(mix(labA, labB, t));
}

vec3 oklab_mix_srgb(vec3 colA, vec3 colB, float t) {
    return linear_to_srgb(oklab_mix(srgb_to_linear(colA), srgb_to_linear(colB), clamp(t, 0.0, 1.0)));
}

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), u.x), mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

float sdRoundBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

void main() {
    vec2 p = vBox;
    vec2 c = p - 0.5;
    float box = sdRoundBox(c, vec2(0.5), 0.205);
    float inner = sdRoundBox(c, vec2(0.395), 0.130);
    float outerMask = 1.0 - smoothstep(-0.004, 0.030, box);
    float innerMask = 1.0 - smoothstep(-0.003, 0.020, inner);
    float shadow = exp(-max(box, 0.0) * 18.0) * 0.34;
    float aura = exp(-length(c * vec2(1.0, 1.1)) * 5.6) * 0.20;
    vec3 accent = oklab_mix_srgb(uAccentBottom, uAccentTop, sat(p.y));
    vec3 base = vec3(0.018, 0.024, 0.030);
    float n = noise(p * 9.0 + vec2(uTime * 0.08, -uTime * 0.05));
    float film = noise(vScreen * 0.42 + uTime * 0.37) * 0.018;
    float caustic = pow(sat(0.5 + 0.5 * sin((p.x * 1.8 - p.y * 1.1 + uTime * 0.28) * 6.28318)), 3.0);
    vec3 glass = base + accent * (0.035 + caustic * 0.045) + vec3(n * 0.018 + film);
    float rimOuter = 1.0 - smoothstep(-0.004, 0.018, abs(box) - 0.006);
    float rimInner = 1.0 - smoothstep(-0.004, 0.018, abs(inner) - 0.003);
    float flow = 0.5 + 0.5 * sin((p.x * 1.6 + p.y * 1.1 - uTime * 0.42) * 6.28318);
    float prism = noise(p * 18.0 + vec2(uTime * 0.24, uTime * 0.11));
    vec3 color = glass * outerMask;
    color += accent * aura;
    color += accent * rimOuter * 0.16;
    color += vec3(1.0) * rimInner * 0.035;
    color += oklab_mix_srgb(uAccentBottom, uAccentTop, prism) * innerMask * (0.055 + flow * 0.035);
    color += vec3(0.82, 0.94, 1.0) * innerMask * pow(aura, 1.4) * 0.10;
    float alpha = max(shadow, outerMask * (0.80 + innerMask * 0.18));
    if (uLightMode > 0.5) {
        vec3 lightBase = vec3(0.992, 0.986, 0.988);
        vec3 lightGlass = oklab_mix_srgb(lightBase, accent, 0.060 + caustic * 0.035);
        color = lightGlass * outerMask;
        color += accent * aura * 0.28;
        color += accent * rimOuter * 0.075;
        color += vec3(1.0) * rimInner * 0.060;
        color += oklab_mix_srgb(vec3(1.0), accent, 0.28) * innerMask * (0.038 + flow * 0.020);
        alpha = max(shadow * 0.22, outerMask * (0.38 + innerMask * 0.10));
    }
    alpha *= sat(uAlpha);
    if (alpha <= 0.001) {
        discard;
    }
    fragColor = vec4(color, alpha);
}
