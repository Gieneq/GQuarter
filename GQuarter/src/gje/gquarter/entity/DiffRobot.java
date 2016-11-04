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

public class DiffRobot extends EntityX implements OnKeyEventListener {

	private PhysicalComponent physical;
	private ModelComponent model;
	private RegionalComponent regional;
	private GravityComponent gravity;

	// private float rotationLeft;
	// private float rotationRight;
	private float accelerationLeft;
	private float accelerationRight;
	private float Ms;
	private float vMax;

	private DiffRobotSchematic schematic;

	private Key keyU;
	private Key keyJ;
	private Key keyI;
	private Key keyK;

	public DiffRobot(Vector3f initPosition, float initYaw, DiffRobotSchematic schematic, World world) {
		super("Diff robot");
		this.schematic = schematic;

		this.accelerationLeft = 0.0f;
		this.accelerationRight = 0.0f;
		this.Ms = 0f;
		this.vMax = 3f;

		physical = new PhysicalComponent(initPosition, new Rotation3f(0f, initYaw, 0f), 1f);
		model = ModelBase.getRefRawModelComp(ModelBase.ROBOT_BOX_ID).buildModelComponent(physical, new Vector3f(0f, 0.25f, 0f), new Rotation3f(), schematic.robotScale);
		regional = new RegionalComponent(physical.getPosition(), world);
		gravity = new GravityComponent(physical, regional);

		super.addComponent(physical);
		super.addComponent(model);
		super.addComponent(regional);
		super.addComponent(gravity);

		keyU = new Key(Keyboard.KEY_U);
		keyU.setOnClickListener(this);
		keyJ = new Key(Keyboard.KEY_J);
		keyJ.setOnClickListener(this);
		keyI = new Key(Keyboard.KEY_I);
		keyI.setOnClickListener(this);
		keyK = new Key(Keyboard.KEY_K);
		keyK.setOnClickListener(this);
	}

	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
		calculatePhysics(dt);
	}

	private void calculatePhysics(float dt) {
		// float velLeft = rotationLeft * schematic.wheelRadius;
		// float velRight = rotationRight * schematic.wheelRadius;

		// float velRobot = (velRight + velLeft) / 2f;
		// float omegaRobot = (velRight - velLeft) / schematic.shaftLength;

		// physical.getRotationVelocity().ry = omegaRobot;
		// physical.getLocalVelocity().x = velRobot;

		/*
		 * ------------------------------------------------------
		 */
		// rampa pochykana w dol wzgloz 0X
		// powiedzmy ze to wektor normalny:

		// Vector3f normalFake = new Vector3f(Maths.SQRT2, Maths.SQRT2, 0f);
		// Vector3f slidingFake = new Vector3f(Maths.SQRT2, -Maths.SQRT2, 0f);

		float vMin = 0.3f;
		float vel = Math.abs(physical.getLocalVelocity().x);
		
		if (Ms > 6f)
			Ms = 6f;
		else if (Ms < -6f)
			Ms = -6f;

		//TODO cos jadac w tyl sie psuje...........
		// Ms = -m * g * r * Maths.sin(alpha);
		float Mop = Maths.linearFunctionValue(vMin, 0f, vMax, Ms, vel);
		float Wwy = Ms - Math.signum(vel) * Mop;

		float alpha = 0f; //Maths.PI / (6f);
		float m = 1f;
		float r = schematic.wheelRadius;
		float g = 9.81f;
		float mi = 0.6f;

		float a = (2f / 3f) * (g * Maths.sin(alpha) + Wwy / (m * r));
		float amax = 2f * (g * mi * Maths.cos(alpha) + Wwy / (m * r));

		if (vel < 0.4f && Math.abs(a) < 0.4f) {
			a = 0f;
			physical.getLocalVelocity().x = 0f;
		}

//		System.out.println("a: " + a + " [" + amax + "], Ms: " + Ms + ", Mop: " + Mop + ", vel: " + vel);

		accelerationLeft = a / r;
		accelerationRight = a / r;

		float accLeft = a;
		float accRight = a;

		float accRobot = (accRight + accLeft) / 2f;
		float epsilonRobot = (accRight - accLeft) / schematic.shaftLength;

		physical.getRotationAcceleration().ry = epsilonRobot;
		physical.getLocalAcceleration().x = accRobot;
	}

	@Override
	public boolean onKeyClick(int keyId) {
		// float dv = 0.4f;
		if (keyId == keyU.getKeyId()) {
			Ms += 0.1f;
			return true;
		}
		if (keyId == keyJ.getKeyId()) {
			Ms -= 0.1f;
			return true;
		}
		if (keyId == keyI.getKeyId()) {
			vMax += 0.5f;
			return true;
		}
		if (keyId == keyK.getKeyId()) {
			vMax -= 0.5f; //TODO uwaga na 0 :D
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		return false;
	}
}
