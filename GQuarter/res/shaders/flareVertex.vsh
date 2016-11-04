#version 140

in vec2 position;

out vec2 textureCoords;
out float brightness;

uniform vec2 offset;
uniform vec2 translation;
uniform vec2 scale;
uniform float atlasRows;

void main(void){
	gl_Position =  vec4((position * scale + translation), 0.0, 1.0);
	textureCoords = ((position + 0.5) / atlasRows) + offset;
}