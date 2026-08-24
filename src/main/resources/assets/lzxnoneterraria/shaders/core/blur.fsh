#version 150

in vec2 texCoord0;

uniform sampler2D Sampler0;
uniform vec2 Direction;

out vec4 fragColor;

void main() {
    vec4 sum = texture(Sampler0, texCoord0) * 0.0788;
    sum += texture(Sampler0, texCoord0 + Direction * 1.4895) * 0.1522;
    sum += texture(Sampler0, texCoord0 - Direction * 1.4895) * 0.1522;
    sum += texture(Sampler0, texCoord0 + Direction * 3.4759) * 0.1326;
    sum += texture(Sampler0, texCoord0 - Direction * 3.4759) * 0.1326;
    sum += texture(Sampler0, texCoord0 + Direction * 5.4618) * 0.1035;
    sum += texture(Sampler0, texCoord0 - Direction * 5.4618) * 0.1035;
    sum += texture(Sampler0, texCoord0 + Direction * 7.4481) * 0.0723;
    sum += texture(Sampler0, texCoord0 - Direction * 7.4481) * 0.0723;
    fragColor = sum;
}
