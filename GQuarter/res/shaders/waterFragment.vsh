#version 330 core

in vec4 clipSpace;
in vec2 textureCoords;
in vec3 toLightVector;
in vec3 toCameraVector;
in float visibility;

out vec4 outColor;

uniform sampler2D reflectionTexture;
uniform sampler2D refractionTexture;
uniform sampler2D dudvMap;
uniform sampler2D normalMap;
uniform sampler2D depthMap;
uniform float nearPlane;
uniform float farPlane;

uniform float time;
uniform float waveFreqMax;
uniform float waveAmplMax;

uniform float waterOpacity;
uniform vec4 bluishColour;
uniform vec4 sunColour;
uniform vec3 skyColor;

const float shineDamper = 60.0;
const float reflectivity = 1.2;
const float alphaDivisor = 1.6;

void main(void) {
	// konwersja przestrzeni
	vec2 ndc = (clipSpace.xy/clipSpace.w)/2.0 + 0.5;
	vec2 refractTexCoords = vec2(ndc.x, ndc.y);
	vec2 reflectTexCoords = vec2(ndc.x, -ndc.y);

	// GLEBOKOSC
	float depth = texture(depthMap, refractTexCoords).r;
	float floorDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane  - (2.0 * depth - 1.0) * (farPlane - nearPlane));
	depth = gl_FragCoord.z;
	float waterDistance = 2.0 * nearPlane * farPlane / (farPlane + nearPlane  - (2.0 * depth - 1.0) * (farPlane - nearPlane));
	float waterDepth = floorDistance - waterDistance;
	float alpha = clamp(waterDepth/alphaDivisor, 0.0, 1.0);

	// FLOW /////////////////
	float amplitude = 0.005;
	vec2 bump = (texture(dudvMap, textureCoords + vec2(time, 0.0)).rg * 2.0 - 1.0) * amplitude;
	reflectTexCoords = clamp(reflectTexCoords + bump, vec2(0.001, -0.999), vec2(0.999, -0.001));
	refractTexCoords = clamp(refractTexCoords + bump, vec2(0.001), vec2(0.999));
	vec4 reflectionColour = texture(reflectionTexture, reflectTexCoords);
	vec4 refractionColour = texture(refractionTexture, refractTexCoords);

	// SPECULAR
	vec3 normToCameraVector = normalize(toCameraVector);
	vec4 normalmapColour = texture(normalMap, textureCoords + bump*8.0);
	vec3 normalVector = vec3((2.0*normalmapColour.r-1.0), normalmapColour.b, (2.0*normalmapColour.g-1.0)); 
	normalVector = normalize(normalVector);
	vec3 reflectedLightNormal = reflect(normalize(-toLightVector), normalVector);
	float specularFactor = dot(reflectedLightNormal, normToCameraVector);
	specularFactor = max(specularFactor, 0.0);
	float dampedFactor = pow(specularFactor, shineDamper);
	vec4 specularColour = reflectivity * dampedFactor * sunColour;// * alpha2; //co to robi?

	// FRESNEL
	float fresnelFactor = dot(normToCameraVector, vec3(0,1,0));
	fresnelFactor = clamp(pow(fresnelFactor, 2), 0, 1);

	// OUTPUT
	outColor = mix(reflectionColour, refractionColour, fresnelFactor);
	outColor = mix(outColor, bluishColour, waterOpacity) + specularColour;
	outColor.a = alpha;

	outColor = mix(vec4(skyColor, 1.0), outColor, visibility);
}