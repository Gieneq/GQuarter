package gje.gquarter.toolbox;

public class Rotation3f {
	public float rx, ry, rz;

	public Rotation3f() {
	}

	public Rotation3f(float rx, float ry, float rz) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}

	public Rotation3f(Rotation3f rot) {
		this.rx = rot.rx;
		this.ry = rot.ry;
		this.rz = rot.rz;
	}

	public Rotation3f(double rx, double ry, double rz) {
		this.rx = (float) rx;
		this.ry = (float) ry;
		this.rz = (float) rz;
	}

	public void set(Rotation3f rot) {
		this.setRotation(rot.rx, rot.ry, rot.rz);
	}

	public void setRotation(float rx, float ry, float rz) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}

	public void addRotation(Rotation3f rot) {
		this.addRotation(rot.rx, rot.ry, rot.rz);
	}

	public void addRotation(float rx, float ry, float rz) {
		this.rx += rx;
		this.ry += ry;
		this.rz += rz;
	}

	public void setRotationDeg(float rxDeg, float ryDeg, float rzDeg) {
		this.rx = (float) Math.toRadians(rxDeg);
		this.ry = (float) Math.toRadians(ryDeg);
		this.rz = (float) Math.toRadians(rzDeg);
	}

	public void addRotationDeg(float rxDeg, float ryDeg, float rzDeg) {
		this.rx += (float) Math.toRadians(rxDeg);
		this.ry += (float) Math.toRadians(ryDeg);
		this.rz += (float) Math.toRadians(rzDeg);
	}

	@Override
	public String toString() {
		return ("[" + (int) Math.toDegrees(rx) + ";" + (int) Math.toDegrees(ry) + ";" + (int) Math.toDegrees(rz) + "]");
	}

	public float lengthSquared() {
		return rx * rx + ry * ry + rz * rz;
	}

	public float length() {
		return (float) Math.sqrt(length());
	}

	public static void sub(Rotation3f left, Rotation3f right, Rotation3f dst) {
		dst.rx = left.rx - right.rx;
		dst.ry = left.ry - right.ry;
		dst.rz = left.rz - right.rz;
	}
}
