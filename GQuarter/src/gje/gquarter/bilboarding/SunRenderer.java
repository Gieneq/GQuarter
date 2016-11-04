package gje.gquarter.bilboarding;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;
import gje.gquarter.toolbox.Maths;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class SunRenderer {
	private static final float[] VERTICES = { -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, -0.5F };
	private static final String SUN_TEXT_FILEPATH = "weather/slonko";
	private static final float SUN_SCALE = 12F;
	private static SunShader shader;
	private static RawModel quadModel;
	private static int textureId;
	private static Matrix4f tempMatrix;
	private static float scale;

	public static void init(Matrix4f projectionMatrix) {
		shader = new SunShader();
		quadModel = Loader.loadToVAO(VERTICES, 2);
		textureId = Loader.loadTextureFiltered(SUN_TEXT_FILEPATH, true).id;
		tempMatrix = new Matrix4f();
		scale = SUN_SCALE;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();

	}

	public static void loadProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public static void rendererRelease() {
		shader.start();
		GL30.glBindVertexArray(quadModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		Maths.prepareBilboardedMatrix(MainRenderer.getWeather().getRealSunPosition(), scale, 0f, MainRenderer.getSelectedCamera(), tempMatrix);
		shader.loadMVMatrix(tempMatrix);
		shader.loadColor(MainRenderer.getWeather().getRealSunColor());

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quadModel.getVertexCount());

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public static void clean() {
		shader.cleanUp();
	}
}
