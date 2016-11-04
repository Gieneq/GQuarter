package gje.gquarter.audio;

import java.util.HashMap;
import java.util.Map;

public class AudioLibrary {
	public static final int BG_MUSIC = 0;
	public static final int WIND_RUSTLE_BIRD_A = 1;
	public static final int WIND_HOWL_FOREST = 2;
	private static Map<Integer, Integer> sounds = new HashMap<Integer, Integer>();
	
	public static void init(){
		sounds.put(BG_MUSIC, AudioMain.loadSound("Vindsvept_Distant"));
		sounds.put(WIND_RUSTLE_BIRD_A, AudioMain.loadSound("rustle_leaves"));
		sounds.put(WIND_HOWL_FOREST, AudioMain.loadSound("forest"));
	}
	
	public static int getSoundBufferId(int musicId) {
		return sounds.get(musicId);
	}
}
