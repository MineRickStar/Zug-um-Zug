package gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JSplitPane;

import application.PropertyEvent;

public class InfoPanel extends JSplitPane implements IUpdatePanel {

	private static final long serialVersionUID = -6143438348516086903L;

	private PublicPanel publicPanel;
	private PlayerPanel playerPanel;

	public InfoPanel() {
		super(JSplitPane.VERTICAL_SPLIT);
		this.setContinuousLayout(false);
		this.setDividerSize(0);

		this.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
				InfoPanel.this.setDividerLocation(.2);
			}

			@Override
			public void componentResized(ComponentEvent e) {
				InfoPanel.this.setDividerLocation(.2);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				InfoPanel.this.setDividerLocation(.2);
			}

			@Override
			public void componentHidden(ComponentEvent e) {}
		});
		this.publicPanel = new PublicPanel();
		this.playerPanel = new PlayerPanel();

		this.setTopComponent(this.publicPanel);
		this.setBottomComponent(this.playerPanel);
	}

	@Override
	public void update(PropertyEvent propertyEvent) {
		this.playerPanel.update(propertyEvent);
		this.publicPanel.update(propertyEvent);
	}

}
