package gje.gquarter.models;

import org.lwjgl.util.vector.Vector3f;

public class ModelData {

	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private float furthestPoint;
	private Vector3f massCenter;

	public ModelData(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Vector3f massCenter, float furthestPoint) {
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
		this.massCenter = massCenter;
		this.furthestPoint = furthestPoint;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}

	public float getBoundingRadius() {
		return furthestPoint;
	}

	public Vector3f getMassCenter() {
		return massCenter;
	}
}
