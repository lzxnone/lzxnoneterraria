#version 150

in vec3 vLocalPos;
in vec4 vColor;

out vec4 fragColor;

uniform vec3 CameraLocal;
uniform vec3 Dimensions;
uniform vec4 VolumeParameters;
uniform vec3 SuperellipsoidColor;
uniform float FlowTime;

const int VOLUME_STEPS = 48;

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

vec2 intersectAABB(vec3 rayOrigin, vec3 rayDir, vec3 boxMin, vec3 boxMax) {
    vec3 invDir = 1.0 / max(abs(rayDir), vec3(0.00001)) * sign(rayDir);
    vec3 t0 = (boxMin - rayOrigin) * invDir;
    vec3 t1 = (boxMax - rayOrigin) * invDir;
    vec3 tmin = min(t0, t1);
    vec3 tmax = max(t0, t1);
    float tEnter = max(max(tmin.x, tmin.y), tmin.z);
    float tExit = min(min(tmax.x, tmax.y), tmax.z);
    return vec2(tEnter, tExit);
}

float evaluateSuperellipsoid(vec3 p, vec3 dim) {
    vec3 q = abs(p) / max(dim, vec3(0.0001));
    return sqrt(max(q.x, 0.0)) + sqrt(max(q.y, 0.0)) + sqrt(max(q.z, 0.0));
}

void main() {
    float extinction = VolumeParameters.x;
    float coreRatio = VolumeParameters.y;
    float coreBrightness = VolumeParameters.w;

    vec3 proxyRadius = Dimensions * 1.05;
    vec3 rayOrigin = CameraLocal;
    vec3 rayDir = normalize(vLocalPos - rayOrigin);

    vec2 hit = intersectAABB(rayOrigin, rayDir, -proxyRadius, proxyRadius);
    float tStart = max(hit.x, 0.0);
    float tEnd = hit.y;

    if (tEnd <= tStart || tEnd <= 0.0) {
        discard;
    }

    float stepSize = (tEnd - tStart) / float(VOLUME_STEPS);
    // 屏幕空间微抖动：消除光线步进固定间距导致的阶梯锯齿
    float dither = hash21(gl_FragCoord.xy);
    float t = tStart + stepSize * dither;

    vec3 accumulatedColor = vec3(0.0);
    float accumulatedAlpha = 0.0;

    // 自适应步长抗锯齿过渡区
    float edgeWidth = max(stepSize * 1.2, 0.025);

    for (int i = 0; i < VOLUME_STEPS; i++) {
        vec3 p = rayOrigin + rayDir * t;

        // 纯净解析超椭圆体场函数，去除噪点扰动引起的突起
        float s = evaluateSuperellipsoid(p, Dimensions);
        float outerDist = s - 1.0;
        float coreDist = (s / max(coreRatio, 0.001)) - 1.0;

        float outerDensity = 1.0 - smoothstep(-edgeWidth, edgeWidth, outerDist);
        float coreDensity = 1.0 - smoothstep(-edgeWidth, edgeWidth, coreDist);
        float colorDensity = max(outerDensity - coreDensity, 0.0);

        float coreWeight = coreDensity * 12.0;
        float sampleDensity = colorDensity * 3.0 + coreWeight;

        if (sampleDensity > 0.0001) {
            vec3 sampleColor = (
                SuperellipsoidColor * 1.8 * (colorDensity * 3.0)
                + vec3(coreBrightness) * coreWeight
            ) / sampleDensity;

            float sampleAlpha = 1.0 - exp(-sampleDensity * stepSize * extinction);
            float remain = 1.0 - accumulatedAlpha;

            accumulatedColor += remain * sampleColor * sampleAlpha;
            accumulatedAlpha += remain * sampleAlpha;

            if (accumulatedAlpha > 0.99) {
                break;
            }
        }

        t += stepSize;
    }

    if (accumulatedAlpha < 0.01) {
        discard;
    }

    vec3 finalRgb = accumulatedColor / max(accumulatedAlpha, 0.0001);
    fragColor = vec4(finalRgb, accumulatedAlpha * vColor.a);
}
