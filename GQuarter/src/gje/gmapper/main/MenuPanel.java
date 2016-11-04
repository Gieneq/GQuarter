package gje.gmapper.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class MenuPanel extends JPanel implements ActionListener {
	private Launcher launcher;
	private JButton generateHeightmap;
	private JButton selectHeightmap;
	private JButton generate;
	private JButton export;

	public MenuPanel(Launcher launcher) {
		super(new GridLayout(1, 4, 1, 1));
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.launcher = launcher;

		generateHeightmap = new JButton("Generate heightmap...");
		generateHeightmap.setPreferredSize(new Dimension(180, 30));
		generateHeightmap.addActionListener(this);
		add(generateHeightmap);

		selectHeightmap = new JButton("Select heightmap...");
		selectHeightmap.setPreferredSize(new Dimension(180, 30));
		selectHeightmap.addActionListener(this);
		add(selectHeightmap);

		generate = new JButton("Generate maps!");
		generate.setPreferredSize(new Dimension(180, 30));
		generate.addActionListener(this);
		generate.setEnabled(false);
		add(generate);

		export = new JButton("Export...");
		export.setPreferredSize(new Dimension(180, 30));
		export.addActionListener(this);
		export.setEnabled(false);
		add(export);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == selectHeightmap) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
			fileChooser.setFileFilter(filter);

			int result = fileChooser.showOpenDialog(this);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				String hmPath = selectedFile.getAbsolutePath();
				launcher.setHightmapBuffImg(loadBufferedImage(hmPath));
				generate.setEnabled(true);
				export.setEnabled(false);
			}
		}

		if (event.getSource() == generate) {
			//TODO opcje
			
			String depth = JOptionPane.showInputDialog("ad");
			
			
			launcher.generate();
			export.setEnabled(true);
		}

		if (event.getSource() == export) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
			fileChooser.setFileFilter(filter);

			int result = fileChooser.showSaveDialog(this);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = fileChooser.getSelectedFile();
				String path = selectedFile.getAbsolutePath();
				if (path.toLowerCase().endsWith(".png"))
					path = path.substring(0, path.length() - 4);
				savePNGImage(path + "Slopemap", launcher.getMapGenPanel().getSlopemapBI());
				savePNGImage(path + "Isolinemap", launcher.getMapGenPanel().getIsolinemapBI());
			}
		}
	}

	private BufferedImage loadBufferedImage(String path) {

		BufferedImage hmImage = null;
		try {
			hmImage = ImageIO.read(new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return hmImage;
	}

	private static void savePNGImage(String filename, BufferedImage img) {
		File file = new File(filename + ".png");
		try {
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
