package gje.gquarter.components;

public interface BasicComponent {
	public static final int CMP_PHYSICAL = 0;
	public static final int CMP_REGIONAL = 1;
	public static final int CMP_GRAVITY = 2;
	public static final int CMP_MODEL = 3;
	public static final int CMP_CONTROL = 4;
	public static final int CMP_LIGHT = 5;
	public static final int CMP_SOUND = 6;
	public static final int CMP_BOUNDING = 7;

	/*
	 * !! zmienic to dopisac instanceof w metodzie ponizej TODO zrobic cos z tym
	 * bo to glupie, jakis enum ...
	 */
	public static final int CMP_COUNT = 8;

	/*
	 * UWAGA na count :p
	 */
	public static int getCmpId(BasicComponent cmp) {
		int cmpId = -1;
		if (cmp instanceof PhysicalComponent)
			cmpId = BasicComponent.CMP_PHYSICAL;
		if (cmp instanceof RegionalComponent)
			cmpId = BasicComponent.CMP_REGIONAL;
		if (cmp instanceof GravityComponent)
			cmpId = BasicComponent.CMP_GRAVITY;
		if (cmp instanceof ModelComponent)
			cmpId = BasicComponent.CMP_MODEL;
		if (cmp instanceof ControlComponent)
			cmpId = BasicComponent.CMP_CONTROL;
		if (cmp instanceof LightComponent)
			cmpId = BasicComponent.CMP_LIGHT;
		if (cmp instanceof SoundComponent)
			cmpId = BasicComponent.CMP_SOUND;
		if (cmp instanceof BoundingComponent)
			cmpId = BasicComponent.CMP_BOUNDING;
		return cmpId;
	}

	public abstract void update(float dt);
}
