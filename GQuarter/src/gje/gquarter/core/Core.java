package gje.gquarter.core;

import gje.gquarter.audio.AudioLibrary;
import gje.gquarter.audio.AudioMain;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.EntityX;
import gje.gquarter.entity.ModelBase;
import gje.gquarter.entity.PlayerEntity;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.event.Inputs;
import gje.gquarter.gui.event.UserController;
import gje.gquarter.terrain.Region;
import gje.gquarter.terrain.World;
import gje.gquarter.terrain.WorldBuilder;
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
		ModelBase.init();

		/*
		 * WORLD
		 */
		world = WorldBuilder.buildTestWorld();
		PlayerEntity player = PlayerEntity.loadPlayer(world);
		player.getModelComponentIfHaving().setRendererType(1);
		camera = Camera.loadCamera(player.getPhysicalCmp().getPosition(), world);
		MainRenderer.setSelectedCamera(camera);
		world.setPlayer(player);
		
		/*
		 * REGION
		 */

		Region rr = WorldBuilder.buildTestRegion(0, 0, world, false);

		EntityX justSource = new EntityX("SourceJust");
		PhysicalComponent phyB = new PhysicalComponent(new Vector3f(30, 12, 30), new Rotation3f(), 1f);
		RegionalComponent regB = new RegionalComponent(phyB.getPosition(), world);
		GravityComponent gravB = new GravityComponent(phyB, regB);
		SoundComponent sndB = new SoundComponent(phyB, AudioLibrary.getSoundBufferId(AudioLibrary.WIND_HOWL_FOREST));
		
		justSource.addComponent(phyB);
		justSource.addComponent(regB);
		justSource.addComponent(gravB);
		justSource.addComponent(sndB);
		rr.addEnvironmentEntity(justSource);

		rr.addLivingEntity(player);
		
		// czujnoik wilgoci do omijania jezior :D
		wt = new WaterTile(809f, -15f, 216f, 160f); // size 20 stad tiling 10
		rr.addWaterTile(wt);
		world.addRegion(rr);
		world.update(0f);

		guiFrame = new GuiFrame(this, world, ICONS_SIZE);
		ToolBox.log(this, Loader.getLoadingSummary());
		ToolBox.log(this, "Start in " + (System.nanoTime() - startupTime) / 1000000l + "ms");
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
