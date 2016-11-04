package gje.gquarter.models;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class OBJXLoader {
	public static final int LOD_LEVELS = 2;
	private static final String RES_LOC = "res/";

	public static ModelData[] loadOBJ(String objFileName) {
		ModelData[] modalDataArray = new ModelData[LOD_LEVELS];
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName + ".obj");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention said your Master! File:" + objFile);
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		GraphMesh mesh = new GraphMesh();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();

		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					GraphVertex newVertex = new GraphVertex(mesh.getVertices().size(), vertex);
					mesh.getVertices().add(newVertex);

				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2f texture = new Vector2f((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3f normal = new Vector3f((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					String[] currentLine = line.split(" ");
					String[] vertex1 = currentLine[1].split("/");
					String[] vertex2 = currentLine[2].split("/");
					String[] vertex3 = currentLine[3].split("/");

					// dodaje do vektora nowe wierzcholki i w razie czego tworze
					// duplikaty na laczeniach
					GraphVertex vertexA = processVertex(vertex1, mesh.getVertices());
					GraphVertex vertexB = processVertex(vertex2, mesh.getVertices());
					GraphVertex vertexC = processVertex(vertex3, mesh.getVertices());

					// majac woerzcholki ktore posiadaja index, pozycje, tc, n
					// tworze z nich trojkat
					GraphTriangle newTriangle = new GraphTriangle(vertexA, vertexB, vertexC);
					mesh.getTriangles().add(newTriangle);

					// tworze graf dodajac 3 nowe krawedzie (chyba ze jakas juz
					// istnieje), nie spisuje indexow w wektorze bo odtworze je
					// z trojkatow. Metoda zwraca wierzcholek z ktorego tworze
					// trojkat, a potem przepisuje do tablicy.
					GraphVertex.makeGraphEdge(vertexA, vertexB);
					GraphVertex.makeGraphEdge(vertexA, vertexC);
					GraphVertex.makeGraphEdge(vertexB, vertexC);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}

		/* Wirzcholki dodane przez obj v ale nie uzyte w obj f */
		for (GraphVertex vertex : mesh.getVertices()) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}

		/*
		 * W tym miejscu mam graf z vierzcholkow. moge tez odszukac wszystkie
		 * trojkaty w jakich jest dany wierzcholek. Teraz trzeba wygenerowac dla
		 * zadanego poziomu LOD wszystkie RawModele!
		 */
		for (int iLevel = 0; iLevel < LOD_LEVELS; ++iLevel) {
			/*
			 * Przy uproszczeniu nalezy przesuwac indexy w wektorze, bo to ma
			 * istotne znaczenie przy przepisywaiu do tablic
			 */
			int verticesCount = mesh.countVertices();
			System.out.println("OBJX-verticesCount: " + verticesCount);
			float[] verticesArray = new float[verticesCount * 3];
			float[] texturesArray = new float[verticesCount * 2];
			float[] normalsArray = new float[verticesCount * 3];
			int[] indicesArray;

			float boundingSphereRadius = 0f;
			float furthest = 0f;
			Vector3f furthestPoint = new Vector3f();
			Vector3f massCenterPoint = new Vector3f();

			for (int i = 0; i < verticesCount; ++i) {
				GraphVertex currentVertex = mesh.getVertices().get(i);

				// czesciowe ustalanie boundingSphere i massCenterPoint
				float currentLength = currentVertex.getLength();
				if (currentLength > furthest) {
					furthest = currentLength;
					furthestPoint.set(currentVertex.getPosition());
				}
				Vector3f position = currentVertex.getPosition();
				// TODO przechowywac wewnatrz wierzcholku i podczas laczenia
				// usredniac?
				Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
				Vector3f normalVector = normals.get(currentVertex.getNormalIndex());

				Vector3f.add(massCenterPoint, position, massCenterPoint);
				verticesArray[i * 3] = position.x;
				verticesArray[i * 3 + 1] = position.y;
				verticesArray[i * 3 + 2] = position.z;
				texturesArray[i * 2] = textureCoord.x;
				texturesArray[i * 2 + 1] = 1 - textureCoord.y;
				normalsArray[i * 3] = normalVector.x;
				normalsArray[i * 3 + 1] = normalVector.y;
				normalsArray[i * 3 + 2] = normalVector.z;
			}
			// policzony srodek masy
			massCenterPoint.scale(1f / verticesCount);

			// policzony promien bounding sphere
			Vector3f.sub(massCenterPoint, furthestPoint, furthestPoint);
			boundingSphereRadius = furthestPoint.length();

			indicesArray = mesh.getIndicesArray();
			System.out.println("OBJX-indicesCount: " + indicesArray.length);
			System.out.println("-----------end");
			ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, massCenterPoint, boundingSphereRadius);
			modalDataArray[iLevel] = data;
			
			//jezeli tak to bedzie kolejny poziom i upraszczam
			if(iLevel < LOD_LEVELS){
				mesh.simplifyRev(boundingSphereRadius, 0.39f);
			}
		}
		return modalDataArray;
	}

	private static GraphVertex processVertex(String[] vertex, List<GraphVertex> vertices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		GraphVertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			// sytuacja normalna
			return currentVertex;
		} else {
			// jezeli chce stworzyc zlozona krawedz z wielu tc i n, ale wiem ze
			// w pliku obj posluguje sie indexem parenta (currentVertex)!

			// tu musze sprawdzic czy nie trzeb stworzyc nowego wierzcholka, lub
			// istnieje on gdzies w drzewie.
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, vertices);
		}
	}

	private static GraphVertex dealWithAlreadyProcessedVertex(GraphVertex previousVertex, int newTextureIndex, int newNormalIndex, List<GraphVertex> vertices) {
		// ma te same tc i n wiec moge uzyc jego indexu kolejny raz
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex))
			return previousVertex;
		// ma inny tc i n wiec dodaje go na sam dol drzewa
		else {
			GraphVertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, vertices);
			}
			// jestem w lisciu wiec moge dodac nowy wierzcholek
			else {
				GraphVertex duplicateVertex = new GraphVertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				return duplicateVertex;
			}
		}
	}
}