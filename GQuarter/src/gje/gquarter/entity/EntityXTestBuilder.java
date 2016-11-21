package gje.gquarter.entity;

import gje.gquarter.audio.AudioLibrary;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Rotation3f;

import org.lwjgl.util.vector.Vector3f;

public class EntityXTestBuilder {
	public static final int STRAWS_ID = 1;
	public static final int REEDS_ID = 2;
	public static final int MUSHROOMS_ID = 3;
	public static final int OAK_TREE_ID = 4;
	public static final int SPRUCE_TREE_ID = 5;

	public static final int BEACON_ID = 6;
	public static final int MARBLE_STONE_ID = 7;
	public static final int SPRUCE_TRUNK_ID = 8;
	public static final int LILY_ID = 9;
	public static final int SEAWEED_ID = 10;

	public static EntityX buildEnvEntityByID(World world, int id, Vector3f initPos, float scale, float ry) {
		if (id == STRAWS_ID)
			return buildStraws(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == REEDS_ID)
			return buildReeds(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == MUSHROOMS_ID)
			return buildMushroomSpot(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == OAK_TREE_ID)
			return buildOakTree(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == SPRUCE_TREE_ID)
			return buildSpruceTree(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		
		if (id == BEACON_ID)
			return buildBeaconWaypoint(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == MARBLE_STONE_ID)
			return buildMarbleStone(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == SPRUCE_TRUNK_ID)
			return buildSpruceTrunk(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == LILY_ID)
			return buildWaterLily(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		if (id == SEAWEED_ID)
			return buildSeaweed(world, initPos, scale, ry, EntityX.TYPE_ENVIRONMENTAL);
		
		return null;
	}

	public static int getIdByEntityName(String name) {
		if (name == "Straws")
			return STRAWS_ID;
		if (name == "Reeds")
			return REEDS_ID;
		if (name == "Mushrooms Spot")
			return MUSHROOMS_ID;
		if (name == "Oak Tree")
			return OAK_TREE_ID;
		if (name == "Spruce Tree")
			return SPRUCE_TREE_ID;

		if (name == "Beacon")
			return BEACON_ID;
		if (name == "Marble Stone")
			return MARBLE_STONE_ID;
		if (name == "Spruce Trunk")
			return SPRUCE_TRUNK_ID;
		if (name == "Water Lily")
			return LILY_ID;
		if (name == "Seaweed")
			return SEAWEED_ID;
		
		return -1; // TODO
	}

	public static EntityX buildOakTree(World world, Vector3f initPos, float size, float rz, int entityType) {
		EntityX entityX = new EntityX("Oak Tree", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.OAK_TREE_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f(0, rz, 0), 0.27f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildSpruceTree(World world, Vector3f initPos, float size, float rz, int entityType) {
		EntityX entityX = new EntityX("Spruce Tree", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.SPRUCE_TREE_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f(0, rz, 0), 0.55f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildBirdSound(World world, Vector3f initPos, float size) {
		EntityX entityX = new EntityX("Sound Birds");

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
		entityX.forceUpdate(0f);
		return entityX;
	}

	public static EntityX buildMushroomSpot(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Mushrooms Spot", entityType);
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
		EntityX entityX = new EntityX("Straws", entityType);
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
	
	public static EntityX buildSeaweed(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Seaweed", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.SEAWEED_ID).buildModelComponent(phy, new Vector3f(0f, -0.1f, 0f), new Rotation3f(0f, 0f, 0f), 0.5f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}
	
	public static EntityX buildWaterLily(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Water Lily", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.LILY_ID).buildModelComponent(phy, new Vector3f(0f, 0.01f, 0f), new Rotation3f(0f, 0f, 0f), 0.4f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}
	
	public static EntityX buildMarbleStone(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Marble Rock", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.MARBLE_ROCK_ID).buildModelComponent(phy, new Vector3f(0f, -0.1f, 0f), new Rotation3f(0f, 0f, 0f), 0.32f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}
	
	public static EntityX buildSpruceTrunk(World world, Vector3f initPos, float size, float ry, int entityType) {
		EntityX entityX = new EntityX("Spruce Trunk", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(0f, ry, 0f), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.SPRUCE_TRUNK_ID).buildModelComponent(phy, new Vector3f(0f, -0.1f, 0f), new Rotation3f(0f, 0f, 0f), 0.29f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}
	
	public static EntityX buildBeaconWaypoint(World world, Vector3f initPos, float size, float rz, int entityType) {
		EntityX entityX = new EntityX("Beacon", entityType);
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.BEACON_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f(0, rz, 0), 0.55f, entityType);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(modelComp);
		entityX.forceUpdate(0f);
		return entityX;
	}
}
