#version 330

in vec3 textureCoords;
out vec4 out_Color;

uniform vec3 fogColor;
uniform vec3 skyColor;

uniform float limitFog;


void main(void) {

	float foggy = clamp(1.0/(2.0 * limitFog) * textureCoords.y + 0.5, 0.0, 1.0);
	foggy = smoothstep(0.0, 1.0, foggy);

	out_Color = vec4(mix(fogColor, skyColor, foggy), 1.0);
}