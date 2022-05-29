package gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import application.PropertyEvent;

public class GamePanel extends JPanel implements IUpdatePanel {

	private static final long serialVersionUID = -8860204251354754377L;

	private JSplitPane splitPane;

	private GameBoardPanel gameBoardPanel;
	private InfoPanel infoPanel;
	private PlayerMissionCardPanel playerMissionCardPanel;

	public GamePanel() {
		super(new BorderLayout());
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.setDividerSize(0);

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				GamePanel.this.splitPane.setDividerLocation(.7);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				GamePanel.this.splitPane.setDividerLocation(.7);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				GamePanel.this.splitPane.setDividerLocation(.7);
			}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		this.gameBoardPanel = new GameBoardPanel();
		this.infoPanel = new InfoPanel();
		this.playerMissionCardPanel = new PlayerMissionCardPanel();

		this.splitPane.setLeftComponent(this.gameBoardPanel);
		this.splitPane.setRightComponent(this.infoPanel);

		this.add(this.splitPane, BorderLayout.CENTER);
		this.add(this.playerMissionCardPanel, BorderLayout.SOUTH);

	}

	@Override
	public void update(PropertyEvent propertyEvent) {
		this.playerMissionCardPanel.update(propertyEvent);
		this.gameBoardPanel.update(propertyEvent);
		this.infoPanel.update(propertyEvent);
	}

}
