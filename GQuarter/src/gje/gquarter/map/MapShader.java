package gje.gquarter.map;

import gje.gquarter.core.ShaderProgram;

public class MapShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/mapVertexShader.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/mapFragmentShader.vsh";

	private int location_bgTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_isolineTexture;
	private int location_slopemapTexture;
	private int location_mode;

	public MapShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_bgTexture = super.getUniformLocation("backgroundTexture");
		location_rTexture = super.getUniformLocation("rTexture");
		location_gTexture = super.getUniformLocation("gTexture");
		location_bTexture = super.getUniformLocation("bTexture");
		location_blendMap = super.getUniformLocation("blendMap");
		location_isolineTexture = super.getUniformLocation("isolineTexture");
		location_slopemapTexture = super.getUniformLocation("slopemapTexture");
		location_mode = super.getUniformLocation("mode");
	}

	public void connectTextureUnits() {
		super.loadInt(location_bgTexture, 0);
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
		super.loadInt(location_isolineTexture, 5);
		super.loadInt(location_slopemapTexture, 6);
	}

	public void loadMode(int mode) {
		super.loadInt(location_mode, mode);
	}
}
