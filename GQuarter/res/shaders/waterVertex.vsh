#version 330 core

in vec2 position;

out vec4 clipSpace;
out vec2 textureCoords;
out vec2 textureCoordsBasic;
out vec3 toLightVector;
out vec3 toCameraVector;
out float visibility;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 modelMatrix;

uniform vec3 sunPosition;
uniform float tiling;


uniform float density;
uniform float gradient;

void main(void) {

	//czemu tu jest 0.0? BO TO JEST PLASKI QUAD position.x.y to kordy wierzcholkow
	vec4 worldPosition = modelMatrix * vec4(position.x, 0.0, position.y, 1.0);
	vec4 eyeSpace = viewMatrix * worldPosition;

	clipSpace = projectionMatrix * eyeSpace;
	gl_Position = clipSpace.xyzw;
 
 	toLightVector = sunPosition;
 	//toLightVector = (sunPosition - worldPosition.xyz);
 	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
 
	textureCoordsBasic = vec2(position.x/2.0 + 0.5, position.y/2.0 + 0.5); //-1:1
 	textureCoords = textureCoordsBasic * tiling; // -10:10

	float distance = length(eyeSpace.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility, 0.0, 1.0);
}