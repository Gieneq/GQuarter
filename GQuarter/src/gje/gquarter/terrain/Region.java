package gje.gquarter.terrain;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.entity.EntityX;
import gje.gquarter.entity.EnvironmentRenderer;
import gje.gquarter.events.OnCameraUpdateListener;
import gje.gquarter.water.WaterRenderer;
import gje.gquarter.water.WaterTile;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Region implements OnCameraUpdateListener {
	private int id;
	private String name;
	private String description;

	private Terrain tarrain;
	private List<EntityX> environment;
	private List<EntityX> entities;

	private List<WaterTile> waterTiles;

	public Region(int id, String name, String description, Terrain tarrain) {
		this.id = id;
		this.description = description;
		this.name = name;
		this.tarrain = tarrain;
		this.entities = new ArrayList<EntityX>();
		this.environment = new ArrayList<EntityX>();
		this.waterTiles = new ArrayList<WaterTile>();
		MainRenderer.getSelectedCamera().addUpdatedListener(this);
	}

	public void addWaterTile(WaterTile wt) {
		waterTiles.add(wt);
		WaterRenderer.getWaterTiles().add(wt);
		updateFrustumCulling(); // TODO... TO NIE OGARNIA WODY -,-
	}

	public WaterTile getIntersectingWaterTile(float x, float z) {
		for (WaterTile wt : waterTiles) {
			float halfSize = wt.getTileSize();
			float cx = wt.getCenterPosition().x;
			float cz = wt.getCenterPosition().z;
			if ((x > cx - halfSize) && (x < cx + halfSize) && (z > cz - halfSize) && (z < cz + halfSize))
				return wt;
		}
		return null;
	}

	public void removeRangeOfEntities(float cx, float cz, float radius) {
		ArrayList<EntityX> batchToRemove = new ArrayList<EntityX>();

		for (int i = 0; i < environment.size(); ++i) {
			EntityX ee = environment.get(i);
			Vector3f envPos = ee.getPhysicalComponentIfHaving().getPosition();
			float dx = envPos.x - cx;
			float dz = envPos.z - cz;
			if (dx * dx + dz * dz < radius * radius)
				batchToRemove.add(ee);
		}
		for (EntityX ex : batchToRemove)
			removeEntity(ex);
	}

	public void addEntity(EntityX ex) {
		ModelComponent mCmp = ex.getModelComponentIfHaving();
		if (mCmp != null) {
			if (mCmp.getRendererType() == EnvironmentRenderer.RENDERER_TYPE)
				environment.add(ex);
			else
				entities.add(ex);
		} else
			entities.add(ex);
		// tu w razie potrzeby laduje do renderera
		updateFrustumCulling();

	}

	public EntityX getClosestEnvEntity(float x, float z, float radius) {
		// TODO TU B SIE PRZYDAL QUADTREE I JAKIES SORTOWANIE :/
		for (EntityX e : environment) {
			Vector3f pos = e.getPhysicalComponentIfHaving().getPosition();
			float dx = pos.x - x;
			float dz = pos.z - z;
			if ((dx * dx + dz * dz) < (radius * radius))
				return e;
		}
		return null;
	}

	public void removeEntity(EntityX ex) {
		if (ex != null) {
			ModelComponent mc = ex.getModelComponentIfHaving();
			if (mc != null) {
				if (mc.getRendererType() == EnvironmentRenderer.RENDERER_TYPE)
					environment.remove(ex);
				else
					entities.remove(ex);
				mc.removeFromRenderer();
			} else
				entities.remove(ex);
			SoundComponent sc = ex.getSoundComponentIfHaving();
			if (sc != null)
				sc.remove();
		}
		updateFrustumCulling();
	}

	public int getAllEntitiesCount() {
		return environment.size() + entities.size();
	}

	public void update(float dt) {
		for (EntityX env : environment)
			env.updateEntity(dt);
		for (EntityX ex : entities)
			ex.updateEntity(dt);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Terrain getTarrain() {
		return tarrain;
	}

	public List<EntityX> getEntities() {
		return entities;
	}

	public List<EntityX> getEnvironment() {
		return environment;
	}

	public List<WaterTile> getWaterTiles() {
		return waterTiles;
	}

	public void getNeighbouringRegions() {
		// ///////////////////////////////////////////
	}

	@Override
	public void onCameraUpdate(float dt) {
		updateFrustumCulling();
	}

	public void updateFrustumCulling() {
		Camera cam = MainRenderer.getSelectedCamera();

		for (EntityX env : environment) {
			// long debugingTimeNanos = System.nanoTime();
			// System.out.println("Region ---" + ": " + (int)
			// ((debugingTimeNanos = System.nanoTime() - debugingTimeNanos) /
			// 1000l) + "us");

			ModelComponent mc = env.getModelComponentIfHaving();

			if (mc != null) {
				boolean isIn = cam.isInsideViewFrustrum(mc.getMassCenterSCaled(), mc.getBoundingSphereRadiusScaled());
				if (!mc.isLoaded() && isIn)
					mc.loadToRenderer();
				if (mc.isLoaded() && !isIn)
					mc.removeFromRenderer();
			}
		}

		for (EntityX ex : entities) {
			ModelComponent mc = ex.getModelComponentIfHaving();
			if (mc != null) {
//				boolean isIn = cam.isIntersectingSweepSphere(mc);
				boolean isIn = true;
				if (!mc.isLoaded() && isIn)
					mc.loadToRenderer();
				if (mc.isLoaded() && !isIn)
					mc.removeFromRenderer();
			}
		}
		getTarrain().updateFrustumCulling();
	}
}
