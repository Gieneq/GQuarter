package gje.gquarter.entity;

import gje.gquarter.core.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class EnvironmentShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/environmentVertex.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/environmentFragment.vsh";

	private int locationProjectionMatrix;
	private int locationViewMatrix;
	private int locationPlane;
	private int locationAnimationValue;
	private int locationHardness;
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
		locationAnimationValue = super.getUniformLocation("animationValue");
		locationHardness = super.getUniformLocation("hardness");
		locationFurthestDistance = super.getUniformLocation("furthestDistance");
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

	public void loadAnimationValue(float timeNormalised) {
		super.loadFloat(locationAnimationValue, timeNormalised);
	}

	public void loadModelParams(float hardnes, float furthestDistance) {
		super.loadFloat(locationHardness, hardnes);
		super.loadFloat(locationFurthestDistance, furthestDistance);
	}
}
