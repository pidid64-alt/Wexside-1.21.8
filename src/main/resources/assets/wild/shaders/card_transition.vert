#version 330 core

layout(location = 0) in vec2 aPos;
layout(location = 1) in vec2 aUv;

uniform vec2 u_viewport;
uniform vec2 u_resolution;

out vec2 v_uv;
out vec2 v_local;
out vec2 v_pos;

void main() {
    v_uv = aUv;
    v_local = vec2(aUv.x * u_resolution.x, (1.0 - aUv.y) * u_resolution.y);
    v_pos = aPos;

    vec2 ndc = vec2((aPos.x / u_viewport.x) * 2.0 - 1.0,
                    1.0 - (aPos.y / u_viewport.y) * 2.0);
    gl_Position = vec4(ndc, 0.0, 1.0);
}
