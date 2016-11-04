package gje.gquarter.boundings;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class BoundingsRenderer {
	private static final String MODEL_SPHERE_PATH = "models/prymitives/bounding_sphere"; // bounding_sphere
	private static final String MODEL_CYLINDER_PATH = "models/prymitives/bounding_cyl";
	private static final String MODEL_CUBE_PATH = "models/prymitives/bounding_rect";

	private static BoundingsShader shader;
	private static RawModel[] models;
	private static boolean visible;

	private static Map<Integer, List<Bounding>> boundingsHashmap;

	/*
	 * dac jakas klase ktora jest podstawa wszystkich boundingow i zawiera
	 * wspolna czesc np getmodelmatrix z niej ekstenduja ksztalty boundingow
	 * dodawanie jest do hashmapy z indeksowaniem po typie renderujac jest
	 * rzutowanie ukryte bo mam metode getmodelmatrix itp
	 */

	private static boolean lastWFMode;
	private static int indicesCount = 0;
	private static int boundingsCount = 0;

	public static void init(Matrix4f projectionMatrix) {
		boundingsHashmap = new HashMap<Integer, List<Bounding>>();
		lastWFMode = false;
		models = new RawModel[3];
		models[Bounding.TYPE_SPHERE] = Loader.buildRawModel(MODEL_SPHERE_PATH);
		models[Bounding.TYPE_CYL] = Loader.buildRawModel(MODEL_CYLINDER_PATH);
		models[Bounding.TYPE_RECT] = Loader.buildRawModel(MODEL_CUBE_PATH);
		shader = new BoundingsShader();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public static void loadProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	public static void clearBatchList() {
		boundingsHashmap.clear();
	}

	public static int getProcessedIndicesCount() {
		return indicesCount;
	}

	public static void load(Bounding bnd) {
		Integer type = bnd.getType();
		List<Bounding> batch = boundingsHashmap.get(type);
		if (batch != null) {
			batch.add(bnd);
		} else {
			List<Bounding> newBatch = new ArrayList<Bounding>();
			newBatch.add(bnd);
			boundingsHashmap.put(type, newBatch);
		}

	}

	public static void remove(Bounding bnd) {
		Integer type = bnd.getType();
		List<Bounding> batch = boundingsHashmap.get(type);
		if (batch != null) {
			batch.remove(bnd);
			if (batch.isEmpty()) {
				boundingsHashmap.remove(type);
			}
		}
	}

	public static void renderRelease(Vector4f plane) {
		boundingsCount = 0;
		indicesCount = 0;
		if (visible) {
			shader.start();
			shader.loadClipPlane(plane);
			shader.loadViewMatrix(MainRenderer.getSelectedCamera());
			lastWFMode = MainRenderer.isWireframeModeOn();
			for (Integer type : boundingsHashmap.keySet()) {
				RawModel rawModel = models[type];
				GL30.glBindVertexArray(rawModel.getVaoID());
				GL20.glEnableVertexAttribArray(0);
				MainRenderer.disableCulling();
				MainRenderer.enableWireframeMode();
				List<Bounding> batch = boundingsHashmap.get(type);
				for (Bounding bnd : batch) {
					shader.loadSelectOption(bnd.isSelected());
					shader.loadTransformationMatrix(bnd.getModelMatrix());
					shader.loadColor(bnd.getColor());
					GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					indicesCount += rawModel.getVertexCount();
					++boundingsCount;
				}
				MainRenderer.enableCulling();
				MainRenderer.setWireframeMode(lastWFMode);
				GL20.glDisableVertexAttribArray(0);
				GL30.glBindVertexArray(0);
			}
			shader.stop();
		}
	}

	public static void clean() {
		shader.cleanUp();
		clearBatchList();
	}

	public static BoundingsShader getBoundingsShader() {
		return shader;
	}

	public static int getBoundingsCount() {
		return boundingsCount;
	}

	public static boolean isVisible() {
		return visible;
	}

	public static void setVisible(boolean visible) {
		BoundingsRenderer.visible = visible;
	}
}
