package gje.gquarter.toolbox;

import java.util.ArrayList;
import java.util.List;

import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Camera;
import gje.gquarter.gui.GuiFrame;
import gje.gquarter.gui.On3DTerrainPick;
import gje.gquarter.terrain.Terrain;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class MousePicker {
	private static final float MAX_RAY_RANGE = 20f;
	private static final int GRAIN_STEPS = 20;
	private static final float GRAIN_DELTA = MAX_RAY_RANGE / GRAIN_STEPS;
	private static final float PRECYSION = 0.001f;

	private static Vector3f currentRay;
	private static Matrix4f projectionMatrix;
	private static Matrix4f viewMatrix;
	private static Camera camera;

	private static List<On3DTerrainPick> terrainPickers = new ArrayList<On3DTerrainPick>();
	private static boolean leftClickLatch = false;
	private static boolean rightClickLatch = false;

	private static Vector2f vec2fNDC = new Vector2f();
	private static Vector3f worldRay = new Vector3f();
	private static Vector3f start = new Vector3f();
	private static Vector3f end = new Vector3f();
	private static Vector4f clipCoords = new Vector4f();
	private static Vector4f eyeCoords = new Vector4f();
	private static Vector4f worldCoords = new Vector4f();
	private static Matrix4f tempMatrix = new Matrix4f();

	public static void init(Matrix4f projectionMatrix) {
		MousePicker.projectionMatrix = projectionMatrix;
		camera = MainRenderer.getSelectedCamera();
		MousePicker.viewMatrix = camera.getViewMatrix();
	}

	public static Vector3f getCurrentRay() {
		return currentRay;
	}

	public static void add3DTerrainPicker(On3DTerrainPick picker) {
		terrainPickers.add(picker);
	}

	public static void updateGeneral() {
		Terrain terrain = camera.getRegional().getRegion().getTarrain();
		Vector3f point = getIntersssectionPoint(terrain);

		if (point != null) {
			boolean lmbClicked = Mouse.isButtonDown(GuiFrame.MOUSE_LMB);
			boolean rmbClicked = Mouse.isButtonDown(GuiFrame.MOUSE_RMB);
			/*
			 * LMB
			 */
			if (lmbClicked) {
				if (!leftClickLatch) {
					onClick(point, GuiFrame.MOUSE_LMB);
					leftClickLatch = true;
				} else
					onPress(point, GuiFrame.MOUSE_LMB);
			} else {
				if (leftClickLatch) {
					onRelease(point, GuiFrame.MOUSE_LMB);
					leftClickLatch = false;
				}
			}

			/*
			 * RMB
			 */
			if (rmbClicked) {
				if (!rightClickLatch) {
					onClick(point, GuiFrame.MOUSE_RMB);
					rightClickLatch = true;
				} else
					onPress(point, GuiFrame.MOUSE_RMB);
			} else {
				if (rightClickLatch) {
					onRelease(point, GuiFrame.MOUSE_RMB);
					rightClickLatch = false;
				}
			}

			/*
			 * HOVER
			 */
			if (!lmbClicked && !rmbClicked)
				onHover(point);
		}

	}

	private static void onClick(Vector3f point, int buttonId) {
		for (On3DTerrainPick picker : terrainPickers)
			picker.on3DClick(point.x, point.y, point.z, buttonId);
	}

	private static void onPress(Vector3f point, int buttonId) {
		for (On3DTerrainPick picker : terrainPickers)
			picker.on3DPress(point.x, point.y, point.z, buttonId);
	}

	private static void onRelease(Vector3f point, int buttonId) {
		for (On3DTerrainPick picker : terrainPickers)
			picker.on3DRelease(point.x, point.y, point.z, buttonId);
	}

	private static void onHover(Vector3f point) {
		for (On3DTerrainPick picker : terrainPickers)
			picker.on3DHover(point.x, point.y, point.z);
	}

	private static Vector3f getIntersssectionPoint(Terrain ter) {
		currentRay = calculateMouseRay();
		start.set(camera.getPosition());
		end.set(camera.getPosition());

		for (int i = 0; i < GRAIN_STEPS; ++i) {
			// przesowam ponkt poczawszy od kamery co krok
			end.translate(currentRay.x * GRAIN_DELTA, currentRay.y * GRAIN_DELTA, currentRay.z * GRAIN_DELTA);

			// sprawdzam czy przecialem teren, jezeli tak to szuakmy bin
			if (end.y <= ter.getHeightOfTerrainGlobal(end.x, end.z)) {

				while (Math.abs(end.length() - start.length()) > PRECYSION) {
					float midX = start.x + (end.x - start.x) / 2f;
					float midY = start.y + (end.y - start.y) / 2f;
					float midZ = start.z + (end.z - start.z) / 2f;
					float height = ter.getHeightOfTerrainGlobal(midX, midZ);

					if (midY >= height)
						start.set(midX, midY, midZ);
					else
						end.set(midX, midY, midZ);
				}
				return end;
			}
			start.set(end);
		}
		return null;
	}

	private static Vector3f calculateMouseRay() {
		// tu sa znormalizowane kordy <-1,1>
		Vector2f ndc = getNDC(Mouse.getX(), Mouse.getY());

		// dodajemy kierunek zeta -1
		clipCoords.set(ndc.x, ndc.y, -1f, 1f);

		// cofamy do widoku kamery - eye space
		eyeCoords = toEyeCoords(clipCoords);

		// cofamy do world space
		worldRay = toWorldCoords(eyeCoords);
		return worldRay;
	}

	private static Vector4f toEyeCoords(Vector4f clipCoords) {
		Matrix4f.invert(projectionMatrix, tempMatrix);
		Matrix4f.transform(tempMatrix, clipCoords, eyeCoords);
		eyeCoords.z = -1f;
		eyeCoords.w = 0f;
		return eyeCoords;
	}

	private static Vector3f toWorldCoords(Vector4f eyeCoords) {
		Matrix4f.invert(viewMatrix, tempMatrix);
		Matrix4f.transform(tempMatrix, eyeCoords, worldCoords);
		worldRay.set(worldCoords.x, worldCoords.y, worldCoords.z);
		worldRay.normalise();
		return worldRay;
	}

	private static Vector2f getNDC(float mx, float my) {
		vec2fNDC.x = 2f * mx / Display.getWidth() - 1;
		vec2fNDC.y = 2f * my / Display.getHeight() - 1;
		return vec2fNDC;
	}

}
