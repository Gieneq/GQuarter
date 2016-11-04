package gje.gquarter.gui.panels;

import gje.gquarter.components.RegionalComponent;
import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiTexture;
import gje.gquarter.map.MapRenderer;
import gje.gquarter.postprocessing.ProcessingRenderer;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;
import gje.gquarter.water.WaterRenderer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class MiniMapPanel extends RadialGuiPanel {

	private GuiTexture mapTexture;
	private GuiTexture rose;
//	private GuiTexture mapPoint;
	private int pointerSize;

	public MiniMapPanel(String idName, int panelX, int panelY, int radius, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, radius * 2, radius * 2, null), visibility, Maths.PI2 * 0.45f, Maths.PI2 * 0.80f, frame);
		pointerSize = w - 2 * GuiFrame.OFFSET;
		if (pointerSize > h - 2 * GuiFrame.OFFSET)
			pointerSize = h - 2 * GuiFrame.OFFSET;
		pointerSize = (int) (pointerSize * 0.052f);

		buttonCircularBuilder("closeThis", "gui/icons/closeIcon", parentFrame.getIconSize(), minAngle);
		buttonCircularBuilder("resize", "gui/icons/expandIcon", parentFrame.getIconSize(), minAngle);
		buttonCircularBuilder("plus", "gui/icons/zoomplus", parentFrame.getIconSize(), minAngle);
		buttonCircularBuilder("minus", "gui/icons/zoomminus", parentFrame.getIconSize(), minAngle);
		buttonCircularBuilder("mode", "gui/icons/mapicon", parentFrame.getIconSize(), minAngle);

		setButtonsCount();

		Rect2i rect = new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET, w - 2 * GuiFrame.OFFSET, h - 2 * GuiFrame.OFFSET, this);
		mapTexture = new GuiTexture(rect, this);
		mapTexture.useTexture(MapRenderer.getMapTexture());
		mapTexture.useRadius(0.96f, 0.98f);
		addNoninteractiveGui(mapTexture);

		int roseOffset = (int) (w * 0.4f);
		Rect2i rectRose = new Rect2i(GuiFrame.OFFSET + roseOffset, GuiFrame.OFFSET + roseOffset, w - 2 * (GuiFrame.OFFSET + roseOffset), h - 2 * (GuiFrame.OFFSET + roseOffset), this);
		rose = new GuiTexture(rectRose, this);
		rose.useTexture(Loader.loadTextureFiltered("gui/icons/indicator", true).id);
		addNoninteractiveGui(rose);

		setClickable(false);
		super.setVisible(visibility);
	}

	@Override
	public void updateGeneral() {
		super.updateGeneral();
		RegionalComponent reg = parentFrame.getPlayer().getRegionalComponentIfHaving();
		float hmSize = reg.getRegion().getTarrain().getVertexCount();		

		rose.setRotation(-MainRenderer.getSelectedCamera().getYaw());
		
		Vector2f relative = reg.getRelativePosition();
		float translationX = -(relative.x / hmSize) + 0.5f;
		float translationY = -(relative.y / hmSize) + 0.5f;
		float zooom = this.mapTexture.getTextureZoom();
		float trZoom = (1f - (1f/zooom))/2f;

		this.mapTexture.getTextureTranslation().set(-translationX + trZoom, -translationY + trZoom);
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
		if (idName == "mode") {
			if (MapRenderer.getMode() == MapRenderer.SATELITE_MAP_MODE) {
				MapRenderer.setMode(MapRenderer.ISOLINE_MAP_MODE);
				mapTexture.useTexture(MapRenderer.getMapTexture());
			}
			else if (MapRenderer.getMode() == MapRenderer.ISOLINE_MAP_MODE) {
				MapRenderer.setMode(MapRenderer.SLOPE_MAP_MODE);
				mapTexture.useTexture(MapRenderer.getMapTexture());
			}
			else if (MapRenderer.getMode() == MapRenderer.SLOPE_MAP_MODE) {
				MapRenderer.setMode(MapRenderer.SATELITE_MAP_MODE);
				mapTexture.useTexture(MapRenderer.getMapTexture());
			}
			return true;
		}
		if (idName == "resize") {
			MapPanel map = parentFrame.getMapPanel();
			if (!map.isVisible())
				map.setVisible(true);
			if (this.isVisible())
				this.setVisible(false);
			return true;
		}
		if (idName == "closeThis") {
			this.setVisible(false);
		}
		if (idName == "plus") {
			float zooom = this.mapTexture.getTextureZoom() * 2f;
			this.mapTexture.setTextureZoom(zooom);
			float tr = (1f - (1f/zooom))/2f;
			this.mapTexture.getTextureTranslation().set(tr, tr);
			System.out.println("+zz" + mapTexture.getTextureZoom());
		}
		if (idName == "minus") {
			float zooom = this.mapTexture.getTextureZoom() / 2f;
			this.mapTexture.setTextureZoom(zooom);
			float tr = (1f - (1f/zooom))/2f;
			this.mapTexture.getTextureTranslation().set(tr, tr);
			System.out.println("-zz" + mapTexture.getTextureZoom());
		}
		return false;
	}
}
