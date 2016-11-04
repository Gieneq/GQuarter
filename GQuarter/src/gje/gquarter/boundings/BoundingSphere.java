package gje.gquarter.boundings;

import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.toolbox.Maths;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class BoundingSphere extends Bounding {
	private float radius;
	private Vector3f globalPosition;

	private static Vector4f tempValue = new Vector4f();

	public BoundingSphere(PhysicalComponent parentCmp, Vector3f translation, float radius) {
		super(Bounding.TYPE_SPHERE, parentCmp, translation);
		this.radius = radius;
		this.globalPosition = new Vector3f();
		forceMultiMatrixUpdate();
	}

	@Override
	public void forceMultiMatrixUpdate() {
		// uzyskanie z lokalnego przesuniecia, globalnego
		Maths.createTransformationMatrix(Bounding.ZERO_VEC3F, parent.getRotation(), parent.getScale(), modelMatrix);
		tempValue.set(translation.x, translation.y, translation.z, 1f);
		tempValue = Matrix4f.transform(modelMatrix, tempValue, tempValue);
		// dodaje i tworze macierz
		globalPosition.set(parent.getPosition().x + tempValue.x, parent.getPosition().y + tempValue.y, parent.getPosition().z + tempValue.z);
		// TODO czy nie trzeba tez skalowac radiusu?
		Maths.createTransformationMatrix(globalPosition, parent.getRotation(), radius, modelMatrix);
	}

	@Override
	public boolean checkSweepSphereIntersection(Vector3f point, float sweepRadius) {
		float dx = point.x - globalPosition.x;
		float dy = point.y - globalPosition.y;
		float dz = point.z - globalPosition.z;
		float distanceSquared = dx * dx + dy * dy + dz * dz;
		if (distanceSquared < (radius + sweepRadius) * (radius + sweepRadius))
			return true;
		return false;
	}

	@Override
	public float checkCollisionAndGetDistSquared(Vector3f point) {
		float dx = point.x - globalPosition.x;
		float dy = point.y - globalPosition.y;
		float dz = point.z - globalPosition.z;
		float distanceSquared = dx * dx + dy * dy + dz * dz;
		if (distanceSquared < radius * radius)
			return distanceSquared;
		return -1;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
		forceMultiMatrixUpdate();
	}

	public Vector3f getGlobalPosition() {
		return globalPosition;
	}
}
