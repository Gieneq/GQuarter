package gje.gquarter.components;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.entity.EnvironmentRenderer;
import gje.gquarter.models.TexturedModel;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Rotation3f;

public class ModelComponent extends RawModelComponent {

	private PhysicalComponent physical;
	private Vector3f meshOffset;
	private Rotation3f meshRotation;
	private float scale;
	private Matrix4f multiMatrix;
	private static Rotation3f rotTotal = new Rotation3f();
	private static Vector3f posTotal = new Vector3f();
	private static Vector4f posTotalPrim = new Vector4f();
	private Vector3f massCenterScaled = new Vector3f();
	private boolean loaded;
	private boolean selected;
	private int rendererType;

	public ModelComponent(TexturedModel model, int atlasIndex, PhysicalComponent physical) {
		super(model, atlasIndex);
		this.physical = physical;
		this.meshOffset = new Vector3f();
		this.meshRotation = new Rotation3f();
		this.scale = 1;
		updateBoundingSphereParams();
		multiMatrix = new Matrix4f();
		loaded = false;
		selected = false;
		rendererType = EntityRenderer.RENDERER_TYPE;
	}

	public ModelComponent(TexturedModel model, int atlasIndex, PhysicalComponent physical, Vector3f meshOffset, Rotation3f meshRotation, float scale) {
		super(model, atlasIndex);
		this.meshOffset = meshOffset;
		this.meshRotation = meshRotation;
		this.scale = scale;
		this.physical = physical;
		updateBoundingSphereParams();
		multiMatrix = new Matrix4f();
		loaded = false;
		selected = false;
		rendererType = EntityRenderer.RENDERER_TYPE;
	}

	@Override
	public void update(float dt) {
		updateBoundingSphereParams();
		forceMultiMatrixUpdate();
	}

	public void loadToRenderer() {
		if (!loaded) {
			if(rendererType == EntityRenderer.RENDERER_TYPE)
				EntityRenderer.loadEntityXModelComponent(this);
			else
				EnvironmentRenderer.loadEntityXModelComponent(this);
			loaded = true;
		}
	}

	public void removeFromRenderer() {
		if (loaded) {
			if(rendererType == EntityRenderer.RENDERER_TYPE)
				EntityRenderer.remove(this);
			else
				EnvironmentRenderer.remove(this);
			loaded = false;
		}
	}

	public float getBoundingSphereRadiusScaled() {
		return super.getModel().getRawModel().getBoundingSphereRadius() * scale;
	}

	public Vector3f getMassCenterSCaled() {
		return massCenterScaled;
	}

	private void updateBoundingSphereParams() {
		this.massCenterScaled.set(super.getModel().getRawModel().getMassCenter());
		this.massCenterScaled.scale(scale);
		Vector3f.add(massCenterScaled, physical.getPosition(), massCenterScaled); // ???napewno_dodac?
	}

	public void forceMultiMatrixUpdate() {
		float scaleTotal = physical.getScale() * scale;

		rotTotal.rx = physical.getRotation().rx + meshRotation.rx;
		rotTotal.ry = physical.getRotation().ry + meshRotation.ry;
		rotTotal.rz = physical.getRotation().rz + meshRotation.rz;

		/*
		 * Tu mam sobie wektorek wyskalowany lokalnego przesuniecia i rotacji
		 */
		posTotalPrim.set(meshOffset.x, meshOffset.y, meshOffset.z, 1f);
		posTotal.set(0f, 0f, 0f);
		Maths.createTransformationMatrix(posTotal, meshRotation, physical.getScale(), multiMatrix);
		Matrix4f.transform(multiMatrix, posTotalPrim, posTotalPrim);

		/*
		 * Teraz muze dodac wektorki, gdzie physical.pos nie skalujemy
		 */
		posTotal.set(physical.getPosition().x + posTotalPrim.x, physical.getPosition().y + posTotalPrim.y, physical.getPosition().z + posTotalPrim.z);

		/*
		 * I stworzyc z tego nowa macierz transformacji
		 */
		Maths.createTransformationMatrix(posTotal, rotTotal, scaleTotal, multiMatrix);
	}

	public Matrix4f getMultiModelMatrix() {
		return multiMatrix;
	}

	public PhysicalComponent getPhysicalComp() {
		return physical;
	}

	public Vector3f getMeshOffset() {
		return meshOffset;
	}

	public Rotation3f getMeshRotation() {
		return meshRotation;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getRendererType() {
		return rendererType;
	}

	public void setRendererType(int rendererType) {
		this.rendererType = rendererType;
	}
}
