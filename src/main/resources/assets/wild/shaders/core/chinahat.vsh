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

out vec3 vViewPos;
out vec3 vNormal;
out vec2 vUv;
out float vCoverage;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    vec4 clip = ProjMat * view;
    gl_Position = clip;
    vViewPos = view.xyz;
    vNormal = normalize(mat3(ModelViewMat) * Normal);
    vUv = UV0;
    vCoverage = Color.a;
}
