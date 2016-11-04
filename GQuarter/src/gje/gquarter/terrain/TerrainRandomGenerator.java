package gje.gquarter.terrain;

import gje.gquarter.toolbox.ImageManager;
import gje.gquarter.toolbox.ToolBox;

import java.awt.image.BufferedImage;
import java.util.Random;

public class TerrainRandomGenerator {

	private static final int SEED = 123123123; //123123123
	private static final int PARAM_X = 1000; //200003
	private static final int PARAM_Z = 2;   //7919
	private Random randomiser;
	private float amplitude;
	private float frequency;
	private int octavesCount;

	public TerrainRandomGenerator(float amplitude, float frequency, int octavesCount) {
		this.randomiser = new Random(SEED);
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.octavesCount = octavesCount;
	}

	public float getNoiseValue(int x, int z) {
		randomiser.setSeed(x * PARAM_X + z * PARAM_Z + SEED);
		return randomiser.nextFloat() * 2f - 1f;
	}
	
	public static void saveBitmap(String filepath, BufferedImage img){
		ImageManager.savePNGImage(filepath, img);
	}
	
	public static int chanelToGrayARGB(int chanelColor){
		int argbcolor = 0;
		argbcolor += (0xFF000000); //100% opacity
		argbcolor += (chanelColor<<16); // R
		argbcolor += (chanelColor<<8); // G
		argbcolor += (chanelColor<<0); // B
		return argbcolor;
	}
	
	@Deprecated
	public static int byteToGrayscaledARGBBroken(int chanelColor){
		int argbcolor = 0;
		argbcolor += (0xFF000000); //100% opacity
		argbcolor += (chanelColor<<24); // R
		argbcolor += (chanelColor<<24); // G
		argbcolor += (chanelColor<<24); // B
		return argbcolor;
	}
	
	public BufferedImage generateNoiseBitmap(int imageSize){
		BufferedImage img = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);
		
		for (int z = 0; z < img.getHeight(); z++) {
			for (int x = 0; x < img.getWidth(); x++) {
				float value = getNoiseValue(x, z);
				int pixel = (int) (255f*(value+1f)/2f);
				img.setRGB(x, z, chanelToGrayARGB(pixel));
			}
		}
		ToolBox.log(this, "generated!");
		return img;
	}

}
