package gje.gquarter.core;

import gje.gquarter.audio.AudioMain;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.Dest;
import gje.gquarter.entity.ObstRock;
import gje.gquarter.entity.Pendulum;
import gje.gquarter.entity.PendulumStick;
import gje.gquarter.entity.PlayerEntity;
import gje.gquarter.entity.Robot;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.event.Inputs;
import gje.gquarter.gui.event.UserController;
import gje.gquarter.terrain.Region;
import gje.gquarter.terrain.World;
import gje.gquarter.terrain.WorldBuilder;
import gje.gquarter.toolbox.ToolBox;
import gje.gquarter.water.WaterTile;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

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
	private WaterTile wt;

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

		wt = new WaterTile(150f, -13.5f, 574f, 100f); 
		rr.addWaterTile(wt);
		
		Vector3f origin = new Vector3f(253f, -3f, 712f);
		
		Pendulum pendulum = new Pendulum(new Vector3f(origin), world);
		rr.addEntity(pendulum);
		PendulumStick stick = new PendulumStick(pendulum, world);
		rr.addEntity(stick);

		Vector3f destOrigin = new Vector3f(origin);
		destOrigin.translate(0f, -12, 12);
		Dest dest = new Dest(destOrigin, pendulum, world);
		rr.addEntity(dest);
		

		Vector3f rockOrigin = new Vector3f(origin);
		rockOrigin.z -= 11f;
		rockOrigin.x -= 1.1f;
		rockOrigin.y = -12f;
		ObstRock rock = new ObstRock(rockOrigin, world);
		rr.addEntity(rock);

		Vector3f robOrigin = new Vector3f(origin);
		robOrigin.z -= 16f;
		robOrigin.y = -12f;
		Robot robot = new Robot(robOrigin, pendulum, dest, rock, world);
		rr.addEntity(robot);


		guiFrame = new GuiFrame(this, world, ICONS_SIZE);
		ToolBox.log(this, Loader.getLoadingSummary());
		ToolBox.log(this, "Start in " + (System.nanoTime() - startupTime) / 1000000l + "ms");
		inputs.start();
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
		inputs.setRunning(false);
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
