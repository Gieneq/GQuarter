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
	private static float dtSec = 0.003f;
	private static float stoperLastTime = 0;
	private static int lastFPS = 2000;
	private static Random randomiser = new Random(lastTime);
	private static int updateDurationUs = 0;
	private static int renderDurationUs = 0;
	private static int fpsCap;
	private static int logFreq;
	private static float fpsPeriod;
	public static final int STARTING_FPS_CAP = 30;
	private static final float LOG_FREQ_DIVISOR = 0.25f;//0.25f

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

	public static int getLogFrequency() {
		return logFreq;
	}

	public static void setFpsCap(int fpsCap) {
		DisplayManager.fpsCap = fpsCap;
		fpsPeriod = 1f / fpsCap;
		logFreq = (int) (fpsCap * LOG_FREQ_DIVISOR);
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

	public static int getUpdateDurationUs() {
		return updateDurationUs;
	}

	public static void setUpdateDurationUs(int updateDurationUs) {
		DisplayManager.updateDurationUs = updateDurationUs;
	}

	public static int getRenderDurationUs() {
		return renderDurationUs;
	}

	public static void setRenderDurationUs(int renderDurationUs) {
		DisplayManager.renderDurationUs = renderDurationUs;
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
}
