package gje.gquarter.core;

import gje.gquarter.bilboarding.SunRenderer;
import gje.gquarter.boundings.BoundingsRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.entity.EntityRenderer;
import gje.gquarter.entity.EnvironmentRenderer;
import gje.gquarter.entity.Light;
import gje.gquarter.entity.ModelBase;
import gje.gquarter.gui.GUIMainRenderer;
import gje.gquarter.map.MapRenderer;
import gje.gquarter.postprocessing.ProcessingRenderer;
import gje.gquarter.sky.FlareRenderer;
import gje.gquarter.sky.SkyboxRenderer;
import gje.gquarter.sky.Weather;
import gje.gquarter.terrain.TerrainRenderer;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.water.WaterRenderer;
import gje.gquarter.water.WaterTile;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

public class MainRenderer {
	private static float INITIAL_FOV = 70;
	private static float INITIAL_NEAR_PLANE = .1f; // 0.1
	private static float INITIAL_FAR_PLANE = 100f; // 1500

	private static float INITIAL_FOG_GRADIENT = 9f;// 4.6_dla_FAR=40
	private static float INITIAL_FOG_DENSITY = 0.01f;// 0.04_dla_FAR=40

	public static final Vector4f UPPER_CLIP_PLANE = new Vector4f(0f, -1f, 0f, 220f);

	private static float fov;
	private static float nearPlane;
	private static float farPlane;

	private static float fogGradient;
	private static float fogDensity;

	private static Matrix4f projectionMatrix;
	private static Camera selectedCamera;
	private static Weather weather;
	private static Vector4f clipPlane = new Vector4f();

	private static boolean wireframeMode;

	private static List<Light> lightList;

	public static void init() {
		projectionMatrix = new Matrix4f();
		selectedCamera = null;
		lightList = new ArrayList<Light>();
		disableWireframeMode();
		enableCulling();
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glLineWidth(1.8f);

		Loader.init();
		TerrainRenderer.init(projectionMatrix);
		EntityRenderer.init(projectionMatrix);
		EnvironmentRenderer.init(projectionMatrix);
		ModelBase.init();
		BoundingsRenderer.init(projectionMatrix);
		WaterRenderer.init(projectionMatrix);
		SkyboxRenderer.init();
		ProcessingRenderer.init();
		FlareRenderer.init();
		SunRenderer.init(projectionMatrix);
		MapRenderer.init();
		GUIMainRenderer.init();

		loadProjectionParams(INITIAL_FOV, INITIAL_NEAR_PLANE, INITIAL_FAR_PLANE);
		loadFogParams(INITIAL_FOG_GRADIENT, INITIAL_FOG_DENSITY);

		weather = new Weather();

		MainRenderer.loadFarPlaneFogSkybox(INITIAL_FAR_PLANE);
	}

	public static void update(float dt) {
		weather.update();
		EnvironmentRenderer.update(dt);
		// TODO SPRAWDZAC CZY TRZEB 3 RENDEROW BO MOZE NIE MA ZADNEJ KRATKI WODY
		// W ZASIEGU!
		// TODO W USTAWIENIACH DAC DYNAMICZNY RENDER WODY, JAK WYLACZE TO
		// POMIJAM FBO!
	}

	public static void render() {
		Camera cam = MainRenderer.getSelectedCamera();
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		WaterTile closestWaterTile = WaterRenderer.getClosestWaterTile();
		float wtHeight = closestWaterTile.getHeight();
		
		/*
		 * REFLECTION
		 */
		float camY = cam.getPosition().y;
		WaterRenderer.getFbos().bindReflectionFrameBuffer();
		cam.invertPitch();
		cam.getPosition().y = (2 * wtHeight - camY);
		cam.updateViewMatrix();
		// TODO FRSTUM CULLING?
		prepareRendering();
		clipPlane.set(0f, 1f, 0f, -wtHeight);
		SkyboxRenderer.rendererRelease();
		TerrainRenderer.rendererRelease(clipPlane);
		EntityRenderer.renderRelease(clipPlane);
		EnvironmentRenderer.renderRelease(clipPlane);
		SunRenderer.rendererRelease();
		cam.getPosition().y = camY;
		cam.invertPitch();
		cam.updateViewMatrix();
		WaterRenderer.getFbos().unbindCurrentFrameBuffer();

		/*
		 * REFRACTION
		 */
		clipPlane.set(0f, -1f, 0f, wtHeight);
		WaterRenderer.getFbos().bindRefractionFrameBuffer();
		prepareRendering();
		TerrainRenderer.rendererRelease(clipPlane);
		EntityRenderer.renderRelease(clipPlane);
		EnvironmentRenderer.renderRelease(clipPlane);
		SunRenderer.rendererRelease();
		WaterRenderer.getFbos().unbindCurrentFrameBuffer();

		/*
		 * FINAL
		 */
		ProcessingRenderer.getFbo().bindFrameBuffer();
		prepareRendering();
		SkyboxRenderer.rendererRelease();
		TerrainRenderer.rendererRelease(UPPER_CLIP_PLANE);
		EntityRenderer.renderRelease(UPPER_CLIP_PLANE);
		EnvironmentRenderer.renderRelease(UPPER_CLIP_PLANE);
		SunRenderer.rendererRelease();
		WaterRenderer.renderRelease();
		ProcessingRenderer.getFbo().unbindCurrentFrameBuffer();
		
		prepareRendering();
		ProcessingRenderer.rendererRelease();
		BoundingsRenderer.renderRelease(UPPER_CLIP_PLANE);
		FlareRenderer.rendererRelease();
		MapRenderer.rendererRelease();
		GUIMainRenderer.rendererRelease();
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
	}

	private static void prepareRendering() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClearColor(weather.getFogColor().x, weather.getFogColor().y, weather.getFogColor().z, 1f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		enableCulling();
	}

	public static void loadLightSource(Light light) {
		// check culling...
		lightList.add(light);
	}

	public static void removeLightSource(Light light) {
		lightList.remove(light);
	}

	public static void cleanLightList() {
		lightList.clear();
	}

	public static void organiseLightList() {
		// TODO
	}

	public static void enableCulling() {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BACK);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BACK);
	}

	public static void setWireframeMode(boolean wfMode) {
		if (wfMode)
			enableWireframeMode();
		else
			disableWireframeMode();
	}

	public static void enableWireframeMode() {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		wireframeMode = true;
	}

	public static void disableWireframeMode() {
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		wireframeMode = false;
	}

	public static boolean isWireframeModeOn() {
		return wireframeMode;
	}

	public static void cleanAllBatchLists() {
		TerrainRenderer.clearBatchList();
		EntityRenderer.clearBatchList();
		EnvironmentRenderer.clearBatchList();
		BoundingsRenderer.clearBatchList();
		WaterRenderer.clearBatchList();
		GUIMainRenderer.clearBatchList();
	}

	public static void clean() {
		Loader.clean();
		TerrainRenderer.clean();
		EntityRenderer.clean();
		EnvironmentRenderer.clean();
		BoundingsRenderer.clean();
		WaterRenderer.clean();
		ProcessingRenderer.clean();
		SunRenderer.clean();
		FlareRenderer.clean();
		SkyboxRenderer.clean();
		MapRenderer.clean();
		GUIMainRenderer.clean();
		cleanLightList();
	}

	public static void loadFogParams(float gradient, float density) {
		fogGradient = gradient;
		fogDensity = density;
		// zaladowan potrzeben przy lcizeniu mgly!!!!
		TerrainRenderer.loadFogParams(fogGradient, fogDensity);
		EntityRenderer.loadFogParams(fogGradient, fogDensity);
		EnvironmentRenderer.loadFogParams(fogGradient, fogDensity);
		WaterRenderer.loadFogParams(gradient, density); // TODO
	}

	public static void loadFogParamBasedOnDist(float dist) {
		float density = 0.0083f;
		if (dist < 10)
			dist = 10;
		if (Maths.isInGap(25, 200, dist))
			density = (float) (-1.4183 / 100000000.0 * Math.pow(dist, 3) + 6.7241 / 1000000.0 * Math.pow(dist, 2) - 1.0444 / 1000.0 * dist + 6.1675 / 100.0);
		MainRenderer.loadFogParams(MainRenderer.getFogGradient(), density);
	}

	public static void loadFarPlaneFogSkybox(float distanceFar) {
		MainRenderer.loadProjectionParams(MainRenderer.getFov(), MainRenderer.getNearPlane(), distanceFar);

		// ustawiam rozmiar skyboxa
		SkyboxRenderer.loadBoxScale(distanceFar / Maths.SQRT3);

		// zmniejszam odleglosc do minimum skyboxa - poczatek mgly
		distanceFar = distanceFar / Maths.SQRT3;
		MainRenderer.loadFogParamBasedOnDist(distanceFar);
	}

	public static void loadProjectionParams(float fov, float nearPlane, float farPlane) {
		MainRenderer.fov = fov;
		MainRenderer.nearPlane = nearPlane;
		MainRenderer.farPlane = farPlane;
		createProjectionMatrix(projectionMatrix);

		// zaladowan potrzeben przy lcizeniu mgly!!!!
		TerrainRenderer.loadProjectionMatrix(projectionMatrix);
		// TerrainRenderer.loadPlanes(nearPlane, farPlane);
		EntityRenderer.loadProjectionMatrix(projectionMatrix);
		EnvironmentRenderer.loadProjectionMatrix(projectionMatrix);
		WaterRenderer.loadProjectionMatrix(projectionMatrix);
		SunRenderer.loadProjectionMatrix(projectionMatrix);
		SkyboxRenderer.loadProjectionMatrix(projectionMatrix);
		BoundingsRenderer.loadProjectionMatrix(projectionMatrix);
	}

	public static Weather getWeather() {
		return weather;
	}

	public static Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public static Camera getSelectedCamera() {
		return selectedCamera;
	}

	public static void setSelectedCamera(Camera selectedCamera) {
		MainRenderer.selectedCamera = selectedCamera;
	}

	public static float getFov() {
		return fov;
	}

	public static float getNearPlane() {
		return nearPlane;
	}

	public static float getFarPlane() {
		return farPlane;
	}

	public static float getFogGradient() {
		return fogGradient;
	}

	public static float getFogDensity() {
		return fogDensity;
	}

	public static List<Light> getLightList() {
		return lightList;
	}

	public static void createProjectionMatrix(Matrix4f matrix) {
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float yScale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))));
		float xScale = yScale / aspectRatio;
		float frustumLength = farPlane - nearPlane;

		matrix.setIdentity();
		matrix.m00 = xScale;
		matrix.m11 = yScale;

		matrix.m22 = -((farPlane + nearPlane) / frustumLength);
		matrix.m23 = -1;

		matrix.m32 = -((2 * farPlane * nearPlane) / frustumLength);
		matrix.m33 = 0;
	}
}
