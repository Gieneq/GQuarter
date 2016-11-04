#version 140

in vec2 textureCoordsRef;
in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D guiTexture;
uniform vec4 colour;
uniform int typeFilling;

uniform float mixValue;
uniform float finalAlpha;
uniform float minRadius;
uniform float maxRadius;

const int NO_FILLING = 0;
const int TEXTURED_FILLING = 1;
const int COLOURED_FILLING = 2;
const int CIRCULAR_FILLING = 4;

const vec4 TRANSPARENCY = vec4(0.0);

bool checkFlag(int flag){
	return ((typeFilling & flag) != 0);
}

void main(void){
	if((textureCoords.x < 0.0) || (textureCoords.x > 1.0) || (textureCoords.y < 0.0) || (textureCoords.y > 1.0))
		discard;

	out_Color = vec4(1.0, 1.0, 1.0, 1.0);
	
	if(checkFlag(TEXTURED_FILLING))
		out_Color = texture(guiTexture,textureCoords);
	
	if(checkFlag(COLOURED_FILLING)) 
		out_Color = mix(out_Color, colour, mixValue);
	
	if(checkFlag(CIRCULAR_FILLING)){
		// [-1,1] x [-1,1]
		vec2 coords = textureCoordsRef * 2.0 - 1.0;

		float dist = length(coords);

		if(dist > maxRadius)
			discard;
		
		//tu juz nie dojde po discard :)
		if(dist > minRadius) {
			out_Color.a *= (1.0-smoothstep(minRadius, maxRadius, dist));
		}
	}
	
	out_Color.a = out_Color.a * finalAlpha;
}
