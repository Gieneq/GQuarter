package gje.gquarter.sky;

import gje.gquarter.core.Core;
import gje.gquarter.core.DisplayManager;
import gje.gquarter.core.MainRenderer;
import gje.gquarter.entity.Light;
import gje.gquarter.toolbox.Maths;
import gje.gquarter.toolbox.ToolBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class Weather {
	private static final String PRESETS_FILEPATH = "weather/weather.gq";
	private static final float ACCELERATION_TIME = 1; // 6k to do testow
	private static final float STARTING_TIME = 10f * Core.HOUR_IN_SEC;
	private static final float SUN_DISTANCE = 0.55f * SkyboxRenderer.RADIUS;

	/** (0; 3600*24 = 86400) */
	private float time;
	private Vector3f fogColor;
	private Vector3f skyColor;
	private Vector3f sunColor;
	private Light sun;
	private float sunAngle;
	private Vector3f realSunPosition;

	private List<Float> sunAngles;
	private List<Float> sunAngleTimes;

	private List<Vector3f> sunlightColors;
	private List<Float> sunlightTimes;

	private List<Vector3f> skyColors;
	private List<Float> skyColorsTimes;
	
	private List<Vector3f> fogColors;
	private List<Float> fogColorsTimes;

	private List<Vector3f> sunColors;
	private List<Float> sunColorsTimes;

	public Weather(boolean startWithRealTime) {
		if(startWithRealTime){
			GregorianCalendar cal = new GregorianCalendar();
			this.time = cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND);
		}
		this.time = STARTING_TIME;
		this.fogColor = Maths.convertColor3f(0x6DB2C9);
		this.skyColor = Maths.convertColor3f(0x2489AB);

		this.sun = new Light(new Vector3f(), new Vector3f());
		this.sun.setTypeDirectional(new Vector3f(0f, -1f, 0f));
		this.sun.getColour().set(0.9f, 0.9f, 0.5f);
		this.sunAngle = 0f;
		this.realSunPosition = new Vector3f();
		this.sunColor = new Vector3f();

		this.sunAngles = new ArrayList<Float>();
		this.sunAngleTimes = new ArrayList<Float>();

		this.sunlightColors = new ArrayList<Vector3f>();
		this.sunlightTimes = new ArrayList<Float>();

		this.skyColors = new ArrayList<Vector3f>();
		this.skyColorsTimes = new ArrayList<Float>();
		this.fogColors = new ArrayList<Vector3f>();
		this.fogColorsTimes = new ArrayList<Float>();

		this.sunColors = new ArrayList<Vector3f>();
		this.sunColorsTimes = new ArrayList<Float>();

		loadPresets(PRESETS_FILEPATH);
	}

	private void loadPresets(String filepath) {
		ArrayList<String> lines = ToolBox.loadGQFile(filepath);
		for (String line : lines) {
			String[] args = ToolBox.splitGQLine(line);

			if (args[0].startsWith("angle")) {
				float time = Float.parseFloat(args[1]) * Core.HOUR_IN_SEC;
				float angleDegs = Float.parseFloat(args[2]);
				sunAngleTimes.add(time);
				sunAngles.add(Maths.toRadians(angleDegs));
			}
			if (args[0].startsWith("lightcolor")) {
				float time = Float.parseFloat(args[1]) * Core.HOUR_IN_SEC;
				int rgb = Maths.convertColorHexToInt(args[2]);
				Vector3f color = Maths.convertColor3f(rgb);
				sunlightTimes.add(time);
				sunlightColors.add(color);
			}
			if (args[0].startsWith("skycolor")) {
				float time = Float.parseFloat(args[1]) * Core.HOUR_IN_SEC;
				int rgbSky = Maths.convertColorHexToInt(args[2]);
				Vector3f colorSky = Maths.convertColor3f(rgbSky);
				skyColorsTimes.add(time);
				skyColors.add(colorSky);
			}
			if (args[0].startsWith("fogcolor")) {
				float time = Float.parseFloat(args[1]) * Core.HOUR_IN_SEC;
				int rgbFog = Maths.convertColorHexToInt(args[2]);
				Vector3f fogSky = Maths.convertColor3f(rgbFog);
				fogColorsTimes.add(time);
				fogColors.add(fogSky);
			}
			if (args[0].startsWith("suncolor")) {
				float time = Float.parseFloat(args[1]) * Core.HOUR_IN_SEC;
				int rgb = Maths.convertColorHexToInt(args[2]);
				Vector3f color = Maths.convertColor3f(rgb);
				sunColorsTimes.add(time);
				sunColors.add(color);
			}
		}
	}

	public float getTime() {
		return time;
	}

	public float getTimeNorm() {
		return time / (24 * Core.HOUR_IN_SEC);
	}

	public void setTime(float time) {
		this.time = time;
	}

	public String getTimeString() {
		return ("[" + ((getHour() < 10) ? "0" : "") + getHour() + ":" + ((getMinute() < 10) ? "0" : "") + getMinute() + "] ");
	}

	public int getHour() {
		return (int) (time / 3600f);
	}

	public int getMinute() {
		return (int) ((time / 60f) % 60);
	}

	public Light getSun() {
		return sun;
	}

	public void update() {
		time += DisplayManager.getDtSec() * ACCELERATION_TIME;
		if (time > Core.HOUR_IN_SEC * 24)
			time %= Core.HOUR_IN_SEC * 24;

		calculateSunAngle();
		sun.getPosition().x = 0f;
		sun.getPosition().y = Maths.cos(sunAngle);
		sun.getPosition().z = Maths.sin(sunAngle);
		calculateRealSunPosition();
		calculateSunlightColor();
		calculateSkyColor();
		calculateRealSunColor();
	}

	private void calculateSunAngle() {
		sunAngle = 0f;
		for (int it = 0; it < sunAngleTimes.size() - 1; ++it) {
			float t0 = sunAngleTimes.get(it);
			float t1 = sunAngleTimes.get(it + 1);
			if (time > t0 && time < t1)
				sunAngle = Maths.cosInterpolation(t0, sunAngles.get(it), t1, sunAngles.get(it + 1), time, 1f);
		}
	}

	private void calculateRealSunPosition() {
		Vector3f cp = MainRenderer.getSelectedCamera().getPosition();
		realSunPosition.x = cp.x + sun.getPosition().x * SUN_DISTANCE;
		realSunPosition.y = cp.y + sun.getPosition().y * SUN_DISTANCE;
		realSunPosition.z = cp.z + sun.getPosition().z * SUN_DISTANCE;
	}

	private void calculateSunlightColor() {
		for (int it = 0; it < sunlightTimes.size() - 1; ++it) {
			float t0 = sunlightTimes.get(it);
			float t1 = sunlightTimes.get(it + 1);
			if (time > t0 && time < t1)
				Maths.linearFColorValue(t0, sunlightColors.get(it), t1, sunlightColors.get(it + 1), time, sun.getColour());
		}
	}

	private void calculateSkyColor() {
		for (int it = 0; it < skyColorsTimes.size() - 1; ++it) {
			float t0 = skyColorsTimes.get(it);
			float t1 = skyColorsTimes.get(it + 1);
			if (time > t0 && time < t1) {
				Maths.linearFColorValue(t0, skyColors.get(it), t1, skyColors.get(it + 1), time, skyColor);
			}
		}
		for (int it = 0; it < fogColorsTimes.size() - 1; ++it) {
			float t0 = fogColorsTimes.get(it);
			float t1 = fogColorsTimes.get(it + 1);
			if (time > t0 && time < t1) {
				Maths.linearFColorValue(t0, fogColors.get(it), t1, fogColors.get(it + 1), time, fogColor);
			}
		}
	}

	private void calculateRealSunColor() {
		for (int it = 0; it < sunColorsTimes.size() - 1; ++it) {
			float t0 = sunColorsTimes.get(it);
			float t1 = sunColorsTimes.get(it + 1);
			if (time > t0 && time < t1)
				Maths.linearFColorValue(t0, sunColors.get(it), t1, sunColors.get(it + 1), time, sunColor);
		}
	}

	public Vector3f getFogColor() {
		return fogColor;
	}

	public Vector3f getSkyColor() {
		return skyColor;
	}

	public Vector3f getRealSunColor() {
		return sunColor;
	}

	public Vector3f getRealSunPosition() {
		return realSunPosition;
	}
}
