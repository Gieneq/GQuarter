package gje.gquarter.gui.panels;

import gje.gquarter.core.Core;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.events.OnCameraUpdateListener;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.GuiPanel;
import gje.gquarter.gui.fonts.GUIText;
import gje.gquarter.gui.fonts.GuiTextMainRenderer;
import gje.gquarter.terrain.TerrainRenderer;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rect2i;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class LogPanel extends GuiPanel implements OnCameraUpdateListener {

	private GUIText resolutionText;
	private GUIText fpsTextLog;
	private GUIText updateUs;
	private GUIText renderUs;
	private GUIText multisamples;
	private GUIText openglMaxVersion;
	private GUIText allocatedMemory;
	private GUIText coresProcessor;
	private GUIText verticesProcessedTerrain;
	private GUIText verticesProcessedEntities;
	private GUIText modelCompcount;
	private GUIText playerPosition;
	private GUIText cameraPosition;
	private GUIText cameraNormal;
	private GUIText cameraDist;
	private GUIText click3DPos;
	private Vector3f tempPos;

	private int updateLogCounter;

	public LogPanel(String idName, int panelX, int panelY, int panelW, int panelH, boolean visibility, GuiFrame frame) {
		super(idName, new Rect2i(panelX, panelY, panelW, panelH, null), true, true, frame, GuiPanel.TYPE_RETANGULAR);
		updateLogCounter = DisplayManager.getLogFrequency();
		tempPos = new Vector3f();

		float fntSize = GuiTextMainRenderer.PANEL_TEXT_SIZE;
		resolutionText = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 0 * 19, panelW - 4, this, false, this);
		resolutionText.setColour(1f, 1f, 1f);
		addTextField(resolutionText);
		resolutionText.setText("Resolution: " + Core.WIDTH + " x " + Core.HEIGHT);

		multisamples = new GUIText("Multisamples: " + Core.MULTISAMPLES_COUNT, fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 1 * 19, panelW - 4, this, false, this);
		multisamples.setColour(1f, 1f, 1f);
		addTextField(multisamples);

		coresProcessor = new GUIText("Cores: " + DisplayManager.getCoresCount(), fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 2 * 19, panelW - 4, this, false, this);
		coresProcessor.setColour(1f, 1f, 1f);
		addTextField(coresProcessor);

		openglMaxVersion = new GUIText("GL_Version: " + DisplayManager.getOpenGLVersion(), fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 3 * 19, panelW - 4, this, false, this);
		openglMaxVersion.setColour(1f, 1f, 1f);
		addTextField(openglMaxVersion);

		allocatedMemory = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 4 * 19, panelW - 4, this, false, this);
		allocatedMemory.setColour(1f, 1f, 1f);
		addTextField(allocatedMemory);

		fpsTextLog = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 5 * 19, panelW - 4, this, false, this);
		fpsTextLog.setColour(1f, 1f, 1f);
		addTextField(fpsTextLog);

		updateUs = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 6 * 19, panelW - 4, this, false, this);
		updateUs.setColour(1f, 1f, 1f);
		addTextField(updateUs);

		renderUs = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 7 * 19, panelW - 4, this, false, this);
		renderUs.setColour(1f, 1f, 1f);
		addTextField(renderUs);

		verticesProcessedTerrain = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 8 * 19, panelW - 4, this, false, this);
		verticesProcessedTerrain.setColour(Maths.convertColor3f(0x76AEFF));
		addTextField(verticesProcessedTerrain);

		verticesProcessedEntities = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 9 * 19, panelW - 4, this, false, this);
		verticesProcessedEntities.setColour(Maths.convertColor3f(0x76AEFF));
		addTextField(verticesProcessedEntities);

		modelCompcount = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 10 * 19, panelW - 4, this, false, this);
		modelCompcount.setColour(Maths.convertColor3f(0x76AEFF));
		addTextField(modelCompcount);

		playerPosition = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 11 * 19, panelW - 4, this, false, this);
		playerPosition.setColour(Maths.convertColor3f(200, 33, 11));
		addTextField(playerPosition);

		cameraPosition = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 12 * 19, panelW - 4, this, false, this);
		cameraPosition.setColour(Maths.convertColor3f(200, 33, 11));
		addTextField(cameraPosition);

		cameraNormal = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 13 * 19, panelW - 4, this, false, this);
		cameraNormal.setColour(Maths.convertColor3f(200, 33, 11));
		addTextField(cameraNormal);

		cameraDist = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 14 * 19, panelW - 4, this, false, this);
		cameraDist.setColour(Maths.convertColor3f(200, 33, 11));
		addTextField(cameraDist);

		click3DPos = new GUIText("...", fntSize, GuiTextMainRenderer.getBasicFont(), 2, 2 + 15 * 19, panelW - 4, this, false, this);
		click3DPos.setColour(Maths.convertColor3f(200, 33, 11));
		addTextField(click3DPos);

		MainRenderer.getSelectedCamera().addUpdatedListener(this);
		
		updateGeneral();
		onCameraUpdate(0f);
		setVisible(visibility);
	}

	@Override
	public void updateGeneral() {
		--updateLogCounter;
		if (updateLogCounter < 0) {
//			long debugingTimeNanos = System.nanoTime();
			updateLogCounter = DisplayManager.getLogFrequency();
			allocatedMemory.setText("Memory: " + DisplayManager.getAllocatedMemoryMB() + " / " + DisplayManager.getMaxMemoryMB() + " [MB]");
			fpsTextLog.setText("FPS: " + DisplayManager.getCurrentFPS() + " / " + DisplayManager.getFpsCap() + " [1/s]");
			updateUs.setText("Update: " + DisplayManager.getUpdateDurationUs() + " [us]");
			renderUs.setText("Render: " + DisplayManager.getRenderDurationUs() + " [us]");

			verticesProcessedEntities.setText("Entities vertices: " + EntityRenderer.getProcessedIndicesCount());
			modelCompcount.setText("Models: " + EntityRenderer.getModelComponentCount() + "(" + parentFrame.getPlayer().getRegionalComponentIfHaving().getRegion().getEntitiesCount() + ")");

//			tempPos = parentFrame.getLast3Dclick();
//			click3DPos.setText("3D click " + Maths.getFloatPosition(tempPos, 100));
//			System.out.println("BAD! "+ getIdName() + ": " + (int) ((debugingTimeNanos = System.nanoTime() - debugingTimeNanos) / 1000l) + "us");
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
		verticesProcessedTerrain.setText("Terrain vertices: (" + TerrainRenderer.getProcessedBlocksCount() + ")" + TerrainRenderer.getProcessedIndicesCount() + " / " + TerrainRenderer.getMaxIndicesCount());

		tempPos = parentFrame.getPlayer().getPhysicalComponentIfHaving().getPosition();
		playerPosition.setText("focus " + Maths.getIntPosition(tempPos));
		tempPos = MainRenderer.getSelectedCamera().getPosition();
		cameraPosition.setText("camera " + Maths.getIntPosition(tempPos));

		tempPos = MainRenderer.getSelectedCamera().getFrontNormal();
		cameraNormal.setText("normal " + Maths.getFloatPosition(tempPos, 2));
		cameraDist.setText("dist: " + MainRenderer.getSelectedCamera().getDistToObservedPoint());

	}
}
