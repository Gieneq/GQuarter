package gje.gquarter.models;

import org.lwjgl.util.vector.Vector3f;

public class RawModel {
	private int vaoID;
	private int vboID;
	private int vertexCount;
	private Vector3f massCenter;
	private float boundingSphereRadius;

	public RawModel(int vaoID, int vertexCount, int indicesVboID, Vector3f massCenter, float boundingSphereRadius) {
		this.vaoID = vaoID;
		this.vertexCount = vertexCount;
		this.vboID = indicesVboID;
		this.massCenter = massCenter;
		this.boundingSphereRadius = boundingSphereRadius;
	}

	public int getVaoID() {
		return vaoID;
	}

	public int getVboID() {
		return vboID;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	public Vector3f getMassCenter() {
		return massCenter;
	}

	public float getBoundingSphereRadius() {
		return boundingSphereRadius;
	}
}
