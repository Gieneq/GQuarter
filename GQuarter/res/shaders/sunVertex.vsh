#version 140

in vec2 position;

out vec2 textureCoords;

uniform mat4 mvMatrix;
uniform mat4 projectionMatrix;

void main(void){

	gl_Position = projectionMatrix * mvMatrix * vec4(position, 0.0, 1.0);
	textureCoords = (position + vec2(0.5, 0.5));
}