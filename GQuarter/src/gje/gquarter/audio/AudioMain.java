package gje.gquarter.audio;

import gje.gquarter.entity.EntityX;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.lwjgl.util.vector.Vector3f;

public class AudioMain {
	private static List<Integer> buffersList = new ArrayList<Integer>();
	private static List<Source> sourcesList = new ArrayList<Source>();
	private static Source bgSourcePlayer;
	private static Vector3f listenerPosition = new Vector3f();
	private static boolean enable;

	public static void init() {
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		AudioLibrary.init();
		
		AudioMain.setListner(0f, 0f, 0f);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		bgSourcePlayer = new Source();
		bgSourcePlayer.play(AudioLibrary.getSoundBufferId(AudioLibrary.BG_MUSIC));
		bgSourcePlayer.setVolume(0.6f);
		bgSourcePlayer.setLooping(true);
		audioDisable();
	}

	public static void audioEnable() {
		for (Source s : sourcesList)
			s.resume();
		bgSourcePlayer.resume();
		enable = true;
	}

	public static void audioDisable() {
		for (Source s : sourcesList)
			s.pause();
		bgSourcePlayer.pause();
		enable = false;
	}
	
	public static boolean isAudioEnabled(){
		return enable;
	}

	public static void setListner(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
		listenerPosition.set(x, y, z);
		AL10.alListener3f(AL10.AL_VELOCITY, 0, 0, 0);
	}

	public static void setListner(Vector3f pos) {
		setListner(pos.x, pos.y, pos.z);
	}

	public static void setListner(EntityX ent) {
		setListner(ent.getPhysicalComponentIfHaving().getPosition());
	}

	public static int loadSound(String path) {
		int bufferId = AL10.alGenBuffers();
		buffersList.add(bufferId);
		WaveData waveFile;
		try {
			waveFile = WaveData.create(new BufferedInputStream(new FileInputStream("res/audio/" + path + ".wav")));
			AL10.alBufferData(bufferId, waveFile.format, waveFile.data, waveFile.samplerate);
			waveFile.dispose();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return bufferId;
	}

	public static void update() {
//		System.out.println(listenerPosition);
		organiseSourcesNotOften(); //TODO
	}

	public static void organiseSourcesNotOften() {
		if (enable) {
			for (Source s : sourcesList) {
				if (s.getRollof() > 0f) {
					float dx = listenerPosition.x - s.getPosition().x;
					float dy = listenerPosition.y - s.getPosition().y;
					float dz = listenerPosition.z - s.getPosition().z;
					float distSquared = dx * dx + dy * dy + dz * dz;
					if (distSquared > s.getMaxDistance() * s.getMaxDistance()) {
						if (s.isPlaying())
							s.pause();
					} else {
						if (!s.isPlaying())
							s.resume();
					}
				}
			}
		}
	}

	public static void addSource(Source s) {
		sourcesList.add(s);
	}
	
	public static void removeSource(Source s) {
		sourcesList.remove(s);
	}

	public static void clean() {
		for (Integer i : buffersList)
			AL10.alDeleteBuffers(i);
		AL.destroy();
	}

	public static Source getBgSourcePlayer() {
		return bgSourcePlayer;
	}
}
