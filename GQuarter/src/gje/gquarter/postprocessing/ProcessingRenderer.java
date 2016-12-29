package gje.gquarter.postprocessing;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import gje.gquarter.core.Loader;
import gje.gquarter.models.RawModel;

public class ProcessingRenderer {
	private static final float[] VERTICES = { -1F, 1F, -1F, -1F, 1F, 1F, 1F, -1F };

	private static RawModel screenModel;
	private static BrightShader brightShader;
	private static HorizontalBlurShader hBlurShader;
	private static VerticalBlurShader vBlurShader;
	private static ProcessingShader processingShader;

	private static ProcessingFBO referenceFBO;
	private static ProcessingFBO brightFBO;
	private static ProcessingFBO horBlurFBO;
	private static ProcessingFBO vertBlurFBO;

	public static void init() {
		screenModel = Loader.loadToVAO(VERTICES, 2);
		
		brightShader = new BrightShader(0.82f);
		hBlurShader = new HorizontalBlurShader(Display.getWidth()/2);
		vBlurShader = new VerticalBlurShader(Display.getHeight()/2);
		processingShader = new ProcessingShader();
		
		referenceFBO = new ProcessingFBO(Display.getWidth(), Display.getHeight(), ProcessingFBO.DEPTH_TEXTURE);
		brightFBO = new ProcessingFBO(Display.getWidth()/2, Display.getHeight()/2, ProcessingFBO.DEPTH_NONE);
		horBlurFBO = new ProcessingFBO(Display.getWidth()/2, Display.getHeight()/2, ProcessingFBO.DEPTH_NONE);
		vertBlurFBO = new ProcessingFBO(Display.getWidth()/2, Display.getHeight()/2, ProcessingFBO.DEPTH_NONE);
	}

	private static void doBrightPostprocessing() {
		brightFBO.bindFrameBuffer();
		brightShader.start();

		prepareRendering();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, referenceFBO.getOutputColorTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		brightShader.stop();
		brightFBO.unbindCurrentFrameBuffer();
	}


	private static void doBlurPostprocessing() {
		/*
		 * HORIZONTAL
		 */
		horBlurFBO.bindFrameBuffer();
		hBlurShader.start();

		prepareRendering();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, brightFBO.getOutputColorTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		hBlurShader.stop();
		horBlurFBO.unbindCurrentFrameBuffer();

		/*
		 * VERTICAL
		 */
		vertBlurFBO.bindFrameBuffer();
		vBlurShader.start();

		prepareRendering();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, horBlurFBO.getOutputColorTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		vBlurShader.stop();
		vertBlurFBO.unbindCurrentFrameBuffer();
		
		//////////////
		
		/*
		 * HORIZONTAL
		 */
		horBlurFBO.bindFrameBuffer();
		hBlurShader.start();

		prepareRendering();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, vertBlurFBO.getOutputColorTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		hBlurShader.stop();
		horBlurFBO.unbindCurrentFrameBuffer();

		/*
		 * VERTICAL
		 */
		vertBlurFBO.bindFrameBuffer();
		vBlurShader.start();

		prepareRendering();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, horBlurFBO.getOutputColorTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		vBlurShader.stop();
		vertBlurFBO.unbindCurrentFrameBuffer();
	}
	
	private static void doFinalPostprocessing() {
		processingShader.start();

		prepareRendering();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, referenceFBO.getOutputColorTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, vertBlurFBO.getOutputColorTexture());
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		processingShader.stop();
	}
	
	private static void prepareRendering() {
		GL11.glClearColor(0f, 0f, 0f, 0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	public static void rendererRelease() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL30.glBindVertexArray(screenModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		
		doBrightPostprocessing();
		doBlurPostprocessing();
		doFinalPostprocessing();
		
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static ProcessingFBO getReferenceFbo() {
		return referenceFBO;
	}

	public static void clean() {
		processingShader.cleanUp();
	}
}
