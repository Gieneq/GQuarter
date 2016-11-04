package gje.gquarter.gui.fonts;

import gje.gquarter.core.Loader;
import gje.gquarter.gui.GuiPanel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiTextMainRenderer {

	private static FontRenderer fontRenderer;
	private static Map<GuiPanel, List<GUIText>> texts;
	private static FontType basicFont;
	public static final float HEADER_TEXT_SIZE = 0.78f;
	public static final float PANEL_TEXT_SIZE = 0.9f;
	public static final float SLIDER_TEXT_SIZE = 0.7f;

	public static void init() {
		fontRenderer = new FontRenderer();
		texts = new HashMap<GuiPanel, List<GUIText>>();
		basicFont = new FontType(Loader.loadTextureFiltered("gui/fonts/arsenal", true).id, new File("res/gui/fonts/arsenal.fnt"));
	}

	public static FontType getBasicFont() {
		return basicFont;
	}

	public static void rendererRelease(GuiPanel lovelyPanel) {
		if (texts.get(lovelyPanel) != null)
			fontRenderer.rendererRelease(texts.get(lovelyPanel));
	}

	public static void loadText(GUIText text) {
		GuiPanel panel = text.getParentPanel();
		FontType font = text.getFont();
		TextMeshData tmd = font.loadText(text);
		int vao = Loader.loadToVAO(tmd.getVertexPositions(), tmd.getTextureCoords());
		text.setMeshInfo(vao, tmd.getVertexCount());
		List<GUIText> textBatch = texts.get(panel);
		if (textBatch == null) {
			textBatch = new ArrayList<GUIText>();
			texts.put(panel, textBatch);
		}
		textBatch.add(text);
	}

	public static void removePanelTexts(GuiPanel badlyPanel) {
		texts.remove(badlyPanel);
	}

	public static void removeText(GUIText text) {
		List<GUIText> batch = texts.get(text.getParentPanel());
		if (batch != null) {
			batch.remove(text);
			if (batch.isEmpty())
				texts.remove(batch);
		}
	}

	public static void clearBatchList() {
		texts.clear();
	}

	public static void clean() {
		fontRenderer.cleanUp();
	}
}
