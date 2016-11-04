package gje.gquarter.models;


public class TerrainTexture {
	private int textureId;
	private int sizePx;
	private String name;

	public TerrainTexture(int textureId, int sizePx) {
		this.setTextureId(textureId);
		this.setSizePx(sizePx);
	}

	public TerrainTexture(TextureLinkData data, String name) {
		this.setTextureId(data.id);
		this.setSizePx(data.sizePx);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}

	public int getSizePx() {
		return sizePx;
	}

	public void setSizePx(int sizePx) {
		this.sizePx = sizePx;
	}
}
