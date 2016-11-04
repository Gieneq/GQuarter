package gje.gquarter.gui.event;

public class MouseEvent {
	public static final int EVENT_NONE = 0;
	public static final int EVENT_ON_PRESS = 1;
	public static final int EVENT_ON_RELEASE = 2;
	public static final int EVENT_ON_DRAGGING = 2;
	public static final int EVENT_ON_ROLL = 4;

	public static final int LEFT_MB = 0;
	public static final int RIGHT_MB = 1;
	public static final int MID_MB = 2;
	public static final int ROLL = 3;

	private int mouseButton;
	private int eventType;
	private int rollValue;
	private int mouseX, mouseY;
	private int draggingX, draggingY;

	public MouseEvent(int mouseButton, int eventType) {
		this.mouseButton = mouseButton;
		this.eventType = eventType;
		this.rollValue = 0;
		this.mouseX = 0;
		this.mouseY = 0;
		this.draggingX = 0;
		this.draggingY = 0;
	}

	public void setMouseXY(int x, int y) {
		mouseX = x;
		mouseY = y;
	}

	public void setDraggingXY(int dx, int dy) {
		draggingX = dx;
		draggingY = dy;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getDraggingX() {
		return draggingX;
	}

	public int getDraggingY() {
		return draggingY;
	}

	public int getRollValue() {
		return rollValue;
	}

	public void setRollValue(int value) {
		this.rollValue = value;
	}

	public int getMouseButton() {
		return mouseButton;
	}

	public int getEventType() {
		return eventType;
	}
}
