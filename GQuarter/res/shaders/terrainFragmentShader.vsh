#version 330 core

in vec2 pass_textureCoords;
in vec2 circleTextureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[8];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform int activeLightsCount;

uniform sampler2D backgroundTexture;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;

uniform sampler2D circlePicker;
uniform float circlePickerVisibility;
uniform vec4 circlePickerColour;

uniform vec3 lightColour[8];
uniform vec3 attenuation[8];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

const float tiling = 1024.0; //180 im wiekszy tym wieksza gestosc, 600 jest ok

void main(void) {
	vec4 blendMapColour = texture(blendMap, pass_textureCoords);
	float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
	vec2 tiledCoords = pass_textureCoords * tiling;
	
	vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColour = texture(rTexture, tiledCoords) * blendMapColour.r;
	vec4 gTextureColour = texture(gTexture, tiledCoords) * blendMapColour.g;
	vec4 bTextureColour = texture(bTexture, tiledCoords) * blendMapColour.b;
	vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;

	
	vec4 circleColour;
	if(circlePickerVisibility > 0.5)
		circleColour = texture(circlePicker, circleTextureCoords);
	
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitToCameraVector = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for(int i = 0; i < activeLightsCount; ++i) {
		float dist = length(toLightVector[i]);
		float attFactor = attenuation[i].x + ( attenuation[i].y * dist) + (attenuation[i].z * dist * dist);
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDot1 = dot(unitNormal, unitLightVector);
		float brightness = max(nDot1, 0.0);
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float speculatFactor = dot(reflectedLightDirection, unitToCameraVector);
		speculatFactor = max(speculatFactor, 0.0);
		float dampedFactor = pow(speculatFactor, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attFactor;
	}
	
	totalDiffuse = max(totalDiffuse, 0.16);
	
	out_Color = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);
}