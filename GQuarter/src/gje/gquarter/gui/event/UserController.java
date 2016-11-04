package gje.gquarter.gui.event;

import gje.gquarter.core.Core;

import java.util.ArrayList;
import java.util.List;

public class UserController {
	private static List<Key> keys;
	private static List<OnMouseEventListener> mouseListeners;

	public static void init() {
		keys = new ArrayList<Key>();
		mouseListeners = new ArrayList<OnMouseEventListener>();
	}

	public static void addKey(Key k) {
		keys.add(k);
		Core.getInputs().addKey(k.getKeyId());
	}

	public static void addMouseListener(OnMouseEventListener listener) {
		mouseListeners.add(listener);
	}

	public static void updateAll(float dt) {
		/*
		 * Key
		 */
		int awaitingKeyEvents = Core.getInputs().getAwaitingKeyEventsCount();
		for (int i = 0; i < awaitingKeyEvents; ++i) {
			KeyEvent keyEvent = Core.getInputs().getNextKeyEvents();
			for (Key k : keys)
				k.update(keyEvent);
		}
		for (Key k : keys)
			k.updatePressState();

		/*
		 * Mouse
		 */
		int awaitingMouseEvents = Core.getInputs().getAwaitingMouseEventsCount();
//		for (int i = 0; i < awaitingMouseEvents; ++i) {
		if(awaitingMouseEvents > 0) {
			MouseEvent mouseEvent = Core.getInputs().getNextMouseEvents();
			for (OnMouseEventListener e : mouseListeners) {
				if (mouseEvent.getEventType() == MouseEvent.EVENT_ON_ROLL)
					e.onMouseRoll(mouseEvent.getRollValue(), dt);
				if (mouseEvent.getEventType() == MouseEvent.EVENT_ON_PRESS)
					e.onMousePress(mouseEvent.getMouseButton(), mouseEvent.getMouseX(), mouseEvent.getMouseY());
				if (mouseEvent.getEventType() == MouseEvent.EVENT_ON_RELEASE)
					e.onMouseRelease(mouseEvent.getMouseButton(), mouseEvent.getMouseX(), mouseEvent.getMouseY());
				if (mouseEvent.getEventType() == MouseEvent.EVENT_ON_DRAGGING)
					e.onMouseDragging(mouseEvent.getMouseButton(), mouseEvent.getDraggingX(), mouseEvent.getDraggingY(), mouseEvent.getMouseX(), mouseEvent.getMouseY());
			}
		}
	}
}