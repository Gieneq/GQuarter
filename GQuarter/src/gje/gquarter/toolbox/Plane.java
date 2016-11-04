package gje.gquarter.toolbox;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Plane {
	private Vector4f plane;
	private Vector3f normal;

	public Plane() {
		plane = new Vector4f(0, 1f, 0, 1f);
		normal = new Vector3f(0, 1f, 0);
	}

	public Plane(Vector3f planeNormal, float constantD) {
		plane = new Vector4f();
		setA(planeNormal.x);
		setB(planeNormal.y);
		setC(planeNormal.z);
		setD(constantD);
		normal = new Vector3f(getA(), getB(), getC());
	}

	public Plane(Vector3f planeNormal, Vector3f anyPoint) {
		plane = new Vector4f();
		setA(planeNormal.x);
		setB(planeNormal.y);
		setC(planeNormal.z);
		setD(-(getA() * anyPoint.x + getB() * anyPoint.y + getC() * anyPoint.z));
		normal = new Vector3f(getA(), getB(), getC());
	}

	public void updatePlane(Vector3f planeNormal, Vector3f anyPoint) {
		setA(planeNormal.x);
		setB(planeNormal.y);
		setC(planeNormal.z);
		setD(-(getA() * anyPoint.x + getB() * anyPoint.y + getC() * anyPoint.z));
		normal.set(getA(), getB(), getC());
	}
	
	public void updatePlane(float nx, float ny, float nz, float px, float py, float pz) {
		setA(nx);
		setB(ny);
		setC(nz);
		setD(-(getA() * px + getB() * py + getC() * pz));
		normal.set(getA(), getB(), getC());
	}

	public Vector3f getNormal() {
		return normal;
	}

	public float getConst() {
		return getD();
	}

	public float getDist(Vector3f point) {
		return (getA() * point.x + getB() * point.y + getC() * point.z + getD());
	}

	public boolean isOnRightSide(Vector3f point) {
		return (getDist(point) < 0f);
	}

	public boolean isOnRightSide(Vector3f point, float sphereRadius) {
		return (getDist(point) < sphereRadius);
	}

	public void setA(float a) {
		plane.x = a;
	}

	public void setB(float b) {
		plane.y = b;
	}

	public void setC(float c) {
		plane.z = c;
	}

	public void setD(float d) {
		plane.w = d;
	}

	public float getA() {
		return plane.x;
	}

	public float getB() {
		return plane.y;
	}

	public float getC() {
		return plane.z;
	}

	public float getD() {
		return plane.w;
	}
}
