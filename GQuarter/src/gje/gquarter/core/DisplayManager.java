package gje.gquarter.core;

import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayManager {
	private static long startingTimeNanos;
	private static long lastTime = System.nanoTime();
	private static long lastProbe1 = System.nanoTime();
	private static long lastProbe2 = System.nanoTime();
	private static float dtSec = 0.003f;
	private static float stoperLastTime = 0;
	private static int lastFPS = 2000;
	private static Random randomiser = new Random(lastTime);

	public static int durationUpdateAllUs = 0;
	public static int durationUpdateCameraUs = 0;
	public static int durationUpdateWorldUs = 0;
	public static int durationUpdateFrameUs = 0;

	public static int durationRenderAllUs = 0;
	public static int durationRenderRefractionUs = 0;
	public static int durationRenderReflectionUs = 0;
	public static int durationRenderSceneUs = 0;
	public static int durationRenderPostprocUs = 0;
	public static int durationRenderOthersUs = 0;

	public static int durationDisplayUpdateUs = 0;

	private static int fpsCap;
	private static float logPeriod;
	private static float fpsPeriod;
	public static final int STARTING_FPS_CAP = 30;
	private static final float LOG_PERIOD_MUL = 32f;

	public static void createDisplay(int width, int height, String title, int multisamples) {
		setFpsCap(STARTING_FPS_CAP);
		ContextAttribs attribs = new ContextAttribs(3, 3);
		attribs.withForwardCompatible(true).withProfileCore(true);

		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create(new PixelFormat().withSamples(multisamples).withDepthBits(24), attribs);
			Display.setTitle(title);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		GL11.glViewport(0, 0, width, height);
		System.out.println("OpenGL version: " + getOpenGLVersion());
		startingTimeNanos = System.nanoTime();
	}

	public static int getFpsCap() {
		return fpsCap;
	}

	public static float getPeriodFps() {
		return fpsPeriod;
	}

	public static float getLogPeriod() {
		return logPeriod;
	}

	public static void setFpsCap(int fpsCap) {
		DisplayManager.fpsCap = fpsCap;
		fpsPeriod = 1f / fpsCap;
		logPeriod = (fpsPeriod * LOG_PERIOD_MUL);
	}

	public static int getAllocatedMemoryMB() {
		return (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024f * 1024f));
	}

	public static int getTotalUsedMemoryMB() {
		return (int) ((Runtime.getRuntime().totalMemory()) / (1024f * 1024f));
	}

	public static int getMaxMemoryMB() {
		return (int) ((Runtime.getRuntime().maxMemory()) / (1024f * 1024f));
	}

	public static int getCoresCount() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static String getOpenGLVersion() {
		return GL11.glGetString(GL11.GL_VERSION);
	}

	public static void updateDisplay() {
		Display.sync(fpsCap);
		Display.update();
	}

	public static void closeDisplay() {
		clearTitle();
		Display.destroy();
	}

	public static void clearTitle() {
		Display.setTitle("");
	}

	public static void updateDeltaTimerStopAndSave() {
		dtSec = (System.nanoTime() - lastTime) / 1000000000f;
		lastFPS = (int) (1f / dtSec);
	}

	public static void startStoper() {
		stoperLastTime = getTimeSeconds();
	}

	public static float stopStoperAndGetTime() {
		return getTimeSeconds() - stoperLastTime;
	}

	public static void updateDeltaTimerStartAndInit() {
		lastTime = System.nanoTime();
	}

	public static int getCurrentFPS() {
		return lastFPS;
	}

	public static float getDtSec() {
		if (Keyboard.isKeyDown(Keyboard.KEY_F))
			return dtSec * 10;
		return dtSec;
	}

	public static float getTimeSeconds() {
		return (System.nanoTime() - startingTimeNanos) / 1000000000f;
	}

	public static float getTimeNormals() {
		return (getTimeSeconds() % 1f);
	}

	public static Random getRandomiser() {
		return randomiser;
	}

	public static void setTitle(String title) {
		Display.setTitle(title);
	}

	public static int probe1UsTime() {
		long time = System.nanoTime();
		int out = (int) ((time - lastProbe1) / 1000l);
		lastProbe1 = time;
		return out;
	}

	public static int probe2UsTime() {
		long time = System.nanoTime();
		int out = (int) ((time - lastProbe2) / 1000l);
		lastProbe2 = time;
		return out;
	}

	public static void sysoutDurations() {
		System.out.println("CORE - FPS: " + DisplayManager.getCurrentFPS());
		System.out.println("Update: " + DisplayManager.durationUpdateAllUs);
		System.out.println(" -cam: " + DisplayManager.durationUpdateCameraUs);
		System.out.println(" -frame: " + DisplayManager.durationUpdateFrameUs);
		System.out.println(" -world: " + DisplayManager.durationUpdateWorldUs);
		System.out.println("Render: " + DisplayManager.durationRenderAllUs);
		System.out.println(" -refl: " + DisplayManager.durationRenderReflectionUs);
		System.out.println(" -refr: " + DisplayManager.durationRenderRefractionUs);
		System.out.println(" -scene: " + DisplayManager.durationRenderSceneUs);
		System.out.println(" -post: " + DisplayManager.durationRenderPostprocUs);
		System.out.println(" -others: " + DisplayManager.durationRenderOthersUs);
		System.out.println("Disp: " + DisplayManager.durationDisplayUpdateUs);
		System.out.println();
	}
}
