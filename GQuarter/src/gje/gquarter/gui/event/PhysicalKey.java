package gje.gquarter.gui.event;

import org.lwjgl.input.Keyboard;

public class PhysicalKey {
	private int keyId;
	private boolean latch;

	public PhysicalKey(int keyId) {
		this.keyId = keyId;
		this.latch = false;
	}

	public boolean isClicked() {
		return Keyboard.isKeyDown(keyId);
	}
	
	public boolean isLatched() {
		return latch;
	}

	public void setLatched(boolean latch) {
		this.latch = latch;
	}

	public int getKeyId() {
		return keyId;
	}
}
