package gje.gquarter.gui;

import gje.gquarter.components.RegionalComponent;
import gje.gquarter.core.Core;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.EntityX;
import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;
import gje.gquarter.gui.panels.EditorPanel;
import gje.gquarter.gui.panels.LogPanel;
import gje.gquarter.gui.panels.MainMenuPanel;
import gje.gquarter.gui.panels.MapPanel;
import gje.gquarter.gui.panels.MiniMapPanel;
import gje.gquarter.gui.panels.SettingsPanel;
import gje.gquarter.gui.panels.WeatherEditPanel;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.BlendmapPainter;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.MousePicker;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiFrame implements OnKeyEventListener {
	public static final int HEADER_HEIGHT = 22;
	public static final float POP_SCALE = 0.01f;
	public static final int MOUSE_LMB = 0;
	public static final int MOUSE_RMB = 1;
	public static final int MOUSE_MPM = 2;
	public static final int OFFSET = 8;
	public static final int SPACING = 8;

	private ArrayList<GuiPanel> panels;
	private GuiPanel draggedPanel;
	private EditorPanel editor;
	private MainMenuPanel mainMenu;
	private WeatherEditPanel weatherPanel;
	private LogPanel logPanel;
	private MapPanel mapPanel;
	private MiniMapPanel minimapPanel;
	private SettingsPanel settingsPanel;

	private World world;
	private int draggingPanelX, draggingPanelY;
	private Key escapeKey, waterKeyF1;
	private Key mapKey;
	private Key printScreen;
	private Key hideGUI;

	private EntityX player;
	private int iconSize;

	public GuiFrame(Core core, World world, int iconSize) {
		this.iconSize = iconSize;
		this.panels = new ArrayList<GuiPanel>();
		this.player = world.getPlayer();
		this.world = world;

		MousePicker.init(MainRenderer.getProjectionMatrix());

		RegionalComponent cmp = player.getRegionalComponentIfHaving();

		mainMenu = new MainMenuPanel("mainmenu", (int) (Display.getWidth() / 2f - 30), (int) (Display.getHeight() / 2f - 55), 150, 150, core, false, this);
		panels.add(mainMenu);

		editor = new EditorPanel(cmp, "Editor panel", 10, 10, 400, 330, false, this);
		panels.add(editor);

		weatherPanel = new WeatherEditPanel("Weather panel", 340, 10, 404, 120, true, this);
		panels.add(weatherPanel);

		settingsPanel = new SettingsPanel("Settings", 800, 300, 100 + 2 * GuiFrame.OFFSET + 2 * (GuiFrame.SPACING + iconSize) + iconSize, 200, false, this);
		panels.add(settingsPanel);

		logPanel = new LogPanel("Log panel", Display.getWidth() - 340, 0, 340, 550, true, this);
		panels.add(logPanel);

		mapPanel = new MapPanel("Map", 600, 600, false, this);
		panels.add(mapPanel);
		mapKey = new Key(Keyboard.KEY_M);
		mapKey.setOnClickListener(this);

		int mmRad = 100;
		float mmTrans = (float) (mmRad * (1f - 1f / Maths.SQRT2));
		mmTrans *= 1.05f;
		int mmX = (int) (Display.getWidth() - 2f * mmRad + mmTrans);
		int mmY = (int) (0f - mmTrans);
		minimapPanel = new MiniMapPanel("Minimap", mmX, mmY, mmRad, true, this);
		panels.add(minimapPanel);

		escapeKey = new Key(Keyboard.KEY_ESCAPE);
		escapeKey.setOnClickListener(this);
		hideGUI = new Key(Keyboard.KEY_F1);
		hideGUI.setOnClickListener(this);
		printScreen = new Key(Keyboard.KEY_P);
		printScreen.setOnClickListener(this);
	}

	/*
	 * W interfacie uzywam tylko LPM!
	 */

	public void update() {
		// przesuwanie paneli
		boolean done = false;

		if (!Mouse.isButtonDown(MOUSE_LMB)) {
			draggedPanel = null;
		}
		if (draggedPanel != null) {
			draggedPanel.movePanel(Mouse.getX() - draggedPanel.x - draggingPanelX, Display.getHeight() - Mouse.getY() - draggedPanel.y - draggingPanelY);
			draggedPanel.forceUpdate();
			done = true;
		}

		if (!done) {
			for (GuiPanel panel : panels) {
				if (panel.isVisible()) {
					if (draggedPanel == null) {
						if (panel.isHeaderLeftClicked()) {
							draggedPanel = panel;
							draggingPanelX = Mouse.getX() - panel.x;
							draggingPanelY = Display.getHeight() - Mouse.getY() - panel.y;
							GUIMainRenderer.moveOntop(draggedPanel);
							done = true;
							break;
						}

						if (panel.buttonsProceedWithResult()) {
							done = true;
							break;
						}
					}
				}
			}
		}

		/*
		 * Zdarzenie nie zwiazane z klikaniem :)
		 */

		for (GuiPanel panel : panels) {
			if (panel.isVisible()) {
				panel.updateGeneral();
				
				if (panel.getParent() != null)
					panel.forceUpdate();
			}
		}
		if (!done)
			MousePicker.updateGeneral();
	}

	public ArrayList<GuiPanel> getPanels() {
		return panels;
	}

	public EditorPanel getEditor() {
		return editor;
	}

	public LogPanel getLogPanel() {
		return logPanel;
	}

	public SettingsPanel getSettingsPanel() {
		return settingsPanel;
	}

	public WeatherEditPanel getWeatherPanel() {
		return weatherPanel;
	}

	public int getIconSize() {
		return iconSize;
	}

	@Override
	public boolean onKeyClick(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		if (keyId == escapeKey.getKeyId()) {
			if (!mainMenu.isVisible())
				mainMenu.setVisible(true);
			else
				mainMenu.setVisible(false);
			return true;
		}
		if (keyId == mapKey.getKeyId()) {
			if (!minimapPanel.isVisible() && !mapPanel.isVisible())
				mapPanel.setVisible(true);
			else if (minimapPanel.isVisible() && !mapPanel.isVisible()) {
				minimapPanel.setVisible(false);
				mapPanel.setVisible(true);
			} else if (!minimapPanel.isVisible() && mapPanel.isVisible()) {
				minimapPanel.setVisible(true);
				mapPanel.setVisible(false);
			}

			return true;
		}
		if (keyId == printScreen.getKeyId())
			BlendmapPainter.screenShot();
		if (keyId == hideGUI.getKeyId()){
			GUIMainRenderer.setVisible(!GUIMainRenderer.isVisible());
		}
		return false;
	}

	public World getWorld() {
		return world;
	}

	public EntityX getPlayer() {
		return player;
	}

	public MapPanel getMapPanel() {
		return mapPanel;
	}

	public MiniMapPanel getMinimapPanel() {
		return minimapPanel;
	}

	public boolean isVisibleGUI() {
		return GUIMainRenderer.isVisible();
	}

	public void setVisibleGUI(boolean visibleGUI) {
		GUIMainRenderer.setVisible(visibleGUI);
	}
}
