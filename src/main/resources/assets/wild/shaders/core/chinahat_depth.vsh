#version 150

#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

invariant gl_Position;

in vec3 Position;

void main() {
    vec4 view = ModelViewMat * vec4(Position, 1.0);
    vec4 clip = ProjMat * view;
    gl_Position = clip;
}
