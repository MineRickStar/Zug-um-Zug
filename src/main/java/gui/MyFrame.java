package gui;

import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import game.Game;

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

		this.getContentPane()
				.add(this.panel);
		this.addMenuBar();
		this.setVisible(true);
		this.toFront();
	}

	public void start() {
		this.infoPanel.startGame();
	}

	private void addMenuBar() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("New Game");

		JMenuItem menuItem = new JMenuItem("Against Computer");

		menuItem.addActionListener(e -> new Thread(() -> Game.getInstance()
				.startComputerGame()).start());

		menu.add(menuItem);

		menuBar.add(menu);
		this.setJMenuBar(menuBar);
	}

}
