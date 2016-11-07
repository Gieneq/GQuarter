package gje.gquarter.entity;

import java.util.Random;

import org.lwjgl.util.vector.Vector3f;

import gje.gquarter.audio.AudioLibrary;
import gje.gquarter.components.ControlComponent;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.LightComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;

public class EntityXTestBuilder {

	private static Random rand = new Random(12);
	
	public static EntityX buildOakBush(World world, Vector3f initPos, float size, float rz){
		EntityX entityX = new EntityX("OakBush");
		
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.OAK_BUSH_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f( 0, rz, 0), 0.27f);

		
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(grav);
		entityX.addComponent(modelComp);
		
		return entityX;
	}

	public static EntityX buildConiTree(World world, Vector3f initPos, float size, float rz){
		EntityX entityX = new EntityX("ConiTree");
		
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.CONI_TREE_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f( 0, rz, 0), 0.55f);

		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(grav);
		entityX.addComponent(modelComp);
		
		return entityX;
	}

	public static EntityX buildBirdSound(World world, Vector3f initPos, float size){
		EntityX entityX = new EntityX("SourceBirds");
		
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		SoundComponent snd = new SoundComponent(phy, AudioLibrary.getSoundBufferId(AudioLibrary.WIND_RUSTLE_BIRD_A));
		snd.setMinRange(6f);
		snd.setMaxRange(12f);
		snd.getSoundSource().setRollof(1.1f);
		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(snd);
		
		return entityX;
	}

	public static EntityX buildMushroomSpot(World world, Vector3f initPos, float size, float rz){
		EntityX entityX = new EntityX("Mushrooms");
		
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
		GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.MUSHROOM_SPOT_ID).buildModelComponent(phy, new Vector3f(0, -0.05f, 0), new Rotation3f( 0, rz, 0), 0.11f);

		entityX.addComponent(phy);
		entityX.addComponent(reg);
		entityX.addComponent(grav);
		entityX.addComponent(modelComp);
		
		return entityX;
	}
	
	public static EntityX buildStraws(World world, Vector3f initPos, float size, float rz){
		EntityX entityX = new EntityX("Straw");
		
		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
//		GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.STRAWS_ID).buildModelComponent(phy, new Vector3f(0, -0.1f, 0), new Rotation3f( 0, rz, 0), 0.32f);
		
		entityX.addComponent(phy);
		entityX.addComponent(reg);
//		entityX.addComponent(grav);
		entityX.addComponent(modelComp);
		
		return entityX;
	}
	
	
	public static EntityX buildTestPlayer(World world, Vector3f initPos, float size){
		EntityX entityX = new EntityX("Player");

		PhysicalComponent phy = new PhysicalComponent(initPos, new Rotation3f(), size);
		RegionalComponent reg = new RegionalComponent(phy.getPosition(), world);
//		GravityComponent grav = new GravityComponent(phy, reg);
		ModelComponent modelComp = ModelBase.getRefRawModelComp(ModelBase.BALL_ID).buildModelComponent(phy, new Vector3f(0f, 0.25f, 0f), new Rotation3f(0, Maths.PI/4f, 0), 0.15f);
//		ControlComponent ctrlComp = new ControlComponent(grav);
		
		Light light = new Light(new Vector3f(), new Vector3f(0.1f, 0.4f, 0.98f), new Vector3f(1f, 0.1f, 0f));
		LightComponent lightComp = new LightComponent(phy, light, new Vector3f(0, 1f, 0f));
		entityX.addComponent(phy);
		entityX.addComponent(reg);
//		entityX.addComponent(grav);
		entityX.addComponent(modelComp);
//		entityX.addComponent(ctrlComp);
		entityX.addComponent(lightComp);
		
		return entityX;
	}
	
	
}
