package gje.gquarter.water;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.Light;
import gje.gquarter.models.RawModel;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class WaterRenderer {
	public static final String DUDV_MAP_PATH = "world/misc/dudv1";
	public static final String NORMAL_MAP_PATH = "world/misc/waterNormal";
	public static final String FLOW_MAP_PATH = "world/misc/flowMap"; // TODO osobno!
	private static final Vector4f waterColor = Maths.convertColor4f(20, 66, 122, 255);

	private static RawModel quad;
	private static WaterShader shader;
	private static WaterFrameBuffers fbos;
	private static List<WaterTile> tilesList;

	private static int dudvTextureId;
	private static int normalTextureId;
	private static float waveFrequency; // TODO
	private static float waveAmplitude; // TODO
	private static float waterOpacity;
	private static int flowMapId;
	private static float normalisedTime = 0f;

	private static Matrix4f modelMatrix;

	public static void init(Matrix4f projectionMatrix) {
		float[] vertices = { -1, -1, -1, 1, 1, -1, 1, -1, -1, 1, 1, 1 };
		quad = Loader.loadToVAO(vertices, 2);

		dudvTextureId = Loader.loadTextureFiltered(DUDV_MAP_PATH,  Loader.MIPMAP_SOFT).id;
		normalTextureId = Loader.loadTextureFiltered(NORMAL_MAP_PATH,  Loader.MIPMAP_SOFT).id;
		flowMapId = Loader.loadTextureFiltered(FLOW_MAP_PATH, Loader.MIPMAP_SOFT).id;
		waveAmplitude = 0.4f; // TODO
		waterOpacity = 1.0f; // TODO
		WaterRenderer.shader = new WaterShader();
		fbos = new WaterFrameBuffers();
		shader.start();
		shader.loadWaterColour(waterColor);
		shader.connectTextureUnits();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
		tilesList = new ArrayList<WaterTile>();
		modelMatrix = new Matrix4f();
	}

	public static void loadFogParams(float gradient, float density) {
		shader.start();
		shader.loadFogParams(gradient, density);
		shader.stop();
	}

	public static void clearBatchList() {
		tilesList.clear();
	}

	public static void loadWaterTile(WaterTile tile) {
		if (MainRenderer.getSelectedCamera().isIntersectingSweepSphere(tile.getCenterPosition(), tile.getSweepSphereRadius()))
			tilesList.add(tile);
	}

	public static void removeWaterTile(WaterTile tile) {
		tilesList.remove(tile);
	}

	public static List<WaterTile> getWaterTiles() {
		return tilesList;
	}

	public static WaterTile getClosestWaterTile() {
		return tilesList.get(0);
	}
	
	public static void update(float dt){
		normalisedTime += (dt * 0.3f);
		if(normalisedTime > 1f)
			normalisedTime %= 1f;
	}


	public static void renderRelease() {
		prepareRender();
		WaterTile tile = getClosestWaterTile();
		float height = tile.getHeight()-0.05f;
		Maths.createTransformationMatrix(new Vector3f(tile.getX(), height, tile.getZ()), new Rotation3f(), tile.getTileSize(), modelMatrix);
		shader.loadModelMatrix(modelMatrix);
		shader.loadTime(normalisedTime);
		shader.loadTilingFactor(tile.getTiling());
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, quad.getVertexCount());
		unbind();
	}

	public static float getWaveAmplitude() {
		return waveAmplitude;
	}

	public static float getWaveFrequency() {
		return waveFrequency;
	}

	public static float getWaterOpacity() {
		return waterOpacity;
	}

	public static void loadWaterParams(float amplitude, float frequency, float opcaity) {
		waveAmplitude = amplitude;
		waveFrequency = frequency;
		waterOpacity = opcaity;
		shader.start();
		shader.loadWaveParams(waveFrequency, waveAmplitude); // TODO
		shader.loadWaterOpacity(waterOpacity);
		shader.stop();
	}

	public static void loadProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.loadNearFarVariables(MainRenderer.getNearPlane(), MainRenderer.getFarPlane());
		shader.stop();
	}

	private static void prepareRender() {
		// rozne obliczenia i zapis do shadera
		shader.start();
		shader.loadSunColour(MainRenderer.getWeather().getSun().getColour());
		shader.loadSkyColor(MainRenderer.getWeather().getFogColor());
		shader.loadSunPosition(MainRenderer.getWeather().getSun().getPosition());
		shader.loadViewMatrix(MainRenderer.getSelectedCamera());
		// ustawienia wody, zawarty czas statycznie!

		// to pobiaram id ci¹gu wierzcholkow z RawModelu i zapisuje jako
		// aktualnie przerabiany w GL
		// tylko 1 atryyt bo reszta jest bezcelowa - tekstura renderujre sie w
		// czasie rzeczywistym podobnie generuje sie normalne
		GL30.glBindVertexArray(quad.getVaoID());
		GL20.glEnableVertexAttribArray(0);

		// tu przypisuje id tekstur do kolejnych samplerow
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTextureId);
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalTextureId);
		// z tego odczytamy glebokosc terenu?
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
		// flow
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, flowMapId);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static WaterFrameBuffers getFbos() {
		return fbos;
	}

	private static void unbind() {
		// czyszcze aktualnie rozpatrywany ciag wierzcholkow
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		shader.stop();
	}

	public static void clean() {
		shader.cleanUp();
		clearBatchList();
	}
}