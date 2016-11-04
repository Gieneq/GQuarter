package gje.gquarter.models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class GraphVertex {

	private static final int NO_INDEX = -1;
	public static final int NOT_VISITED = -1;
	public static final int HALF_VISITED = 0;
	public static final int VISITED = 1;

	private Vector3f position;
	private int textureIndex;
	private int normalIndex;
	private GraphVertex duplicateVertex;
	private List<GraphVertex> neighbours;
	private int index;
	private int color;

	public GraphVertex(int index, Vector3f position) {
		this.color = NOT_VISITED;
		this.textureIndex = NO_INDEX;
		this.normalIndex = NO_INDEX;
		this.duplicateVertex = null;
		this.neighbours = new ArrayList<GraphVertex>();
		this.index = index;
		this.position = position;
	}

	public static void makeGraphEdge(GraphVertex verA, GraphVertex verB) {
		verA.addNeighbouringVertex(verB);
		verB.addNeighbouringVertex(verA);
	}

	public static void collapseGraphEdge(GraphVertex verA, GraphVertex verB) {
		verA.removeNeighbouringVertex(verB);
		verB.removeNeighbouringVertex(verA);
	}

	private void addNeighbouringVertex(GraphVertex neighbouringVertex) {
		if (!neighbours.contains(neighbouringVertex) && neighbouringVertex != this)
			neighbours.add(neighbouringVertex);
	}

	private void removeNeighbouringVertex(GraphVertex ver) {
		if (neighbours.contains(ver))
			neighbours.remove(ver);
	}

	public List<GraphVertex> getNeighbours() {
		return neighbours;
	}

	public int getIndex() {
		return index;
	}

	public float getLength() {
		return position.length();
	}

	public boolean isSet() {
		return textureIndex != NO_INDEX && normalIndex != NO_INDEX;
	}

	public boolean hasSameTextureAndNormal(int textureIndexOther, int normalIndexOther) {
		return textureIndexOther == textureIndex && normalIndexOther == normalIndex;
	}

	public void setTextureIndex(int textureIndex) {
		this.textureIndex = textureIndex;
	}

	public void setNormalIndex(int normalIndex) {
		this.normalIndex = normalIndex;
	}

	public Vector3f getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}

	public GraphVertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(GraphVertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

}
