package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import application.Application;
import game.GameMap;

public class MapCreator extends JDialog {

	private static final long serialVersionUID = -5299717348756812866L;

	private static final Dimension minDimension = new Dimension(2000, 2000);

	public static File createNewMap() throws IOException {
		MapCreator m = new MapCreator();
		return m.folder;
	}

	private File folder;

	private MapCreator() throws IOException {
		super(Application.frame, "New Map", true);
		this.setLayout(new BorderLayout());
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".png", "png");
		// tmp
		JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"), "Downloads"));
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileFilter(filter);

		if (chooser.showOpenDialog(Application.frame) == JFileChooser.APPROVE_OPTION) {
			File imageFile = chooser.getSelectedFile();

			BufferedImage image = ImageIO.read(imageFile);
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension newD = this.getScaledDimension(image.getWidth(), image.getHeight(), screen.width >> 1, screen.height >> 1);
			JLabel mapLabel = new JLabel(new ImageIcon(image.getScaledInstance(newD.width, newD.height, Image.SCALE_SMOOTH)));
			this.add(mapLabel, BorderLayout.CENTER);

			JPanel infoPanel = new JPanel(new GridBagLayout());

			String name = imageFile.getName().substring(0, imageFile.getName().lastIndexOf("."));
			JTextField nameField = new JTextField(name, Math.max(30, name.length()));
			nameField.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					nameField.selectAll();
				}
			});
			nameField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						MapCreator.this.testOK(nameField.getText(), imageFile);
					}
				}
			});
			nameField.requestFocusInWindow();
			nameField.selectAll();

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);

			if ((image.getWidth() < MapCreator.minDimension.getWidth()) || (image.getHeight() < MapCreator.minDimension.getHeight())) {
				JLabel warning = new JLabel("Map is smaller than the recommended 2000 x 2000 Pixels.");
				gbc.gridy = 0;
				gbc.gridx = 0;
				gbc.gridwidth = 3;
				gbc.anchor = GridBagConstraints.LINE_START;
				infoPanel.add(warning, gbc);
			}

			gbc.anchor = GridBagConstraints.CENTER;
			gbc.gridwidth = 1;
			gbc.gridy++;
			gbc.gridx = 0;
			infoPanel.add(nameField, gbc);

			gbc.weightx = 1;
			gbc.gridx = 1;
			infoPanel.add(new JLabel(), gbc);

			JButton okButton = new JButton("OK");
			okButton.addActionListener(e -> this.testOK(nameField.getText(), imageFile));

			gbc.weightx = 0;
			gbc.gridx = 2;
			infoPanel.add(okButton, gbc);

			this.add(infoPanel, BorderLayout.SOUTH);

			this.pack();
			this.setResizable(false);
			this.setLocationRelativeTo(Application.frame);
			this.setVisible(true);
		}
	}

	private void testOK(String name, File imageFile) {
		boolean tested = this.testName(name);
		if (tested) {
			this.createMapFolder(name, imageFile);
			this.dispose();
		}
	}

	private boolean testName(String name) {
		if (!GameMap.SavedGamesFolder.exists()) {
			if (!GameMap.SavedGamesFolder.mkdirs()) {
				JOptionPane.showMessageDialog(this, "Saved Game Folder could not be created");
				return false;
			}
		}
		if (!GameMap.myGameFolder.exists()) {
			if (!GameMap.myGameFolder.mkdirs()) {
				JOptionPane.showMessageDialog(this, Application.NAME + " Folder could not be created");
				return false;
			}
		}
		File[] savedMaps = GameMap.myGameFolder.listFiles(File::isDirectory);
		File newMapFolder = new File(GameMap.myGameFolder, name);
		for (File file : savedMaps) {
			if (file.equals(newMapFolder)) { return false; }
		}
		return true;
	}

	private void createMapFolder(String name, File imageFile) {
		File mapFolder = new File(GameMap.myGameFolder, name);
		if (!mapFolder.mkdir()) {
			JOptionPane.showMessageDialog(this, "MapFolder could not be created");
			return;
		}
		try {
			Files.copy(imageFile.toPath(), new File(mapFolder, GameMap.MAPFILE).toPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		boolean createdAll = true;
		File locationsFile = new File(mapFolder, GameMap.LOCATIONSFILE);
		File connectionsFile = new File(mapFolder, GameMap.CONNECTIONFILE);
		File missionCardsFile = new File(mapFolder, GameMap.MISSIONCARDSFILE);
		File rulesFile = new File(mapFolder, GameMap.RULESFILE);
		try {
			createdAll &= locationsFile.createNewFile();
			createdAll &= connectionsFile.createNewFile();
			createdAll &= missionCardsFile.createNewFile();
			createdAll &= rulesFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (!createdAll) {
			JOptionPane.showMessageDialog(this, "Not all Files could be created");
		}
		// Folder could be created
		this.folder = mapFolder;
	}

	private Dimension getScaledDimension(int original_width, int original_height, int bound_width, int bound_height) {
		int new_width = original_width;
		int new_height = original_height;

		// first check if we need to scale width
		if (original_width > bound_width) {
			// scale width to fit
			new_width = bound_width;
			// scale height to maintain aspect ratio
			new_height = (new_width * original_height) / original_width;
		}

		// then check if we need to scale even with the new height
		if (new_height > bound_height) {
			// scale height to fit instead
			new_height = bound_height;
			// scale width to maintain aspect ratio
			new_width = (new_height * original_width) / original_height;
		}
		return new Dimension(new_width, new_height);
	}
}
