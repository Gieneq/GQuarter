package gje.gquarter.gui.panels;

import gje.gquarter.core.Core;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.GuiSlider;
import gje.gquarter.gui.GuiTexture;
import gje.gquarter.gui.SliderFunction;
import gje.gquarter.gui.fonts.GUIText;
import gje.gquarter.gui.fonts.GuiTextMainRenderer;
import gje.gquarter.toolbox.Rect2i;

import org.lwjgl.util.vector.Vector4f;

public class WeatherEditPanel extends GuiPanel {
	private GuiSlider daylightBar;
	private GuiTexture skyColorSample;
	private GUIText hourText;
	private float updateCounter;

	public WeatherEditPanel(String idName, int panelX, int panelY, int panelW, int panelH, boolean visible, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, panelW, panelH, null), visible, true, frame, GuiPanel.TYPE_RETANGULAR);
		int iconSize = parentFrame.getIconSize();

		SliderFunction function = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return value / (24f * Core.HOUR_IN_SEC);
			}

			@Override
			public float getValueFromSlider(float norm) {
				return 24f * Core.HOUR_IN_SEC * norm;
			}
		};

		this.daylightBar = new GuiSlider("Time", new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET, w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, function);
		addGuiProgressBar(daylightBar);
		// daylightBar.setValue(23f * Core.HOUR_IN_SEC);

		skyColorSample = new GuiTexture(new Rect2i(GuiFrame.OFFSET + 0 * iconSize, GuiFrame.OFFSET + 18 + GuiFrame.SPACING, iconSize, iconSize, this), this);
		skyColorSample.useColour(new Vector4f(1f, 1f, 1f, 1f), 1f);
		addNoninteractiveGui(skyColorSample);

		int textX = GuiFrame.OFFSET + 1 * iconSize + GuiFrame.SPACING;
		int maxWidth = this.w - textX - 2 * GuiFrame.OFFSET;
		hourText = new GUIText("xx:xx", GuiTextMainRenderer.PANEL_TEXT_SIZE, GuiTextMainRenderer.getBasicFont(), textX, GuiFrame.OFFSET + 18 + GuiFrame.SPACING, maxWidth, this, false, this);
		addTextField(hourText);

		updateCounter = DisplayManager.getLogPeriod()*8f;
		setVisible(visible);
		updateGeneral();
	}

	@Override
	public void updateGeneral() {
		if (isVisible()) {
			updateCounter -= DisplayManager.getDtSec();
			if (updateCounter < 0) {
				updateCounter = DisplayManager.getLogPeriod();
				daylightBar.setValue(MainRenderer.getWeather().getTime());
				hourText.setText(MainRenderer.getWeather().getHour() + " : " + MainRenderer.getWeather().getMinute());
				updateSkyColorSample();
			}
		}
	}

	public void updateSkyColorSample() {
		skyColorSample.getColour().x = MainRenderer.getWeather().getFogColor().x;
		skyColorSample.getColour().y = MainRenderer.getWeather().getFogColor().y;
		skyColorSample.getColour().z = MainRenderer.getWeather().getFogColor().z;
	}

	@Override
	public boolean onClick(String idName) {
		return false;
	}

	@Override
	public boolean onPress(String idName) {
		if (idName == daylightBar.getIdName()) {
			updateSkyColorSample();
			MainRenderer.getWeather().setTime(daylightBar.getValue());
			return true;
		}

		return false;
	}

	@Override
	public boolean onRelease(String idName) {
		return false;
	}

	public GuiSlider getDaylightBar() {
		return daylightBar;
	}
}
