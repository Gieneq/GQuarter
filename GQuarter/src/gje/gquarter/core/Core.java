package gje.gquarter.core;

import java.util.Random;

import gje.gquarter.audio.AudioLibrary;
import gje.gquarter.audio.AudioMain;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.EntityX;
import gje.gquarter.entity.EntityXTestBuilder;
import gje.gquarter.entity.EnvironmentRenderer;
import gje.gquarter.entity.ModelBase;
import gje.gquarter.entity.PlayerEntity;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.event.Inputs;
import gje.gquarter.gui.event.UserController;
import gje.gquarter.terrain.Region;
import gje.gquarter.terrain.World;
import gje.gquarter.terrain.WorldBuilder;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.MousePicker;
import gje.gquarter.toolbox.Rotation3f;
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
		MainRenderer.init();// <3 !!
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
		rr.addLivingEntity(player);

		// czujnoik wilgoci do omijania jezior :D
		wt = new WaterTile(809f, -15f, 216f, 160f); // size 20 stad tiling 10
		rr.addWaterTile(wt);
		world.addRegion(rr);
		world.update(0f);

		guiFrame = new GuiFrame(this, world, ICONS_SIZE);
		ToolBox.log(this, Loader.getLoadingSummary());
		ToolBox.log(this, "Start in " + (System.nanoTime() - startupTime) / 1000000l + "ms");

		Random rnd = new Random();
		float count = 400;
		float camp = 10;
		float size = rr.getTarrain().getSize() / 10f;
		float campSize = 3;
		for (int i = 0; i < count; ++i) {

			float x = campSize + (size - 2 * campSize) * rnd.nextFloat();
			float z = campSize + (size - 2 * campSize) * rnd.nextFloat();

			for (int j = 0; j < camp; ++j) {
				float xx = x + campSize * (rnd.nextFloat() - 0.5f) * 2f;
				float zz = z + campSize * (rnd.nextFloat() - 0.5f) * 2f;
				float yy = rr.getTarrain().getHeightOfTerrainGlobal(xx, zz);

				if (rnd.nextBoolean()) {
					EntityX grass = EntityXTestBuilder.buildStraws(world, new Vector3f(xx, yy, zz), 1.2f + rnd.nextFloat() * 0.8f, rnd.nextFloat() * Maths.PI2);
					grass.getModelComponentIfHaving().setRendererType(EnvironmentRenderer.RENDERER_TYPE);
					rr.addEnvironmentEntity(grass);
					grass.updateEntity(0f);
					grass.setActive(false);
				} else {

					EntityX reeds = EntityXTestBuilder.buildReeds(world, new Vector3f(xx, yy, zz), 1.2f + rnd.nextFloat() * 0.8f, rnd.nextFloat() * Maths.PI2);
					reeds.getModelComponentIfHaving().setRendererType(EnvironmentRenderer.RENDERER_TYPE);
					rr.addEnvironmentEntity(reeds);
					reeds.updateEntity(0f);
					reeds.setActive(false);
				}
			}
		}
	}

	public void loop() {

		while (!Display.isCloseRequested() && isRunning()) {
			DisplayManager.updateDeltaTimerStartAndInit();

			DisplayManager.startStoper();
			update();
			DisplayManager.setUpdateDurationUs((int) (DisplayManager.stopStoperAndGetTime() * 1000000f));

			DisplayManager.startStoper();
			render();
			DisplayManager.setRenderDurationUs((int) (DisplayManager.stopStoperAndGetTime() * 1000000f));

			DisplayManager.startStoper();
			DisplayManager.updateDisplay();

			// koniec klatki
			DisplayManager.updateDeltaTimerStopAndSave();
		}
	}

	private void update() {
		float dt = DisplayManager.getDtSec();
		UserController.updateAll(dt);
		world.update(dt);
		camera.update(dt);
		guiFrame.update();

		MainRenderer.update();
		AudioMain.update();
		AudioMain.setListner(world.getPlayer());
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
