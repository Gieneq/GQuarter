#version 140

in vec2 position;

out vec2 textureCoordsRef;
out vec2 textureCoords;

uniform mat4 transformationMatrix;
uniform vec2 textureTranslation;
uniform float textureZoom;


void main(void){

	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
	// [0,1] x [0,1]
	textureCoordsRef = vec2((position.x+1.0)/2.0, 1 - (position.y+1.0)/2.0);
	//przesuniecie i zoom
	textureCoords = textureCoordsRef / textureZoom;
	textureCoords = textureCoords + textureTranslation;
}