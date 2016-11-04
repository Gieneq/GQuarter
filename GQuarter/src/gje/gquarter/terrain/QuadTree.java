package gje.gquarter.terrain;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.toolbox.Maths;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;

public class QuadTree {
	private static final float OFFSET = 2f;
	private Terrain terrain;
	// index w obrebie danego poziomu
	private int index;
	// poziom, 0 oznacza root
	private int level;
	// ilosc blokow w krawedzi calego terenu
	private int blocksCount;
	// ilosc wierzcholkow w krawedzi jednego bloku
	private int size;
	// ilosc wierzcholkow w krawedzi calego terenu
	private int terrainVertexCount;

	private int[] indicesInts;
	private IntBuffer indicesBuffer;

	private Vector3f[] corners;
	private QuadTree[] children;

	private Vector3f massCenter;
	private float boundingSphereRadius;

	public QuadTree(int index, int level, int levelsCount, Terrain terrain) {
		this.terrain = terrain;
		this.index = index;
		this.level = level;
		this.blocksCount = (int) Math.pow(2, level);
		this.terrainVertexCount = terrain.getVertexCount();
		this.size = (int) ((terrainVertexCount - 1.0) / Math.pow(2, level)) + 1;

		this.corners = new Vector3f[8];
		for (int i = 0; i < 8; ++i)
			this.corners[i] = new Vector3f();
		massCenter = new Vector3f();

		calculateBounding();
		if (level < levelsCount - 1) {
			this.children = new QuadTree[4];
			for (int i = 0; i < 4; ++i)
				this.children[i] = new QuadTree(index * 4 + i, level + 1, levelsCount, terrain);
		} else {
			// liscie nie maja dzieci :(
			// koniec rekurencji, liscie maja

			this.indicesInts = new int[6 * (size - 1) * (size - 1)];
			this.indicesBuffer = BufferUtils.createIntBuffer(indicesInts.length);
			// TODO a gdyby jeszcze geomipmapping w nizszych lvlach?

			calculateIndicesBuffer();
		}
	}

	public QuadTree getLeaf(int index) {
		if (!isLeaf()) {
			QuadTree child;
			for (int i = 0; i < 4; ++i) {
				child = children[i].getLeaf(index);
				if ((child != null) && (child.index == index))
					return child;
			}
		} else if (this.index == index)
			return this;
		return null;
	}

	public boolean isLeaf() {
		return (children == null);
		// return (level == )
	}

	public void calculateIndicesBuffer() {
		// koordynaty bloku
		int blockX = index % blocksCount;
		int blockZ = index / blocksCount;

		// koordynaty pirwszej kratki w bloku
		int fieldX = blockX * (size - 1);
		int fieldZ = blockZ * (size - 1);

		// iterujemy, wartosci = ineksom
		int pointer = 0;
		for (int iz = fieldZ; iz < fieldZ + size - 1; ++iz) {
			for (int ix = fieldX; ix < fieldX + size - 1; ++ix) {
				int topLeft = (iz * terrainVertexCount) + ix;
				int topRight = topLeft + 1;
				int bottomLeft = ((iz + 1) * terrainVertexCount) + ix;
				int bottomRight = bottomLeft + 1;
				indicesInts[pointer++] = topLeft;
				indicesInts[pointer++] = bottomLeft;
				indicesInts[pointer++] = topRight;
				indicesInts[pointer++] = topRight;
				indicesInts[pointer++] = bottomLeft;
				indicesInts[pointer++] = bottomRight;
			}
		}

		// uzupelniam bufor
		indicesBuffer.clear();
		indicesBuffer.put(indicesInts);
		indicesBuffer.flip();
	}

	@Deprecated
	public void printAllIndices() {
		String ss = "";
		for (int i = 0; i < getIndicesInts().length; ++i) {
			ss += (getIndicesInts()[i] + ", ");
		}
		System.out.println(ss);
	}

	private void calculateBounding() {
		// koordynaty bloku
		int blockX = index % blocksCount;
		int blockZ = index / blocksCount;

		// koordynaty pirwszej kratki w bloku
		int fieldX = blockX * (size - 1);
		int fieldZ = blockZ * (size - 1);

		// rozmiar jednej kratki w jednostkach fizycznych
		float terrainSize = terrain.getSize();
		float squareSize = terrainSize / (terrainSize - 1);

		// koordynaty w X i Z
		float bbX = fieldX * squareSize;
		float bbZ = fieldZ * squareSize;
		float bbWH = size * squareSize;
		// System.out.println("id: " + index + ", lvl: " + level + "> " + bbWH);

		// koordynaty w Y
		float bbMinY = terrain.getHeightOfTerrainLocal(bbX, bbZ);
		float bbMaxY = bbMinY;

		for (int iz = 0; iz < size; ++iz) {
			for (int ix = 0; ix < size; ++ix) {
				float tHeight = terrain.getHeightOfTerrainLocal(bbX + ix * squareSize, bbZ + iz * squareSize);
				if (tHeight < bbMinY)
					bbMinY = tHeight;
				if (tHeight > bbMaxY)
					bbMaxY = tHeight;
			}
		}

		// zapisanie
		int pointer = 0;
		corners[pointer++].set(bbX, bbMinY, bbZ);
		corners[pointer++].set(bbX, bbMinY, bbZ + bbWH);
		corners[pointer++].set(bbX + bbWH, bbMinY, bbZ);
		corners[pointer++].set(bbX + bbWH, bbMinY, bbZ + bbWH);

		corners[pointer++].set(bbX, bbMaxY, bbZ);
		corners[pointer++].set(bbX, bbMaxY, bbZ + bbWH);
		corners[pointer++].set(bbX + bbWH, bbMaxY, bbZ);
		corners[pointer++].set(bbX + bbWH, bbMaxY, bbZ + bbWH);

		// TODO to nie dziala, za duzo przepuszcza zamiast 2 - 400 :o
		massCenter.set(bbX + bbWH / 2f, (bbMaxY + bbMinY) / 2f, bbZ + bbWH / 2f);
		boundingSphereRadius = (bbWH * bbWH / 4f * Maths.SQRT2);
	}

	@Deprecated
	public boolean isSphereInFrustum() {
		Camera cam = MainRenderer.getSelectedCamera();
		return cam.isInsideViewFrustrum(massCenter, boundingSphereRadius);
	}

	public boolean isCameraInsideLeafBox() {
		// sprawdza czy kamera jest wewnatrz liscia
		Vector3f camPos = MainRenderer.getSelectedCamera().getPosition();
		if (camPos.x >= corners[0].x && camPos.x <= corners[2].x) {
			if (camPos.z >= corners[0].z && camPos.z <= corners[1].z)
				return true;
		}
		return false;
	}

	public boolean isLeafInFrustum() {
		// to sprawdza czy wiercholki liscia przecinaja view frustum
		Camera cam = MainRenderer.getSelectedCamera();
		for (int i = 0; i < 8; ++i) {
			if (cam.isInsideViewFrustrum(corners[i], OFFSET))
				return true;
		}
		return false;
	}

	@Deprecated
	public void findAllIntersectingBlocksOld(ArrayList<QuadTree> leaves) {
		// czy jest widziany
		if (isLeafInFrustum()) {
			// jak nie jest lisciem to sprawdzam kazdego potomka
			if (!isLeaf()) {
				for (int i = 0; i < 4; ++i)
					children[i].findAllIntersectingBlocksOld(leaves);
			} else {
				// jest lisciem i jest widziany wiec dodaje :D
				leaves.add(this);
			}
		}
	}

	@Deprecated
	public void findAllIntersectingBlocks(ArrayList<QuadTree> leaves) {
		// jak nie jest lisciem to sprawdzam kazdego potomka
		if (!isLeaf()) {
			for (int i = 0; i < 4; ++i)
				if (children[i].isLeafInFrustum()) // TODO trzeba dodac zawiera
													// sie
					children[i].findAllIntersectingBlocks(leaves);
		} else
			leaves.add(this);
	}

	public int getLevel() {
		return level;
	}

	public int[] getIndicesInts() {
		return indicesInts;
	}

	public IntBuffer getIndicesBuffer() {
		return indicesBuffer;
	}

	public Vector3f[] getCorners() {
		return corners;
	}

	public QuadTree[] getChildren() {
		return children;
	}

	public void useBuffer() {
		Loader.updateIndicesVbo(terrain.getModel().getIndicesVboId(), indicesBuffer);
	}

	public int getVertexCount() {
		return indicesInts.length;
	}

	public Vector3f getMassCenter() {
		return massCenter;
	}

	public float getBoundingSphereRadius() {
		return boundingSphereRadius;
	}
}
