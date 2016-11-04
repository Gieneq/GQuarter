package gje.gquarter.terrain;

import gje.gquarter.core.ShaderProgram;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.Light;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class TerrainShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/terrainVertexShader.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/terrainFragmentShader.vsh";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	private int location_lightPosition[];
	private int location_lightColour[];
	private int[] location_attenuation;
	private int location_activeLightsCount;

	private int location_shineDamper;
	private int location_reflectivity;

	private int location_skyColor;

	private int location_bgTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;

	private int location_plane;

	private int location_circlePicker;
	private int location_pickingPosition;
	private int location_circlePickerVisibility;
	private int location_circlePickerRadius;
	private int location_circlePickerColour;

	private int location_nearPlane;
	private int location_farPlane;
	private int location_fogDensity;
	private int location_fogGradient;

	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");

		location_lightPosition = new int[MAX_LIGHTS_COUNT];
		location_lightColour = new int[MAX_LIGHTS_COUNT];
		location_attenuation = new int[MAX_LIGHTS_COUNT];
		location_activeLightsCount = super.getUniformLocation("activeLightsCount");

		for (int i = 0; i < MAX_LIGHTS_COUNT; ++i) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");

		location_skyColor = super.getUniformLocation("skyColor");

		location_bgTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");

		location_plane = super.getUniformLocation("plane");

		location_circlePicker = super.getUniformLocation("circlePicker");
		location_pickingPosition = super.getUniformLocation("pickingPosition");
		location_circlePickerVisibility = super.getUniformLocation("circlePickerVisibility");
		location_circlePickerRadius = super.getUniformLocation("circlePickerRadius");
		location_circlePickerColour = super.getUniformLocation("circlePickerColour");

		location_nearPlane = super.getUniformLocation("nearPlane");
		location_farPlane = super.getUniformLocation("farPlane");
		location_fogDensity = super.getUniformLocation("density");
		location_fogGradient= super.getUniformLocation("gradient");
	}

	// load to shader!
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadPlanes(float near, float far){
		loadFloat(location_nearPlane, near);
		loadFloat(location_farPlane, far);
	}
	
	public void loadFogParams(float gradient, float density){
		loadFloat(location_fogGradient, gradient);
		loadFloat(location_fogDensity, density);
	}
	
	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}

	public void loadActiveLightsCount(int value){
		super.loadInt(location_activeLightsCount, value);
	}
	
	public void loadLights(List<Light> lights) {
		int inputLightCont = lights.size();
		if (inputLightCont > MAX_LIGHTS_COUNT)
			inputLightCont = MAX_LIGHTS_COUNT;
		loadActiveLightsCount(inputLightCont);
		
		for (int i = 0; i < inputLightCont; ++i) {
			super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
			super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
			super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
		}
	}

	public void loadShineVariables(float shineDamp, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamp);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadSkyColor(Vector3f color) {
		super.loadVector3f(location_skyColor, color);
	}

	public void connectTextureUnits() {
		super.loadInt(location_bgTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
		super.loadInt(location_circlePicker, 5);
	}

	public void loadClipPlane(Vector4f plane) {
		super.loadVector4f(location_plane, plane);
	}
	
	public void loadRadiusMarkerVisibility(boolean visibility) {
		super.loadBoolean(location_circlePickerVisibility, visibility);
	}
}
