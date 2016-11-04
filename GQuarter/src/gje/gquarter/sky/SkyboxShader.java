package gje.gquarter.sky;

import gje.gquarter.core.ShaderProgram;
import gje.gquarter.entity.Camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/skyboxVertexShader.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/skyboxFragmentShader.vsh";

	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColour;
	private int location_color;
	private int location_foglimit;
	private int location_scale;
	private Matrix4f matrix;

	public SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		matrix = new Matrix4f();
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		matrix.load(camera.getViewMatrix());
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		super.loadMatrix(location_viewMatrix, matrix);
	}

	public void loadFogColor(Vector3f color) {
		super.loadVector3f(location_fogColour, color);
	}

	public void loadSkyColor(Vector3f color) {
		super.loadVector3f(location_color, color);
	}

	/**
	 * @param limit
	 *            - from 0 to 1
	 */
	public void loadFogLimit(float limit) {
		super.loadFloat(location_foglimit, limit);
	}

	public void loadBoxScale(float scale) {
		super.loadFloat(location_scale, scale);
	}

	public void connectTextureUnits() {
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColour = super.getUniformLocation("fogColor");
		location_color = super.getUniformLocation("skyColor");
		location_foglimit = super.getUniformLocation("limitFog");
		location_scale = super.getUniformLocation("scale");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
