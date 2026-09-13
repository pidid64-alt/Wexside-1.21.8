#version 150

#moj_import <minecraft:globals.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in vec3 Normal;

out vec2 vUv;
out vec4 vColor;
out float vDistance;
out float vKind;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    gl_Position = ProjMat * view;
    vUv = UV0;
    vColor = Color;
    vDistance = length(view.xyz);
    vKind = Normal.x;
}
