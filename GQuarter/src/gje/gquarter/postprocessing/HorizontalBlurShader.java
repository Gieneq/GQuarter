package gje.gquarter.postprocessing;

import gje.gquarter.core.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/processingHorBlurVertex.vsh";
	private final static String FRAGMENT_FILE = "res/shaders/processingBlurFragment.vsh";

	private int locationInputTexture;
	private int locationWidth;

	public HorizontalBlurShader(float width) {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		connectTextureUnits();
		loadPixelWidth(1f / width);
		stop();
	}

	@Override
	protected void getAllUniformLocations() {
		locationInputTexture = super.getUniformLocation("inputTexture");
		locationWidth = super.getUniformLocation("pixelWidth");
	}

	public void loadPixelWidth(float width) {
		super.loadFloat(locationWidth, width);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	public void connectTextureUnits() {
		super.loadInt(locationInputTexture, 0);
	}
}
