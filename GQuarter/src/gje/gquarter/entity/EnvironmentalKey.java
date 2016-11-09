package gje.gquarter.entity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.core.Loader;
import gje.gquarter.models.TexturedModel;

public class EnvironmentalKey {
	private TexturedModel model;
	private List<ModelComponent> modelsBatch;
	private final int maxCountInFrustum;
	private int pointer;
	private boolean needRefill;
	private final FloatBuffer floatBuffer;
	private int objectsCount;
	private int instanceVBO;

	public EnvironmentalKey(int maxCountInFrustum, TexturedModel model) {
		this.maxCountInFrustum = maxCountInFrustum;
		this.floatBuffer = BufferUtils.createFloatBuffer(maxCountInFrustum * EnvironmentRenderer.INSTANCED_DATA_LENGTH);
		this.instanceVBO = Loader.createEmptyFloatVbo(maxCountInFrustum * EnvironmentRenderer.INSTANCED_DATA_LENGTH, Loader.USAGE_STATIC_DRAW);
		this.model = model;

		Loader.addInstancedAttr(getModelVAO(), instanceVBO, 3, 4, EnvironmentRenderer.INSTANCED_DATA_LENGTH, 0);
		Loader.addInstancedAttr(getModelVAO(), instanceVBO, 4, 4, EnvironmentRenderer.INSTANCED_DATA_LENGTH, 4);
		Loader.addInstancedAttr(getModelVAO(), instanceVBO, 5, 4, EnvironmentRenderer.INSTANCED_DATA_LENGTH, 8);
		Loader.addInstancedAttr(getModelVAO(), instanceVBO, 6, 4, EnvironmentRenderer.INSTANCED_DATA_LENGTH, 12);

		this.pointer = 0;
		this.needRefill = true;
		this.objectsCount = 0;
		this.modelsBatch = new ArrayList<ModelComponent>();
	}

	// TODO w przypadku przenoszenia wybranego wntity potrzeba updatowacv mu
	// macierz, takze jakis subset by sie przydal czy cos...

	public void refillBuffer() {
		this.pointer = 0;

		this.objectsCount = modelsBatch.size();
		if (this.objectsCount > this.maxCountInFrustum)
			this.objectsCount = maxCountInFrustum;

		float data[] = new float[objectsCount * EnvironmentRenderer.INSTANCED_DATA_LENGTH];
		for (int i = 0; i < objectsCount; ++i) {
			Matrix4f matrix = modelsBatch.get(i).getMultiModelMatrix();
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
		Loader.updateFloatsVbo(this.instanceVBO, data, this.floatBuffer, Loader.USAGE_STATIC_DRAW);
		System.out.println("model: " + this.model.getRawModel().getVaoID() + " count: " + objectsCount);
		this.needRefill = false;
	}

	public void sort() {
		// sortowanie moze nie calre ale tez zgodne z quad tree? zeby uzyskac
		// szybciej dany wycinek i go lokalnie posortowac
		// to musialbym przechowywac obiekty w lisciach quadtree!!
	}

	public int getModelVAO() {
		return model.getRawModel().getVaoID();
	}

	public int getModelVBO() {
		return model.getRawModel().getVboID();
	}

	public int getInstancedVBO() {
		return instanceVBO;
	}

	public void clearBatch() {
		modelsBatch.clear();
	}

	public TexturedModel getKeyModel() {
		return model;
	}

	public int getObjectsCount() {
		return objectsCount;
	}

	public void removeModelFromBatch(ModelComponent mCmp) {
		modelsBatch.remove(mCmp);
		needRefill = true;
	}

	public void addModelToBatch(ModelComponent mCmp) {
		modelsBatch.add(mCmp);
		needRefill = true;
	}

	public boolean needRefill() {
		return needRefill;
	}
}
