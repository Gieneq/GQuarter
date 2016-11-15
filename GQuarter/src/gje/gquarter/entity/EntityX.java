package gje.gquarter.entity;

import gje.gquarter.components.BasicComponent;
import gje.gquarter.components.GravityComponent;
import gje.gquarter.components.LightComponent;
import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.components.SoundComponent;

public class EntityX {
	public static final int TYPE_LIVING = EntityRenderer.RENDERER_TYPE;
	public static final int TYPE_ENVIRONMENTAL = EnvironmentRenderer.RENDERER_TYPE;
	private BasicComponent[] components;
	private int cmpsCap;
	private String name;
	private int entityType;

	public EntityX(String name) {
		cmpsCap = BasicComponent.CMP_COUNT;
		components = new BasicComponent[cmpsCap];
		this.name = name;
		this.entityType = TYPE_LIVING;
	}

	public EntityX(String name, int renderingType) {
		cmpsCap = BasicComponent.CMP_COUNT;
		components = new BasicComponent[cmpsCap];
		this.name = name;
		this.entityType = renderingType;
	}

	/** Cap = ID Najwiekszego z componentow!!! */
	public EntityX(int cmpsCap, String name, int renderingType) {
		this.cmpsCap = cmpsCap;
		components = new BasicComponent[cmpsCap];
		this.name = name;
		this.entityType = renderingType;
	}

	public void updateEntity(float dt) {
		if (entityType != TYPE_ENVIRONMENTAL)
			forceUpdate(dt);
	}

	public void forceUpdate(float dt) {
		for (int i = 0; i < cmpsCap; ++i) {
			if (components[i] != null) {
				components[i].update(dt);
			}
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

	public int getEntityType() {
		return entityType;
	}

	public void setEntityType(int renderingType) {
		this.entityType = renderingType;
	}
}
