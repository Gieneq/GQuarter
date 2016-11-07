package gje.gquarter.terrain;

import gje.gquarter.core.Loader;
import gje.gquarter.map.MapRenderer;
import gje.gquarter.models.ModelData;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TerrainTexture;
import gje.gquarter.models.TerrainTexturePack;
import gje.gquarter.toolbox.BlendmapPainter;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.ToolBox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Terrain extends Vector3f {
	private static final long serialVersionUID = 1L;
	private static final float MAX_PIXEL_COLOUR = 256f * 256f * 256f;
	private static final int QUADTREE_LEVELS = 5;
	private static final int BLOCKS_COUNT = (int) (Math.pow(2, 2 * (QUADTREE_LEVELS - 1)));

	private RawModel model;
	private IntBuffer indicesIntBuffer; // TODO STATIC
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;
	private String heightmapPath;
	private int vertexCount;
	private float[][] heights;
	private float size;
	private float maxAmpl;
	private int isolineTexture;
	private int slopemapTexture;
	private int indicesCount;
	private int indicesCountMax;
	private BufferedImage heightmapImg;

	private QuadTree quadTreeRoot;
	private ArrayList<QuadTree> leavesBatch;

	private static Vector3f pointA = new Vector3f();
	private static Vector3f pointB = new Vector3f();
	private static Vector3f pointC = new Vector3f();
	private static Vector2f point2D = new Vector2f();
	private static Vector3f gradientTempA = new Vector3f();
	private static Vector3f gradientTempB = new Vector3f();
	private static Vector3f tempNormal = new Vector3f();
	private static Vector2f grad = new Vector2f();

	/**
	 * UWAGA - zapisuje indeksami w siatce typu 0, 1, 2... ale w pamieci jest
	 * wymnozone!
	 */
	public Terrain(float gridX, float gridZ, float size, float maxAmpl, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightmap, boolean create) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.size = size;
		this.maxAmpl = maxAmpl;
		this.x = gridX * size;
		this.y = 0f;
		this.z = gridZ * size;
		indicesCount = 0;
		this.heightmapPath = heightmap;
		model = generateTerrain(heightmap, create);
		indicesCountMax = model.getVertexCount();
		TerrainRenderer.loadTerrain(this);
		isolineTexture = Loader.loadTextureFiltered(heightmapPath + "Isoline",  Loader.MIPMAP_MEDIUM).id;
		slopemapTexture = Loader.loadTextureFiltered(heightmapPath + "Grad",  Loader.MIPMAP_MEDIUM).id;

		quadTreeRoot = new QuadTree(0, 0, QUADTREE_LEVELS, this);
		leavesBatch = new ArrayList<QuadTree>();
	}

	public String getHeightmapPath() {
		return heightmapPath;
	}

	public int getIsolineTextureId() {
		return isolineTexture;
	}

	public int getSlopemapTexture() {
		return slopemapTexture;
	}

	public void updateFrustumCulling() {
		leavesBatch.clear();
		for (int i = 0; i < BLOCKS_COUNT; ++i) {
			QuadTree leaf = quadTreeRoot.getLeaf(i);
			if (leaf.isCameraInsideLeafBox())
				leavesBatch.add(leaf);
			else if (leaf.isLeafInFrustum())
				leavesBatch.add(leaf);
		}

		// TODO
		// quadTreeRoot.findAllIntersectingBlocks(leavesBatch);
	}

	public ArrayList<QuadTree> getLeavesBatch() {
		return leavesBatch;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int getindicesCountProcessed() {
		return indicesCount;
	}

	public int getIndicesCountMax() {
		return indicesCountMax;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public float getHeightOfTerrainGlobal(float gloabalX, float gloabalZ) {
		return getHeightOfTerrainLocal(gloabalX - x, gloabalZ - z);
	}

	public float getHeightOfTerrainLocal(float localX, float localZ) {
		float terrainX = localX;
		float terrainZ = localZ;
		float gridSquareSize = size / (heights.length - 1f);
		// to sa jakies kordy w ktorym (int) kwadraciku jestem
		int gridX = (int) (Math.floor(terrainX / gridSquareSize));
		int gridZ = (int) (Math.floor(terrainZ / gridSquareSize));
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0)
			return 0; // out of bound :/

		float coordX = (terrainX % gridSquareSize) / gridSquareSize;
		float coordZ = (terrainZ % gridSquareSize) / gridSquareSize;

		point2D.set(coordX, coordZ);
		if (coordX <= (1 - coordZ)) {
			pointA.set(0, heights[gridX][gridZ], 0f);
			pointB.set(1, heights[gridX + 1][gridZ], 0f);
			pointC.set(0, heights[gridX][gridZ + 1], 1f);
			return Maths.barryCentric(pointA, pointB, pointC, point2D);
		}
		pointA.set(1, heights[gridX + 1][gridZ], 0f);
		pointB.set(1, heights[gridX + 1][gridZ + 1], 1f);
		pointC.set(0, heights[gridX][gridZ + 1], 1f);
		return Maths.barryCentric(pointA, pointB, pointC, point2D);
	}

	private RawModel generateTerrain(String heightmap, boolean create) {

		long lastTime = System.nanoTime();

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightmap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		vertexCount = image.getHeight();
		heightmapImg = image;
		heights = new float[vertexCount][vertexCount];

		int count = vertexCount * vertexCount;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (vertexCount - 1) * (vertexCount - 1)];
		int vertexPointer = 0;

		for (int i = 0; i < vertexCount; i++) {
			for (int j = 0; j < vertexCount; j++) {
				float height = getHeight(j, i, image);
				heights[j][i] = height;
				// polozenie wierzcholka
				vertices[vertexPointer * 3] = (float) j / ((float) vertexCount - 1) * size;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) vertexCount - 1) * size;
				// wek normalny wierzcholka
				generateNormal(j, i, tempNormal);
				normals[vertexPointer * 3] = tempNormal.x;
				normals[vertexPointer * 3 + 1] = tempNormal.y;
				normals[vertexPointer * 3 + 2] = tempNormal.z;
				// kord text wiercholka
				textureCoords[vertexPointer * 2] = (float) j / ((float) (vertexCount - 1));
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) (vertexCount - 1));
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < vertexCount - 1; ++gz) {
			for (int gx = 0; gx < vertexCount - 1; ++gx) {
				int topLeft = (gz * vertexCount) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * vertexCount) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		System.out.println("Generating heights = " + (System.nanoTime() - lastTime) / 1000000l + "[ms]");

		if (create) {
			lastTime = System.nanoTime();
			generateIsolineMap(image);
			System.out.println("Creating isoMaps = " + (System.nanoTime() - lastTime) / 1000000l + "[ms]");

			lastTime = System.nanoTime();
			BufferedImage gradient = generateGradientMapFake(image);
			System.out.println("Creating gradMaps = " + (System.nanoTime() - lastTime) / 1000000l + "[ms]");

			lastTime = System.nanoTime();
			rebuildBlendMap(blendMap.getName(), blendMap.getTextureId(), blendMap.getSizePx(), vertexCount, image, gradient);
			System.out.println("Creating blendMaps = " + (System.nanoTime() - lastTime) / 1000000l + "[ms]");
		}
		ModelData data = new ModelData(vertices, textureCoords, normals, indices, new Vector3f(), 1f);
		return Loader.loadDynamicToVAO(data, indicesIntBuffer);
	}

	@Deprecated
	public BufferedImage generateIsolineMap(BufferedImage heights) {
		int mapSize = vertexCount - 1;

		BufferedImage img = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_4BYTE_ABGR);

		for (int gz = 0; gz < mapSize; ++gz) {
			for (int gx = 0; gx < mapSize; ++gx) {
				// TODO na colorach!
				float averageHeight = (getHeight(gz, gx, heights) + getHeight(gz + 1, gx, heights) + getHeight(gz, gx + 1, heights) + getHeight(gz + 1, gx + 1, heights)) / 4f;
				// od 0 do 1
				averageHeight /= maxAmpl;
				averageHeight = averageHeight / 2f + 0.5f;
				averageHeight = Maths.clampF(averageHeight, 0f, 1f);
				// robimy schodki - rzutowaniem na inta
				averageHeight *= (MapRenderer.LEVELS_ISOLINE - 1);
				int colorARGB = MapRenderer.COLORS_ISOLINE[(int) averageHeight];
				img.setRGB(gz, gx, colorARGB);
			}
		}
		ToolBox.savePNGImage("res/" + heightmapPath + "Isoline", img);
		return img;
	}

	@Deprecated
	public BufferedImage generateGradientMapFake(BufferedImage heights) {
		int mapSize = vertexCount - 1;

		BufferedImage img = new BufferedImage(mapSize, mapSize, BufferedImage.TYPE_4BYTE_ABGR);
		for (int gz = 0; gz < mapSize; ++gz) {
			for (int gx = 0; gx < mapSize; ++gx) {

				int hZ1 = getColorChannel(gx, gz - 2, heights, 0xFF);
				int hZ2 = getColorChannel(gx, gz + 2, heights, 0xFF);
				int hX1 = getColorChannel(gx - 2, gz, heights, 0xFF);
				int hX2 = getColorChannel(gx + 2, gz, heights, 0xFF);

				int modifier = 4;

				int dZ = Math.abs(hZ1 - hZ2);
				int dX = Math.abs(hX1 - hX2);

				int channel = 255 - (Maths.clampI(modifier * (dZ + dX) / 2, 0, 255) & 0xFF);

				int colorARGB = 0xFF0000FF | (channel << 16) | (channel << 8);
				img.setRGB(gx, gz, colorARGB);
			}
		}
		ToolBox.savePNGImage("res/" + heightmapPath + "Grad", img);
		return img;
	}

	@Deprecated
	private void rebuildBlendMap(String bmName, int bmId, int blendmapSize, int heightmapSize, BufferedImage heights, BufferedImage gradient) {
		BufferedImage img = new BufferedImage(blendmapSize, blendmapSize, BufferedImage.TYPE_4BYTE_ABGR);

		int ratio = (int) Math.ceil((blendmapSize * 1f) / (heightmapSize - 1)); // 2,
																				// 4,
																				// 8...
		// System.out.println(blendmapSize + ", " + heightmapSize + ", " +
		// ratio);
		int mapX = 0;
		int mapZ = 0;

		for (int gz = 0; gz < blendmapSize; ++gz) {
			mapZ = gz / ratio;
			for (int gx = 0; gx < blendmapSize; ++gx) {
				mapX = gx / ratio;
				int color = 0;

				int hZ1 = getColorChannel(gx, gz - 2, heights, 0xFF);
				int hZ2 = getColorChannel(gx, gz + 2, heights, 0xFF);
				int hX1 = getColorChannel(gx - 2, gz, heights, 0xFF);
				int hX2 = getColorChannel(gx + 2, gz, heights, 0xFF);

				float h = (hZ1 + hZ2 + hX1 + hX2) / (4f * 255f);

				if (h > 0.8) {
					int blue = (int) Maths.linearFunctionValue(0.8f, 0f, 0.9f, 255f, h);
					if (h > 0.9)
						blue = 255;
					color = 0xFF000000 | blue;
				} else if (h < 0.2) {
					int green = (int) Maths.linearFunctionValue(0.1f, 255f, 0.2f, 0f, h);
					if (h < 0.1)
						green = 255;
					color = 0xFF000000 | (green << 8);
				} else {

					float gradBlue = (0xFF - (getColorChannel(mapX, mapZ, gradient, 0xFF00) >> 8)) / 255f;
					gradBlue = Maths.clampF(gradBlue, 0f, 1f);
					gradBlue = (float) (255d * Math.pow(gradBlue, 0.85d)) * 1.4f;
					if (gradBlue > 255f)
						gradBlue = 255f;

					int gradBlueInt = (int) gradBlue & 0xFF;
					BlendmapPainter.changeTexelColor(bmId, gx, gz, 0, 0, gradBlueInt, 0xFF);

					color = 0xFF000000 | gradBlueInt;
				}
				img.setRGB(gx, gz, color);
			}
		}

		ToolBox.savePNGImage("res/world/blendMaps/" + bmName, img);
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 1)
			x = 1;
		if (x >= image.getHeight() - 1)
			x = image.getHeight() - 2;
		if (z < 1)
			z = 1;
		if (z >= image.getWidth() - 1)
			z = image.getWidth() - 2;

		float height = image.getRGB(x, z);
		float hB = (image.getRGB(x + 1, z) + image.getRGB(x - 1, z) + image.getRGB(x, z + 1) + image.getRGB(x, z - 1)) / 4f;
		float hC = (image.getRGB(x + 1, z + 1) + image.getRGB(x - 1, z - 1) + image.getRGB(x - 1, z + 1) + image.getRGB(x + 1, z - 1)) / 4f;
		height = ((hC + hB) / 2f + height) / 2f;

		height += MAX_PIXEL_COLOUR / 2f;
		height /= MAX_PIXEL_COLOUR / 2f;
		height *= maxAmpl;
		return height;
	}

	private int getColorChannel(int x, int z, BufferedImage image, int mask) {
		if (x < 0)
			x = 0;
		if (x >= image.getHeight())
			x = image.getHeight() - 1;
		if (z < 0)
			z = 0;
		if (z >= image.getWidth())
			z = image.getWidth() - 1;

		return image.getRGB(x, z) & mask;
	}

	/** Dokladny wektor normalny!!! */
	public void getNormal(float x, float z, Vector3f temp) {
		float xMin = getHeightOfTerrainLocal(x - 1f, z);
		float xMax = getHeightOfTerrainLocal(x + 1f, z);
		float zMin = getHeightOfTerrainLocal(x, z - 1f);
		float zMax = getHeightOfTerrainLocal(x, z + 1f);

		temp.set((xMin - xMax) / 2f, 1f, (zMin - zMax) / 2f);
		temp.normalise();
	}

	/** Przyblizony wektor */
	public void generateNormal(int x, int z, Vector3f temp) {
		float xMin = getColorChannel(x - 1, z, heightmapImg, 0xFF);
		float xMax = getColorChannel(x + 1, z, heightmapImg, 0xFF);
		float zMin = getColorChannel(x, z - 1, heightmapImg, 0xFF);
		float zMax = getColorChannel(x, z + 1, heightmapImg, 0xFF);

		temp.set((xMin - xMax) / 2f, 255f, (zMin - zMax) / 2f);
		temp.normalise();
	}

	public void getSliding(float x, float z, Vector3f vector) {
		float left = getHeightOfTerrainLocal(x - 1f, z);
		float right = getHeightOfTerrainLocal(x + 1f, z);
		float down = getHeightOfTerrainLocal(x, z - 1f);
		float up = getHeightOfTerrainLocal(x, z + 1f);

		float dx = left - right;
		float dz = down - up;
		if ((dx != 0f) || (dz != 0f)) {
			grad.set(dx, dz);
			grad.normalise();

			float forward = getHeightOfTerrainLocal(x + grad.x, z + grad.y);
			float backward = getHeightOfTerrainLocal(x - grad.x, z - grad.y);
			float dy = forward - backward;

			vector.set(dx, dy, dz);
			vector.normalise();
		} else
			vector.set(0f, 1f, 0f);
	}

	@Override
	public String toString() {
		return "[" + (int) (getX() / size) + "," + (int) (getZ() / size) + "]";
	}

	public float getGridX() {
		return x / size;
	}

	public float getGridZ() {
		return y / size;
	}

	public float getSize() {
		return size;
	}

	public float getMaxAmpl() {
		return maxAmpl;
	}

	public QuadTree getQuadTreeRoot() {
		return quadTreeRoot;
	}
}
