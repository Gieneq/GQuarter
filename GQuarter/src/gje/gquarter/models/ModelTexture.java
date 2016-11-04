package gje.gquarter.models;


public class ModelTexture {

	private TextureLinkData linkData;
	private float shineDamper = 1;
	private float reflectivity = 0;
	
	private int numberOfRows = 1;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	public ModelTexture(int id, int sizePx) {
		linkData = new TextureLinkData(id, sizePx);
	}
	
	public ModelTexture(TextureLinkData data) {
		linkData = new TextureLinkData(data.id, data.sizePx);
	}
	
	public int getId() {
		return linkData.id;
	}
	
	/**To jest wielkosc plamki na powierzchni */
	public float getShineDamper() {
		return shineDamper;
	}

	/**To jest wielkosc plamki na powierzchni */
	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	/** to jest jak mocno oddaje odbity kolor */
	public float getReflectivity() {
		return reflectivity;
	}

	/** to jest jak mocno oddaje odbity kolor */
	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public boolean isHasTransparency() {
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency) {
		this.hasTransparency = hasTransparency;
	}

	public boolean isUseFakeLighting() {
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting) {
		this.useFakeLighting = useFakeLighting;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public int getSizePx() {
		return linkData.sizePx;
	}
	
	
}
