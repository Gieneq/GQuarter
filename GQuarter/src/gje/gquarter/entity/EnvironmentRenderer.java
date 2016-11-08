package gje.gquarter.entity;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.ModelTexture;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TexturedModel;
import gje.gquarter.toolbox.Maths;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
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
	public static final int MAX_COUNT_STRAWS = 690 + 1;
	private static EnvironmentShader shader;
	private static Map<TexturedModel, List<ModelComponent>> modelComponentsHashmap;
	private static int indicesCount = 0;
	private static int modelComponentCount = 0;
	private static boolean visible;
	private static float normalisedTime = 0f;

	private static int strawModelVBO;
	private static int strawModelsCount;
	private static final FloatBuffer strawFloatBuffer = BufferUtils.createFloatBuffer(MAX_COUNT_STRAWS * INSTANCED_DATA_LENGTH);
	private static int pointer = 0;

	private static boolean needRefill = true;

	public static void init(Matrix4f projectionMatrix) {
		modelComponentsHashmap = new HashMap<TexturedModel, List<ModelComponent>>();
		shader = new EnvironmentShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		visible = true;

		// TODO przydadza sie obiekty trzymajace wszystkie dane i robiace
		// setup... i je fdac jako klucz
		strawModelVBO = Loader.createEmptyFloatVbo(MAX_COUNT_STRAWS * INSTANCED_DATA_LENGTH);
		int vaoID = ModelBase.getRefRawModelComp(ModelBase.STRAWS_ID).getModel().getRawModel().getVaoID();
		Loader.addInstancedAttr(vaoID, strawModelVBO, 3, 4, INSTANCED_DATA_LENGTH, 0);
		Loader.addInstancedAttr(vaoID, strawModelVBO, 4, 4, INSTANCED_DATA_LENGTH, 4);
		Loader.addInstancedAttr(vaoID, strawModelVBO, 5, 4, INSTANCED_DATA_LENGTH, 8);
		Loader.addInstancedAttr(vaoID, strawModelVBO, 6, 4, INSTANCED_DATA_LENGTH, 12);
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
		shader.start(); // TODO
		// shader.loadFogParams(gradient, density);
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
		needRefill = true;
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
		needRefill = true;
	}

	public static void refilStrawsBuffer() {
		pointer = 0;
		// TODO ZROBIC JAKIE OBIEKTY I PRZYPISYWAC ZE DANY RAW MODEL JAK TRAFI
		// DO WEKTORA ENV TO MA BYTC TAK TRAKTOWANY -,-

		// dodaje sobie do regionu elementy, potem jest culling - dodawane tu do
		// hashmapy...

		// potem musze aktualizowac, co kazda zmiane bufor i wgrywac go do
		// gpu...

		// zmiana oznacza zdjecie oraz dodanie :/
		TexturedModel strawModel = ModelBase.getRefRawModelComp(ModelBase.STRAWS_ID).getModel();
		List<ModelComponent> batch = modelComponentsHashmap.get(strawModel);
		if (batch != null) {
			strawModelsCount = batch.size();
			if(strawModelsCount > MAX_COUNT_STRAWS)
				strawModelsCount = MAX_COUNT_STRAWS;
			float[] data = new float[strawModelsCount * INSTANCED_DATA_LENGTH];
			for (int ii = 0; ii < strawModelsCount; ++ii) {
				Matrix4f matrix = batch.get(ii).getMultiModelMatrix();
				data[pointer++] = matrix.m00;
				data[pointer++] = matrix.m01;
				data[pointer++] = matrix.m02;
				data[pointer++] = matrix.m03;

				data[pointer++] = matrix.m10;
				data[pointer++] = matrix.m11;
				data[pointer++] = matrix.m12;
				data[pointer++] = matrix.m13;

				data[pointer++] = matrix.m20;
				data[pointer++] = matrix.m21;
				data[pointer++] = matrix.m22;
				data[pointer++] = matrix.m23;

				data[pointer++] = matrix.m30;
				data[pointer++] = matrix.m31;
				data[pointer++] = matrix.m32;
				data[pointer++] = matrix.m33;
			}
			Loader.updateFloatsVbo(strawModelVBO, data, strawFloatBuffer);
			System.out.println("Straws: " + strawModelsCount);
		}
	}

	public static void renderRelease(Vector4f plane) {
		if (visible) {
			if (needRefill) {
				needRefill = false;
				refilStrawsBuffer();
			}
			normalisedTime += (DisplayManager.getDtSec()*2f);
			shader.start();
			shader.loadClipPlane(plane);
			shader.loadAnimationValue(Maths.sin(normalisedTime) * Maths.PI);
			shader.loadViewMatrix(MainRenderer.getSelectedCamera());

			modelComponentCount = 0;
			indicesCount = 0;

			for (TexturedModel tModel : modelComponentsHashmap.keySet()) {
				RawModel rawModel = tModel.getRawModel();
				if (tModel.getTexture().isHasTransparency())
					MainRenderer.disableCulling();
				if (rawModel == ModelBase.getRefRawModelComp(ModelBase.STRAWS_ID).getModel().getRawModel()) {
					// System.out.println(strawModelsCount);
					// refilStrawsBuffer();
					GL30.glBindVertexArray(rawModel.getVaoID());
					GL20.glEnableVertexAttribArray(0);
					GL20.glEnableVertexAttribArray(1);
					GL20.glEnableVertexAttribArray(2);
					GL20.glEnableVertexAttribArray(3);
					GL20.glEnableVertexAttribArray(4);
					GL20.glEnableVertexAttribArray(5);
					GL20.glEnableVertexAttribArray(6);
					shader.loadModelParams(4f, rawModel.getBoundingSphereRadius());

					GL11.glEnable(GL11.GL_DEPTH_TEST);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, tModel.getTexture().getId());

					int iCount = rawModel.getVertexCount();
					indicesCount += iCount;
					modelComponentCount += strawModelsCount;
					GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, iCount, GL11.GL_UNSIGNED_INT, 0, strawModelsCount);

					GL11.glDisable(GL11.GL_DEPTH_TEST);
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
				MainRenderer.enableCulling();
			}
			shader.stop();
		} else {
			modelComponentCount = 0;
			indicesCount = 0;
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
