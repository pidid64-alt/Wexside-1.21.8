#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uBackground;
uniform vec2 uViewport;
uniform vec2 uTextureSize;
uniform vec2 uSourceScale;
uniform vec4 uContent;
uniform vec4 uRow;
uniform vec2 uPointerLocal;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform float uRadius;
uniform float uRowRadius;
uniform float uRowGlow;
uniform float uGlow;
uniform float uHover;
uniform float uEntry;
uniform float uReveal;
uniform float uRevealDir;
uniform float uTime;
uniform float uLightMode;
uniform vec4 uChevron;
uniform float uChevronDir;
uniform float uChevronAlpha;
uniform float uScrim;
uniform float uDensity;

out vec4 FragColor;

const float PI = 3.14159265;
const float TAU = 6.28318531;
const vec3 LUMA = vec3(0.2126, 0.7152, 0.0722);

const vec3 BODY_VEIL = vec3(0.0455, 0.0440, 0.0710);
const float BODY_ALPHA = 0.76;
const float BODY_TRANSMIT = 0.34;
const float VEIL_ACCENT = 0.070;
const float VEIL_TOP_LIFT = 1.28;
const float VEIL_BOTTOM_SINK = 0.80;

const float DENSE_TRANSMIT = 0.062;
const float DENSE_TOP_LIFT = 1.80;
const float DENSE_BOTTOM_SINK = 1.17;
const float DENSE_LIGHT_MIX = 0.984;
const float DENSE_BLUR_PX = 27.0;
const float DENSE_BLUR_MIX = 0.93;
const float DENSE_DESAT = 0.34;
const float DENSE_MIX_LO = 0.42;
const float DENSE_MIX_HI = 0.78;
const float DENSE_RIM = 1.34;
const float DENSE_FRESNEL = 1.30;
const float DENSE_CAUSTIC = 1.35;
const float SHEEN_PX = 26.0;
const float SHEEN_GAIN = 0.088;

const float LENS_WIDTH_PX = 9.0;
const float LENS_SLOPE = 0.86;
const float LENS_SLOPE_CEIL = 2.80;
const float REFRACT_PX = 4.6;
const float CAUSTIC_CENTER = 0.76;
const float CAUSTIC_SIGMA = 0.36;
const float CAUSTIC_GAIN = 0.110;

const float RIM_WIDTH_PX = 1.25;
const float RIM_FLOOR = 0.185;
const float RIM_LIT = 0.520;
const float HAIRLINE_PX = 0.80;
const float HAIRLINE_GAIN = 0.150;
const float FRESNEL_GAIN = 0.110;
const float CREST_RATIO = 0.130;
const float KEY_X_RATIO = 0.62;
const float KEY_LIFT = 0.85;
const float KEY_CREST = 0.80;

const float ROW_TINT = 0.115;
const float ROW_RIM = 0.300;
const float ROW_ACCENT = 0.420;
const float ROW_LENS_PX = 5.2;
const float ROW_SLOPE = 0.78;
const float ROW_SLOPE_CEIL = 2.20;
const float ROW_REFRACT_PX = 3.1;
const float ROW_RIM_PX = 1.00;
const float ROW_SPEC_POW = 26.0;
const float ROW_SPEC_GAIN = 0.460;
const float ROW_FRESNEL = 0.130;
const float ROW_LIGHT_TILT = 0.82;
const float ROW_PROX_PX = 90.0;

const float CHEVRON_THICK_PX = 1.05;
const float CHEVRON_ALPHA = 0.780;
const float CHEVRON_HOVER = 0.220;
const float CHEV_SLOPE = 0.92;
const float CHEV_SLOPE_CEIL = 2.40;
const float CHEV_SPEC_POW = 20.0;
const float CHEV_SPEC_GAIN = 0.520;
const float CHEV_FRESNEL = 0.200;
const float CHEV_HALO_PX = 2.6;
const float CHEV_HALO_GAIN = 0.340;
const float CHEV_HALO_TINT = 0.550;

const vec3 SCRIM_VEIL_DARK = vec3(0.0250, 0.0265, 0.0455);
const vec3 SCRIM_VEIL_LIGHT = vec3(0.884, 0.886, 0.906);
const float SCRIM_MIX_DARK = 0.560;
const float SCRIM_MIX_LIGHT = 0.520;
const float SCRIM_DESAT = 0.340;
const float SCRIM_ACCENT = 0.045;

float sq(float x) {
    return x * x;
}

float pow5(float x) {
    float a = x * x;
    return a * a * x;
}

vec3 roundBoxField(vec2 p, vec2 b, float r) {
    r = min(r, min(b.x, b.y));
    vec2 s = vec2(p.x < 0.0 ? -1.0 : 1.0, p.y < 0.0 ? -1.0 : 1.0);
    vec2 q = abs(p) - b + r;
    if (max(q.x, q.y) > 0.0) {
        vec2 m = max(q, vec2(0.0));
        float l = length(m);
        vec2 g = l > 1.0e-5 ? m / l : vec2(0.0, 1.0);
        return vec3(s * g, l - r);
    }
    vec2 g = q.x > q.y ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
    return vec3(s * g, max(q.x, q.y) - r);
}

vec3 segField(vec2 p, vec2 a, vec2 b) {
    vec2 pa = p - a;
    vec2 ba = b - a;
    float h = clamp(dot(pa, ba) / max(dot(ba, ba), 1.0e-6), 0.0, 1.0);
    vec2 d = pa - ba * h;
    float l = length(d);
    return vec3(l > 1.0e-5 ? d / l : vec2(0.0, 1.0), l);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

vec2 region() {
    return uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
}

vec3 blurred(vec2 uv) {
    vec2 texel = 1.0 / max(uTextureSize, vec2(1.0));
    vec2 r = region();
    return textureLod(uBackground, clamp(uv * r, texel * 0.5, r - texel * 0.5), 0.0).rgb;
}

vec3 denseBlur(vec2 uv, float radiusPx, float dither) {
    vec2 step_ = vec2(radiusPx) / max(uViewport, vec2(1.0));
    float ang = dither * TAU;
    vec2 ca = vec2(cos(ang), sin(ang));
    vec2 cb = vec2(-ca.y, ca.x);
    vec3 s = blurred(uv + ca * step_);
    s += blurred(uv - ca * (step_ * 0.68));
    s += blurred(uv + cb * (step_ * 0.82));
    s += blurred(uv - cb * step_);
    return s * 0.25;
}

vec3 frostBackdrop(vec2 uv, float radiusPx, float dither, float w) {
    vec3 c = blurred(uv);
    if (w > 0.0) {
        c = mix(c, denseBlur(uv, radiusPx, dither), DENSE_BLUR_MIX * w);
        c = mix(c, vec3(dot(c, LUMA)), DENSE_DESAT * w);
    }
    return c;
}

vec3 addLight(vec3 base, vec3 light) {
    return base + light * max(vec3(0.0), vec3(1.0) - base);
}

vec3 accentAt(float t) {
    return mix(uAccentTop, uAccentBottom, clamp(t, 0.0, 1.0));
}

vec3 accentNeon(float t, float peakTarget) {
    vec3 c = accentAt(t);
    float peak = max(max(c.r, c.g), c.b);
    return c * (peak > 1.0e-4 ? peakTarget / peak : 1.0);
}

void main() {
    float entry = smoothstep(0.0, 1.0, uEntry);
    if (entry <= 0.002) {
        discard;
    }

    float ign = interleavedGradient(vScreen);
    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));

    if (uScrim > 0.004) {
        vec3 capture = blurred(uv);
        float capLuma = dot(capture, LUMA);
        vec3 accentVeil = accentAt(1.0 - uv.y);
        bool lightUi = uLightMode > 0.5;
        vec3 veil = mix(lightUi ? SCRIM_VEIL_LIGHT : SCRIM_VEIL_DARK, accentVeil, SCRIM_ACCENT);
        float strength = clamp(uGlow > 0.004 ? uGlow : 1.0, 0.0, 1.0);
        vec3 settled = mix(capture, vec3(capLuma), SCRIM_DESAT * strength);
        vec3 source = mix(veil, settled, smoothstep(0.0008, 0.0060, capLuma));
        vec3 veiled = mix(source, veil, (lightUi ? SCRIM_MIX_LIGHT : SCRIM_MIX_DARK) * strength);
        veiled += ign * (1.0 / 255.0);
        FragColor = vec4(veiled, clamp(uScrim, 0.0, 1.0) * entry);
        return;
    }

    float dens = clamp(uDensity, 0.0, 1.0);

    vec2 size = max(uContent.zw, vec2(1.0));
    vec2 half_ = size * 0.5;
    vec2 p = vLocal - uContent.xy - half_;

    float unit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;
    float radius = min(uRadius, min(half_.x, half_.y));
    vec3 field = roundBoxField(p, half_, radius);
    vec2 nOut = field.xy;
    float dC = field.z;
    float minHalf = min(half_.x, half_.y);
    float perimeter = 2.0 * (size.x + size.y);

    float aa = max(length(vec2(dFdx(dC), dFdy(dC))) * 0.70, 0.55);
    float pxScale = max(fwidth(vLocal.x), 0.35);
    float coverage = 1.0 - smoothstep(-aa, aa, dC);
    float dIn = max(-dC, 0.0);
    float ad = abs(dC);

    float reveal = clamp(uReveal, 0.0, 1.0);
    float revealMask = 1.0;
    if (uRevealDir < -0.5) {
        float edge = mix(size.y + 12.0, -12.0, reveal);
        revealMask = smoothstep(edge - 10.0, edge + 10.0, p.y + half_.y);
    } else if (uRevealDir > 0.5) {
        float edge = mix(-12.0, size.y + 12.0, reveal);
        revealMask = 1.0 - smoothstep(edge - 10.0, edge + 10.0, p.y + half_.y);
    }
    coverage *= revealMask;

    if (coverage <= 0.002) {
        discard;
    }

    float vertical = clamp((p.y + half_.y) / max(size.y, 1.0), 0.0, 1.0);
    vec3 accent = accentAt(vertical);
    vec3 accentHot = accentNeon(vertical, 0.94);

    vec2 lightPos = uPointerLocal;
    vec2 keyPos = vec2(-half_.x * KEY_X_RATIO, -(half_.y + minHalf * KEY_LIFT));
    float crestSigma = max(perimeter * CREST_RATIO, 40.0);
    float crestFall = 1.0 / sq(crestSigma);
    vec2 ptrDelta = p - lightPos;
    vec2 keyDelta = p - keyPos;
    float ptrCrest = exp(-dot(ptrDelta, ptrDelta) * crestFall);
    float keyCrest = exp(-dot(keyDelta, keyDelta) * crestFall) * KEY_CREST;
    float crest = ptrCrest + keyCrest - ptrCrest * keyCrest;
    float live = clamp(max(uHover, uGlow), 0.0, 1.0);

    float lensWidth = max(min(LENS_WIDTH_PX * unit, min(half_.y * 0.55, radius * 1.15)), 2.2);
    float tRaw = dIn / lensWidth;
    float t = min(tRaw, 1.0);
    float shoulder = 1.0 - t;
    float profile = sqrt(max(1.0 - sq(shoulder), 0.030));
    float lensSlope = min(LENS_SLOPE * shoulder / profile, LENS_SLOPE_CEIL);
    vec3 N = normalize(vec3(nOut * lensSlope, 1.0));
    float fresnel = pow5(1.0 - clamp(N.z, 0.0, 1.0));

    vec2 refract2 = N.xy * REFRACT_PX * unit / max(uViewport, vec2(1.0));
    vec2 sampleUv = uv - refract2;
    float denseMix = smoothstep(DENSE_MIX_LO, DENSE_MIX_HI, dens);
    float densePx = DENSE_BLUR_PX * unit;
    vec3 backdrop = frostBackdrop(sampleUv, densePx, ign, denseMix);

    vec3 body;
    float bodyAlpha;
    vec3 rimColor;
    vec3 rowTint;
    float transmitOut;
    if (uLightMode > 0.5) {
        float lightMix = mix(0.84, DENSE_LIGHT_MIX, dens);
        float vgrad = mix(1.02, 0.968, vertical);
        body = mix(backdrop, vec3(0.984, 0.984, 0.994), lightMix) * vgrad;
        bodyAlpha = mix(0.84, 1.0, dens);
        rimColor = mix(vec3(0.18, 0.19, 0.26), accent, 0.28);
        rowTint = vec3(0.10, 0.10, 0.16);
        transmitOut = (1.0 - lightMix) * vgrad;
    } else {
        vec3 veilTint = mix(BODY_VEIL, accent, VEIL_ACCENT);
        float transmit = mix(BODY_TRANSMIT, DENSE_TRANSMIT, dens);
        float lift = mix(mix(VEIL_TOP_LIFT, DENSE_TOP_LIFT, dens), mix(VEIL_BOTTOM_SINK, DENSE_BOTTOM_SINK, dens), vertical);
        body = backdrop * transmit + veilTint * lift;
        bodyAlpha = mix(BODY_ALPHA, 1.0, dens);
        rimColor = mix(vec3(0.90, 0.94, 1.0), accentHot, 0.38 + 0.26 * live);
        rowTint = vec3(1.0);
        transmitOut = transmit;
    }

    body = addLight(body, mix(vec3(0.86, 0.90, 1.0), accentHot, 0.42) * fresnel * FRESNEL_GAIN * mix(1.0, DENSE_FRESNEL, dens));

    float causticCenter = CAUSTIC_CENTER + 0.055 * dens * sin(uTime * 0.3701 + vertical * 2.1);
    float caustic = exp(-sq((tRaw - causticCenter) / CAUSTIC_SIGMA));
    body = addLight(body, mix(accentHot, vec3(1.0), 0.30) * caustic * CAUSTIC_GAIN * mix(1.0, DENSE_CAUSTIC, dens) * (0.20 + 0.80 * crest));

    float topDist = max(p.y + half_.y, 0.0);
    float sheen = exp(-topDist / max(SHEEN_PX * unit, 4.0)) * dens * (0.86 + 0.14 * sin(uTime * 0.2337 + vertical * 1.7));
    body = addLight(body, mix(vec3(0.88, 0.92, 1.0), accentHot, 0.30) * sheen * SHEEN_GAIN);

    float rimWidth = max(RIM_WIDTH_PX * unit, 0.80);
    float rimBand = exp(-sq(ad / rimWidth));
    float hairline = exp(-sq(ad / max(HAIRLINE_PX * unit, 0.58)));
    float rimLight = RIM_FLOOR + RIM_LIT * crest * (0.30 + 0.70 * live);

    vec3 surface = body;
    float alphaIn = coverage * bodyAlpha;
    float rimAlpha = coverage * clamp((rimBand * rimLight + hairline * HAIRLINE_GAIN) * mix(1.0, DENSE_RIM, dens), 0.0, 1.0);
    surface = mix(surface, rimColor, clamp(rimAlpha / max(alphaIn + rimAlpha, 1.0e-4), 0.0, 1.0));
    float aIn = clamp(alphaIn + rimAlpha * (1.0 - alphaIn), 0.0, 1.0);

    float rowGlow = clamp(uRowGlow, 0.0, 1.0);
    if (uRow.z > 0.5 && uRow.w > 0.5 && rowGlow > 0.003) {
        vec2 rowHalf = max(uRow.zw * 0.5, vec2(0.5));
        vec2 rowCenter = vec2(uRow.x + rowHalf.x - half_.x, uRow.y + rowHalf.y - half_.y);
        float rowRadius = min(uRowRadius, min(rowHalf.x, rowHalf.y));
        vec3 rowField = roundBoxField(p - rowCenter, rowHalf, rowRadius);
        float dR = rowField.z;
        float rowCov = (1.0 - smoothstep(-aa, aa, dR)) * coverage;
        float rowRimBand = exp(-sq(abs(dR) / max(ROW_RIM_PX * unit, 0.62)));
        float rowE = rowCov * rowGlow;
        if (rowE > 0.002) {
            float rowIn = max(-dR, 0.0);
            float rowLensW = max(min(ROW_LENS_PX * unit, min(rowHalf.y * 0.60, rowRadius * 1.20)), 1.5);
            float rt = clamp(rowIn / rowLensW, 0.0, 1.0);
            float rShoulder = 1.0 - rt;
            float rProfile = sqrt(max(1.0 - sq(rShoulder), 0.030));
            float rSlope = min(ROW_SLOPE * rShoulder / rProfile, ROW_SLOPE_CEIL);
            vec3 rN = normalize(vec3(rowField.xy * rSlope, 1.0));
            float rFres = pow5(1.0 - clamp(rN.z, 0.0, 1.0));

            vec2 rowRefract = rN.xy * (ROW_REFRACT_PX * unit) / max(uViewport, vec2(1.0));
            vec3 rowShift = (frostBackdrop(sampleUv - rowRefract, densePx, ign, denseMix) - backdrop) * transmitOut;

            vec2 dl = lightPos - p;
            float dl2 = dot(dl, dl);
            vec3 L = normalize(vec3(dl * inversesqrt(max(dl2, 1.0e-4)) * ROW_LIGHT_TILT, 1.0));
            vec3 H = normalize(L + vec3(0.0, 0.0, 1.0));
            float spec = pow(max(dot(rN, H), 0.0), ROW_SPEC_POW);
            float proxFall = 1.0 / sq(max(rowHalf.x * 0.85, ROW_PROX_PX * unit));
            float ptrProx = exp(-dl2 * proxFall);
            float keyProx = exp(-dot(keyDelta, keyDelta) * proxFall) * KEY_CREST;
            float prox = ptrProx + keyProx - ptrProx * keyProx;

            surface = max(surface + rowShift * rowE, vec3(0.0));

            vec3 rowSpecColor = uLightMode > 0.5 ? mix(vec3(1.0), accentHot, 0.22) : mix(accentHot, vec3(1.0), 0.38);
            float specGain = ROW_SPEC_GAIN * (uLightMode > 0.5 ? 0.55 : 1.0);
            surface = addLight(surface, rowSpecColor * (spec * specGain * (0.22 + 0.78 * prox) + rFres * ROW_FRESNEL) * rowE);

            vec3 rowFill = mix(rowTint, accentHot, uLightMode > 0.5 ? 0.10 : ROW_ACCENT);
            vec3 rowRimColor = uLightMode > 0.5 ? mix(vec3(0.20, 0.21, 0.28), accentHot, 0.34) : mix(vec3(0.92, 0.95, 1.0), accentHot, 0.52);
            float rowA = rowE * ROW_TINT * (0.72 + 0.28 * prox);
            float rowRimA = rowE * rowRimBand * ROW_RIM;
            surface = mix(surface, rowFill, clamp(rowA, 0.0, 1.0));
            surface = mix(surface, rowRimColor, clamp(rowRimA, 0.0, 1.0));
            aIn = clamp(aIn + (rowA * 0.55 + rowRimA * 0.85) * (1.0 - aIn), 0.0, 1.0);
        }
    }

    if (uChevronAlpha > 0.004 && uChevron.z > 0.5) {
        vec2 c = vec2(uChevron.x + uChevron.z * 0.5 - half_.x, uChevron.y - half_.y);
        float w = uChevron.z * 0.5;
        float h = uChevron.w;
        float thick = max(CHEVRON_THICK_PX * unit, 0.85);
        float bound = length(vec2(w, h)) + thick + (CHEV_HALO_PX + 2.0) * unit;
        vec2 rel = p - c;
        if (abs(rel.x) < bound && abs(rel.y) < bound) {
            float ang = clamp(uChevronDir, -1.25, 1.25) * PI;
            float cs = cos(ang);
            float sn = sin(ang);
            vec2 q = vec2(cs * rel.x - sn * rel.y, sn * rel.x + cs * rel.y);
            vec3 f0 = segField(q, vec2(-w, -h), vec2(0.0, h));
            vec3 f1 = segField(q, vec2(0.0, h), vec2(w, -h));
            vec3 fc = f0.z <= f1.z ? f0 : f1;
            float dChev = fc.z - thick;
            float chevAa = max(pxScale * 0.70, 0.40);
            float line = 1.0 - smoothstep(-chevAa, chevAa, dChev);

            vec2 gS = vec2(cs * fc.x + sn * fc.y, -sn * fc.x + cs * fc.y);
            float ct = clamp(fc.z / thick, 0.0, 1.0);
            float cProfile = sqrt(max(1.0 - sq(ct), 0.030));
            float cSlope = min(CHEV_SLOPE * ct / cProfile, CHEV_SLOPE_CEIL);
            vec3 cN = normalize(vec3(gS * cSlope, 1.0));
            float cFres = pow5(1.0 - clamp(cN.z, 0.0, 1.0));

            vec2 cdl = lightPos - p;
            vec3 cL = normalize(vec3(cdl * inversesqrt(max(dot(cdl, cdl), 1.0e-4)) * 0.80, 1.0));
            float cSpec = pow(max(dot(cN, normalize(cL + vec3(0.0, 0.0, 1.0))), 0.0), CHEV_SPEC_POW);

            float hoverF = clamp(uHover, 0.0, 1.0);
            float chevOpacity = clamp(uChevronAlpha, 0.0, 1.0);
            float chevA = line * chevOpacity * (CHEVRON_ALPHA + CHEVRON_HOVER * hoverF);
            float haloA = exp(-sq(max(dChev, 0.0) / max(CHEV_HALO_PX * unit, 1.0))) * (1.0 - line) * chevOpacity * CHEV_HALO_GAIN;

            float lightM = step(0.5, uLightMode);
            float gloss = (cSpec * CHEV_SPEC_GAIN + cFres * CHEV_FRESNEL) * (0.55 + 0.45 * hoverF) * mix(1.0, 0.45, lightM);
            vec3 chevBase = uLightMode > 0.5 ? vec3(0.16, 0.16, 0.22) : mix(vec3(0.94, 0.96, 1.0), accentHot, 0.26);
            vec3 chevColor = addLight(chevBase, mix(accentHot, vec3(1.0), 0.42) * gloss);

            surface = addLight(surface, accentHot * (CHEV_HALO_TINT * haloA * (1.0 - lightM)));
            surface = mix(surface, vec3(0.34, 0.34, 0.42), haloA * lightM);
            surface = mix(surface, chevColor, chevA);
            aIn = clamp(aIn + (chevA * 0.85 + haloA * 0.50) * (1.0 - aIn), 0.0, 1.0);
        }
    }

    vec3 color = surface * aIn;
    float alpha = aIn;

    float alphaOut = clamp(alpha, 0.0, 1.0) * entry;
    if (alphaOut <= 0.003) {
        discard;
    }
    vec3 straight = color * entry / max(alphaOut, 1.0e-4);
    straight += ign * (1.0 / 255.0);
    FragColor = vec4(straight, alphaOut);
}
