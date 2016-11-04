package gje.gquarter.gui;

import gje.gquarter.gui.fonts.GUIText;
import gje.gquarter.gui.fonts.GuiTextMainRenderer;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector4f;

public class GuiSlider extends Rect2i {
	public static final int BORDER = 4;
	public static final int HEIGHT = 18;

	public static final SliderFunction BASIC_FUNCTION = new SliderFunction() {
		@Override
		public float setSliderPosition(float value) {
			return 0;
		}

		@Override
		public float getValueFromSlider(float norm) {
			return 0;
		}
	};

	private GuiTexture bgTexture;
	private GuiTexture fieldTexture;
	private GuiTexture sliderTexture;
	private GUIText valueText;
	private String idName;
	private OnClickGui onClicker;
	private final int offset;
	private float progress;
	private boolean lastPressedState;
	private SliderFunction function;

	public GuiSlider(String idName, Rect2i rect, GuiPanel panel, SliderFunction function) {
		super(rect);
		this.function = function;
		this.offset = BORDER;
		bgTexture = new GuiTexture(new Rect2i(0, 0, w, h, this), panel);
		bgTexture.useColour(GuiPanel.HEADER_COLOR_DARK, 1f);

		Rect2i fieldRect = new Rect2i(0 + offset, 0 + offset, w - 2 * offset, h - 2 * offset, this);
		fieldTexture = new GuiTexture(fieldRect, panel);
		fieldTexture.useColour(GuiPanel.HEADER_COLOR_LIGHT, 1f);

		Rect2i sliderRect = new Rect2i(0, 0, h, h, this);
		sliderTexture = new GuiTexture(sliderRect, panel);
		sliderTexture.useColour(GuiPanel.SLIDER_COLOR, 1f);
		sliderTexture.useRadius(0.95f, 1f);

		valueText = new GUIText(idName, GuiTextMainRenderer.SLIDER_TEXT_SIZE, GuiTextMainRenderer.getBasicFont(), 0, 0, w, this, true, panel);
		valueText.setColour(1f, 1f, 1f);

		this.idName = idName;
		updateName();
	}

	public void setOnClickListener(OnClickGui onClicker) {
		this.onClicker = onClicker;
	}

	public void setSliderColour(float r, float g, float b, float a) {
		sliderTexture.getColour().set(r, g, b, a);
	}

	public void setValue(float value) {
		setProgress(function.setSliderPosition(value));
	}

	public float getValue() {
		return function.getValueFromSlider(getProgress());
	}

	private void setProgress(float normPerc) {
		progress = Maths.clampF(normPerc, 0f, 1f);
		sliderTexture.x = (int) ((fieldTexture.w - sliderTexture.w / 2) * progress);
		forceUpdatePositionSize();
	}

	public void updateSliderPos() {
		sliderTexture.x = Mouse.getX() - fieldTexture.getGlobalX();// -
																	// sliderTexture.w/2;
		if (sliderTexture.x > fieldTexture.w - sliderTexture.w / 2)
			sliderTexture.x = fieldTexture.w - sliderTexture.w / 2;
		if (sliderTexture.x < 0)
			sliderTexture.x = 0;
		forceUpdatePositionSize();

	}

	private float getProgress() {
		progress = sliderTexture.x * 1f / (fieldTexture.w - sliderTexture.w / 2);
		return progress;
	}

	public void forceUpdatePositionSize() {
		bgTexture.forceUpdatePositionSize();
		fieldTexture.forceUpdatePositionSize();
		sliderTexture.forceUpdatePositionSize();
		valueText.forceUpdatePosition();
		updateName();
	}

	public void updateName() {
		valueText.setText(idName + ": " + getValue());
	}

	public boolean update() {
		if (onClicker != null) {
			if (!lastPressedState) {
				if (fieldTexture.isLeftClicked()) {
					onClicker.onClick(this.getIdName());
					lastPressedState = true;
//					updateName();
				}
			} else {
				// podtrzymaj...
				updateSliderPos();
				onClicker.onPress(this.getIdName());
//				updateName();
				if (!Mouse.isButtonDown(GuiFrame.MOUSE_LMB)) {
					onClicker.onRelease(this.getIdName());
					lastPressedState = false;
//					updateName();
				}
			}
		}
		return lastPressedState;
	}

	public Vector4f getSliderColour() {
		return sliderTexture.getColour();
	}

	public String getIdName() {
		return idName;
	}

	public GuiTexture getTextureBackground() {
		return bgTexture;
	}

	public GuiTexture getTextureInnerField() {
		return fieldTexture;
	}

	public GuiTexture getTextureSlider() {
		return sliderTexture;
	}

	public GUIText getValueText() {
		return valueText;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		return (int) (getProgress() * 100f) + "%";
	}

	/*
	 * SKALOWANIE, PRZESUNIECIA
	 */

	public void setSize(int width, int height) {
		w = width;
		h = height;
		bgTexture.setSizeWithTopLeft(width, height);
		fieldTexture.setSizeWithTopLeft(width - 2 * offset, height - 2 * offset);
		sliderTexture.setSizeWithCenter(height, height);// ???
	}

	public void setPositionTopLeft(int newX, int newY) {
		x = newX;
		y = newY;
		bgTexture.setTopLeftPx(newX, newY);
		fieldTexture.setTopLeftPx(newX + offset, newY + offset);
		sliderTexture.setTopLeftPx(newX + offset + (int) (progress * (fieldTexture.w)) - sliderTexture.w / 2, newY + offset);
	}

	/**
	 * WARNING! Can make huge errors when using too often with float conversion!
	 */
	public void movePixels(int dx, int dy) {
		this.x += dx;
		this.y += dy;
		forceUpdatePositionSize();
	}
}
