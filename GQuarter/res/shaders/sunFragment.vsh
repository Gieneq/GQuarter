#version 140

in vec2 textureCoords;

out vec4 out_Color;
uniform vec3 color;
uniform sampler2D bilboardedTexture;

void main(void){
	out_Color = texture(bilboardedTexture, textureCoords);
	vec4 filling = vec4(color, 1.0);
	out_Color = out_Color * filling;

}