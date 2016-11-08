package gje.gquarter.entity;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

import gje.gquarter.components.ModelComponent;

public class EnvironmentalKey {
	private ModelComponent modelComponent;
	private final int maxCountInFrustum;
	private int pointer;
	private boolean needRefill;
	private final FloatBuffer floatBuffer;
	private int objectsCount;

	public EnvironmentalKey(int maxCountInFrustum) {
		this.maxCountInFrustum = maxCountInFrustum;
		this.floatBuffer = BufferUtils.createFloatBuffer(maxCountInFrustum * EnvironmentRenderer.INSTANCED_DATA_LENGTH);
	}

	public int getVAO(){
		return modelComponent.getModel().getRawModel().getVaoID();
	}

	public int getVBO(){
		return modelComponent.getModel().getRawModel().getVboID();
	}
	
}
