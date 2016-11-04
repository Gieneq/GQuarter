package gje.gquarter.boundings;

import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.toolbox.Maths;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public abstract class Bounding {
	public static final int TYPE_SPHERE = 0;
	public static final int TYPE_CYL = 1;
	public static final int TYPE_RECT = 2;
	public static final Vector3f ZERO_VEC3F = new Vector3f();
	private static final Vector4f BASIC_COLOR = Maths.convertColor4f(255, 210, 10, 205);

	protected PhysicalComponent parent;
	protected Vector3f translation;

	protected int type;
	protected Matrix4f modelMatrix;
	protected Vector4f color;
	protected boolean select;

	public Bounding(int type, PhysicalComponent parentCmp, Vector3f translation) {
		this.parent = parentCmp;
		this.translation = translation;
		this.type = type;
		this.modelMatrix = new Matrix4f();
		this.modelMatrix.setIdentity();
		this.color = new Vector4f(BASIC_COLOR);
		this.select = false;
	}

	public void setColor(Vector4f color) {
		this.color = color;
	}

	public void setColor(float r, float g, float b, float a) {
		this.color.set(r, g, b, a);
	}

	public Vector4f getColor() {
		return color;
	}

	public abstract void forceMultiMatrixUpdate();

	/** Returns -1 if no collision */
	public abstract float checkCollisionAndGetDistSquared(Vector3f point);

	public abstract boolean checkSweepSphereIntersection(Vector3f point, float radius);

	public boolean checkSweepSphereIntersection(BoundingSphere sphere) {
		return checkSweepSphereIntersection(sphere.getGlobalPosition(), sphere.getRadius());
	}

	public int getType() {
		return type;
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public boolean isSelected() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}
}
