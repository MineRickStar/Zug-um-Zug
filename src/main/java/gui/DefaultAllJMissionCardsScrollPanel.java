package gui;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import application.Application;

public class DefaultAllJMissionCardsScrollPanel extends DefaultAllJMissionCardsPanel implements Scrollable {

	private static final long serialVersionUID = 6581927893465655290L;

	private int maxHeight;
	private int maxWidth;

	public DefaultAllJMissionCardsScrollPanel(int rowCount, int columnCount) {
		this(rowCount, columnCount, Application.frame.getHeight() >> 1, (int) (Application.frame.getWidth() * .33));
	}

	public DefaultAllJMissionCardsScrollPanel(int rowCount, int columnCount, int maxHeight, int maxWidth) {
		super(rowCount, columnCount);
		this.maxHeight = maxHeight;
		this.maxWidth = maxWidth;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		Dimension supPref = super.getPreferredSize();
		if ((this.getCount() == 0) || (this.missionCardPanelList.get(0).getPreferredSize().height == 0)) { return supPref; }

		int width = Math.min(this.maxWidth == 0 ? Integer.MAX_VALUE : this.maxWidth, supPref.width);
		int height = Math.min(this.maxHeight == 0 ? Integer.MAX_VALUE : this.maxHeight, supPref.height);

		return new Dimension(width, height);
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL) {
			return 50;
		} else {
			return 80;
		}
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		if (orientation == SwingConstants.VERTICAL) {
			return 50;
		} else {
			return 80;
		}
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

}
