package gje.gquarter.core;

import gje.gquarter.models.ModelData;
import gje.gquarter.models.ModelTexture;
import gje.gquarter.models.OBJFileLoader;
import gje.gquarter.models.OBJXLoader;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TextureData;
import gje.gquarter.models.TextureLinkData;
import gje.gquarter.models.TexturedModel;
import gje.gquarter.toolbox.ToolBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.NVXGpuMemoryInfo;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

/**
 * @author Piotr <br/>
 *         Class providing loading and keeping track of all items in GPU memory.
 * */
public class Loader {
	private static List<Integer> vaos;
	private static List<Integer> vbos;
	private static List<Integer> textures;
	private static HashMap<String, TextureLinkData> textureTemp;

	private static long totalModelMemoeroUsage;
	private static long totalTextureMememoryUsage;

	public static void init() {
		vaos = new ArrayList<Integer>();
		vbos = new ArrayList<Integer>();
		textures = new ArrayList<Integer>();
		textureTemp = new HashMap<String, TextureLinkData>();
		totalModelMemoeroUsage = 0;
		totalTextureMememoryUsage = 0;
	}

	/** laduje kazda normalna bryle statyczna */
	public static RawModel loadStaticToVAO(ModelData modlesData) {
		int vaoID = createVAO();
		int indicesVBoId = bindIndicesBuffer(modlesData.getIndices());
		storeDataInAttributeList(0, 3, modlesData.getVertices());
		storeDataInAttributeList(1, 2, modlesData.getTextureCoords());
		storeDataInAttributeList(2, 3, modlesData.getNormals());
		unbindVAO();
		return new RawModel(vaoID, modlesData.getIndices().length, indicesVBoId, modlesData.getMassCenter(), modlesData.getBoundingRadius());
	}

	/** laduje kazda normalna bryle dynamiczna tj. z LOD/Geomipmapping */
	public static RawModel loadDynamicToVAO(ModelData modlesData, IntBuffer intsBuffer) {
		int vaoID = createVAO();

		intsBuffer = storeDataInIntBuffer(modlesData.getIndices());
		int indicesVBoId = GL15.glGenBuffers();
		vbos.add(indicesVBoId);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesVBoId);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, intsBuffer, GL15.GL_STREAM_DRAW);

		storeDataInAttributeList(0, 3, modlesData.getVertices());
		storeDataInAttributeList(1, 2, modlesData.getTextureCoords());
		storeDataInAttributeList(2, 3, modlesData.getNormals());
		unbindVAO();
		return new RawModel(vaoID, modlesData.getIndices().length, indicesVBoId, modlesData.getMassCenter(), modlesData.getBoundingRadius());
	}

	private static int bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		return vboID; // ma sie przydac w terenie
	}

	/** laduje kazda normalna bryle */
	public static int loadToVAO(float[] positions, float[] textureCoords) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, 2, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		unbindVAO();
		return vaoID;
	}

	/** laduje proste ksztalty z samych wierzcholkow, indicesVBO = 0 */
	public static RawModel loadToVAO(float[] positions, int coordsCount) {
		int vaoID = createVAO();
		storeDataInAttributeList(0, coordsCount, positions);// wspolrzedne
		unbindVAO();
		// tu te ostatnie parametry takie troche na odczepne :p
		return new RawModel(vaoID, positions.length / coordsCount, 0, new Vector3f(), 1f);
	}

	public int createEmptyFloatVbo(int floatsCount, FloatBuffer buffer) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public static int createEmptyIntVbo(int intsCount, IntBuffer buffer) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public static void addInstancedAttr(int vao, int vbo, int attr, int dataSize, int instancedDataLength, int offset) {
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		// * 4 - w bajtach, float ma 4
		GL20.glVertexAttribPointer(attr, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attr, 1); // co instacje sie zmienia
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static void updateFloatsVbo(int vbo, float[] dataFloats, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(dataFloats);
		buffer.flip();
		// System.out.println(dataFloats.length + "/" + buffer.capacity());

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		// GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
		// GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4,
		// GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static void updateIntsVbo(int vbo, int[] dataInts, IntBuffer buffer) {
		buffer.clear();
		buffer.put(dataInts);
		buffer.flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/** Sluzy do LOD!! */
	public static void updateIndicesVbo(int vbo, int[] dataInts, IntBuffer buffer) {
		buffer.clear();
		buffer.put(dataInts);
		buffer.flip();

		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	/** Sluzy do Geomipmpapping!! */
	public static void updateIndicesVbo(int vbo, IntBuffer buffer) {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ELEMENT_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * Metoda stosuje wczesniej zapisane pola! lepiej uwazac. Ta jest bardziej
	 * rozbudowana o glowMape
	 */
	public static TexturedModel buildTexturedModelWithGlowMap(String objFileName, String textureFilename, String glowMapFilename) {
		ModelData modelData = OBJFileLoader.loadOBJ(objFileName);
		ModelTexture modelTexture = new ModelTexture(loadTextureFiltered(textureFilename, true));
		ModelTexture glowMapTexture = new ModelTexture(loadTextureFiltered(glowMapFilename, true));
		RawModel rawModel = loadStaticToVAO(modelData);
		return new TexturedModel(rawModel, modelTexture, glowMapTexture);
	}
	
	public static RawModel buildRawModel(String objFileName) {
		ModelData modelData = OBJFileLoader.loadOBJ(objFileName);
//		ModelData modelData = OBJXLoader.loadOBJ(objFileName)[OBJXLoader.LOD_LEVELS-1];
		return loadStaticToVAO(modelData);
	}

	/**
	 * Metoda stosuje wczesniej zapisane pola! lepiej uwazac. Ta jest prostsza
	 */
	public static TexturedModel buildTexturedModel(String objFileName, String textureFilename) {
		ModelData modelData = OBJFileLoader.loadOBJ(objFileName);
//		ModelData modelData = OBJXLoader.loadOBJ(objFileName)[OBJXLoader.LOD_LEVELS-1];
		ModelTexture modelTexture = new ModelTexture(loadTextureFiltered(textureFilename, true));
		RawModel rawModel = loadStaticToVAO(modelData);
		return new TexturedModel(rawModel, modelTexture);
	}

	public static String getLoadingSummary() {
		String msg = "";
		int nvidia_total_memory = (int) (GL11.glGetInteger(NVXGpuMemoryInfo.GL_GPU_MEMORY_INFO_TOTAL_AVAILABLE_MEMORY_NVX) / 1000f);
		msg += ("Total GPU Memory [MB]: " + nvidia_total_memory + "\n");
		msg += ("Total Textures Memory [MB]: " + Loader.getTotalTextureMememoryUsage() / 1000000f + ", textures count: " + textures.size() + "\n");
		msg += ("Total Models Memory [MB]: ??" + Loader.getTotalModelMemoeroUsage() / 1000000f + ", models count: " + vaos.size() + "\n");
		return msg;
	}

	public static TextureLinkData loadTextureFiltered(String filename, boolean record) {
		// na poczatku sprawdzam czy juz tego nie zaladowalem, np w gui jest
		// tego duzo!
		for (String key : textureTemp.keySet()) {
			if (key.equals(filename))
				return textureTemp.get(key);
		}
		int dataBytes = 0;
		int size = 0;
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + filename + ".png"));
			if (record)
				dataBytes = texture.getTextureData().length;
			totalTextureMememoryUsage += dataBytes;
			size = texture.getTextureHeight(); // RGB
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
			// mipmapping
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f); // -0.1f
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			if (GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else
				System.out.println("Anisotropic not supported :(");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		TextureLinkData data = new TextureLinkData(texture.getTextureID(), size);
		// ToolBox.log(Loader.class, "TextureFiltId: " + textureID + " " +
		// filename + "bytes: " + dataBytes);
		textures.add(data.id);
		textureTemp.put(filename, data);
		return data;
	}

	public static long getTotalModelMemoeroUsage() {
		return totalModelMemoeroUsage;
	}

	public static long getTotalTextureMememoryUsage() {
		return totalTextureMememoryUsage;
	}

	public static int loadCubeMap(String[] textureFiles, int textureActive) {
		int textID = GL11.glGenTextures();
		GL13.glActiveTexture(textureActive);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, textID);

		for (int i = 0; i < textureFiles.length; ++i) {
			TextureData data = decodeTextureFile("res/" + textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}

		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		ToolBox.log(Loader.class, "TextureCubeId: " + textID + " " + textureFiles[0]);
		textures.add(textID);
		return textID;
	}

	private static TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName + ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}

	private static int createVAO() {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}

	public static void clean() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	private static void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

	}

	private static void unbindVAO() {
		GL30.glBindVertexArray(0);
	}

	private static IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private static FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
