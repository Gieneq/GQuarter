package gje.gquarter.audio;

import org.lwjgl.openal.AL10;
import org.lwjgl.util.vector.Vector3f;

public class Source {
	private int sourceId;
	private float minDistance;
	private float maxDistance;
	private float rollof;
	private Vector3f position;

	public Source(Vector3f pos) {
		sourceId = AL10.alGenSources();
		setVolume(1f);
		setPitch(1f);
		position = new Vector3f();
		setPosition(pos);
		setAttenuationParams(0f, 1f, 16f);
		AudioMain.addSource(this);
	}

	public Source() {
		this(new Vector3f());
	}

	public void setAttenuationParams(float rollof, float referenceValue, float maxDistance) {
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rollof);
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, referenceValue);
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, maxDistance);
		this.minDistance = referenceValue;
		this.maxDistance = maxDistance;
		this.rollof = rollof;
	}

	public void setRollof(float rollof) {
		AL10.alSourcef(sourceId, AL10.AL_ROLLOFF_FACTOR, rollof);
		this.rollof = rollof;
	}

	public void setReferenceValue(float min) {
		AL10.alSourcef(sourceId, AL10.AL_REFERENCE_DISTANCE, min);
		this.minDistance = min;
	}

	public void setMaxValue(float max) {
		AL10.alSourcef(sourceId, AL10.AL_MAX_DISTANCE, max);
		this.maxDistance = max;
	}

	public void setLooping(boolean looping) {
		AL10.alSourcei(sourceId, AL10.AL_LOOPING, looping == true ? AL10.AL_TRUE : AL10.AL_FALSE);
	}

	public void setVolume(float volume) {
		AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
	}

	public void setPitch(float pitch) {
		AL10.alSourcef(sourceId, AL10.AL_PITCH, pitch);
	}

	public void setPosition(Vector3f pos) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, pos.x, pos.y, pos.z);
		position.set(pos);
	}

	public void setPosition(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
		position.set(x, y, z);
	}

	public void setVelocity(Vector3f vel) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, vel.x, vel.y, vel.z);
	}

	public void setVelocity(float vx, float vy, float vz) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, vx, vy, vz);
	}

	public void play(int buffer) {
		stop();
		AL10.alSourcei(sourceId, AL10.AL_BUFFER, buffer);
		AL10.alSourcePlay(sourceId);
		resume();
	}

	public boolean isPlaying() {
		return AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING;
	}

	public void pause() {
		AL10.alSourcePause(sourceId);
	}

	public void resume() {
		AL10.alSourcePlay(sourceId);
	}

	public void stop() {
		AL10.alSourceStop(sourceId);
	}

	public void delete() {
		stop();
		AL10.alDeleteSources(sourceId);
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public float getMinDistance() {
		return minDistance;
	}

	public float getRollof() {
		return rollof;
	}

	public Vector3f getPosition() {
		return position;
	}
}
