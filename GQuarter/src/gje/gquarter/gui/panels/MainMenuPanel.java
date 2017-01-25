package gje.gquarter.gui.panels;

import gje.gquarter.core.Core;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.terrain.WorldBuilder;
import gje.gquarter.toolbox.BlendmapPainter;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;

public class MainMenuPanel extends RadialGuiPanel {
	private Core core;

	// Honza

	public MainMenuPanel(String idName, int panelX, int panelY, int panelW, int panelH, Core core, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, panelW, panelH, null), visibility, Maths.PI2 * 0.0f, Maths.PI2 * 0.85f, frame);

		this.core = core;
		int iconSize = parentFrame.getIconSize();

		buttonCircularBuilder("closeThis", "gui/icons/closeIcon", iconSize, minAngle);
		buttonCircularBuilder("locker", "gui/icons/lockIcon", iconSize, minAngle);
		getButtonById("locker").setToggleMode(true);
		buttonCircularBuilder("settings", "gui/icons/settingsIcon", iconSize, minAngle);
		buttonCircularBuilder("screenshot", "gui/icons/screenCapturer", iconSize, minAngle);
		buttonCircularBuilder("editor", "gui/icons/editorIcon", iconSize, minAngle);
		buttonCircularBuilder("weatherPanel", "gui/icons/weatherIcon", iconSize, minAngle);
		buttonCircularBuilder("log", "gui/icons/logIcon", iconSize, minAngle);
		buttonCircularBuilder("saver", "gui/icons/saveIcon", iconSize, minAngle);
		buttonCircularBuilder("turnoff", "gui/icons/turnoffIcon", iconSize, minAngle);

		setButtonsCount();
		super.setVisible(visibility);
	}

	@Override
	public boolean onClick(String idName) {
		if (idName == "turnoff") {
			core.setRunning(false);
			return true;
		}
		return false;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
//		if (!visible)
//			parentFrame.getSettingsPanel().setVisible(false);
	}

	@Override
	public boolean onPress(String idName) {
		return false;
	}

	@Override
	public boolean onRelease(String idName) {
		if (idName == "closeThis") {
			setVisible(false);
		}
		if (idName == "screenshot") {
			BlendmapPainter.screenShot();
		}
		if (idName == "saver") {
			long startTime = System.nanoTime();
			parentFrame.getEditor().saveBM();
			long endTimeBM = (System.nanoTime() - startTime) / 1000000l;
			WorldBuilder.saveTestRegionsEnvironment(MainRenderer.getSelectedCamera().getRegional().getRegion());
			long endTimeTotal = (System.nanoTime() - startTime) / 1000000l;
			System.out.println("Saving times [ms]: BM: " + endTimeBM + ", REGION: " + (endTimeTotal - endTimeBM) + ", TOTAL: " + endTimeTotal);
		}
		if (idName == "settings") {
			if (parentFrame.getSettingsPanel().isVisible())
				parentFrame.getSettingsPanel().setVisible(false);
			else
				parentFrame.getSettingsPanel().setVisible(true);
		}
		if (idName == "editor") {
			if (parentFrame.getEditor().isVisible())
				parentFrame.getEditor().setVisible(false);
			else
				parentFrame.getEditor().setVisible(true);
		}
		if (idName == "weatherPanel") {
			if (parentFrame.getWeatherPanel().isVisible())
				parentFrame.getWeatherPanel().setVisible(false);
			else
				parentFrame.getWeatherPanel().setVisible(true);
		}
		if (idName == "log") {
			if (parentFrame.getLogPanel().isVisible())
				parentFrame.getLogPanel().setVisible(false);
			else
				parentFrame.getLogPanel().setVisible(true);
		}
		return false;
	}
}