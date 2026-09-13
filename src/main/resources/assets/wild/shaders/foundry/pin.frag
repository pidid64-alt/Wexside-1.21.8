#version 330 core
in vec2 vPixel;
in vec2 vCenter;
in vec2 vRadii;
in vec4 vColor;
in vec4 vInnerColor;
in vec4 vParams;

uniform float uTime;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

vec3 screenBlend(vec3 base, vec3 light, float amount) {
    vec3 s = 1.0 - (1.0 - base) * (1.0 - light);
    return mix(base, s, sat(amount));
}

void main() {
    vec2 d = vPixel - vCenter;
    float dist = length(d);
    float radius = max(vRadii.x, 0.01);
    float coreRadius = max(vRadii.y, 0.01);
    float hover = sat(vParams.x);
    float phase = vParams.y;
    float shell = 1.0 - smoothstep(radius - 0.68, radius + 0.68, dist);
    float core = 1.0 - smoothstep(coreRadius - 0.52, coreRadius + 0.52, dist);
    float ring = 1.0 - smoothstep(0.42, 1.15, abs(dist - radius * 0.62));
    float halo = exp(-pow(max(dist - coreRadius, 0.0) / max(radius + 8.0 - coreRadius, 0.01), 2.0) * 2.25);
    float pulse = 0.5 + 0.5 * sin(uTime * 5.2 + phase * 6.2831853);
    float glass = exp(-pow(dist / max(radius * 0.94, 0.01), 2.0) * 1.8);
    vec3 accent = vColor.rgb;
    vec3 inner = vInnerColor.rgb;
    vec3 chroma = mix(vec3(0.30, 0.92, 1.0), vec3(1.0, 0.36, 0.70), pulse);
    vec3 rgb = accent * shell * 0.82;
    rgb = screenBlend(rgb, accent, ring * (0.26 + hover * 0.24));
    rgb += accent * halo * (0.16 + hover * 0.24);
    rgb += chroma * halo * hover * 0.14;
    rgb = mix(rgb, inner, core * (0.80 + hover * 0.06));
    rgb += vec3(1.0) * core * (0.04 + hover * 0.10);
    rgb += accent * glass * hover * 0.10;
    float alpha = shell * vColor.a + halo * vColor.a * (0.12 + hover * 0.18) + ring * vColor.a * 0.13 + core * vInnerColor.a;
    fragColor = vec4(clamp(rgb, 0.0, 1.0), sat(alpha));
}
