package gje.gquarter.terrain;

import gje.gquarter.components.RegionalComponent;
import gje.gquarter.entity.EntityX;
import gje.gquarter.entity.PlayerEntity;
import gje.gquarter.toolbox.ToolBox;

import java.util.ArrayList;

public class World {
	public static final String WORLD_FILE_PATH = "world/world.txt";

	private String name;
	private String description;
	private float regionSize;
	private float chunkAmplitude;

	private ArrayList<Region> regionsList;
	private PlayerEntity player;

	private int offsetX;
	private int offsetZ;

	public World(String name, String descr, float regSize, float chunkAmpl, int offsetX, int offsetZ) {
		this.regionsList = new ArrayList<Region>();
		this.name = name;
		this.description = descr;
		this.regionSize = regSize;
		this.chunkAmplitude = chunkAmpl;
		this.offsetX = offsetX;
		this.offsetZ = offsetZ;
		player = null;
	}

	public void addRegion(Region region) {
		regionsList.add(region);
	}

	public PlayerEntity getPlayer() {
		return player;
	}

	public void setPlayer(PlayerEntity player) {
		this.player = player;
	}

	public void update(float dt) {
		for (Region reg : regionsList)
			reg.update(dt);
	}


	public void updateRegionContainingRegionalComp(RegionalComponent cmp) {
		float globalX = cmp.getPosition().x;
		float globalZ = cmp.getPosition().z;

		for (Region reg : regionsList) {
			Terrain t = reg.getTarrain();
			if (globalX >= t.getX() && globalX < t.getX() + regionSize) {
				if (globalZ >= t.getZ() && globalZ < t.getZ() + regionSize) {
					if (cmp.getRegion() != reg) {
						cmp.setRegion(reg);
						ToolBox.log(this, "sth entering chunck: " + cmp.getRegion().getTarrain());
					}
				}
			}
		}
	}

	public Region getRegionContaining(float globalX, float globalZ) {
		for (Region reg : regionsList) {
			Terrain t = reg.getTarrain();
			if (globalX >= t.getX() && globalX < t.getX() + regionSize) {
				if (globalZ >= t.getZ() && globalZ < t.getZ() + regionSize) {
					return reg;
				}
			}
		}
		return null;
	}

	public Region getRegionContaining(RegionalComponent comp) {
		for (Region reg : regionsList) {
			Terrain t = reg.getTarrain();
			if (comp.getPosition().x >= t.getX() && comp.getPosition().x < t.getX() + regionSize) {
				if (comp.getPosition().z >= t.getZ() && comp.getPosition().z < t.getZ() + regionSize) {
					return reg;
				}
			}
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public float getRegionSize() {
		return regionSize;
	}

	public float getRegionAmplitude() {
		return chunkAmplitude;
	}

	public ArrayList<Region> getRegionsList() {
		return regionsList;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetZ() {
		return offsetZ;
	}
}
