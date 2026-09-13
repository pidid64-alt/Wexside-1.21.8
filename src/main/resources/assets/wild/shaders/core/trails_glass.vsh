#version 150

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

out vec3 vViewPos;
out vec3 vNormal;
out vec2 vUv;
out vec4 vColor;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * view;
    vViewPos = view.xyz;
    vNormal = normalize(mat3(ModelViewMat) * Normal);
    vUv = UV0;
    vColor = Color;
}
