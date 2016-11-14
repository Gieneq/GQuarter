package gje.gquarter.terrain;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.core.Loader;
import gje.gquarter.entity.EntityX;
import gje.gquarter.entity.EntityXTestBuilder;
import gje.gquarter.models.TerrainTexture;
import gje.gquarter.models.TerrainTexturePack;
import gje.gquarter.toolbox.Rotation3f;
import gje.gquarter.toolbox.ToolBox;

public class WorldBuilder {
	private static final String TEST_REGION_FILEATH = "world/REGIONS/REGIONeNV.gq";

	public static World buildTestWorld() {
		World world;
		String name = "Jakis swiat";
		String descr = "Nic ciekawego";

		float regSize = 1025;// 257
		float chunkAmpl = 23;// 17

		int offsetX = 0;
		int offsetZ = 0;

		world = new World(name, descr, regSize, chunkAmpl, offsetX, offsetZ);

		return world;
	}

	public static Region buildTestRegion(int gridX, int gridZ, World parentWorld, boolean create) {
		Region reg;

		int id = 0;
		String name = "Jakis pierwszy region";
		String descr = "Nic ciekawego, znowu";

		String bmName = "bm1";
		String hmName = "hm1Xbig";

		String rName = "cobblePathBright";
		String gName = "darkDirt";
		String bName = "rock";
		String aName = "plainGrass";

		TerrainTexture blendMap = new TerrainTexture(Loader.loadTextureFiltered("world/blendMaps/" + bmName, Loader.MIPMAP_MEDIUM), bmName);
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + rName, Loader.MIPMAP_MEDIUM), rName);
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + gName, Loader.MIPMAP_MEDIUM), gName);
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + bName, Loader.MIPMAP_MEDIUM), gName);
		TerrainTexture aTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + aName, Loader.MIPMAP_MEDIUM), aName);
		TerrainTexturePack pack = new TerrainTexturePack(aTexture, rTexture, gTexture, bTexture);

		Terrain ter = new Terrain(gridX, gridZ, parentWorld.getRegionSize(), parentWorld.getRegionAmplitude(), pack, blendMap, "world/heightMaps/" + hmName, create);

		reg = new Region(id, name, descr, ter);
		parentWorld.addRegion(reg);
		parentWorld.update(0f);
		loadTestRegionsEnvironment(reg, parentWorld);
		return reg;

	}

	private static void loadTestRegionsEnvironment(Region region, World parentWorld) {
		ArrayList<String> raw = ToolBox.loadGQFile(TEST_REGION_FILEATH);
		for (String line : raw) {
			String[] args = ToolBox.splitGQLine(line);
			if (args[0].startsWith("e")) {
				int id = Integer.parseInt(args[1]);
				Vector3f initPos = new Vector3f(Float.parseFloat(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4]));
				float eScale = Float.parseFloat(args[5]);
				float eRY = Float.parseFloat(args[6]);
				EntityX ent = EntityXTestBuilder.buildEnvEntityByID(parentWorld, id, initPos, eScale, eRY);
				if (ent != null)
					region.addEntity(ent);
			}
		}
	}

	public static void saveTestRegionsEnvironment(Region region) {
		String data = "";
		System.out.println(region.getEntities().size());
		for (EntityX ent : region.getEnvironment()) {
			if (ent.getEntityType() == EntityX.TYPE_ENVIRONMENTAL) {
				int id = EntityXTestBuilder.getIdByEntityName(ent.getName());
				if (id == EntityXTestBuilder.STRAWS_ID || id == EntityXTestBuilder.REEDS_ID) {
					PhysicalComponent phy = ent.getPhysicalComponentIfHaving();
					float eX = phy.getPosition().x;
					float eY = phy.getPosition().y;
					float eZ = phy.getPosition().z;
					float eScale = phy.getScale();
					float eRY = phy.getRotation().ry;
					String line = ToolBox.buildGQLine("e", id + "", eX + "", eY + "", eZ + "", eScale + "", eRY + "");
					data += line;
				}
			}
		}
		ToolBox.saveFile(data, TEST_REGION_FILEATH);
	}
}
