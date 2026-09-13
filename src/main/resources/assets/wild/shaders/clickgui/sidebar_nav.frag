#version 330 core

in vec2 vScreen;
in vec2 vBox;
in vec2 vDraw;

uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform vec4 uMuted;
uniform vec4 uFill;
uniform vec4 uOutline;
uniform float uTime;
uniform float uAlpha;
uniform float uLightMode;
uniform float uHover;
uniform float uActive;
uniform float uPop;
uniform int uIcon;
uniform float uIconOnly;
uniform vec4 uBoxRect;

out vec4 FragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float sdCircle(vec2 p, float r) {
    return length(p) - r;
}

float sdRoundBox(vec2 p, vec2 b, float r) {
    vec2 q = abs(p) - b + r;
    return min(max(q.x, q.y), 0.0) + length(max(q, vec2(0.0))) - r;
}

float sdSeg(vec2 p, vec2 a, vec2 b) {
    vec2 pa = p - a;
    vec2 ba = b - a;
    float h = sat(dot(pa, ba) / dot(ba, ba));
    return length(pa - ba * h);
}

float smin(float a, float b, float k) {
    float h = sat(0.5 + 0.5 * (b - a) / k);
    return mix(b, a, h) - k * h * (1.0 - h);
}

float fillMask(float d) {
    float e = max(fwidth(d) * 0.75, 0.0001);
    return smoothstep(e, -e, d);
}

float chev(vec2 p, float ox, float w) {
    vec2 a = vec2(ox - 0.20, -0.44);
    vec2 b = vec2(ox + 0.22, 0.0);
    vec2 c = vec2(ox - 0.20, 0.44);
    return min(sdSeg(p, a, b), sdSeg(p, b, c)) - w;
}

void over(inout vec4 acc, vec3 c, float a) {
    acc.rgb = c * a + acc.rgb * (1.0 - a);
    acc.a = a + acc.a * (1.0 - a);
}

void main() {
    vec2 q = (vBox - 0.5) * 2.0 / max(uPop, 0.6);
    float pxPerQ = max(uBoxRect.z, 1.0) * 0.5 * max(uPop, 0.6);
    float emph = max(uHover * 0.6, uActive);

    float ds = sdRoundBox(q, vec2(1.0), 0.45);
    float dPx = ds * pxPerQ;
    float ePx = max(fwidth(dPx) * 0.75, 0.0001);
    float inside = smoothstep(ePx, -ePx, dPx);
    float outside = 1.0 - smoothstep(-ePx, ePx, dPx);

    vec4 acc = vec4(0.0);

    float quadEdge = min(min(vDraw.x, 1.0 - vDraw.x), min(vDraw.y, 1.0 - vDraw.y));
    if (uIconOnly < 0.5) {
        float haloShape = exp(-max(dPx, 0.0) / 9.0) * outside * smoothstep(0.0, 0.14, quadEdge);
        float haloA = haloShape * (uActive * 0.30 + uHover * 0.08);
        vec3 haloC = mix(uAccentBottom, uAccentTop, 0.35);
        if (uLightMode > 0.5) {
            haloC = vec3(0.05);
            haloA = haloShape * uActive * 0.10;
        }
        over(acc, haloC, haloA);

        vec3 accentV = mix(uAccentTop, uAccentBottom, sat(0.5 + q.y * 0.5));
        vec3 fillC = mix(uFill.rgb, accentV, uActive * 0.14 + uHover * 0.05);
        float fillA = sat(uFill.a + emph * 0.05) * inside;
        over(acc, fillC, fillA);

        float innerGlow = (1.0 - smoothstep(0.0, 1.15, length(q - vec2(0.0, 0.25)))) * uActive * 0.18 * inside;
        over(acc, mix(uAccentBottom, uAccentTop, 0.5), innerGlow);

        float sCoord = (q.x - q.y) * 0.25 + 0.5;
        float sPos = fract(uTime * 0.22) * 1.8 - 0.4;
        float sDelta = (sCoord - sPos) / 0.16;
        float sheenA = exp(-sDelta * sDelta) * uHover * 0.09 * inside;
        over(acc, vec3(1.0), sheenA);

        float borderMask = 1.0 - smoothstep(1.1 - ePx, 1.1 + ePx, abs(dPx));
        vec2 dir = normalize(q + vec2(0.0001));
        float lobe = pow(0.5 + 0.5 * dot(dir, vec2(cos(uTime * 1.1), sin(uTime * 1.1))), 3.0);
        float breath = 0.92 + 0.08 * sin(uTime * 2.2);
        vec3 borderC = mix(uOutline.rgb, mix(uAccentBottom, uAccentTop, lobe), max(uHover * 0.45, uActive));
        float borderA = mix(uOutline.a, (0.42 + 0.5 * lobe) * breath, uActive);
        borderA = max(borderA, uOutline.a);
        over(acc, borderC, borderMask * borderA);
    }

    float iconScale = mix(0.50, 0.68, step(0.5, uIconOnly));
    vec2 p = q / iconScale;
    float w = mix(0.09, 0.125, step(0.5, uIconOnly));
    float d = 10.0;
    float iconA = 1.0;

    if (uIcon == 0) {
        float ring = abs(sdCircle(p, 0.58)) - w;
        float ticks = min(min(sdSeg(p, vec2(0.0, 0.70), vec2(0.0, 0.97)), sdSeg(p, vec2(0.0, -0.70), vec2(0.0, -0.97))),
                          min(sdSeg(p, vec2(0.70, 0.0), vec2(0.97, 0.0)), sdSeg(p, vec2(-0.70, 0.0), vec2(-0.97, 0.0)))) - w;
        float dotC = sdCircle(p, 0.13 + 0.05 * uActive);
        d = min(min(ring, ticks), dotC);
    } else if (uIcon == 1) {
        float shim = max(uHover, uActive);
        float c0 = chev(p, -0.50, w);
        float c1 = chev(p, 0.02, w);
        float c2 = chev(p, 0.54, w);
        float a0 = fillMask(c0) * mix(1.0, 0.55 + 0.45 * sin(uTime * 3.2), shim);
        float a1 = fillMask(c1) * mix(1.0, 0.55 + 0.45 * sin(uTime * 3.2 - 1.25), shim);
        float a2 = fillMask(c2) * mix(1.0, 0.55 + 0.45 * sin(uTime * 3.2 - 2.5), shim);
        float shimA = sat(a0 + a1 + a2);
        vec3 iCol = mix(uMuted.rgb, mix(uAccentTop, uAccentBottom, sat(0.5 + p.y * 0.55)), emph);
        float iA = mix(uMuted.a, 1.0, emph);
        over(acc, iCol, shimA * iA * inside);
        acc.rgb *= uAlpha;
        acc.a *= uAlpha;
        if (acc.a <= 0.002) {
            discard;
        }
        FragColor = acc;
        return;
    } else if (uIcon == 2) {
        float c1 = sdCircle(p - vec2(0.0, 0.62), 1.02);
        float c2 = sdCircle(p + vec2(0.0, 0.62), 1.02);
        float lid = abs(max(c1, c2)) - w;
        float pupil = sdCircle(p, 0.20 + 0.05 * uActive);
        float glint = sdCircle(p - vec2(0.07, -0.07), 0.07);
        pupil = max(pupil, -glint);
        d = min(lid, pupil);
    } else if (uIcon == 3) {
        float head = sdCircle(p - vec2(0.0, -0.46), 0.27);
        float body = sdRoundBox(p - vec2(0.0, 0.44), vec2(0.44, 0.30), 0.29);
        d = smin(head, body, 0.10);
    } else if (uIcon == 4) {
        float slide = smoothstep(0.0, 1.0, uHover);
        float tracks = min(min(sdSeg(p, vec2(-0.60, -0.50), vec2(0.60, -0.50)), sdSeg(p, vec2(-0.60, 0.0), vec2(0.60, 0.0))), sdSeg(p, vec2(-0.60, 0.50), vec2(0.60, 0.50))) - 0.045;
        float k1 = sdCircle(p - vec2(-0.18 + 0.28 * slide, -0.50), 0.14);
        float k2 = sdCircle(p - vec2(0.25 - 0.34 * slide, 0.0), 0.14);
        float k3 = sdCircle(p - vec2(-0.05 + 0.24 * slide, 0.50), 0.14);
        d = min(min(tracks, k1), min(k2, k3));
    } else if (uIcon == 5) {
        float h1 = sdSeg(p, vec2(-0.85, -0.52), vec2(-0.55, -0.52)) - w;
        float h2 = sdSeg(p, vec2(-0.55, -0.52), vec2(-0.38, 0.16)) - w;
        float top = sdSeg(p, vec2(-0.45, -0.28), vec2(0.72, -0.28)) - w;
        float rs = sdSeg(p, vec2(0.72, -0.28), vec2(0.56, 0.16)) - w;
        float bt = sdSeg(p, vec2(-0.38, 0.16), vec2(0.56, 0.16)) - w;
        float w1 = sdCircle(p - vec2(-0.16, 0.52), 0.14);
        float w2 = sdCircle(p - vec2(0.40, 0.52), 0.14);
        d = min(min(min(h1, h2), min(top, rs)), min(bt, min(w1, w2)));
    } else {
        float head = sdRoundBox(p - vec2(0.0, -0.05), vec2(0.62, 0.48), 0.20);
        float antenna = sdSeg(p, vec2(0.0, -0.54), vec2(0.0, -0.82)) - w;
        float antennaDot = sdCircle(p - vec2(0.0, -0.91), 0.11);
        float earL = sdRoundBox(p - vec2(-0.75, -0.03), vec2(0.12, 0.25), 0.08);
        float earR = sdRoundBox(p - vec2(0.75, -0.03), vec2(0.12, 0.25), 0.08);
        float eyeL = sdCircle(p - vec2(-0.24, -0.10), 0.09);
        float eyeR = sdCircle(p - vec2(0.24, -0.10), 0.09);
        float mouth = sdSeg(p, vec2(-0.25, 0.22), vec2(0.25, 0.22)) - w * 0.72;
        float shell = min(min(abs(head) - w, antenna), min(abs(earL) - w, abs(earR) - w));
        d = min(shell, min(min(antennaDot, eyeL), min(eyeR, mouth)));
    }

    float iconMask = fillMask(d) * iconA;
    vec3 iconGradient = mix(uAccentTop, uAccentBottom, sat(0.5 + p.y * 0.55));
    iconGradient = mix(iconGradient, mix(uAccentTop, uAccentBottom, 0.28), step(0.5, uIconOnly));
    vec3 iCol = mix(uMuted.rgb, iconGradient, emph);
    float iA = mix(uMuted.a, 1.0, emph);
    over(acc, iCol, iconMask * iA * inside);

    acc.rgb *= uAlpha;
    acc.a *= uAlpha;
    if (acc.a <= 0.002) {
        discard;
    }
    FragColor = acc;
}
