#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec2 aP0;
layout(location = 2) in vec2 aP1;
layout(location = 3) in vec2 aP2;
layout(location = 4) in vec2 aP3;
layout(location = 5) in vec4 aColorA;
layout(location = 6) in vec4 aColorB;
layout(location = 7) in vec4 aWidths;
layout(location = 8) in vec4 aParams;

uniform vec2 uViewport;

out vec2 vPixel;
out vec2 vP0;
out vec2 vP1;
out vec2 vP2;
out vec2 vP3;
out vec4 vColorA;
out vec4 vColorB;
out vec4 vWidths;
out vec4 vParams;

void main() {
    vec2 ndc = vec2(
        aPos.x / max(uViewport.x, 1.0) * 2.0 - 1.0,
        1.0 - aPos.y / max(uViewport.y, 1.0) * 2.0
    );
    vPixel = aPos;
    vP0 = aP0;
    vP1 = aP1;
    vP2 = aP2;
    vP3 = aP3;
    vColorA = aColorA;
    vColorB = aColorB;
    vWidths = aWidths;
    vParams = aParams;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
