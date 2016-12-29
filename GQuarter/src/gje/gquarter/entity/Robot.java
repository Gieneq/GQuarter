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

public class Robot extends EntityX implements OnKeyEventListener {

	private PhysicalComponent physical;
	private ModelComponent model;
	private RegionalComponent regional;
	private GravityComponent gravity;

	private Vector3f force;
	private Vector3f forcePend;
	private Vector3f forceDest;
	private Vector3f forceRock;
	private float radius;
	private Vector3f origin;
	private Pendulum pendulum;
	private Dest destination;
	private ObstRock rock;
	private float mass;
	private Vector3f temp;
	private float initialDx;
	private Key resetKey;

	public Robot(Vector3f initPosition, Pendulum pendulum, Dest destination, ObstRock rock, World world) {
		super("Pendulum");
		this.radius = 1f;// m
		this.mass = 5f;// kg
		this.pendulum = pendulum;
		this.destination = destination;
		this.rock = rock;

		origin = new Vector3f(initPosition);
		physical = new PhysicalComponent(initPosition, new Rotation3f(0f, 0f, 0f), 1f);
		model = ModelBase.getRefRawModelComp(ModelBase.PENDULUM).buildModelComponent(physical, new Vector3f(0f, 1f, 0f), new Rotation3f(), radius, EntityRenderer.RENDERER_TYPE);
		regional = new RegionalComponent(physical.getPosition(), world);
		gravity = new GravityComponent(physical, regional);
		this.force = new Vector3f();
		this.forcePend = new Vector3f();
		this.forceDest = new Vector3f();
		this.forceRock = new Vector3f();
		this.temp = new Vector3f();

		super.addComponent(physical);
		super.addComponent(model);
		super.addComponent(regional);
		super.addComponent(gravity);

		resetKey = new Key(Keyboard.KEY_F5);
		resetKey.setOnClickListener(this);
	}

	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
		calculatePhysics(dt);
	}

	private void calculatePhysics(float dt) {
		pendulum.getForceField().calculateForce(getRadius(), pendulum.getRadius(), physical.getPosition(), forcePend);
		destination.getForceField().calculateForce(getRadius(), destination.getRadius(), physical.getPosition(), forceDest);
		rock.getForceField().calculateForce(getRadius(), rock.getRadius(), physical.getPosition(), forceRock);
		Vector3f.add(forcePend, forceDest, force);
		Vector3f.add(force, forceRock, force);
		force.scale(1f / mass);
		physical.getGlobalVelocity().x = force.x;
		physical.getGlobalVelocity().z = force.z;
		gravity.update(dt);
	}

	@Override
	public boolean onKeyClick(int keyId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		physical.stopAll();
		physical.getPosition().set(origin);
		return false;
	}

	public float getRadius() {
		return radius;
	}

}
