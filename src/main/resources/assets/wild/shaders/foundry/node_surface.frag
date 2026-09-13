#version 330 core

in vec2 vLocal;
in vec4 vShape;
in vec4 vAccentTop;
in vec4 vAccentBottom;
in vec4 vBase;
in vec4 vParams;
in vec2 vMouse;
in vec2 vPixel;

uniform float uTime;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float grainHash(vec2 p, float t) {
    vec3 p3 = fract(vec3(p.x, p.y, t) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.zyx + 31.32);
    return fract((p3.x + p3.y) * p3.z);
}

float roundedBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

vec3 screenBlend(vec3 base, vec3 light, float amount) {
    vec3 s = 1.0 - (1.0 - base) * (1.0 - light);
    return mix(base, s, sat(amount));
}

void main() {
    vec2 size = max(vShape.xy, vec2(1.0));
    float radius = max(vShape.z, 0.0);
    float shadow = max(vShape.w, 1.0);
    float hover = sat(vParams.x);
    float selected = sat(vParams.y);
    float velocity = sat(vParams.z);
    float alphaMul = sat(vParams.w);
    vec2 p = vLocal - size * 0.5;
    float d = roundedBox(p, size * 0.5, radius);
    float aa = max(fwidth(d), 0.72);
    float inside = 1.0 - smoothstep(0.0, aa, d);
    float rim = 1.0 - smoothstep(0.0, max(1.05, aa * 1.15), abs(d));
    float outer = exp(-pow(max(d, 0.0) / max(shadow, 1.0), 2.0) * 2.15);
    float outerWide = exp(-pow(max(d, 0.0) / max(shadow * 2.15, 1.0), 2.0) * 2.7);
    float innerEdge = exp(-pow(max(-d, 0.0) / max(radius * 1.7, 1.0), 2.0));
    vec2 uv = vLocal / size;
    float topSheen = exp(-(uv.y / 0.22) * (uv.y / 0.22));
    float sideSheen = exp(-pow(abs(uv.x - 0.10) / 0.18, 2.0)) * (1.0 - uv.y);
    float grain = grainHash(floor(vPixel * 0.72), floor(uTime * 6.0)) - 0.5;
    float pulse = 0.5 + 0.5 * sin(uTime * 2.8 + dot(uv, vec2(2.7, 1.9)));
    vec3 accent = mix(vAccentBottom.rgb, vAccentTop.rgb, sat(uv.y * 0.72 + uv.x * 0.28));
    vec3 base = vBase.rgb;
    vec3 mica = base;
    mica = screenBlend(mica, vAccentTop.rgb, 0.020 + selected * 0.050 + topSheen * 0.018);
    mica = screenBlend(mica, vAccentBottom.rgb, hover * 0.022 + sideSheen * 0.018);
    mica += accent * (innerEdge * 0.020 + selected * 0.016 + velocity * 0.010);
    mica += grain * vec3(0.020, 0.022, 0.026);
    vec2 mp = vMouse - vLocal;
    float md = length(mp);
    float magnetic = exp(-pow(md / max(min(size.x, size.y) * (0.72 + hover * 0.22), 1.0), 2.0));
    mica += accent * magnetic * (0.030 + hover * 0.090 + velocity * 0.035);
    mica += vec3(1.0) * magnetic * hover * 0.025;
    vec3 rimColor = mix(vAccentBottom.rgb, vAccentTop.rgb, sat(uv.x * 0.55 + uv.y * 0.45));
    vec3 shadowColor = vec3(0.0);
    vec3 glowColor = accent * (outer * (0.15 + selected * 0.24 + hover * 0.10) + outerWide * selected * 0.12);
    vec3 color = shadowColor * outerWide * 0.66;
    color += glowColor;
    color = mix(color, mica, inside);
    color += rimColor * rim * inside * (0.070 + hover * 0.045 + selected * 0.090);
    color += vec3(1.0) * rim * inside * topSheen * 0.020;
    color += rimColor * pulse * selected * outer * 0.055;
    float alpha = (outerWide * 0.22 + outer * (0.18 + selected * 0.10 + hover * 0.05) + inside * vBase.a + rim * inside * 0.12) * alphaMul;
    if (alpha <= 0.0025) {
        discard;
    }
    fragColor = vec4(clamp(color, 0.0, 1.0), sat(alpha));
}
