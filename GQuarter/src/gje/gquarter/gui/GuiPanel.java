package gje.gquarter.gui;

import gje.gquarter.gui.fonts.GUIText;
import gje.gquarter.gui.fonts.GuiTextMainRenderer;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector4f;

public abstract class GuiPanel extends Rect2i implements OnClickGui {
	public static final int TYPE_RETANGULAR = 0;
	public static final int TYPE_RADIAL = 1;
	public static final Vector4f HEADER_COLOR_DARK = Maths.convertColor4f(6, 55, 55, 255);
	public static final Vector4f HEADER_COLOR_LIGHT = Maths.convertColor4f(6, 130, 140, 255);
	public static final Vector4f SLIDER_COLOR = Maths.convertColor4f(255, 244, 255, 230);

	private int panelType;
	private String idName;
	private GuiTexture backgroundQuad;
	private GuiTexture headerQuad;
	private GUIText headerText;
	private ArrayList<GuiTexture> nonInteractiveGuis;
	private ArrayList<GuiButton> buttons;
	private ArrayList<GuiSlider> sliders;
	private ArrayList<GUIText> texts;
	private boolean visible;
	protected GuiFrame parentFrame;
	protected boolean clickable;

	public GuiPanel(String idName, Rect2i rect, boolean visible, boolean withHeader, GuiFrame frame, int panelType) {
		super(rect);
		clickable = true;
		if (panelType == TYPE_RADIAL)
			h = w;
		this.panelType = panelType;
		this.idName = idName;
		this.parentFrame = frame;
		this.nonInteractiveGuis = new ArrayList<GuiTexture>();
		this.buttons = new ArrayList<GuiButton>();
		this.sliders = new ArrayList<GuiSlider>();
		this.texts = new ArrayList<GUIText>();

		if ((panelType == TYPE_RETANGULAR) && (withHeader)) {
			headerText = new GUIText(idName, GuiTextMainRenderer.HEADER_TEXT_SIZE, GuiTextMainRenderer.getBasicFont(), 4, 0, rect.w - 4, this, false, this);
			headerText.setColour(1f, 1f, 1f);
			texts.add(headerText);
		}

		backgroundQuad = new GuiTexture(new Rect2i(0, 0, w, h, this), this); // czy_2_tez_0??
		backgroundQuad.useColour(Maths.convertColor4f(6, 55, 55, 40), 1f);
		if (panelType == TYPE_RADIAL) {
			backgroundQuad.useRadius(0.95f, 1f);
		}
		addNoninteractiveGui(backgroundQuad);

		if (withHeader) {
			if (panelType == TYPE_RETANGULAR) {
				headerQuad = new GuiTexture(new Rect2i(0, 0, w, GuiFrame.HEADER_HEIGHT, this), this);
				headerQuad.useColour(Maths.convertColor4f(6, 55, 55, 255), 1f);
				nonInteractiveGuis.add(headerQuad);
			}

			if (panelType == TYPE_RADIAL) {
				int diameter = 2 * GuiFrame.HEADER_HEIGHT;
				int xx = 0 + w / 2 - diameter / 2;
				int yy = 0 + h / 2 - diameter / 2; // to juz jest wzgledem
													// headera...
				headerQuad = new GuiTexture(new Rect2i(xx, yy, diameter, diameter, this), this);
				headerQuad.useColour(Maths.convertColor4f(6, 55, 55, 255), 1f);
				headerQuad.useRadius(0.85f, 1f);
				nonInteractiveGuis.add(headerQuad);
			}
		}

	}

	public Rect2i getGridRect(int iconSize, int gapSize, int gridWidth, int gridHeight, int dy, int numerId, Rect2i parent) {
		// ustalam ile jest przyciskow w siatce i jaki dac margines. Lepiej by
		// nie liczyc tego za kazdym razem tylko przy ustalaniu wymiarow panelu!
		int widthCount = gridWidth / (iconSize + gapSize);
		int widthMargin = (gridWidth - widthCount * (iconSize + gapSize)) / 2;
		int heightCount = gridHeight / (iconSize + gapSize);
		int heightMargin = widthMargin;//(gridHeight - heightCount * (iconSize + gapSize)) / 2;

		// licze pozycje nowego Recta
		int buttonGridX = numerId % widthCount;
		int buttonGridY = numerId / widthCount;
		int rectx = widthMargin + gapSize / 2 + buttonGridX * (iconSize + gapSize);
		int recty = dy + heightMargin + gapSize / 2 + buttonGridY * (iconSize + gapSize);
		return new Rect2i(rectx, recty, iconSize, iconSize, parent);
	}

	public void updateGeneral() {
	}

	public GuiTexture getHeaderQuad() {
		return headerQuad;
	}

	public GuiTexture getBackgroundQuad() {
		return backgroundQuad;
	}

	public ArrayList<GUIText> getTexts() {
		return texts;
	}

	public void addTextField(GUIText text) {
		text.movePixels(0, ((panelType == TYPE_RADIAL) ? 0 : GuiFrame.HEADER_HEIGHT));
		texts.add(text);
	}

	public void addNoninteractiveGui(GuiTexture niGui) {
		niGui.movePx(0, ((panelType == TYPE_RADIAL) ? 0 : GuiFrame.HEADER_HEIGHT));
		nonInteractiveGuis.add(niGui);
	}

	public void addGuiButton(GuiButton button) {
		button.movePixels(0, ((panelType == TYPE_RADIAL) ? 0 : GuiFrame.HEADER_HEIGHT));
		button.setOnClickListener(this);
		buttons.add(button);
	}

	public void addGuiProgressBar(GuiSlider bar) {
		bar.movePixels(0, ((panelType == TYPE_RADIAL) ? 0 : GuiFrame.HEADER_HEIGHT));
		bar.setOnClickListener(this);
		sliders.add(bar);
	}

	public GuiButton getButtonById(String id) {
		for (GuiButton button : buttons) {
			if (button.getIdName().equals(id))
				return button;
		}
		return null;
	}

	public GuiSlider getSldierById(String id) {
		for (GuiSlider sl : sliders) {
			if (sl.getIdName().equals(id))
				return sl;
		}
		return null;
	}

	public boolean isMouseInsideHeader() {
		return headerQuad.isMouseInside();
	}

	public boolean isHeaderLeftClicked() {
		if ((headerQuad == null) || (!clickable))
			return false; // TODO JAKIES KOLKOW W CRODKU CZY COS...
		return headerQuad.isLeftClicked();
	}

	public boolean buttonsProceedWithResult() {
		boolean active = false;
		if (visible) {
			for (GuiSlider slider : sliders) {
				if (slider.update())
					active = true;
			}
			// to ma zapobiec klikaniu podczas przesowania slidera
			if (active)
				return true;
			for (GuiButton button : buttons) {
				// tu wywolywane sa metody z interfacu!
				// klimam zawsze max jedno
				if (button.update())
					active = true;
			}
		}
		return active;
	}

	public void movePanel(int dx, int dy) {
		this.x += dx;
		this.y += dy;
	}

	public void setPositionTopLeft(int newX, int newY) {
		this.x = newX;
		this.y = newY;
	}

	public void forceUpdate() {
		for (GuiTexture t : nonInteractiveGuis)
			t.forceUpdatePositionSize();
		for (GuiButton b : buttons)
			b.forceUpdatePositionSize();
		for (GUIText txt : texts)
			txt.forceUpdatePosition();
		for (GuiSlider slider : sliders)
			slider.forceUpdatePositionSize();
	}

	// TODO PANEL JAKO OBIEKT DO RENDOEROWANIA ALE PRZEZ 2 KLASY RENDERER.. TJ
	// TEKST I TEXTURE

	public void load() {
		GUIMainRenderer.load(this);
	}

	public void remove() {
		GUIMainRenderer.remove(this);
	}

	private void setVisibilityToAllGUIs(boolean visibility) {
		if (visibility)
			load();
		else
			remove();
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		setVisibilityToAllGUIs(visible);

		this.visible = visible;
	}

	public ArrayList<GuiTexture> getNonInteractiveGuis() {
		return nonInteractiveGuis;
	}

	public ArrayList<GuiButton> getButtons() {
		return buttons;
	}

	public ArrayList<GuiSlider> getSliders() {
		return sliders;
	}

	public String getIdName() {
		return idName;
	}

	public boolean isClickable() {
		return clickable;
	}

	public void setClickable(boolean clickable) {
		this.clickable = clickable;
	}
}
