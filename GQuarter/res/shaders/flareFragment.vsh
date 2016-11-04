#version 140

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D flareTexture;
uniform float brightnesFactor;

void main(void){
	out_Color = texture(flareTexture, textureCoords) * brightnesFactor;
}