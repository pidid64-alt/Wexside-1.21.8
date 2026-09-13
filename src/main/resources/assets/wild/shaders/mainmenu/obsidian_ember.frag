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

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
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

vec3 screenBlend(vec3 base, vec3 light, float amount) {
    vec3 s = 1.0 - (1.0 - base) * (1.0 - light);
    return mix(base, s, sat(amount));
}

vec4 emberLayer(vec2 uv, float aspect, float time, float density, float riseSpeed, float size, float spawnRate, float phase) {
    vec2 moving;
    moving.y = uv.y + time * riseSpeed;
    moving.x = uv.x + sin(time * (0.16 + phase * 0.11) + uv.y * 2.2 + phase * TAU) * 0.010;
    vec2 scaled = vec2(moving.x * aspect, moving.y) * density + vec2(phase * 47.0, phase * 31.0);
    vec2 cell = floor(scaled);
    vec2 local = fract(scaled) - 0.5;

    float r1 = hash12(cell + phase * 113.0);
    float r2 = hash12(cell + phase * 113.0 + vec2(31.41, 27.18));
    float r3 = hash12(cell + phase * 113.0 + vec2(71.31, 13.37));
    float alive = step(1.0 - spawnRate, r1);

    local.x -= (r2 - 0.5) * 0.44 + sin(time * (0.7 + r3 * 1.3) + r1 * TAU) * 0.075;
    local.y -= (r3 - 0.5) * 0.40;

    float flicker = 0.42 + 0.58 * (0.5 + 0.5 * sin(time * (2.1 + r2 * 3.6) + r1 * TAU));
    float rad = size * (0.65 + 0.70 * r3);
    float d = length(local);
    float core = 1.0 - smoothstep(rad * 0.30, rad, d);
    float halo = 0.0060 / max(d + 0.026, 0.006);

    float segY = clamp(local.y, 0.0, 0.30);
    vec2 tp = vec2(local.x, local.y - segY);
    float tFade = 1.0 - segY / 0.30;
    float trail = (1.0 - smoothstep(rad * 0.08, rad * 0.60, length(tp))) * tFade * tFade * smoothstep(-0.02, 0.03, local.y);

    float heightFade = smoothstep(0.10, 0.55, uv.y);

    vec3 amber = vec3(1.0, 0.694, 0.412);
    vec3 orange = vec3(1.0, 0.357, 0.165);
    vec3 col = mix(orange, amber, smoothstep(0.30, 0.92, uv.y));
    col = screenBlend(col, uAccentBottom, 0.10);

    float energy = (core * 1.15 + halo * 0.40 + trail * 0.50) * alive * flicker * heightFade;
    return vec4(col, energy);
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time = uTime;
    vec2 mouse = clamp(uMouse, vec2(0.0), vec2(1.0));
    vec2 parallax = (mouse - 0.5) * vec2(aspect, 1.0);
    vec2 gp = vec2(uv.x * aspect, uv.y);

    vec3 gTop = vec3(0.024, 0.023, 0.030);
    vec3 gBottom = vec3(0.052, 0.041, 0.043);
    vec3 bg = mix(gTop, gBottom, smoothstep(0.0, 1.0, uv.y));

    float grain = noise(gp * 3.4 + vec2(0.0, time * 0.008)) * 0.62 + noise(gp * 7.9) * 0.38;
    bg += vec3(grain - 0.5) * 0.016;

    float sheenP = dot(gp, normalize(vec2(0.82, -0.57)));
    float sheenW = 0.5 + 0.5 * sin(sheenP * 5.2 - time * 0.10);
    bg += vec3(0.050, 0.052, 0.062) * sheenW * sheenW * sheenW * 0.10;

    float hazeMask = smoothstep(0.85, 1.0, uv.y);
    float wob = (sin(gp.x * 30.0 + time * 1.9 + sin(gp.x * 11.0 - time * 0.8) * 1.3) * 0.62
               + sin(gp.x * 57.0 - time * 2.6 + uv.y * 34.0) * 0.38) * 0.014 * hazeMask;
    float gy = uv.y + wob;
    float ground = smoothstep(0.66, 1.02, gy);
    ground *= ground;
    float breath = 0.70 + 0.30 * sin(time * 0.42 + sin(time * 0.17) * 0.9);

    vec3 amber = vec3(1.0, 0.694, 0.412);
    vec3 orange = vec3(1.0, 0.357, 0.165);
    vec3 glowCol = mix(orange, amber, 0.55);
    glowCol = screenBlend(glowCol, uAccentBottom, 0.08);
    bg += glowCol * ground * breath * 0.085;
    float edge = smoothstep(0.965, 1.005, gy);
    bg += orange * edge * breath * 0.055;

    vec4 e1 = emberLayer(uv + parallax * 0.0016, aspect, time, 7.2, 0.052, 0.058, 0.16, 0.23);
    vec4 e2 = emberLayer(uv + parallax * 0.0034, aspect, time, 5.0, 0.082, 0.080, 0.20, 0.57);
    vec4 e3 = emberLayer(uv + parallax * 0.0058, aspect, time, 3.4, 0.118, 0.104, 0.24, 0.89);

    vec3 color = bg;
    color = screenBlend(color, e1.rgb * sat(e1.a), 0.55);
    color = screenBlend(color, e2.rgb * sat(e2.a), 0.75);
    color = screenBlend(color, e3.rgb * sat(e3.a), 0.95);

    vec2 centered = (uv - 0.5) * vec2(aspect, 1.0);
    float vig = 1.0 - smoothstep(0.38, 1.25, length(centered));
    color *= 0.78 + vig * 0.22;
    color += (hash12(vScreen + fract(time) * 8192.0) - 0.5) * vec3(0.0016);

    float sparkEnergy = sat(e1.a) + sat(e2.a) + sat(e3.a);
    float alpha = clamp((0.80 + sat(sparkEnergy) * 0.06) * sat(uAlpha), 0.0, 0.86);
    FragColor = vec4(clamp(color, 0.0, 1.0), alpha);
}
