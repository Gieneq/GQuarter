package gje.gquarter.gui;

import gje.gquarter.core.MainRenderer;
import gje.gquarter.gui.fonts.GUIText;
import gje.gquarter.gui.fonts.GuiTextMainRenderer;

import java.util.ArrayList;
import java.util.List;

public class GUIMainRenderer {
	private static List<GuiPanel> panels;
	private static boolean visible;

	/** Initialization */
	public static void init() {
		panels = new ArrayList<GuiPanel>();
		GuiTextureRenderer.init();
		GuiTextMainRenderer.init();
	}

	/** Loading panel, storing its textures and texts in memory */
	public static void load(GuiPanel panel) {
		for (GuiPanel gp : panels) {
			if (gp == panel)
				return;
		}
		panels.add(panel);
		for (GuiTexture gt : panel.getNonInteractiveGuis())
			GuiTextureRenderer.loadGuiTexture(gt);

		for (GuiButton b : panel.getButtons()) {
			GuiTextureRenderer.loadGuiTexture(b.getTextureBg());
			GuiTextureRenderer.loadGuiTexture(b.getTextureIcon());
		}
		for (GuiSlider gpb : panel.getSliders()) {
			GuiTextureRenderer.loadGuiTexture(gpb.getTextureBackground());
			GuiTextureRenderer.loadGuiTexture(gpb.getTextureInnerField());
			GuiTextureRenderer.loadGuiTexture(gpb.getTextureSlider());
			GuiTextMainRenderer.loadText(gpb.getValueText());
		}

		for (GUIText txt : panel.getTexts())
			GuiTextMainRenderer.loadText(txt);

	}

	/** Removing panel from lists */
	public static void remove(GuiPanel panel) {
		panels.remove(panel);
		GuiTextureRenderer.removePanelTextures(panel);
		GuiTextMainRenderer.removePanelTexts(panel);
	}

	public static void moveOntop(GuiPanel topPanel) {
		panels.remove(topPanel);
		panels.add(topPanel);
	}

	/** Invoking process of rendering keeping panels order! */
	public static void rendererRelease() {
		boolean wfMode = MainRenderer.isWireframeModeOn();
		MainRenderer.disableWireframeMode();
		for (GuiPanel panel : panels) {
			GuiTextureRenderer.rendererRelease(panel);
			GuiTextMainRenderer.rendererRelease(panel);
		}
		MainRenderer.setWireframeMode(wfMode);
	}

	public static void clean() {
		GuiTextureRenderer.clean();
		GuiTextMainRenderer.clean();
	}

	public static void clearBatchList() {
		GuiTextureRenderer.clearBatchList();
		GuiTextMainRenderer.clearBatchList();
	}

	public static boolean isVisible() {
		return visible;
	}

	public static void setVisible(boolean visible) {
		GUIMainRenderer.visible = visible;
	}
}
