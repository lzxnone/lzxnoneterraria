#version 150

in vec3 vLocalPos;
in vec4 vColor;

out vec4 fragColor;

uniform vec3 CameraLocal;
uniform vec3 BladeDimensions;
// x=剑根最大轮廓外凸，y=单侧彩层厚度/核心完整厚度，z=外焰宽度，w=消光系数
uniform vec4 VolumeParameters;
uniform vec3 EdgeColor0;
uniform vec3 EdgeColor1;
uniform vec3 EdgeColor2;
uniform float FlowTime;

const int VOLUME_STEPS = 32;
const float EDGE_SOFTNESS = 0.003;
const float AURA_DENSITY = 0.40;
const float CORE_DENSITY_BOOST = 18.0;
const float CORE_BRIGHTNESS = 2.5;
const float CORE_LENGTH_RATIO = 0.90;
const float FACE_AMPLITUDE_RATIO = 0.5;
const float PARALLEL_EPSILON = 0.00001;

// 使用半空间 n·p <= d 裁剪射线，逐平面收紧进入和离开距离。
bool clipHalfSpace(
    vec3 rayOrigin,
    vec3 rayDirection,
    vec3 normal,
    float distance,
    inout float tEnter,
    inout float tExit
) {
    float denominator = dot(normal, rayDirection);
    float numerator = distance - dot(normal, rayOrigin);

    if (abs(denominator) < PARALLEL_EPSILON) {
        return numerator >= 0.0;
    }

    float t = numerator / denominator;
    if (denominator < 0.0) {
        tEnter = max(tEnter, t);
    } else {
        tExit = min(tExit, t);
    }

    return tEnter <= tExit;
}

vec2 intersectTriangularPrism(
    vec3 rayOrigin,
    vec3 rayDirection,
    vec3 dimensions
) {
    float tEnter = -1.0e20;
    float tExit = 1.0e20;
    float halfThickness = dimensions.x;
    float bladeLength = dimensions.y;
    float rootHalfWidth = dimensions.z;
    float bladeSlope = rootHalfWidth / bladeLength;

    if (!clipHalfSpace(rayOrigin, rayDirection, vec3( 1.0, 0.0,  0.0), halfThickness, tEnter, tExit)) return vec2(1.0, -1.0);
    if (!clipHalfSpace(rayOrigin, rayDirection, vec3(-1.0, 0.0,  0.0), halfThickness, tEnter, tExit)) return vec2(1.0, -1.0);
    if (!clipHalfSpace(rayOrigin, rayDirection, vec3( 0.0, -1.0, 0.0), 0.0, tEnter, tExit)) return vec2(1.0, -1.0);
    if (!clipHalfSpace(rayOrigin, rayDirection, vec3(0.0, bladeSlope,  1.0), rootHalfWidth, tEnter, tExit)) return vec2(1.0, -1.0);
    if (!clipHalfSpace(rayOrigin, rayDirection, vec3(0.0, bladeSlope, -1.0), rootHalfWidth, tEnter, tExit)) return vec2(1.0, -1.0);

    return vec2(tEnter, tExit);
}

// 无三角函数的确定性伪随机 Hash。
float hash31(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

// 对格点随机值作三线性平滑插值，得到空间连续的 Value Noise。
float valueNoise(vec3 p) {
    vec3 cell = floor(p);
    vec3 local = fract(p);
    vec3 weight = local * local * (3.0 - 2.0 * local);

    float n000 = hash31(cell + vec3(0.0, 0.0, 0.0));
    float n100 = hash31(cell + vec3(1.0, 0.0, 0.0));
    float n010 = hash31(cell + vec3(0.0, 1.0, 0.0));
    float n110 = hash31(cell + vec3(1.0, 1.0, 0.0));
    float n001 = hash31(cell + vec3(0.0, 0.0, 1.0));
    float n101 = hash31(cell + vec3(1.0, 0.0, 1.0));
    float n011 = hash31(cell + vec3(0.0, 1.0, 1.0));
    float n111 = hash31(cell + vec3(1.0, 1.0, 1.0));

    float z0 = mix(
        mix(n000, n100, weight.x),
        mix(n010, n110, weight.x),
        weight.y
    );
    float z1 = mix(
        mix(n001, n101, weight.x),
        mix(n011, n111, weight.x),
        weight.y
    );
    return mix(z0, z1, weight.z);
}

float surfaceNoise(vec3 p) {
    // 将正反表面作为 YZ 高度场采样，避免噪声沿射线深度变化后被体积积分平均掉。
    vec3 noisePosition = vec3(
        p.y / max(BladeDimensions.y, 0.0001) * 12.0,
        p.z / max(BladeDimensions.z, 0.0001) * 2.6,
        0.0
    );
    float flowPhase = FlowTime * 0.18;

    // 两层噪声主要沿 +Y（剑根到剑尖）流动，并带少量横向漂移。
    // 细节层的位移约为粗糙层的 2.03 倍，使两层具有相近的实际流速。
    float coarse = valueNoise(
        noisePosition + vec3(-flowPhase * 1.35, flowPhase * 0.11, flowPhase * 0.18)
    );
    float detail = valueNoise(
        noisePosition * 2.03
        + vec3(17.1, 9.2, 13.7)
        + vec3(-flowPhase * 2.74, flowPhase * 0.23, -flowPhase * 0.27)
    );
    float centered = (coarse * 0.72 + detail * 0.28) * 2.0 - 1.0;
    return clamp(centered * 2.2, -1.0, 1.0);
}

float sharedOutlineDisplacement(vec3 p, float y01) {
    // 剑根端点保持闭合；剑尖处由局部宽度自然把轮廓幅度收敛到 0。
    float rootMask = smoothstep(0.0, 0.025, y01);
    float localHalfWidth = BladeDimensions.z * max(1.0 - y01, 0.0);
    float localWidthRatio = localHalfWidth / max(BladeDimensions.z, 0.0001);

    // 左右侧分别采样边界上的流动噪声。最大外凸随当地剑宽同比缩放：
    // 剑根为完整宽度的 1/3，之后一直保持相同的相对比例。
    float sideSign = p.z < 0.0 ? -1.0 : 1.0;
    vec3 outlineSamplePosition = vec3(0.0, p.y, sideSign * localHalfWidth);
    float outlineNoise = surfaceNoise(outlineSamplePosition);
    float localMaxOutward = VolumeParameters.x * localWidthRatio;
    float localMaxInward = localHalfWidth * 0.20;
    float outlineDisplacement = outlineNoise >= 0.0
        ? pow(max(outlineNoise, 0.0), 0.70) * localMaxOutward
        : outlineNoise * localMaxInward;
    return outlineDisplacement * rootMask;
}

void evaluateBladeFields(vec3 p, out float outerField, out float coreField) {
    float y01 = clamp(p.y / BladeDimensions.y, 0.0, 1.0);
    float rootMask = smoothstep(0.0, 0.025, y01);
    float baseOuterHalfWidth = BladeDimensions.z * max(1.0 - y01, 0.0);
    float outlineDisplacement = sharedOutlineDisplacement(p, y01);

    // 把噪声看成坐标扭曲，而不是只修改某一层的边界。
    // 外层和核心使用同一 warpedX/warpedZ，因此位移大小与速度严格一致。
    float faceDisplacement = surfaceNoise(p)
        * BladeDimensions.x * FACE_AMPLITUDE_RATIO * rootMask;
    float warpedX = abs(p.x) - faceDisplacement;
    float warpedZ = abs(p.z) - outlineDisplacement;

    float outerXDistance = warpedX - BladeDimensions.x;
    float outerRootDistance = -p.y;
    float outerSideDistance = warpedZ - baseOuterHalfWidth;
    outerField = max(
        outerXDistance,
        max(outerRootDistance, outerSideDistance)
    );

    // 若单侧彩层 T = 核心完整厚度 C * ratio，则：
    // 外层完整厚度 = C + 2T = C * (1 + 2ratio)。
    float colorToCoreRatio = max(VolumeParameters.y, 0.0);
    float coreScale = 1.0 / (1.0 + 2.0 * colorToCoreRatio);
    float coreLength = BladeDimensions.y * CORE_LENGTH_RATIO;
    float coreY01 = clamp(p.y / max(coreLength, 0.0001), 0.0, 1.0);
    float baseCoreHalfWidth = BladeDimensions.z
        * coreScale
        * max(1.0 - coreY01, 0.0);

    float coreXDistance = warpedX - BladeDimensions.x * coreScale;
    float coreRootDistance = -p.y;
    float coreTipDistance = p.y - coreLength;
    float coreSideDistance = warpedZ - baseCoreHalfWidth;
    coreField = max(
        max(coreXDistance, coreRootDistance),
        max(coreTipDistance, coreSideDistance)
    );
}

vec3 sectionEdgeColor(float y01) {
    if (y01 < 1.0 / 3.0) {
        return EdgeColor0;
    }
    if (y01 < 2.0 / 3.0) {
        return EdgeColor1;
    }
    return EdgeColor2;
}

void main() {
    float maxOutlineProtrusion = VolumeParameters.x;
    float auraWidth = VolumeParameters.z;
    float extinction = VolumeParameters.w;

    // 轮廓变形按剑宽同比缩放，因此代理保持短三角形；只对外焰作平行扩张。
    float auraPadding = auraWidth * 3.0;
    float faceAmplitude = BladeDimensions.x * FACE_AMPLITUDE_RATIO;
    float deformedRootHalfWidth = BladeDimensions.z + maxOutlineProtrusion;
    float proxyRootHalfWidth = deformedRootHalfWidth + auraPadding;
    vec3 proxyDimensions = vec3(
        BladeDimensions.x + faceAmplitude + auraPadding,
        BladeDimensions.y * proxyRootHalfWidth / deformedRootHalfWidth,
        proxyRootHalfWidth
    );

    vec3 rayOrigin = CameraLocal;
    vec3 rayDirection = normalize(vLocalPos - rayOrigin);
    vec2 hit = intersectTriangularPrism(
        rayOrigin,
        rayDirection,
        proxyDimensions
    );

    float tStart = max(hit.x, 0.0);
    float tEnd = hit.y;
    if (tEnd <= tStart) {
        discard;
    }

    float stepSize = (tEnd - tStart) / float(VOLUME_STEPS);
    float t = tStart + stepSize * 0.5;
    vec3 accumulatedColor = vec3(0.0);
    float accumulatedAlpha = 0.0;

    for (int i = 0; i < VOLUME_STEPS; i++) {
        vec3 p = rayOrigin + rayDirection * t;
        float outerField;
        float coreField;
        evaluateBladeFields(p, outerField, coreField);

        float outerDensity =
            1.0 - smoothstep(-EDGE_SOFTNESS, EDGE_SOFTNESS, outerField);
        float coreDensity =
            1.0 - smoothstep(-EDGE_SOFTNESS, EDGE_SOFTNESS, coreField);
        float colorDensity = max(outerDensity - coreDensity, 0.0);
        float outsideBlend =
            smoothstep(-EDGE_SOFTNESS, EDGE_SOFTNESS, outerField);
        float auraDensity =
            outsideBlend
            * exp(-max(outerField, 0.0) / max(auraWidth, 0.0001));
        float auraWeight = auraDensity * AURA_DENSITY;
        float coreWeight = coreDensity * CORE_DENSITY_BOOST;
        float sampleDensity = colorDensity + coreWeight + auraWeight;

        if (sampleDensity > 0.0001) {
            float y01 = clamp(p.y / BladeDimensions.y, 0.0, 1.0);
            vec3 edgeColor = sectionEdgeColor(y01);
            vec3 sampleColor = (
                edgeColor * (colorDensity + auraWeight)
                + vec3(CORE_BRIGHTNESS) * coreWeight
            ) / sampleDensity;

            float sampleAlpha =
                1.0 - exp(-sampleDensity * stepSize * extinction);
            float remain = 1.0 - accumulatedAlpha;

            accumulatedColor +=
                remain * sampleColor * sampleAlpha;
            accumulatedAlpha += remain * sampleAlpha;

            if (accumulatedAlpha > 0.995) {
                break;
            }
        }

        t += stepSize;
    }

    if (accumulatedAlpha < 0.001) {
        discard;
    }

    vec3 color =
        accumulatedColor / max(accumulatedAlpha, 0.0001);
    fragColor = vec4(color, accumulatedAlpha * vColor.a);
}
