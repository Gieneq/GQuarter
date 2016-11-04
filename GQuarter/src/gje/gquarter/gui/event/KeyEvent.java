package gje.gquarter.gui.event;

public class KeyEvent {
	public static final int EVENT_NONE = 0;
	public static final int EVENT_ON_PRESS = 1;
	public static final int EVENT_ON_RELEASE = 2;

	private int eventType;
	private int eventKey;

	public KeyEvent(int eventKey, int eventType) {
		this.eventKey = eventKey;
		this.eventType = eventType;
	}

	public int getEventType() {
		return eventType;
	}

	public int getKeyEventId() {
		return eventKey;
	}
}
