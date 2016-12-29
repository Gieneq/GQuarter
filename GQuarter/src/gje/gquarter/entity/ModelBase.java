package gje.gquarter.entity;

import gje.gquarter.components.RawModelComponent;
import gje.gquarter.core.Loader;
import gje.gquarter.models.TexturedModel;

import java.util.HashMap;
import java.util.Map;

public class ModelBase {
	public static final int BEACON_ID = 0;
	public static final int MARBLE_ROCK_ID = 1;
	public static final int STRAWS_ID = 2;
	public static final int OAK_TREE_ID = 3;
	public static final int SPRUCE_TREE_ID = 4;
	public static final int MUSHROOM_SPOT_ID = 5;
	public static final int LILY_ID = 6;
	public static final int ROBOT_WHEEL_ID = 7;
	public static final int ROBOT_BOX_ID = 8;
	public static final int REEDS_ID = 9;
	public static final int SEAWEED_ID = 10;
	public static final int SPRUCE_TRUNK_ID = 11;
	public static final int PENDULUM = 12;
	public static final int PENDULUM_STICK = 13;
	private static Map<Integer, RawModelComponent> rawModels = new HashMap<Integer, RawModelComponent>();

	public static void init() {
		rawModels.put(BEACON_ID, setupBeacon());
		rawModels.put(MARBLE_ROCK_ID, setupMarbleRock());
		rawModels.put(STRAWS_ID, setupStraws());
		rawModels.put(OAK_TREE_ID, setupOakTree());
		rawModels.put(SPRUCE_TREE_ID, setupSpruceTree());
		rawModels.put(MUSHROOM_SPOT_ID, setupMushroomSpot());
		rawModels.put(LILY_ID, setupLily());
		rawModels.put(ROBOT_WHEEL_ID, setupWheel());
		rawModels.put(ROBOT_BOX_ID, setupRobotBox());
		rawModels.put(REEDS_ID, setupReeds());
		rawModels.put(SEAWEED_ID, setupSeaweed());
		rawModels.put(SPRUCE_TRUNK_ID, setupSpruceTrunk());
		rawModels.put(PENDULUM, setupPendulum());
		rawModels.put(PENDULUM_STICK, setupPendulumStick());
	}

	private static RawModelComponent setupStraws() {
		String objFilepath = "models/nature/grass_tuft";
		String textFilepath = "models/nature/grass_tuft";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(360, texturedModel);
		key.setAnimationType(EnvironmentalKey.ANIMATION_STAW);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupLily() {
		String objFilepath = "models/nature/lily";
		String textFilepath = "models/nature/liliTextures";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(30, texturedModel);
		key.setAnimationType(EnvironmentalKey.ANIMATION_WATERLILY);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupSeaweed() {
		String objFilepath = "models/nature/seaweed";
		String textFilepath = "models/nature/seaweedsTexture";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(5, texturedModel);
		key.setAnimationType(EnvironmentalKey.ANIMATION_SEAWEED);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupSpruceTrunk() {
		String objFilepath = "models/nature/trunk";
		String textFilepath = "models/nature/trunkTexture";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(25, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupMarbleRock() {
		String objFilepath = "models/nature/rock";
		String textFilepath = "models/nature/marble2";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(45, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupBeacon() {
		String objFilepath = "models/structures/beacon";
		String textFilepath = "models/structures/beaconTexture";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 3f;
		float reflectivity = 3f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(30, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupReeds() {
		String objFilepath = "models/nature/reeds_tuft";
		String textFilepath = "models/nature/reeds_tuft";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(360, texturedModel);
		key.setAnimationType(EnvironmentalKey.ANIMATION_STAW);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupOakTree() {
		String objFilepath = "models/nature/bush_oak";
		String textFilepath = "models/nature/oak";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(100, texturedModel);
		key.setAnimationType(EnvironmentalKey.ANIMATION_STAW);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupSpruceTree() {
		String objFilepath = "models/nature/tree_coni";
		String textFilepath = "models/nature/coniferous";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(100, texturedModel);
		key.setAnimationType(EnvironmentalKey.ANIMATION_STAW);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupMushroomSpot() {
		String objFilepath = "models/nature/mushroom";
		String textFilepath = "models/nature/mushroom";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 20f;
		float reflectivity = 0.1f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(12, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupWheel() {
		String objFilepath = "models/robot/robot_wheel";
		String textFilepath = "models/robot/wheel";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 1f;
		float reflectivity = 0.1f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupRobotBox() {
		String objFilepath = "models/robot/robot_smooth";
		String textFilepath = "models/robot/robot_texture";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 1f;
		float reflectivity = 0.2f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		return new RawModelComponent(texturedModel, atlasId);
	}
	
	private static RawModelComponent setupPendulum() {
		String objFilepath = "models/prymitives/bounding_sphere";
		String textFilepath = "models/nature/marble2";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 4f;
		float reflectivity = 0.5f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		return new RawModelComponent(texturedModel, atlasId);
	}
	
	private static RawModelComponent setupPendulumStick() {
		String objFilepath = "models/prymitives/bounding_cyl";
		String textFilepath = "models/nature/marble2";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 1f;
		float reflectivity = 0.4f;
		boolean hasTransparency = false;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		return new RawModelComponent(texturedModel, atlasId);
	}
	
	public static RawModelComponent getRefRawModelComp(int id) {
		return rawModels.get(id);
	}
}
