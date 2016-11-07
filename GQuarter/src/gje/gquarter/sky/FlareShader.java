package gje.gquarter.sky;

import org.lwjgl.util.vector.Vector2f;

import gje.gquarter.core.ShaderProgram;

public class FlareShader extends ShaderProgram {
	private static final String VERTEX_FILE = "res/shaders/flareVertex.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/flareFragment.vsh";

	private int locationOffset;
	private int locationScale;
	private int locationTranslation;
	private int locationTexture;

	private int locationAtlasRows;
	private int brigthtnesFactor;

	public FlareShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		loadInt(locationTexture, 0);
		stop();
	}

	public void loadAtlasValues(Vector2f offset, Vector2f translation) {
		super.loadVector2f(locationOffset, offset);
		super.loadVector2f(locationTranslation, translation);
	}

	public void loadScale(Vector2f scale) {
		super.loadVector2f(locationScale, scale);
	}

	public void loadAtlasRows(int rows) {
		super.loadFloat(locationAtlasRows, 1f * rows);
	}

	public void loadBrightnesFactor(float factor) {
		super.loadFloat(brigthtnesFactor, factor);
	}

	@Override
	protected void getAllUniformLocations() {
		locationTexture = super.getUniformLocation("flareTexture");
		locationOffset = super.getUniformLocation("offset");
		locationTranslation = super.getUniformLocation("translation");
		locationScale = super.getUniformLocation("scale");
		locationAtlasRows = super.getUniformLocation("atlasRows");
		brigthtnesFactor = super.getUniformLocation("brightnesFactor");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
