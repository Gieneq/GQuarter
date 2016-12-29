package gje.gquarter.postprocessing;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class ProcessingFBO {
	public static final int DEPTH_NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_BUFFER = 2;
	private int depthType;
	private int width;
	private int height;

	private int outputFrameBuffer;

	private int outputColorTexture;
	private int outputDepthTexture;
	private int outputDepthBuffer;

	public ProcessingFBO(int width, int height, int depthType) {
		this.depthType = depthType;
		this.width = width;
		this.height = height;
		outputFrameBuffer = createFrameBuffer();
		outputColorTexture = createTextureAttachment(width, height);
		if ((depthType & DEPTH_TEXTURE) != 0)
			outputDepthTexture = createDepthTextureAttachment(width, height);
		if ((depthType & DEPTH_BUFFER) != 0)
			outputDepthBuffer = createDepthBufferAttachment(width, height);
		unbindCurrentFrameBuffer();
	}

	public void bindFrameBuffer() {
		bindFrameBuffer(outputFrameBuffer, width, height);
	}

	public void unbindCurrentFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public int getOutputColorTexture() {
		return outputColorTexture;
	}

	public int getOutputDepthTexture() {
		return outputDepthTexture;
	}
	
	public int getOutputDepthBuffer() {
		return outputDepthBuffer;
	}
	
	public int getDepthType() {
		return depthType;
	}

	public void setDepthType(int depthType) {
		this.depthType = depthType;
	}

	public void cleanUp() {
		GL30.glDeleteFramebuffers(outputFrameBuffer);
		GL11.glDeleteTextures(outputColorTexture);
		GL11.glDeleteTextures(outputDepthTexture);
		GL30.glDeleteRenderbuffers(outputDepthBuffer);
	}
	
	/*
	 * PRIVATE
	 */

	private void bindFrameBuffer(int frameBuffer, int width, int height) {
		// To make sure the texture isn't bound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	private int createFrameBuffer() {
		int frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
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

	private int createDepthTextureAttachment(int width, int height) {
		int texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT32, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, texture, 0);
		return texture;
	}

	private int createDepthBufferAttachment(int width, int height) {
		int depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
		return depthBuffer;
	}
}