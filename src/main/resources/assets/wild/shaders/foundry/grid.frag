#version 330 core

in vec2 vUv;

uniform vec2 uResolution;
uniform vec2 uPan;
uniform float uZoom;
uniform vec2 uMouse;
uniform vec2 uSpringMouse;
uniform vec2 uMouseVelocity;
uniform float uMagnetEnergy;
uniform float uTime;
uniform float uAlpha;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uLightMode;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float dotMask(float d, float r) {
    float aa = clamp(fwidth(d), 0.72, 1.6);
    return 1.0 - smoothstep(r - aa, r + aa, d);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

void main() {
    vec2 pixel = vec2(vUv.x * uResolution.x, (1.0 - vUv.y) * uResolution.y);
    float zoom = max(uZoom, 0.001);
    float spacingWorld = 24.0;
    vec2 world = (pixel - uPan) / zoom;
    vec2 gridWorld = floor(world / spacingWorld + 0.5) * spacingWorld;
    vec2 gridScreen = uPan + gridWorld * zoom;
    vec2 delta = pixel - gridScreen;
    float d = length(delta);
    float mouseDistance = length(gridScreen - uSpringMouse);
    float raw = 1.0 - smoothstep(18.0, 168.0, mouseDistance);
    float proximity = exp(-pow(mouseDistance / 126.0, 2.0));
    float energy = sat(uMagnetEnergy);
    float attraction = sat(max(raw, proximity) * energy);
    float velocity = sat(length(uMouseVelocity) * 0.0035);
    vec2 gridId = floor(abs(gridWorld / spacingWorld) + 0.5);
    float majorX = 1.0 - step(0.5, mod(gridId.x, 4.0));
    float majorY = 1.0 - step(0.5, mod(gridId.y, 4.0));
    float major = max(majorX, majorY);
    float seed = hash12(gridWorld * 0.03125);
    float rest = mix(0.66, 0.98, seed) + major * 0.24;
    float radius = min(2.08, rest + pow(attraction, 0.58) * (0.86 + velocity * 0.23));
    vec2 pullDir = normalize(gridScreen - uSpringMouse + vec2(0.0001));
    float orbital = sin(dot(gridWorld, vec2(0.031, 0.047)) + uTime * 1.7) * 0.16 * attraction;
    float warpedD = length(delta + pullDir * orbital);
    float mask = dotMask(warpedD, radius);
    vec2 uv = pixel / max(uResolution, vec2(1.0));
    vec2 vp = uv - 0.5;
    vp.x *= uResolution.x / max(uResolution.y, 1.0);
    float vignette = sat(1.0 - dot(vp, vp) * 0.55);
    vec3 darkBase = mix(vec3(0.12, 0.145, 0.18), mix(uAccentBottom, uAccentTop, 0.48), 0.22 + attraction * 0.18);
    vec3 lightBase = mix(vec3(0.38, 0.43, 0.50), mix(uAccentBottom, uAccentTop, 0.52), 0.15 + attraction * 0.12);
    vec3 color = mix(darkBase, lightBase, uLightMode);
    color += mix(uAccentBottom, uAccentTop, seed) * attraction * 0.085;
    color += vec3(1.0) * mask * attraction * 0.026;
    float alpha = mask * (0.092 + major * 0.044 + attraction * 0.165) * (0.50 + vignette * 0.50) * uAlpha;
    fragColor = vec4(clamp(color, 0.0, 1.0), sat(alpha));
}
