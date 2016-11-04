#version 330 core

out vec4 out_Color;
uniform vec4 color;
uniform float select;

void main(void) {
	out_Color = color;
	if(select > 0.5)
		out_Color = mix(out_Color, vec4(1.0), 0.5);
}