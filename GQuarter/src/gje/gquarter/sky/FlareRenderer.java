package gje.gquarter.sky;

import java.util.ArrayList;
import java.util.List;

import gje.gquarter.core.Loader;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.models.RawModel;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.ToolBox;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class FlareRenderer {
	private static final float[] VERTICES = { -0.5F, 0.5F, -0.5F, -0.5F, 0.5F, 0.5F, 0.5F, -0.5F };
	private static final String FLARE_TEXT_FILEPATH = "weather/flare";
	private static final String FLARE_PRESETS_FILEPATH = "weather/flare.gq";

	private static RawModel quad;
	private static FlareShader shader;
	private static Vector2f sunImagePosition;
	private static Vector2f tempOffset;
	private static Vector2f tempPosition;
	private static Vector2f tempScale;
	private static Float[] scales;
	private static Float[] distances;
	private static Vector4f tempVector;
	private static int flareTexture;
	private static int textureAtlasRows;
	private static int imagesCount;
	private static float aspectRatio;

	public static void init() {
		shader = new FlareShader();
		quad = Loader.loadToVAO(VERTICES, 2);
		sunImagePosition = new Vector2f();
		tempVector = new Vector4f();
		tempOffset = new Vector2f();
		tempPosition = new Vector2f();
		tempScale = new Vector2f();
		aspectRatio = 1f * Display.getWidth() / Display.getHeight();
		flareTexture = Loader.loadTextureFiltered(FLARE_TEXT_FILEPATH, true).id;
		loadPresets(FLARE_PRESETS_FILEPATH);
		shader.start();
		shader.loadAtlasRows(textureAtlasRows);
		shader.stop();
	}

	private static void loadPresets(String filepath) {
		ArrayList<String> lines = ToolBox.loadGQFile(filepath);
		for (String line : lines) {
			String[] args = ToolBox.splitGQLine(line);
			if (args[0].startsWith("atlas")) {
				textureAtlasRows = Integer.parseInt(args[1]);
			}
			if (args[0].startsWith("count")) {
				imagesCount = Integer.parseInt(args[1]);
				scales = new Float[imagesCount];
				distances = new Float[imagesCount];
			}
			if (args[0].startsWith("scale")) {
				int index = Integer.parseInt(args[1]);
				distances[index] = Float.parseFloat(args[2]);
				scales[index] = Float.parseFloat(args[3]);
			}
		}
	}

	public static void calculateSunImagePosition() {
		// WORLD SPACE
		Vector3f sunPosition = MainRenderer.getWeather().getRealSunPosition();
		tempVector.set(sunPosition.x, sunPosition.y, sunPosition.z, 1.0f);

		// EYE SPACE
		Matrix4f viewMatrix = MainRenderer.getSelectedCamera().getViewMatrix();
		Matrix4f.transform(viewMatrix, tempVector, tempVector);

		// CLIP SPACE
		Matrix4f projMatrix = MainRenderer.getProjectionMatrix();
		Matrix4f.transform(projMatrix, tempVector, tempVector);

		// NDC [-1:1]
		tempVector.scale(1f / tempVector.w);
		sunImagePosition.x = tempVector.x;
		sunImagePosition.y = tempVector.y;
		// sunImagePosition.x = (vector.x + 1f) * Display.getWidth() / 2f;
		// sunImagePosition.y = (-vector.y + 1f) * Display.getHeight() / 2f;
	}

	public static void calculateImagePosition(int index) {
		// zeby pokazac przejscie przez srodek...
		float dx = -2f * (sunImagePosition.x - 0f);
		float dy = -2f * (sunImagePosition.y - 0f);
		tempPosition.x = sunImagePosition.x + dx * distances[index];
		tempPosition.y = sunImagePosition.y + dy * distances[index];
		// index 0 jest w srokdu slonca
	}

	public static void rendererRelease() {
		calculateSunImagePosition();
		if (sunImagePosition.x > -1f && sunImagePosition.x < 1f && sunImagePosition.y > -1f && sunImagePosition.y < 1f) {
			shader.start();
			GL30.glBindVertexArray(quad.getVaoID());
			GL20.glEnableVertexAttribArray(0);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
//			 GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, flareTexture);

			for (int i = 0; i < imagesCount; ++i) {
				tempOffset.set(getTextureXOffset(i), getTextureYOffset(i));
				calculateImagePosition(i);
				shader.loadValues(tempOffset, tempPosition);

				tempScale.set(1f, aspectRatio);
				tempScale.scale(scales[i]);
				shader.loadScale(tempScale);
				
				float brightnes = MainRenderer.getWeather().getSun().getPosition().y;
				brightnes = Maths.clampF(brightnes, 0f, 1f);
				brightnes = (float) Math.pow(brightnes, 2.0);
				brightnes = brightnes * (1f - sunImagePosition.length());
				shader.loadBrightnesFactor(brightnes);

				GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
			}
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			shader.stop();
		}
	}

	private static float getTextureXOffset(int atlasIndex) {
		int column = atlasIndex % textureAtlasRows;
		return (float) column / (float) textureAtlasRows;
	}

	private static float getTextureYOffset(int atlasIndex) {
		int row = atlasIndex / textureAtlasRows;
		return (float) row / (float) textureAtlasRows;
	}

	public static void clean() {
		shader.cleanUp();
	}
}
