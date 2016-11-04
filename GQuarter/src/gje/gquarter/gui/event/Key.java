package gje.gquarter.gui.event;



public class Key {

	private int keyboardKeyId;
	// lacz przyda sie do press
	private boolean latch;
	private OnKeyEventListener onClicker;

	public Key(int keyboardKeyId) {
		this.keyboardKeyId = keyboardKeyId;
		this.latch = false;
		UserController.addKey(this);
	}

	public void setOnClickListener(OnKeyEventListener click) {
		this.onClicker = click;
	}
	
	public boolean isClicked(){
		return latch;
	}

	public void update(KeyEvent keyEvent) {
		// jezeli to ten przycisk to wykonaj zdarzenia, jezeli nie to zwykly
		// update w tym zdarzenie przytrzymania przycisku jezeli nie zostal
		// puszczony
		if (keyEvent.getKeyEventId() == keyboardKeyId) {
			int eventType = keyEvent.getEventType();
			if (eventType == KeyEvent.EVENT_ON_PRESS) {
				onClicker.onKeyClick(keyboardKeyId);
				latch = true;
			} else if (eventType == KeyEvent.EVENT_ON_RELEASE) {
				onClicker.onKeyRelease(keyboardKeyId);
				latch = false;
			}
		}
	}
	public void updatePressState(){
		if (latch)
			onClicker.onKeyPress(keyboardKeyId);
	}

	public int getKeyId() {
		return keyboardKeyId;
	}
}
