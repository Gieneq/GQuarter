package gje.gquarter.components;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import gje.gquarter.models.TexturedModel;
import gje.gquarter.toolbox.Rotation3f;

public class RawModelComponent implements BasicComponent {

	private TexturedModel model;
	private int atlasIndex;
	private Vector2f textureAtlasOffset;

	public RawModelComponent(TexturedModel model, int atlasIndex) {
		this.model = model;
		this.atlasIndex = atlasIndex;
		this.textureAtlasOffset = new Vector2f(getTextureXOffset(), getTextureYOffset());
	}

	@Override
	public void update(float dt) {
		// nico :(
	}

	private float getTextureXOffset() {
		int column = atlasIndex % model.getTexture().getNumberOfRows();
		return (float) column / (float) model.getTexture().getNumberOfRows();
	}

	private float getTextureYOffset() {
		int row = atlasIndex / model.getTexture().getNumberOfRows();
		return (float) row / (float) model.getTexture().getNumberOfRows();
	}

	public ModelComponent buildModelComponent(PhysicalComponent physical) {
		return new ModelComponent(model, atlasIndex, physical);
	}

	public ModelComponent buildModelComponent(PhysicalComponent physical, Vector3f meshOffset, Rotation3f meshRotation, float scale) {
		return new ModelComponent(model, atlasIndex, physical, meshOffset, meshRotation, scale);
	}

	public TexturedModel getModel() {
		return model;
	}

	public int getAtlasIndex() {
		return atlasIndex;
	}

	public void setAtlasIndex(int atlasIndex) {
		this.atlasIndex = atlasIndex;
	}

	public Vector2f getTextureAtlasOffset() {
		return textureAtlasOffset;
	}
}
