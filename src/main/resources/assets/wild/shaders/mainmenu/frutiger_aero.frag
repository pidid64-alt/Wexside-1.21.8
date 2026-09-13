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

float caustics(vec2 uv, float aspect, float time) {
    vec2 p = uv * vec2(aspect, 1.0) * 5.4;
    float a = sin(p.x * 1.7 + time * 0.31) + sin(p.y * 2.3 - time * 0.24 + 1.3);
    float b = sin((p.x + p.y) * 1.1 + time * 0.19 + 4.1) + sin(p.x * 0.7 - p.y * 1.6 - time * 0.27);
    float band = max((a + b) * 0.25, 0.0);
    float lit = sq(band);
    return sq(lit);
}

vec4 bubbleLayer(
    vec2 uv,
    float aspect,
    float time,
    float density,
    float riseSpeed,
    float size,
    float spawnRate,
    float phase
) {
    vec2 moving;
    moving.x = uv.x;
    moving.y = uv.y + time * riseSpeed;

    vec2 scaled = vec2(moving.x * aspect, moving.y) * density + vec2(phase * 53.0, phase * 29.0);
    vec2 cell   = floor(scaled);
    vec2 local  = fract(scaled) - 0.5;

    float r1 = hash12(cell + phase * 97.0);
    float r2 = hash12(cell + phase * 97.0 + vec2(17.71, 41.13));
    float r3 = hash12(cell + phase * 97.0 + vec2(61.07, 23.99));

    float alive = step(1.0 - spawnRate, r1);

    local -= (vec2(r2, r3) - 0.5) * 0.40;
    local.x += sin(time * mix(0.55, 1.05, r2) + r3 * TAU) * 0.05;

    float radius = size * mix(0.55, 1.0, r3);
    float d = length(local) - radius;

    float fill = smoothstep(0.010, -0.004, d) * alive;
    float rim  = smoothstep(radius * 0.55, radius * 0.96, length(local)) * fill;

    vec2 n = local / max(radius, 0.0001);
    vec2 sd = n - vec2(-0.38, -0.38);
    float spec = exp(-dot(sd, sd) * 14.0) * fill;
    vec2 sd2 = n - vec2(0.30, 0.34);
    float spec2 = exp(-dot(sd2, sd2) * 30.0) * fill * 0.30;

    vec3 shell = mix(vec3(0.86, 0.97, 0.99), uAccentTop * 0.60 + vec3(0.40), 0.28);
    vec3 col = shell;
    col = mix(col, vec3(0.94, 1.00, 0.99), rim * 0.55);
    col = mix(col, vec3(1.0), clamp(spec + spec2, 0.0, 1.0));

    float a = alive * clamp(fill * 0.07 + rim * 0.42 + spec * 0.85 + spec2, 0.0, 1.0);
    return vec4(col, a);
}

float lensGlint(vec2 uv, float aspect, float time) {
    vec2 dir = normalize(vec2(0.82, 0.57));
    float proj = dot(uv * vec2(aspect, 1.0), dir);
    float travel = mix(-0.9, aspect + 1.4, fract(time * 0.021));
    float d = proj - travel;
    float core = exp(-sq(d * 9.0));
    float soft = exp(-sq(d * 3.2)) * 0.35;
    return core + soft;
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv0 = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time = uTime;

    vec2 parallax = (clamp(uMouse, vec2(0.0), vec2(1.0)) - 0.5) * vec2(0.014, 0.010);
    vec2 uv = uv0 + parallax * 0.4;

    vec3 skyTop = mix(vec3(0.482, 0.894, 0.949), uAccentTop * 0.55 + vec3(0.45), 0.20);
    vec3 skyMid = mix(vec3(0.665, 0.945, 0.910), uAccentBottom * 0.40 + vec3(0.60), 0.14);
    vec3 glowLow = mix(vec3(0.216, 0.776, 0.627), uAccentBottom * 0.65 + vec3(0.35), 0.22);

    vec3 bg = mix(skyTop, skyMid, smoothstep(0.0, 0.55, uv.y));
    bg = mix(bg, glowLow, smoothstep(0.48, 1.05, uv.y) * 0.62);
    bg = mix(bg, vec3(1.0), uLightMode * 0.14);

    float sunSpot = 1.0 - smoothstep(0.0, 0.9, length((uv - vec2(0.22, -0.06)) * vec2(aspect * 0.5, 1.0)));
    bg = mix(bg, vec3(0.94, 1.00, 0.98), sunSpot * 0.16);

    float lit = caustics(uv + parallax * 0.2, aspect, time);
    float litFade = smoothstep(0.05, 0.45, uv.y) * smoothstep(1.05, 0.55, uv.y);
    bg = mix(bg, vec3(0.88, 1.00, 0.96), lit * litFade * 0.11);

    float glint = lensGlint(uv, aspect, time);
    bg = mix(bg, vec3(1.0, 1.0, 0.98), glint * 0.085);

    vec4 farBubbles = bubbleLayer(uv + parallax * 0.5, aspect, time,
        7.0,
        0.021,
        0.070,
        0.30,
        0.19
    );

    vec4 midBubbles = bubbleLayer(uv + parallax * 1.0, aspect, time,
        4.5,
        0.039,
        0.105,
        0.33,
        0.47
    );

    vec4 nearBubbles = bubbleLayer(uv + parallax * 1.7, aspect, time,
        2.8,
        0.063,
        0.150,
        0.28,
        0.83
    );

    vec3 color = bg;
    color = mix(color, farBubbles.rgb, farBubbles.a * 0.55);
    color = mix(color, midBubbles.rgb, midBubbles.a * 0.75);
    color = mix(color, nearBubbles.rgb, nearBubbles.a * 0.90);

    float totalBubbles = farBubbles.a + midBubbles.a + nearBubbles.a;
    float atmosphere = 0.335 + smoothstep(0.0, 1.0, uv.y) * 0.045 + lit * litFade * 0.05 + glint * 0.04 + totalBubbles * 0.20;
    float alpha = clamp(atmosphere * clamp(uAlpha, 0.0, 1.0), 0.0, 0.66);

    FragColor = vec4(clamp(color, 0.0, 1.0), alpha);
}
