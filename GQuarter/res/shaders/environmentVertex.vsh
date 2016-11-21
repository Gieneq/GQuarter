#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in mat4 modelMatrix;

out vec2 passTextureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;
out float minimalDiffuse;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec4 plane;

//animacja
uniform int animationType;
uniform float time;
uniform float val0;
uniform float val1;
uniform float val2;
uniform float furthestDistance;

//swiatla
uniform vec3 lightPosition[4];
uniform vec3 lightColour[4];
uniform int activeLightsCount;

//mgla
uniform float density;
uniform float gradient;

void main(void) {
	//animacja
	float dx = 0.0;
	float dy = 0.0;
	float dz = 0.0;

	if(animationType == 1) {
		float fi = sin(time) * 0.5;
	
		float distNorm = clamp(length(position)/furthestDistance, 0.0, 1.0);
		distNorm = pow(distNorm, val0);
		fi = fi * distNorm; // przy podstawie nie ma wygiecia!

		dx = 0.0;
		dy = abs(cos(fi)) * 0.2;
		dz = sin(fi) * 0.2;
	}
	if(animationType == 2) {
		float distNorm = clamp(length(position)/furthestDistance, 0.0, 1.0);
		distNorm = pow(distNorm, val2);
		float ampl = 0.06 * distNorm;
		dx = ampl * cos(val0 * time - val1 * position.y + 0.1);
		dy = 0.0;
		dz = ampl * cos(val0 * time - val1 * position.y + 0.5);
	}
	if(animationType == 4) {
		float ampl = 0.018;
		dx = 0.0;
		dy = ampl * cos(val0 * time - val1 * position.z);
		dz = 0.0;
	}
	
	vec4 modifiedPosition = vec4(position.x + dx, position.y + dy, position.z + dz, 1.0);

	vec4 worldPosition = modelMatrix * modifiedPosition;
	gl_ClipDistance[0] = dot(worldPosition, plane);
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;

	passTextureCoords = textureCoords;

	//swiatlo
	surfaceNormal = (modelMatrix * vec4(normal,0.0)).xyz;
	toLightVector[0] = lightPosition[0];
	for(int i = 1; i < activeLightsCount; ++i) {
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	
	minimalDiffuse = clamp(length(lightColour[0])/3.0, 0.1, 0.9);
}