package gje.gquarter.models;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class GraphMesh {
	private List<GraphVertex> vertices;
	private List<GraphTriangle> triangles;
	private static Vector3f tempVector = new Vector3f();

	public GraphMesh() {
		vertices = new ArrayList<GraphVertex>();
		triangles = new ArrayList<GraphTriangle>();
	}

	public List<GraphVertex> getVertices() {
		return vertices;
	}

	public List<GraphTriangle> getTriangles() {
		return triangles;
	}

	public int countVertices() {
		int count = 0;
		for (GraphVertex vr : vertices) {
			if (vr != null)
				++count;
		}
		return count;
	}

	public int countTriangles() {
		int count = 0;
		for (GraphTriangle tr : triangles) {
			if (tr != null)
				++count;
		}
		return count;
	}

	public int[] getIndicesArray() {
		int[] indices = new int[this.countTriangles() * 3];
		int pointer = 0;
		for (GraphTriangle tr : triangles) {
			if (tr != null) {
				indices[pointer++] = tr.vertexA.getIndex();
				indices[pointer++] = tr.vertexB.getIndex();
				indices[pointer++] = tr.vertexC.getIndex();
			}
		}

		return indices;
	}
	/*
	 * --------------------------------------------------------------------------
	 */
	public void simplifyRev(float refValue, float factor) {
		float treshold = refValue * factor;
		float maxLenSquared = (treshold * treshold) * (treshold * treshold);
		
		for (GraphVertex vertex : vertices) {
			
		}
	}
	/*
	 * --------------------------------------------------------------------------
	 */

	public void simplify(float refValue, float factor) {
		float treshold = refValue * factor;
		float maxLenSquared = (treshold * treshold) * (treshold * treshold);
		for (GraphVertex vertex : vertices) {
			List<GraphVertex> neighbours = vertex.getNeighbours();
			
			int ieSize = neighbours.size();
			for (int ie = 0; ie <ieSize; ++ie) {
				GraphVertex edge = neighbours.get(ie);
				Vector3f.sub(edge.getPosition(), vertex.getPosition(), tempVector);
				float currentLenSquared = tempVector.lengthSquared();
				if (currentLenSquared < maxLenSquared) {
					// uproscic krawedz, czyli zlaczyc 2 wierzcholki, pierwszy
					// usuwam, dodaje do drugiego, usuwam trojkaty w ktorych sa
					// oba i zmieniam definiecje pozostalych trojkatow

					// usowam polaczenie miedzy wierzcholkami
					GraphVertex.collapseGraphEdge(vertex, edge);
					ieSize = 0;

					// przenosze sasiadow z vertex do edge, kazdy sasiad ma
					// referencje na edge

					int viSize = vertex.getNeighbours().size();
					for (int vi = 0; vi < viSize; ++vi) {
						GraphVertex translatedVertex = vertex.getNeighbours().get(vi);
						GraphVertex.makeGraphEdge(translatedVertex, edge);
						GraphVertex.collapseGraphEdge(vertex, translatedVertex);
						--viSize;

					}

					int tiSize = triangles.size();
					for (int ti = 0; ti < tiSize; ++ti) {
						int same = 0;
						GraphTriangle triangle = triangles.get(ti);

						// war konieczny musi byc edge
						if (triangle.vertexA == edge)
							++same;
						else if (triangle.vertexB == edge)
							++same;
						else if (triangle.vertexC == edge)
							++same;

						// jak same = 1 i znajdzie jeszcze vertex to usun
						// trojkat, jak same = 0 i znajdzie to ref na edge
						if (same == 1) {
							if ((triangle.vertexA == vertex) || (triangle.vertexB == vertex) || (triangle.vertexC == vertex)) {
								triangles.remove(triangle);
								// System.out.println(ti);
								--tiSize;
							}
						} else {
							if (triangle.vertexA == vertex)
								triangle.vertexA = edge;
							else if (triangle.vertexB == vertex)
								triangle.vertexB = edge;
							else if (triangle.vertexC == vertex)
								triangle.vertexC = edge;
						}

					}
				}
			}
		}
	}

}
