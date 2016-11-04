package gje.gquarter.entity;

import gje.gquarter.components.ModelComponent;
import gje.gquarter.components.PhysicalComponent;
import gje.gquarter.components.RegionalComponent;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.events.OnCameraUpdateListener;
import gje.gquarter.gui.event.Key;
import gje.gquarter.gui.event.OnKeyEventListener;
import gje.gquarter.terrain.World;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.Plane;
import gje.gquarter.toolbox.Rotation3f;
import gje.gquarter.toolbox.ToolBox;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Camera implements OnCameraUpdateListener, OnKeyEventListener {
	private static final String FILEPATH = "world/entities/camera.gq";
	private static final int PLANE_BACK = 0;
	private static final int PLANE_FRONT = 1;
	private static final int PLANE_LEFT = 2;
	private static final int PLANE_RIGHT = 3;
	private static final int PLANE_TOP = 4;
	private static final int PLANE_BOTTOM = 5;

	private static final float MAX_ZOOM = 11.9F;
	private static final float MIN_ZOOM = 0.9F;
	private static final float MIN_HEIGHT_ABOVE_TERRAIN = 0.4F;

	private static final float MOUSE_CAMERA_X = 0.1F;
	private static final float MOUSE_CAMERA_Y = 0.07F;
	private static final float MOUSE_CAMERA_ZOOM = 5F;

	private static final float MIN_VELOCITY_VALUE_TO_UPDATE = 0.9F;
	private static final float CUTTOFF_ANGLE = Maths.toRadians(80);

	private static final float SWEEP_SPHERE_RADIUS_SQUARED = (float) Math.pow(16d, 2d);
	private static final float MINIMAL_SQUARED_DISPLACEMENT_TO_UPDATE_FRUSTUM = (float) Math.pow(0.8d, 2d);
	private static final float MINIMAL_SQUARED_ROTATION_TO_UPDATE_FRUSTUM = (float) Math.pow(0.01d, 2d);

	private RegionalComponent regionalComp;
	private float zoom;
	private float zoomVelocity;
	private boolean infZoom;
	private float pitch;
	private float pitchVelocity;
	private float yaw;
	private float yawVelocity;

	private float elevation;

	private Vector3f observed;
	private Matrix4f viewMatrix;
	private Vector3f tempTranslator;
	private Rotation3f tempRatator;
	private Vector3f viewNormal;
	private Vector3f topNormal;
	private Vector3f sideNormal;
	private Vector3f tNormal, tPoint;
	private Plane[] frustrumPlanes;
	private Vector3f lastUpdatedPosition;
	private Rotation3f rotationAngles;
	private Rotation3f lastUpdatedRotationAngles;
	private Key resetKey;

	private List<OnCameraUpdateListener> cameraUpdatedListeners;

	/**
	 * ten model component to ma byc kiedys zlozonym ksztaltem, z jakims punktem
	 * skupienia kamery
	 */
	public Camera(Vector3f observedPoint, World world, boolean infZoom) {
		this.observed = observedPoint;
		this.infZoom = infZoom;

		viewNormal = new Vector3f(0f, 0f, 1f);
		topNormal = new Vector3f(0f, 1f, 0f);
		sideNormal = new Vector3f(1f, 0f, 0f);
		tNormal = new Vector3f();
		tPoint = new Vector3f();
		regionalComp = new RegionalComponent(new Vector3f(), world);
		this.viewMatrix = new Matrix4f();
		this.tempTranslator = new Vector3f();
		this.tempRatator = new Rotation3f();

		resetCamera();
		this.elevation = 0f;

		updateViewMatrix();

		frustrumPlanes = new Plane[6];
		for (int i = 0; i < 6; ++i)
			frustrumPlanes[i] = new Plane();
		lastUpdatedPosition = new Vector3f(getPosition());
		rotationAngles = new Rotation3f();
		lastUpdatedRotationAngles = new Rotation3f(getPitch(), getYaw(), getRoll());

		cameraUpdatedListeners = new ArrayList<OnCameraUpdateListener>();
		addUpdatedListener(this);
		resetKey = new Key(Keyboard.KEY_R);
		resetKey.setOnClickListener(this);
	}

	public void update(float dt) {
		regionalComp.update(dt);
		move(dt);
		updateViewMatrix();
		updateCamerasListeners(dt);
	}

	/** Checking if camera needs an update and enables all connected listeners. */
	private void updateCamerasListeners(float dt) {
		/*
		 * Conditions
		 */
		boolean needs = false;
		Vector3f.sub(getPosition(), lastUpdatedPosition, tempTranslator);
		Rotation3f.sub(getRotationAngles(), lastUpdatedRotationAngles, tempRatator);

		if (tempTranslator.lengthSquared() > MINIMAL_SQUARED_DISPLACEMENT_TO_UPDATE_FRUSTUM)
			needs = true;
		if (tempRatator.lengthSquared() > MINIMAL_SQUARED_ROTATION_TO_UPDATE_FRUSTUM)
			needs = true;

		if (needs) {
			lastUpdatedPosition.set(getPosition());
			lastUpdatedRotationAngles = new Rotation3f(getPitch(), getYaw(), getRoll());
			/** This Camera is also stored in the List */
			for (OnCameraUpdateListener listener : cameraUpdatedListeners)
				listener.onCameraUpdate(dt);
		}
	}

	public float getDistSquaredToObservedPoint() {
		float ddx = getPosition().x - observed.x;
		float ddy = getPosition().y - observed.y;
		float ddz = getPosition().z - observed.z;
		return ddx * ddx + ddy * ddy + ddz * ddz;
	}

	public float getDistToObservedPoint() {
		return (float) Math.sqrt(getDistSquaredToObservedPoint());
	}

	public void addUpdatedListener(OnCameraUpdateListener nCameraUpdateListener) {
		cameraUpdatedListeners.add(nCameraUpdateListener);
	}

	public boolean isIntersectingSweepSphere(ModelComponent cmp) {
		Vector3f.sub(regionalComp.getPosition(), cmp.getMassCenterSCaled(), tempTranslator);
		float distSqred = tempTranslator.lengthSquared();
		float rad = cmp.getBoundingSphereRadiusScaled();
		return (distSqred <= SWEEP_SPHERE_RADIUS_SQUARED + rad * rad);
	}

	public boolean isIntersectingSweepSphere(Vector3f pos, float collidingSphereRadius) {
		Vector3f.sub(regionalComp.getPosition(), pos, tempTranslator);
		float distSqred = tempTranslator.lengthSquared();
		return (distSqred <= SWEEP_SPHERE_RADIUS_SQUARED + collidingSphereRadius * collidingSphereRadius);
	}

	public void updateViewFrustrumPlanes() {
		float widthHeight = (float) (Math.tan(MainRenderer.getFov() / 1.0d) * MainRenderer.getNearPlane());
		topNormal.set(0f, 1f, 0f);
		Vector3f.cross(viewNormal, topNormal, sideNormal);
		sideNormal.normalise();
		Vector3f.cross(sideNormal, viewNormal, topNormal);
		topNormal.normalise();

		calculateBackPlane();
		calculateFrontPlane();
		calculateLeftPlane(widthHeight);
		calculateRightPlane(widthHeight);
		calculateTopPlane(widthHeight);
		calculateBottomPlane(widthHeight);

	}

	private void calculateBackPlane() {
		tNormal.set(-viewNormal.x, -viewNormal.y, -viewNormal.z);
		tPoint.set(getPosition());
		tPoint.x += MainRenderer.getNearPlane() * tNormal.x;
		tPoint.y += MainRenderer.getNearPlane() * tNormal.y;
		tPoint.z += MainRenderer.getNearPlane() * tNormal.z;
		frustrumPlanes[PLANE_BACK].updatePlane(tNormal, tPoint);
	}

	private void calculateFrontPlane() {
		tNormal.set(viewNormal);
		tPoint.set(getPosition());
		tPoint.x += MainRenderer.getFarPlane() * tNormal.x;
		tPoint.y += MainRenderer.getFarPlane() * tNormal.y;
		tPoint.z += MainRenderer.getFarPlane() * tNormal.z;
		frustrumPlanes[PLANE_FRONT].updatePlane(tNormal, tPoint);
	}

	private void calculateLeftPlane(float width) {
		tNormal.set(viewNormal);
		tPoint.set(getPosition());
		tPoint.x += MainRenderer.getNearPlane() * viewNormal.x - width * sideNormal.x;
		tPoint.y += MainRenderer.getNearPlane() * viewNormal.y - width * sideNormal.y;
		tPoint.z += MainRenderer.getNearPlane() * viewNormal.z - width * sideNormal.z;
		// point - punkt na plaszczynie, teraz licze wektor wzdloz plaszczyzny
		tNormal.x = tPoint.x - getPosition().x;
		tNormal.y = tPoint.y - getPosition().y;
		tNormal.z = tPoint.z - getPosition().z;
		tNormal.normalise();
		Vector3f.cross(topNormal, tNormal, tNormal);
		frustrumPlanes[PLANE_LEFT].updatePlane(tNormal, tPoint);
	}

	private void calculateRightPlane(float width) {
		tNormal.set(viewNormal);
		tPoint.set(getPosition());
		tPoint.x += MainRenderer.getNearPlane() * viewNormal.x + width * sideNormal.x;
		tPoint.y += MainRenderer.getNearPlane() * viewNormal.y + width * sideNormal.y;
		tPoint.z += MainRenderer.getNearPlane() * viewNormal.z + width * sideNormal.z;
		// point - punkt na plaszczynie, teraz licze wektor wzdloz plaszczyzny
		tNormal.x = tPoint.x - getPosition().x;
		tNormal.y = tPoint.y - getPosition().y;
		tNormal.z = tPoint.z - getPosition().z;
		tNormal.normalise();
		Vector3f.cross(tNormal, topNormal, tNormal);
		frustrumPlanes[PLANE_RIGHT].updatePlane(tNormal, tPoint);
	}

	private void calculateTopPlane(float width) {
		tNormal.set(viewNormal);
		tPoint.set(getPosition());
		tPoint.x += MainRenderer.getNearPlane() * viewNormal.x + width * topNormal.x;
		tPoint.y += MainRenderer.getNearPlane() * viewNormal.y + width * topNormal.y;
		tPoint.z += MainRenderer.getNearPlane() * viewNormal.z + width * topNormal.z;
		// point - punkt na plaszczynie, teraz licze wektor wzdloz plaszczyzny
		tNormal.x = -(tPoint.x - getPosition().x);
		tNormal.y = -(tPoint.y - getPosition().y);
		tNormal.z = -(tPoint.z - getPosition().z);
		tNormal.normalise();
		Vector3f.cross(tNormal, sideNormal, tNormal);
		frustrumPlanes[PLANE_TOP].updatePlane(tNormal, tPoint);
	}

	private void calculateBottomPlane(float width) {
		tNormal.set(viewNormal);
		tPoint.set(getPosition());
		tPoint.x += MainRenderer.getNearPlane() * viewNormal.x - width * topNormal.x;
		tPoint.y += MainRenderer.getNearPlane() * viewNormal.y - width * topNormal.y;
		tPoint.z += MainRenderer.getNearPlane() * viewNormal.z - width * topNormal.z;
		tNormal.x = -(tPoint.x - getPosition().x);
		tNormal.y = -(tPoint.y - getPosition().y);
		tNormal.z = -(tPoint.z - getPosition().z);
		tNormal.normalise();
		Vector3f.cross(sideNormal, tNormal, tNormal);
		frustrumPlanes[PLANE_BOTTOM].updatePlane(tNormal, tPoint);
	}

	public boolean isInsideViewFrustrum(Vector3f point) {
		// jezeli po ktorejs sie nie zgadza to koniec :/
		for (int i = 0; i < 6; ++i) {
			if (!frustrumPlanes[i].isOnRightSide(point))
				return false;
		}
		return true;
	}

	public boolean isInsideViewFrustrum(Vector3f point, float boundingSphereRadius) {
		// jezeli po ktorejs sie nie zgadza to koniec :/
		for (int i = 0; i < 6; ++i) {
			if (!frustrumPlanes[i].isOnRightSide(point, boundingSphereRadius))
				return false;
		}
		return true;
	}

	private void move(float dt) {
		updateAnglesValues(dt);
		calculateVelocities(dt);

		calculateCameraPosition();
		calculateFrontNormal();
		calculateTerrainColision();
	}

	private void calculateVelocities(float dt) {
		/*
		 * YAW
		 */
		if (Mouse.isButtonDown(1))
			yawVelocity = Mouse.getDX() * MOUSE_CAMERA_X;
		else
			yawVelocity = 0f;

		/*
		 * PITCH
		 */
		if (Mouse.isButtonDown(1))
			pitchVelocity = -Mouse.getDY() * MOUSE_CAMERA_Y;
		else
			pitchVelocity = 0f;

		/*
		 * ZOOM
		 */
		zoomVelocity = Mouse.getDWheel() * MOUSE_CAMERA_ZOOM * 0.1f;
	}

	private void updateAnglesValues(float dt) {
		/*
		 * YAW
		 */
		yaw += yawVelocity * dt;
		yaw %= Maths.PI2;

		/*
		 * PITCH
		 */
		pitch += pitchVelocity * dt;
		if (pitch < -CUTTOFF_ANGLE) {
			pitch = -CUTTOFF_ANGLE;
		} else if (pitch > CUTTOFF_ANGLE) {
			pitch = CUTTOFF_ANGLE;
		}

		/*
		 * ZOOM
		 */
		zoom += zoomVelocity * dt;
		if (zoomVelocity * zoomVelocity < MIN_VELOCITY_VALUE_TO_UPDATE)
			zoomVelocity = 0f;
		if (!infZoom) {
			if (zoom < MIN_ZOOM)
				zoom = MIN_ZOOM;
			else if (zoom > MAX_ZOOM)
				zoom = MAX_ZOOM;
		}
	}

	private void calculateCameraPosition() {
		float theta = Maths.PI - yaw;
		float horizontalDistance = (float) (zoom * Math.cos(pitch));
		float verticalDistance = (float) (zoom * Math.sin(pitch));

		float offsetX = (float) (horizontalDistance * Math.sin(theta));
		float offsetZ = (float) (horizontalDistance * Math.cos(theta));

		regionalComp.getPosition().x = observed.x - offsetX;
		regionalComp.getPosition().z = observed.z - offsetZ;
		regionalComp.getPosition().y = observed.y + elevation + verticalDistance;
	}

	public void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate(this.getPitch(), Maths.ROTATOR_X, viewMatrix, viewMatrix);// x
		Matrix4f.rotate(this.getYaw(), Maths.ROTATOR_Y, viewMatrix, viewMatrix);
		Matrix4f.rotate(this.getRoll(), Maths.ROTATOR_Z, viewMatrix, viewMatrix);// z
		tempTranslator.x = -regionalComp.getPosition().x;
		tempTranslator.y = -regionalComp.getPosition().y;
		tempTranslator.z = -regionalComp.getPosition().z;
		Matrix4f.translate(tempTranslator, viewMatrix, viewMatrix);
	}

	public void calculateFrontNormal() {
		viewNormal.x = observed.x - this.getPosition().x;
		viewNormal.y = observed.y - this.getPosition().y;
		viewNormal.z = observed.z - this.getPosition().z;
		viewNormal.normalise();
	}

	private void calculateTerrainColision() {
		float minHeight = regionalComp.getTerrainHeight() + MIN_HEIGHT_ABOVE_TERRAIN;
		if (regionalComp.getPosition().y < minHeight)
			regionalComp.getPosition().y = minHeight;
	}

	/*
	 * INTERFACE -----------------
	 */

	public void invertPitch() {
		this.pitch = -pitch;
	}

	/*
	 * AKCESORY ------------------
	 */

	public Vector3f getObserved() {
		return observed;
	}

	/** Returns previously calculated view matrix */
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}
	
	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}

	/** Not supported! */
	public float getRoll() {
		return 0f;
	}

	public boolean isInfZoom() {
		return infZoom;
	}

	public Vector3f getPosition() {
		return regionalComp.getPosition();
	}

	public RegionalComponent getRegional() {
		return regionalComp;
	}

	public Vector3f getFrontNormal() {
		return viewNormal;
	}

	public Vector3f getSideNormal() {
		return sideNormal;
	}

	public Vector3f getTopNormal() {
		return topNormal;
	}

	private Rotation3f getRotationAngles() {
		rotationAngles.setRotation(getPitch(), getYaw(), getRoll());
		return rotationAngles;
	}

	public void resetCamera() {
		this.pitch = Maths.toRadians(35); // patrze w przod
		this.pitchVelocity = 0f;
		this.yaw = Maths.toRadians(0); // patrz wzdloz x
		this.yawVelocity = 0f;
		this.zoom = 5f;
		this.zoomVelocity = 0f;
		if (regionalComp.getRegion() != null)
			update(0f);
	}
	
	public static Camera loadCamera(Vector3f observedPoint, World world) {
		ArrayList<String> raw = ToolBox.loadGQFile(FILEPATH);
		float camZoom = 0f;
		float camPitch = 0f;
		float camYaw = 0f;
		boolean camInf = false;
		
		for (String line : raw) {
			String[] args = ToolBox.splitGQLine(line);
			if (args[0].startsWith("zoom")) {
				camZoom = Float.parseFloat(args[1]);
			}
			if (args[0].startsWith("pitch")) {
				camPitch = Float.parseFloat(args[1]);
			}
			if (args[0].startsWith("yaw")) {
				camYaw = Float.parseFloat(args[1]);
			}
			if (args[0].startsWith("inf")) {
				camInf = ToolBox.parseGQLogic(args[1]);
			}
		}
		Camera cam = new Camera(observedPoint, world, camInf);
		cam.setZoom(camZoom);
		cam.setPitch(camPitch);
		cam.setYaw(camYaw);
		cam.updateViewMatrix();
		return cam;
	}
	
	public static void saveCamera(Camera cam) {
		String camData = "";
		camData += ToolBox.buildGQLine("zoom", cam.getZoom() + "");
		camData += ToolBox.buildGQLine("pitch", cam.getPitch() + "");
		camData += ToolBox.buildGQLine("yaw", cam.getYaw() + "");
		camData += ToolBox.buildGQLine("inf", ToolBox.inverseGQLogic(cam.isInfZoom()));
		
		ToolBox.saveFile(camData, FILEPATH);
	}

	@Override
	public void onCameraUpdate(float dt) {
		updateViewFrustrumPlanes();
	}

	@Override
	public boolean onKeyClick(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyPress(int keyId) {
		return false;
	}

	@Override
	public boolean onKeyRelease(int keyId) {
		if (keyId == resetKey.getKeyId()) {
			// resetCamera();
			System.out.println(this.toString());
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		int yawDegr = (int) Maths.toDegrees(yaw);
		int pitchDegr = (int) Maths.toDegrees(pitch);
		int xInt = (int) getPosition().x;
		int yInt = (int) getPosition().y;
		int zInt = (int) getPosition().z;

		int oxInt = (int) observed.x;
		int oyInt = (int) observed.y;
		int ozInt = (int) observed.z;

		return ("yaw: " + yawDegr + ", pitch: " + pitchDegr + ", x: " + xInt + ", y: " + yInt + ", z: " + zInt + ", ox: " + oxInt + ", oy: " + oyInt + ", oz: " + ozInt);
	}
}