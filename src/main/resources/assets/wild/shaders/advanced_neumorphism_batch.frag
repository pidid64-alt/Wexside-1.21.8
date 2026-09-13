#version 330 core

const int MAX_PLATES = 128;

in vec2 vScreen;
flat in int vPlateIndex;

uniform vec2 u_LightDirection;

layout(std140) uniform NeumorphicPlateBlock {
    vec4 b_DrawRect[MAX_PLATES];
    vec4 b_ElementRect[MAX_PLATES];
    vec4 b_Style[MAX_PLATES];
    vec4 b_Base[MAX_PLATES];
    vec4 b_Dark[MAX_PLATES];
    vec4 b_Light[MAX_PLATES];
    vec4 b_Flags[MAX_PLATES];
};

out vec4 FragColor;

float saturate(float value) {
    return clamp(value, 0.0, 1.0);
}

float roundedBox(vec2 p, vec2 halfSize, float radius) {
    vec2 q = abs(p) - halfSize + vec2(radius);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - radius;
}

float aaFill(float d) {
    float aa = max(0.62, fwidth(d));
    return saturate(0.5 - d / aa);
}

float softMask(float d, float blur) {
    return saturate(0.5 - d / max(1.0, blur));
}

float directional(vec2 p, vec2 halfSize, vec2 direction) {
    vec2 n = p / max(halfSize, vec2(1.0));
    float l = max(length(n), 0.0001);
    return saturate(dot(n / l, direction) * 0.72 + 0.50);
}

vec3 surfaceColor(vec3 baseColor, vec2 p, vec2 halfSize, int shapeType, float intensity) {
    vec2 n = clamp(p / max(halfSize, vec2(1.0)), vec2(-1.0), vec2(1.0));
    float diagonal = saturate((n.x + n.y) * 0.25 + 0.5);
    float shape = shapeType == 2 ? -1.0 : 1.0;
    float gain = shapeType == 0 ? 0.0 : (0.5 - diagonal) * 0.105 * shape * (0.70 + intensity * 0.30);
    return clamp(baseColor + vec3(gain), 0.0, 1.0);
}

void main() {
    int index = vPlateIndex;
    vec4 element = b_ElementRect[index];
    vec4 style = b_Style[index];
    vec4 base = b_Base[index];
    vec4 dark = b_Dark[index];
    vec4 light = b_Light[index];
    vec4 flags = b_Flags[index];
    vec2 center = element.xy + element.zw * 0.5;
    vec2 halfSize = max(element.zw * 0.5, vec2(1.0));
    float radius = clamp(style.x, 0.0, min(halfSize.x, halfSize.y));
    float distanceValue = max(0.0, style.y);
    float blurValue = max(1.0, style.z);
    float intensity = saturate(style.w);
    int shapeType = int(flags.x + 0.5);
    int inset = int(flags.y + 0.5);
    vec2 p = vScreen - center;
    float sdf = roundedBox(p, halfSize, radius);
    float fill = aaFill(sdf);
    float outside = 1.0 - fill;
    vec2 lightDir = length(u_LightDirection) > 0.001 ? normalize(u_LightDirection) : normalize(vec2(-1.0, -1.0));
    vec2 darkDir = -lightDir;
    vec3 baseColor = clamp(base.rgb, 0.0, 1.0);
    vec3 darkColor = clamp(dark.rgb, 0.0, 1.0);
    vec3 lightColor = clamp(light.rgb, 0.0, 1.0);
    vec3 surface = surfaceColor(baseColor, p, halfSize, shapeType, intensity);

    if (inset == 1) {
        float edgeWidth = max(1.2, distanceValue + blurValue * 0.36);
        float edge = fill * saturate((edgeWidth + sdf) / edgeWidth);
        float darkInner = edge * directional(p, halfSize, lightDir) * (0.32 + intensity * 0.68) * dark.a;
        float lightInner = edge * directional(p, halfSize, darkDir) * (0.22 + intensity * 0.54) * light.a;
        vec3 color = mix(surface, darkColor, saturate(darkInner));
        color = mix(color, lightColor, saturate(lightInner));
        color = mix(color, baseColor * 0.91, (shapeType == 2 ? 0.022 : 0.010) * intensity * fill);
        FragColor = vec4(clamp(color, 0.0, 1.0), fill * base.a);
        return;
    }

    float darkSdf = roundedBox(p - darkDir * distanceValue, halfSize, radius);
    float lightSdf = roundedBox(p - lightDir * distanceValue, halfSize, radius);
    float darkMask = softMask(darkSdf, blurValue) * outside * directional(p, halfSize + vec2(distanceValue + blurValue), darkDir);
    float lightMask = softMask(lightSdf, blurValue) * outside * directional(p, halfSize + vec2(distanceValue + blurValue), lightDir);
    float power = 0.25 + intensity * 0.75;
    float darkAlpha = saturate(darkMask * dark.a * power);
    float lightAlpha = saturate(lightMask * light.a * power);
    float outerAlpha = max(darkAlpha, lightAlpha);
    vec3 outerColor = (darkColor * darkAlpha + lightColor * lightAlpha) / max(darkAlpha + lightAlpha, 0.0001);
    vec3 color = mix(outerColor, surface, fill);
    FragColor = vec4(clamp(color, 0.0, 1.0), max(outerAlpha, fill) * base.a);
}
