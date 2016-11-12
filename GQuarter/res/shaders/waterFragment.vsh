#version 330 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec2 textureCoordsBasic;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 out_Color;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap; //zamienilem depth i normal
uniform sampler2D depthMap;
uniform sampler2D flowMap;
uniform float nearPlane;
uniform float farPlane;

uniform float time;
uniform float waveFreqMax;
uniform float waveAmplMax;

uniform float waterOpacity;
uniform vec4 bluishColour;
uniform vec4 sunColour;
uniform vec3 skyColor;

const float shineDamper = 20.0;
const float reflectivity = 0.6;
const float alphaDivisor = 1.6;
const vec4 foamColour = vec4(1.0,0.97,0.95, 1.0);

void main(void) {
	//konwersja przestrzeni
	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 refractTexCoords = vec2(ndc.x, ndc.y);
	vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);


	// GLEBOKOSC //////////////////
	float depth = texture(depthMap, refractTexCoords).r;
	float floorDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane  - (2.0 * depth - 1.0) * (farPlane - nearPlane));
	
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane  - (2.0 * depth - 1.0) * (farPlane - nearPlane));
	float waterDepth = floorDistance - waterDistance;
	
	float alpha = clamp(waterDepth/alphaDivisor, 0.0, 1.0);
	float alpha2 = clamp(waterDepth/(2.0*alphaDivisor), 0.0, 1.0); //troche slabszy wspolczynnik

	// FLOW /////////////////
	float wSpeed = 0.2; //1.2 //0.1
	float wAmplitude = 0.03; //0.01
	vec4 flowMapColour = texture(flowMap, textureCoordsBasic);
	vec2 flowDirection = (flowMapColour.rg * 2.0 - 1.0);
	float foam = flowMapColour.b;

	vec2 flowOffset1 = flowDirection * wSpeed * time;
	vec2 flowOffset2 = flowDirection * wSpeed * mod((time + 0.5), 1.0);

	vec4 bump1 = texture(dudvMap, textureCoords + flowOffset1);
	vec4 bump2 = texture(dudvMap, textureCoords + flowOffset2);

	float selector = 2 * abs(time - 0.5);
	vec4 bump = mix(bump1,bump2, selector);

	vec4 foamColor = foamColour * foam * length(bump.rg)*0.8;

	bump = bump * wAmplitude; //przesuniecia wyjsciowe tekstur

	refractTexCoords = refractTexCoords + bump.rg;
	reflectTexCoords = reflectTexCoords + bump.rg;

	vec4 reflectionColour = texture(reflectionTexture, reflectTexCoords);
	vec4 refractionColour = texture(refractionTexture, refractTexCoords);

	// SPECULAR/SPARKS ///////////////////////
	vec3 normToCameraVector = normalize(toCameraVector);

	vec4 normalmapColour = texture(normalMap, textureCoords*0.25 + bump.rg);
	vec3 normalVector = vec3((2.0*normalmapColour.r-1.0), normalmapColour.b, (2.0*normalmapColour.g-1.0)); 
	normalVector = normalize(normalVector);

	//odbity wektor swiatla
	vec3 reflectedLightNormal = reflect(normalize(-toLightVector), normalVector);
	float specularFactor = dot(reflectedLightNormal, normToCameraVector);
	specularFactor = max(specularFactor, 0.0);
	float dampedFactor = pow(specularFactor, shineDamper);
	vec4 specularColour = reflectivity * dampedFactor * sunColour * alpha2;

	// FRESNEL /////////////
	float fresnelFactor = dot(normToCameraVector, vec3(0,1,0));
	fresnelFactor = clamp(pow(fresnelFactor, 2), 0, 1);

	// OUTPUT //////////////
	out_Color = mix(reflectionColour, refractionColour, fresnelFactor);
	out_Color = mix(out_Color, bluishColour, waterOpacity) + specularColour;
	out_Color.a = alpha;

	out_Color = out_Color + foamColor;
	out_Color = mix(vec4(skyColor, 1.0), out_Color, 0.5 + visibility/2.0);
}