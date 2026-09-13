#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform vec2 u_Resolution;
uniform vec4 u_ElementRect;
uniform float u_Radius;
uniform vec3 u_BaseColor;
uniform vec3 u_LightShadowColor;
uniform vec3 u_DarkShadowColor;
uniform float u_LightShadowAlpha;
uniform float u_DarkShadowAlpha;
uniform float u_Alpha;
uniform float u_Distance;
uniform float u_Blur;
uniform float u_Intensity;
uniform vec2 u_LightDirection;
uniform int u_ShapeType;
uniform int u_Shape;
uniform int u_Inset;

out vec4 FragColor;

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

float roundedBox(vec2 p, vec2 halfSize, float radius) {
    vec2 q = abs(p) - halfSize + vec2(radius);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
}

float coverage(float distance, float aa) {
    return saturate(0.5 - distance / max(aa, 0.0001));
}

float softCoverage(float distance, float blur) {
    return saturate(0.5 - distance / max(blur, 1.0));
}

float directional(vec2 p, vec2 halfSize, vec2 direction) {
    vec2 n = p / max(halfSize, vec2(1.0));
    float l = length(n);
    if (l <= 0.0001) {
        return 0.0;
    }
    return saturate(dot(n / l, direction) * 0.72 + 0.50);
}

vec3 applySurface(vec3 baseColor, vec2 p, vec2 halfSize, int shapeType, float intensity) {
    vec2 normalizedPoint = clamp(p / max(halfSize, vec2(1.0)), vec2(-1.0), vec2(1.0));
    float diagonal = saturate((normalizedPoint.x + normalizedPoint.y) * 0.25 + 0.5);
    float signValue = shapeType == 2 ? -1.0 : 1.0;
    float gradient = (0.5 - diagonal) * 0.10 * signValue;
    float gain = shapeType == 0 ? 0.0 : gradient * (0.72 + intensity * 0.28);
    return clamp(baseColor + vec3(gain), 0.0, 1.0);
}

void main() {
    vec2 center = u_ElementRect.xy + u_ElementRect.zw * 0.5;
    vec2 halfSize = max(u_ElementRect.zw * 0.5, vec2(1.0));
    float radius = clamp(u_Radius, 0.0, min(halfSize.x, halfSize.y));
    vec2 p = vScreen - center;
    float sdf = roundedBox(p, halfSize, radius);
    float aa = max(0.62, fwidth(sdf) * 0.95);
    float fill = coverage(sdf, aa);
    float outside = 1.0 - fill;
    float distanceValue = max(0.0, u_Distance);
    float blurValue = max(1.0, u_Blur);
    float intensity = saturate(u_Intensity);
    vec2 lightDir = length(u_LightDirection) > 0.001 ? normalize(u_LightDirection) : normalize(vec2(-1.0, -1.0));
    vec2 darkDir = -lightDir;
    vec2 darkOffset = darkDir * distanceValue;
    vec2 lightOffset = lightDir * distanceValue;
    vec3 baseColor = clamp(u_BaseColor, 0.0, 1.0);
    vec3 darkColor = clamp(mix(baseColor * 0.78, u_DarkShadowColor, 0.74), 0.0, 1.0);
    vec3 lightColor = clamp(max(u_LightShadowColor, baseColor * 1.24), 0.0, 1.0);
    int shapeType = clamp(u_ShapeType, 0, 2);
    vec3 surface = applySurface(baseColor, p, halfSize, shapeType, intensity);

    if (u_Inset == 1) {
        float edgeWidth = max(1.2, distanceValue + blurValue * 0.42);
        float edge = fill * saturate((edgeWidth + sdf) / edgeWidth);
        float darkInner = edge * directional(p, halfSize, lightDir) * (0.35 + intensity * 0.65) * u_DarkShadowAlpha;
        float lightInner = edge * directional(p, halfSize, darkDir) * (0.25 + intensity * 0.58) * u_LightShadowAlpha;
        float shapeGain = shapeType == 2 ? 0.026 * intensity : 0.012 * intensity;
        vec3 insetColor = mix(surface, darkColor, saturate(darkInner));
        insetColor = mix(insetColor, lightColor, saturate(lightInner));
        insetColor = mix(insetColor, baseColor * 0.92, shapeGain * fill);
        FragColor = vec4(clamp(insetColor, 0.0, 1.0), fill * u_Alpha);
        return;
    }

    float darkSdf = roundedBox(p - darkOffset, halfSize, radius);
    float lightSdf = roundedBox(p - lightOffset, halfSize, radius);
    float darkMask = softCoverage(darkSdf, blurValue) * outside * directional(p, halfSize + vec2(distanceValue + blurValue), darkDir);
    float lightMask = softCoverage(lightSdf, blurValue) * outside * directional(p, halfSize + vec2(distanceValue + blurValue), lightDir);
    float shadowPower = 0.26 + intensity * 0.74;
    float darkAlpha = saturate(darkMask * u_DarkShadowAlpha * shadowPower);
    float lightAlpha = saturate(lightMask * u_LightShadowAlpha * shadowPower);
    float outerAlpha = max(darkAlpha, lightAlpha);
    vec3 outerColor = (darkColor * darkAlpha + lightColor * lightAlpha) / max(darkAlpha + lightAlpha, 0.0001);
    vec3 finalColor = mix(outerColor, surface, fill);
    float finalAlpha = max(outerAlpha, fill) * u_Alpha;
    FragColor = vec4(clamp(finalColor, 0.0, 1.0), finalAlpha);
}
