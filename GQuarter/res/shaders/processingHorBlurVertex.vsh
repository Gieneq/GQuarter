#version 150

in vec2 position;
out vec2 textureCoords[11];

uniform float pixelWidth;

void main(void){
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTextureCoords = position * 0.5 + 0.5;

	for(int i = -5; i <= 5; ++i) {
		textureCoords[i + 5] = centerTextureCoords + vec2(clamp(pixelWidth * i, 0.0, 1.0), 0.0);
	}
}