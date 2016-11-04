#version 330

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform sampler2D fontAtlas;

void main(void) {
	float alpha = texture(fontAtlas, pass_textureCoords).a;
	alpha = sqrt(alpha);

	if(alpha < 0.1)
		alpha = 0.0;
	else if(alpha > 0.9)
		alpha = 1.0;
	else
		alpha = smoothstep(0.1, 0.9, alpha);

	out_Color = vec4(color, alpha);
	//out_Color = vec4(0.4);
}