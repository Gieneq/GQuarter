package gje.gquarter.components;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import gje.gquarter.bilboarding.SunRenderer;
import gje.gquarter.entity.Light;

public class LightComponent implements BasicComponent {

	private Vector3f relativePosition;
	private Light lightSource;
	private PhysicalComponent parentPhyComp;
	private static Vector4f tVec = new Vector4f();

	public LightComponent(PhysicalComponent parentPhyComp, Light lightSource, Vector3f relativePosition) {
		this.lightSource = lightSource;
		this.parentPhyComp = parentPhyComp;
		this.relativePosition = relativePosition;
	}

	@Override
	public void update(float dt) {
		// przesuniecie w ukldzie lokalnym rodzica
		tVec.set(relativePosition.x, relativePosition.y, relativePosition.z, 1f);
		Matrix4f.transform(parentPhyComp.getModelMatrix(), tVec, tVec);
		lightSource.getPosition().set(tVec.x, tVec.y, tVec.z);
	}
}
