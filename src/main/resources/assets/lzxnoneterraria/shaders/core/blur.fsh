#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform vec2 Direction;

out vec4 fragColor;

void main() {
    vec4 sum = texture(Sampler0, texCoord0) * 0.0788;
    sum += texture(Sampler0, texCoord0 + Direction * 1.0) * 0.0777;
    sum += texture(Sampler0, texCoord0 - Direction * 1.0) * 0.0777;
    sum += texture(Sampler0, texCoord0 + Direction * 2.0) * 0.0745;
    sum += texture(Sampler0, texCoord0 - Direction * 2.0) * 0.0745;
    sum += texture(Sampler0, texCoord0 + Direction * 3.0) * 0.0695;
    sum += texture(Sampler0, texCoord0 - Direction * 3.0) * 0.0695;
    sum += texture(Sampler0, texCoord0 + Direction * 4.0) * 0.0631;
    sum += texture(Sampler0, texCoord0 - Direction * 4.0) * 0.0631;
    sum += texture(Sampler0, texCoord0 + Direction * 5.0) * 0.0557;
    sum += texture(Sampler0, texCoord0 - Direction * 5.0) * 0.0557;
    sum += texture(Sampler0, texCoord0 + Direction * 6.0) * 0.0478;
    sum += texture(Sampler0, texCoord0 - Direction * 6.0) * 0.0478;
    sum += texture(Sampler0, texCoord0 + Direction * 7.0) * 0.0399;
    sum += texture(Sampler0, texCoord0 - Direction * 7.0) * 0.0399;
    sum += texture(Sampler0, texCoord0 + Direction * 8.0) * 0.0324;
    sum += texture(Sampler0, texCoord0 - Direction * 8.0) * 0.0324;
    fragColor = sum;
}
