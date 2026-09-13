#version 150

#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec2 UV0;
in vec4 Color;
in ivec2 UV2;
in vec3 Normal;

layout(std140) uniform BlockEsp {
    mat4 EspView;
    vec4 EspCamera;
    vec4 EspFade;
    vec4 EspStyle;
    vec4 EspTune;
    vec4 EspRange;
    vec4 EspLocal;
    vec4 EspSurface;
};

out vec2 vFace;
out vec3 vTint;
out vec3 vView;
out float vRise;
flat out vec3 vNormal;
flat out float vPhase;
flat out float vEvent;
flat out int vFlags;

void main() {
    vec3 rel = Position - EspCamera.xyz;
    gl_Position = ProjMat * EspView * vec4(rel, 1.0);

    vFace = UV0;
    vTint = Color.rgb;
    vPhase = Color.a;
    vNormal = Normal;
    vView = -rel;

    int bits = UV2.x & 0xFFFF;
    vFlags = bits & 31;
    vRise = float((bits >> 8) & 255) * (1.0 / 255.0);

    int ticks = UV2.y & 0xFFFF;
    vEvent = ticks == 65535 ? -1.0 : float(ticks) * (1.0 / 128.0);
}
