package gje.gquarter.postprocessing;

import gje.gquarter.core.ShaderProgram;

public class VerticalBlurShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/processingVertBlurVertex.vsh";
	private final static String FRAGMENT_FILE = "res/shaders/processingBlurFragment.vsh";

	private int locationInputTexture;
	private int locationHeight;

	public VerticalBlurShader(float height) {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		connectTextureUnits();
		loadPixelHeight(1f / height);
		stop();
	}

	@Override
	protected void getAllUniformLocations() {
		locationInputTexture = super.getUniformLocation("inputTexture");
		locationHeight = super.getUniformLocation("pixelHeight");
	}

	public void loadPixelHeight(float height) {
		super.loadFloat(locationHeight, height);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void connectTextureUnits() {
		super.loadInt(locationInputTexture, 0);
	}
}
