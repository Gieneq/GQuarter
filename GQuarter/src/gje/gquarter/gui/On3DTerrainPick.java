package gje.gquarter.gui;

public interface On3DTerrainPick {

	public abstract boolean on3DClick(float x, float y, float z, int buttonId);
	public abstract boolean on3DPress(float x, float y, float z, int buttonId);
	public abstract boolean on3DHover(float x, float y, float z);
	public abstract boolean on3DRelease(float x, float y, float z, int buttonId);
}
