#version 330 core

in vec2 vPixel;
in vec2 vP0;
in vec2 vP1;
in vec2 vP2;
in vec2 vP3;
in vec4 vColorA;
in vec4 vColorB;
in vec4 vWidths;
in vec4 vParams;

uniform float uAlpha;
uniform float u_Time;

out vec4 fragColor;

float sat(float v) {
    return clamp(v, 0.0, 1.0);
}

vec2 bezier(float t) {
    float u = 1.0 - t;
    return u * u * u * vP0 + 3.0 * u * u * t * vP1 + 3.0 * u * t * t * vP2 + t * t * t * vP3;
}

vec2 bezierD(float t) {
    float u = 1.0 - t;
    return 3.0 * u * u * (vP1 - vP0) + 6.0 * u * t * (vP2 - vP1) + 3.0 * t * t * (vP3 - vP2);
}

vec3 spectral(vec3 a, vec3 b, float t) {
    vec3 base = mix(a, b, smoothstep(0.0, 1.0, t));
    float oil = 0.5 + 0.5 * sin((t * 1.52 - u_Time * 0.030) * 6.2831853);
    vec3 film = mix(vec3(0.58, 0.95, 1.0), vec3(1.0, 0.38, 0.70), oil);
    return mix(base, film, 0.052);
}

float sq(float x) {
    return x * x;
}

float packet(float t, float phase, float speed, float activeAmt) {
    float wave = fract(u_Time * speed + phase) * 1.64 - 0.32;
    float x = t - wave;
    float gate = smoothstep(-0.26, -0.04, wave) * (1.0 - smoothstep(1.02, 1.30, wave));
    float head = exp(-sq(x * 22.0));
    float body = exp(-sq((x + 0.070) * 9.5)) * 0.34;
    float tail = exp(-sq((x + 0.155) * 5.7)) * (1.0 - smoothstep(-0.015, 0.16, x)) * 0.18;
    return (head + body + tail) * gate * activeAmt;
}

void main() {
    float best = 1.0e20;
    float bestT = 0.0;
    const int coarse = 36;
    for (int i = 0; i <= coarse; i++) {
        float t = float(i) / float(coarse);
        vec2 p = bezier(t);
        float d = dot(vPixel - p, vPixel - p);
        if (d < best) {
            best = d;
            bestT = t;
        }
    }
    float span = 1.0 / float(coarse);
    for (int i = 0; i < 4; i++) {
        float l = clamp(bestT - span, 0.0, 1.0);
        float m0 = clamp(bestT - span * 0.34, 0.0, 1.0);
        float m1 = clamp(bestT + span * 0.34, 0.0, 1.0);
        float r = clamp(bestT + span, 0.0, 1.0);
        float dl = dot(vPixel - bezier(l), vPixel - bezier(l));
        float dm0 = dot(vPixel - bezier(m0), vPixel - bezier(m0));
        float dm1 = dot(vPixel - bezier(m1), vPixel - bezier(m1));
        float dr = dot(vPixel - bezier(r), vPixel - bezier(r));
        best = dm0;
        bestT = m0;
        if (dl < best) {
            best = dl;
            bestT = l;
        }
        if (dm1 < best) {
            best = dm1;
            bestT = m1;
        }
        if (dr < best) {
            best = dr;
            bestT = r;
        }
        span *= 0.42;
    }
    float dPx = sqrt(max(best, 0.0));
    float coreHalf = max(vWidths.x, 0.05);
    float haloHalf = max(vWidths.y, coreHalf + 0.05);
    float bloomHalf = max(vWidths.z, haloHalf + 0.05);
    float aa = max(fwidth(dPx) * 1.45, 0.92);
    float core = 1.0 - smoothstep(coreHalf - aa * 0.82, coreHalf + aa * 1.30, dPx);
    float needle = exp(-sq(dPx / max(coreHalf * 0.86, 0.26))) * 0.34;
    float halo = exp(-sq(max(dPx - coreHalf, 0.0) / max(haloHalf - coreHalf, 0.05)) * 3.70);
    float bloom = exp(-sq(max(dPx - coreHalf, 0.0) / max(bloomHalf - coreHalf, 0.05)) * 4.75);
    float t = sat(bestT);
    float flowSpeed = vParams.x;
    float pulseStrength = vParams.y;
    float activeAmt = vParams.z;
    float phase = vParams.w;
    float filament = packet(t, phase, flowSpeed, activeAmt) * pulseStrength;
    vec3 base = spectral(vColorA.rgb, vColorB.rgb, t);
    vec3 hot = mix(vec3(1.0), base, 0.28);
    vec3 chroma = mix(base, hot, 0.52 + 0.12 * sin((t + phase + u_Time * 0.025) * 6.2831853));
    float tangentEnergy = sat(length(bezierD(t)) / 360.0);
    float conversion = sat(length(vColorA.rgb - vColorB.rgb) * 1.35);
    float bridge = exp(-sq((t - 0.5) * 2.20)) * conversion;
    vec3 rgb = base * (core * 1.24 + halo * 0.24 + bloom * 0.105);
    rgb += hot * needle * (0.055 + filament * 0.54);
    rgb += mix(vColorA.rgb, vColorB.rgb, t) * bloom * bridge * 0.18;
    rgb += hot * core * bridge * 0.035;
    rgb += base * bloom * filament * 0.72;
    rgb += chroma * bloom * filament * (0.30 + tangentEnergy * 0.10);
    rgb += vec3(1.0) * core * 0.010;
    float colorAlpha = mix(vColorA.a, vColorB.a, smoothstep(0.0, 1.0, t));
    float alpha = (core * 0.80 + needle * 0.035 + halo * 0.195 + bloom * (0.095 + filament * 0.26)) * colorAlpha * uAlpha;
    if (alpha <= 0.0015) {
        discard;
    }
    fragColor = vec4(clamp(rgb, 0.0, 1.0), sat(alpha));
}
