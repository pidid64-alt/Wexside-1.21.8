#version 150

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>

in vec2 vUv;
in vec4 vColor;
in float vDistance;
in float vKind;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

vec2 rot(vec2 p, float a) {
    float s = sin(a);
    float c = cos(a);
    return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
}

void main() {
    vec3 tint = max(vColor.rgb, vec3(0.025));
    float seed = dot(tint, vec3(0.231, 0.513, 0.717));
    float distanceFade = smoothstep(0.25, 1.8, vDistance) * (1.0 - smoothstep(42.0, 56.0, vDistance));
    vec3 cold = vec3(0.70, 0.84, 1.0);
    vec3 warm = mix(vec3(1.0, 0.70, 0.95), vec3(0.68, 0.95, 1.0), seed);
    vec3 color;
    float alpha;

    if (vKind > 0.5) {
        float x = sat(vUv.x);
        float y = abs(vUv.y * 2.0 - 1.0);
        float headR = length(vec2((x - 0.94) * 2.45, (vUv.y - 0.5) * 2.85));
        float head = exp(-headR * headR * 14.0);
        float headCore = exp(-headR * headR * 58.0);
        float tailWidth = 0.030 + x * 0.165;
        float tail = exp(-pow(y / max(tailWidth, 0.001), 2.0)) * pow(x, 1.82) * (1.0 - smoothstep(0.985, 1.0, x));
        float silk = exp(-pow(y / max(tailWidth * 0.42, 0.001), 2.0)) * pow(x, 3.25);
        float wake = exp(-pow(y / max(tailWidth * 2.6, 0.001), 2.0)) * pow(x, 1.24) * 0.36;
        float cut = smoothstep(0.00, 0.050, x) * (1.0 - smoothstep(0.998, 1.0, x));
        float shimmer = 0.76 + 0.24 * sin(GameTime * 1280.0 + seed * 121.0 + x * 11.0);
        vec3 prism = mix(tint * 1.55, tint.bgr * 1.22 + warm * 0.28, sat(x * 1.18));
        color = vec3(1.0) * headCore * 6.4 + mix(cold, prism, 0.72) * head * 2.15 + prism * tail * 1.42;
        color += mix(warm, vec3(1.0), 0.40) * silk * 0.92;
        color += tint * wake * 0.58;
        alpha = (headCore * 1.00 + head * 0.62 + tail * 0.42 + silk * 0.26 + wake * 0.12) * cut * shimmer * distanceFade * vColor.a;
    } else {
        vec2 p = vUv * 2.0 - 1.0;
        float r2 = dot(p, p);
        float edge = 1.0 - smoothstep(0.78, 1.0, r2);
        if (edge <= 0.001) {
            discard;
        }
        float core = exp(-r2 * 95.0);
        float inner = exp(-r2 * 18.0);
        float halo = exp(-r2 * 3.6);
        float radial = sqrt(max(r2, 0.0));
        vec2 pA = rot(p, seed * 3.14159265 + GameTime * 0.010);
        vec2 pB = rot(p, 0.78539816 + seed * 2.7 - GameTime * 0.007);
        float rayX = exp(-abs(pA.y) * 34.0) * (1.0 - smoothstep(0.16, 1.0, abs(pA.x)));
        float rayY = exp(-abs(pA.x) * 34.0) * (1.0 - smoothstep(0.16, 1.0, abs(pA.y)));
        float rayD = (exp(-abs(pB.y) * 46.0) * (1.0 - smoothstep(0.18, 1.0, abs(pB.x))) + exp(-abs(pB.x) * 46.0) * (1.0 - smoothstep(0.18, 1.0, abs(pB.y)))) * 0.46;
        float crown = pow(sat(0.5 + 0.5 * sin(atan(p.y, p.x) * 8.0 + seed * 19.0 + GameTime * 720.0)), 5.0) * exp(-radial * 5.8);
        float corona = exp(-radial * 4.4) * edge;
        float glass = exp(-pow(abs(radial - 0.31) * 7.0, 2.0)) * edge;
        float twinkle = 0.70 + 0.30 * sin(GameTime * 980.0 + seed * 92.0 + r2 * 3.1);
        color = vec3(1.0) * core * 6.10 + mix(cold, tint * 1.66, 0.70) * inner * 1.95 + tint.bgr * halo * 0.46;
        color += mix(warm, tint, 0.48) * (rayX + rayY + rayD) * 0.82;
        color += mix(tint, vec3(1.0), 0.42) * corona * 0.42;
        color += mix(tint.bgr, vec3(1.0), 0.30) * (crown + glass * 0.36) * 0.48;
        alpha = (core * 1.00 + inner * 0.50 + halo * 0.15 + (rayX + rayY + rayD) * 0.15 + corona * 0.08 + crown * 0.05 + glass * 0.04) * edge * twinkle * distanceFade * vColor.a;
    }

    if (alpha <= 0.003) {
        discard;
    }
    fragColor = vec4(clamp(color, 0.0, 1.0), clamp(alpha, 0.0, 1.0)) * ColorModulator;
}
