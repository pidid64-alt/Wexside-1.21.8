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

const float PI = 3.14159265359;
const float TAU = 6.28318530718;

struct RainFx {
    vec2 offset;
    float drop;
    float trail;
    float shine;
};

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p) {
    return vec2(hash12(p), hash12(p + vec2(37.17, 19.91)));
}

float noise(vec2 p) {
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
    float v = 0.0;
    float a = 0.53;
    mat2 m = mat2(0.82, -0.57, 0.57, 0.82);
    for (int i = 0; i < 4; i++) {
        v += noise(p) * a;
        p = m * p * 2.04 + vec2(6.71, 4.37);
        a *= 0.48;
    }
    return v;
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0) + hash12(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

float gauss(float x, float w) {
    x /= max(w, 0.0001);
    return exp(-x * x);
}

vec3 screenBlend(vec3 base, vec3 light, float amount) {
    vec3 s = 1.0 - (1.0 - base) * (1.0 - light);
    return mix(base, s, sat(amount));
}

float softCircle(vec2 uv, vec2 c, float r, float aspect) {
    return 1.0 - smoothstep(r * 0.20, r, length((uv - c) * vec2(aspect, 1.0)));
}

vec3 rainbowPalette(float t) {
    vec3 c = 0.56 + 0.44 * cos(TAU * (t + vec3(0.00, 0.34, 0.67)));
    return c * vec3(1.08, 0.98, 1.02);
}

vec3 backdrop(vec2 uv, float aspect, float time, vec3 emerald, vec3 sun) {
    vec3 skyTop = vec3(0.720, 0.910, 0.985);
    vec3 skyLow = vec3(0.945, 1.000, 0.925);
    vec3 warm = vec3(1.000, 0.830, 0.420);
    vec3 color = mix(skyTop, skyLow, smoothstep(0.02, 0.70, uv.y));
    color = screenBlend(color, warm, softCircle(uv, vec2(0.88, 0.09), 0.70, aspect) * 0.35);

    float field = fbm(uv * vec2(2.0 * aspect, 1.15) + vec2(time * 0.010, -time * 0.006));
    float canopy = smoothstep(0.16, 0.98, uv.y);
    vec3 greenA = vec3(0.085, 0.340, 0.175);
    vec3 greenB = vec3(0.300, 0.690, 0.390);
    vec3 cyanA = vec3(0.245, 0.760, 0.780);
    vec3 forest = mix(greenA, greenB, field);
    forest = mix(forest, cyanA, softCircle(uv, vec2(0.24, 0.58), 0.34, aspect) * 0.28);
    color = mix(color, forest, canopy * (0.32 + 0.24 * field));

    float b0 = softCircle(uv, vec2(0.18, 0.30), 0.28, aspect);
    float b1 = softCircle(uv, vec2(0.68, 0.42), 0.38, aspect);
    float b2 = softCircle(uv, vec2(0.44, 0.78), 0.52, aspect);
    float b3 = softCircle(uv, vec2(0.90, 0.72), 0.36, aspect);
    color = mix(color, vec3(0.105, 0.430, 0.230), b0 * 0.28);
    color = screenBlend(color, vec3(0.600, 0.970, 0.760), b1 * 0.18);
    color = mix(color, vec3(0.095, 0.285, 0.170), b2 * 0.18);
    color = screenBlend(color, vec3(0.920, 0.995, 0.880), b3 * 0.12);

    float mist = gauss(uv.y - 0.60, 0.20) * (0.35 + 0.65 * fbm(uv * vec2(3.5 * aspect, 1.2) + time * 0.012));
    color = mix(color, vec3(0.875, 0.990, 0.925), mist * 0.16);
    return clamp(color, 0.0, 1.0);
}

float godRays(vec2 uv, float aspect, float time) {
    vec2 sunPos = vec2(0.88, 0.08);
    vec2 p = (uv - sunPos) * vec2(aspect, 1.0);
    float d = length(p);
    float a = atan(p.y, p.x);
    float fan = pow(max(cos(a - 2.34), 0.0), 1.15);
    float radial = smoothstep(0.025, 0.24, d) * (1.0 - smoothstep(1.20, 1.88, d));
    float n = fbm(uv * vec2(2.35 * aspect, 1.35) + vec2(time * 0.018, -time * 0.010));
    float bands = sin(a * 10.5 + d * 3.2 - time * 0.19 + n * 2.6) * 0.5 + 0.5;
    float wide = sin(a * 4.0 - time * 0.075 + n * 1.7) * 0.5 + 0.5;
    float rays = (pow(bands, 3.1) * 0.58 + pow(wide, 1.6) * 0.42) * fan * radial;
    return rays * (0.82 + 0.18 * sin(time * 0.55 + n * 2.0));
}

float dustMotes(vec2 uv, float aspect, float time, float density, float size, float seed) {
    vec2 p = vec2(uv.x * aspect, uv.y) * density + vec2(seed, seed * 1.73);
    vec2 id = floor(p);
    vec2 f = fract(p);
    vec2 rnd = hash22(id + seed);
    float rare = step(0.76, rnd.x);
    vec2 c = rnd;
    c.y = fract(c.y - time * (0.010 + rnd.x * 0.020));
    vec2 d = f - c;
    d.x *= aspect;
    float r = size * mix(0.42, 1.55, rnd.y);
    float core = 1.0 - smoothstep(r * 0.20, r, length(d));
    return core * core * rare;
}

vec3 atmosphere(vec2 uv, float aspect, float time, vec3 base, vec3 sun) {
    vec3 color = base;
    float rays = godRays(uv, aspect, time);
    vec3 rayColor = mix(sun, vec3(1.0, 0.940, 0.640), 0.42);
    color = screenBlend(color, rayColor, rays * 0.40);
    color += rayColor * rays * 0.045;

    float solar = softCircle(uv, vec2(0.88, 0.08), 0.48, aspect);
    color = screenBlend(color, vec3(1.0, 0.890, 0.440), solar * 0.30);

    vec2 arcCenter = vec2(-0.24, 1.04);
    float r = length((uv - arcCenter) * vec2(aspect * 0.72, 1.0));
    float radius = 0.965;
    float width = 0.060;
    float band = smoothstep(radius - width, radius - width * 0.36, r) * (1.0 - smoothstep(radius + width * 0.36, radius + width, r));
    float t = sat((r - (radius - width)) / (width * 2.0));
    float dissolve = smoothstep(0.04, 0.30, uv.x) * (1.0 - smoothstep(0.74, 1.02, uv.x));
    dissolve *= smoothstep(0.04, 0.22, uv.y) * (1.0 - smoothstep(0.86, 1.02, uv.y));
    dissolve *= 0.72 + 0.28 * fbm(uv * vec2(2.0 * aspect, 1.0) + vec2(0.0, time * 0.010));
    vec3 rb = rainbowPalette(t);
    color = screenBlend(color, rb, band * dissolve * 0.125);

    float dust = dustMotes(uv, aspect, time, 7.0, 0.080, 2.1);
    dust += dustMotes(uv + vec2(0.013, -0.021), aspect, time, 11.0, 0.050, 8.4) * 0.58;
    dust *= smoothstep(0.025, 0.32, rays + solar * 0.45);
    color = screenBlend(color, mix(vec3(1.0), rayColor, 0.32), dust * 0.25);
    return color;
}

RainFx rainLayer(vec2 uv, float aspect, float time, float scale, float speed, float sizeMul, float spawnEdge, float seed) {
    vec2 p = vec2(uv.x * aspect, uv.y) * scale;
    float col = floor(p.x);
    float columnSpeed = mix(0.55, 1.48, hash12(vec2(col, seed)));
    p.y -= time * speed * columnSpeed;
    vec2 id = floor(p);
    vec2 gv = fract(p) - 0.5;
    vec2 rnd = hash22(id + vec2(seed, seed * 2.1));
    float rnd3 = hash12(id + vec2(seed * 4.7, 31.7));
    float alive = smoothstep(spawnEdge, 1.0, rnd3);
    float wobble = sin(time * (0.55 + rnd.x * 0.70) + rnd.y * TAU) * 0.060;
    vec2 center = vec2((rnd.x - 0.5) * 0.62 + wobble, (rnd.y - 0.5) * 0.22);
    vec2 q = gv - center;
    float s = mix(0.075, 0.190, rnd3) * sizeMul;
    vec2 e = vec2(q.x / max(s * 0.58, 0.001), q.y / max(s * 1.08, 0.001));
    float d = length(e);
    float body = (1.0 - smoothstep(0.58, 1.04, d)) * alive;
    float inner = 1.0 - smoothstep(0.18, 0.82, d);
    float rim = (1.0 - smoothstep(0.82, 1.08, d)) * smoothstep(0.34, 0.86, d) * alive;
    float tailA = smoothstep(-s * 4.2, -s * 1.0, q.y);
    float tailB = 1.0 - smoothstep(-s * 0.32, s * 0.22, q.y);
    float tail = tailA * tailB;
    float tw = s * mix(0.11, 0.27, rnd.x) * (1.0 + tail * 0.85);
    float trail = exp(-(q.x * q.x) / max(tw * tw, 0.0001)) * tail * alive;
    float beads = smoothstep(0.46, 0.94, noise(vec2(q.x * 42.0 + id.x * 1.7, q.y * 26.0 + id.y * 0.9 - time * 0.25)));
    trail *= 0.32 + 0.68 * beads;
    vec2 normal = vec2(e.x / max(aspect, 0.7), e.y);
    vec2 lens = -normal * body * (0.014 + 0.021 * rnd3) * sizeMul;
    vec2 wet = vec2((noise(p * 1.7 + seed) - 0.5) / max(aspect, 0.7), -0.32) * trail * 0.010 * sizeMul;
    float shine = rim + inner * 0.30 + trail * beads * 0.32;
    return RainFx(lens + wet, max(body, trail * 0.45), trail, shine);
}

RainFx addRain(RainFx a, RainFx b) {
    return RainFx(a.offset + b.offset, max(a.drop, b.drop), max(a.trail, b.trail), max(a.shine, b.shine));
}

RainFx rainGlass(vec2 uv, float aspect, float time) {
    RainFx a = rainLayer(uv, aspect, time, 5.3, 0.34, 1.20, 0.46, 1.7);
    RainFx b = rainLayer(uv + vec2(0.019, 0.031), aspect, time, 9.2, 0.24, 0.82, 0.58, 6.9);
    RainFx c = rainLayer(uv + vec2(-0.027, 0.011), aspect, time, 15.5, 0.42, 0.52, 0.66, 13.4);
    return addRain(addRain(a, b), c);
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time = uTime;
    vec2 mouse = clamp(uMouse, vec2(0.0), vec2(1.0));
    vec2 velocity = vec2(uMouseVelocity.x * aspect, uMouseVelocity.y);
    float speed = sat(length(velocity) * 0.7);
    vec3 emerald = uAccentTop;
    vec3 sun = uAccentBottom;

    float filmA = fbm(uv * vec2(16.0 * aspect, 6.0) + vec2(time * 0.014, -time * 0.070));
    float filmB = fbm(uv * vec2(7.0 * aspect, 18.0) + vec2(-time * 0.025, time * 0.040));
    vec2 film = vec2(filmA - 0.5, filmB - 0.5) * (0.0034 + speed * 0.0016);

    RainFx rain = rainGlass(uv, aspect, time);
    vec2 refractedUv = clamp(uv + film + rain.offset, vec2(0.0), vec2(1.0));
    vec3 base = backdrop(refractedUv, aspect, time, emerald, sun);
    vec3 color = atmosphere(refractedUv, aspect, time, base, sun);

    vec2 chromaUv = clamp(uv - rain.offset * 0.62 + film * 0.45, vec2(0.0), vec2(1.0));
    vec3 chromaBase = backdrop(chromaUv, aspect, time, emerald, sun);
    vec3 chroma = atmosphere(chromaUv, aspect, time, chromaBase, sun);
    float lensMask = sat(rain.drop * 1.15);
    color = mix(color, vec3(chroma.r, color.g, chroma.b), lensMask * 0.30);
    color = mix(color, color * 0.88, rain.drop * 0.060);
    color = screenBlend(color, vec3(0.90, 1.00, 0.94), rain.trail * 0.105);
    color = screenBlend(color, vec3(1.00, 0.98, 0.82), rain.shine * (0.22 + 0.20 * godRays(uv, aspect, time)));

    float cursor = 1.0 - smoothstep(0.0, 0.24 + speed * 0.05, length((uv - mouse) * vec2(aspect, 1.0)));
    color = screenBlend(color, mix(emerald, sun, 0.48), cursor * (0.035 + speed * 0.055) * sat(uActivity + 0.2));

    for (int i = 0; i < 14; i++) {
        vec4 tr = uTrail[i];
        float live = tr.w * (1.0 - smoothstep(0.0, 3.2, tr.z));
        float d = length((uv - tr.xy) * vec2(aspect, 1.0));
        float glow = exp(-d * d * 34.0) * live;
        color = screenBlend(color, mix(emerald, sun, 0.58), glow * 0.050);
    }

    vec2 centered = (uv - 0.5) * vec2(aspect, 1.0);
    float vignette = 1.0 - smoothstep(0.26, 1.24, length(centered));
    color *= 0.955 + vignette * 0.045;
    color += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    color = screenBlend(color, vec3(0.992, 1.000, 0.988), 0.018 + uLightMode * 0.012);

    FragColor = vec4(clamp(color, 0.0, 1.0), clamp(uAlpha, 0.0, 1.0));
}
