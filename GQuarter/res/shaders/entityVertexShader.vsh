#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[8];
out vec3 toCameraVector;
out float visibility;
out float minimalDiffuse;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[8];
uniform vec3 lightColour[8];

uniform int activeLightsCount;
uniform float useFakeLighting;
uniform float numberOfRows;
uniform vec2 offset;
uniform float density;// = 0.0004f;
uniform float gradient;// = 10;
uniform vec4 plane;
uniform float timeNormalised;

void main(void) {

	vec3 modifiedPosition = position;
	//trzeba by unormowac odleglosc do najdalszego punktu :/ to ejst w raw model
	float radius = length(position) * 0.02;
	float angle = (timeNormalised) * 3.14 * 2.0;
	float dx = sin(angle) * radius;
	float dz = cos(angle) * radius;

	modifiedPosition.x = modifiedPosition.x + dx;
	modifiedPosition.z = modifiedPosition.z + dz;

	vec4 worldPosition = transformationMatrix * vec4(modifiedPosition, 1.0);
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoords = (textureCoords/numberOfRows) + offset;
	
	vec3 actualNormal = normal;
	if(useFakeLighting > 0.5)
		actualNormal = vec3(0.0, 1.0, 0.0);
	
	surfaceNormal = (transformationMatrix * vec4(actualNormal,0.0)).xyz;
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