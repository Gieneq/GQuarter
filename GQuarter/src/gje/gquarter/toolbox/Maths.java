package gje.gquarter.toolbox;

import java.awt.Color;

import gje.gquarter.entity.Camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Maths {
	public static final Vector3f ROTATOR_X = new Vector3f(1, 0, 0);
	public static final Vector3f ROTATOR_Y = new Vector3f(0, 1, 0);
	public static final Vector3f ROTATOR_Z = new Vector3f(0, 0, 1);

	public static final Vector3f TRANSLATOR_X = ROTATOR_X;
	public static final Vector3f TRANSLATOR_Y = ROTATOR_Y;
	public static final Vector3f TRANSLATOR_Z = ROTATOR_Z;

	public static final float PI = (float) Math.PI;
	public static final float PI_HALF = 0.5f * Maths.PI;
	public static final float PI2 = 2f * Maths.PI;
	public static final float SQRT2 = (float) Math.sqrt(2d);
	public static final float SQRT3 = (float) Math.sqrt(3d);

	private static Matrix4f viewMatrix = new Matrix4f();
	private static Vector3f translation = new Vector3f();
	private static Vector3f scale = new Vector3f();

	private static Vector3f scaleVec = new Vector3f();

	private static Vector4f tmpVec4f = new Vector4f();
	private static Vector4f dstVec4f = new Vector4f();

	public static float max(float a, float b) {
		if (a > b)
			return a;
		return b;
	}

	public static float min(float a, float b) {
		if (a < b)
			return a;
		return b;
	}

	public static float sin(float angle) {
		return (float) Math.sin(angle);
	}

	public static float cos(float angle) {
		return (float) Math.cos(angle);
	}

	public static float toRadians(float degrees) {
		return PI * degrees / 180f;
	}

	public static float toDegrees(float radians) {
		return 180 * radians / PI;
	}

	public static String getIntPosition(Vector3f pos) {
		return ("[" + (int) pos.x + "," + (int) pos.y + "," + (int) pos.z + "," + "]");
	}

	public static String getFloatPosition(Vector3f pos, int divisor) {
		return ("[" + 1f * (int) (pos.x * divisor) / divisor + "," + 1f * (int) (pos.y * divisor) / divisor + "," + 1f * (int) (pos.z * divisor) / divisor + "]");
	}

	/**
	 * @param x0
	 *            wartosc wyznaczonej funkcji liniowej
	 */
	public static float linearFunctionValue(float x1, float y1, float x2, float y2, float x0) {
		float a = ((y1 - y2) / (x1 - x2));
		float b = y1 - a * x1;
		return a * x0 + b;
	}

	public static int convertColorHexToInt(String hex) {
		if (hex.startsWith("#"))
			return Integer.parseInt(hex.replace("#", ""), 16);
		if (hex.startsWith("0x"))
			return Integer.parseInt(hex.replace("0x", ""), 16);
		return Integer.parseInt(hex, 16);
	}

	public static Vector3f convertColor3f(int rgb) {
		Vector3f color = new Vector3f();
		color.x = (rgb >> (4 * 4)) / 255f;
		color.y = ((rgb & 0x00FF00) >> (2 * 4)) / 255f;
		color.z = (rgb & 0x0000FF) / 255f;
		return color;
	}

	public static Vector3f convertColor3f(int r, int g, int b) {
		Vector3f color = new Vector3f();
		color.x = r / 255f;
		color.y = g / 255f;
		color.z = b / 255f;
		return color;
	}

	/** Generuje nowy wektor reprezentujacy kolor we floatach */
	public static Vector4f convertColor4f(int r, int g, int b, int a) {
		Vector4f color = new Vector4f();
		color.x = r / 255f;
		color.y = g / 255f;
		color.z = b / 255f;
		color.w = a / 255f;
		return color;
	}

	public static float cosInterpolation(float t1, float y1, float t2, float y2, float t, float cosFactor) {
		float u = (t - t2) / (t1 - t2);
		float k = (1f + Maths.cos(Maths.PI * u)) / 2f;
		k = (float) Math.pow(k, cosFactor);
		return (y2 - y1) * k + y1;
	}

	public static void vec4CosInterpolation(float t1, Vector4f y1, float t2, Vector4f y2, float time, float factorR, float factorG, float factorB, float factorA, Vector4f src) {
		src.x = cosInterpolation(t1, y1.x, t2, y2.x, time, factorR);
		src.y = cosInterpolation(t1, y1.y, t2, y2.y, time, factorG);
		src.z = cosInterpolation(t1, y1.z, t2, y2.z, time, factorB);
		src.w = cosInterpolation(t1, y1.w, t2, y2.w, time, factorA);
	}

	public static void vec3CosInterpolation(float t1, Vector3f y1, float t2, Vector3f y2, float time, float factorR, float factorG, float factorB, Vector3f src) {
		src.x = cosInterpolation(t1, y1.x, t2, y2.x, time, factorR);
		src.y = cosInterpolation(t1, y1.y, t2, y2.y, time, factorG);
		src.z = cosInterpolation(t1, y1.z, t2, y2.z, time, factorB);
	}

	public static boolean isInGap(float min, float max, float src) {
		return ((src > min) && (src <= max));
	}

	public static void linearFColorValue(float x1, Vector3f startingColor, float x2, Vector3f endingColor, float x0, Vector3f modifiedColor) {
		modifiedColor.x = linearFunctionValue(x1, startingColor.x, x2, endingColor.x, x0);
		modifiedColor.y = linearFunctionValue(x1, startingColor.y, x2, endingColor.y, x0);
		modifiedColor.z = linearFunctionValue(x1, startingColor.z, x2, endingColor.z, x0);
	}

	public static void transformVec3f(Matrix4f left, Vector3f right, Vector3f dst) {
		tmpVec4f.set(right.x, right.y, right.z, 1f);
		Matrix4f.transform(left, tmpVec4f, dstVec4f);
		dst.set(dstVec4f.x, dstVec4f.y, dstVec4f.z);
	}

	public static void createTransformationMatrix(Vector3f translation, Rotation3f rotationInRads, float scale, Matrix4f matrix) {
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rotationInRads.rx, ROTATOR_X, matrix, matrix);
		Matrix4f.rotate(rotationInRads.ry, ROTATOR_Y, matrix, matrix);
		Matrix4f.rotate(rotationInRads.rz, ROTATOR_Z, matrix, matrix);
		Maths.scale.x = scale;
		Maths.scale.y = scale;
		Maths.scale.z = scale;
		Matrix4f.scale(Maths.scale, matrix, matrix);
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation, Rotation3f rotationInRads, Vector3f scale, Matrix4f matrix) {
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(rotationInRads.rx, ROTATOR_X, matrix, matrix);
		Matrix4f.rotate(rotationInRads.ry, ROTATOR_Y, matrix, matrix);
		Matrix4f.rotate(rotationInRads.rz, ROTATOR_Z, matrix, matrix);

		Maths.scale.x = scale.x;
		Maths.scale.y = scale.y;
		Maths.scale.z = scale.z;
		Matrix4f.scale(Maths.scale, matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Rotation3f rotationInRads, Matrix4f matrix) {
		matrix.setIdentity();
		Matrix4f.rotate(rotationInRads.rx, ROTATOR_X, matrix, matrix);
		Matrix4f.rotate(rotationInRads.ry, ROTATOR_Y, matrix, matrix);
		Matrix4f.rotate(rotationInRads.rz, ROTATOR_Z, matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale, float rz) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		Matrix4f.rotate(rz, ROTATOR_Z, matrix, matrix);
		return matrix;
	}

	public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

	public static float barryCentric(Triangle3Point trian, Vector2f pos) {
		return barryCentric(trian.getpA(), trian.getpA(), trian.getpA(), pos);
	}

	public static Float getMinPositiveRoot(float a, float b, float c) {
		float delta = b * b - 4 * a * c;
		if (delta < 0)
			return null;
		// jest jakies rozwiazanie
		float r1 = (float) (-b - Math.sqrt(delta) / (2 * a));
		float r2 = (float) (-b + Math.sqrt(delta) / (2 * a));
		// sortowanie
		if (r2 > r1) {
			float temp = r2;
			r2 = r1;
			r1 = temp;
		}
		// musi byc mniejsze od zera
		if (r1 > 0)
			return r1;
		// r1 mniejsze od zera
		if (r2 > 0)
			return r1;
		// nie ma pasujacego rozwiazania
		return null;
	}

	@Deprecated
	public static Matrix4f createViewMjatrix(Camera camera) {
		viewMatrix.setIdentity();
		Matrix4f.rotate(camera.getPitch(), ROTATOR_X, viewMatrix, viewMatrix);
		Matrix4f.rotate(camera.getYaw(), ROTATOR_Y, viewMatrix, viewMatrix);
		Matrix4f.rotate(camera.getRoll(), ROTATOR_Z, viewMatrix, viewMatrix);
		Maths.translation.x = -camera.getPosition().x;
		Maths.translation.y = -camera.getPosition().y;
		Maths.translation.z = -camera.getPosition().z;
		Matrix4f.translate(translation, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	public static void prepareBilboardedMatrix(Vector3f pos, float scale, float rotation, Camera camera, Matrix4f mvMatrix) {
		Matrix4f viewM = camera.getViewMatrix();
		// update macierzy czastki
		Matrix4f.setIdentity(mvMatrix);
		Matrix4f.translate(pos, mvMatrix, mvMatrix);
		// to ma mi zroibc macierz jednostkowa w miejsce rotacji - zeby
		// uzyskac tylko translacje z kamery bez rotacji
		mvMatrix.m00 = viewM.m00;
		mvMatrix.m01 = viewM.m10;
		mvMatrix.m02 = viewM.m20;
		mvMatrix.m10 = viewM.m01;
		mvMatrix.m11 = viewM.m11;
		mvMatrix.m12 = viewM.m21;
		mvMatrix.m20 = viewM.m02;
		mvMatrix.m21 = viewM.m12;
		mvMatrix.m22 = viewM.m22;

		/*
		 * Bilbording
		 */
		Matrix4f.rotate(rotation, ROTATOR_Z, mvMatrix, mvMatrix);
		scaleVec.set(scale, scale, scale);
		Matrix4f.scale(scaleVec, mvMatrix, mvMatrix);
		Matrix4f.mul(viewM, mvMatrix, mvMatrix); // macierz viewModel
	}

	public static int clampI(int value, int min, int max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	public static float clampF(float value, float min, float max) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}
}
