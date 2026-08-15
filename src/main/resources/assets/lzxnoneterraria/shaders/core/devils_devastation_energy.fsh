#version 150

in vec3 vLocalPos;
flat in vec3 vCameraLocal;
in vec4 vColor;

out vec4 fragColor;

uniform float EffectTime;
uniform vec4 ColorA;
uniform vec4 ColorB;

const vec3 BOX_MIN = vec3(-0.25, 0.0, -1.0);
const vec3 BOX_MAX = vec3( 0.25, 32.0, 1.0);

vec3 mod289(vec3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 mod289(vec4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 permute(vec4 x) { return mod289(((x * 34.0) + 1.0) * x); }
vec4 taylorInvSqrt(vec4 r) { return 1.79284291400159 - 0.85373472095314 * r; }

float snoise(vec3 v) {
    const vec2 C = vec2(1.0 / 6.0, 1.0 / 3.0);
    const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);

    vec3 i = floor(v + dot(v, C.yyy));
    vec3 x0 = v - i + dot(i, C.xxx);

    vec3 g = step(x0.yzx, x0.xyz);
    vec3 l = 1.0 - g;
    vec3 i1 = min(g.xyz, l.zxy);
    vec3 i2 = max(g.xyz, l.zxy);

    vec3 x1 = x0 - i1 + C.xxx;
    vec3 x2 = x0 - i2 + C.yyy;
    vec3 x3 = x0 - D.yyy;

    i = mod289(i);
    vec4 p = permute(permute(permute(
        i.z + vec4(0.0, i1.z, i2.z, 1.0))
        + i.y + vec4(0.0, i1.y, i2.y, 1.0))
        + i.x + vec4(0.0, i1.x, i2.x, 1.0));

    float n_ = 0.142857142857;
    vec3 ns = n_ * D.wyz - D.xzx;

    vec4 j = p - 49.0 * floor(p * ns.z * ns.z);

    vec4 x_ = floor(j * ns.z);
    vec4 y_ = floor(j - 7.0 * x_);

    vec4 x = x_ * ns.x + ns.yyyy;
    vec4 y = y_ * ns.x + ns.yyyy;
    vec4 h = 1.0 - abs(x) - abs(y);

    vec4 b0 = vec4(x.xy, y.xy);
    vec4 b1 = vec4(x.zw, y.zw);

    vec4 s0 = floor(b0) * 2.0 + 1.0;
    vec4 s1 = floor(b1) * 2.0 + 1.0;
    vec4 sh = -step(h, vec4(0.0));

    vec4 a0 = b0.xzyw + s0.xzyw * sh.xxyy;
    vec4 a1 = b1.xzyw + s1.xzyw * sh.zzww;

    vec3 p0 = vec3(a0.xy, h.x);
    vec3 p1 = vec3(a0.zw, h.y);
    vec3 p2 = vec3(a1.xy, h.z);
    vec3 p3 = vec3(a1.zw, h.w);

    vec4 norm = taylorInvSqrt(vec4(dot(p0, p0), dot(p1, p1), dot(p2, p2), dot(p3, p3)));
    p0 *= norm.x; p1 *= norm.y; p2 *= norm.z; p3 *= norm.w;

    vec4 m = max(0.6 - vec4(dot(x0, x0), dot(x1, x1), dot(x2, x2), dot(x3, x3)), 0.0);
    m = m * m;
    return 42.0 * dot(m * m, vec4(dot(p0, x0), dot(p1, x1), dot(p2, x2), dot(p3, x3)));
}

float fbm3(vec3 p) {
    float value = 0.0;
    float amplitude = 0.5;

    // 每层旋转坐标，打破各八度格点对齐（消除 tile 拼接感）
    mat3 rot = mat3(
        0.866, -0.5, 0.0,
        0.5, 0.866, 0.0,
        0.0, 0.0, 1.0
    );

    for (int i = 0; i < 3; i++) {
        value += snoise(p) * amplitude;
        p = rot * p * 2.03 + vec3(17.1, 9.2, 13.7);
        amplitude *= 0.5;
    }

    return value;
}

float safeComponent(float x) {
    if (abs(x) > 0.00001) return x;
    return x < 0.0 ? -0.00001 : 0.00001;
}

vec2 intersectBox(vec3 ro, vec3 rd) {
    vec3 safeRd = vec3(
        safeComponent(rd.x),
        safeComponent(rd.y),
        safeComponent(rd.z)
    );

    vec3 inv = 1.0 / safeRd;
    vec3 t0 = (BOX_MIN - ro) * inv;
    vec3 t1 = (BOX_MAX - ro) * inv;

    vec3 nearT = min(t0, t1);
    vec3 farT = max(t0, t1);

    float tEnter = max(max(nearT.x, nearT.y), nearT.z);
    float tExit = min(min(farT.x, farT.y), farT.z);

    return vec2(tEnter, tExit);
}

float densityField(vec3 p, float time) {
    // 转换成统一的归一化 Sword Space。
    float nx = p.x / 0.25;
    float ny = p.y / 32.0;
    float nz = p.z;

    // 顶部收窄：z 宽度随 ny 减小到接近 0（剑尖锥形）
    float tip = smoothstep(1.0, 0.5, ny);
    float zHalf = 0.06 + 0.94 * tip;
    nz = p.z / zHalf;

    // 横截面中心更亮，靠近 X/Z 边界逐渐减弱。
    // 边界阈值随噪声波动 → 边缘不规则撕裂（能量溢出感）
    float edgeNoise = fbm3(vec3(ny * 22.0, time * 4.0, 0.0)) * 0.5 + 0.5;
    float edgeX = 1.0 - smoothstep(0.55 + edgeNoise * 0.35, 1.05 + edgeNoise * 0.35, abs(nx));
    float edgeZ = 1.0 - smoothstep(0.55 + edgeNoise * 0.35, 1.05 + edgeNoise * 0.35, abs(nz));
    float volumeMask = edgeX * edgeZ;

    // 根部与剑尖稍微渐隐。
    volumeMask *= smoothstep(0.0, 0.02, ny);
    volumeMask *= 1.0 - smoothstep(0.96, 1.0, ny);

    // 三维噪声沿 Y 轴流动（加快速度）。
    vec3 flowPos = vec3(
        nx * 3.5,
        ny * 18.0 - time * 4.0,
        nz * 3.5
    );

    // domain warping：单次 fbm 驱动三个轴向，减少噪声采样开销（原来是 3 次独立 fbm）
    float warp = fbm3(flowPos + vec3(0.0, 0.0, time * 0.4)) - 0.5;
    flowPos += vec3(warp, warp * 0.7, warp * 0.5) * 0.65;

    // snoise 输出范围 -1~1，重映射到 0~1 再参与密度（与旧 value noise 语义一致）
    float n1 = fbm3(flowPos) * 0.5 + 0.5;

    float n2 = fbm3(vec3(
        nx * 6.0 + 7.3,
        ny * 11.0 - time * 2.6,
        nz * 6.0 - 4.7
    )) * 0.5 + 0.5;

    // 明显的纵向能量束。
    float stream1 = 0.5 + 0.5 * sin(
        ny * 95.0 - time * 16.0 + n1 * 10.0 + nz * 5.0
    );

    float stream2 = 0.5 + 0.5 * sin(
        ny * 143.0 - time * 22.0 - n2 * 13.0 + nx * 6.0
    );

    stream1 = pow(stream1, 7.0);
    stream2 = pow(stream2, 10.0);

    // 中央能量核。
    float radial = length(vec2(nx, nz));
    float core = 1.0 - smoothstep(0.05, 0.75, radial);

    // 溢出细丝：边界附近的高频刺状丝，断续随机，随时间沿剑身流动
    float filament = fbm3(vec3(nx * 8.0, ny * 36.0, nz * 8.0) + vec3(0.0, time * 5.0, 0.0)) * 0.5 + 0.5;
    float edgeDist = min(1.0 - abs(nx), 1.0 - abs(nz));     // 距边界（内部）距离，0=边界
    float leak = step(0.74, filament) * smoothstep(0.12, 0.0, edgeDist);

    float density =
        n1 * 0.30 +
        stream1 * 0.85 +
        stream2 * 0.55 +
        core * 0.65;

    // 细丝顶破表面（不乘 volumeMask，靠 slab 限制在盒内，边缘处 mask 已衰减由丝接管）
    density = max(density, leak * 1.4);

    density = smoothstep(0.30, 1.15, density);

    // leak 必须绕过 volumeMask（边界处 mask≈0 会把它乘没），靠 slab 限制在盒内
    return max(density * volumeMask, leak);
}

vec3 densityColor(vec3 p, float density) {
    float alongBlade = clamp(p.y / 32.0, 0.0, 1.0);

    vec3 base = mix(ColorA.rgb, ColorB.rgb, alongBlade);

    float nx = p.x / 0.25;
    float nz = p.z;
    float core = 1.0 - smoothstep(0.0, 0.65, length(vec2(nx, nz)));

    // 核心更白热：平方增强 + 高权重，中间不再是淡色
    vec3 hot = mix(base, vec3(1.0, 0.85, 1.0), pow(core, 1.5) * 0.85);

    // 整体提亮 + 核心区额外增益（加法混合下超 1 的亮度会喂给 bloom）
    float brighten = 1.0 + core * 0.9;
    return hot * (1.1 + density * 2.4) * brighten;
}

void main() {
    float time = EffectTime * 0.2;

    vec3 ro = vCameraLocal;
    vec3 rd = normalize(vLocalPos - ro);

    vec2 hit = intersectBox(ro, rd);

    float tStart = max(hit.x, 0.0);
    float tEnd = hit.y;

    if (tEnd <= tStart) {
        discard;
    }

    const int STEPS = 20;

    float stepSize = (tEnd - tStart) / float(STEPS);
    float t = tStart + stepSize * 0.5;

    vec3 accumulatedColor = vec3(0.0);
    float accumulatedAlpha = 0.0;

    for (int i = 0; i < STEPS; i++) {
        vec3 p = ro + rd * t;
        float density = densityField(p, time);

        if (density > 0.001) {
            float opticalDepth = density * stepSize * 5.0;
            float sampleAlpha = 1.0 - exp(-opticalDepth);
            vec3 sampleColor = densityColor(p, density);

            float remain = 1.0 - accumulatedAlpha;

            accumulatedColor += remain * sampleColor * sampleAlpha;
            accumulatedAlpha += remain * sampleAlpha;

            if (accumulatedAlpha > 0.98) {
                break;
            }
        }

        t += stepSize;
    }

    if (accumulatedAlpha < 0.005) {
        discard;
    }

    float pulse = 0.9 + 0.1 * sin(time * 8.0);
    accumulatedColor *= pulse;

    vec3 color = accumulatedColor / max(accumulatedAlpha, 0.0001);

    fragColor = vec4(color, accumulatedAlpha * vColor.a);
}