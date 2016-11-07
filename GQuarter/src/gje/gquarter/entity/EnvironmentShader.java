package gje.gquarter.entity;

import gje.gquarter.core.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class EnvironmentShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/environmentVertex.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/environmentFragment.vsh";

	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_plane;
	private int location_time;

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
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_plane = super.getUniformLocation("plane");
		location_time = super.getUniformLocation("timeNormalised");
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}

	public void loadClipPlane(Vector4f plane) {
		super.loadVector4f(location_plane, plane);
	}

	public void loadTimeNormalised(float timeNormalised) {
		super.loadFloat(location_time, timeNormalised);
	}
}
