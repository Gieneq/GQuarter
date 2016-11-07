package gje.gquarter.entity;

import java.util.List;

import gje.gquarter.core.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class EnvironmentShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/entityVertexShader.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/entityFragmentShader.vsh";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	private int[] location_lightPosition;
	private int[] location_lightColour;
	private int[] location_attenuation;
	private int location_activeLightsCount;

	private int location_shineDamper;
	private int location_reflectivity;

	private int location_fakeLighting;
	private int location_skyColor;

	private int location_numberOfRows;
	private int location_offset;

	private int location_plane;

	private int location_shineTexture;
	private int location_shine;

	private int location_fogDensity;
	private int location_fogGradient;

	private int location_selectedOption;
	private int location_time;

	public EnvironmentShader() {
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

		for (int i = 0; i < MAX_LIGHTS_COUNT; i++) {
			location_lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
			location_lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
			location_attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
		}

		location_shineDamper = super.getUniformLocation("shineDamper");
		location_reflectivity = super.getUniformLocation("reflectivity");

		location_fakeLighting = super.getUniformLocation("useFakeLighting");
		location_skyColor = super.getUniformLocation("skyColor");

		location_numberOfRows = super.getUniformLocation("numberOfRows");
		location_offset = super.getUniformLocation("offset");

		location_plane = super.getUniformLocation("plane");

		location_shineTexture = super.getUniformLocation("shineSampler");
		location_shine = super.getUniformLocation("useAdditionalShine");

		location_fogDensity = super.getUniformLocation("density");
		location_fogGradient = super.getUniformLocation("gradient");

		location_selectedOption = super.getUniformLocation("selectedOption");
		location_time = super.getUniformLocation("timeNormalised");
	}

	// load to shader!
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadFogParams(float gradient, float density) {
		loadFloat(location_fogGradient, gradient);
		loadFloat(location_fogDensity, density);
	}

	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}

	public void loadActiveLightsCount(int value) {
		super.loadInt(location_activeLightsCount, value);
	}

	public void loadLights(List<Light> lights) {
		int inputLightCont = lights.size();
		if (inputLightCont > MAX_LIGHTS_COUNT)
			inputLightCont = MAX_LIGHTS_COUNT;
		loadActiveLightsCount(inputLightCont);

		for (int i = 0; i < inputLightCont; ++i) {
//			System.out.println(i + "-" +lights.get(i).getPosition());
			super.loadVector3f(location_lightPosition[i], lights.get(i).getPosition());
			super.loadVector3f(location_lightColour[i], lights.get(i).getColour());
			super.loadVector3f(location_attenuation[i], lights.get(i).getAttenuation());
		}
	}

	public void loadShineVariables(float shineDamp, float reflectivity) {
		super.loadFloat(location_shineDamper, shineDamp);
		super.loadFloat(location_reflectivity, reflectivity);
	}

	public void loadfakeLighting(boolean useFakeLigting) {
		super.loadBoolean(location_fakeLighting, useFakeLigting);
	}

	public void loadSkyColor(Vector3f color) {
		super.loadVector3f(location_skyColor, color);
	}

	public void loadNumberOfRows(float numberOfRows) {
		super.loadFloat(location_numberOfRows, numberOfRows);
	}

	public void loadOffset(Vector2f textureOffset) {
		super.loadVector2f(location_offset, textureOffset);
	}

	public void loadClipPlane(Vector4f plane) {
		super.loadVector4f(location_plane, plane);
	}

	public void loadAdditionalShine(boolean useAdditionalShine) {
		super.loadBoolean(location_shine, useAdditionalShine);
	}

	public void addShineBitmap() {
		super.loadInt(location_shineTexture, 1);
	}

	public void loadSelectedOption(boolean selected) {
		super.loadBoolean(location_selectedOption, selected);
	}
	
	public void loadTimeNormalised(float timeNormalised) {
		super.loadFloat(location_time, timeNormalised);
	}
}
