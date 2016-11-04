package gje.gquarter.entity;

import gje.gquarter.core.MainRenderer;

import org.lwjgl.util.vector.Vector3f;

public class Light {

	private static final Vector3f BLACK_COLOR = new Vector3f(0, 0, 0);
	public static final int POINT_LIGHT = 0;
	public static final int DIRECTIONAL_LIGHT = 1;
	private Vector3f position;
	private Vector3f colour;
	private Vector3f attenuation;
	private boolean visible = true;
	private int type = POINT_LIGHT;

	public Light(Vector3f position, Vector3f colour) {
		attenuation = new Vector3f(1, 0, 0); // infinite range
		this.setPosition(position);
		this.setColour(colour);
		MainRenderer.loadLightSource(this);
	}

	public Light(Vector3f position, Vector3f colour, Vector3f attenuation) {
		this.attenuation = attenuation;
		this.setPosition(position);
		this.setColour(colour);
		MainRenderer.loadLightSource(this);
	}

	public int getType() {
		return type;
	}

	public void setTypePoint() {
		this.type = POINT_LIGHT;
	}

	public void setTypeDirectional(Vector3f normalVector) {
		this.type = DIRECTIONAL_LIGHT;
		setPosition(normalVector);
		position.normalise(); // na wszelki wypadek
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getColour() {
		if (isVisible())
			return colour;
		return BLACK_COLOR;
	}

	public void setColour(Vector3f colour) {
		this.colour = colour;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void toggleVisible() {
		if (isVisible())
			setVisible(false);
		else
			setVisible(true);
	}

}
