package gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JSplitPane;

public class GamePanel extends JSplitPane {

	private static final long serialVersionUID = -8860204251354754377L;

	private GameBoardPanel gameBoardPanel;
	private InfoPanel infoPanel;

	public GamePanel() {
		super(JSplitPane.HORIZONTAL_SPLIT);
		this.setContinuousLayout(false);
		this.setDividerSize(0);

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				GamePanel.this.setDividerLocation(.7);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				GamePanel.this.setDividerLocation(.7);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				GamePanel.this.setDividerLocation(.7);
			}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		this.gameBoardPanel = new GameBoardPanel();
		this.infoPanel = new InfoPanel();

		this.setLeftComponent(this.gameBoardPanel);
		this.setRightComponent(this.infoPanel);

	}

}
