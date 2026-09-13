#version 330 core

in vec2 vUnit;

uniform sampler2D uTexture;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uTime;
uniform float uAlpha;
uniform float uLightMode;

out vec4 FragColor;

void over(inout vec4 acc, vec3 c, float a) {
    acc.rgb = c * a + acc.rgb * (1.0 - a);
    acc.a = a + acc.a * (1.0 - a);
}

void main() {
    vec2 q = (vUnit - 0.5) * 3.2;
    float r = length(q);
    float e = max(fwidth(r), 0.0015);
    vec2 dir = q / max(r, 0.0001);
    float lobe = pow(0.5 + 0.5 * dot(dir, vec2(cos(uTime * 1.1), sin(uTime * 1.1))), 3.0);
    float breath = 0.92 + 0.08 * sin(uTime * 2.2);
    float quadEdge = min(min(vUnit.x, 1.0 - vUnit.x), min(vUnit.y, 1.0 - vUnit.y));

    vec4 acc = vec4(0.0);

    float haloShape = exp(-max(r - 1.14, 0.0) / 0.20) * smoothstep(1.14 - e, 1.14 + e, r) * smoothstep(0.0, 0.14, quadEdge);
    vec3 orbitC = mix(uAccentBottom, uAccentTop, lobe);
    float haloA = haloShape * (0.10 + 0.30 * lobe) * breath;
    if (uLightMode > 0.5) {
        haloA *= 0.45;
    }
    over(acc, orbitC, haloA);

    float ringD = abs(r - 1.14) - 0.055;
    float ringMask = smoothstep(e, -e, ringD);
    float ringFloor = uLightMode > 0.5 ? 0.55 : 0.34;
    float ringA = ringMask * mix(ringFloor, 1.0, lobe) * breath;
    over(acc, orbitC, ringA);

    float photoMask = smoothstep(e, -e, r - 1.0);
    vec2 uv = q * 0.5 + 0.5;
    vec3 col = texture(uTexture, uv).rgb;
    float rim = smoothstep(0.80, 1.0, r);
    col = mix(col, mix(uAccentBottom, uAccentTop, 0.5), rim * 0.20);
    col *= mix(1.0, 0.88, smoothstep(0.60, 1.0, r));
    over(acc, col, photoMask);

    acc.rgb *= uAlpha;
    acc.a *= uAlpha;
    if (acc.a <= 0.002) {
        discard;
    }
    FragColor = acc;
}
