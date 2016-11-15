package gje.gquarter.gui.panels;

import gje.gquarter.core.Core;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.entity.EnvironmentRenderer;
import gje.gquarter.events.OnCameraUpdateListener;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.On3DTerrainPick;
import gje.gquarter.gui.fonts.GUIText;
import gje.gquarter.gui.fonts.GuiTextMainRenderer;
import gje.gquarter.terrain.Region;
import gje.gquarter.terrain.TerrainRenderer;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.MousePicker;
import gje.gquarter.toolbox.Rect2i;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class LogPanel extends GuiPanel implements OnCameraUpdateListener, On3DTerrainPick {
	private static final Vector3f TEXT_BASIC = Maths.convertColor3f(0xCCD1ED);
	private static final Vector3f TEXT_UPDATES = Maths.convertColor3f(0x9CF9BC);
	private static final Vector3f TEXT_WARNING = Maths.convertColor3f(0xFF6357);
	private GUIText resolutionText;
	private GUIText fpsTextLog;

	private GUIText updateTime;
	private GUIText updateCameraTime;
	private GUIText updateWorldTime;
	private GUIText updateFrameTime;

	private GUIText renderTime;
	private GUIText renderRefletionTime;
	private GUIText renderRefractionTime;
	private GUIText renderSceneTime;
	private GUIText renderPostprocTime;
	private GUIText renderOthersTime;

	private GUIText dispUpdateTime;

	private GUIText multisamples;
	private GUIText openglMaxVersion;
	private GUIText allocatedMemory;
	private GUIText coresProcessor;
	private GUIText terrain;
	private GUIText entities;
	private GUIText environment;
	private GUIText playerPosition;
	private GUIText cameraPosition;
	private GUIText last3DPick;
	private GUIText cameraDist;
	private GUIText click3DPos;
	private Vector3f tempPos;

	private float updateLogCounter;

	public LogPanel(String idName, int panelX, int panelY, int panelW, int panelH, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, panelW, panelH, null), true, true, frame, GuiPanel.TYPE_RETANGULAR);
		MousePicker.add3DTerrainPicker(this);
		updateLogCounter = DisplayManager.getLogPeriod();
		tempPos = new Vector3f();

		int pointer = 0;

		float fntSize = GuiTextMainRenderer.PANEL_TEXT_SIZE;
		resolutionText = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		resolutionText.setColour(TEXT_BASIC);
		addTextField(resolutionText);
		resolutionText.setText("Resolution: " + Core.WIDTH + " x " + Core.HEIGHT);

		multisamples = new GUIText("Multisamples: " + Core.MULTISAMPLES_COUNT, fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		multisamples.setColour(TEXT_BASIC);
		addTextField(multisamples);

		coresProcessor = new GUIText("Cores: " + DisplayManager.getCoresCount(), fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		coresProcessor.setColour(TEXT_BASIC);
		addTextField(coresProcessor);

		openglMaxVersion = new GUIText("GL_Version: " + DisplayManager.getOpenGLVersion(), fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		openglMaxVersion.setColour(TEXT_BASIC);
		addTextField(openglMaxVersion);

		allocatedMemory = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		allocatedMemory.setColour(TEXT_BASIC);
		addTextField(allocatedMemory);

		fpsTextLog = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		fpsTextLog.setColour(TEXT_BASIC);
		addTextField(fpsTextLog);

		updateTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		updateTime.setColour(TEXT_UPDATES);
		addTextField(updateTime);

		updateCameraTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		updateCameraTime.setColour(TEXT_UPDATES);
		addTextField(updateCameraTime);

		updateWorldTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		updateWorldTime.setColour(TEXT_UPDATES);
		addTextField(updateWorldTime);

		updateFrameTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		updateFrameTime.setColour(TEXT_UPDATES);
		addTextField(updateFrameTime);

		renderTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		renderTime.setColour(TEXT_UPDATES);
		addTextField(renderTime);

		renderRefletionTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		renderRefletionTime.setColour(TEXT_UPDATES);
		addTextField(renderRefletionTime);

		renderRefractionTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		renderRefractionTime.setColour(TEXT_UPDATES);
		addTextField(renderRefractionTime);

		renderSceneTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		renderSceneTime.setColour(TEXT_UPDATES);
		addTextField(renderSceneTime);

		renderPostprocTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		renderPostprocTime.setColour(TEXT_UPDATES);
		addTextField(renderPostprocTime);

		renderOthersTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		renderOthersTime.setColour(TEXT_UPDATES);
		addTextField(renderOthersTime);

		dispUpdateTime = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		dispUpdateTime.setColour(TEXT_UPDATES);
		addTextField(dispUpdateTime);

		terrain = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		terrain.setColour(TEXT_BASIC);
		addTextField(terrain);

		entities = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		entities.setColour(TEXT_BASIC);
		addTextField(entities);

		environment = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		environment.setColour(TEXT_BASIC);
		addTextField(environment);

		playerPosition = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		playerPosition.setColour(TEXT_BASIC);
		addTextField(playerPosition);

		cameraPosition = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		cameraPosition.setColour(TEXT_BASIC);
		addTextField(cameraPosition);

		last3DPick = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		last3DPick.setColour(TEXT_BASIC);
		addTextField(last3DPick);

		cameraDist = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		cameraDist.setColour(TEXT_BASIC);
		addTextField(cameraDist);

		click3DPos = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + (pointer++) * 19, panelW - 4, this, false, this);
		click3DPos.setColour(TEXT_BASIC);
		addTextField(click3DPos);

		MainRenderer.getSelectedCamera().addUpdatedListener(this);
		getBackgroundQuad().useColour(Maths.convertColor4f(0, 4, 22, 180), 1f);

		updateGeneral();
		onCameraUpdate(0f);
		setVisible(visibility);
	}

	@Override
	public void updateGeneral() {
		updateLogCounter -= DisplayManager.getDtSec();
		if (updateLogCounter < 0) {
			// long debugingTimeNanos = System.nanoTime();
			updateLogCounter = DisplayManager.getLogPeriod();
			allocatedMemory.setText("Memory: " + DisplayManager.getAllocatedMemoryMB() + " / " + DisplayManager.getMaxMemoryMB() + " [MB]");
			fpsTextLog.setText("FPS: " + DisplayManager.getCurrentFPS() + " / " + DisplayManager.getFpsCap() + " [1/s]");
			if (DisplayManager.getCurrentFPS() < DisplayManager.getFpsCap())
				fpsTextLog.setColour(TEXT_WARNING);
			else
				fpsTextLog.setColour(TEXT_BASIC);
			updateTime.setText("Update: " + DisplayManager.durationUpdateAllUs / 1000f + " [ms]");
			updateCameraTime.setText(" -camera: " + DisplayManager.durationUpdateCameraUs / 1000f + " [ms]");
			updateWorldTime.setText(" -world: " + DisplayManager.durationUpdateWorldUs / 1000f + " [ms]");
			updateFrameTime.setText(" -frame: " + DisplayManager.durationUpdateFrameUs / 1000f + " [ms]");
			renderTime.setText("Render: " + DisplayManager.durationRenderAllUs / 1000f + " [ms]");
			renderRefletionTime.setText(" -refl: " + DisplayManager.durationRenderReflectionUs / 1000f + " [ms]");
			renderRefractionTime.setText(" -refr: " + DisplayManager.durationRenderRefractionUs / 1000f + " [ms]");
			renderSceneTime.setText(" -scene: " + DisplayManager.durationRenderSceneUs / 1000f + " [ms]");
			renderPostprocTime.setText(" -post: " + DisplayManager.durationRenderPostprocUs / 1000f + " [ms]");
			renderOthersTime.setText(" -others: " + DisplayManager.durationRenderOthersUs / 1000f + " [ms]");
			dispUpdateTime.setText("DisplayUp: " + DisplayManager.durationDisplayUpdateUs / 1000f + " [ms]");

		}
	}

	public void on3DLeftClick(Vector2f clickPlace) {
	}

	public void on3DLeftPress(Vector2f clickPlace) {
	}

	public void on3DLeftRelease(Vector2f clickPlace) {
		// marker.setVisibility(false);
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

	@Override
	public void onCameraUpdate(float dt) {
		terrain.setText("Terrain: (" + TerrainRenderer.getProcessedBlocksCount() + ")" + TerrainRenderer.getProcessedIndicesCount() + " / " + TerrainRenderer.getMaxIndicesCount());
		Region rr = MainRenderer.getSelectedCamera().getRegional().getRegion();
		if (rr != null) {
			entities.setText("Entities: " + EntityRenderer.getModelComponentCount() + "(" + rr.getEntities().size() + "), v: " + EntityRenderer.getProcessedIndicesCount());
			environment.setText("Environment: " + EnvironmentRenderer.getModelComponentCount() + "(" + rr.getEnvironment().size() + "), v: " + EnvironmentRenderer.getProcessedIndicesCount());
		}
		tempPos = parentFrame.getPlayer().getPhysicalComponentIfHaving().getPosition();
		playerPosition.setText("focus " + Maths.getIntPosition(tempPos));
		tempPos = MainRenderer.getSelectedCamera().getPosition();
		cameraPosition.setText("camera " + Maths.getIntPosition(tempPos));
	}

	@Override
	public boolean on3DClick(float x, float y, float z, int buttonId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean on3DPress(float x, float y, float z, int buttonId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean on3DHover(float x, float y, float z) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean on3DRelease(float x, float y, float z, int buttonId) {
		last3DPick.setText("3D release (" + buttonId + ") [" + (int) x + ", " + (int) y + ", " + (int) z + "]");
		return false;
	}
}
