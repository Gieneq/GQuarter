package gje.gquarter.gui.event;

public interface OnMouseEventListener {
	public abstract boolean onMousePress(int mouseButtonId, int mouseX, int mouseY);
	public abstract boolean onMouseRelease(int mouseButtonId, int mouseX, int mouseY);
	public abstract boolean onMouseDragging(int mouseButtonId, int deltaX, int deltaY, int currentX, int currentY);
	public abstract boolean onMouseRoll(float deltaValue, float dt);
}
