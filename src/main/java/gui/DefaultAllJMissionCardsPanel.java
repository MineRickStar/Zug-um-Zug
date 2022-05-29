package gui;

public class DefaultAllJMissionCardsPanel extends AllJMissionCardsPanel {

	private static final long serialVersionUID = 192266763354481165L;

	private int rowCount;
	private int columnCount;

	private boolean keepRowCount;
	private boolean keepColumnCount;

	public DefaultAllJMissionCardsPanel(int rowCount, int columnCount) {
		this.rowCount = rowCount;
		this.keepRowCount = rowCount != -1;
		this.columnCount = columnCount;
		this.keepColumnCount = columnCount != -1;
	}

	@Override
	protected int getRowCount() {
		return this.keepRowCount ? this.rowCount : (this.rowCount = this.getCount() / this.columnCount);
	}

	@Override
	protected int getColumnCount() {
		return this.keepColumnCount ? this.columnCount : (this.columnCount = this.getCount() / this.rowCount);
	}

}
