#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;
uniform vec2 TexelSize;
uniform vec4 OutlineColor;
uniform float Radius;
uniform float DepthBias;

out vec4 fragColor;

void main() {
    float center = texture(Sampler0, texCoord0).a;
    float dilated = center;
    float outlineDepth = texture(Sampler1, texCoord0).r;
    int radius = int(clamp(Radius, 1.0, 4.0));
    int radiusSquared = radius * radius;

    for(int y = -4; y <= 4; ++y) {
        for(int x = -4; x <= 4; ++x) {
            if(x * x + y * y > radiusSquared) continue;

            vec2 sampleUv = texCoord0 + vec2(float(x), float(y)) * TexelSize;
            float sampleMask = texture(Sampler0, sampleUv).a;
            if(sampleMask <= 0.0) continue;

            float sampleDepth = texture(Sampler1, sampleUv).r;
            if(sampleMask > dilated || (sampleMask == dilated && sampleDepth < outlineDepth)) {
                dilated = sampleMask;
                outlineDepth = sampleDepth;
            }
        }
    }

    float outline = max(dilated - center, 0.0);
    float sceneDepth = texture(Sampler2, texCoord0).r;
    if(outline <= 0.0 || outlineDepth > sceneDepth + DepthBias) discard;

    fragColor = vec4(OutlineColor.rgb, outline * OutlineColor.a);
}
