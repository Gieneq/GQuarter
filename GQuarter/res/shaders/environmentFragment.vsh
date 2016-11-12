#version 330 core

in vec2 passTextureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in float minimalDiffuse;

out vec4 outColor;

uniform sampler2D textureSampler;
uniform int activeLightsCount;
uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

void main(void) {	
	vec3 unitNormal = normalize(surfaceNormal);
	vec3 unitToCameraVector = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i = 0; i < activeLightsCount; ++i) {
		float dist = length(toLightVector[i]);
		float attFactor = attenuation[i].x + attenuation[i].y * dist + attenuation[i].z * dist * dist;
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDot1 = dot(unitNormal, unitLightVector);
		float brightness = max(nDot1, 0.0); // jasnosc zwiazana z orientacja powierzchni wzgledem zrodla
		
		vec3 lightDirection = -unitLightVector;
		vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
		float speculatFactor = dot(reflectedLightDirection, unitToCameraVector); //specular - ile promienia odbitego wpada do kamery
		speculatFactor = max(speculatFactor, 0.0);
		
		float dampedFactor = pow(speculatFactor, shineDamper); //duzy shineDamper mocno podnosci wspolczynnik jasnosci - plamka
		totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attFactor; //kolor obiektu - rownomierny
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attFactor; //kolor plamki, refl - jak mocno oddaje kolor odbity
	}
	
	totalDiffuse = max(totalDiffuse, minimalDiffuse);
	
	vec4 textureColour = texture(textureSampler, passTextureCoords);
	
	//przezroczystosc
	if(textureColour.a < 0.5) {
		discard;
	}
	//wynikowy kolor to kolor rownomierny + plamka zwiazana z kolrem swiatla
	outColor = vec4(totalDiffuse, 1.0) * textureColour + vec4(totalSpecular, 1.0);
	outColor = mix(vec4(skyColor, 1.0), outColor, visibility);
}