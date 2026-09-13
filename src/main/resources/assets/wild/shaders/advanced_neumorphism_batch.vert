#version 330 core

const int MAX_PLATES = 128;

uniform vec2 uViewport;

layout(std140) uniform NeumorphicPlateBlock {
    vec4 b_DrawRect[MAX_PLATES];
    vec4 b_ElementRect[MAX_PLATES];
    vec4 b_Style[MAX_PLATES];
    vec4 b_Base[MAX_PLATES];
    vec4 b_Dark[MAX_PLATES];
    vec4 b_Light[MAX_PLATES];
    vec4 b_Flags[MAX_PLATES];
};

out vec2 vScreen;
flat out int vPlateIndex;

vec2 vertexPosition(int id) {
    if (id == 0) return vec2(0.0, 0.0);
    if (id == 1) return vec2(1.0, 0.0);
    if (id == 2) return vec2(1.0, 1.0);
    if (id == 3) return vec2(0.0, 0.0);
    if (id == 4) return vec2(1.0, 1.0);
    return vec2(0.0, 1.0);
}

void main() {
    int index = gl_InstanceID;
    vec2 aPos = vertexPosition(gl_VertexID);
    vec4 rect = b_DrawRect[index];
    vec2 px = rect.xy + aPos * rect.zw;
    vec2 viewport = max(uViewport, vec2(1.0));
    vec2 ndc = vec2(px.x / viewport.x * 2.0 - 1.0, 1.0 - px.y / viewport.y * 2.0);
    vScreen = px;
    vPlateIndex = index;
    gl_Position = vec4(ndc, 0.0, 1.0);
}
