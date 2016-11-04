package gje.gquarter.terrain;

import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TerrainTexturePack;
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

/**
 * @author Piotr <br/>
 *         Class providing terrains loading / removing from list and rendering
 *         them.
 * */
public class TerrainRenderer {
	private static final float SHINE_DAMPER_PRESET = 6f;
	private static final float REFLECTIVITY_PRESET = 0.2F;

	private static TerrainShader shader;
	private static List<Terrain> terrainsList;
	private static boolean visible;

	private static final Rotation3f rotaionZero = new Rotation3f();
	private static Matrix4f transformatonMatrix = new Matrix4f();
	private static int processedBlocks = 0;
	private static int processedVerticesCount = 0;
	private static int maxVerticesCount = 0;

	/**
	 * Initialize values of renderer. Must be invoked before terrain processing!
	 * <i> Includes shader start stop! </i> <br/>
	 * 
	 * @param projectionMatrix
	 *            - matrix of projection, must be updated every time projection
	 *            planes or FOV is changed.
	 */
	public static void init(Matrix4f projectionMatrix) {
		shader = new TerrainShader();
		terrainsList = new ArrayList<Terrain>();

		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.connectTextureUnits();
		shader.loadShineVariables(SHINE_DAMPER_PRESET, REFLECTIVITY_PRESET);
		shader.stop();

		setVisible(true);
	}

	/** Return if terrains stored in list are rendered to the next stage. */
	public static boolean isVisible() {
		return visible;
	}

	/**
	 * Sets if terrains stored in list are rendered to the next stage.
	 * 
	 * @param visible
	 *            - true if terrains are going to be visible.
	 */
	public static void setVisible(boolean visible) {
		TerrainRenderer.visible = visible;
	}

	/**
	 * Return count of processed indices in rendering stage with culling
	 * applied.
	 */
	public static int getProcessedIndicesCount() {
		return processedVerticesCount;
	}

	/**
	 * Return count of processed blocks in rendering stage with culling applied.
	 */
	public static int getProcessedBlocksCount() {
		return processedBlocks;
	}

	/**
	 * Return count of processed indices in rendering stage without culling
	 * applied (maximal possible count).
	 */
	public static int getMaxIndicesCount() {
		return maxVerticesCount;
	}

	/**
	 * After changing projection params like planes or FOV this method updates
	 * projectionMatrix. <i> Includes shader start stop! </i>
	 * 
	 * @param projectionMatrix
	 *            - new matrix of projection.
	 */
	public static void loadProjectionMatrix(Matrix4f projectionMatrix) {
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}

	/**
	 * Upload new params of fog propagation. <i> Includes shader start stop!
	 * </i>
	 * 
	 * @param gradient
	 *            - how steep change of fog is,
	 * @param density
	 *            - where is trigger of fog density.
	 */
	public static void loadFogParams(float gradient, float density) {
		shader.start();
		shader.loadFogParams(gradient, density);
		shader.stop();
	}

	/**
	 * Upload new params of projection planes. <i> Includes shader start stop!
	 * </i>
	 * 
	 * @param near
	 *            - near plane,
	 * @param far
	 *            - far plane.
	 */
	public static void loadPlanes(float near, float far) {
		shader.start();
		shader.loadPlanes(near, far);
		shader.stop();
	}

	/**
	 * Use to lead light material attributes of all terrain tiles which will be
	 * next rendered.
	 * 
	 * @param shineDamper
	 *            - how hard the edge of light spot is,
	 * @param reflectivity
	 *            - how many light is reflectedor how bright spot is.
	 */
	public static void loadTerrainMaterialParams(float shineDamper, float reflectivity) {
		shader.start();
		shader.loadShineVariables(shineDamper, reflectivity);
		shader.stop();
	}

	/**
	 * Ckearing all elements in the list. Recomended using when necessary. Use
	 * Remove method to distore object when not needed.
	 */
	public static void clearBatchList() {
		terrainsList.clear();
	}

	/**
	 * Add terrain to the list, provides checking if terrain in intersecting
	 * frustum, and culls extra vertices. Is terrain visibility is off there
	 * will be no terrains loaded
	 * 
	 * @param terr
	 *            - terrain to be processed.
	 */
	public static void loadTerrain(Terrain terr) {
		// TODO culling itp.. sprawdzanie kolizji z AABB kratki
		if (visible) {
			terrainsList.add(terr);
		}
	}

	/**
	 * Used to remove not used terrain from batch list.
	 * 
	 * @param terr
	 *            - to be removed
	 */
	public static void removeTerrain(Terrain terr) {
		terrainsList.remove(terr);
	}

	/** Removeing all not necessary terrains from list... */
	public static void organiseTerrains() {
		// TODO a co gdy podczas ruchu kamera do renderowania np wody zmieni sie
		// dramatycznie kat stad ilosc indicow.
	}

	/** Render all stored object in lists excluding clled vertices. */
	public static void rendererRelease(Vector4f clipPlane) {
		if (visible) {
			shader.start();
			shader.loadClipPlane(clipPlane);
			shader.loadSkyColor(MainRenderer.getWeather().getFogColor());
			shader.loadLights(MainRenderer.getLightList());
			shader.loadViewMatrix(MainRenderer.getSelectedCamera());
			processedBlocks = 0;
			processedVerticesCount = 0;
			maxVerticesCount = 0;
			for (Terrain t : terrainsList) {
				loadModelMatrix(t);
				processedBlocks += t.getLeavesBatch().size();
				for (QuadTree leaf : t.getLeavesBatch()) {
					leaf.useBuffer();
					prepareTerrain(t);
					GL11.glDrawElements(GL11.GL_TRIANGLES, leaf.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
					processedVerticesCount += leaf.getVertexCount();
					unbindTerrainModel();
				}
				maxVerticesCount += t.getIndicesCountMax();
			}
			shader.stop();
		} else {
			processedVerticesCount = 0;
			maxVerticesCount = 0;
		}
	}

	/**
	 * Preparation process of rendering terrain object, use <b> before each </b>
	 * drawing stage.
	 */
	private static void prepareTerrain(Terrain terrain) {
		RawModel rawModel = terrain.getModel();
		GL30.glBindVertexArray(rawModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		bindTextures(terrain);
	}

	/**
	 * Connect texture units with their id numbers. Use before <b> each </b>
	 * rendering.
	 */
	private static void bindTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBgTexture().getTextureId());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureId());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureId());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureId());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureId());
	}

	/**
	 * Free space in which are stored attributes, use after <b> each </b>
	 * rendering.
	 */
	private static void unbindTerrainModel() {
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0);
	}

	/**
	 * Creates transformation matrix from specyfic terrain.
	 * 
	 * @param terrain
	 *            - loaded terrain object and extracts its model and transforms
	 *            it into model matrix.
	 */
	private static void loadModelMatrix(Terrain terrain) {
		Maths.createTransformationMatrix((Vector3f) terrain, rotaionZero, 1, transformatonMatrix);
		shader.loadTransformationMatrix(transformatonMatrix);
	}

	/** Clearing <b> must be used at program ending!</b> */
	public static void clean() {
		shader.cleanUp();
		clearBatchList();
	}

	/** Returns shader class of terrain renderer */
	public static TerrainShader getShader() {
		return shader;
	}
}
