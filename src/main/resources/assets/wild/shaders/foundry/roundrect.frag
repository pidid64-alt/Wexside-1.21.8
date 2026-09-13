#version 330 core
layout(location = 0) out vec4 fragColor;

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 uViewport;
uniform vec4 uRect;
uniform vec4 uRadii;
uniform vec4 uTint;
uniform float uSoftness;
uniform float uAlpha;
uniform float uStrokeWidth;
uniform vec4 uStrokeTint;

float cornerRadius(vec2 p, vec4 radii) {
    float top = mix(radii.x, radii.y, step(0.0, p.x));
    float bottom = mix(radii.w, radii.z, step(0.0, p.x));
    return mix(top, bottom, step(0.0, p.y));
}

float roundRectDistance(vec2 p, vec2 halfSize, vec4 radii) {
    float r = clamp(cornerRadius(p, radii), 0.0, min(halfSize.x, halfSize.y));
    vec2 q = abs(p) - halfSize + vec2(r);
    return length(max(q, vec2(0.0))) - r + min(max(q.x, q.y), 0.0);
}

void main() {
    vec2 halfSize = max(uRect.zw * 0.5, vec2(0.5));
    vec2 p = vLocal - uRect.zw * 0.5;
    float d = roundRectDistance(p, halfSize, uRadii);
    float aa = max(fwidth(d), max(uSoftness, 0.0001));
    float fill = 1.0 - smoothstep(0.0, aa, d);
    float strokeHalf = max(uStrokeWidth, 0.0) * 0.5;
    float stroke = (1.0 - smoothstep(strokeHalf, strokeHalf + aa, abs(d))) * step(0.0001, strokeHalf);
    vec4 fillColor = vec4(uTint.rgb, uTint.a * fill);
    vec4 strokeColor = vec4(uStrokeTint.rgb, uStrokeTint.a * stroke);
    float outA = strokeColor.a + fillColor.a * (1.0 - strokeColor.a);
    vec3 outRgb = outA <= 0.0001 ? vec3(0.0) : (strokeColor.rgb * strokeColor.a + fillColor.rgb * fillColor.a * (1.0 - strokeColor.a)) / outA;
    fragColor = vec4(outRgb, outA * clamp(uAlpha, 0.0, 1.0));
}
