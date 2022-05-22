package gui;

import java.awt.Frame;
import java.awt.Toolkit;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import application.Application;
import game.Game;
import gui.dialog.ClientSettingsDialog;
import gui.dialog.MapCreator;
import gui.dialog.OnlinePlayerDialogHostDialog;
import gui.dialog.OnlinePlayerDialogJoinDialog;
import gui.gameStart.GameStartDialog;

public class MyFrame extends JFrame {

	private static final long serialVersionUID = 4070509246110827584L;

	public MyFrame() {
		super(Application.NAME);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(900, 600);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.addMenuBar();
		this.setResizable(false);
		this.setUndecorated(true);
		this.setVisible(true);
		this.toFront();
	}

	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(this.gameMenu());
		menuBar.add(this.settingsMenu());
		menuBar.add(this.mapCreatorMenu());

		this.setJMenuBar(menuBar);
	}

	private JMenu gameMenu() {
		JMenu gameMenu = new JMenu("Game");

		JMenu onlinePlay = new JMenu("Online spielen");

		JMenuItem onlinePlayHost = new JMenuItem("Create new Game");
		onlinePlayHost.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		onlinePlayHost.addActionListener(e -> new OnlinePlayerDialogHostDialog());
		JMenuItem onlinePlayJoin = new JMenuItem("Join Game");
		onlinePlayJoin.addActionListener(e -> new OnlinePlayerDialogJoinDialog());
		onlinePlay.add(onlinePlayHost);
		onlinePlay.add(onlinePlayJoin);

		JMenuItem againstComputerMenu = new JMenuItem("Against Computer");
		againstComputerMenu.addActionListener(e -> {
			if (!Game.getInstance().isGameStarted()) {
				new GameStartDialog();
			}
		});
		againstComputerMenu.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		JMenuItem quitGame = new JMenuItem("Quit Game");
		quitGame.addActionListener(e -> Application.frame.dispose());

		gameMenu.add(onlinePlay);
		gameMenu.add(againstComputerMenu);
		gameMenu.add(quitGame);

		return gameMenu;
	}

	private JMenu settingsMenu() {
		JMenu settingsMenu = new JMenu("Settings");

		settingsMenu.addActionListener(e -> new ClientSettingsDialog());

		return settingsMenu;
	}

	private JMenu mapCreatorMenu() {
		JMenu mapCreatorMenu = new JMenu("Map Creator");

		JMenuItem mapCreatorMenuItem = new JMenuItem("Create new Map");
		mapCreatorMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		mapCreatorMenuItem.addActionListener(e -> {
			try {
				MapCreator.createNewMap();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		JMenuItem editMapMenuItem = new JMenuItem("Edit Map");

		mapCreatorMenu.add(mapCreatorMenuItem);
		mapCreatorMenu.add(editMapMenuItem);

		return mapCreatorMenu;
	}

	public void setComponent(JComponent com) {
		this.getContentPane().removeAll();
		this.getContentPane().add(com);
		this.revalidate();
		this.repaint();
	}

}
