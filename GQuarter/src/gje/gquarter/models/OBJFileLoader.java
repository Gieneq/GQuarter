package gje.gquarter.models;

import gje.gquarter.toolbox.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class OBJFileLoader {

	private static final String RES_LOC = "res/";

	public static ModelData loadOBJ(String objFileName) {
		FileReader isr = null;
		File objFile = new File(RES_LOC + objFileName + ".obj");
		try {
			isr = new FileReader(objFile);
		} catch (FileNotFoundException e) {
			System.err.println("File not found in res; don't use any extention said your Master! File:" + objFile);
		}
		BufferedReader reader = new BufferedReader(isr);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2f> textures = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3f vertex = new Vector3f((float) Float.valueOf(currentLine[1]), (float) Float.valueOf(currentLine[2]), (float) Float.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);

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
					processVertex(vertex1, vertices, indices);
					processVertex(vertex2, vertices, indices);
					processVertex(vertex3, vertices, indices);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("Error reading the file");
		}

		/*
		 * W tym miejscu mam 2 wazne wektory: wektor Vertexow i wektor indexow
		 * na te vertexy - na ich polozenia w wektorze. ___________ Chce teraz
		 * nie uzywac ich, tylko na bierzaco uzupelniac graf wierzcholkow w
		 * ktorym kazdy wierzcholek zna swoich sasiadow oraz wie z jaimi
		 * wierzcholkami tworzy trojkat! ________ Podczas generowania trojkaty w
		 * ktorych oba wierzcholki wystepuja usowam i tworze nowy wierzcholek i
		 * aktualizuje referencje do niego _____ wciaz pozostaje poprzednie
		 * trojkaty tylko z referencja na wierzcholek powstaly z polaczenia 2
		 * poprzednich!
		 */

		removeUnusedVertices(vertices);
		float[] verticesArray = new float[vertices.size() * 3];
		float[] texturesArray = new float[vertices.size() * 2];
		float[] normalsArray = new float[vertices.size() * 3];
		Vector3f massCenter = new Vector3f();
		float furthest = convertDataToArrays(vertices, textures, normals, verticesArray, texturesArray, normalsArray, massCenter);
		int[] indicesArray = convertIndicesListToArray(indices);
		ModelData data = new ModelData(verticesArray, texturesArray, normalsArray, indicesArray, massCenter, furthest);
		return data;
	}

	private static void processVertex(String[] vertex, List<Vertex> vertices, List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
		} else {
			dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices, vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private static float convertDataToArrays(List<Vertex> vertices, List<Vector2f> textures, List<Vector3f> normals, float[] verticesArray, float[] texturesArray, float[] normalsArray, Vector3f massCenter) {
		float furthestPoint = 0;
		Vector3f furthestVertex = new Vector3f();
		// dodaje do masy potem podziele przez dlugosci
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
				furthestVertex.set(currentVertex.getPosition());
			}
			Vector3f position = currentVertex.getPosition();
			Vector3f.add(massCenter, position, massCenter);
			Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
		}
		// srednia z odleglosci
		massCenter.scale(1f / vertices.size());
		// odejuje srodek i najdalszy punkt i mam z rego promien
		Vector3f.sub(massCenter, furthestVertex, furthestVertex);
		return furthestVertex.length();
	}

	private static void dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, indices, vertices);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
			}
		}
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

}