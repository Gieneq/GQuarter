package gje.gquarter.gui.panels;

import gje.gquarter.core.DisplayManager;
import gje.gquarter.gui.GuiButton;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;

public abstract class RadialGuiPanel extends GuiPanel {
	protected static final int ANIMATION_STATE_HIDDEN = 0;
	protected static final int ANIMATION_STATE_OPENING = 1;
	protected static final int ANIMATION_STATE_OPENED = 2;
	protected static final int ANIMATION_STATE_HIDING = 3;
	protected static final float ANGULAR_SPEED_SLOW = 5.0f;
	protected static final float ANGULAR_SPEED_EVERAGE = 7.5f;
	protected static final float ANGULAR_SPEED_FAST = 10.0f;

	protected float minAngle, maxAngle;
	protected float anglePerSlot;
	protected float currentAngle;
	protected float anglarSpeed;
	protected int animationState;
	protected int buttonsCount;

	public RadialGuiPanel(String idName, Rect2i rect, boolean visible, float minAngle, float maxAngle, GuiFrame frame) {
		super(idName, rect, visible, true, frame, GuiPanel.TYPE_RADIAL);
		this.minAngle = minAngle; // TODO CZEMU_NIE_0.5?
		this.maxAngle = maxAngle;
		this.currentAngle = minAngle;
		this.anglarSpeed = ANGULAR_SPEED_EVERAGE;
	}

	public GuiButton buttonCircularBuilder(String idName, String iconPath, int diameter, float initialAngle) {
		GuiButton butty = new GuiButton(idName, iconPath, new Rect2i(0, 0, diameter, diameter, this), this);
		butty.setRoundedBasic();
		setAngularPosition(butty, initialAngle);
		addGuiButton(butty);
		return butty;
	}

	public void setButtonsCount() {
		this.buttonsCount = getButtons().size();
		this.anglePerSlot = (maxAngle - minAngle) / (buttonsCount - 1);
	}

	public void setAngularPosition(GuiButton butty, float angle) {
		float radius = this.w / 2f;
		int newX = (int) (radius * (1f + Maths.sin(angle)));
		int newY = (int) (radius * (1f - Maths.cos(angle)));
		butty.setPositionCentered(newX, newY);
		butty.forceUpdatePositionSize();
	}

	@Override
	public void setVisible(boolean visible) {
		if (!isVisible() && visible) {
			for (GuiButton b : getButtons()) {
				setAngularPosition(b, minAngle);
			}
			currentAngle = minAngle;
			animationState = ANIMATION_STATE_OPENING;
			super.setVisible(true);
		}
		if (isVisible() && !visible) {
			animationState = ANIMATION_STATE_HIDING;
			return;
		}
	}

	@Override
	public void updateGeneral() {
		super.updateGeneral();
		if (animationState == ANIMATION_STATE_OPENING) {
			currentAngle += anglarSpeed * DisplayManager.getDtSec();
			for (int i = 0; i < buttonsCount; ++i) {
				if (currentAngle > i * anglePerSlot + minAngle)
					setAngularPosition(getButtons().get(i),  i * anglePerSlot + minAngle);
				else
					setAngularPosition(getButtons().get(i), currentAngle);
			}
			if (currentAngle >= maxAngle){
				animationState = ANIMATION_STATE_OPENED;
				currentAngle = maxAngle;
			}
		}

		if ((animationState == ANIMATION_STATE_HIDING)) {
			currentAngle -= anglarSpeed * DisplayManager.getDtSec();
			for (int i = 0; i < buttonsCount; ++i) {
				if (currentAngle > i * anglePerSlot + minAngle)
					setAngularPosition(getButtons().get(i), i * anglePerSlot + minAngle);
				else
					setAngularPosition(getButtons().get(i), currentAngle);
			}
			if (currentAngle <= minAngle) {
				currentAngle = minAngle;
				animationState = ANIMATION_STATE_HIDDEN;
				super.setVisible(false);
			}
		}
	}

	public int getRadiusBasic() {
		return (w + h) / 4; // srednia
	}
}
