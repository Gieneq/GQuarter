package gje.gquarter.entity;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import gje.gquarter.components.ControlComponent;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.LightComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;
import gje.gquarter.toolbox.ToolBox;

public class PlayerEntity extends EntityX implements OnKeyEventListener {
	private static final String FILEPATH = "world/player/player.gq";
	private PhysicalComponent phy;
	private RegionalComponent reg;
	private GravityComponent grav;
	private ModelComponent model;
	private LightComponent lightComp;
	private ControlComponent control;
	private Key keyT;

	public PlayerEntity(World world, Vector3f initPos) {
		super("Player");
		phy = new PhysicalComponent(initPos, new Rotation3f(0f, 0f, 0f), 1f);
		reg = new RegionalComponent(phy.getPosition(), world);
		grav = new GravityComponent(phy, reg);
		model = ModelBase.getRefRawModelComp(ModelBase.ROBOT_BOX_ID).buildModelComponent(phy, new Vector3f(), new Rotation3f(0f, -Maths.PI / 2f, 0f), 1f, EntityRenderer.RENDERER_TYPE);

		Light light = new Light(new Vector3f(), new Vector3f(0.1f, 0.4f, 0.98f), new Vector3f(1f, 0.1f, 0f));
		lightComp = new LightComponent(phy, light, new Vector3f(0, 1.0f, 0f));
		control = new ControlComponent(grav);

		super.addComponent(phy);
		super.addComponent(reg);
		super.addComponent(grav);
		super.addComponent(model);
		super.addComponent(lightComp);
		super.addComponent(control);

		keyT = new Key(Keyboard.KEY_T);
		keyT.setOnClickListener(this);
	}

	@Override
	public void updateEntity(float dt) {
		super.updateEntity(dt);
	}

	@Override
	public boolean onKeyClick(int keyId) {
		if (keyId == Keyboard.KEY_T) {
			Vector3f vs = new Vector3f();
			reg.getSliding(vs);
			float tetha = (float) Math.atan2(vs.x, vs.z);
			phy.getRotation().ry = tetha;
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

	public PhysicalComponent getPhysicalCmp() {
		return phy;
	}

	public static PlayerEntity loadPlayer(World world) {
		ArrayList<String> raw = ToolBox.loadGQFile(FILEPATH);
		Vector3f playerPositon = new Vector3f();
		Rotation3f playerRotation = new Rotation3f();

		for (String line : raw) {
			String[] args = ToolBox.splitGQLine(line);
			if (args[0].startsWith("position")) {
				playerPositon.x = Float.parseFloat(args[1]);
				playerPositon.y = Float.parseFloat(args[2]);
				playerPositon.z = Float.parseFloat(args[3]);
			}
			if (args[0].startsWith("rotation")) {
				playerRotation.rx = Float.parseFloat(args[1]);
				playerRotation.ry = Float.parseFloat(args[2]);
				playerRotation.rz = Float.parseFloat(args[3]);
			}
		}
		PlayerEntity pl = new PlayerEntity(world, playerPositon);
		pl.getPhysicalCmp().getRotation().set(playerRotation);
		return pl;
	}

	public static void savePlayer(PlayerEntity player) {
		String playerData = "";
		PhysicalComponent phy = player.getPhysicalCmp();
		playerData += ToolBox.buildGQLine("position", phy.getPosition().x + "", phy.getPosition().y + "", phy.getPosition().z + "");
		playerData += ToolBox.buildGQLine("rotation", phy.getRotation().rx + "", phy.getRotation().ry + "", phy.getRotation().rz + "");
		
		ToolBox.saveFile(playerData, FILEPATH);
	}
}
