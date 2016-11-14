package gje.gquarter.water;

import gje.gquarter.toolbox.Maths;

import org.lwjgl.util.vector.Vector3f;

public class WaterTile {
	private float tileSize;
	private float tiling;
	private Vector3f centerPosition;

	public WaterTile(float centerX, float height, float centerZ, float tileSize) {
		this.centerPosition = new Vector3f(centerX, height, centerZ);
		this.tileSize = tileSize;
		this.tiling = tileSize * 0.5f;
	}

	public float getHeight() {
		return centerPosition.y;
	}

	public float getX() {
		return centerPosition.x;
	}

	public float getZ() {
		return centerPosition.z;
	}

	public float getTileSize() {
		return tileSize;
	}

	public float getTiling() {
		return tiling;
	}

	public Vector3f getCenterPosition() {
		return centerPosition;
	}

	public float getSweepSphereRadius() {
		return tileSize * Maths.SQRT2;
	}
}