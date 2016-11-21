#version 140

in vec2 textureCoords;

out vec4 outColor;

uniform sampler2D inputTexture;
uniform float threshold;

float getBrightnes(vec4 inColor) {
	return 0.2126 * inColor.r + 0.7152 * inColor.g + 0.0722 * inColor.b;
}

void main(void) {
	vec4 inColor = texture(inputTexture, textureCoords);
	float brightnes = getBrightnes(inColor);
	if(brightnes > threshold)
		outColor = inColor;
	else
		discard;
}