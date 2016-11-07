package gje.gquarter.gui.panels;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import gje.gquarter.components.RegionalComponent;
import gje.gquarter.core.Loader;
import gje.gquarter.gui.GuiButton;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.GuiTexture;
import gje.gquarter.map.MapRenderer;
import gje.gquarter.toolbox.Rect2i;

public class MapPanel extends GuiPanel {

	private GuiTexture mapTexture;
	private GuiTexture rose;
	private GuiTexture mapPoint;
	private GuiButton toggleModeButton;
	private GuiButton resizeButton;
	private int pointerSize;

	public MapPanel(String idName, int panelW, int panelH, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i((Display.getWidth() - panelW) / 2, (Display.getHeight() - panelH) / 2, panelW, panelH, null), visibility, true, frame, GuiPanel.TYPE_RETANGULAR);

		pointerSize = w - 2 * GuiFrame.OFFSET;
		if (pointerSize > h - 2 * GuiFrame.OFFSET)
			pointerSize = h - 2 * GuiFrame.OFFSET;

		pointerSize = (int) (pointerSize * 0.022f);

		Rect2i rect = new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET, w - 2 * GuiFrame.OFFSET, h - 2 * GuiFrame.OFFSET, this);
		mapTexture = new GuiTexture(rect, this);
		mapTexture.useTexture(MapRenderer.getMapTexture());
		addNoninteractiveGui(mapTexture);

		Rect2i rectPoint = new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET, pointerSize, pointerSize, this);
		mapPoint = new GuiTexture(rectPoint, this);
		mapPoint.useTexture(Loader.loadTextureFiltered("gui/icons/player_pointer", Loader.MIPMAP_MEDIUM).id);
		mapPoint.useRadius(0.98f, 1f);
		addNoninteractiveGui(mapPoint);

		Rect2i modeRect = new Rect2i(w - parentFrame.getIconSize() - GuiFrame.OFFSET, GuiFrame.OFFSET, parentFrame.getIconSize(), parentFrame.getIconSize(), this);
		toggleModeButton = new GuiButton("mode", "gui/icons/mapicon", modeRect, this);
		toggleModeButton.setRoundedBasic();
		addGuiButton(toggleModeButton);
		onRelease(toggleModeButton.getIdName());

		Rect2i resizeRect = new Rect2i(w - parentFrame.getIconSize() - GuiFrame.OFFSET, GuiFrame.OFFSET + GuiFrame.SPACING + parentFrame.getIconSize(), parentFrame.getIconSize(), parentFrame.getIconSize(), this);
		resizeButton = new GuiButton("resize", "gui/icons/contractIcon", resizeRect, this);
		resizeButton.setRoundedBasic();
		addGuiButton(resizeButton);
		
		int roseSize = (int) (w * 0.2f);
		Rect2i rectRose = new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET, roseSize, roseSize, this);
		rose = new GuiTexture(rectRose, this);
		rose.useTexture(Loader.loadTextureFiltered("gui/icons/crossrose", Loader.MIPMAP_MEDIUM).id);
		addNoninteractiveGui(rose);

		setVisible(visibility);
	}

	@Override
	public void updateGeneral() {
		RegionalComponent reg = parentFrame.getPlayer().getRegionalComponentIfHaving();
		float hmSize = reg.getRegion().getTarrain().getVertexCount();
		
		if(mapTexture.isLeftClicked() && isVisible()){
			float dx = (Mouse.getX() - mapTexture.getGlobalX()) * hmSize / mapTexture.w;
			float dz = (Display.getHeight()-Mouse.getY() - mapTexture.getGlobalY()) * hmSize / mapTexture.h;
			float placingHeight = reg.getRegion().getTarrain().getHeightOfTerrainGlobal(dx, dz);
			parentFrame.getPlayer().getPhysicalComponentIfHaving().getPosition().set(dx, placingHeight, dz);
		}
		
		Vector2f relative = reg.getRelativePosition();
		int newX = (int) ((relative.x / hmSize) * (w - 2 * GuiFrame.OFFSET) + GuiFrame.OFFSET - pointerSize / 2f);
		int newY = (int) ((relative.y / hmSize) * (h - 2 * GuiFrame.OFFSET) + GuiFrame.OFFSET + GuiFrame.HEADER_HEIGHT - pointerSize / 2f);
		mapPoint.x = newX;
		mapPoint.y = newY;
		mapPoint.forceUpdatePositionSize();
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
		if (idName == toggleModeButton.getIdName()) {
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
		if (idName == resizeButton.getIdName()) {
			MiniMapPanel minimap = parentFrame.getMinimapPanel();
			if (!minimap.isVisible())
				minimap.setVisible(true);
			if (this.isVisible())
				this.setVisible(false);
			return true;
		}
		return false;
	}
}
