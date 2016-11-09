#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in mat4 modelMatrix;

out vec2 passTextureCoords;
out float passSelect;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 plane;

uniform float animationValue; //-pi do pi
uniform float hardness; // higher = harder
uniform float furthestDistance;// = 1.8039675;


void main(void) {
	
	//zmiany w niewielkim zakrsie
	float fi = animationValue * 0.04*4.0; // OD - 7.2* DO 7.2* ) * 4
	
	float distNorm = clamp(length(position)/furthestDistance, 0.0, 1.0);
	distNorm = pow(distNorm, hardness);
	fi = fi * distNorm; // przy podstawie nie ma wygiecia!

	float dx = 0.0;
	float dy = abs(cos(fi)) * 0.2;
	float dz = sin(fi) * 0.2;

	vec4 modifiedPosition = vec4(position.x + dx, position.y + dy, position.z + dz, 1.0);

	vec4 worldPosition = modelMatrix * modifiedPosition;
	gl_ClipDistance[0] = dot(worldPosition, plane);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	passTextureCoords = textureCoords;
}