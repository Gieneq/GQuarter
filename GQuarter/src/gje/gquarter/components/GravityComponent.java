package gje.gquarter.components;

import org.lwjgl.util.vector.Vector3f;

public class GravityComponent implements BasicComponent {
	private static final float GRAVITY_ACCELERATION_MULTIPLIER = 1f;
	public static final float GRAVITY_ACCELERATION_VALUE = 9.81f * GRAVITY_ACCELERATION_MULTIPLIER;
	public static final float SLIDING_FACTOR = 10.0f;

	public static final float DAMPER_FRICTION = 5f;
	public static final float MIN_BREAKING_SPEED = 0.6f;

	private PhysicalComponent physicalComponent;
	private RegionalComponent regionComponent;
	private float mass;
	
	private static Vector3f jumpNormal = new Vector3f();

	public GravityComponent(PhysicalComponent physicalComponent, RegionalComponent regionComponent) {
		this.physicalComponent = physicalComponent;
		this.regionComponent = regionComponent;
		this.mass = 1f;
	}

	@Override
	public void update(float dt) {
		if (regionComponent.isAboveTerrain())
			physicalComponent.getGlobalAcceleration().y = -GRAVITY_ACCELERATION_VALUE;
		else {
			physicalComponent.getPosition().y = regionComponent.getTerrainHeight();
			physicalComponent.getLocalAcceleration().y = 0f;
			physicalComponent.getLocalVelocity().y = 0f;
			physicalComponent.getGlobalAcceleration().y = 0f;
			physicalComponent.getGlobalVelocity().y = 0f;
			physicalComponent.forceMatrixUpdate();
		}
	}

	public void jump(float jumppower) {
		regionComponent.getNormal(jumpNormal);
		jumpNormal.scale(jumppower);
		physicalComponent.getGlobalVelocity().set(jumpNormal);
	}

	public PhysicalComponent getPhysicalComponent() {
		return physicalComponent;
	}

	public RegionalComponent getRegionComponent() {
		return regionComponent;
	}

	public float getFrictionForceValue() {
		return mass * regionComponent.getFrictionFactorValue();
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}
}
