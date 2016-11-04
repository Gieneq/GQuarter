package gje.gquarter.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import gje.gquarter.core.Loader;
import gje.gquarter.models.RawModel;
import gje.gquarter.toolbox.Maths;

public class GuiTextureRenderer {
	private static RawModel quad;
	private static GuiShader shader;
	private static Map<GuiPanel, List<GuiTexture>> texturesMap;

	public static void init() {
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
		quad = Loader.loadToVAO(positions, 2);
		shader = new GuiShader();
		texturesMap = new HashMap<GuiPanel, List<GuiTexture>>();
	}

	public static void clearBatchList() {
		texturesMap.clear();
	}

	/**
	 * Load panel's textures, where panel is key in hasmap and batch is list of
	 * textures.
	 */
	public static void loadGuiTexture(GuiTexture texture) {
		List<GuiTexture> textureBatch = texturesMap.get(texture.getParentPanel());
		if (textureBatch == null) {
			textureBatch = new ArrayList<GuiTexture>();
			texturesMap.put(texture.getParentPanel(), textureBatch);
		}
		textureBatch.add(texture);
	}

	public static void removePanelTextures(GuiPanel badlyPanel) {
		texturesMap.remove(badlyPanel);
	}

	/** Remove hasmap's key - panel - with all it's textures */
	public static void removeGuiTexture(GuiTexture texture) {
		List<GuiTexture> textureBatch = texturesMap.get(texture.getParentPanel());
		textureBatch.remove(texture);
		if (textureBatch.isEmpty())
			texturesMap.remove(textureBatch);
	}

	public static void rendererRelease(GuiPanel lovelyPanel) {
		if (texturesMap.get(lovelyPanel) != null) {
			shader.start();
			GL30.glBindVertexArray(quad.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			for (GuiTexture gt : texturesMap.get(lovelyPanel)) {
				Matrix4f matrix = Maths.createTransformationMatrix(gt.getPosition(), gt.getScale(), gt.getRotation());
				shader.loadTransformation(matrix);
				shader.loadTextureTranslation(gt.getTextureTranslation());
				shader.loadTextureZoom(gt.getTextureZoom());
				shader.loadTypeOfFilling(gt.getTypeRegister());

				if (gt.isUsing(GuiTexture.TYPE_FILLING_TEXTURE)) {
					GL13.glActiveTexture(GL13.GL_TEXTURE0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, gt.getTextureId());
				}

				if (gt.isUsing(GuiTexture.TYPE_FILLING_COLOUR))
					shader.loadColour(gt.getColour(), gt.getMixColorValue());

				if (gt.isUsing(GuiTexture.TYPE_FILLING_CIRCULAR))
					shader.loadRadius(gt.getRadiusMin(), gt.getRadiusMax());

				shader.loadFinalAlpha(gt.getFinalAlphaValue());
				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			shader.stop();
		}
	}

	public static void clean() {
		shader.cleanUp();
		clearBatchList();
	}
}
