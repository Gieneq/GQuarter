package gje.gquarter.gui.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.lwjgl.input.Mouse;

public class Inputs extends Thread {
	private static final float FPS_CAP = 60f;
	private static final long INTERVAL_NANOS = (long) ((1f / FPS_CAP) * 1000000000f);
	private boolean running;
	private long lastTime;

	private List<PhysicalKey> keys;
	private Queue<KeyEvent> keyEventsQueue;

	private Queue<MouseEvent> mouseEventsQueue;
	private boolean latchLMB;
	private boolean latchRMB;
	private boolean latchMMB;

	public Inputs() {
		running = true;
		keys = new ArrayList<PhysicalKey>();
		keyEventsQueue = new ConcurrentLinkedQueue<KeyEvent>();
		mouseEventsQueue = new ConcurrentLinkedQueue<MouseEvent>();
		latchLMB = false;
		latchRMB = false;
		latchMMB = false;

		UserController.init();

		start();
	}

	public boolean addKey(int keyId) {
		for (PhysicalKey key : keys) {
			if (key.getKeyId() == keyId)
				return false;
		}
		keys.add(new PhysicalKey(keyId));
		System.out.println("Inputs.addKey: " + keyId);
		return true;
	}

	@Deprecated
	/** Jeden klawisz jest przypisany do wielu uchwytow ktre konroluja dane obiekty*/
	public boolean removeKey(int keyId) {
		for (PhysicalKey key : keys) {
			if (key.getKeyId() == keyId) {
				keys.remove(key);
				return true;
			}
		}
		return false;
	}

	@Override
	public void run() {
		while (running) {
			lastTime = System.nanoTime();
			/*
			 * Update
			 */
			updateKeys();
			// updateMouse();

			long someTime = INTERVAL_NANOS - (System.nanoTime() - lastTime);
			if (someTime > 0l) {
				try {
					Thread.sleep(someTime / 1000000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateKeys() {
		for (PhysicalKey key : keys) {
			if (key.isClicked() && !key.isLatched()) {
				key.setLatched(true);
				KeyEvent event = new KeyEvent(key.getKeyId(), KeyEvent.EVENT_ON_PRESS);
				synchronized (keyEventsQueue) {
					keyEventsQueue.offer(event);
				}
			} else if (!key.isClicked() && key.isLatched()) {
				key.setLatched(false);
				KeyEvent event = new KeyEvent(key.getKeyId(), KeyEvent.EVENT_ON_RELEASE);
				synchronized (keyEventsQueue) {
					keyEventsQueue.offer(event);
				}
			}
		}
	}

	@Deprecated
	private void updateMouse() {
		/*
		 * Roll
		 */
		int rollValue = Mouse.getDWheel();
		int dx = Mouse.getDX();
		int dy = Mouse.getDY();
		if (rollValue != 0) {
			MouseEvent event = new MouseEvent(MouseEvent.ROLL, MouseEvent.EVENT_ON_ROLL);
			event.setRollValue(rollValue);
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		}

		/*
		 * LMB
		 */
		if (Mouse.isButtonDown(MouseEvent.LEFT_MB) && !latchLMB) {
			latchLMB = !latchLMB;
			MouseEvent event = new MouseEvent(MouseEvent.LEFT_MB, MouseEvent.EVENT_ON_PRESS);
			event.setMouseXY(Mouse.getX(), Mouse.getY());
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		} else if (!Mouse.isButtonDown(MouseEvent.LEFT_MB) && latchLMB) {
			latchLMB = !latchLMB;
			MouseEvent event = new MouseEvent(MouseEvent.LEFT_MB, MouseEvent.EVENT_ON_RELEASE);
			event.setMouseXY(Mouse.getX(), Mouse.getY());
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		} else if (Mouse.isButtonDown(MouseEvent.LEFT_MB) && latchLMB) {
			if ((dx != 0) || (dy != 0)) {
				MouseEvent event = new MouseEvent(MouseEvent.LEFT_MB, MouseEvent.EVENT_ON_DRAGGING);
				event.setMouseXY(Mouse.getX(), Mouse.getY());
				event.setDraggingXY(dx, dy);
				synchronized (mouseEventsQueue) {
					mouseEventsQueue.offer(event);
				}
			}
		}

		/*
		 * RMB
		 */
		if (Mouse.isButtonDown(MouseEvent.RIGHT_MB) && !latchRMB) {
			latchRMB = !latchRMB;
			MouseEvent event = new MouseEvent(MouseEvent.RIGHT_MB, MouseEvent.EVENT_ON_PRESS);
			event.setMouseXY(Mouse.getX(), Mouse.getY());
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		} else if (!Mouse.isButtonDown(MouseEvent.RIGHT_MB) && latchRMB) {
			latchRMB = !latchRMB;
			MouseEvent event = new MouseEvent(MouseEvent.RIGHT_MB, MouseEvent.EVENT_ON_RELEASE);
			event.setMouseXY(Mouse.getX(), Mouse.getY());
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		} else if (Mouse.isButtonDown(MouseEvent.RIGHT_MB) && latchRMB) {
			if ((dx != 0) || (dy != 0)) {
				MouseEvent event = new MouseEvent(MouseEvent.RIGHT_MB, MouseEvent.EVENT_ON_DRAGGING);
				event.setMouseXY(Mouse.getX(), Mouse.getY());
				event.setDraggingXY(dx, dy);
				synchronized (mouseEventsQueue) {
					mouseEventsQueue.offer(event);
				}
			}
		}

		/*
		 * MMB
		 */
		if (Mouse.isButtonDown(MouseEvent.MID_MB) && !latchMMB) {
			latchMMB = !latchMMB;
			MouseEvent event = new MouseEvent(MouseEvent.LEFT_MB, MouseEvent.EVENT_ON_PRESS);
			event.setMouseXY(Mouse.getX(), Mouse.getY());
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		} else if (!Mouse.isButtonDown(MouseEvent.MID_MB) && latchMMB) {
			latchMMB = !latchMMB;
			MouseEvent event = new MouseEvent(MouseEvent.MID_MB, MouseEvent.EVENT_ON_RELEASE);
			event.setMouseXY(Mouse.getX(), Mouse.getY());
			synchronized (mouseEventsQueue) {
				mouseEventsQueue.offer(event);
			}
		} else if (Mouse.isButtonDown(MouseEvent.MID_MB) && latchMMB) {
			if ((dx != 0) || (dy != 0)) {
				MouseEvent event = new MouseEvent(MouseEvent.MID_MB, MouseEvent.EVENT_ON_DRAGGING);
				event.setMouseXY(Mouse.getX(), Mouse.getY());
				event.setDraggingXY(dx, dy);
				synchronized (mouseEventsQueue) {
					mouseEventsQueue.offer(event);
				}
			}
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	/*
	 * Keys
	 */
	public synchronized int getAwaitingKeyEventsCount() {
		return keyEventsQueue.size();
	}

	public synchronized boolean isKeyEventAwaiting() {
		return !keyEventsQueue.isEmpty();
	}

	public synchronized KeyEvent getNextKeyEvents() {
		return keyEventsQueue.poll();
	}

	/*
	 * Mouse
	 */
	public synchronized int getAwaitingMouseEventsCount() {
		return mouseEventsQueue.size();
	}

	public synchronized boolean isMouseEventAwaiting() {
		return !mouseEventsQueue.isEmpty();
	}

	public synchronized MouseEvent getNextMouseEvents() {
		return mouseEventsQueue.poll();
	}
}
