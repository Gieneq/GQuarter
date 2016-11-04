package gje.gquarter.main;

import gje.gquarter.core.Core;
import gje.gquarter.toolbox.ToolBox;

//gje.gquarter.tester.MainGameLoop
public class MainGameLoop {

	public static void main(String[] args) {
		long startingTime = System.nanoTime();
		ToolBox.log(MainGameLoop.class.getName(), "Gquarters start" + (1 | 2));
		////////////////////////////////////////////
		@SuppressWarnings("unused")
		Core core = new Core();
		core.start();
		////////////////////////////////////////////
		startingTime = System.nanoTime() - startingTime;
		ToolBox.log(MainGameLoop.class.getName(), "Gquarters ends, total time: " + startingTime/1000000000l + " seconds.");
	}
}
