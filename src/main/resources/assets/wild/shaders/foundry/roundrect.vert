#version 330 core
layout(location = 0) in vec2 aPos;

uniform vec2 uViewport;
uniform vec4 uRect;

out vec2 vUv;
out vec2 vLocal;
out vec2 vScreen;

void main() {
    vec2 px = uRect.xy + aPos * uRect.zw;
    vec2 ndc = vec2(px.x / max(uViewport.x, 1.0) * 2.0 - 1.0, 1.0 - px.y / max(uViewport.y, 1.0) * 2.0);
    vLocal = px - uRect.xy;
    vUv = vLocal / max(uRect.zw, vec2(1.0));
    vScreen = px;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
