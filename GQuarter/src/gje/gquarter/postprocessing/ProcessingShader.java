package gje.gquarter.postprocessing;

import gje.gquarter.core.ShaderProgram;

public class ProcessingShader extends ShaderProgram {

	private final static String VERTEX_FILE = "res/shaders/processingVertex.vsh";
	private final static String FRAGMENT_FILE = "res/shaders/processingFragment.vsh";

	private int locationInputTexture;
	private int locationBloomTexture;
//	private int locationDepthTexture;
	
	public ProcessingShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		connectTextureUnits();
		stop();
	}
	
	@Override
	protected void getAllUniformLocations() {
		locationInputTexture = super.getUniformLocation("inputTexture");
		locationBloomTexture = super.getUniformLocation("bloomTexture");
//		locationDepthTexture = super.getUniformLocation("depthTexture");
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void connectTextureUnits(){
		super.loadInt(locationInputTexture, 0);
		super.loadInt(locationBloomTexture, 1);
//		super.loadInt(locationDepthTexture, 2);
	}
}
