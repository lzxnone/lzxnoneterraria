#version 150

in vec3 Position;
in vec4 Color;

uniform mat4 ModelMat;
uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec3 vLocalPos;
flat out vec3 vCameraLocal;
out vec4 vColor;

void main() {
    vec4 posedPos = ModelMat * vec4(Position, 1.0);
    vec4 viewPos = ModelViewMat * posedPos;

    gl_Position = ProjMat * viewPos;

    vLocalPos = Position;
    vColor = Color;

    mat4 localToView = ModelViewMat * ModelMat;
    vec4 cameraLocal = inverse(localToView) * vec4(0.0, 0.0, 0.0, 1.0);

    vCameraLocal = cameraLocal.xyz / cameraLocal.w;
}