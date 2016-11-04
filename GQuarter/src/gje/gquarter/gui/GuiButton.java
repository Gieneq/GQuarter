package gje.gquarter.gui;

import org.lwjgl.util.vector.Vector4f;

import gje.gquarter.core.Loader;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;

public class GuiButton extends Rect2i {
	@Deprecated
	private static final int OFFSET = 3;
	private static final float MIX_ABOVE = 0.7f;
	private static final float MIX_PRESSED = 0.4f;
	private static final float MIX_RELEASED = 1.0f;
	public static final Vector4f COLOR_BG_GRAYOUT = Maths.convertColor4f(128, 128, 128, 255);
	public static final Vector4f COLOR_BG_REGULAR = Maths.convertColor4f(6, 55, 57, 255);
	public static final Vector4f COLOR_BG_TOGGLE_ON = Maths.convertColor4f(3, 155, 17, 255);
	public static final Vector4f COLOR_BG_TOGGLE_OFF = Maths.convertColor4f(225, 57, 30, 255);
	public static final Vector4f COLOR_CLICKED = Maths.convertColor4f(255, 255, 255, 62);

	private static final float POP_SIZE = 1.22f;

	private GuiTexture textureBg;
	private GuiTexture icon;
	private String idName;
	private OnClickGui onClicker;
	private int poppedWidth, poppedHeight;
	private int regularWidth, regularHeight;
	private boolean lastPressedState;
	private boolean popState;
	private boolean toggleMode;
	private boolean toggleState;
	private boolean active;

	/**
	 * Watchout textures dont have references, but using set interface removes
	 * this problem.
	 */
	public GuiButton(String idName, String textureIcon, Rect2i rect, GuiPanel panel) {
		super(rect);
		this.textureBg = new GuiTexture(new Rect2i(0, 0, w, h, this), panel);
		this.textureBg.useColour(COLOR_BG_REGULAR, MIX_RELEASED); // potem ma
																	// byc 0!

		this.icon = new GuiTexture(new Rect2i(OFFSET, OFFSET, w - 2 * OFFSET, h - 2 * OFFSET, this), panel);
		this.icon.useTexture(Loader.loadTextureFiltered(textureIcon, true).id);

		this.idName = idName;
		this.onClicker = null;
		this.lastPressedState = false;

		regularWidth = w;
		regularHeight = h;

		poppedWidth = (int) (w * POP_SIZE);
		poppedWidth = Maths.clampI(poppedWidth, w+3, w+10);
		poppedHeight = (int) (h * POP_SIZE);
		poppedHeight = Maths.clampI(poppedHeight, h+3, h+10);
		
		popState = false;
		setToggleMode(false);
		setToggleState(false);
		setActive(true);
	}

	public void setRoundedBasic() {
		this.textureBg.useRadius(0.95f, 1f);
		this.icon.useRadius(0.9f, 1f);
	}

	public void setOnClickListener(OnClickGui onClicker) {
		this.onClicker = onClicker;
	}

	public boolean update() {
		if ((onClicker != null) && active) {

			if (isLeftClicked()) {
				// KLIKAM PRZYCISK
				if (!lastPressedState) {
					if (isMouseInside()) {
						onClicker.onClick(this.getIdName());
						toggleState = !toggleState;
						toggleModeProceed(toggleState);
					}
					lastPressedState = true;
				} else {
					// podtrzymaj...
					onClicker.onPress(this.getIdName());
					if (!toggleMode)
						textureBg.setMixColorValue(MIX_PRESSED);
				}
			} else {
				if (isMouseInside()) {
					if (lastPressedState) {
						// odklikam

						onClicker.onRelease(this.getIdName());
						lastPressedState = false;
					} else {
						if (!toggleMode) {
							textureBg.setMixColorValue(MIX_ABOVE);
							updateTexturePopEffect(true);
						}
					}
				} else {
					if (!toggleMode) {
						textureBg.setMixColorValue(MIX_RELEASED);
						updateTexturePopEffect(false);
					}
				}
			}

		}
		return lastPressedState;
	}

	private void toggleModeProceed(boolean state) {
		if (toggleMode && active) {
			if (state)
				textureBg.useColour(COLOR_BG_TOGGLE_ON, MIX_RELEASED);
			else
				textureBg.useColour(COLOR_BG_TOGGLE_OFF, MIX_RELEASED);

			// gdy klikniety mala ikonka
			updateTexturePopEffect(!state);
		}
	}

	private void updateTexturePopEffect(boolean pop) {
		if (pop && !popState) {
			setSizeCentered(poppedWidth, poppedHeight);
			popState = true;
		} else if (!pop && popState) {
			setSizeCentered(regularWidth, regularHeight);
			popState = false;
		}
	}

	public String getIdName() {
		return idName;
	}

	public GuiTexture getTextureBg() {
		return textureBg;
	}

	public GuiTexture getTextureIcon() {
		return icon;
	}

	/*
	 * SKALOWANIE, PRZESUNIECIA
	 */

	public void setSize(int width, int height) {
		this.w = width;
		this.h = height;
		textureBg.setSizeWithTopLeft(width, height);
		icon.setSizeWithTopLeft(width - 2 * OFFSET, height - 2 * OFFSET);
	}

	public void setSizeCentered(int width, int height) {
		this.w = width;
		this.h = height;
		textureBg.setSizeWithCenter(width, height);
		icon.setSizeWithCenter(width - 2 * OFFSET, height - 2 * OFFSET);
	}

	public void setPositionTopLeft(int newX, int newY) {
		this.x = newX;
		this.y = newY;
	}

	public void setPositionCentered(int newCenterX, int newCenterY) {
		// LOOOOOL
		this.x = newCenterX - poppedWidth / 2;
		this.y = newCenterY - poppedHeight / 2;
	}

	public boolean isToggleState() {
		return toggleState;
	}

	public void setToggleState(boolean toggleState) {
		this.toggleState = toggleState;
		toggleModeProceed(toggleState);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
		if (!active) {
			textureBg.useColour(COLOR_BG_GRAYOUT, 0.5f);
			icon.useColour(COLOR_BG_GRAYOUT, 0.5f);
			updateTexturePopEffect(false);
		} else {
			// aktualizuje kolory itp
			setToggleMode(isToggleMode());
			icon.notUseColor();
		}
	}

	public boolean isToggleMode() {
		return toggleMode;
	}

	public void setToggleMode(boolean toggleMode) {
		this.toggleMode = toggleMode;
		if (!toggleMode)
			textureBg.useColour(COLOR_BG_REGULAR, textureBg.getMixColorValue());
		toggleModeProceed(toggleState);
	}

	/**
	 * WARNING! Can make huge errors when using too often with float conversion!
	 */
	public void movePixels(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		forceUpdatePositionSize();
	}

	public void forceUpdatePositionSize() {
		textureBg.forceUpdatePositionSize();
		icon.forceUpdatePositionSize();
	}
}
