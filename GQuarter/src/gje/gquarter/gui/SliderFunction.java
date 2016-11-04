package gje.gquarter.gui;

public interface SliderFunction {
	/** Returns value based on normalised [0;1] slider's progress */
	public float getValueFromSlider(float norm);
	/** Returns normalised position of slider - [0;1]*/
	public float setSliderPosition(float value);
}
