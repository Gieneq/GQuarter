package gje.gquarter.bilboarding;

import gje.gquarter.core.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SunShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/sunVertex.vsh";
	private final static String FRAGMENT_FILE = "res/shaders/sunFragment.vsh";


	private int location_modelViewatrix;
	private int location_projectionMatrix;
	private int location_color;

	public SunShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_modelViewatrix = super.getUniformLocation("mvMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_color = super.getUniformLocation("color");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		
	}

	public void loadProjectionMatrix(Matrix4f projectionMatrix) {
		super.loadMatrix(location_projectionMatrix, projectionMatrix);
	}
	
	public void loadMVMatrix(Matrix4f mv){
		super.loadMatrix(location_modelViewatrix, mv);
	}
	
	public void loadColor(Vector3f color) {
		super.loadVector3f(location_color, color);
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
	}
	
}
