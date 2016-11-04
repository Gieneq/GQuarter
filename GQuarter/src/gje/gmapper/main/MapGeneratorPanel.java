package gje.gmapper.main;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MapGeneratorPanel extends JPanel {

	public static final int ICON_SIZE =300;
	private JLabel hmLabel;
	private JLabel hmInside;
	private JLabel smLabel;
	private JLabel smInside;
	private JLabel imLabel;
	private JLabel imInside;

	private BufferedImage heightmapBI;
	private BufferedImage slopemapBI;
	private BufferedImage isolinemapBI;
	private BufferedImage blendmapBI;

	public MapGeneratorPanel() {
		super(new GridLayout(2, 3, 1, 1));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		hmLabel = new JLabel("HeightMap ()");
		hmLabel.setHorizontalAlignment(JLabel.CENTER);
		hmLabel.setPreferredSize(new Dimension(ICON_SIZE, 40));
		add(hmLabel);
		smLabel = new JLabel("SlopeMap ()");
		smLabel.setHorizontalAlignment(JLabel.CENTER);
		smLabel.setPreferredSize(new Dimension(ICON_SIZE, 40));
		add(smLabel);
		imLabel = new JLabel("IsolineMap ()");
		imLabel.setHorizontalAlignment(JLabel.CENTER);
		imLabel.setPreferredSize(new Dimension(ICON_SIZE, 40));
		add(imLabel);

		hmInside = new JLabel();
		hmInside.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		add(hmInside);
		smInside = new JLabel();
		smInside.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		add(smInside);
		imInside = new JLabel();
		imInside.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		add(imInside);

		setMaximumSize(new Dimension(getPreferredSize().width, 400));
		setVisible(true);
	}

	public void setHeightMapBuffImg(BufferedImage hmImg) {
		this.heightmapBI = hmImg;
		ImageIcon hmIcon = new ImageIcon(heightmapBI);
		hmIcon = new ImageIcon(hmIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
		hmInside.setIcon(hmIcon);
		hmLabel.setText("HeightMap (" + hmImg.getWidth() + "x" + hmImg.getHeight() + ")");
	}

	public void generateIsolineMapBuffImg() {
		this.isolinemapBI = GeneratorCore.generateIsolineMap(this.heightmapBI);
		ImageIcon smIcon = new ImageIcon(isolinemapBI);
		smIcon = new ImageIcon(smIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
		imInside.setIcon(smIcon);
		imLabel.setText("IsolineMap (" + isolinemapBI.getWidth() + "x" + isolinemapBI.getHeight() + ")");
	}

	public void generateSlopeMapBuffImg() {
		this.slopemapBI = GeneratorCore.generateNormalMap(this.heightmapBI);
		ImageIcon smIcon = new ImageIcon(slopemapBI);
		smIcon = new ImageIcon(smIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH));
		smInside.setIcon(smIcon);
		smLabel.setText("SlopeMap (" + slopemapBI.getWidth() + "x" + slopemapBI.getHeight() + ")");
	}

	public BufferedImage getHeightmapBI() {
		return heightmapBI;
	}

	public BufferedImage getSlopemapBI() {
		return slopemapBI;
	}

	public BufferedImage getIsolinemapBI() {
		return isolinemapBI;
	}

	public BufferedImage getBlendmapBI() {
		return blendmapBI;
	}
}
