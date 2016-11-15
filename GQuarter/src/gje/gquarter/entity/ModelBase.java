package gje.gquarter.entity;

import gje.gquarter.components.RawModelComponent;
import gje.gquarter.core.Loader;
import gje.gquarter.models.TexturedModel;

import java.util.HashMap;
import java.util.Map;

public class ModelBase {
	public static final int BALL_ID = 0;
	public static final int SNOW_BOX_ID = 1;
	public static final int STRAWS_ID = 2;
	public static final int OAK_BUSH_ID = 3;
	public static final int CONI_TREE_ID = 4;
	public static final int MUSHROOM_SPOT_ID = 5;
	public static final int DRY_BUSH_ID = 6;
	public static final int ROBOT_WHEEL_ID = 7;
	public static final int ROBOT_BOX_ID = 8;
	public static final int REEDS_ID = 9;
	private static Map<Integer, RawModelComponent> rawModels = new HashMap<Integer, RawModelComponent>();

	public static void init() {
		rawModels.put(BALL_ID, setupBall());
		rawModels.put(SNOW_BOX_ID, setupSnowBox());
		rawModels.put(STRAWS_ID, setupStraws());
		rawModels.put(OAK_BUSH_ID, setupBushOak());
		rawModels.put(CONI_TREE_ID, setupTreeConi());
		rawModels.put(MUSHROOM_SPOT_ID, setupMushroomSpot());
		rawModels.put(ROBOT_WHEEL_ID, setupWheel());
		rawModels.put(ROBOT_BOX_ID, setupRobotBox());
		rawModels.put(REEDS_ID, setupReeds());
	}

	@Deprecated
	private static RawModelComponent setupBall() {
		String objFilepath = "models/prymitives/bounding_sphere";
		String textFilepath = "models/structures/origin";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 100f;
		float reflectivity = 10f;
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

	@Deprecated
	private static RawModelComponent setupSnowBox() {
		String objFilepath = "models/structures/box";
		String textFilepath = "models/structures/snowbox";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 8f;
		float reflectivity = 1f;
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

		new EnvironmentalKey(80, texturedModel);
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

		new EnvironmentalKey(100, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupBushOak() {
		String objFilepath = "models/nature/bush_oak";
		String textFilepath = "models/nature/oak";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 8f;
		float reflectivity = 0f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		new EnvironmentalKey(20, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupTreeConi() {
		String objFilepath = "models/nature/tree_coni";
		String textFilepath = "models/nature/coniferous";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 8f;
		float reflectivity = 0f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		new EnvironmentalKey(20, texturedModel);
		return new RawModelComponent(texturedModel, atlasId);
	}

	private static RawModelComponent setupMushroomSpot() {
		String objFilepath = "models/nature/mushroom";
		String textFilepath = "models/nature/mushroom";
		int atlasRows = 1;
		int atlasId = 1;
		float shineDamper = 8f;
		float reflectivity = 0f;
		boolean hasTransparency = true;
		boolean useFakeLight = false;

		TexturedModel texturedModel = Loader.buildTexturedModel(objFilepath, textFilepath, Loader.MIPMAP_MEDIUM);
		texturedModel.getTexture().setNumberOfRows(atlasRows);
		texturedModel.getTexture().setShineDamper(shineDamper);
		texturedModel.getTexture().setReflectivity(reflectivity);
		texturedModel.getTexture().setHasTransparency(hasTransparency);
		texturedModel.getTexture().setUseFakeLighting(useFakeLight);

		EnvironmentalKey key = new EnvironmentalKey(12, texturedModel);
		key.setHardnes(24f);
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

	public static RawModelComponent getRefRawModelComp(int id) {
		return rawModels.get(id);
	}
}
