#version 150

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

layout(std140) uniform JumpCircle {
    vec4 u_Params;
};

out vec3 vViewPos;
out vec3 vNormal;
out vec2 vUv;
out vec4 vColor;
out vec4 vClip;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    vec4 clip = ProjMat * view;
    gl_Position = clip;
    vViewPos = view.xyz;
    vNormal = normalize(mat3(ModelViewMat) * Normal);
    vUv = UV0;
    vColor = Color;
    vClip = clip;
}
