package gje.gquarter.map;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;
import gje.gquarter.models.TerrainTexturePack;
import gje.gquarter.terrain.Terrain;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * @author Piotr <br/>
 *         Class providing terrains loading / removing from list and rendering
 *         them.
 * */
public class MapRenderer {
	public static final int LEVELS_ISOLINE = 8;
	public static final int[] COLORS_ISOLINE = {0xFF008DEB, 0xFF0AE6F0, 0xFF46E679, 0xFF82F028, 0xFFFEFE00, 0xFFFEAE00, 0xFFE67D00, 0xFFC9341E};
	public static final int LEVELS_GRAD = 4;
	public static final int[] COLORS_GRAD = {0xFFEDEADD, 0xE7DAA6, 0xFFF69F34, 0xFFFC5205};

	public static final int SATELITE_MAP_MODE = 0;
	public static final int ISOLINE_MAP_MODE = 1;
	public static final int SLOPE_MAP_MODE = 2;
	
	private static final float[] VERTICES = { -1F, 1F, -1F, -1F, 1F, 1F, 1F, -1F };
	private static RawModel quadModel;
	private static boolean visible;
	private static MapShader shader;
	private static MapFrameBuffer mapFBO;
	private static int mode;

	public static void init() {
		quadModel = Loader.loadToVAO(VERTICES, 2);
		shader = new MapShader();
		mapFBO = new MapFrameBuffer();
		mode = ISOLINE_MAP_MODE;
		
		shader.start();
		shader.connectTextureUnits();
		shader.stop();

		setVisible(true);
	}
	
	public static int getMode() {
		return mode;
	}

	public static void setMode(int mapRendererMode) {
		shader.start();
		shader.loadMode(mapRendererMode);
		shader.stop();
		mode = mapRendererMode;
	}

	/** Return if map is going to be rendered. */
	public static boolean isVisible() {
		return visible;
	}

	/**
	 * Sets if map will be rendered.
	 * 
	 * @param visible
	 *            - true if map will be rendered.
	 */
	public static void setVisible(boolean visible) {
		MapRenderer.visible = visible;
	}

	/** Render current terrain to FBO */
	public static void rendererRelease() {
		if (visible) {
			mapFBO.bindTerrMapFrameBuffer();
			shader.start();
			prepareTerrain(MainRenderer.getSelectedCamera().getRegional().getRegion().getTarrain());
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quadModel.getVertexCount());
			unbindTerrainModel();
			shader.stop();
			mapFBO.unbindCurrentFrameBuffer();
		}
	}

	/** Returns map drawn using FBO */
	public static int getMapTexture() {
		return mapFBO.getMapTexture();
	}

	/**
	 * Preparation process of rendering terrain object, use <b> before each </b>
	 * drawing stage.
	 */
	private static void prepareTerrain(Terrain terrain) {
		GL30.glBindVertexArray(quadModel.getVaoID());
		GL20.glEnableVertexAttribArray(0);

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
		GL13.glActiveTexture(GL13.GL_TEXTURE5);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getIsolineTextureId());
		GL13.glActiveTexture(GL13.GL_TEXTURE6);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getSlopemapTexture());
	}

	/**
	 * Free space in which are stored attributes, use after <b> each </b>
	 * rendering.
	 */
	private static void unbindTerrainModel() {
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	/** Clearing <b> must be used at program ending!</b> */
	public static void clean() {
		shader.cleanUp();
		mapFBO.cleanUp();
	}

	/** Returns shader class of terrain renderer */
	public static MapShader getShader() {
		return shader;
	}
}
