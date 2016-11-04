package gje.gquarter.entity;

import org.lwjgl.util.vector.Vector3f;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Rotation3f;

public class RobotWheel extends EntityX {

	private PhysicalComponent physical;
	private ModelComponent model;
	private RegionalComponent regional;

	public RobotWheel(EntityX parent, World world, DiffRobotSchematic schematic) {
		super("Robot wheel");

		physical = new PhysicalComponent(new Vector3f(parent.getPhysicalComponentIfHaving().getPosition()), new Rotation3f(), 1f);
		model = ModelBase.getRefRawModelComp(ModelBase.ROBOT_WHEEL_ID).buildModelComponent(physical, new Vector3f(0f, 0.25f, 0f), new Rotation3f(), schematic.robotScale);
		regional = new RegionalComponent(physical.getPosition(), world);

		super.addComponent(physical);
		super.addComponent(model);
		super.addComponent(regional);
	}
	
	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
	}
}
