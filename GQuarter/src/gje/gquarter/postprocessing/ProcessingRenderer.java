package gje.gquarter.postprocessing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import gje.gquarter.core.Loader;
import gje.gquarter.models.RawModel;

public class ProcessingRenderer {
	private static final float[] VERTICES = { -1F, 1F, -1F, -1F, 1F, 1F, 1F, -1F };

	private static RawModel screenModel;
	private static ProcessingShader shader;
	private static ProcessingFBO fbo;

	public static void init() {
		screenModel = Loader.loadToVAO(VERTICES, 2);
		shader = new ProcessingShader();
		fbo = new ProcessingFBO();
	}

	public static void rendererRelease() {
		shader.start();
		GL30.glBindVertexArray(screenModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbo.getOutputTexture());
		
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbo.getOutputDepthTexture());
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, screenModel.getVertexCount());

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public static ProcessingFBO getFbo() {
		return fbo;
	}

	public static void clean() {
		shader.cleanUp();
	}
}
