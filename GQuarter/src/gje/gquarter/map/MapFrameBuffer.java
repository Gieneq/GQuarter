package gje.gquarter.map;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class MapFrameBuffer {

	protected static final int MAP_TEXTURE_WIDTH = 256;
	private static final int MAP_TEXTURE_HEIGHT = 256;

	private int terrMapFrameBuffer;
	private int terrMapTexture;

	// call when loading the game
	public MapFrameBuffer() {
		terrMapFrameBuffer = createFrameBuffer();
		terrMapTexture = createTextureAttachment(MAP_TEXTURE_WIDTH, MAP_TEXTURE_HEIGHT);
		unbindCurrentFrameBuffer();
	}

	public void cleanUp() {
		GL30.glDeleteFramebuffers(terrMapFrameBuffer);
		GL11.glDeleteTextures(terrMapTexture);
	}

	// call before rendering to this FBO
	public void bindTerrMapFrameBuffer() {
		// To make sure the texture isn't bound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, terrMapFrameBuffer);
		GL11.glViewport(0, 0, MAP_TEXTURE_WIDTH, MAP_TEXTURE_HEIGHT);
	}

	// call to switch to default frame buffer
	public void unbindCurrentFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	// get the resulting texture
	public int getMapTexture() {
		return terrMapTexture;
	}

	private int createFrameBuffer() {
		int frameBuffer = GL30.glGenFramebuffers();
		// generate name for frame buffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		// create the framebuffer
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
		// indicate that we will always render to color attachment 0
		return frameBuffer;
	}

	private int createTextureAttachment(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, texture, 0);
		return texture;
	}
}