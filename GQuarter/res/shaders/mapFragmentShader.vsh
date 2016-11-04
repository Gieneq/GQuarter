#version 330 core

in vec2 textureCoords;
out vec4 out_Color;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform sampler2D isolineTexture;
uniform sampler2D slopemapTexture;

uniform int mode;

const float tiling = 180.0;

void main(void) {
	//0 - satelite
	//1 - isomap
	//2 - satelite+slope
	if(mode == 1) {
		out_Color = texture(isolineTexture, textureCoords);
	} else {
		vec4 blendMapColour = texture(blendMap, textureCoords);
		float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
		vec2 tiledCoords = textureCoords * tiling;
		vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
		vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
		vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
		vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;
		out_Color = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;

		if(mode == 2) {
			float slopemapColour = 1.0-texture(slopemapTexture, textureCoords).r;
			out_Color = mix(out_Color, vec4(0.0, 0.0, 0.0, 1.0), slopemapColour);
			//out_Color.a = slopemapColour.r;
		}
	}
}