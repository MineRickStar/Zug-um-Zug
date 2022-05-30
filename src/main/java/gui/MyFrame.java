package gui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import application.Application;
import application.PropertyEvent;
import application.PropertyEvent.Property;
import game.Game;
import gui.dialog.ClientSettingsDialog;
import gui.dialog.MapCreator;
import gui.dialog.OnlinePlayerDialogHostDialog;
import gui.dialog.OnlinePlayerDialogJoinDialog;
import gui.gameStart.GameStartDialog;
import language.MyResourceBundle.LanguageKey;

public class MyFrame extends JFrame {

	private static final long serialVersionUID = 4070509246110827584L;

	private IUpdatePanel currentPanel;
	private Timer timer;

	public MyFrame() {
		super(Application.NAME);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				MyFrame.this.resizeFrame();
			}
		});
		this.timer = new Timer("Frame Resizer");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setMinimumSize(Application.scaleDimension(Toolkit.getDefaultToolkit().getScreenSize(), .5));
		this.setSize(Application.scaleDimension(Toolkit.getDefaultToolkit().getScreenSize(), .5));
		this.setExtendedState(Frame.MAXIMIZED_BOTH);
		this.addMenuBar();
		this.setVisible(true);
		this.toFront();
	}

	private void resizeFrame() {
		this.timer.cancel();
		this.timer = new Timer("Frame Resizer");
		this.timer.schedule(new TimerTask() {

			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> MyFrame.this.update(new PropertyEvent(null, Property.FRAMESIZECHANGED)));
				SwingUtilities.invokeLater(() -> MyFrame.this.update(new PropertyEvent(null, Property.FRAMESIZECHANGED)));
			}
		}, 100);
	}

	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		menuBar.add(this.gameMenu());
		menuBar.add(this.settingsMenu());
		menuBar.add(this.mapCreatorMenu());

		this.setJMenuBar(menuBar);
	}

	private JMenu gameMenu() {
		JMenu gameMenu = new JMenu(Application.resources.getString(LanguageKey.GAME));

		JMenu onlinePlay = new JMenu(Application.resources.getString(LanguageKey.PLAYONLINE));

		JMenuItem onlinePlayHost = new JMenuItem(Application.resources.getString(LanguageKey.CREATENEWGAME));
		onlinePlayHost.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		onlinePlayHost.addActionListener(e -> new OnlinePlayerDialogHostDialog());
		JMenuItem onlinePlayJoin = new JMenuItem(Application.resources.getString(LanguageKey.JOINGAME));
		onlinePlayJoin.addActionListener(e -> new OnlinePlayerDialogJoinDialog());
		onlinePlay.add(onlinePlayHost);
		onlinePlay.add(onlinePlayJoin);

		JMenuItem againstComputerMenu = new JMenuItem(Application.resources.getString(LanguageKey.AGAINSTCOMPUTER));
		againstComputerMenu.addActionListener(e -> {
			if (!Game.getInstance().isGameStarted()) {
				new GameStartDialog();
			}
		});
		againstComputerMenu.setAccelerator(KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));

		JMenuItem quitGame = new JMenuItem(Application.resources.getString(LanguageKey.QUITGAME));
		quitGame.addActionListener(e -> Application.frame.dispose());

		gameMenu.add(onlinePlay);
		gameMenu.add(againstComputerMenu);
		gameMenu.add(quitGame);

		return gameMenu;
	}

	private JMenu settingsMenu() {
		JMenu settingsMenu = new JMenu(Application.resources.getString(LanguageKey.SETTINGS));

		settingsMenu.addActionListener(e -> new ClientSettingsDialog());

		return settingsMenu;
	}

	private JMenu mapCreatorMenu() {
		JMenu mapCreatorMenu = new JMenu(Application.resources.getString(LanguageKey.MAPCREATOR));

		JMenuItem mapCreatorMenuItem = new JMenuItem(Application.resources.getString(LanguageKey.CREATENEWMAP));
		mapCreatorMenuItem.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
		mapCreatorMenuItem.addActionListener(e -> {
			try {
				MapCreator.createNewMap();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		JMenuItem editMapMenuItem = new JMenuItem(Application.resources.getString(LanguageKey.EDITMAP));

		mapCreatorMenu.add(mapCreatorMenuItem);
		mapCreatorMenu.add(editMapMenuItem);

		return mapCreatorMenu;
	}

	public void startGame() {
		this.setComponent(new GamePanel());
	}

	public void mapEditor() {
		// TODO MapEditor
	}

	private void setComponent(IUpdatePanel com) {
		this.getContentPane().removeAll();
		this.getContentPane().add((Component) com);
		this.currentPanel = com;
		this.revalidate();
		this.repaint();
	}

	public void update(PropertyEvent propertyEvent) {
		if (this.currentPanel != null) {
			this.currentPanel.update(propertyEvent);
		}
		this.revalidate();
		this.repaint();
	}

}
