#version 330 core

in vec2 passTextureCoords;

out vec4 outColor;

uniform sampler2D textureSampler;

void main(void) {	
	vec4 textureColour = texture(textureSampler, passTextureCoords);
	outColor = textureColour.rbga;
}