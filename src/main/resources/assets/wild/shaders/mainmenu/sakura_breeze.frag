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
uniform float uActivity;
uniform float uAlpha;
uniform float uLightMode;
uniform vec4 uTrail[14];

out vec4 FragColor;

const float TAU = 6.28318530718;

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + vec3(33.33));
    return fract((p3.x + p3.y) * p3.z);
}

mat2 rot2(float a) {
    float s = sin(a);
    float c = cos(a);
    return mat2(c, -s, s, c);
}

float windOffset(float y, float time, float phase) {
    float slow = sin(time * 0.28 + y * 1.9 + phase) * 0.038;
    float fast = sin(time * 0.71 + y * 4.3 + phase * 2.17) * 0.014;
    return slow + fast;
}

vec4 petalLayer(
    vec2 uv,
    float aspect,
    float time,
    float density,
    float fallSpeed,
    float windAmp,
    float size,
    float spawnRate,
    float spinSpeed,
    float phase
) {
    float sway = windOffset(uv.y, time, phase) * windAmp * 28.0;
    vec2 moving;
    moving.y = uv.y + time * fallSpeed;
    moving.x = uv.x + sway + sin(time * 0.42 + uv.y * 2.6 + phase * TAU) * windAmp * 0.55;

    vec2 scaled = vec2(moving.x * aspect, moving.y) * density + vec2(phase * 47.0, phase * 31.0);
    vec2 cell   = floor(scaled);
    vec2 local  = fract(scaled) - 0.5;

    float r1 = hash12(cell + phase * 113.0);
    float r2 = hash12(cell + phase * 113.0 + vec2(31.41, 27.18));
    float r3 = hash12(cell + phase * 113.0 + vec2(71.31, 13.37));

    float alive = step(1.0 - spawnRate, r1);

    local -= (vec2(r2, r3) - 0.5) * 0.46;
    local  = rot2(r2 * TAU + time * spinSpeed) * local;

    vec2 q;
    q.x = local.x / mix(0.48, 0.68, r3);
    q.y = local.y;
    q.y += abs(q.x) * mix(0.20, 0.42, r2);
    float d = length(q) - size;

    float fill = smoothstep(0.016, -0.006, d) * alive;
    float rim  = smoothstep(size * 0.90, size * 0.45, length(q)) * fill;

    float vein = exp(-abs(q.x) * 22.0) * smoothstep(0.3, -0.1, q.y) * fill;

    vec3 col = mix(
        mix(vec3(1.000, 0.778, 0.868), uAccentTop  * 0.88 + vec3(0.12), 0.24),
        mix(vec3(1.000, 0.920, 0.950), uAccentBottom * 0.80 + vec3(0.20), 0.20),
        r3
    );
    col  = mix(col, vec3(1.0, 0.97, 0.99), rim  * 0.32);
    col -= vec3(0.08, 0.02, 0.04) * vein * 0.18;

    return vec4(col, fill);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0) + hash12(px * 0.731 + seed * 61.0) * 0.37);
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
}

float breezeShimmer(vec2 uv, float aspect, float time) {
    vec2 dir = normalize(vec2(1.0, 0.38));
    float proj = dot(uv * vec2(aspect, 1.0), dir);
    float wave1 = sin(proj * 14.0 - time * 0.55) * 0.5 + 0.5;
    float wave2 = sin(proj * 22.0 + time * 0.34 + 1.7) * 0.5 + 0.5;
    float shimmer = pow(wave1, 3.5) * pow(wave2, 2.5);

    float edgeFade = smoothstep(0.0, 0.18, uv.y) * smoothstep(1.0, 0.82, uv.y);
    return shimmer * edgeFade * 0.028;
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv     = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time   = uTime;

    vec3 snow  = mix(vec3(0.998, 0.996, 0.998), uAccentTop  * 0.18 + vec3(0.82), 0.032);
    vec3 peach = mix(vec3(1.000, 0.906, 0.858), uAccentBottom, 0.22);
    vec3 rose  = mix(vec3(1.000, 0.930, 0.950), uAccentTop,    0.16);

    vec3 bg = mix(snow, peach, smoothstep(0.00, 0.58, uv.y));
    bg = mix(bg, rose, smoothstep(0.40, 1.0,  uv.y) * 0.50);

    float warmSpot = 1.0 - smoothstep(0.0, 0.86, length((uv - vec2(0.78, 0.03)) * vec2(aspect * 0.56, 1.0)));
    bg = mix(bg, mix(peach, vec3(1.0), 0.60), warmSpot * 0.13);

    float shimmer = breezeShimmer(uv, aspect, time);
    bg = mix(bg, vec3(1.0, 0.96, 0.98), shimmer);

    vec4 bgPetals = petalLayer(uv, aspect, time,
        5.8,
        0.034,
        0.018,
        0.088,
        0.28,
        0.28,
        0.23
    );

    vec4 fgPetals = petalLayer(uv, aspect, time,
        3.6,
        0.066,
        0.032,
        0.128,
        0.45,
        0.52,
        0.71
    );

    vec3 color = bg;
    color = mix(color, bgPetals.rgb, bgPetals.a * 0.70);
    color = mix(color, fgPetals.rgb, fgPetals.a * 0.90);

    float totalPetals = bgPetals.a + fgPetals.a;
    float atmosphere  = 0.325 + smoothstep(0.0, 1.0, uv.y) * 0.055 + totalPetals * 0.22;
    float alpha = clamp(atmosphere * clamp(uAlpha, 0.0, 1.0), 0.0, 0.68);
    color += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    FragColor = vec4(clamp(color, 0.0, 1.0), alpha);
}
