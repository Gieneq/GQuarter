package gje.gquarter.entity;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TexturedModel;
import gje.gquarter.toolbox.Maths;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class EnvironmentRenderer {
	public static final int RENDERER_TYPE = 1;
	public static final int INSTANCED_DATA_LENGTH = 4 * 4;
	private static EnvironmentShader shader;
	private static List<EnvironmentalKey> environmentalKeys;
	private static int indicesCount = 0;
	private static int modelComponentCount = 0;
	private static boolean visible;
	private static float normalisedTime = 0f;

	public static void init(Matrix4f projectionMatrix) {
		environmentalKeys = new ArrayList<EnvironmentalKey>();
		shader = new EnvironmentShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		visible = true;
	}

	/** Return if entities stored in list are rendered to the next stage. */
	public static boolean isVisible() {
		return visible;
	}

	/**
	 * Sets if entities stored in list are rendered to the next stage.
	 * 
	 * @param visible
	 *            - true if entities are going to be visible.
	 */
	public static void setVisible(boolean visible) {
		EnvironmentRenderer.visible = visible;
	}

	public static void loadProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public static void loadFogParams(float gradient, float density) {
		shader.start();
		 shader.loadFogParams(gradient, density);
		shader.stop();
	}

	public static void addEnvironmentalKey(EnvironmentalKey key) {
		environmentalKeys.add(key);
	}

	public static void clearBatchList() {
		for (EnvironmentalKey key : environmentalKeys)
			key.clearBatch();
	}

	public static int getProcessedIndicesCount() {
		return indicesCount;
	}

	public static void loadEntityXModelComponent(ModelComponent comp) {
		TexturedModel tModel = comp.getModel();
		boolean found = false;
		for (EnvironmentalKey key : environmentalKeys) {
			if (key.getKeyModel() == tModel) {
				found = true;
				key.addModelToBatch(comp);
			}
		}
		if (!found) {
			System.err.println("Model's key not added before! Add it into base next time!");
			System.exit(-1);
		}
	}

	public static void remove(ModelComponent comp) {
		TexturedModel tModel = comp.getModel();
		boolean found = false;
		for (EnvironmentalKey key : environmentalKeys) {
			if (key.getKeyModel() == tModel) {
				found = true;
				key.removeModelFromBatch(comp);
			}
		}
		if (!found) {
			System.err.println("Model's key not added before! Add it into base next time!");
			System.exit(-1);
		}
	}

	public static void refillBuffers() {
		for (EnvironmentalKey key : environmentalKeys) {
			if (key.needRefill())
				key.refillBuffer();
		}
	}
	
	public static void update(float dt){
		normalisedTime += (dt * 2f);
	}

	public static void renderRelease(Vector4f plane) {
		if (visible) {

			shader.start();
			shader.loadClipPlane(plane);
			shader.loadSkyColor(MainRenderer.getWeather().getFogColor());
			shader.loadLights(MainRenderer.getLightList());
			shader.loadAnimationValue(Maths.sin(normalisedTime) * Maths.PI);
			shader.loadViewMatrix(MainRenderer.getSelectedCamera());

			modelComponentCount = 0;
			indicesCount = 0;

			for (EnvironmentalKey key : environmentalKeys) {
				if (key.getObjectsCount() > 0) {
					TexturedModel tModel = key.getKeyModel();
					RawModel rawModel = tModel.getRawModel();
					shader.loadShineVariables(tModel.getTexture().getShineDamper(), tModel.getTexture().getReflectivity());

					if (tModel.getTexture().isHasTransparency())
						MainRenderer.disableCulling();

					GL30.glBindVertexArray(rawModel.getVaoID());
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					GL20.glEnableVertexAttribArray(2);
					GL20.glEnableVertexAttribArray(3);
					GL20.glEnableVertexAttribArray(4);
					GL20.glEnableVertexAttribArray(5);
					GL20.glEnableVertexAttribArray(6);

					shader.loadModelParams(key.getHardnes(), rawModel.getBoundingSphereRadius());

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, tModel.getTexture().getId());

					indicesCount += rawModel.getVertexCount();
					modelComponentCount += key.getObjectsCount();

					GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0, key.getObjectsCount());

					GL11.glDisable(GL11.GL_BLEND);
					MainRenderer.enableCulling();
					GL20.glDisableVertexAttribArray(0);
					GL20.glDisableVertexAttribArray(1);
					GL20.glDisableVertexAttribArray(2);
					GL20.glDisableVertexAttribArray(3);
					GL20.glDisableVertexAttribArray(4);
					GL20.glDisableVertexAttribArray(5);
					GL20.glDisableVertexAttribArray(6);
					GL30.glBindVertexArray(0);
				}
			}
			MainRenderer.enableCulling();
			shader.stop();
		}
	}

	public static void clean() {
		shader.cleanUp();
		clearBatchList();
	}

	public static int getModelComponentCount() {
		return modelComponentCount;
	}
}
