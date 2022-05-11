package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import application.Application;
import game.Game;
import game.cards.MyColor;

public class ComputerPlayDialog extends JDialog {

	private static final long serialVersionUID = 3133143941114817863L;

	private static final int DEFAULT_OPPONENT_NAME_SIZE = 30;
	private static final String COM_NAME = "Com";

	private JPanel panel;

	private JPanel comPanel;

	private List<ComPanel> comPanels;
	private JButton addComButton;

	private boolean canceled = false;

	public ComputerPlayDialog() {
		super(Application.frame, "Select Opponents", true);
		this.panel = new JPanel(new GridBagLayout());
		this.comPanel = new JPanel(new GridBagLayout());
		this.comPanels = new ArrayList<>();
		this.addComButton = new JButton("Add");
		this.addComButton.addActionListener(e -> this.addComPanel());
		this.addComPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.gridy = 0;
		this.panel.add(this.comPanel, gbc);
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 10, 10, 10);
		this.panel.add(this.createButtonPanel(), gbc);
		this.add(this.panel);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ComputerPlayDialog.this.canceled = true;
			}
		});
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);

	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			for (ComPanel panel : this.comPanels) {
				String opponentName = panel.opponentName.getText();
				if (opponentName.isEmpty()) {
					JOptionPane.showMessageDialog(this, "Name must not be emtpy");
					return;
				} else if (opponentName.isBlank()) {
					JOptionPane.showMessageDialog(this, "Name must not be blank");
					return;
				}
				if (panel.colorSelection.getSelectedIndex() == -1) {
					JOptionPane.showMessageDialog(this, "Color must be selected");
					return;
				}
			}
			this.comPanels.forEach(c -> {
				Game.getInstance().addComputer(c.opponentName.getText(), (MyColor) c.colorSelection.getSelectedItem(), c.opponentLevel.getValue());
			});
			this.dispose();
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			this.canceled = true;
			this.dispose();
		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void addComPanel() {
		if (this.comPanels.size() < 4) {
			this.comPanels.add(new ComPanel());
			this.updateLayout();
		}
	}

	private void removeComPanel(ComPanel panel) {
		if (this.comPanels.size() > 1) {
			this.comPanels.remove(panel);
			MyColor item = (MyColor) panel.colorSelection.getSelectedItem();
			if (item != null) {
				this.comPanels.forEach(comPanel -> ((DefaultComboBoxModel<MyColor>) comPanel.colorSelection.getModel()).insertElementAt(item, List.of(this.getAvailableColors()).indexOf(item)));
			}
			int counter = 1;
			for (ComPanel comPanel : this.comPanels) {
				if (!comPanel.nameChange) {
					comPanel.opponentName.setText(ComputerPlayDialog.COM_NAME + counter++);
				}
			}
			this.updateLayout();
		}
	}

	private MyColor[] getAvailableColors() {
		return List.of(MyColor.getNormalMyColors()).stream().filter(m -> {
			if (!Game.getInstance().isColorAvailable(m)) { return false; }
			if (this.comPanels.size() == 0) { return true; }
			return !this.comPanels.stream().anyMatch(c -> {
				Object item = c.colorSelection.getSelectedItem();
				return item == null ? false : item.equals(m);
			});
		}).toArray(MyColor[]::new);
	}

	private void updateLayout() {
		this.comPanel.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.gridy = 0;
		for (ComPanel panel : this.comPanels) {
			this.comPanel.add(panel, gbc);
			gbc.gridy++;
		}
		if (this.comPanels.size() < 4) {
			this.comPanel.add(this.addComButton, gbc);
		}
		this.revalidate();
		this.repaint();
		this.pack();
	}

	public boolean wasCanceled() {
		return this.canceled;
	}

	private final class ComPanel extends JPanel {

		private static final long serialVersionUID = -4831348484788838388L;

		boolean nameChange = false;
		private JButton removeOpponent;
		private JTextField opponentName;
		private JSlider opponentLevel;
		private JComboBox<MyColor> colorSelection;

		private ComPanel() {
			this.removeOpponent = new JButton("Remove");
			this.removeOpponent.addActionListener(e -> {
				if (ComputerPlayDialog.this.comPanels.size() > 1) {
					ComputerPlayDialog.this.removeComPanel(this);
				} else {
					JOptionPane.showMessageDialog(this, "Must have at least one Opponent");
				}
			});
			this.opponentName = new JTextField(ComputerPlayDialog.COM_NAME + (ComputerPlayDialog.this.comPanels.size() + 1), ComputerPlayDialog.DEFAULT_OPPONENT_NAME_SIZE);
			this.opponentName.setFocusable(true);
			this.opponentName.setDoubleBuffered(true);
			this.opponentName.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					ComPanel.this.nameChange = true;
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						ComPanel.this.opponentLevel.requestFocusInWindow();
					}
				}
			});
			this.opponentLevel = this.getOpponentLevelSlider();
			this.colorSelection = this.getColorSelection();
			this.createPanel();
		}

		private JSlider getOpponentLevelSlider() {
			JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 1, 4, 2);
			slider.setMinorTickSpacing(1);
			slider.setMajorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);
			slider.setFocusable(true);
			slider.setDoubleBuffered(true);

			Hashtable<Integer, JLabel> dic = new Hashtable<>(4);
			dic.put(1, new JLabel("Easy"));
			dic.put(2, new JLabel("Medium"));
			dic.put(3, new JLabel("Hard"));
			dic.put(4, new JLabel("Extreme"));
			slider.setLabelTable(dic);

			return slider;
		}

		private JComboBox<MyColor> getColorSelection() {
			DefaultComboBoxModel<MyColor> model = new DefaultComboBoxModel<>(ComputerPlayDialog.this.getAvailableColors());
			JComboBox<MyColor> colorSelection = new JComboBox<>(model);
			colorSelection.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ComputerPlayDialog.this.comPanels.forEach(c -> {
						if (c == this) { return; }
						((DefaultComboBoxModel<MyColor>) c.colorSelection.getModel()).removeElement(e.getItem());
					});
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					ComputerPlayDialog.this.comPanels.forEach(c -> {
						if (c == this) { return; }
						((DefaultComboBoxModel<MyColor>) c.colorSelection.getModel()).insertElementAt((MyColor) e.getItem(),
								List.of(ComputerPlayDialog.this.getAvailableColors()).indexOf(e.getItem()));
					});
				}
			});
			return colorSelection;
		}

		private void createPanel() {
			this.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.insets = new Insets(0, 0, 0, 10);
			this.add(this.removeOpponent, gbc);
			gbc.insets = new Insets(0, 10, 0, 10);
			gbc.gridx = 1;
			this.add(this.opponentName, gbc);
			gbc.gridx = 2;
			this.add(this.opponentLevel, gbc);
			gbc.gridx = 3;
			gbc.insets = new Insets(0, 10, 0, 0);
			this.add(this.colorSelection, gbc);
		}

	}

}
