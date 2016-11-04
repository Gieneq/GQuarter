package gje.gmapper.main;

import gje.gquarter.toolbox.Maths;

import java.awt.image.BufferedImage;
import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

public class GeneratorCore {
	/*
	 * To nalezy wspoldzielic z silnikie mgraficaznym w razie potrzeby
	 * generowania w czasie dzialania!
	 */

	public static final int LEVELS_ISOLINE = 8;
	public static final int[] COLORS_ISOLINE = { 0xFF008DEB, 0xFF0AE6F0, 0xFF46E679, 0xFF82F028, 0xFFFEFE00, 0xFFFEAE00, 0xFFE67D00, 0xFFC9341E };

	private static Random rand;
	private static int window;
	private static Vector3f gradientTempA;

	public static void init(long seed) {
		rand = new Random(seed);
		gradientTempA = new Vector3f();
		window = 1;
	}

	public static BufferedImage generateSlopeMap(BufferedImage hmImg) {
		int mapSizeW = hmImg.getWidth() - 1;
		int mapSizeH = hmImg.getHeight() - 1;

		BufferedImage img = new BufferedImage(mapSizeW, mapSizeH, BufferedImage.TYPE_4BYTE_ABGR);
		for (int gz = 0; gz < mapSizeH; ++gz) {
			for (int gx = 0; gx < mapSizeW; ++gx) {
				int hZ1 = getGrayScale(gx, gz - window, hmImg);
				int hZ2 = getGrayScale(gx, gz + window, hmImg);
				int hX1 = getGrayScale(gx - window, gz, hmImg);
				int hX2 = getGrayScale(gx + window, gz, hmImg);

				int modifier = 4;

				int dZ = Math.abs(hZ1 - hZ2);
				int dX = Math.abs(hX1 - hX2);

				int channel = 255 - (Maths.clampI(modifier * (dZ + dX) / 2, 0, 255) & 0xFF);

				int colorARGB = 0xFF0000FF | (channel << 16) | (channel << 8);
				img.setRGB(gx, gz, colorARGB);
			}
		}
		return img;
	}

	public static BufferedImage generateIsolineMap(BufferedImage hmImg) {
		int mapSizeW = hmImg.getWidth() - 1;
		int mapSizeH = hmImg.getHeight() - 1;

		BufferedImage img = new BufferedImage(mapSizeW, mapSizeH, BufferedImage.TYPE_4BYTE_ABGR);

		for (int gz = 0; gz < mapSizeH; ++gz) {
			for (int gx = 0; gx < mapSizeW; ++gx) {
				int hZ1 = getGrayScale(gx, gz - window, hmImg);
				int hZ2 = getGrayScale(gx, gz + window, hmImg);
				int hX1 = getGrayScale(gx - window, gz, hmImg);
				int hX2 = getGrayScale(gx + window, gz, hmImg);

				float averageHeight = (hZ1 + hZ2 + hX1 + hX2) / (4f * 255f);
				averageHeight = Maths.clampF(averageHeight, 0f, 1f);

				// od 0 do 1, robimy schodki - rzutowaniem na inta
				averageHeight *= (LEVELS_ISOLINE - 1);
				int colorARGB = COLORS_ISOLINE[(int) averageHeight];
				img.setRGB(gx, gz, colorARGB);
			}
		}
		return img;
	}

	public static BufferedImage generateNormalMap(BufferedImage hmImg) {
		int mapSizeW = hmImg.getWidth() - 1;
		int mapSizeH = hmImg.getHeight() - 1;

		BufferedImage img = new BufferedImage(mapSizeW, mapSizeH, BufferedImage.TYPE_4BYTE_ABGR);

		for (int gz = 0; gz < mapSizeH; ++gz) {
			for (int gx = 0; gx < mapSizeW; ++gx) {

				int hZ1 = getGrayScale(gx, gz - window, hmImg);
				int hZ2 = getGrayScale(gx, gz + window, hmImg);
				int hX1 = getGrayScale(gx - window, gz, hmImg);
				int hX2 = getGrayScale(gx + window, gz, hmImg);
				
				float strength = 100f;

				float dHX = (hX2 - hX1);
				dHX = Maths.clampF(dHX, -255f, 255f);
				float dHZ = (hZ2 - hZ1);
				dHZ = Maths.clampF(dHZ, -255f, 255f);

				gradientTempA.set(dHX, 255f/strength, dHZ);
				gradientTempA.normalise();

//				gradientTempA.set(510f, dHX, 0f);
//				gradientTempA.normalise();
//				gradientTempB.set(0f, dHZ, 510f);
//				gradientTempB.normalise();
//				Vector3f.cross(gradientTempB, gradientTempA, gradientTempA);
//
				// wektor ma skladowe od -1f do 1f
				int r = (int) ((gradientTempA.x + 1f) * 127.5f) & 0xFF;
				int b = (int) ((gradientTempA.y + 1f) * 127.5f) & 0xFF;
				int g = (int) ((gradientTempA.z + 1f) * 127.5f) & 0xFF;

				int colorARGB = 0xFF000000 | (r << 16) | (g << 8) | (b << 0);
				img.setRGB(gx, gz, colorARGB);
			}
		}
		return img;
	}

	public static int getColorChannel(int x, int z, BufferedImage image, int mask) {
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

	public static int getGrayScale(int x, int z, BufferedImage image) {
		if (x < 0)
			x = 0;
		if (x >= image.getWidth())
			x = image.getWidth() - 1;
		if (z < 0)
			z = 0;
		if (z >= image.getHeight())
			z = image.getHeight() - 1;

		int colorB = (image.getRGB(x, z) & 0xFF);
		int colorG = (image.getRGB(x, z) & 0xFF00) >> 8;
		int colorR = (image.getRGB(x, z) & 0xFF0000) >> 16;

		int color = (colorB + colorG + colorR) / 3;
		if (color > 255)
			return 255;
		return color;
	}
}
