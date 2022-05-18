package gui;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import game.Game;
import gui.dialog.ClientSettingsDialog;
import gui.dialog.OnlinePlayerDialogHostDialog;
import gui.dialog.OnlinePlayerDialogJoinDialog;
import gui.gameStart.GameStartDialog;

public class MyFrame extends JFrame {

	private static final long serialVersionUID = 4070509246110827584L;

	private JSplitPane panel;
	private GameBoardPanel gameBoardPanel;
	private InfoPanel infoPanel;

	public MyFrame() {
		super("Zug um Zug");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(900, 600);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				MyFrame.this.panel.setDividerLocation(.7);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				MyFrame.this.panel.setDividerLocation(.7);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				MyFrame.this.panel.setDividerLocation(.7);
			}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		this.gameBoardPanel = new GameBoardPanel();
		this.infoPanel = new InfoPanel();
		this.panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.gameBoardPanel, this.infoPanel);
		this.panel.setContinuousLayout(false);
		this.panel.setDividerSize(0);

		this.getContentPane().add(this.panel);
		this.addMenuBar();
		this.setVisible(true);
		this.toFront();
	}

	public void start() {
		this.infoPanel.startGame();
		this.gameBoardPanel.startGame();
	}

	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu newGameMenu = new JMenu("New Game");
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

		newGameMenu.add(onlinePlay);
		newGameMenu.add(againstComputerMenu);

		menuBar.add(newGameMenu);

		JMenu settingsMenu = new JMenu("Settings");
		JMenuItem settingsMenuItem = new JMenuItem("Settings");
		settingsMenuItem.addActionListener(e -> new ClientSettingsDialog());
		settingsMenu.add(settingsMenuItem);

		menuBar.add(settingsMenu);

		this.setJMenuBar(menuBar);
	}

}
