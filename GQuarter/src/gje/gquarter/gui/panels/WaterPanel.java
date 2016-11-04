package gje.gquarter.gui.panels;

import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.GuiTexture;
import gje.gquarter.toolbox.Rect2i;

public class WaterPanel extends GuiPanel {

	private GuiTexture waterReflectionGui;
	private GuiTexture waterRefractionGui;
	private GuiTexture waterDepthGui;

	public WaterPanel(String idName, Rect2i rect, boolean visible, GuiFrame frame) {
		super(idName, rect, visible,true, frame, GuiPanel.TYPE_RETANGULAR);
		int heightGui = (int) (rect.w * 7f / 13f);

		waterReflectionGui = new GuiTexture(new Rect2i(2, 8, rect.w - 4, heightGui, this), this);
		addNoninteractiveGui(waterReflectionGui);

		waterRefractionGui = new GuiTexture(new Rect2i(2, 8 + heightGui + 8, rect.w - 4, heightGui,this), this);
		addNoninteractiveGui(waterRefractionGui);

		waterDepthGui = new GuiTexture(new Rect2i(2, 8 + 2 * heightGui + 2 * 8, rect.w - 4, heightGui,this), this);
		addNoninteractiveGui(waterDepthGui);
	}

	public void setTexturesFromFBO() {
//		waterReflectionGui.addTexture(WaterRenderer..getReflectionTexture());
//		waterRefractionGui.addTexture(fbo.getRefractionTexture());
//		waterDepthGui.addTexture(fbo.getRefractionDepthTexture());
	}

	@Override
	public boolean onClick(String idName) {
		return false;
	}

	@Override
	public boolean onPress(String idName) {
		return false;
	}

	@Override
	public boolean onRelease(String idName) {
		return false;
	}

}
