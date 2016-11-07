package gje.gquarter.entity;

import gje.gquarter.components.BasicComponent;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.LightComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;

public class EntityX {
	private static final boolean DEBUG_MODE = false;
	private static long debugingTimeNanos;
	private BasicComponent[] components;
	private int cmpsCap;
	private String name;
	private boolean active;

	public EntityX(String name) {
		cmpsCap = BasicComponent.CMP_COUNT;
		components = new BasicComponent[cmpsCap];
		this.name = name;
		this.active = true;
	}

	/** Cap = ID Najwiekszego z componentow!!! */
	public EntityX(int cmpsCap, String name) {
		this.cmpsCap = cmpsCap;
		components = new BasicComponent[cmpsCap];
		this.name = name;
	}

	public void updateEntity(float dt) {
		if (active) {
			if (DEBUG_MODE)
				debugingTimeNanos = System.nanoTime();
			for (int i = 0; i < cmpsCap; ++i) {
				if (components[i] != null) {
					components[i].update(dt);
				}
			}
			if (DEBUG_MODE)
				System.out.println("  E-" + name + ": " + (int) ((debugingTimeNanos = System.nanoTime() - debugingTimeNanos) / 1000l) + "us");
		}
	}

	public void setVisibleIfHaving(boolean visibility) {
		ModelComponent mc = (ModelComponent) getComponent(BasicComponent.CMP_MODEL);
		if (mc != null) {
			if (visibility)
				mc.loadToRenderer();
			else
				mc.removeFromRenderer();
		}
	}

	public ModelComponent getModelComponentIfHaving() {
		return (ModelComponent) getComponent(BasicComponent.CMP_MODEL);
	}

	public PhysicalComponent getPhysicalComponentIfHaving() {
		return (PhysicalComponent) getComponent(BasicComponent.CMP_PHYSICAL);
	}

	public RegionalComponent getRegionalComponentIfHaving() {
		return (RegionalComponent) getComponent(BasicComponent.CMP_REGIONAL);
	}

	public SoundComponent getSoundComponentIfHaving() {
		return (SoundComponent) getComponent(BasicComponent.CMP_SOUND);
	}

	public LightComponent getLightComponentIfHaving() {
		return (LightComponent) getComponent(BasicComponent.CMP_LIGHT);
	}

	public GravityComponent getGravityComponentIfHaving() {
		return (GravityComponent) getComponent(BasicComponent.CMP_GRAVITY);
	}

	private void addComponent(int cmpId, BasicComponent cmp) {
		components[cmpId] = cmp;
	}

	public void addComponent(BasicComponent cmp) {
		int cmpId = BasicComponent.getCmpId(cmp);
		if (cmpId >= 0)
			addComponent(cmpId, cmp);
	}

	public boolean hasComponent(int cmpId) {
		return (components[cmpId] != null);
	}

	public BasicComponent getComponent(int cmpId) {
		return components[cmpId];
	}

	public int getComponentsCap() {
		return cmpsCap;
	}

	public String getName() {
		return name;
	}

	public void setSelect(boolean option) {
		ModelComponent model = getModelComponentIfHaving();
		if (model != null)
			model.setSelected(option);

		SoundComponent snd = getSoundComponentIfHaving();
		if (snd != null)
			snd.setSelect(option);
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
