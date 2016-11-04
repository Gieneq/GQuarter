package gje.gquarter.components;

import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class PhysicalComponent implements BasicComponent {
	private Vector3f position;
	private Vector3f lokalVelocity;
	private Vector3f lokalAcceleration;

	private Vector3f globalVelocity;
	private Vector3f globalAcceleration;

	private Rotation3f rotation;
	private Rotation3f rotationVelocity;
	private Rotation3f rotationAcceleration;

	private float scale;
	private Matrix4f modelMatrix;

	private static Vector3f tempVelocity = new Vector3f();
	private static Vector3f tempAcceleration = new Vector3f();

	private boolean physical;

	public PhysicalComponent(Vector3f initialPosition, Rotation3f initialRotation, float scale) {
		this.position = initialPosition;
		this.lokalVelocity = new Vector3f();
		this.lokalAcceleration = new Vector3f();
		this.globalVelocity = new Vector3f();
		this.globalAcceleration = new Vector3f();

		this.rotation = initialRotation;
		this.rotationVelocity = new Rotation3f();
		this.rotationAcceleration = new Rotation3f();

		this.scale = scale;
		this.physical = true;
		modelMatrix = new Matrix4f();
		forceMatrixUpdate();
	}

	@Override
	public void update(float dt) {
		if (physical) {
			// RUCH OBROTOWY
			rotation.rx += rotationVelocity.rx * dt;
			rotation.ry += rotationVelocity.ry * dt;
			rotation.rz += rotationVelocity.rz * dt;

			rotationVelocity.rx += rotationAcceleration.rx * dt;
			rotationVelocity.ry += rotationAcceleration.ry * dt;
			rotationVelocity.rz += rotationAcceleration.rz * dt;

			// ZAMIANA UKLADU ODNIECSIENIA NA LOKALNY (Z OBROTOW)
			Maths.createTransformationMatrix(rotation, modelMatrix);

			// RUCH POSTEPOWY
			Maths.transformVec3f(modelMatrix, lokalVelocity, tempVelocity);
			position.x += (tempVelocity.x + globalVelocity.x) * dt;
			position.y += (tempVelocity.y + globalVelocity.y) * dt;
			position.z += (tempVelocity.z + globalVelocity.z) * dt;

			Maths.transformVec3f(modelMatrix, lokalAcceleration, tempAcceleration);
			lokalVelocity.x += tempAcceleration.x * dt;
			lokalVelocity.y += tempAcceleration.y * dt;
			lokalVelocity.z += tempAcceleration.z * dt;

			globalVelocity.x += globalAcceleration.x * dt;
			globalVelocity.y += globalAcceleration.y * dt;
			globalVelocity.z += globalAcceleration.z * dt;
			forceMatrixUpdate();
		}
	}

	public void forceMatrixUpdate() {
		Maths.createTransformationMatrix(position, rotation, scale, modelMatrix);
	}

	public void stopMovement() {
		lokalVelocity.set(0, 0, 0);
		lokalAcceleration.set(0, 0, 0);
	}

	public void stopRotation() {
		rotationVelocity.setRotation(0, 0, 0);
		rotationAcceleration.setRotation(0, 0, 0);
	}

	public void stopAllt() {
		stopMovement();
		stopRotation();
	}

	/*
	 * AKCESORY
	 */

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getLocalVelocity() {
		return lokalVelocity;
	}

	public Vector3f getLocalAcceleration() {
		return lokalAcceleration;
	}

	public Rotation3f getRotation() {
		return rotation;
	}

	public Rotation3f getRotationVelocity() {
		return rotationVelocity;
	}

	public Rotation3f getRotationAcceleration() {
		return rotationAcceleration;
	}

	public float getScale() {
		return scale;
	}

	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	public Vector3f getGlobalVelocity() {
		return globalVelocity;
	}

	public Vector3f getGlobalAcceleration() {
		return globalAcceleration;
	}

	public boolean isPhysical() {
		return physical;
	}

	public void setPhysical(boolean physical) {
		this.physical = physical;
	}
}
