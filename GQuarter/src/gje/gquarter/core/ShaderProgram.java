package gje.gquarter.core;

import gje.gquarter.toolbox.ToolBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public abstract class ShaderProgram {
	public static final int MAX_LIGHTS_COUNT = 8;

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16); // TODO?

	/**
	 * @param geometryFileOptional
	 *            - opcjonalnie, uzywane do operacji na vierzcholkach
	 */
	public ShaderProgram(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);

		// atrybuty wirzcholkow i uniformy dodaje czy
		// ustawiam metodami abstrakcyjnymi
		bindAttributes();
		GL20.glLinkProgram(programID);
		String log = GL20.glGetProgramInfoLog(programID, 65536);
		if(log.length() > 0)
			ToolBox.log(this, "Program link: " + log);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}

	protected abstract void getAllUniformLocations();

	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	
	public void start() {
		GL20.glUseProgram(programID);
	}

	public void stop() {
		GL20.glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}

	protected abstract void bindAttributes();

	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}

	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	protected void loadVector4f(int location, Vector4f vector) {
		GL20.glUniform4f(location, vector.x, vector.y, vector.z, vector.w);
	}

	protected void loadVector3f(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z);
	}

	protected void loadVector2f(int location, Vector2f vector) {
		GL20.glUniform2f(location, vector.x, vector.y);
	}

	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if (value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}

	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}

	@SuppressWarnings("deprecation")
	public static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);

		if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			ToolBox.log(ShaderProgram.class.getName(), "Error shader id: " + GL20.glGetShader(shaderID, 500));
			ToolBox.log(ShaderProgram.class.getName(), "Could not compile shader. :( " + file);
			System.exit(-1);
		}
		ToolBox.log(ShaderProgram.class.getName(), "ShaderProgram: Build prog ok " + file + ". Type: " + type);
		return shaderID;
	}
	
	
	public static String loaderFileSource(String path) {
		File file = new File(path);
		if (!file.exists()) {
			ToolBox.log(ShaderProgram.class.getName(), "Unable to open file " + file.getAbsolutePath());
			System.exit(-1);
		}
		
		StringBuilder source = new StringBuilder();
		String line;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				source.append(line).append("\n");
			}
			reader.close();
		} catch (Exception e) {
			ToolBox.log(ShaderProgram.class.getName(), "Failed to read shader source code");
			e.printStackTrace();
			System.exit(-1);
		}
		return source.toString();
	}
}
