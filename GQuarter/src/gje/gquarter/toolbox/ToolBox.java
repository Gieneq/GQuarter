package gje.gquarter.toolbox;

import gje.gquarter.core.DisplayManager;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ToolBox {
	public static final String COMMENT_CHAR = "#";
	public static final String INFO_CHAR = "?";
	public static final String VALUE_SPLITTER = ": ";
	public static final String SPLITING_STRING = " ";
	public static final String ARGUMENT_SPLITTER = " ";
	public static final int HEAD = 0;
	public static final int TAIL = 1;

	/**
	 * @param object
	 *            - sluzy do wziecia nazwy klasy, z reguly piszemy tam this
	 * @param msg
	 *            - jakis tekst do wyswietlenia <br>
	 *            Metoda ma uproscic znajdywanie wszystkich wpisanych logow :)
	 * @author Piotr
	 */
	public static void log(Object object, String msg) {
		log(object.getClass().getName(), msg);
	}

	/**
	 * @param className
	 *            - nazwa klasy w ktorej uzywamy albo z ktorej pochodzi metoda
	 * @param msg
	 *            - jakis tekst do wyswietlenia <br>
	 *            Metoda ma uproscic znajdywanie wszystkich wpisanych logow :)
	 * @author Piotr
	 */
	public static void log(String className, String msg) {
		System.out.println("  >'.'< (" + className + ") : " + msg);
	}

	/**
	 * @param path
	 *            - sciezka z dopisanym rozszerzeniem np <b> world.gq </b>
	 */
	public static ArrayList<String> loadGQFile(String path) {
		try {
			BufferedReader bufferedReader = loadFile("res/" + path);
			String line = "";
			ArrayList<String> lines = new ArrayList<String>();
			while ((line = bufferedReader.readLine()) != null) {
				// odsiewam komentarze i odstepy TODO SPRAWDZIC REDUKCJE
				// DLUGOSCI ...
				line = line.trim();
				if (line.startsWith(INFO_CHAR))
					log(ToolBox.class, "--TODO--" + line);
				else if (!line.startsWith(COMMENT_CHAR)) {
					if ((line.length() > 2))
						lines.add(line);
				}

			}
			bufferedReader.close();
			return lines;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String buildGQLine(String head, String... args) {
		String line = head + ":";
		for (String arg : args)
			line += (" " + arg);
		return line + "\n";
	}

	public static boolean parseGQLogic(String arg) {
		if (arg.toLowerCase().startsWith("true"))
			return true;
		return false;
	}

	public static String inverseGQLogic(boolean statement) {
		if (statement)
			return "true";
		return "false";
	}

	public static BufferedReader loadFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				ToolBox.log(ToolBox.class, "Erhm... failed to load sth -.-' Path= " + path);
				DisplayManager.closeDisplay();
			}

			FileReader fileReader = new FileReader(file);
			return new BufferedReader(fileReader);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse line to head and tail like: <i> "name: Bob Butterworth" </i> turns
	 * into <i> ["name", "Bob Butterwoth"]
	 * 
	 * @param line
	 *            line to be parsed.
	 */
	public static String[] splitGQLine(String line) {
		String[] raw = line.split(VALUE_SPLITTER);
		if (raw.length > 1) {
			String[] args = raw[1].split(ARGUMENT_SPLITTER);
			String[] result = new String[args.length + 1];
			result[0] = raw[0];
			for (int ii = 0; ii < args.length; ++ii)
				result[ii + 1] = args[ii];
			return result;
		}
		return raw;
	}

	@Deprecated
	public static void clearCSVFile(String filepath) {
		File file = new File(filepath + ".csv");
		try {
			FileWriter writer = new FileWriter(file, false);

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveFile(String data, String filepath) {
		File file = new File("res/" + filepath);
		try {
			FileWriter writer = new FileWriter(file, false);
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	/** Extension like .csv */
	public static void saveToCSVFile(String filepath, int columnsCount, int[] data, boolean clearFile) {
		int counter = 0;
		File file = new File(filepath + ".csv");
		if (!clearFile) {
			while (file.exists()) {
				++counter;
				file = new File(filepath + counter + ".csv");
			}
		}

		try {
			FileWriter writer = new FileWriter(file, clearFile);
			for (int i = 0; i < data.length; i += columnsCount) {
				writer.write("" + data[i]);
				for (int c = 1; c < columnsCount; ++c)
					writer.write("," + data[i + c]);
				writer.write("\n");
			}

			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void savePNGImage(String filename, BufferedImage img) {
		File file = new File(filename + ".png");
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String arraToStirngInts(int[] ints) {
		String ss = "[";
		for (int i : ints)
			ss += i + ", ";
		return ss + "]";
	}

	public static String arraToStirngInts(float[] floats, float multiplier) {
		String ss = "[";
		for (float f : floats)
			ss += (int) (f * multiplier) + ", ";
		return ss + "]";
	}
}
