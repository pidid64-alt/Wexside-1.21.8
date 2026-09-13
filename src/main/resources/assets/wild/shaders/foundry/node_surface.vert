#version 330 core
layout(location = 0) in vec2 aPos;
layout(location = 1) in vec2 aLocal;
layout(location = 2) in vec4 aShape;
layout(location = 3) in vec4 aAccentTop;
layout(location = 4) in vec4 aAccentBottom;
layout(location = 5) in vec4 aBase;
layout(location = 6) in vec4 aParams;
layout(location = 7) in vec2 aMouse;

uniform vec2 uViewport;

out vec2 vLocal;
out vec4 vShape;
out vec4 vAccentTop;
out vec4 vAccentBottom;
out vec4 vBase;
out vec4 vParams;
out vec2 vMouse;
out vec2 vPixel;

void main() {
    vec2 ndc = vec2(
        aPos.x / max(uViewport.x, 1.0) * 2.0 - 1.0,
        1.0 - aPos.y / max(uViewport.y, 1.0) * 2.0
    );
    vLocal = aLocal;
    vShape = aShape;
    vAccentTop = aAccentTop;
    vAccentBottom = aAccentBottom;
    vBase = aBase;
    vParams = aParams;
    vMouse = aMouse;
    vPixel = aPos;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
