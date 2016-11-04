package gje.gquarter.components;

import gje.gquarter.terrain.Region;
import gje.gquarter.terrain.World;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class RegionalComponent implements BasicComponent {
	private static final float TEST_FRICTION_FACTOR_VALUE = 0.6F;
	private World world;
	private Vector3f position;
	private Region region;
	private Vector2f relativePosition;
	private Vector2f normalisedelativePosition;

	public RegionalComponent(Vector3f position, World world) {
		this.world = world;
		this.position = position;
		this.region = world.getRegionContaining(position.x, position.z);
		this.relativePosition = new Vector2f();
		this.normalisedelativePosition = new Vector2f();
	}

	public float getTerrainHeight() {
		return region.getTarrain().getHeightOfTerrainGlobal(position.x, position.z);
	}

	public void getNormal(Vector3f normal) {
		region.getTarrain().getNormal(position.x, position.z, normal);
	}
	
	public void getSliding(Vector3f sliding) {
		region.getTarrain().getSliding(position.x, position.z, sliding);
	}
	
	public float getFrictionFactorValue() {
//		Terrain ter = region.getTarrain();
//		BlendmapPainter.sampleBlendMapPixel(textureX, textureZ, tempSample);
		
		return TEST_FRICTION_FACTOR_VALUE;
	}

	public boolean isAboveTerrain() {
		return (position.y > this.getTerrainHeight());
	}

	@Override
	public void update(float dt) {
		world.updateRegionContainingRegionalComp(this);
		relativePosition.set(position.x - region.getTarrain().x, position.z - region.getTarrain().z);
		normalisedelativePosition.set((position.x / region.getTarrain().getSize()) % 1, (position.z / region.getTarrain().getSize()) % 1);
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public boolean isInsideRegion() {
		float nx = normalisedelativePosition.x;
		float nz = normalisedelativePosition.y;
		return ((nx >= 0) && (nx <= 1) && (nz >= 0) && (nz <= 1));
	}

	public Vector2f getRelativePosition() {
		return relativePosition;
	}

	public Vector2f getNormalisedelativePosition() {
		return normalisedelativePosition;
	}

	public Vector3f getPosition() {
		return position;
	}
}
