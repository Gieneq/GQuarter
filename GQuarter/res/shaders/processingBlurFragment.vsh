#version 150

in vec2 textureCoords[11];

out vec4 outColor;

uniform sampler2D inputTexture;

void main(void) {
	outColor = vec4(0.0);
	outColor += texture(inputTexture, textureCoords[0]) * 0.066414;
	outColor += texture(inputTexture, textureCoords[1]) * 0.079465;
	outColor += texture(inputTexture, textureCoords[2]) * 0.091364;
	outColor += texture(inputTexture, textureCoords[3]) * 0.100939;
	outColor += texture(inputTexture, textureCoords[4]) * 0.107159;
	outColor += texture(inputTexture, textureCoords[5]) * 0.109317;
	outColor += texture(inputTexture, textureCoords[6]) * 0.107159;
	outColor += texture(inputTexture, textureCoords[7]) * 0.100939;
	outColor += texture(inputTexture, textureCoords[8]) * 0.091364;
	outColor += texture(inputTexture, textureCoords[9]) * 0.079465;
	outColor += texture(inputTexture, textureCoords[10]) * 0.066414;
	outColor = outColor.rbga;
}