#version 330 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoords;
out vec2 circleTextureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[8];
out vec3 toCameraVector;
out float visibility;

uniform int activeLightsCount;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[8];
uniform vec2 pickingPosition; //0-1
uniform float circlePickerVisibility;
uniform float circlePickerRadius; //20 jest

uniform vec4 plane;

const vec4 backPlane = vec4(0, 1, 0, 0);

const float fadeMargin = 8;

uniform float nearPlane;
uniform float farPlane;
uniform float density;
uniform float gradient;

void main(void) {

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	
	//vec4 transPlane = viewMatrix * backPlane;
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	//gl_ClipDistance[1] = dot(worldPosition, viewMatrix);
	
	vec4 positionRelativeToCamera = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionRelativeToCamera;
	pass_textureCoords = textureCoords;

	if(circlePickerVisibility > 0.5)
		circleTextureCoords = vec2((textureCoords.x - pickingPosition.x)*circlePickerRadius + 0.5, (textureCoords.y - pickingPosition.y)*circlePickerRadius + 0.5);
	
	surfaceNormal = normal; //(transformationMatrix * vec4(normal,0.0)).xyz;
	//zalozenie ze slonce jest w slocie 0 i jest jedynym kierunkowym swiatlem
	toLightVector[0] = lightPosition[0];
	for(int i = 1; i < activeLightsCount; ++i){
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*density), gradient));
	//visibility = pow((distance - farPlane)/(farPlane-nearPlane), 2.0);
	visibility = clamp(visibility, 0.0, 1.0);
}