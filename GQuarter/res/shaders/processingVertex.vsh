#version 140

in vec2 position;
out vec2 textureCoords;

void main(void){
	gl_Position = vec4(position, 0.0, 1.0);
	textureCoords = (position/2.0 + vec2(0.5, 0.5));
}