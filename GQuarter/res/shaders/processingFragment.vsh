#version 140

in vec2 textureCoords;

out vec4 outColor;

uniform sampler2D inputTexture;
uniform sampler2D bloomTexture;

vec4 convertToSepia(vec4 inColor) {
	vec4 outColor = vec4(1.0);
	outColor.r = (inColor.r * .393) + (inColor.g *.769) + (inColor.b * .189);
	outColor.g = (inColor.r * .349) + (inColor.g *.686) + (inColor.b * .168);
	outColor.b = (inColor.r * .272) + (inColor.g *.534) + (inColor.b * .131);
	return outColor;
}

vec4 convertToGrayscale(vec4 inColor) {
	vec4 outColor = vec4(1.0);
	outColor.r = (inColor.r + inColor.g + inColor.b)/3.0;
	outColor.g = outColor.r;
	outColor.b = outColor.r;
	return outColor;
}

vec4 adjustContrast(float value, vec4 iColor) {
	return (iColor - 0.5) * (1.0 + value) + 0.5;
}

void main(void) {
	vec4 inColor = texture(inputTexture, textureCoords);
	vec4 bloomColor = texture(bloomTexture, textureCoords);
	outColor = adjustContrast(0.3, inColor) + bloomColor*3.0;
}