package gje.gquarter.postprocessing;

import gje.gquarter.core.ShaderProgram;

public class BrightShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/processingVertex.vsh";
	private final static String FRAGMENT_FILE = "res/shaders/processingBrightFragment.vsh";

	private int locationInputTexture;
	private int locationThrehold;
	
	public BrightShader(float threshold) {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		connectTextureUnits();
		loadThreshold(threshold);
		stop();
	}
	
	@Override
	protected void getAllUniformLocations() {
		locationInputTexture = super.getUniformLocation("inputTexture");
		locationThrehold = super.getUniformLocation("threshold");
	}
	
	public void loadThreshold(float threshold){
		super.loadFloat(locationThrehold, threshold);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void connectTextureUnits(){
		super.loadInt(locationInputTexture, 0);
	}
}
