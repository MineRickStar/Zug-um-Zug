package gui.gameStart;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public abstract class AbstractTabbedPanel extends JPanel {

	private static final long serialVersionUID = -2376785774727437507L;

	protected GameStartDialog parent;

	protected GridBagConstraints gbc;

	protected AbstractTabbedPanel(GameStartDialog startDialog) {
		super(new GridBagLayout());
		this.parent = startDialog;
		this.gbc = new GridBagConstraints();
	}

	protected abstract void layoutComponents();

	protected final void nextColumn(JPanel panel, GridBagConstraints gbc, JComponent... components) {
		gbc.gridx++;
		for (int i = 0; i < components.length; i++) {
			gbc.gridy = i;
			if (components[i] != null) {
				panel.add(components[i], gbc);
			}
		}
	}

	Border getTitleBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2), title);
	}

	protected final void addTabbing(int gridWidth) {
		this.gbc.gridx = 0;
		this.gbc.gridy++;
		this.gbc.gridwidth = gridWidth + 1;
		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.weightx = 1;
		this.gbc.weighty = 1;
		this.add(new JPanel(), this.gbc);
	}

	public abstract String getDisplayName();

	public abstract boolean isAllCorrect();

	public abstract boolean save();

	protected class MySpinner extends JSpinner {
		private static final long serialVersionUID = 6649165971696022099L;
		int defaultValue;

		protected MySpinner(int initialValue, int minValue, int maxValue) {
			super(new SpinnerNumberModel(initialValue, minValue, maxValue, 1));
			this.defaultValue = initialValue;
			JFormattedTextField textField = ((NumberEditor) this.getEditor()).getTextField();
			textField.setHorizontalAlignment(SwingConstants.CENTER);
			textField.setColumns(5);
		}

		protected void reset() {
			this.setValue(this.defaultValue);
		}

		public int getIntValue() {
			return (int) this.getValue();
		}

	}

}
