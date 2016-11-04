package gje.gquarter.toolbox;

import org.lwjgl.util.vector.Vector3f;

public class Triangle3Point {
	private Vector3f pA;
	private Vector3f pB;
	private Vector3f pC;

	public Triangle3Point(Vector3f pA, Vector3f pB, Vector3f pC) {
		this.pA = pA;
		this.pB = pB;
		this.pC = pC;
	}

	public Plane getPlane() {
		Vector3f planeNormal = Vector3f.cross(Vector3f.sub(pB, pA, null), Vector3f.sub(pC, pA, null), null);
		float vecLen = planeNormal.length();
		// pA dowolny punkt
		float planeConst = -(Vector3f.dot(pA, planeNormal)) / vecLen;
		planeNormal.normalise();
		return new Plane(planeNormal, planeConst);
	}

	public boolean isPointInside(Vector3f point) {
		Vector3f v0 = Vector3f.sub(pC, pA, null);
		Vector3f v1 = Vector3f.sub(pB, pA, null);
		Vector3f v2 = Vector3f.sub(point, pA, null);

		float dot00 = Vector3f.dot(v0, v0);
		float dot01 = Vector3f.dot(v0, v1);
		float dot02 = Vector3f.dot(v0, v2);
		float dot11 = Vector3f.dot(v1, v1);
		float dot12 = Vector3f.dot(v1, v2);

		// liczymy kordy Barrycentirc
		float invDenom = 1f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return (u > 0) && (v > 0) && (u + v < 1);
	}

	public Vector3f getpA() {
		return pA;
	}

	public Vector3f getpB() {
		return pB;
	}

	public Vector3f getpC() {
		return pC;
	}
	
	@Override
	public String toString() {
		return pA + ", " + pB + ", " + pC;
	}
}
