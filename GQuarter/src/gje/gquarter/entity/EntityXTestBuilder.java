package gje.gquarter.entity;

import gje.gquarter.audio.AudioLibrary;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.LightComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;

import org.lwjgl.util.vector.Vector3f;

public class EntityXTestBuilder {
	public static final int STRAWS_ID = 1;
	public static final int REEDS_ID = 2;
	public static final int MUSHROOMS_ID = 3;
	public static final int OAK_BUSH_ID = 4;
	public static final int CONI_TREE_ID = 5;

	public static EntityX buildEnvEntityByID(World world, int id, Vector3f initPos, float scale, float ry) {
		if (id == STRAWS_ID)
			return buildStraws(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == REEDS_ID)
			return buildReeds(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == MUSHROOMS_ID)
			return buildMushroomSpot(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == OAK_BUSH_ID)
			return buildOakBush(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == CONI_TREE_ID)
			return buildConiTree(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		return null;
	}

	public static int getIdByEntityName(String name) {
		if (name == "Straws")
			return STRAWS_ID;
		if (name == "Reeds")
			return REEDS_ID;
		if (name == "Mushrooms")
			return MUSHROOMS_ID;
		if (name == "OakBush")
			return OAK_BUSH_ID;
		if (name == "ConiTree")
			return CONI_TREE_ID;
		return -1; // TODO
	}

	public static EntityX buildOakBush(World world, Vector3f initPos, float size, float rz, int entityType) {
		EntityX entityX = new EntityX("OakBush", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.OAK_BUSH_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f(0, rz, 0), 0.27f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildConiTree(World world, Vector3f initPos, float size, float rz, int entityType) {
		EntityX entityX = new EntityX("ConiTree", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.CONI_TREE_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f(0, rz, 0), 0.55f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildBirdSound(World world, Vector3f initPos, float size) {
		EntityX entityX = new EntityX("SourceBirds");

		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		GravityComponent grav = new GravityComponent(phy, reg);
		SoundComponent snd = new SoundComponent(phy, AudioLibrary.getSoundBufferId(AudioLibrary.WIND_RUSTLE_BIRD_A));
		snd.setMinRange(6f);
		snd.setMaxRange(12f);
		snd.getSoundSource().setRollof(1.1f);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(snd);
		entityX.addComponent(grav);

		return entityX;
	}

	public static EntityX buildMushroomSpot(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Mushrooms", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.MUSHROOM_SPOT_ID).buildModelComponent(phy, new Vector3f(0f, -0.05f, 0f), new Rotation3f(0f, 0f, 0f), 0.11f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(grav);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildStraws(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Straw", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.STRAWS_ID).buildModelComponent(phy, new Vector3f(0f, -0.1f, 0f), new Rotation3f(0f, 0f, 0f), 0.32f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildReeds(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Reeds", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.REEDS_ID).buildModelComponent(phy, new Vector3f(0f, -0.1f, 0f), new Rotation3f(0f, 0f, 0f), 0.32f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	@Deprecated
	public static EntityX buildTestPlayer(World world, Vector3f initPos, float size) {
		EntityX entityX = new EntityX("Player");

		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		// GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.BALL_ID).buildModelComponent(phy, new Vector3f(0f, 0.25f, 0f), new Rotation3f(0, Maths.PI / 4f, 0), 0.15f, EntityRenderer.RENDERER_TYPE);
		// ControlComponent ctrlComp = new ControlComponent(grav);

		Light light = new Light(new Vector3f(), new Vector3f(0.1f, 0.4f, 0.98f), new Vector3f(1f, 0.1f, 0f));
		LightComponent lightComp = new LightComponent(phy, light, new Vector3f(0, 1f, 0f));
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		// entityX.addComponent(grav);
		entityX.addComponent(modelComp);
		// entityX.addComponent(ctrlComp);
		entityX.addComponent(lightComp);

		return entityX;
	}

}
