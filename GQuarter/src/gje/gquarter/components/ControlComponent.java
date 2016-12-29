package gje.gquarter.components;

import gje.gquarter.core.MainRenderer;
import gje.gquarter.events.OnCameraUpdateListener;
import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;
import gje.gquarter.toolbox.Maths;

import org.lwjgl.input.Keyboard;

public class ControlComponent implements BasicComponent, OnKeyEventListener {

	public static final float MAX_MOVEMENT_SPEED = 10f;
	public static final float JUMP_POWER = 7f;

	private PhysicalComponent phyCmp;
	private RegionalComponent regCmp;
	private float speedMultiplier;
	private Key keyUp;
	private Key keyDown;
	private Key keyForward;
	private Key keyBackward;
	private Key keyLeft;
	private Key keyRight;

	public ControlComponent(PhysicalComponent phy, RegionalComponent reg) {
		this.phyCmp = phy;
		this.regCmp = reg;
		this.speedMultiplier = MAX_MOVEMENT_SPEED;

		this.keyUp = new Key(Keyboard.KEY_SPACE);
		this.keyUp.setOnClickListener(this);
		this.keyDown = new Key(Keyboard.KEY_LSHIFT);
		this.keyDown.setOnClickListener(this);

		this.keyForward = new Key(Keyboard.KEY_W);
		this.keyForward.setOnClickListener(this);
		this.keyBackward = new Key(Keyboard.KEY_S);
		this.keyBackward.setOnClickListener(this);
		this.keyLeft = new Key(Keyboard.KEY_A);
		this.keyLeft.setOnClickListener(this);
		this.keyRight = new Key(Keyboard.KEY_D);
		this.keyRight.setOnClickListener(this);
	}

	@Override
	public void update(float dt) {
		this.phyCmp.getRotation().ry = Maths.PI - MainRenderer.getSelectedCamera().getYaw();

		if (!regCmp.isAboveTerrain())
			phyCmp.getPosition().y = regCmp.getTerrainHeight();
	}

	@Override
	public boolean onKeyClick(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		if (keyId == keyUp.getKeyId()) {
			phyCmp.getLocalVelocity().y = speedMultiplier;
		}
		if (keyId == keyDown.getKeyId()) {
			if (regCmp.isAboveTerrain())
				phyCmp.getLocalVelocity().y = -speedMultiplier;
		}

		if (keyId == keyForward.getKeyId()) {
			phyCmp.getLocalVelocity().z = speedMultiplier;
		}
		if (keyId == keyBackward.getKeyId()) {
			phyCmp.getLocalVelocity().z = -speedMultiplier;
		}
		if (keyId == keyLeft.getKeyId()) {
			phyCmp.getLocalVelocity().x = speedMultiplier;
		}
		if (keyId == keyRight.getKeyId()) {
			phyCmp.getLocalVelocity().x = -speedMultiplier;
		}
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		if ((keyId == keyForward.getKeyId()) || (keyId == keyBackward.getKeyId()) || (keyId == keyLeft.getKeyId()) || (keyId == keyRight.getKeyId()) || (keyId == keyUp.getKeyId()) || (keyId == keyDown.getKeyId())) {
			phyCmp.stopAll();
			return true;
		}
		return false;
	}
}
