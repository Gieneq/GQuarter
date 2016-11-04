package gje.gquarter.gui.fonts;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import gje.gquarter.core.ShaderProgram;


public class FontShader extends ShaderProgram {

	private static final String VERTEX_FILE = "res/shaders/fontVertex.vsh";
	private static final String FRAGMENT_FILE = "res/shaders/fontFragment.vsh";

	private int location_color;
	private int location_translation;
	
	public FontShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_color = super.getUniformLocation("color");
		location_translation = super.getUniformLocation("translation");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}

	protected void loadColor(Vector3f color){
		loadVector3f(location_color, color);
	}

	protected void loadTranslation(Vector2f translation){
		loadVector2f(location_translation, translation);
	}
}
