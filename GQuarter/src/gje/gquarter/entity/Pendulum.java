package gje.gquarter.entity;

import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Pendulum extends EntityX {

	private PhysicalComponent physical;
	private ModelComponent model;
	private RegionalComponent regional;
	private float length;
	private float omega;
	private float maxAngle;
	private float angle;
	private float amplitude;
	private float time;
	private float radius;
	private Vector3f origin;
	private Field forceField;

	public Pendulum(Vector3f initPosition, World world) {
		super("Pendulum");
		this.radius = 2f;//m
		origin = new Vector3f(initPosition);
		physical = new PhysicalComponent(initPosition, new Rotation3f(0f, 0f, 0f), 1f);
		model = ModelBase.getRefRawModelComp(ModelBase.PENDULUM).buildModelComponent(physical, new Vector3f(0f, 0f, 0f), new Rotation3f(), radius, EntityRenderer.RENDERER_TYPE);
		regional = new RegionalComponent(physical.getPosition(), world);
		this.length = 7f; //m
		this.omega = (float) Math.sqrt(GravityComponent.GRAVITY_ACCELERATION_VALUE / length);
		this.maxAngle = Maths.toRadians(15f); //*
		this.time = 0;
		this.angle = maxAngle;
//		this.amplitude = 2f;
		this.forceField = new Field(60f, initPosition, Field.TYPE_MINUS);
		
		super.addComponent(physical);
		super.addComponent(model);
		super.addComponent(regional);
	}

	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
		calculatePhysics(dt);
	}

	private void calculatePhysics(float dt) {
		time += dt;
		angle = (float) (maxAngle * Math.sin(omega * time));
		float pendulumX = origin.x + length * Maths.sin(angle);
		float pendulumY = origin.y - length * Maths.cos(angle);
		float pendulumZ = origin.z;
		physical.getPosition().set(pendulumX, pendulumY, pendulumZ);
		physical.getRotation().rz = angle;
	}

	public float getLength() {
		return length;
	}

	public float getAngle() {
		return angle;
	}

	public Vector3f getOrigin() {
		return origin;
	}

	public Field getForceField() {
		return forceField;
	}

	public float getRadius() {
		return radius;
	}
	
}
