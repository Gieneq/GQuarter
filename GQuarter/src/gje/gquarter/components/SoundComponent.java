package gje.gquarter.components;

import gje.gquarter.audio.AudioMain;
import gje.gquarter.audio.Source;
import gje.gquarter.boundings.BoundingSphere;
import gje.gquarter.boundings.BoundingsRenderer;
import gje.gquarter.toolbox.Maths;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class SoundComponent implements BasicComponent {
	public static final Vector4f MIN_SHPHERE_COLOR = Maths.convertColor4f(80, 180, 20, 255);
	public static final Vector4f MAX_SHPHERE_COLOR = Maths.convertColor4f(125, 10, 95, 255);
	private Source soundSource;
	private BoundingSphere minSphere;
	private BoundingSphere maxSphere;
	private Vector3f sphereTranslation;
	private PhysicalComponent parent;
	private Vector3f lastParentPosition;
	private boolean boundingVisibility;

	public SoundComponent(PhysicalComponent parent, int musicId) {
		this.parent = parent;
		this.lastParentPosition = new Vector3f(parent.getPosition());
		soundSource = new Source(parent.getPosition());
		soundSource.setAttenuationParams(1, 1.7f, 9);
		soundSource.play(musicId);
		soundSource.pause();

		sphereTranslation = new Vector3f();
		minSphere = new BoundingSphere(parent, sphereTranslation, soundSource.getMinDistance());
		minSphere.getColor().set(MIN_SHPHERE_COLOR);
		maxSphere = new BoundingSphere(parent, sphereTranslation, soundSource.getMaxDistance());
		maxSphere.getColor().set(MAX_SHPHERE_COLOR);

		setBoundingVisibility(true);
		update(0f);
	}

	@Override
	public void update(float dt) {
		if (parent.getPosition().lengthSquared() != lastParentPosition.lengthSquared()) {
			soundSource.setPosition(parent.getPosition().x + sphereTranslation.x, parent.getPosition().y + sphereTranslation.y, parent.getPosition().z + sphereTranslation.z);
		}
		minSphere.forceMultiMatrixUpdate();
		maxSphere.forceMultiMatrixUpdate();
		lastParentPosition.set(parent.getPosition());
	}

	public Source getSoundSource() {
		return soundSource;
	}

	public boolean isBoundingVisibile() {
		return boundingVisibility;
	}

	public void remove() {
		setBoundingVisibility(false);
		AudioMain.removeSource(soundSource);
	}

	public void setBoundingVisibility(boolean boundingVisibility) {
		if (this.boundingVisibility && !boundingVisibility) {
			BoundingsRenderer.remove(minSphere);
			BoundingsRenderer.remove(maxSphere);
		} else if (!this.boundingVisibility && boundingVisibility) {
			BoundingsRenderer.load(minSphere);
			BoundingsRenderer.load(maxSphere);
		}
		this.boundingVisibility = boundingVisibility;
	}

	public void setMinRange(float min) {
		soundSource.setReferenceValue(min);
		minSphere.setRadius(min);
	}

	public void setMaxRange(float max) {
		soundSource.setMaxValue(max);
		maxSphere.setRadius(max);
	}

	public void setSelect(boolean option) {
		if (isBoundingVisibile()) {
			minSphere.setSelect(option);
			maxSphere.setSelect(option);
		} else {
			minSphere.setSelect(false);
			maxSphere.setSelect(false);
		}
	}
}
