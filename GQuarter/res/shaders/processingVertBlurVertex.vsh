#version 150

in vec2 position;
out vec2 textureCoords[11];

uniform float pixelHeight;

void main(void){
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTextureCoords = position * 0.5 + 0.5;

	for(int i = -5; i <= 5; ++i) {
		textureCoords[i + 5] = centerTextureCoords + vec2(0.0, clamp(pixelHeight * i, -1.0, 0.0));
	}
}