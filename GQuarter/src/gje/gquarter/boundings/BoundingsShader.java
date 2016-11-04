package gje.gquarter.boundings;

import gje.gquarter.core.ShaderProgram;
import gje.gquarter.entity.Camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class BoundingsShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/boundingVertexShader.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/boundingFragmentShader.vsh";

	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_plane;
	private int location_viewMatrix;
	private int location_color;
	private int location_select;

	public BoundingsShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_color = super.getUniformLocation("color");
		location_plane = super.getUniformLocation("plane");
		location_select = super.getUniformLocation("select");
	}
	
	public void loadClipPlane(Vector4f plane) {
		super.loadVector4f(location_plane, plane);
	}

	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadProjectionMatrix(Matrix4f matrix) {
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	public void loadColor(Vector4f color) {
		super.loadVector4f(location_color, color);
	}

	public void loadViewMatrix(Camera camera) {
		super.loadMatrix(location_viewMatrix, camera.getViewMatrix());
	}

	public void loadSelectOption(boolean select) {
		super.loadBoolean(location_select, select);
	}
}
