package gje.gquarter.components;

import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;

import org.lwjgl.input.Keyboard;

public class ControlComponent implements BasicComponent, OnKeyEventListener {

	public static final float MAX_MOVEMENT_SPEED = 3 * 1f; // 10 * 1f
	public static final float JUMP_POWER = 7f;// 5
	public static final float MIDAIR_DECELERATION = 0.968f;

	private GravityComponent gravCmp;
	private PhysicalComponent phyCmp;
	private RegionalComponent regCmp;
	private float speedMultiplier;
	private Key keyJump;
	private Key keyForward;
	private Key keyBackward;
	private Key keyLeft;
	private Key keyRight;

	public ControlComponent(GravityComponent gravCmp) {
		this.gravCmp = gravCmp;
		this.phyCmp = gravCmp.getPhysicalComponent();
		this.regCmp = gravCmp.getRegionComponent();
		this.speedMultiplier = MAX_MOVEMENT_SPEED;

		this.keyJump = new Key(Keyboard.KEY_SPACE);
		this.keyJump.setOnClickListener(this);
		
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
	}

	@Override
	public boolean onKeyClick(int keyId) {
		if (!regCmp.isAboveTerrain()) {
			if (keyId == keyJump.getKeyId()) {
				gravCmp.jump(JUMP_POWER);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		if (!regCmp.isAboveTerrain()) {
			if (keyId == keyForward.getKeyId()) {
				phyCmp.getLocalVelocity().z = speedMultiplier;
			}
			if (keyId == keyBackward.getKeyId()) {
				phyCmp.getLocalVelocity().z = -speedMultiplier;
			}
			if (keyId == keyLeft.getKeyId()) {
				phyCmp.getRotationVelocity().ry = speedMultiplier;
			}
			if (keyId == keyRight.getKeyId()) {
				phyCmp.getRotationVelocity().ry = -speedMultiplier;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		if ((keyId == keyForward.getKeyId()) || (keyId == keyBackward.getKeyId()) || (keyId == keyLeft.getKeyId()) || (keyId == keyRight.getKeyId())) {
			phyCmp.getLocalVelocity().x = 0f;
			phyCmp.getLocalVelocity().z = 0f;
			phyCmp.getGlobalVelocity().x = 0f;
			phyCmp.getGlobalVelocity().z = 0f;
			phyCmp.getRotationVelocity().ry = 0f;
			return true;
		}
		return false;
	}

}
