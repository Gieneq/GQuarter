package gje.gquarter.water;

import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.ShaderProgram;
import gje.gquarter.entity.Camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/waterVertex.vsh";
	private final static String FRAGMENT_FILE = "res/shaders/waterFragment.vsh";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;

	private int location_reflectionTexture;
	private int location_refractionTexture;
	private int location_dudvMap;
	private int location_normalMap;
	private int location_depthMap;
	private int location_nearPlane;
	private int location_farPlane;
	private int location_waterOpacity;
	private int location_waveTime;
	private int location_waveFreq;
	private int location_waveAmpl;
	private int location_flowMap;
	

	private int location_sunColor;
	private int location_skyColor;

	private int location_waterColor;
	private int location_sunPosition;
	private int location_tilingFactor;

	private int location_fogDensity;
	private int location_fogGradient;
	
	private static Vector4f tempVec4 = new Vector4f(0, 0, 0, 1f);

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");

		location_reflectionTexture = getUniformLocation("reflectionTexture");
		location_refractionTexture = getUniformLocation("refractionTexture");
		location_dudvMap = getUniformLocation("dudvMap");
		location_normalMap = getUniformLocation("normalMap");
		location_depthMap = getUniformLocation("depthMap");
		location_nearPlane = getUniformLocation("nearPlane");
		location_farPlane = getUniformLocation("farPlane");
		location_waterOpacity = getUniformLocation("waterOpacity");
		location_waveFreq= getUniformLocation("waveFreqMax");
		location_waveAmpl = getUniformLocation("waveAmplMax");
		location_waveTime = getUniformLocation("time");
		location_flowMap = getUniformLocation("flowMap");

		location_sunColor = getUniformLocation("sunColour");
		location_skyColor = super.getUniformLocation("skyColor");

		location_waterColor = getUniformLocation("bluishColour");
		location_sunPosition = getUniformLocation("sunPosition");
		location_tilingFactor = getUniformLocation("tiling");
		
		location_fogDensity = super.getUniformLocation("density");
		location_fogGradient= super.getUniformLocation("gradient");
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}

	public void loadFogParams(float gradient, float density){
		loadFloat(location_fogGradient, gradient);
		loadFloat(location_fogDensity, density);
	}
	
	public void loadViewMatrix(Camera camera) {
		loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}

	public void loadModelMatrix(Matrix4f modelMatrix) {
		loadMatrix(location_modelMatrix, modelMatrix);
	}

	public void loadWaterOpacity(float opacityNorm){
		loadFloat(location_waterOpacity, opacityNorm);
	}
	
	public void loadTime(){
		loadFloat(location_waveTime, (DisplayManager.getTimeSeconds())%1f);
	}
	
	public void loadWaveParams(float frequency, float amplitude) {
//		loadFloat(location_waveTime, (DisplayManager.getTimeSeconds())%1f);
		//TODO USTAWIAC PONIZSZE TYLKO NA RZADANIE, PEWNIE TYLKO RAZ ALE CO TAM...
		loadFloat(location_waveFreq, frequency);
		loadFloat(location_waveAmpl, amplitude);
	}
	
	public void loadSkyColor(Vector3f color) {
		super.loadVector3f(location_skyColor, color);
	}


	public void loadTilingFactor(float tiling) {
		loadFloat(location_tilingFactor, tiling);
	}

	public void loadWaterColour(Vector4f waterColour) {
		loadVector4f(location_waterColor, waterColour);
	}

	public void loadSunPosition(Vector3f tiling) {
		loadVector3f(location_sunPosition, tiling);
	}

	public void loadNearFarVariables(float near, float far) {
		loadFloat(location_nearPlane, near);
		loadFloat(location_farPlane, far);
	}

	public void loadSunColour(Vector3f sunColour) {
		tempVec4.set(sunColour.x, sunColour.y, sunColour.z);
		loadVector4f(location_sunColor, tempVec4);
	}

	public void connectTextureUnits() {
		super.loadInt(location_reflectionTexture, 0);
		super.loadInt(location_refractionTexture, 1);
		super.loadInt(location_dudvMap, 2);
		super.loadInt(location_normalMap, 3);
		super.loadInt(location_depthMap, 4);
		super.loadInt(location_flowMap, 5);
	}

}