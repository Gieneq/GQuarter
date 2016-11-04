package gje.gquarter.models;

import gje.gquarter.core.Loader;

public class TexturedModel {
	private RawModel rawModel;
	private ModelTexture texture;
	private ModelTexture glowMap;
	private boolean useGlowMap;

	public TexturedModel(RawModel model, ModelTexture texture) {
		this.rawModel = model;
		this.texture = texture;
		this.glowMap = null; // nie uzywam
		setUseGlowMap(false);
	}

	public TexturedModel(RawModel model, ModelTexture texture, ModelTexture glowMap) {
		this.rawModel = model;
		this.texture = texture;
		this.glowMap = glowMap;
		setUseGlowMap(true);
	}
	
	public static TexturedModel buildTexturedModel(String objFileName, String textureFileName, String glowMapFileName) {
		return Loader.buildTexturedModelWithGlowMap(objFileName, textureFileName, glowMapFileName);
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public ModelTexture getGlowMap() {
		return glowMap;
	}

	/** Obszary objete kolorem czerwonym sa zawsze jasne */
	public boolean isUseGlowMap() {
		return useGlowMap;
	}

	/** Obszary objete kolorem czerwonym sa zawsze jasne */
	public void setUseGlowMap(boolean useGlowMap) {
		this.useGlowMap = useGlowMap;
	}
}
