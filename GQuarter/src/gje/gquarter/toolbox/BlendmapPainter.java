package gje.gquarter.toolbox;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

public class BlendmapPainter {
	public static final int BYTE_PER_PIXEL = 4;
	public static final int BLENDMAP_SIZE = 1024;
	private int diameter;
	private ByteBuffer brushBuffer;
	private static Vector4f sampledColor = new Vector4f();
	private static ByteBuffer blendmapBuffer = BufferUtils.createByteBuffer(BLENDMAP_SIZE * BLENDMAP_SIZE * BYTE_PER_PIXEL);
	private static byte[] blendmapBytes = new byte[BLENDMAP_SIZE * BLENDMAP_SIZE * BYTE_PER_PIXEL];
	private int r, g, b, a;
	private static int lastX = 0, lastZ = 0;
	private float hardness;

	public BlendmapPainter(int diameter, int r, int g, int b, int a) {
		setMainColor(r, g, b, a);
		hardness = 1f;
		buildSquareBrush(diameter);
	}

	public void buildSquareBrush(int diameter) {
		this.diameter = diameter;
		if (this.diameter < 1)
			this.diameter = 1;
		if(brushBuffer != null)
			brushBuffer.clear();
		brushBuffer = BufferUtils.createByteBuffer(diameter * diameter * BYTE_PER_PIXEL);
		for (int iz = 0; iz < diameter; ++iz) {
			for (int ix = 0; ix < diameter; ++ix) {
				brushBuffer.put((byte) r);
				brushBuffer.put((byte) g);
				brushBuffer.put((byte) b);
				brushBuffer.put((byte) a);
			}
		}
		brushBuffer.flip();
	}

	public void setHardnes(float hardness) {
		this.hardness = hardness;
	}

	public void buildSoftBrushWithPreviousRadius(int tx, int tz) {
		brushBuffer.clear();
		for (int iz = 0; iz < diameter; ++iz) {
			for (int ix = 0; ix < diameter; ++ix) {
				// sampluje to co pod pedzlem
				int radSquared = (iz - diameter / 2) * (iz - diameter / 2) + (ix - diameter / 2) * (ix - diameter / 2);
				int smapleX = Maths.clampI(tx + ix - diameter / 2, 0, BLENDMAP_SIZE);
				int smapleZ = Maths.clampI(tz + iz - diameter / 2, 0, BLENDMAP_SIZE);
				sampleBlendMapPixel(smapleX, smapleZ, sampledColor);

				if (radSquared <= (diameter * diameter / 4)) {
					// rad [0,1]
					float rad = radSquared / (diameter * diameter / 4f);
					rad = (float) Math.pow(rad, hardness);

					// r,g,b,a - natezenia kolorow. Jak 0 to nie wybrany kolor
					float rr = 0;
					float gg = 0;
					float bb = 0;
					if(r > 0){
						rr = Maths.linearFunctionValue(0, sampledColor.x * 255f, 255, 255, r);
						gg = Maths.linearFunctionValue(0, sampledColor.y * 255f, 255, 0, r);
						bb = Maths.linearFunctionValue(0, sampledColor.z * 255f, 255, 0, r);
					}
					else if(g > 0){
						rr = Maths.linearFunctionValue(0, sampledColor.x * 255f, 255, 0, g);
						gg = Maths.linearFunctionValue(0, sampledColor.y * 255f, 255, 255, g);
						bb = Maths.linearFunctionValue(0, sampledColor.z * 255f, 255, 0, g);
					}
					else if(b > 0){
						rr = Maths.linearFunctionValue(0, sampledColor.x * 255f, 255, 0, b);
						gg = Maths.linearFunctionValue(0, sampledColor.y * 255f, 255, 0, b);
						bb = Maths.linearFunctionValue(0, sampledColor.z * 255f, 255, 255, b);
					}
					else if(a > 0){
						rr = Maths.linearFunctionValue(0, sampledColor.x * 255f, 255, 0, a);
						gg = Maths.linearFunctionValue(0, sampledColor.y * 255f, 255, 0, a);
						bb = Maths.linearFunctionValue(0, sampledColor.z * 255f, 255, 0, a);
					}
					//funckcja twardosci pedzla
					brushBuffer.put((byte) Maths.linearFunctionValue(0, rr, 1, sampledColor.x * 255, rad));
					brushBuffer.put((byte) Maths.linearFunctionValue(0, gg, 1, sampledColor.y * 255, rad));
					brushBuffer.put((byte) Maths.linearFunctionValue(0, bb, 1, sampledColor.z * 255, rad));
					brushBuffer.put((byte) 255);
				} else {
					brushBuffer.put((byte) (sampledColor.x * 255));
					brushBuffer.put((byte) (sampledColor.y * 255));
					brushBuffer.put((byte) (sampledColor.z * 255));
					brushBuffer.put((byte) 255);
				}
			}
		}
		brushBuffer.flip();
	}

	public void applyBlendMapPreBuiltBrush(int id, int tX, int tZ) {
		// za kazdym razem zmienia sie kolor blendmapy wiec update
		// updateBlendmapBuffers(id);
		// odczytaj kolor w miejscu samplowania
		// sampleBlendMapPixel(tX, tZ, sampledColor);
		if ((lastX != tX) && (lastZ != tZ)) {
			updateBlendmapBuffers(id);
			buildSoftBrushWithPreviousRadius(tX, tZ);
			changePatchColor(id, tX, tZ, diameter, brushBuffer);
			lastX = tX;
			lastZ = tZ;
		}
	}

	public static void updateBlendmapBuffers(int blendmapTextureId) {
		// tu zapisuje do RAM pixele z tekstury i zamieniam je na bajty
		blendmapBuffer.clear();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, blendmapTextureId);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, blendmapBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		blendmapBuffer.get(blendmapBytes);

		// odwracamy kolejnosc z RGBA na ABGR :O
		for (int i = 0; i < blendmapBytes.length; i += 4) {
			byte a = blendmapBytes[i + 3], r = blendmapBytes[i + 0], g = blendmapBytes[i + 1], b = blendmapBytes[i + 2];
			blendmapBytes[i] = a;
			blendmapBytes[i + 1] = b;
			blendmapBytes[i + 2] = g;
			blendmapBytes[i + 3] = r;
		}
		blendmapBuffer.flip();
	}
	
	public static void changePatchColor(int id, int tX, int tY, int patchSize, ByteBuffer pixels) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, tX - patchSize / 2, tY - patchSize / 2, patchSize, patchSize, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void setMainColor(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		buildSquareBrush(diameter);
	}

	public int getDiameter() {
		return diameter;
	}

	public ByteBuffer getPixels() {
		return brushBuffer;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public int getA() {
		return a;
	}

	public static void changeTexelColor(int id, int tX, int tY, int r, int g, int b, int a) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		ByteBuffer pixels = BufferUtils.createByteBuffer(BYTE_PER_PIXEL);
		pixels.put((byte) r);
		pixels.put((byte) g);
		pixels.put((byte) b);
		pixels.put((byte) a);
		pixels.flip();
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, tX, tY, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixels);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void savePreupdatedBlendampBufferToFile(int textureId, String path) {
		BufferedImage img = new BufferedImage(BLENDMAP_SIZE, BLENDMAP_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(blendmapBytes, blendmapBytes.length), new Point()));
		savePNGImage(path, img);
	}

	public static void savePNGImage(String filename, BufferedImage img) {
		File file = new File(filename + ".png");
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sampleBlendMapPixel(int textureX, int textureZ, Vector4f sampledColor) {
		// po wykonaniu update mam w buforzy byteow zapisane wartosci pixeli!
		// Trzeba tylko odczytac kordy :D

		// index poczatkowy - uwaga ABGR!
		textureX = Maths.clampI(textureX, 0, BLENDMAP_SIZE);
		textureZ = Maths.clampI(textureZ, 0, BLENDMAP_SIZE);
		int index = BYTE_PER_PIXEL * (textureZ * BLENDMAP_SIZE + textureX);
		int a = (int) (blendmapBytes[index++] & 0xFF);
		int b = (int) (blendmapBytes[index++] & 0xFF);
		int g = (int) (blendmapBytes[index++] & 0xFF);
		int r = (int) (blendmapBytes[index++] & 0xFF);
		sampledColor.x = r / 255f;
		sampledColor.y = g / 255f;
		sampledColor.z = b / 255f;
		sampledColor.w = a / 255f;
		// System.out.println("XZ=[" + textureX + "," +textureZ + "] a:" + a +
		// "b:" + b + "g:" + g + "r:" + r);
	}

	public static void screenShot() {
		GL11.glReadBuffer(GL11.GL_FRONT);
		ByteBuffer buffer = BufferUtils.createByteBuffer(Display.getWidth() * Display.getHeight() * BYTE_PER_PIXEL);
		GL11.glReadPixels(0, 0, Display.getWidth(), Display.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		// ZAPIS DO PLIKU, heh nie ma przezroczystosci :p
		Calendar date = Calendar.getInstance();
		int year = date.get(Calendar.YEAR);
		int month = date.get(Calendar.MONTH) + 1;
		int day = date.get(Calendar.DAY_OF_MONTH);
		int hour = date.get(Calendar.HOUR_OF_DAY);
		int min = date.get(Calendar.MINUTE);
		int sec = date.get(Calendar.SECOND);

		File file = new File("screenshots/ss" + "_" + year + "_" + month + "_" + day + "_" + hour + "_" + min + "_" + sec + ".png");
		ToolBox.log(BlendmapPainter.class, "SS: " + file.getAbsolutePath());
		BufferedImage img = new BufferedImage(Display.getWidth(), Display.getHeight(), BufferedImage.TYPE_INT_RGB);
		int iter = 0;
		for (int iy = 0; iy < Display.getHeight(); ++iy) {
			for (int ix = 0; ix < Display.getWidth(); ++ix) {
				iter = (ix + Display.getWidth() * iy) * BYTE_PER_PIXEL;
				int r = buffer.get(iter) & 0xFF;
				int g = buffer.get(iter + 1) & 0xFF;
				int b = buffer.get(iter + 2) & 0xFF;
				img.setRGB(ix, Display.getHeight() - (iy + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		try {
			ImageIO.write(img, "PNG", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
