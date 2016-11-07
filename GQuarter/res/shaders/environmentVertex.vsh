#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in mat4 modelMatrix;

out vec2 passTextureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 plane;

void main(void) {
	vec4 worldPosition = modelMatrix * vec4(position, 1.0);
	gl_ClipDistance[0] = dot(worldPosition, plane);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	passTextureCoords = textureCoords;
}