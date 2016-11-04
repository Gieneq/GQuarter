package gje.gquarter.toolbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageManager {

	public static final Random randomiser = new Random();
	public static final int MODE_NORMAL = 0;
	public static final int MODE_RAND = 1;
	public static final int MODE_BRIGHTNES = 2;
	public static final int MODE_DIVIDED = 3;
	
	/*
	 * najwazniejsza metoda: load
	 * potem zapis: save :)
	 */

	/**nie pisac .png*/
	public static BufferedImage loadPNGImage(String filename) {
		File file = null;
		file = new File(filename + ".png");
		try {
			return ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void savePNGImage(String filename, BufferedImage img) {
		File file = new File(filename + ".png");
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void convertToGrayScale(BufferedImage img) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int argb = img.getRGB(x, y);
				img.setRGB(x, y, getGrayScale(argb));
			}
		}
	}
	
	/** color 0xFFCC00DD jest to 100% opacity rozowy :D*/
	public static void fillImageARGB(BufferedImage img, int color) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				img.setRGB(x, y, color);
			}
		}
	}

	public static void convertToSepia(BufferedImage img) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int argb = img.getRGB(x, y);
				img.setRGB(x, y, getSepia(argb));
			}
		}
	}

	public static void convertToNegative(BufferedImage img) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int argb = img.getRGB(x, y);
				img.setRGB(x, y, getNegative(argb));
			}
		}
	}
	
	public static void convertWithEffect(BufferedImage img, int mode) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int argb = img.getRGB(x, y);
				argb = applyEffect(argb, mode);
				img.setRGB(x, y, argb);
			}
		}
	}
	
	public static void convertWithSplit(BufferedImage img, int start, int stop) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int color = img.getRGB(x, y);
				
				int tempColor = getGrayScale(color);
				if ((tempColor & 0xFF) > stop)
					color = getSepia(color);
				else if ((tempColor & 0xFF) < start)
					color = tempColor;
				else {
					float value = (((tempColor & 0xFF) - start*1f)/(stop-start)*1f);
					color = mix(0xFF010101 & 0xFFDDDDDD, getSepia(color), value);
				}
				img.setRGB(x, y, color);
			}
		}
	}
	
	public static void convert2Bit(BufferedImage img, int edge, int maxColor, int minColor) {
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				int color = img.getRGB(x, y);
				
				int tempColor = getGrayScale(color);
				if ((tempColor & 0xFF) > edge)
					color = maxColor;
				else
					color = minColor;

				img.setRGB(x, y, color);
			}
		}
	}

	public static int getGrayScale(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = (argb >> 0) & 0xFF;
		int gray = (r + g + b) / 3;
		return ((a << 24) + (gray << 16) + (gray << 8) + (gray << 0));
	}

	public static int getSepia(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = (argb >> 0) & 0xFF;
		int outR = Math.min((int) (r * 0.393f + g * 0.769f + b * 0.189), 0xFF);
		int outG = Math.min((int) (r * 0.349f + g * 0.686f + b * 0.168), 0xFF);
		int outB = Math.min((int) (r * 0.272f + g * 0.543f + b * 0.131), 0xFF);
		return ((a << 24) + (outR << 16) + (outG << 8) + (outB << 0));
	}

	public static int getNegative(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = 255 - ((argb >> 16) & 0xFF);
		int g = 255 - ((argb >> 8) & 0xFF);
		int b = 255 - ((argb >> 0) & 0xFF);
		return ((a << 24) + (r << 16) + (g << 8) + (b << 0));
	}

	public static final int mix(int colorA, int colorB, float brightness) {
		// kapeluszoida
		// brightness = (float) Math.exp(-( Math.pow((brightness*1.6f-2f),2) +
		// 4*(brightness*1.6f-2f) + 4 ));
		int brightA = Math.min((int) (((colorA >> 24) & 0xFF) * (1 - brightness) + ((colorB >> 24) & 0xFF) * brightness), 0xFF);
		int brightR = Math.min((int) (((colorA >> 16) & 0xFF) * (1 - brightness) + ((colorB >> 16) & 0xFF) * brightness), 0xFF);
		int brightG = Math.min((int) (((colorA >> 8) & 0xFF) * (1 - brightness) + ((colorB >> 8) & 0xFF) * brightness), 0xFF);
		int brightB = Math.min((int) (((colorA >> 0) & 0xFF) * (1 - brightness) + ((colorB >> 0) & 0xFF) * brightness), 0xFF);
		return (brightA << 24) + (brightR << 16) + (brightG << 8) + (brightB << 0);
	}

	public static void convertToBlur(BufferedImage img, int blurSize, boolean coarsBlur, int mode) {
		if (blurSize < 1)
			blurSize = 1;
		blurSize = blurSize * 2 + 1; // z 1 robi sie 3, z 2 5 itd

		for (int y = blurSize / 2; y < img.getHeight() - (blurSize + 1) / 2; y += (coarsBlur ? blurSize : 1)) {
			for (int x = blurSize / 2; x < img.getWidth() - (blurSize + 1) / 2; x += (coarsBlur ? blurSize : 1)) {

				int[] argb = new int[blurSize * blurSize];
				int iii = 0;
				for (int yB = -blurSize / 2; yB < (blurSize + 1) / 2; ++yB) {
					for (int xB = -blurSize / 2; xB < (blurSize + 1) / 2; ++xB) {
						argb[iii++] = img.getRGB(x + xB, y + yB);
					}
				}

				int a = 0, r = 0, g = 0, b = 0;

				// wartosc srednia
				for (int i = 0; i < blurSize * blurSize; ++i) {
					a += (argb[i] >> 24) & 0xFF;
					r += (argb[i] >> 16) & 0xFF;
					g += (argb[i] >> 8) & 0xFF;
					b += (argb[i] >> 0) & 0xFF;
				}
				a = Math.min(a / (blurSize * blurSize), 0xFF);
				r = Math.min(r / (blurSize * blurSize), 0xFF);
				g = Math.min(g / (blurSize * blurSize), 0xFF);
				b = Math.min(b / (blurSize * blurSize), 0xFF);
				int color = ((a << 24) + (r << 16) + (g << 8) + (b << 0));

				color = applyEffect(color, mode);

				if (!coarsBlur)
					img.setRGB(x, y, color);
				else {
					// na krancu pojawia sie niedociagniecie :/
					int dx = 0, dy = 0;
					if (x + blurSize > img.getWidth()) {
						dx = img.getWidth() - x - (blurSize + 1) / 2;
						System.out.println(dx);
					}
					if (y + blurSize > img.getHeight())
						dy = img.getHeight() - y - (blurSize + 1) / 2;

					for (int yB = -blurSize / 2; yB < (blurSize + 1) / 2 + dy; ++yB) {
						for (int xB = -blurSize / 2; xB < (blurSize + 1) / 2 + dx; ++xB) {
							img.setRGB(x + xB, y + yB, color);
						}
					}

				}
			}
		}
	}
	
	public static int applyEffect(int color, int mode){
		if (mode == MODE_RAND) {
			int nextRand = randomiser.nextInt(2);
			if (nextRand == 0)
				color = getGrayScale(color);
			if (nextRand == 1)
				color = getSepia(color);
			if (nextRand == 2)
				color = getNegative(color);
		} else if (mode == MODE_BRIGHTNES) {
			int gray = getGrayScale(color);
			int sepia = getSepia(color);
			float brightness = (gray & 0xFF) / 255f;
			color = mix(gray, sepia, brightness);
		} else if (mode == MODE_DIVIDED) {
			int tempColor = getGrayScale(color);
			final int start = 110, stop = 160; 
			if ((tempColor & 0xFF) > stop)
				color = getSepia(color);
			else if ((tempColor & 0xFF) < start)
				color = tempColor;
			else {
				float value = (((tempColor & 0xFF) - start*1f)/(stop-start)*1f);
				color = mix(tempColor, getSepia(color), value);
			}
		}
		return color;
	}
}
