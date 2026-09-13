#version 150

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

invariant gl_Position;

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

layout(std140) uniform ChinaHatMaterial {
    vec4 u_AccentTop;
    vec4 u_AccentBottom;
    vec4 u_ResolutionInfo;
    vec4 u_Material;
    vec4 u_KeyLightDirection;
    vec4 u_KeyLightColor;
    vec4 u_AuraParams;
    vec4 u_HatUp;
    mat4 u_InvProjection;
    vec4 u_AuraCenter;
    vec4 u_AuraMajorX;
    vec4 u_AuraMajorZ;
    vec4 u_AuraVertical;
};

flat out vec3 vCenterView;
flat out vec3 vMajorXView;
flat out vec3 vMajorZView;
flat out vec3 vVerticalView;
flat out vec4 vCenterClip;
flat out vec4 vMajorXClip;
flat out vec4 vMajorZClip;
flat out vec4 vVerticalClip;
flat out float vProjectedRadiusPx;
flat out float vBoundVisibility;

const float TAU = 6.28318530718;
const float RADIAL_RATIO = 0.025 / 1.025;

void main() {
    vec4 centerView = ModelViewMat * vec4(u_AuraCenter.xyz, 1.0);
    vec4 majorXView = ModelViewMat * vec4(u_AuraMajorX.xyz, 0.0);
    vec4 majorZView = ModelViewMat * vec4(u_AuraMajorZ.xyz, 0.0);
    vec4 verticalView = ModelViewMat * vec4(u_AuraVertical.xyz, 0.0);
    vec4 centerClip = ProjMat * centerView;
    vec4 majorXClip = ProjMat * majorXView;
    vec4 majorZClip = ProjMat * majorZView;
    vec4 verticalClip = ProjMat * verticalView;
    float ringWSupport = length(vec2(majorXClip.w, majorZClip.w));
    float minimumClipW = centerClip.w - ringWSupport * (1.0 + RADIAL_RATIO) - abs(verticalClip.w);
    float safeVisibility = smoothstep(0.120, 0.280, minimumClipW);
    vec2 centerNdc = centerClip.xy / max(centerClip.w, 1.0e-4);
    vec2 boundMinimum = centerNdc;
    vec2 boundMaximum = centerNdc;
    vec2 ringMinimum = centerNdc;
    vec2 ringMaximum = centerNdc;
    if (minimumClipW > 0.080) {
        boundMinimum = vec2(1.0e20);
        boundMaximum = vec2(-1.0e20);
        ringMinimum = vec2(1.0e20);
        ringMaximum = vec2(-1.0e20);
        for (int ringSample = 0; ringSample < 64; ringSample++) {
            float theta = TAU * float(ringSample) * (1.0 / 64.0);
            float cosine = cos(theta);
            float sine = sin(theta);
            vec4 lineClip = centerClip + majorXClip * cosine + majorZClip * sine;
            vec4 radialClip = (majorXClip * cosine + majorZClip * sine) * RADIAL_RATIO;
            vec2 lineNdc = lineClip.xy / lineClip.w;
            ringMinimum = min(ringMinimum, lineNdc);
            ringMaximum = max(ringMaximum, lineNdc);
            for (int tubeSample = 0; tubeSample < 16; tubeSample++) {
                float phi = TAU * float(tubeSample) * (1.0 / 16.0);
                vec4 sampleClip = lineClip + radialClip * cos(phi) + verticalClip * sin(phi);
                vec2 sampleNdc = sampleClip.xy / sampleClip.w;
                boundMinimum = min(boundMinimum, sampleNdc);
                boundMaximum = max(boundMaximum, sampleNdc);
            }
        }
    }
    float projectedRadiusPx = max(
        (ringMaximum.x - ringMinimum.x) * u_ResolutionInfo.x,
        (ringMaximum.y - ringMinimum.y) * u_ResolutionInfo.y
    ) * 0.25;
    float reservePx = 12.50 + projectedRadiusPx * 0.00250;
    vec2 reserveNdc = reservePx * 2.0 * u_ResolutionInfo.zw;
    boundMinimum = clamp(boundMinimum - reserveNdc, vec2(-1.0), vec2(1.0));
    boundMaximum = clamp(boundMaximum + reserveNdc, vec2(-1.0), vec2(1.0));
    float visibility = safeVisibility
            * smoothstep(1.4, 5.5, projectedRadiusPx)
            * u_AuraParams.x
            * Color.a;
    vec2 cornerNdc = mix(boundMinimum, boundMaximum, UV0);
    cornerNdc = mix(centerNdc, cornerNdc, step(1.0e-5, visibility));
    gl_Position = vec4(cornerNdc, centerClip.z / max(centerClip.w, 1.0e-4), 1.0);
    vCenterView = centerView.xyz;
    vMajorXView = majorXView.xyz;
    vMajorZView = majorZView.xyz;
    vVerticalView = verticalView.xyz;
    vCenterClip = centerClip;
    vMajorXClip = majorXClip;
    vMajorZClip = majorZClip;
    vVerticalClip = verticalClip;
    vProjectedRadiusPx = projectedRadiusPx;
    vBoundVisibility = visibility;
}
