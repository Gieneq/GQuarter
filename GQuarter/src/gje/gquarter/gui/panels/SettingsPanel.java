package gje.gquarter.gui.panels;

import gje.gquarter.audio.AudioMain;
import gje.gquarter.bilboarding.SunRenderer;
import gje.gquarter.boundings.BoundingsRenderer;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.gui.GuiButton;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.GuiSlider;
import gje.gquarter.gui.SliderFunction;
import gje.gquarter.terrain.TerrainRenderer;
import gje.gquarter.toolbox.Rect2i;

import org.lwjgl.util.vector.Vector2f;

public class SettingsPanel extends GuiPanel {

	private GuiSlider fpsBar;
	private GuiSlider renderDistanceBar;

	public SettingsPanel(String idName, int panelX, int panelY, int panelW, int panelH, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, panelW, panelH, null), visibility, true, frame, GuiPanel.TYPE_RETANGULAR);

		int iconSize = parentFrame.getIconSize();

		GuiButton terrButton = new GuiButton("terrainToggler", "gui/icons/terrIcon", new Rect2i(GuiFrame.OFFSET + 0 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET, iconSize, iconSize, this), this);
		terrButton.setToggleMode(true);
		terrButton.setToggleState(true);
		addGuiButton(terrButton);

		GuiButton entitiesButton = new GuiButton("entitiesVis", "gui/icons/entitiesVis", new Rect2i(GuiFrame.OFFSET + 1 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET, iconSize, iconSize, this), this);
		entitiesButton.setToggleMode(true);
		entitiesButton.setToggleState(true);
		addGuiButton(entitiesButton);

		GuiButton lightButton = new GuiButton("lightTogller", "gui/icons/lightIcon", new Rect2i(GuiFrame.OFFSET + 2 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET, iconSize, iconSize, this), this);
		lightButton.setToggleMode(true);
		lightButton.setToggleState(false);
		lightButton.setActive(false);
		addGuiButton(lightButton);
//		onRelease(lightButton.getIdName());

		GuiButton soundsButton = new GuiButton("soundsToggler", "gui/icons/sounds", new Rect2i(GuiFrame.OFFSET + 3 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET, iconSize, iconSize, this), this);
		soundsButton.setToggleMode(true);
		soundsButton.setToggleState(AudioMain.isAudioEnabled());
		addGuiButton(soundsButton);

		GuiButton soundsBoundings = new GuiButton("soundsBoundings", "gui/icons/noteSphere", new Rect2i(GuiFrame.OFFSET + 4 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET, iconSize, iconSize, this), this);
		soundsBoundings.setToggleMode(true);
		soundsBoundings.setToggleState(false);
		soundsBoundings.setActive(false);
		addGuiButton(soundsBoundings);

		GuiButton boundingsButton = new GuiButton("boundingsTogller", "gui/icons/boundings", new Rect2i(GuiFrame.OFFSET + 0 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET + 1 * (GuiFrame.SPACING + iconSize), iconSize, iconSize, this), this);
		boundingsButton.setToggleMode(true);
		boundingsButton.setToggleState(false);
		addGuiButton(boundingsButton);

		GuiButton wfButton = new GuiButton("wfToggler", "gui/icons/wfIcon", new Rect2i(GuiFrame.OFFSET + 1 * (GuiFrame.SPACING + iconSize), GuiFrame.OFFSET + 1 * (GuiFrame.SPACING + iconSize), iconSize, iconSize, this), this);
		wfButton.setToggleMode(true);
		wfButton.setToggleState(false);
		addGuiButton(wfButton);

		SliderFunction function = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return (value - 5f) / 95f;
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * 95f + 5f;
			}
		};
		this.fpsBar = new GuiSlider("FPS CAP", new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET + 2 * (GuiFrame.SPACING + iconSize), w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, function);
		this.fpsBar.setValue(30);
		addGuiProgressBar(fpsBar);

		SliderFunction fRenderDist = new SliderFunction() {
			@Override
			public float setSliderPosition(float value) {
				return (value - 5f) / 685f;
			}

			@Override
			public float getValueFromSlider(float norm) {
				return norm * 685f + 5f;
			}
		};
		this.renderDistanceBar = new GuiSlider("Render distance", new Rect2i(GuiFrame.OFFSET, GuiFrame.OFFSET + 3 * (GuiFrame.SPACING + iconSize), w - 2 * GuiFrame.OFFSET, GuiSlider.HEIGHT, this), this, fRenderDist);
		this.renderDistanceBar.setValue(100);
		addGuiProgressBar(renderDistanceBar);

		setVisible(visibility);
	}

	public void on3DLeftClick(Vector2f clickPlace) {
		// marker.setVisibility(true);
	}

	public void on3DLeftPress(Vector2f clickPlace) {
	}

	public void on3DLeftRelease(Vector2f clickPlace) {
		// marker.setVisibility(false);
	}

	public void sampleTexel() {
		// sluzy to jak pipeta, pobiera texel i rozbija go na kolory pedzla
	}

	public void setBoundingVisibility(boolean visibility) {
		getButtonById("boundingsTogller").setToggleState(visibility);
		onRelease("boundingsTogller");
	}

	@Override
	public boolean onClick(String idName) {
		return false;
	}

	@Override
	public boolean onPress(String idName) {
		if (idName == renderDistanceBar.getIdName()) {
			float dist = renderDistanceBar.getValue();
			
			//ustawiam FAR plane
			parentFrame.getPlayer().getRegionalComponentIfHaving().getRegion().updateFrustumCulling();
			MainRenderer.loadFarPlaneFogSkybox(dist);
			return true;
		}
		return false;
	}

	@Override
	public boolean onRelease(String idName) {
		if (idName == "terrainToggler") {
			TerrainRenderer.setVisible(getButtonById("terrainToggler").isToggleState());
			return true;
		}
		if (idName == "entitiesVis") {
			EntityRenderer.setVisible(getButtonById("entitiesVis").isToggleState());
			return true;
		}
		if (idName == "wfToggler") {
			MainRenderer.setWireframeMode(getButtonById("wfToggler").isToggleState());
			return true;
		}
		if (idName == "boundingsTogller") {
			// TODO - SA ROZNE BOUNDOINGI.... PATRZEC OD JAKIEGO KOMPONENTU SA?
			BoundingsRenderer.setVisible(getButtonById("boundingsTogller").isToggleState());
			return true;
		}
		if (idName == "soundsToggler") {
			boolean playing = getButtonById("soundsToggler").isToggleState() == true;
			if (playing)
				AudioMain.audioEnable();
			else
				AudioMain.audioDisable();
			return true;
		}
		if (idName == fpsBar.getIdName()) {
			DisplayManager.setFpsCap((int) fpsBar.getValue());
			return true;
		}
		return false;
	}
}
