package gje.gquarter.events;

/**
 * Release after bigger Cameras changes like translation, rotation, zoom. </br>
 * In genral: frustum changes listener.
 */
public interface OnCameraUpdateListener {
	/** Update to be done. */
	public abstract void onCameraUpdate(float dt);
}
