package gje.gquarter.entity;

import java.util.List;

import gje.gquarter.core.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class EnvironmentShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/environmentVertex.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/environmentFragment.vsh";

	private int locationProjectionMatrix;
	private int locationViewMatrix;
	private int locationPlane;
	
	private int[] locationLightPosition;
	private int[] locationLightColour;
	private int[] locationAttenuation;
	private int locationActiveLightsCount;
	private int locationFogDensity;
	private int locationFogGradient;
	
	private int locationShineDamper;
	private int locationReflectivity;
	private int locationSkyColor;

	private int locationAnimationType;
	private int locationAnimationValue;
	private int locationVal0;
	private int locationVal1;
	private int locationVal2;
	private int locationFurthestDistance;

	public EnvironmentShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
		super.bindAttribute(3, "modelMatrix");
	}

	@Override
	protected void getAllUniformLocations() {
		locationProjectionMatrix = super.getUniformLocation("projectionMatrix");
		locationViewMatrix = super.getUniformLocation("viewMatrix");
		locationPlane = super.getUniformLocation("plane");
		
		locationAnimationType = super.getUniformLocation("animationType");
		locationAnimationValue = super.getUniformLocation("time");
		locationVal0 = super.getUniformLocation("val0");
		locationVal1 = super.getUniformLocation("val1");
		locationVal2 = super.getUniformLocation("val2");
		locationFurthestDistance = super.getUniformLocation("furthestDistance");

		locationLightPosition = new int[MAX_LIGHTS_COUNT];
		locationLightColour = new int[MAX_LIGHTS_COUNT];
		locationAttenuation = new int[MAX_LIGHTS_COUNT];
		locationActiveLightsCount = super.getUniformLocation("activeLightsCount");

		for (int i = 0; i < MAX_LIGHTS_COUNT; i++) {
			locationLightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			locationLightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			locationAttenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}
		
		locationShineDamper = super.getUniformLocation("shineDamper");
		locationReflectivity = super.getUniformLocation("reflectivity");
		locationSkyColor = super.getUniformLocation("skyColor");
		locationFogDensity = super.getUniformLocation("density");
		locationFogGradient = super.getUniformLocation("gradient");
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(locationProjectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(locationViewMatrix, camera.getViewMatrix());
	}

	public void loadClipPlane(Vector4f plane) {
		super.loadVector4f(locationPlane, plane);
	}

	public void loadAnimationValue(float time) {
		super.loadFloat(locationAnimationValue, time);
	}

	public void loadModelParams(float furthestDistance) {
		super.loadFloat(locationFurthestDistance, furthestDistance);
	}
	
	public void loadAnimationParams(int type, float val0, float val1, float val2){
		super.loadInt(locationAnimationType, type);
		super.loadFloat(locationVal0, val0);
		super.loadFloat(locationVal1, val1);
		super.loadFloat(locationVal2, val2);
	}
	
	public void loadActiveLightsCount(int value) {
		super.loadInt(locationActiveLightsCount, value);
	}

	public void loadLights(List<Light> lights) {
		int inputLightCont = lights.size();
		if (inputLightCont > MAX_LIGHTS_COUNT)
			inputLightCont = MAX_LIGHTS_COUNT;
		loadActiveLightsCount(inputLightCont);

		for (int i = 0; i < inputLightCont; ++i) {
			super.loadVector3f(locationLightPosition[i], lights.get(i).getPosition());
			super.loadVector3f(locationLightColour[i], lights.get(i).getColour());
			super.loadVector3f(locationAttenuation[i], lights.get(i).getAttenuation());
		}
	}

	public void loadShineVariables(float shineDamp, float reflectivity) {
		super.loadFloat(locationShineDamper, shineDamp);
		super.loadFloat(locationReflectivity, reflectivity);
	}

	public void loadSkyColor(Vector3f color) {
		super.loadVector3f(locationSkyColor, color);
	}
	
	public void loadFogParams(float gradient, float density) {
		loadFloat(locationFogGradient, gradient);
		loadFloat(locationFogDensity, density);
	}
}
