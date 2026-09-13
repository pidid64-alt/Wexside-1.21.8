#version 330 core

in vec2 vUv;

uniform vec2 uResolution;
uniform float uTime;
uniform vec4 uDrawRect;
uniform vec4 uElementRect;
uniform float uRadius;
uniform float uAlpha;
uniform float uInset;
uniform vec4 uSurfaceColor;
uniform vec4 uOutlineColor;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform vec2 uMouse;
uniform float uShadow;
uniform float uOutline;
uniform float uLightMode;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

float hash12(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    float a = hash12(i);
    float b = hash12(i + vec2(1.0, 0.0));
    float c = hash12(i + vec2(0.0, 1.0));
    float d = hash12(i + vec2(1.0, 1.0));
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += noise(p) * a;
        p = mat2(1.62, -1.18, 1.18, 1.62) * p + vec2(7.13, -3.71);
        a *= 0.5;
    }
    return v;
}

float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    vec2 h = max(halfSize, vec2(0.5));
    float r = clamp(radius, 0.0, min(h.x, h.y));
    vec2 q = abs(p) - h + vec2(r);
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
    vec2 pixel = vec2(vUv.x * uResolution.x, (1.0 - vUv.y) * uResolution.y);
    vec2 clipMin = uDrawRect.xy;
    vec2 clipMax = uDrawRect.xy + uDrawRect.zw;
    vec2 clipEdge = min(pixel - clipMin, clipMax - pixel);
    float clipMask = smoothstep(-1.0, 2.0, min(clipEdge.x, clipEdge.y));
    if (clipMask <= 0.001) {
        discard;
    }

    vec2 center = uElementRect.xy + uElementRect.zw * 0.5;
    vec2 local = pixel - uElementRect.xy;
    vec2 uv = local / max(uElementRect.zw, vec2(1.0));
    float insetPx = uInset * max(1.0, min(uElementRect.z, uElementRect.w) * 0.055);
    vec2 halfSize = max(vec2(1.0), uElementRect.zw * 0.5 - vec2(insetPx));
    float radius = max(0.0, uRadius - insetPx);
    float d = sdRoundBox(pixel - center, halfSize, radius);
    float aa = max(fwidth(d), 1.0);
    float fill = 1.0 - smoothstep(0.0, aa, d);
    float outside = max(d, 0.0);
    float shadow = exp(-(outside * outside) / 520.0) * (1.0 - smoothstep(0.0, 34.0, outside)) * uShadow;
    float edge = exp(-(d * d) / max(2.0 * aa * aa + 2.0, 0.001));
    float innerEdge = exp(-pow(max(-d, 0.0) / 7.5, 2.0)) * fill;
    float vertical = sat(uv.y);
    vec3 themeAccent = mix(uAccentTop, uAccentBottom, vertical);
    vec3 spectralA = vec3(0.18, 0.58, 1.00);
    vec3 spectralB = vec3(0.78, 0.36, 1.00);
    vec3 spectralC = vec3(0.22, 1.00, 0.82);
    float t = uTime * 0.55;
    vec2 aspectUv = (uv - 0.5) * vec2(uElementRect.z / max(uElementRect.w, 1.0), 1.0);
    float n0 = fbm(uv * vec2(4.2, 3.1) + vec2(t * 0.19, -t * 0.11));
    float n1 = fbm(uv * vec2(14.0, 9.0) + vec2(-t * 0.48, t * 0.31));
    float n2 = fbm(pixel * 0.072 + vec2(t * 0.65, -t * 0.23));
    float swirl = sin(atan(aspectUv.y, aspectUv.x) * 3.0 + length(aspectUv) * 9.0 - t * 2.2 + n0 * 4.8);
    float wave = sin(pixel.x * 0.018 + pixel.y * 0.033 + t * 2.9 + n0 * 5.4);
    float veins = sin((pixel.x * 0.042 - pixel.y * 0.027) + t * 3.4 + n1 * 7.0 + swirl * 1.2);
    float ferroCells = pow(sat(0.52 + 0.48 * veins), 7.2) * (0.30 + 0.70 * n2);
    float oilFilm = pow(sat(0.45 + 0.55 * sin(n0 * 5.2 + n1 * 3.4 + vertical * 2.6 - t * 1.1)), 3.4);
    float ribbonA = pow(sat(0.50 + 0.50 * sin((uv.x * 8.2 - uv.y * 3.6) + n0 * 5.6 + t * 1.55)), 4.2);
    float ribbonB = pow(sat(0.50 + 0.50 * sin((uv.x * -4.8 + uv.y * 9.4) + n1 * 4.2 - t * 1.85)), 5.4);
    float liquid = sat(max(ferroCells * 1.22, ribbonA * 0.62 + ribbonB * 0.46) * fill);
    vec3 chroma = mix(mix(spectralA, spectralB, ribbonA), spectralC, ribbonB * 0.72);
    vec3 prism = mix(mix(spectralA, spectralB, vertical), chroma, oilFilm * 0.48 + liquid * 0.20);
    vec3 accent = mix(themeAccent, prism, 0.20 + liquid * 0.16);
    float mica = (n0 - 0.5) * 0.030 + (n2 - 0.5) * 0.018 + wave * 0.010;
    vec2 mouseUv = clamp((uMouse - uElementRect.xy) / max(uElementRect.zw, vec2(1.0)), vec2(0.0), vec2(1.0));
    float mouseInside = step(uElementRect.x, uMouse.x) * step(uMouse.x, uElementRect.x + uElementRect.z) * step(uElementRect.y, uMouse.y) * step(uMouse.y, uElementRect.y + uElementRect.w);
    vec2 magneticDelta = (uv - mouseUv) * vec2(uElementRect.z / max(uElementRect.w, 1.0), 1.0);
    float magnetic = exp(-dot(magneticDelta, magneticDelta) / 0.040) * mouseInside * fill;
    float topSheen = smoothstep(0.62, 0.0, uv.y) * smoothstep(0.0, 0.22, uv.x) * smoothstep(1.0, 0.78, uv.x);
    float bottomDepth = smoothstep(0.50, 1.0, uv.y);
    float meniscusG = d / max(aa * 1.45, 1.0);
    float meniscus = exp(-meniscusG * meniscusG);
    float innerMeniscus = exp(-pow(max(-d, 0.0) / 5.4, 2.0)) * fill;
    float outerAura = exp(-pow(max(d, 0.0) / 14.0, 2.0)) * (1.0 - fill);
    float edgeX = max(exp(-(uv.x / 0.040) * (uv.x / 0.040)), exp(-((1.0 - uv.x) / 0.040) * ((1.0 - uv.x) / 0.040))) * fill;
    float edgeY = max(exp(-(uv.y / 0.055) * (uv.y / 0.055)), exp(-((1.0 - uv.y) / 0.055) * ((1.0 - uv.y) / 0.055))) * fill;
    float rimField = max(edgeX, edgeY) * (0.46 + ferroCells * 0.42 + oilFilm * 0.12);
    float specBand = pow(sat(0.5 + 0.5 * sin((uv.x - uv.y) * 8.0 + n0 * 4.0 - t * 1.6)), 6.0) * fill;
    float light = sat(uLightMode);
    float fresnel = pow(sat(1.0 - abs(dot(normalize(vec3(dFdx(d), dFdy(d), 1.55)), vec3(0.0, 0.0, 1.0)))), 2.0);
    vec3 darkBase = mix(vec3(0.018, 0.020, 0.030), uSurfaceColor.rgb + themeAccent * 0.030, 0.92);
    vec3 milk = vec3(0.982, 0.992, 1.0) + mix(uAccentTop, uAccentBottom, 0.5) * 0.032;
    vec3 lightBase = mix(milk, uSurfaceColor.rgb + themeAccent * 0.120, 0.34);
    vec3 darkColor = darkBase + mica * 0.82;
    darkColor += accent * (innerMeniscus * 0.126 + magnetic * 0.270 + rimField * 0.250 + ferroCells * 0.092 + liquid * 0.105);
    darkColor += mix(themeAccent, prism, 0.34) * oilFilm * specBand * 0.185;
    darkColor += chroma * liquid * (0.040 + rimField * 0.065);
    darkColor += vec3(0.92, 0.98, 1.0) * (topSheen * 0.060 + meniscus * 0.036 + magnetic * 0.055);
    darkColor += themeAccent * outerAura * uOutline * 0.165;
    darkColor += mix(uAccentTop, uAccentBottom, sat(wave * 0.5 + 0.5)) * fill * oilFilm * 0.048;
    darkColor -= vec3(0.018, 0.018, 0.022) * bottomDepth * (0.22 + uInset * 0.16);
    vec3 lightColor = lightBase + mica * 0.34;
    lightColor += accent * (innerMeniscus * 0.102 + magnetic * 0.210 + rimField * 0.190 + ferroCells * 0.070 + fresnel * 0.205 + liquid * 0.118);
    lightColor += mix(themeAccent, prism, 0.45) * oilFilm * specBand * 0.155;
    lightColor += chroma * liquid * (0.045 + oilFilm * 0.060);
    lightColor += vec3(1.0) * (topSheen * 0.088 + meniscus * 0.074 + magnetic * 0.055 + fresnel * 0.064);
    lightColor -= vec3(0.028, 0.034, 0.045) * bottomDepth * (0.15 + uInset * 0.10);
    vec3 color = mix(darkColor, lightColor, light);
    color = mix(color, uOutlineColor.rgb, meniscus * uOutline * uOutlineColor.a * mix(0.06, 0.10, light));
    color = mix(color, mix(vec3(0.018, 0.016, 0.022), vec3(0.105, 0.125, 0.160), light), sat(shadow * (1.0 - fill) * mix(0.65, 1.05, light)));

    float rimAlpha = (meniscus * mix(0.086, 0.122, light) + rimField * mix(0.094, 0.074, light) + outerAura * mix(0.160, 0.084, light) + fresnel * fill * 0.036 * light + liquid * 0.014) * uOutline;
    float alpha = fill * mix(uSurfaceColor.a, max(uSurfaceColor.a, 0.52) * 0.76, light) * uAlpha + rimAlpha * uAlpha + shadow * mix(0.24, 0.16, light) * uAlpha + magnetic * mix(0.034, 0.026, light) * uAlpha + oilFilm * fill * mix(0.012, 0.022, light) * uAlpha;
    alpha *= clipMask;
    if (alpha <= 0.001) {
        discard;
    }
    fragColor = vec4(clamp(color, 0.0, 1.0), sat(alpha));
}
