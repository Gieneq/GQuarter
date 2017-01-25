package gje.gquarter.entity;

import org.lwjgl.util.vector.Vector3f;

public class Field {
	public static final int TYPE_PLUS = 1;
	public static final int TYPE_MINUS = -1;
	public static final float CUTOUT_LEN = 1.6f;
	//done
//usunac 
	private float factor;
	private Vector3f position;
	private int type;

	/** potential + przyciaga, - odpycha */
	public Field(float factor, Vector3f position, int type) {
		this.factor = factor;
		this.position = position;
		this.type = type;
	}

	public void calculateForce(float rRob, float rObst, Vector3f point, Vector3f dst) {
		Vector3f.sub(position, point, dst);
		
		if (type == TYPE_PLUS) {
			dst.scale(factor);
			return;
		}
		
		float length = dst.length() - rRob - rObst;
		if (length < 0f)
			length = 0.2f;
		dst.normalise();

		if (type == TYPE_MINUS) {
			if (length > CUTOUT_LEN) {
				dst.scale(0f);
				return;
			} else {
				float a = (1f / length) - (1f / CUTOUT_LEN);
				float pot = -0.5f * factor * a * a;
				dst.scale(pot);
				return;
			}
		}
	}

	public float getFactor() {
		return factor;
	}

	public Vector3f getPosition() {
		return position;
	}

}
