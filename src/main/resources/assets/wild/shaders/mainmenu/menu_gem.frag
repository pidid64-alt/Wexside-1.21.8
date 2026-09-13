#version 330 core

in vec2 vUv;
in vec2 vLocal;
in vec2 vScreen;

uniform sampler2D uBackground;
uniform vec2 uViewport;
uniform vec2 uTextureSize;
uniform vec2 uSourceScale;
uniform vec4 uGem;
uniform vec3 uAccentTop;
uniform vec3 uAccentBottom;
uniform vec2 uPointer;
uniform float uRadius;
uniform float uTime;
uniform float uEntry;
uniform float uAlpha;
uniform float uSeed;
uniform float uLightMode;

out vec4 FragColor;

const float SECTORS = 8.0;
const float PAVILION_SECTORS = 16.0;
const float TABLE_RATIO = 0.420;
const float CROWN_RATIO = 0.735;

const float TABLE_TILT = 0.060;
const float CROWN_TILT = 0.420;
const float PAVILION_TILT = 0.720;
const float FACET_JITTER = 0.150;
const float STAR_ALTERNATE = 0.170;

const float BODY_TRANSMIT = 0.34;
const vec3 BODY_VEIL = vec3(0.0520, 0.0498, 0.0840);
const float VEIL_ACCENT = 0.220;

const float LIGHT_HEIGHT = 0.95;
const float SPEC_SHINE = 34.0;
const float SPEC_GAIN = 0.760;
const float KEY_SHINE = 22.0;
const float KEY_GAIN = 0.420;
const float FRESNEL_GAIN = 0.310;

const float FIRE_HUE_SPREAD = 1.24;
const float FIRE_DISPERSION = 0.94;

const float EDGE_WIDTH_PX = 0.95;
const float EDGE_WIDE_PX = 3.20;
const float GLINT_TOL = 0.115;
const float GLINT_ALONG = 0.330;
const float GLINT_SPAN = 1.55;
const float GLINT_GAIN = 0.86;
const float GLINT_SPARSITY = 0.24;
const float GLINT_TWINKLE = 0.46;
const float GLINT_TWINKLE_RATE = 0.71;

const float RIM_WIDTH_PX = 1.30;
const float RIM_GAIN = 0.760;
const float HALO_RATIO = 0.400;
const float HALO_GAIN = 0.260;
const float CAUSTIC_GAIN = 0.300;

float sq(float x) {
    return x * x;
}

float pow5(float x) {
    float a = x * x;
    return a * a * x;
}

vec3 hueRotate(vec3 c, float a) {
    const vec3 k = vec3(0.5773503, 0.5773503, 0.5773503);
    float cs = cos(a);
    float sn = sin(a);
    return max(c * cs + cross(k, c) * sn + k * dot(k, c) * (1.0 - cs), vec3(0.0));
}

vec2 hash22(vec2 p) {
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx + p3.yz) * p3.zy);
}

float interleavedGradient(vec2 px) {
    return fract(52.9829189 * fract(dot(px, vec2(0.06711056, 0.00583715))));
}

float triangularNoise(vec2 px, float seed) {
    float r = fract(interleavedGradient(px + seed * 137.0));
    return r < 0.5 ? sqrt(2.0 * r) - 1.0 : 1.0 - sqrt(2.0 - 2.0 * r);
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

vec3 fireRamp(float x, vec3 c0, vec3 c1, vec3 c2, vec3 c3) {
    float t = abs(fract(x * 0.5) * 2.0 - 1.0);
    float s = t * 3.0;
    vec3 c = s < 1.0 ? mix(c0, c1, s)
           : (s < 2.0 ? mix(c1, c2, s - 1.0) : mix(c2, c3, s - 2.0));
    float peak = max(max(c.r, c.g), c.b);
    return c * (peak > 1.0e-4 ? 0.96 / peak : 1.0);
}

float polygonField(vec2 p, float radius, float sides, float phase) {
    float a = atan(p.y, p.x) + phase;
    float seg = 6.2831853 / sides;
    float cornered = cos(floor(0.5 + a / seg) * seg - a);
    return length(p) * max(cornered, 1.0e-4) - radius;
}

float zoneSectors(float zone) {
    return zone < 1.5 ? SECTORS : PAVILION_SECTORS;
}

float zonePhase(float zone, float spin) {
    return spin + (zone < 1.5 ? 0.0 : 3.1415927 / PAVILION_SECTORS);
}

float sectorIndex(vec2 p, float zone, float spin) {
    float a = atan(p.y, p.x) + zonePhase(zone, spin);
    return floor(a / (6.2831853 / zoneSectors(zone)) + 0.5);
}

float sectorEdge(vec2 p, float zone, float spin) {
    float seg = 6.2831853 / zoneSectors(zone);
    float a = atan(p.y, p.x) + zonePhase(zone, spin);
    float k = a / seg;
    float d = abs(k - floor(k + 0.5));
    return d * seg * max(length(p), 1.0e-4);
}

vec2 facetSlope(float sector, float zone, float radial, float spin) {
    if (zone < 0.5) {
        return vec2(0.0, TABLE_TILT * radial);
    }
    float count = zoneSectors(zone);
    float seg = 6.2831853 / count;
    float angle = sector * seg - zonePhase(zone, spin);
    vec2 axis = vec2(cos(angle), sin(angle));
    vec2 h = hash22(vec2(sector * 3.719 + zone * 11.317, zone * 5.911 + uSeed * 2.113));
    float tilt = zone < 1.5 ? CROWN_TILT : PAVILION_TILT;
    tilt *= 1.0 + FACET_JITTER * (h.x - 0.5) * 2.0;
    tilt *= 1.0 + STAR_ALTERNATE * (mod(abs(sector), 2.0) < 0.5 ? 1.0 : -1.0);
    float swirl = (h.y - 0.5) * 0.24;
    vec2 turned = vec2(axis.x * cos(swirl) - axis.y * sin(swirl), axis.x * sin(swirl) + axis.y * cos(swirl));
    return turned * tilt;
}

float creaseSpark(vec2 mirror, vec2 base, vec2 jump, float ridge, float spark) {
    float weight = ridge * spark;
    float jl = length(jump);
    if (weight <= 0.0015 || jl <= 1.0e-5) {
        return 0.0;
    }
    vec2 axis = jump / jl;
    vec2 d = mirror - base;
    float along = dot(d, axis);
    float perp = d.x * axis.y - d.y * axis.x;
    float over = max(abs(along) - jl * GLINT_SPAN, 0.0);
    return weight * exp(-(sq(perp / GLINT_TOL) + sq(over / GLINT_ALONG)));
}

vec3 blurred(vec2 uv) {
    vec2 texel = 1.0 / max(uTextureSize, vec2(1.0));
    vec2 r = uSourceScale.x > 0.0 && uSourceScale.y > 0.0 ? uSourceScale : vec2(1.0);
    return texture(uBackground, clamp(uv * r, texel * 0.5, r - texel * 0.5)).rgb;
}

void main() {
    float entry = clamp(uEntry, 0.0, 1.0);
    entry = entry * entry * entry * (entry * (entry * 6.0 - 15.0) + 10.0);
    float alpha0 = entry * clamp(uAlpha, 0.0, 1.0);
    if (alpha0 <= 0.004) {
        discard;
    }

    vec2 half_ = max(uGem.xy, vec2(1.0));
    vec2 p = vLocal - half_;
    float radius = max(uRadius, 4.0);
    float unit = max(min(uViewport.x, uViewport.y), 1.0) / 1080.0;

    float breathe = 1.0 + 0.021 * sin(uTime * 0.62 + uSeed * 1.71);
    float spin = uTime * 0.041 + uSeed * 0.83;
    float r = radius * breathe;

    float dGem = polygonField(p, r, SECTORS, spin);
    float aa = max(fwidth(dGem) * 0.70, 0.55);
    float coverage = 1.0 - smoothstep(-aa, aa, dGem);
    float outside = max(dGem, 0.0);

    float halo = exp(-sq(outside / max(r * HALO_RATIO, 8.0)));
    float quadFade = 1.0 - smoothstep(0.55, 1.0, max(abs(p.x), abs(p.y)) / max(min(half_.x, half_.y), 1.0));
    quadFade *= quadFade;
    halo *= quadFade;

    vec3 accentHot = accentNeon(0.5, 0.94);
    if (coverage <= 0.0) {
        float loneAlpha = halo * HALO_GAIN * alpha0;
        if (loneAlpha <= 0.003) {
            discard;
        }
        vec3 lone = mix(vec3(0.78, 0.84, 1.0), accentHot, 0.80)
                + triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
        FragColor = vec4(lone, min(loneAlpha, 1.0));
        return;
    }

    float len = max(length(p), 1.0e-4);
    vec2 dir = p / len;
    float outerSeg = 6.2831853 / SECTORS;
    float outerAngle = atan(p.y, p.x) + spin;
    float cornered = max(cos(floor(0.5 + outerAngle / outerSeg) * outerSeg - outerAngle), 1.0e-4);
    float radial = clamp(len * cornered / r, 0.0, 1.0);
    float tableEdge = abs(radial - TABLE_RATIO);
    float crownEdge = abs(radial - CROWN_RATIO);
    float zone = radial < TABLE_RATIO ? 0.0 : (radial < CROWN_RATIO ? 1.0 : 2.0);
    float sector = sectorIndex(p, zone, spin);
    float tableRadial = radial / max(TABLE_RATIO, 1.0e-3);

    vec2 slope = facetSlope(sector, zone, tableRadial, spin);
    float ringZone = zone < 1.5 ? zone + 1.0 : zone - 1.0;
    vec2 slopeNeighbourRing = facetSlope(sectorIndex(p, ringZone, spin), ringZone, tableRadial, spin);
    vec2 slopeNeighbourSector = facetSlope(sector + 1.0, zone, tableRadial, spin);

    vec3 N = normalize(vec3(slope, 1.0));
    vec3 V = vec3(0.0, 0.0, 1.0);
    vec2 rawLight = (uPointer - half_) / max(r, 1.0);
    float pointerReach = clamp(1.35 - (length(rawLight) - 1.1) / 2.2, 0.0, 1.0);
    vec2 idleLight = vec2(cos(uTime * 0.233 + uSeed), sin(uTime * 0.191 + uSeed * 1.7) * 0.62) * 0.72;
    vec2 lightXY = mix(idleLight, clamp(rawLight, vec2(-2.4), vec2(2.4)), pointerReach);
    vec3 L = normalize(vec3(lightXY, LIGHT_HEIGHT));
    vec3 H = normalize(L + V);
    vec2 mirror = H.xy / max(H.z, 0.05);

    float ndh = clamp(dot(N, H), 0.0, 1.0);
    float spec = pow(ndh, SPEC_SHINE);
    float fresnel = pow5(1.0 - clamp(N.z, 0.0, 1.0));

    float keyAz = 2.234 + 0.62 * sin(uTime * 0.213 + uSeed * 2.399);
    vec3 Lkey = normalize(vec3(cos(keyAz) * 0.44, sin(keyAz) * 0.22 - 0.28, 0.90));
    float keySpec = pow(max(dot(N, normalize(Lkey + V)), 0.0), KEY_SHINE);

    float ringPx = min(tableEdge, crownEdge) / max(fwidth(radial), 1.0e-5);
    float sectorPx = sectorEdge(p, zone, spin) / max(fwidth(len), 1.0e-4);
    float ringRidge = exp(-sq(ringPx / EDGE_WIDTH_PX));
    float sectorRidge = exp(-sq(sectorPx / EDGE_WIDTH_PX));
    float ringWide = exp(-sq(ringPx / EDGE_WIDE_PX));
    float sectorWide = exp(-sq(sectorPx / EDGE_WIDE_PX));
    ringRidge *= 1.0 - 0.70 * sectorWide;
    sectorRidge *= 1.0 - 0.70 * ringWide;

    vec2 hs = hash22(vec2(sector * 7.317 + zone * 2.113, uSeed * 5.731));
    vec2 hr = hash22(vec2(sector * 1.913, zone * 9.117 + uSeed * 3.311));
    float sparkSector = smoothstep(GLINT_SPARSITY, 1.0, hs.x);
    float sparkRing = smoothstep(GLINT_SPARSITY, 1.0, hr.x);
    sparkSector *= 1.0 - GLINT_TWINKLE * (0.5 - 0.5 * cos(uTime * GLINT_TWINKLE_RATE + hs.y * 6.2831853));
    sparkRing *= 1.0 - GLINT_TWINKLE * (0.5 - 0.5 * cos(uTime * GLINT_TWINKLE_RATE * 1.31 + hr.y * 6.2831853));

    float glint = creaseSpark(mirror, slope, (slopeNeighbourSector - slope) * 0.5, sectorRidge, sparkSector)
            + creaseSpark(mirror, slope, (slopeNeighbourRing - slope) * 0.5, ringRidge, sparkRing);
    glint = min(glint * GLINT_GAIN, 0.90);

    vec3 fireCool = hueRotate(uAccentTop, -FIRE_HUE_SPREAD);
    vec3 fireWarm = hueRotate(uAccentBottom, FIRE_HUE_SPREAD);
    float disp = FIRE_DISPERSION * (1.0 - ndh);
    float fireX = dot(slope, vec2(2.60, 1.90)) + radial * 1.35 + disp + sector * 0.21;
    vec3 fireCol = fireRamp(fireX, fireCool, uAccentTop, uAccentBottom, fireWarm);

    vec2 uv = vec2(vScreen.x / max(uViewport.x, 1.0), 1.0 - vScreen.y / max(uViewport.y, 1.0));
    vec2 refract2 = vec2(N.x, -N.y) * (9.0 * unit) / max(uViewport, vec2(1.0));
    vec3 backdrop = blurred(uv - refract2);

    float vertical = clamp((p.y + half_.y) / max(half_.y * 2.0, 1.0), 0.0, 1.0);
    vec3 accent = accentAt(vertical);
    vec3 veil = mix(BODY_VEIL, accent, VEIL_ACCENT);
    vec3 body;
    vec3 lightTint;
    if (uLightMode > 0.5) {
        body = mix(backdrop, vec3(0.972, 0.974, 0.988), 0.62);
        body = mix(body, accent, 0.10);
        lightTint = vec3(1.0);
    } else {
        body = backdrop * BODY_TRANSMIT + veil * mix(1.28, 0.72, vertical);
        lightTint = vec3(1.000, 0.988, 0.972);
    }

    float caustic = exp(-sq((radial - 0.72) / 0.20)) * (0.30 + 0.70 * clamp(1.0 - length(lightXY), 0.0, 1.0));
    vec3 specColor = mix(fireCol, mix(lightTint, accentHot, 0.60), 0.20);

    float facetLum = 0.5 + 0.5 * dot(normalize(slope + vec2(1.0e-4)), vec2(0.42, -0.907));
    body = addLight(body, mix(accent, fireCol, 0.42) * ((0.070 + 0.115 * facetLum) * (0.45 + 0.55 * (1.0 - radial))));
    body = addLight(body, specColor * (spec * SPEC_GAIN));
    body = addLight(body, mix(fireCol, accentHot, 0.36) * (keySpec * KEY_GAIN));
    body = addLight(body, mix(vec3(0.76, 0.82, 0.98), accentHot, 0.70) * (fresnel * FRESNEL_GAIN));
    body = addLight(body, mix(fireCol, accentHot, 0.42) * (caustic * CAUSTIC_GAIN));
    if (uLightMode > 0.5) {
        vec3 ink = mix(vec3(0.135, 0.140, 0.198), accent, 0.36);
        body = mix(body, ink, clamp(fresnel * 1.55 + abs(slope.x) * 0.36, 0.0, 0.52));
        body = mix(body, mix(accent, fireCol, 0.34) * 0.88, clamp(glint * 0.62, 0.0, 1.0));
    } else {
        body = addLight(body, mix(mix(fireCol, accentHot, 0.42), vec3(1.0), 0.30) * glint);
    }

    float ad = abs(dGem);
    float rimBand = exp(-sq(ad / max(RIM_WIDTH_PX * unit, 0.85)));
    vec2 lightDir = length(lightXY) > 1.0e-4 ? normalize(lightXY) : vec2(0.0, -1.0);
    float facing = clamp(dot(dir, lightDir), -1.0, 1.0);
    float rimLight = RIM_GAIN * (0.40 + 0.60 * clamp(facing, 0.0, 1.0));
    vec3 rimColor = uLightMode > 0.5
            ? mix(vec3(0.20, 0.21, 0.28), accent, 0.34)
            : mix(vec3(0.80, 0.86, 1.0), accentHot, 0.62);

    float bodyAlpha = uLightMode > 0.5 ? 0.80 : 0.76;
    float alphaIn = coverage * bodyAlpha;
    float rimAlpha = coverage * clamp(rimBand * rimLight, 0.0, 1.0);
    vec3 surface = mix(body, rimColor, clamp(rimAlpha / max(alphaIn + rimAlpha, 1.0e-4), 0.0, 1.0));
    float aIn = clamp(alphaIn + rimAlpha * (1.0 - alphaIn), 0.0, 1.0);

    float haloAlpha = halo * HALO_GAIN * (1.0 - coverage);
    vec3 haloColor = mix(vec3(0.78, 0.84, 1.0), accentHot, 0.80);
    vec3 color = surface * aIn + haloColor * haloAlpha * (1.0 - aIn);
    float alpha = aIn + haloAlpha * (1.0 - aIn);

    alpha *= alpha0;
    if (alpha <= 0.003) {
        discard;
    }
    vec3 straight = color * alpha0 / max(alpha, 1.0e-4);
    straight += triangularNoise(vScreen, fract(uTime * 0.41)) * (1.0 / 255.0);
    FragColor = vec4(straight, clamp(alpha, 0.0, 1.0));
}
