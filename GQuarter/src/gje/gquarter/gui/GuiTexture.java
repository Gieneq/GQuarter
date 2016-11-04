package gje.gquarter.gui;

import gje.gquarter.toolbox.Rect2i;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class GuiTexture extends Rect2i {
	public static final int TYPE_FILLING_NONE = 0; // 000
	public static final int TYPE_FILLING_TEXTURE = 1; // 001
	public static final int TYPE_FILLING_COLOUR = 2; // 010
	public static final int TYPE_FILLING_CIRCULAR = 4; // 100

	private int type;
	private int texture;
	private Vector4f colour;
	private float mixColorValue;
	private float radiusMin, radiusMax;
	private float finalAlphaValue;
	private Vector2f position;
	private Vector2f scale;
	private GuiPanel parentPanel;
	private float rotation;

	private Vector2f textureTranslation;
	private float textureZoom;

	/**
	 * Build new <b> raw </b> GUI texture. Then manipulate it with use and
	 * position interface!
	 */
	public GuiTexture(Rect2i rect, GuiPanel parentPanel) {
		super(rect);
		this.position = new Vector2f();
		this.scale = new Vector2f();
		forceUpdatePositionSize();
		this.parentPanel = parentPanel;
		this.textureTranslation = new Vector2f();
		this.textureZoom = 1f;

		this.type = TYPE_FILLING_NONE;
		this.texture = -1;
		GuiTextureRenderer.loadGuiTexture(this);
		colour = new Vector4f(0f, 0f, 0f, 1f);
		finalAlphaValue = 1f;
		mixColorValue = 0f;
		radiusMin = 1f;
		radiusMax = 1f;
		rotation = 0f;
	}

	public void load() {
		GuiTextureRenderer.loadGuiTexture(this);
	}

	public void remove() {
		GuiTextureRenderer.removeGuiTexture(this);
	}
	
	public void setRotation(float rz){
		this.rotation = rz;
	}
	
	public float getRotation(){
		return rotation;
	}

	public void useNothing(int textureId) {
		type = TYPE_FILLING_NONE;
	}

	public void useTexture(int textureId) {
		this.texture = textureId;
		type |= TYPE_FILLING_TEXTURE;
	}

	public void useColour(Vector4f colour, float initialMixValue) {
		this.colour = colour;
		this.mixColorValue = initialMixValue;
		type |= TYPE_FILLING_COLOUR;
	}

	public void notUseColor() {
		type &= ~(TYPE_FILLING_COLOUR);
	}

	public void useRadius(float minRad, float maxRad) {
		this.radiusMin = minRad;
		this.radiusMax = maxRad;
		type |= TYPE_FILLING_CIRCULAR;
	}

	public int getTypeRegister() {
		return type;
	}

	public boolean isUsing(int flag) {
		return ((type & flag) != 0);
	}

	/**
	 * Triggers update based on preset rectangle values. Calculates new scale
	 * and 2D float positions.
	 */
	public void forceUpdatePositionSize() {
		setSizeWithTopLeft(w, h);
	}

	/**
	 * Move texture, it doesn't depend on type of coorinate: topleft or
	 * centered.
	 * 
	 * @param dx
	 *            - delta x,
	 * @param dy
	 *            - delta y.
	 */
	public void movePx(int dx, int dy) {
		this.move(dx, dy);
		setTopLeftPx(x, y);
	}

	/**
	 * Set position in pixels of center of texture with preset scale. Value in
	 * normalised gl space is then calculated and stored based on new position
	 * and previosly set size.
	 * 
	 * @param newX
	 *            - x coordinate of center of texture,
	 * @param newY
	 *            - y coordinate of center of texture.
	 */
	public void setCenterPx(int newCenterX, int newCenterY) {
		this.x = newCenterX - w / 2;
		this.y = newCenterY - h / 2;

		// tu cos trzeba by dodac/odjac zeby zlapac naroznik
		float xx = (getGlobalX() + w) * 1f;
		float yy = (getGlobalY() + w) * 1f;

		xx = (2 * xx) / Display.getWidth() - 1f;
		yy = (2 * yy) / Display.getHeight() - 1f;
		this.position.set(xx, -yy);
	}

	/**
	 * Set position in pixels of top left corner of texture with preset scale.
	 * Value in normalised gl space is then calculated and stored based on new
	 * position and previosly set size.
	 * 
	 * @param newX
	 *            - x coordinate of top left corner,
	 * @param newY
	 *            - y coordinate of top left corner.
	 */
	public void setTopLeftPx(int newX, int newY) {
		this.x = newX;
		this.y = newY;

		float xx = (getGlobalX()) * 1f;
		float yy = (getGlobalY()) * 1f;

		xx += w / 2.0f;
		yy += h / 2.0f;

		xx = (2 * xx) / Display.getWidth() - 1f;
		yy = (2 * yy) / Display.getHeight() - 1f;
		this.position.set(xx, -yy);
	}

	/**
	 * Set new scale based on new width and height which are stored as rectangle
	 * sizes. Then position of top left corner is recalcullated - position in
	 * pixels is not changed but center in gl coordinate system is moved!
	 * 
	 * @param width
	 *            - width of texture,
	 * @param height
	 *            - height of texture.
	 */
	public void setSizeWithTopLeft(int width, int height) {
		this.w = width;
		this.h = height;
		// trzeba zmienic srodek na nieco inny :/
		this.scale = new Vector2f(w * 1f / Display.getWidth(), h * 1f / Display.getHeight());
		setTopLeftPx(x, y);
	}

	/**
	 * Set new scale based on new width and height. Position in gl coordinate
	 * system is not changed, but information about rectangle (especially
	 * position) is changed - half of deltaW/deltaH.
	 * 
	 * @param width
	 *            - width of texture,
	 * @param height
	 *            - height of texture. <br/>
	 *            <u>Pretty good with pulsing effect of buttons!!!!</u>
	 */
	public void setSizeWithCenter(int width, int height) {
		int dw = (w - width) / 2;
		int dh = (h - height) / 2;
		this.w = width;
		this.h = height;
		// punkt srodka sie nie zmienia, ale rect.xy sie zmini :/
		this.scale = new Vector2f(w * 1f / Display.getWidth(), h * 1f / Display.getHeight());
		// przejscie odwrotne, z gl coords przejsc do top left :/
		// albo oszukam i dam polowe zmiany w/h :D
		this.x += dw;
		this.y += dh;
	}

	/*
	 * AKCESORY
	 */

	public int getTextureId() {
		return texture;
	}

	/** Changing values forbidden! */
	public Vector2f getPosition() {
		return position;
	}

	/** Changing values forbidden! */
	public Vector2f getScale() {
		return scale;
	}

	public GuiPanel getParentPanel() {
		return parentPanel;
	}

	public float getMixColorValue() {
		return mixColorValue;
	}

	public void setMixColorValue(float mixColorValue) {
		this.mixColorValue = mixColorValue;
	}

	public float getRadiusMin() {
		return radiusMin;
	}

	public float getRadiusMax() {
		return radiusMax;
	}

	public float getFinalAlphaValue() {
		return finalAlphaValue;
	}

	public Vector4f getColour() {
		return colour;
	}

	public float getTextureZoom() {
		return this.textureZoom;
	}

	public void setTextureZoom(float textureZoom) {
		this.textureZoom = textureZoom;
	}

	public Vector2f getTextureTranslation() {
		return textureTranslation;
	}
}
