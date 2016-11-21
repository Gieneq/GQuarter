#version 150

in vec2 textureCoords[11];

out vec4 outColor;

uniform sampler2D inputTexture;

void main(void) {
	outColor = vec4(0.0);
	outColor += texture(inputTexture, textureCoords[0]) * 0.000003;
	outColor += texture(inputTexture, textureCoords[1]) * 0.000229;
	outColor += texture(inputTexture, textureCoords[2]) * 0.005977;
	outColor += texture(inputTexture, textureCoords[3]) * 0.060598;
	outColor += texture(inputTexture, textureCoords[4]) * 0.24173;
	outColor += texture(inputTexture, textureCoords[5]) * 0.382925;
	outColor += texture(inputTexture, textureCoords[6]) * 0.24173;
	outColor += texture(inputTexture, textureCoords[7]) * 0.060598;
	outColor += texture(inputTexture, textureCoords[8]) * 0.005977;
	outColor += texture(inputTexture, textureCoords[9]) * 0.000229;
	outColor += texture(inputTexture, textureCoords[10]) * 0.000003;
}