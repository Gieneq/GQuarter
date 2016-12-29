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

public class ObstRock extends EntityX {

	private PhysicalComponent physical;
	private ModelComponent model;
	private RegionalComponent regional;
	private GravityComponent gravity;

	private float time;
	private float omega;
	private float size;
	private Vector3f origin;
	private Field forceField;
	private float radius;

	public ObstRock(Vector3f initPosition, World world) {
		super("Pendulum");
		this.size = 1f;// m
		this. radius = 1.2f;
		origin = new Vector3f(initPosition);
		physical = new PhysicalComponent(initPosition, new Rotation3f(0f, 0f, 0f), 1f);
		model = ModelBase.getRefRawModelComp(ModelBase.MARBLE_ROCK_ID).buildModelComponent(physical, new Vector3f(0f, 0f, 0f), new Rotation3f(), size, EntityRenderer.RENDERER_TYPE);
		regional = new RegionalComponent(physical.getPosition(), world);
		gravity = new GravityComponent(physical, regional);
		this.time = 0f;
		float period = 2f;
		this.omega = Maths.PI2 * (1f / period);
		super.addComponent(physical);
		super.addComponent(model);
		super.addComponent(regional);
		super.addComponent(gravity);
		this.forceField = new Field(90f, initPosition, Field.TYPE_MINUS);
	}

	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
		calculatePhysics(dt);
	}

	private void calculatePhysics(float dt) {
	}

	public Field getForceField() {
		return forceField;
	}

	public float getRadius() {
		return radius;
	}
	
}
