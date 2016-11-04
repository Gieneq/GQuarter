package gje.gquarter.entity;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.ModelTexture;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TexturedModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class EntityRenderer {
	private static EntityShader shader;
	private static Map<TexturedModel, List<ModelComponent>> modelComponentsHashmap;
	private static int indicesCount = 0;
	private static int modelComponentCount = 0;
	private static boolean visible;
	private static float normalisedTime = 0f;

	public static void init(Matrix4f projectionMatrix) {
		modelComponentsHashmap = new HashMap<TexturedModel, List<ModelComponent>>();
		shader = new EntityShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.addShineBitmap(); // TODO co to ma byc!!? XD
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
		EntityRenderer.visible = visible;
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

	public static void clearBatchList() {
		modelComponentsHashmap.clear();
	}

	public static int getProcessedIndicesCount() {
		return indicesCount;
	}

	public static void loadEntityXModelComponent(ModelComponent comp) {
		TexturedModel tModel = comp.getModel();
		List<ModelComponent> batch = modelComponentsHashmap.get(tModel);
		if (batch != null) {
			batch.add(comp);
		} else {
			System.out.println("EntityRenderer: batchAdded");
			List<ModelComponent> newBatch = new ArrayList<ModelComponent>();
			newBatch.add(comp);
			modelComponentsHashmap.put(tModel, newBatch);
		}

	}

	public static void remove(ModelComponent comp) {
		TexturedModel tModel = comp.getModel();
		List<ModelComponent> batch = modelComponentsHashmap.get(tModel);
		if (batch != null) {
			batch.remove(comp);
			if (batch.isEmpty()) {
				System.out.println("EntityRenderer: batchRemoved");
				modelComponentsHashmap.remove(tModel);
			}
		}
	}

	public static void renderRelease(Vector4f plane) {
		if (visible) {
			normalisedTime += (DisplayManager.getDtSec()/8f);
			if(normalisedTime > 1f)
				normalisedTime %= 1f;
			shader.start();
			shader.loadClipPlane(plane);
			shader.loadTimeNormalised(normalisedTime);
			shader.loadSkyColor(MainRenderer.getWeather().getFogColor());
			shader.loadLights(MainRenderer.getLightList());
			shader.loadViewMatrix(MainRenderer.getSelectedCamera());
			modelComponentCount = 0;
			indicesCount = 0;
			for (TexturedModel tModel : modelComponentsHashmap.keySet()) {
				prepareTexturedModel(tModel); // jedno przygotowanie wiele kopi
				List<ModelComponent> batch = modelComponentsHashmap.get(tModel);
				for (ModelComponent m : batch) {
					prepareInstance(m);
					GL11.glDrawElements(GL11.GL_TRIANGLES, tModel.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					indicesCount += m.getModel().getRawModel().getVertexCount();
					++modelComponentCount;
				}
				unbindTextureModel();
			}
			shader.stop();
		} else {
			modelComponentCount = 0;
			indicesCount = 0;
		}
	}

	public static void prepareTexturedModel(TexturedModel model) {
		RawModel rawModel = model.getRawModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = model.getTexture();
		shader.loadNumberOfRows(texture.getNumberOfRows());

		if (texture.isHasTransparency()) {
			MainRenderer.disableCulling();
		}
		shader.loadfakeLighting(texture.isUseFakeLighting());
		shader.loadAdditionalShine(model.isUseGlowMap());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

		GL13.glActiveTexture(GL13.GL_TEXTURE0); // tu jest wyjscie samplera2D
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getId());

		if (model.isUseGlowMap()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getGlowMap().getId());
		}
	}

	private static void prepareInstance(ModelComponent comp) {
		shader.loadTransformationMatrix(comp.getMultiModelMatrix());
		shader.loadOffset(comp.getTextureAtlasOffset());
		shader.loadSelectedOption(comp.isSelected());
	}

	private static void unbindTextureModel() {
		MainRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	public static void clean() {
		shader.cleanUp();
		clearBatchList();
	}

	public static EntityShader getEntityShader() {
		return shader;
	}

	public static int getModelComponentCount() {
		return modelComponentCount;
	}
}
