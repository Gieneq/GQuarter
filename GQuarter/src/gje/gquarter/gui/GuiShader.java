package gje.gquarter.gui;

import gje.gquarter.core.ShaderProgram;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class GuiShader extends ShaderProgram {
	private static final String VERTEX_FILE = "res/shaders/guiVertexShader.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/guiFragmentShader.vsh";

	private int location_transformationMatrix;
	private int location_colour;
	private int location_mixValue;
	private int location_finalAlpha;
	private int location_minRadius;
	private int location_maxRadius;
	private int location_typeFilling;

	private int location_textureTranslation;
	private int location_textureZoom;

	public GuiShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
		start();
		connectTextureUnits();
		stop();
	}

	public void loadTransformation(Matrix4f matrix) {
		super.loadMatrix(location_transformationMatrix, matrix);
	}

	public void loadColour(Vector4f col, float mixValue) {
		super.loadVector4f(location_colour, col);
		super.loadFloat(location_mixValue, mixValue);
	}

	public void loadFinalAlpha(float alpha) {
		super.loadFloat(location_finalAlpha, alpha);
	}

	/** Type from GuiTexture TYPE_ consts */
	public void loadTypeOfFilling(int type) {
		super.loadInt(location_typeFilling, type);
	}

	public void loadRadius(float min, float max) {
		super.loadFloat(location_minRadius, min);
		super.loadFloat(location_maxRadius, max);
	}

	public void loadTextureTranslation(Vector2f translation) {
		super.loadVector2f(location_textureTranslation, translation);
	}

	public void loadTextureZoom(float zoom) {
		super.loadFloat(location_textureZoom, zoom);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_colour = super.getUniformLocation("colour");
		location_typeFilling = super.getUniformLocation("typeFilling");

		location_mixValue = super.getUniformLocation("mixValue");
		location_finalAlpha = super.getUniformLocation("finalAlpha");
		location_minRadius = super.getUniformLocation("minRadius");
		location_maxRadius = super.getUniformLocation("maxRadius");

		location_textureTranslation = super.getUniformLocation("textureTranslation");
		location_textureZoom = super.getUniformLocation("textureZoom");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}
	
	public void connectTextureUnits(){
		super.loadInt(location_textureTranslation, 0);
		super.loadInt(location_textureZoom, 1);
	}
}