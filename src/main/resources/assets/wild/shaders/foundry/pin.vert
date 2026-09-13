#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec2 aCenter;
layout(location = 2) in vec2 aRadii;
layout(location = 3) in vec4 aColor;
layout(location = 4) in vec4 aInnerColor;
layout(location = 5) in vec4 aParams;

uniform vec2 uViewport;

out vec2 vPixel;
out vec2 vCenter;
out vec2 vRadii;
out vec4 vColor;
out vec4 vInnerColor;
out vec4 vParams;

void main() {
    vec2 ndc = vec2(
        aPos.x / max(uViewport.x, 1.0) * 2.0 - 1.0,
        1.0 - aPos.y / max(uViewport.y, 1.0) * 2.0
    );
    vPixel = aPos;
    vCenter = aCenter;
    vRadii = aRadii;
    vColor = aColor;
    vInnerColor = aInnerColor;
    vParams = aParams;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
