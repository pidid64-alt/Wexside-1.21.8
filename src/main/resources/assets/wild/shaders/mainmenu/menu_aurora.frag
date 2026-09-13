#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uResolution;
uniform vec2 uMouse;
uniform vec2 uMouseVelocity;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uActivity;
uniform float uLightMode;

out vec4 FragColor;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm3(vec2 p) {
    float v = vnoise(p) * 0.56;
    v += vnoise(p * 2.03 + vec2(2.7, 1.9)) * 0.29;
    v += vnoise(p * 4.11 + vec2(5.1, 8.3)) * 0.15;
    return v;
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0) + hash12(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

float pool(vec2 p, vec2 c, vec2 radii, float rot) {
    float s = sin(rot);
    float co = cos(rot);
    vec2 q = p - c;
    q = vec2(q.x * co - q.y * s, q.x * s + q.y * co) / max(radii, vec2(1.0e-4));
    return exp(-dot(q, q));
}

void main() {
    vec2 uv = vLocal / max(uResolution, vec2(1.0));
    float aspect = uResolution.x / max(uResolution.y, 1.0);
    vec2 p = (uv - 0.5) * vec2(aspect, 1.0);
    float t = uTime;

    float w = fbm3(p * 0.85 + vec2(t * 0.0091, -t * 0.0067));
    vec2 warped = p + (vec2(w, fbm3(p * 0.85 + 13.7 - vec2(t * 0.0074, t * 0.0055))) - 0.5) * 0.185;

    vec3 violet = vec3(0.44, 0.33, 0.98);
    vec3 magenta = vec3(0.92, 0.34, 0.74);
    vec3 azure = vec3(0.20, 0.48, 0.96);
    vec3 teal = vec3(0.24, 0.76, 0.82);
    vec3 ember = vec3(0.98, 0.55, 0.42);

    float p1 = pool(warped, vec2(-0.30 * aspect + 0.055 * sin(t * 0.0417), -0.26 + 0.035 * cos(t * 0.0331)), vec2(0.58, 0.38), 0.42);
    float p2 = pool(warped, vec2(0.36 * aspect + 0.045 * cos(t * 0.0289), 0.20 + 0.040 * sin(t * 0.0362)), vec2(0.50, 0.32), -0.28);
    float p3 = pool(warped, vec2(0.10 * aspect + 0.070 * sin(t * 0.0233), -0.44 + 0.030 * cos(t * 0.0451)), vec2(0.66, 0.24), 0.12);
    float p4 = pool(warped, vec2(-0.46 * aspect, 0.44 + 0.030 * sin(t * 0.0197)), vec2(0.38, 0.26), -0.55);
    float p5 = pool(warped, vec2(0.52 * aspect, -0.40), vec2(0.34, 0.30), 0.30);

    vec3 ground = mix(vec3(0.0295, 0.0268, 0.0480), vec3(0.0175, 0.0162, 0.0300), smoothstep(0.0, 1.0, uv.y));

    vec3 c = ground;
    vec3 head = max(vec3(0.0), vec3(0.985) - c);
    c += min(violet * p1 * 0.230, head);
    head = max(vec3(0.0), vec3(0.985) - c);
    c += min(magenta * p2 * 0.150, head);
    head = max(vec3(0.0), vec3(0.985) - c);
    c += min(azure * p3 * 0.128, head);
    head = max(vec3(0.0), vec3(0.985) - c);
    c += min(teal * p4 * 0.062, head);
    head = max(vec3(0.0), vec3(0.985) - c);
    c += min(ember * p5 * 0.048, head);

    float sheen = smoothstep(0.42, 0.95, fbm3(vec2(p.x * 0.52, p.y * 2.10) + vec2(t * 0.0126, -t * 0.0048)));
    head = max(vec3(0.0), vec3(0.985) - c);
    c += min(vec3(0.70, 0.74, 0.94) * sheen * 0.070 * (0.30 + p1 + p2), head);

    vec2 dm = p - (uMouse - 0.5) * vec2(aspect, 1.0);
    float torch = exp(-dot(dm, dm) * 4.6);
    head = max(vec3(0.0), vec3(0.985) - c);
    c += min(mix(magenta, violet, 0.45) * torch * 0.075 * (0.30 + sheen), head);

    float grain = fbm3(p * 9.4 + vec2(0.0, -t * 0.0074));
    c *= 0.968 + 0.032 * grain;

    if (uLightMode > 0.5) {
        vec3 porcelain = vec3(0.912, 0.906, 0.936);
        porcelain -= vec3(0.055, 0.052, 0.042) * smoothstep(0.30, 1.24, length(p)) * 0.6;
        porcelain = mix(porcelain, mix(porcelain, violet, 0.28), p1 * 0.5 + p3 * 0.3);
        porcelain = mix(porcelain, mix(porcelain, magenta, 0.24), p2 * 0.45);
        c = porcelain;
    }

    c += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    FragColor = vec4(c, 1.0);
}
