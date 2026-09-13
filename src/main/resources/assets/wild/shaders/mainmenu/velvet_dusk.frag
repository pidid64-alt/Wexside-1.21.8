#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec2 uResolution;
uniform vec2 uMouse;
uniform vec2 uMouseVelocity;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uAlpha;
uniform float uLightMode;

out vec4 FragColor;

const float TAU = 6.28318530718;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

float sq(float x) {
    return x * x;
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

float fbm(vec2 p) {
    float total = vnoise(p) * 0.62;
    total += vnoise(p * 2.13 + vec2(17.7, 9.2)) * 0.26;
    total += vnoise(p * 4.31 + vec2(41.3, 27.9)) * 0.12;
    return total;
}

vec4 fireflyLayer(
    vec2 uv,
    float aspect,
    float time,
    float density,
    float size,
    float spawnRate,
    float phase
) {
    vec2 scaled = uv * vec2(aspect, 1.0) * density + vec2(phase * 59.0, phase * 41.0);
    vec2 cell   = floor(scaled);
    vec2 local  = fract(scaled) - 0.5;

    float r1 = hash12(cell + phase * 107.0);
    float r2 = hash12(cell + phase * 107.0 + vec2(23.17, 51.37));
    float r3 = hash12(cell + phase * 107.0 + vec2(67.61, 13.03));
    float r4 = hash12(cell + phase * 107.0 + vec2(37.43, 79.19));

    float alive = step(1.0 - spawnRate, r1);

    vec2 wander;
    wander.x = sin(time * mix(0.11, 0.23, r2) + r1 * TAU) * 0.20 + sin(time * mix(0.31, 0.53, r3) + r2 * TAU) * 0.10;
    wander.y = sin(time * mix(0.13, 0.21, r3) + r3 * TAU) * 0.20 + sin(time * mix(0.29, 0.47, r4) + r4 * TAU) * 0.10;

    float edgeFade = smoothstep(0.30, 0.20, max(abs(wander.x), abs(wander.y)));

    vec2 d = local - wander;
    float dist2 = dot(d, d);

    float life = 0.5 + 0.5 * sin(time * mix(0.09, 0.19, r4) + r1 * TAU);
    float vis = smoothstep(0.16, 0.58, life);
    float pulse = 0.55 + 0.45 * sin(time * mix(1.2, 2.3, r2) + r3 * TAU);

    float coreK = 1.0 / max(sq(size * 0.24), 0.00001);
    float haloK = 1.0 / max(sq(size * 1.05), 0.00001);
    float core = exp(-dist2 * coreK);
    float halo = exp(-dist2 * haloK) * 0.30;

    vec3 gold = vec3(1.0, 0.82, 0.45);
    vec3 col = mix(gold, vec3(1.0, 0.94, 0.72), core);

    float a = alive * vis * pulse * edgeFade * clamp(core + halo, 0.0, 1.0);
    return vec4(col, a);
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv0 = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time = uTime;

    vec2 parallax = (clamp(uMouse, vec2(0.0), vec2(1.0)) - 0.5) * vec2(0.013, 0.009);
    vec2 uv = uv0 + parallax * 0.4;

    vec3 duskTop = mix(vec3(0.051, 0.031, 0.106), uAccentBottom * 0.10 + vec3(0.02, 0.01, 0.05), 0.30);
    vec3 duskMid = mix(vec3(0.098, 0.051, 0.184), uAccentTop * 0.14 + vec3(0.04, 0.02, 0.09), 0.26);
    vec3 duskLow = mix(vec3(0.145, 0.075, 0.243), uAccentTop * 0.20 + vec3(0.05, 0.02, 0.11), 0.24);

    vec3 bg = mix(duskTop, duskMid, smoothstep(0.0, 0.55, uv.y));
    bg = mix(bg, duskLow, smoothstep(0.45, 1.05, uv.y) * 0.72);
    bg = mix(bg, vec3(0.32, 0.26, 0.42), uLightMode * 0.12);

    vec2 centered = (uv - 0.5) * vec2(aspect, 1.0);
    float vignette = smoothstep(1.25, 0.35, dot(centered, centered));
    bg *= mix(0.82, 1.0, vignette);

    float silk = fbm(uv * vec2(aspect, 1.0) * 2.1 + vec2(time * 0.011, time * 0.007));
    float fold = 0.5 + 0.5 * sin(uv.x * 4.6 + silk * 5.2 + time * 0.045);
    bg *= mix(0.94, 1.07, fold);
    float sheen = sq(smoothstep(0.42, 0.88, fold));
    bg = mix(bg, mix(vec3(0.30, 0.19, 0.50), uAccentTop * 0.45 + vec3(0.08, 0.04, 0.16), 0.30), sheen * 0.085);

    float mistNoise = fbm(vec2(uv.x * aspect * 2.3 + time * 0.009, uv.y * 3.6 + time * 0.052));
    float mistMask = smoothstep(0.56, 1.02, uv.y);
    float mist = mistMask * (0.35 + mistNoise * 0.65);
    vec3 mistCol = mix(vec3(0.42, 0.28, 0.66), uAccentTop * 0.55 + vec3(0.10, 0.05, 0.20), 0.32);
    bg = mix(bg, mistCol, mist * 0.14);

    vec4 farFlies = fireflyLayer(uv + parallax * 0.6, aspect, time,
        5.4,
        0.105,
        0.26,
        0.31
    );

    vec4 nearFlies = fireflyLayer(uv + parallax * 1.5, aspect, time,
        3.1,
        0.170,
        0.30,
        0.77
    );

    vec3 color = bg;
    color = mix(color, farFlies.rgb, farFlies.a * 0.55);
    color = mix(color, nearFlies.rgb, nearFlies.a * 0.80);

    float totalFlies = farFlies.a + nearFlies.a;
    float atmosphere = 0.430 + smoothstep(0.0, 1.0, uv.y) * 0.050 + mist * 0.08 + totalFlies * 0.24;
    float alpha = clamp(atmosphere * clamp(uAlpha, 0.0, 1.0), 0.0, 0.72);

    FragColor = vec4(clamp(color, 0.0, 1.0), alpha);
}
