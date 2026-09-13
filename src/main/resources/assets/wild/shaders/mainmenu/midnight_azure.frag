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
uniform float uAlpha;
uniform float uLightMode;
uniform vec4 uTrail[14];

out vec4 FragColor;

const float TAU = 6.28318530718;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash21(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.11369, 0.13787));
    p3 += dot(p3, p3.yzx + 19.19);
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p) {
    return vec2(hash21(p), hash21(p + vec2(37.17, 91.71)));
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.52;
    mat2 m = mat2(0.82, -0.57, 0.57, 0.82);
    for (int i = 0; i < 4; i++) {
        v += noise(p) * a;
        p = m * p * 2.03 + vec2(7.13, 3.91);
        a *= 0.48;
    }
    return v;
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0) + hash21(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

vec3 screenBlend(vec3 base, vec3 light, float amount) {
    vec3 s = 1.0 - (1.0 - base) * (1.0 - light);
    return mix(base, s, sat(amount));
}

vec2 rotate2(vec2 p, float s, float c) {
    return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
}

float starFlare(vec2 p, float mass, float flareLength, float flareThickness) {
    float h = max(0.0, 1.0 - abs(p.x * flareLength)) * max(0.0, 1.0 - abs(p.y * flareThickness));
    float v = max(0.0, 1.0 - abs(p.y * flareLength)) * max(0.0, 1.0 - abs(p.x * flareThickness));
    return (h + v) * mass;
}

vec3 opticalStarLayer(vec2 uv, float aspect, float time, float scale, vec2 speed, float alpha, float softness, float flarePower, float spawnEdge, float seed) {
    vec2 p = vec2(uv.x * aspect, uv.y) * scale + time * speed + vec2(seed * 13.71, seed * 5.83);
    vec2 baseCell = floor(p);
    float angle = time * (0.020 + seed * 0.004) + seed * 1.618;
    float s = sin(angle);
    float c = cos(angle);
    vec3 acc = vec3(0.0);
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 cell = baseCell + vec2(float(x), float(y));
            vec2 rnd = hash22(cell + seed);
            float starSeed = hash21(cell + vec2(seed * 9.31, seed * 2.17));
            float alive = smoothstep(spawnEdge, 1.0, starSeed);
            vec2 starPos = (rnd - 0.5) * 0.54;
            vec2 local = p - cell - vec2(0.5) - starPos;
            float d = length(local);
            float mass = smoothstep(0.72, 1.0, rnd.x) * alive;
            float coreRadius = mix(0.006, 0.030, mass) * (0.78 + 0.22 * rnd.y);
            float core = 1.0 - smoothstep(coreRadius * 0.36, coreRadius, d);
            float glow = 0.015 / max(d + softness, 0.0025);
            glow *= alive * (0.36 + 0.64 * mass);
            vec2 flareUv = rotate2(local, s, c);
            float flare = starFlare(flareUv, mass, mix(10.0, 4.2, mass), mix(190.0, 92.0, mass)) * flarePower;
            float twinkle = 0.68 + 0.32 * sin(time * (0.80 + rnd.y * 2.40) + rnd.x * TAU);
            vec3 tint = mix(vec3(0.58, 0.78, 1.0), vec3(1.0), mass * 0.72);
            tint = screenBlend(tint, mix(uAccentBottom, uAccentTop, rnd.y), 0.12 + 0.16 * mass);
            float energy = (glow * 0.36 + core * (1.2 + mass * 1.8) + flare) * alpha * twinkle;
            acc += tint * energy;
        }
    }
    return acc;
}

float bokehDust(vec2 uv, float aspect, float time, float scale, float speed, float alpha, float seed) {
    vec2 p = vec2(uv.x * aspect, uv.y) * scale + vec2(seed * 8.1, time * speed + seed);
    vec2 baseCell = floor(p);
    float acc = 0.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 cell = baseCell + vec2(float(x), float(y));
            vec2 rnd = hash22(cell + seed);
            float alive = smoothstep(0.76, 1.0, rnd.x);
            vec2 c = (rnd - 0.5) * 0.58;
            c.x += sin(time * (0.18 + rnd.y * 0.22) + rnd.x * TAU) * 0.12;
            vec2 d = p - cell - vec2(0.5) - c;
            float r = mix(0.065, 0.165, rnd.y);
            float core = 1.0 - smoothstep(r * 0.12, r, length(d));
            float halo = 0.010 / max(length(d) + 0.040, 0.010);
            acc += alive * (core * 0.26 + halo * 0.16) * alpha;
        }
    }
    return acc;
}

vec3 nebulaField(vec2 uv, float aspect, float time) {
    vec2 p = (uv - 0.5) * vec2(aspect, 1.0);
    float n0 = fbm(p * vec2(0.58, 0.42) + vec2(time * 0.006, -time * 0.004));
    float n1 = fbm(p * vec2(1.14, 0.82) + vec2(-time * 0.004, time * 0.007) + n0 * 0.22);
    float n2 = fbm(p * vec2(2.10, 1.35) + vec2(time * 0.003, time * 0.002));
    float density = smoothstep(0.34, 0.92, n0 * 0.58 + n1 * 0.34 + n2 * 0.08);
    float high = smoothstep(0.62, 0.96, n1 * 0.74 + n2 * 0.26);
    vec3 space = vec3(3.0, 7.0, 18.0) / 255.0;
    vec3 sapphire = vec3(0.0, 68.0, 255.0) / 255.0;
    vec3 cyan = vec3(0.0, 240.0, 255.0) / 255.0;
    vec3 gas = mix(sapphire, cyan, high * 0.46);
    return (gas * density * (0.22 + high * 0.08) + space * 0.04) * 0.30;
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv0 = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    vec2 centered = (uv0 - 0.5) * vec2(aspect, 1.0);
    vec2 velocity = vec2(uMouseVelocity.x * aspect, uMouseVelocity.y);
    float speed = sat(length(velocity));
    float lens = dot(centered, centered);
    vec2 uv = uv0 + (uv0 - 0.5) * lens * 0.018 + velocity * 0.004 * speed;
    vec2 mouse = clamp(uMouse, vec2(0.0), vec2(1.0));
    float time = uTime;

    vec3 space = vec3(3.0, 7.0, 18.0) / 255.0;
    vec3 color = space;
    color += nebulaField(uv, aspect, time);

    vec2 parallax = (mouse - 0.5) * vec2(aspect, 1.0);
    vec3 farStars = opticalStarLayer(uv + parallax * 0.0018, aspect, time, 4.0, vec2(0.006, -0.004), 0.26, 0.030, 0.018, 0.50, 1.7);
    vec3 midStars = opticalStarLayer(uv + parallax * 0.0032, aspect, time, 8.0, vec2(-0.012, 0.008), 0.46, 0.016, 0.040, 0.66, 5.3);
    vec3 nearStars = opticalStarLayer(uv + parallax * 0.0055, aspect, time, 12.0, vec2(0.020, 0.014), 1.00, 0.004, 0.090, 0.78, 9.9);
    color = screenBlend(color, farStars, 0.42);
    color = screenBlend(color, midStars, 0.66);
    color = screenBlend(color, nearStars, 0.92);

    float dust = bokehDust(uv + vec2(0.0, time * 0.004), aspect, time, 5.2, 0.038, 0.38, 12.4);
    dust += bokehDust(uv + vec2(0.017, -0.023), aspect, time, 9.0, 0.061, 0.23, 19.7);
    vec3 dustColor = mix(uAccentTop, vec3(0.82, 0.96, 1.0), 0.18);
    color = screenBlend(color, dustColor, dust * 0.30);

    vec2 m = (mouse - 0.5) * vec2(aspect, 1.0);
    float cursor = exp(-dot(centered - m, centered - m) * mix(10.5, 6.0, speed)) * sat(uActivity + 0.16);
    color = screenBlend(color, uAccentTop, cursor * (0.030 + speed * 0.030));

    for (int i = 0; i < 14; i++) {
        vec4 tr = uTrail[i];
        float live = tr.w * (1.0 - smoothstep(0.0, 3.2, tr.z));
        vec2 tp = (tr.xy - 0.5) * vec2(aspect, 1.0);
        float d = length(centered - tp);
        float glow = 0.010 / max(d + 0.035, 0.010) * live;
        color = screenBlend(color, uAccentTop, glow * 0.025);
    }

    float vignette = 1.0 - smoothstep(0.32, 1.22, length(centered));
    color *= 0.72 + vignette * 0.28;
    color += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    color = screenBlend(color, vec3(0.64, 0.86, 1.0), 0.010);

    FragColor = vec4(clamp(color, 0.0, 1.0), min(0.84, sat(uAlpha) * 0.84));
}
