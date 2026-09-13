#version 330 core

layout(location = 0) in vec2 aPos;

uniform vec2 uViewport;
uniform vec4 uDrawRect;
uniform vec4 uBoxRect;

out vec2 vScreen;
out vec2 vBox;
out vec2 vDraw;

void main() {
    vec2 px = uDrawRect.xy + aPos * uDrawRect.zw;
    vec2 ndc = vec2(px.x / max(uViewport.x, 1.0) * 2.0 - 1.0, 1.0 - px.y / max(uViewport.y, 1.0) * 2.0);
    vScreen = px;
    vBox = (px - uBoxRect.xy) / max(uBoxRect.zw, vec2(1.0));
    vDraw = aPos;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
