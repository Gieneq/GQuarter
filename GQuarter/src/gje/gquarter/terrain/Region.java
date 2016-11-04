package gje.gquarter.terrain;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.SoundComponent;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.entity.EntityX;
import gje.gquarter.events.OnCameraUpdateListener;
import gje.gquarter.water.WaterRenderer;
import gje.gquarter.water.WaterTile;

import java.util.ArrayList;
import java.util.List;

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

	public void addLivingEntity(EntityX ex) {
		entities.add(ex);
		updateFrustumCulling();
	}

	public void addWaterTile(WaterTile wt) {
		waterTiles.add(wt);
		WaterRenderer.getWaterTiles().add(wt);
		updateFrustumCulling(); // TODO... TO NIE OGARNIA WODY -,-
	}

	public void addEnvironmentEntity(EntityX ex) {
		environment.add(ex);
		updateFrustumCulling();
	}

	public EntityX getClosestEnvEntity(float x, float z, float radius) {
		// TODO TU B SIE PRZYDAL QUADTREE I JAKIES SORTOWANIE :/
		for (EntityX e : environment) {
			float dx = e.getPhysicalComponentIfHaving().getPosition().x - x;
			float dz = e.getPhysicalComponentIfHaving().getPosition().z - z;
			if ((dx * dx + dz * dz) < (radius * radius))
				return e;
		}
		return null;
	}

	public void removeEnvironmentEntity(EntityX ex) {
		if (ex != null) {
			environment.remove(ex);
			ModelComponent mc = ex.getModelComponentIfHaving();
			if (mc != null)
				EntityRenderer.remove(mc);
			SoundComponent sc = ex.getSoundComponentIfHaving();
			if (sc != null)
				sc.remove();
		}
		updateFrustumCulling();
	}

	public int getEntitiesCount() {
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
			long debugingTimeNanos = System.nanoTime();
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
				boolean isIn = cam.isIntersectingSweepSphere(mc);
				if (!mc.isLoaded() && isIn)
					mc.loadToRenderer();
				if (mc.isLoaded() && !isIn)
					mc.removeFromRenderer();
			}
		}
		getTarrain().updateFrustumCulling();
	}
}
