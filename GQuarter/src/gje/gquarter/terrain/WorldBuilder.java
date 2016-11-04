package gje.gquarter.terrain;

import gje.gquarter.core.Loader;
import gje.gquarter.models.TerrainTexture;
import gje.gquarter.models.TerrainTexturePack;

public class WorldBuilder {

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

		TerrainTexture blendMap = new TerrainTexture(Loader.loadTextureFiltered("world/blendMaps/" + bmName, true), bmName);
		TerrainTexture rTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + rName, true), rName);
		TerrainTexture gTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + gName, true), gName);
		TerrainTexture bTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + bName, true), gName);
		TerrainTexture aTexture = new TerrainTexture(Loader.loadTextureFiltered("world/textures/" + aName, true), aName);
		TerrainTexturePack pack = new TerrainTexturePack(aTexture, rTexture, gTexture, bTexture);

		Terrain ter = new Terrain(gridX, gridZ, parentWorld.getRegionSize(), parentWorld.getRegionAmplitude(), pack, blendMap, "world/heightMaps/" + hmName, create);

		reg = new Region(id, name, descr, ter);
		return reg;

	}

	// public static World builderWorld(Loader loader, RawModelCompBase base,
	// String path) {
	// /*
	// * 1 - wczytac ustawienia 2 - wczytac region 3 - wczytac chunki z
	// * regionu, itd
	// */
	// String wName = "";
	// String wDescription = "";
	// float wRegionAmplitude = 1;
	// float wRegionSize = 100;
	// int wOffsetX = 0;
	// int wOffsetZ = 0;
	// Light tempSun = new Light(new Vector3f(), new Vector3f());
	// Weather tempWeather = new Weather(tempSun);
	// ArrayList<Region> regions = new ArrayList<Region>();
	//
	// int regionBuilderId = -1;
	// int regionBuilderOffsetX = 0;
	// int regionBuilderOffsetZ = 0;
	// String regionBuilderPath = "";
	//
	// ArrayList<String> listening = ToolBox.loadGQFile("world/world.gq");
	// for (String line : listening) {
	//
	// String[] words = ToolBox.lineSplitterGQFile2Str(line);
	// String head = words[ToolBox.HEAD];
	// String tail = words[ToolBox.TAIL];
	//
	// if (head.equals("name"))
	// wName = tail;
	// if (head.equals("description"))
	// wDescription = tail;
	// if (head.equals("amplitude"))
	// wRegionAmplitude = Float.parseFloat(tail);
	// if (head.equals("regionSize"))
	// wRegionSize = Float.parseFloat(tail);
	// if (head.equals("wXZ")) {
	// String[] values = tail.split(ToolBox.ARGUMENT_SPLITTER);
	// wOffsetX = Integer.parseInt(values[0]);
	// wOffsetZ = Integer.parseInt(values[1]);
	// } else if (head.equals("sunDirection")) {
	// String[] values = tail.split(ToolBox.ARGUMENT_SPLITTER);
	// float sunX = Float.parseFloat(values[0]);
	// float sunY = Float.parseFloat(values[1]);
	// float sunZ = Float.parseFloat(values[2]);
	// tempSun.setTypeDirectional(new Vector3f(sunX, sunY, sunZ));
	// }
	// // region builder:
	// if (head.equals("rId"))
	// regionBuilderId = Integer.parseInt(tail);
	// if (head.equals("rPath"))
	// regionBuilderPath = tail;
	// if (head.equals("rXZ")) {
	// String[] values = tail.split(ToolBox.ARGUMENT_SPLITTER);
	// regionBuilderOffsetX = Integer.parseInt(values[0]);
	// regionBuilderOffsetZ = Integer.parseInt(values[1]);
	// }
	// // blok komend
	// if (head.equals("cmd") && tail.equals("bake")) {
	// Region r = Region.regionBuilder(loader, entityDatabase, regionBuilderId,
	// regionBuilderPath, wOffsetX + regionBuilderOffsetX, wOffsetZ +
	// regionBuilderOffsetZ, wRegionSize, wRegionAmplitude);
	// regions.add(r);
	// }
	// }
	//
	// // przy starcie zeby gracz mial jakikolwiek teren, nie pojawi sie w
	// // normalnej grze
	// // if(player.getRegionContaining() == null){
	// // player.setRegionContaining(regions.get(0));
	// // player.setPosRelativeToterrain(new Vector3f());
	// // }
	//
	// ToolBox.log(World.class, "Loading world done, regions: " + regions.size()
	// + ".");
	// return new World(loader, tempWeather, entityDatabase, wName,
	// wDescription, wRegionSize, wRegionAmplitude, wOffsetX, wOffsetZ,
	// regions);
	// }
	//
	//
	// public static Region regionBuilder(Loader loader, EntityDatabaseLoader
	// entityDatabase, int id, String path, int offsetX, int offsetZ, float
	// terrainSize, float maxAmplitude) {
	// String bmName = "";
	// String hmName = "";
	// String rName = "";
	// String gName = "";
	// String bName = "";
	// String aName = "";
	// Vector3f waterTilePos = new Vector3f();
	// float waterTileSize = 10;
	// float globalX = offsetX * terrainSize;
	// float globalZ = offsetZ * terrainSize;
	// ArrayList<BasicComponent> tempComponents = new
	// ArrayList<BasicComponent>();
	// ArrayList<WaterTile> waterTiles = new ArrayList<WaterTile>();
	//
	// String regName = "", regDescription = "";
	// ArrayList<String> listening = ToolBox.loadGQFile("world/regions/" + path
	// + ".gq");
	// for (String line : listening) {
	// String[] words = ToolBox.lineSplitterGQFile2Str(line);
	// String head = words[ToolBox.HEAD];
	// String tail = words[ToolBox.TAIL];
	//
	// if (head.equals("name"))
	// regName = tail;
	// if (head.equals("description"))
	// regDescription = tail;
	// if (head.equals("bmName"))
	// bmName = tail;
	// if (head.equals("hmName"))
	// hmName = tail;
	// if (head.equals("rName"))
	// rName = tail;
	// if (head.equals("gName"))
	// gName = tail;
	// if (head.equals("bName"))
	// bName = tail;
	// if (head.equals("aName"))
	// aName = tail;
	// if (head.equals("waterXYZ")) {
	// String[] values = tail.split(ToolBox.ARGUMENT_SPLITTER);
	// waterTilePos.set(Float.parseFloat(values[0]),
	// Float.parseFloat(values[1]), Float.parseFloat(values[2]));
	// }
	// if (head.equals("waterSize"))
	// waterTileSize = Float.parseFloat(tail);
	// // TODO obiekty, woda, swiatla
	// if (head.equals("cmd")) {
	// if (tail.equals("bake")) {
	// TerrainTexture blendMap = new
	// TerrainTexture(loader.loadTextureFiltered("world/blendMaps/" + bmName,
	// true));
	// TerrainTexture rTexture = new
	// TerrainTexture(loader.loadTextureFiltered("world/textures/" + rName,
	// true));
	// TerrainTexture gTexture = new
	// TerrainTexture(loader.loadTextureFiltered("world/textures/" + gName,
	// true));
	// TerrainTexture bTexture = new
	// TerrainTexture(loader.loadTextureFiltered("world/textures/" + bName,
	// true));
	// TerrainTexture aTexture = new
	// TerrainTexture(loader.loadTextureFiltered("world/textures/" + aName,
	// true));
	// TerrainTexturePack pack = new TerrainTexturePack(aTexture, rTexture,
	// gTexture, bTexture);
	//
	// Terrain terr = new Terrain(offsetX, offsetZ, terrainSize, maxAmplitude,
	// loader, pack, blendMap, "world/heightMaps/" + hmName);
	// return new Region(id, regName, regDescription, terr, tempComponents,
	// waterTiles);
	// } else if (tail.equals("waterBake")) {
	// WaterTile waterTile = new WaterTile(globalX + waterTilePos.x,
	// waterTilePos.y, globalZ + waterTilePos.z, waterTileSize);
	// waterTiles.add(waterTile);
	// }
	// }
	// }
	// return null;
	// }

}
