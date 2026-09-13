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

float rayBeam(vec2 rel, float angle, float sharp, float reach) {
    vec2 dir = vec2(cos(angle), -sin(angle));
    float along = dot(rel, dir);
    float perp = rel.x * dir.y - rel.y * dir.x;
    float beam = exp(-sq(perp * sharp));
    float gate = smoothstep(0.02, 0.30, along);
    float fade = exp(-along * reach);
    return beam * gate * fade;
}

vec4 moteLayer(
    vec2 uv,
    float aspect,
    float time,
    float density,
    float driftX,
    float driftY,
    float size,
    float spawnRate,
    float phase
) {
    vec2 moving;
    moving.x = uv.x - time * driftX + sin(time * 0.34 + uv.y * 2.8 + phase * TAU) * 0.016;
    moving.y = uv.y + time * driftY;

    vec2 scaled = vec2(moving.x * aspect, moving.y) * density + vec2(phase * 43.0, phase * 37.0);
    vec2 cell   = floor(scaled);
    vec2 local  = fract(scaled) - 0.5;

    float r1 = hash12(cell + phase * 131.0);
    float r2 = hash12(cell + phase * 131.0 + vec2(19.19, 47.03));
    float r3 = hash12(cell + phase * 131.0 + vec2(73.73, 11.31));

    float alive = step(1.0 - spawnRate, r1);

    local -= (vec2(r2, r3) - 0.5) * 0.44;
    local.y += sin(time * mix(0.4, 0.8, r2) + r3 * TAU) * 0.035;

    float dist2 = dot(local, local);
    float coreK = 1.0 / max(sq(size * 0.30), 0.00001);
    float haloK = 1.0 / max(sq(size * 1.15), 0.00001);
    float core = exp(-dist2 * coreK);
    float halo = exp(-dist2 * haloK) * 0.22;

    vec3 col = mix(vec3(1.0, 0.985, 0.94), uAccentTop * 0.35 + vec3(0.65), 0.18);

    float a = alive * clamp(core * 0.85 + halo, 0.0, 1.0) * mix(0.6, 1.0, r3);
    return vec4(col, a);
}

void main() {
    vec2 resolution = max(uResolution, vec2(1.0));
    vec2 uv0 = vScreen / resolution;
    float aspect = resolution.x / max(resolution.y, 1.0);
    float time = uTime;

    vec2 parallax = (clamp(uMouse, vec2(0.0), vec2(1.0)) - 0.5) * vec2(0.011, 0.008);
    vec2 uv = uv0 + parallax * 0.4;

    vec3 cream  = mix(vec3(0.988, 0.976, 0.952), uAccentTop * 0.10 + vec3(0.90), 0.06);
    vec3 blush  = mix(vec3(0.995, 0.948, 0.905), uAccentBottom * 0.18 + vec3(0.82), 0.10);
    vec3 amber  = mix(vec3(1.000, 0.905, 0.780), uAccentBottom * 0.30 + vec3(0.70), 0.14);

    vec3 bg = mix(cream, blush, smoothstep(0.05, 0.85, uv.y));
    bg = mix(bg, vec3(1.0), uLightMode * 0.05);

    vec2 rel = (uv - vec2(0.04, 1.03)) * vec2(aspect, 1.0);
    float breath = 2.4 - 0.45 * sin(time * 0.19);
    float glow = exp(-dot(rel, rel) * breath);
    bg = mix(bg, amber, glow * 0.34);
    bg = mix(bg, vec3(1.0, 0.965, 0.885), sq(glow) * 0.22);

    float ray1 = rayBeam(rel, 0.42 + sin(time * 0.041 + 0.7) * 0.085, 7.5, 0.62);
    float ray2 = rayBeam(rel, 0.78 + sin(time * 0.033 + 2.9) * 0.070, 10.0, 0.75);
    float ray3 = rayBeam(rel, 1.08 + sin(time * 0.052 + 5.1) * 0.095, 6.0, 0.90);
    float rays = ray1 * 0.055 + ray2 * 0.045 + ray3 * 0.040;
    bg = mix(bg, vec3(1.0, 0.94, 0.83), rays);

    vec4 farMotes = moteLayer(uv + parallax * 0.6, aspect, time,
        6.2,
        0.012,
        0.008,
        0.085,
        0.24,
        0.29
    );

    vec4 nearMotes = moteLayer(uv + parallax * 1.4, aspect, time,
        3.4,
        0.021,
        0.014,
        0.130,
        0.30,
        0.67
    );

    vec3 color = bg;
    color = mix(color, farMotes.rgb, farMotes.a * 0.40);
    color = mix(color, nearMotes.rgb, nearMotes.a * 0.55);

    float totalMotes = farMotes.a + nearMotes.a;
    float atmosphere = 0.300 + glow * 0.06 + rays * 0.5 + totalMotes * 0.12;
    float alpha = clamp(atmosphere * clamp(uAlpha, 0.0, 1.0), 0.0, 0.52);

    FragColor = vec4(clamp(color, 0.0, 1.0), alpha);
}
