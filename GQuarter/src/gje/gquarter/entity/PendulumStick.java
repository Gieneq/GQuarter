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

public class PendulumStick extends EntityX {

	private PhysicalComponent physical;
	private ModelComponent model;
	private RegionalComponent regional;
	private Pendulum pendulum;
	private float radius;
	private float length;
	private Vector3f scale;

	public PendulumStick(Pendulum pendulum, World world) {
		super("Pendulum");
		this.pendulum = pendulum;
		this.radius = 0.2f;//m patyczka
		physical = new PhysicalComponent(new Vector3f(pendulum.getOrigin()), new Rotation3f(0f, 0f, 0f), 1f);
		model = ModelBase.getRefRawModelComp(ModelBase.PENDULUM_STICK).buildModelComponent(physical, new Vector3f(0f, 0f, 0f), new Rotation3f(), radius, EntityRenderer.RENDERER_TYPE);
		regional = new RegionalComponent(physical.getPosition(), world);
		this.length = pendulum.getLength();
		this.scale = new Vector3f(radius, length/2f, radius);
		this.physical.setPhysical(false);
		super.addComponent(physical);
		super.addComponent(model);
		super.addComponent(regional);
	}

	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
		calculatePhysics(dt);
		Maths.createTransformationMatrix(physical.getPosition(), physical.getRotation(), scale, model.getMultiModelMatrix());
		
	}

	private void calculatePhysics(float dt) {
		Vector3f origin = pendulum.getOrigin();
		
		float stickX = origin.x + length * Maths.sin(pendulum.getAngle()) * 0.5f;
		float stickY = origin.y - length * Maths.cos(pendulum.getAngle()) * 0.5f;
		float stickZ = origin.z;
		physical.getPosition().set(stickX, stickY, stickZ);
		physical.getRotation().rz = pendulum.getAngle();
	}
}
