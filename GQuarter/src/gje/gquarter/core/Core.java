package gje.gquarter.core;

import gje.gquarter.audio.AudioMain;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.PlayerEntity;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.event.Inputs;
import gje.gquarter.gui.event.UserController;
import gje.gquarter.terrain.Region;
import gje.gquarter.terrain.World;
import gje.gquarter.terrain.WorldBuilder;
import gje.gquarter.toolbox.ToolBox;
import gje.gquarter.water.WaterTile;

import org.lwjgl.opengl.Display;

public class Core extends Thread {
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int ICONS_SIZE = 40;
	public static final int MULTISAMPLES_COUNT = 1;
	public static final float ASPECT_RATIO = 1f * WIDTH / HEIGHT;
	public static final float HOUR_IN_SEC = 3600;

	private Camera camera;
	private World world;
	private GuiFrame guiFrame;
	private static Inputs inputs;
	private boolean running;

	WaterTile wt;

	@Override
	public void run() {
		init();
		ToolBox.log(this, "starting!");
		loop();
		ToolBox.log(this, "ending!");
		cleanup();
	}

	public void init() {
		long startupTime = System.nanoTime();
		DisplayManager.createDisplay(WIDTH, HEIGHT, "GQuarter - v3", MULTISAMPLES_COUNT);
		MainRenderer.init();
		AudioMain.init();
		inputs = new Inputs();
		running = true;

		/*
		 * WORLD
		 */
		world = WorldBuilder.buildTestWorld();
		PlayerEntity player = PlayerEntity.loadPlayer(world);
		camera = Camera.loadCamera(player.getPhysicalCmp().getPosition(), world);
		MainRenderer.setSelectedCamera(camera);
		world.setPlayer(player);

		/*
		 * REGION
		 */

		Region rr = WorldBuilder.buildTestRegion(0, 0, world, false);
		rr.addEntity(player);

		wt = new WaterTile(272f, -18f, 721f, 74f); 
		rr.addWaterTile(wt);

		guiFrame = new GuiFrame(this, world, ICONS_SIZE);
		ToolBox.log(this, Loader.getLoadingSummary());
		ToolBox.log(this, "Start in " + (System.nanoTime() - startupTime) / 1000000l + "ms");
	}

	public void loop() {

		while (!Display.isCloseRequested() && isRunning()) {
			DisplayManager.updateDeltaTimerStartAndInit();

			DisplayManager.probe2UsTime();
			update();
			DisplayManager.durationUpdateAllUs = DisplayManager.probe2UsTime();

			render();
			DisplayManager.durationRenderAllUs = DisplayManager.probe2UsTime();

			DisplayManager.updateDisplay();
			DisplayManager.durationDisplayUpdateUs = DisplayManager.probe2UsTime();

			// koniec klatki
			DisplayManager.updateDeltaTimerStopAndSave();
		}
	}

	private void update() {
		float dt = DisplayManager.getDtSec();
		UserController.updateAll(dt);
		MainRenderer.update(dt);
		AudioMain.update();
		AudioMain.setListner(MainRenderer.getSelectedCamera().getPosition());

		DisplayManager.probe1UsTime();

		world.update(dt);
		DisplayManager.durationUpdateWorldUs = DisplayManager.probe1UsTime();

		camera.update(dt);
		DisplayManager.durationUpdateCameraUs = DisplayManager.probe1UsTime();

		guiFrame.update();
		DisplayManager.durationUpdateFrameUs = DisplayManager.probe1UsTime();
	}

	private void render() {
		MainRenderer.render();
	}

	public void cleanup() {
		PlayerEntity.savePlayer(world.getPlayer());
		Camera.saveCamera(camera);
		MainRenderer.clean();
		AudioMain.clean();
		DisplayManager.closeDisplay();
	}

	/*
	 * AKCESORY
	 */

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public static Inputs getInputs() {
		return inputs;
	}
}
