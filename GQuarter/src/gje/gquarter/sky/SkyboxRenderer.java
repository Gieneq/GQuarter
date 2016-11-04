package gje.gquarter.sky;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;
import gje.gquarter.toolbox.Maths;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class SkyboxRenderer {
	private static final float UNIT = 1;// 700
	public static final float RADIUS = 100;
	public static final float SCALE = 0.98f * RADIUS / Maths.SQRT3;// 700
	private static final float FOG_LIMIT =  0.2f;

	public static final float[] VERTICES = { -UNIT, UNIT, -UNIT, -UNIT, -UNIT, -UNIT, UNIT, -UNIT, -UNIT, UNIT, -UNIT, -UNIT, UNIT, UNIT, -UNIT, -UNIT, UNIT, -UNIT,

	-UNIT, -UNIT, UNIT, -UNIT, -UNIT, -UNIT, -UNIT, UNIT, -UNIT, -UNIT, UNIT, -UNIT, -UNIT, UNIT, UNIT, -UNIT, -UNIT, UNIT,

	UNIT, -UNIT, -UNIT, UNIT, -UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, -UNIT, UNIT, -UNIT, -UNIT,

	-UNIT, -UNIT, UNIT, -UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, -UNIT, UNIT, -UNIT, -UNIT, UNIT,

	-UNIT, UNIT, -UNIT, UNIT, UNIT, -UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, UNIT, -UNIT, UNIT, UNIT, -UNIT, UNIT, -UNIT,

	-UNIT, -UNIT, -UNIT, -UNIT, -UNIT, UNIT, UNIT, -UNIT, -UNIT, UNIT, -UNIT, -UNIT, -UNIT, -UNIT, UNIT, UNIT, -UNIT, UNIT };

	private static RawModel cubeModel;
	private static SkyboxShader skyboxShader;
	private static float boxScale;

	public static void init() {
		cubeModel = Loader.loadToVAO(VERTICES, 3);

		skyboxShader = new SkyboxShader();
		skyboxShader.start();
		skyboxShader.connectTextureUnits();
		skyboxShader.loadProjectionMatrix(MainRenderer.getProjectionMatrix());
		skyboxShader.loadBoxScale(SCALE);
		skyboxShader.loadFogLimit(FOG_LIMIT);
		skyboxShader.stop();
	}

	public static void loadProjectionMatrix(Matrix4f projectionMatrix) {
		skyboxShader.start();
		skyboxShader.loadProjectionMatrix(projectionMatrix);
		skyboxShader.stop();
	}

	public static void loadBoxScale(float scale) {
		skyboxShader.start();
		skyboxShader.loadBoxScale(scale);
		skyboxShader.stop();
		boxScale = scale;
	}

	public static void rendererRelease() {
		skyboxShader.start();
		skyboxShader.loadViewMatrix(MainRenderer.getSelectedCamera());
		skyboxShader.loadFogColor(MainRenderer.getWeather().getFogColor());

		GL30.glBindVertexArray(cubeModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		Weather weather = MainRenderer.getWeather();
		bind(weather.getFogColor(), weather.getSkyColor());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, cubeModel.getVertexCount());
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		skyboxShader.stop();
	}

	private static void bind(Vector3f fogColor, Vector3f skyColorr) {
		skyboxShader.loadFogColor(fogColor);
		skyboxShader.loadSkyColor(skyColorr);
	}

	public static void clean() {
		skyboxShader.cleanUp();
	}

	public static float getBoxScale() {
		return boxScale;
	}
}
