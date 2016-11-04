#version 140

in vec2 position;
in mat4 modelViewMatrix;
in float timeNorm;

uniform mat4 projectionMatrix;
uniform int columns; //np 2
uniform int framesCount; //np 3
uniform float cosineFactor; // [0;inf)
const float pi = 3.1415;

out vec2 textureCoordsPrev;
out vec2 textureCoordsNext;
out float progress;
out float timeNormModified;

void main(void) {
	float time = (1.0 - cos(pi * timeNorm)) / 2.0; // <0,1)
	time = pow(time, cosineFactor);
	timeNormModified = time;

	float frame = time * framesCount;
	float index1 = floor(frame); //0-col^2
	progress = frame - index1; //0-1
	float index2 = index1;
	if(index1 < framesCount - 1.0) //zeby nie wypasc
		index2 = index1 + 1.0;

	float dy1 = floor(index1 / columns)/columns;
	float dx1 = floor(index1 - floor(index1 / columns) * columns)/columns;
	float dy2 = floor(index2 / columns)/columns;
	float dx2 = floor(index2 - floor(index2 / columns) * columns)/columns;

	textureCoordsPrev = (position + vec2(0.5, 0.5));
	textureCoordsPrev.y = 1.0 - textureCoordsPrev.y;
	textureCoordsPrev = textureCoordsPrev/columns + vec2(dx1, dy1);

	textureCoordsNext = (position + vec2(0.5, 0.5));
	textureCoordsNext.y = 1.0 - textureCoordsNext.y;
	textureCoordsNext = textureCoordsNext/columns + vec2(dx2, dy2);

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);
	//gl_Position = vec4(position, 0.0, 1.0);

}