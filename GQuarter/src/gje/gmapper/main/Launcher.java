package gje.gmapper.main;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Launcher extends JFrame {

	private JPanel generalPanel;
	private MapGeneratorPanel mapGenPanel;
	private MenuPanel menuPanel;

	public Launcher() {
		GeneratorCore.init(123123l);
		super.setTitle("Genberator map - Piotr Adamczyk - 2016 v1");
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setSize(700, 600);

		generalPanel = new JPanel(new FlowLayout());

		menuPanel = new MenuPanel(this);
		generalPanel.add(menuPanel);

		mapGenPanel = new MapGeneratorPanel();
		generalPanel.add(mapGenPanel);

		add(generalPanel);
		setVisible(true);
	}

	public void setHightmapBuffImg(BufferedImage hmimg) {
		mapGenPanel.setHeightMapBuffImg(hmimg);
	}

	public JPanel getGeneralPanel() {
		return generalPanel;
	}

	public MapGeneratorPanel getMapGenPanel() {
		return mapGenPanel;
	}

	public MenuPanel getMenuPanel() {
		return menuPanel;
	}

	public static void main(String[] args) {
		new Launcher();
		// stad mozna tez odpalac sama symulacje :D
	}

	public void generate() {
		mapGenPanel.generateSlopeMapBuffImg();
		mapGenPanel.generateIsolineMapBuffImg();
	}
}
