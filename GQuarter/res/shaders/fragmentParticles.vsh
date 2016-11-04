#version 140

in vec2 textureCoordsPrev;
in vec2 textureCoordsNext;
in float progress;
in float timeNormModified;

uniform float fade;
uniform sampler2D particleTexture;

out vec4 out_colour;

void main(void) {
	out_colour = mix(texture(particleTexture, textureCoordsPrev), texture(particleTexture, textureCoordsNext), progress);
	if(fade > 0.5)
		out_colour.w = out_colour.w*(1-timeNormModified);
//out_colour = vec4(0.0, 1.0, 1.0, 1.0)/2.0;
}

