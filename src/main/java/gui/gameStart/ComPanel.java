package gui.gameStart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import application.Application;
import game.Computer.Difficulty;
import game.Game;
import game.cards.ColorCard.MyColor;
import language.MyResourceBundle.LanguageKey;

public class ComPanel extends AbstractTabbedPanel {

	private static final long serialVersionUID = 9022206666739844613L;

	private static final int DEFAULT_OPPONENT_NAME_SIZE = 30;
	private static final String COM_NAME = "Com";

	private final int maxComs = 4;

	private JPanel comPanel;

	private List<SingelComPanel> singelComPanels;
	private JButton addComButton;

	public ComPanel(GameStartDialog parent) {
		super(parent);
		this.comPanel = new JPanel(new GridLayout(this.maxComs, 1, 10, 10));
		this.singelComPanels = new ArrayList<>();
		this.addComButton = new JButton(Application.resources.getString(LanguageKey.ADD));
		this.addComButton.addActionListener(e -> this.addComPanel());
		this.addComPanel();
		this.layoutComponents();
	}

	@Override
	protected void layoutComponents() {
		this.gbc.insets = new Insets(10, 10, 10, 10);
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		this.add(this.comPanel, this.gbc);

		this.gbc.anchor = GridBagConstraints.LINE_START;
		this.gbc.gridy = 1;
		this.add(this.addComButton, this.gbc);
	}

	private void addComPanel() {
		if (this.singelComPanels.size() < this.maxComs) {
			this.singelComPanels.add(new SingelComPanel());
			if (this.singelComPanels.size() == this.maxComs) {
				this.addComButton.setEnabled(false);
				this.revalidate();
				this.repaint();
			}
			this.updateLayout();
		}
	}

	private void removeComPanel(SingelComPanel panel) {
		this.addComButton.setEnabled(true);
		if (this.singelComPanels.size() > 1) {
			this.singelComPanels.remove(panel);
			MyColor item = (MyColor) panel.colorSelection.getSelectedItem();
			if (item != null) {
				this.singelComPanels.forEach(comPanel -> ((DefaultComboBoxModel<MyColor>) comPanel.colorSelection.getModel()).insertElementAt(item, List.of(this.getAvailableColors()).indexOf(item)));
			}
			int counter = 1;
			for (SingelComPanel comPanel : this.singelComPanels) {
				if (!comPanel.nameChange) {
					comPanel.opponentName.setText(ComPanel.COM_NAME + counter++);
				}
			}
			this.updateLayout();
		}
	}

	private MyColor[] getAvailableColors() {
		return List.of(MyColor.getNormalMyColors()).stream().filter(m -> {
			if (!Game.getInstance().isColorAvailable(m)) { return false; }
			if (this.singelComPanels.size() == 0) { return true; }
			return !this.singelComPanels.stream().anyMatch(c -> {
				Object item = c.colorSelection.getSelectedItem();
				return item == null ? false : item.equals(m);
			});
		}).toArray(MyColor[]::new);
	}

	private void updateLayout() {
		this.comPanel.removeAll();
		for (int i = 0; i < this.maxComs; i++) {
			if (i < this.singelComPanels.size()) {
				this.comPanel.add(this.singelComPanels.get(i));
			} else {
				this.comPanel.add(new JPanel());
			}
		}
		this.revalidate();
		this.repaint();
	}

	private final class SingelComPanel extends JPanel {

		private static final long serialVersionUID = -4831348484788838388L;

		boolean nameChange = false;
		private JButton removeOpponent;
		private JTextField opponentName;
		private JSlider opponentLevel;
		private JComboBox<MyColor> colorSelection;
		// TODO Color Selection bei Coms funktioniert immer noch nicht

		private SingelComPanel() {
			this.removeOpponent = new JButton(Application.resources.getString(LanguageKey.REMOVE));
			this.removeOpponent.addActionListener(e -> {
				if (ComPanel.this.singelComPanels.size() > 1) {
					ComPanel.this.removeComPanel(this);
				} else {
					JOptionPane.showMessageDialog(this, Application.resources.getString(LanguageKey.ATLEASTONECOMPONENT));
				}
			});
			this.opponentName = new JTextField(ComPanel.COM_NAME + (ComPanel.this.singelComPanels.size() + 1), ComPanel.DEFAULT_OPPONENT_NAME_SIZE);
			this.opponentName.setFocusable(true);
			this.opponentName.setDoubleBuffered(true);
			this.opponentName.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					SingelComPanel.this.nameChange = true;
					if (e.getKeyChar() == KeyEvent.VK_ENTER) {
						SingelComPanel.this.opponentLevel.requestFocusInWindow();
					}
				}
			});
			this.opponentLevel = this.getOpponentLevelSlider();
			this.colorSelection = this.getColorSelection();
			this.createPanel();
		}

		private JSlider getOpponentLevelSlider() {
			Difficulty[] difficulties = Difficulty.values();
			JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 1, difficulties.length, 2);
			slider.setMinorTickSpacing(1);
			slider.setMajorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);
			slider.setFocusable(true);
			slider.setDoubleBuffered(true);

			Hashtable<Integer, JLabel> dic = new Hashtable<>(difficulties.length);
			List.of(difficulties).forEach(d -> dic.put(d.index, new JLabel(d.getDisplayName())));
			slider.setLabelTable(dic);

			return slider;
		}

		private JComboBox<MyColor> getColorSelection() {
			DefaultComboBoxModel<MyColor> model = new DefaultComboBoxModel<>(ComPanel.this.getAvailableColors());
			JComboBox<MyColor> colorSelection = new JComboBox<>(model);
			colorSelection.addItemListener(e -> {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ComPanel.this.singelComPanels.forEach(c -> {
						if (c == this) { return; }
						((DefaultComboBoxModel<MyColor>) c.colorSelection.getModel()).removeElement(e.getItem());
					});
				} else if (e.getStateChange() == ItemEvent.DESELECTED) {
					ComPanel.this.singelComPanels.forEach(c -> {
						if (c == this) { return; }
						((DefaultComboBoxModel<MyColor>) c.colorSelection.getModel()).insertElementAt((MyColor) e.getItem(), List.of(ComPanel.this.getAvailableColors()).indexOf(e.getItem()));
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

	@Override
	public String getDisplayName() {
		return Application.resources.getString(LanguageKey.COMS);
	}

	@Override
	public boolean isAllCorrect() {
		for (SingelComPanel panel : this.singelComPanels) {
			String opponentName = panel.opponentName.getText();
			if (opponentName.isEmpty()) {
				JOptionPane.showMessageDialog(this, Application.resources.getString(LanguageKey.COMNAMENOTEMPTY));
				return false;
			} else if (opponentName.isBlank()) {
				JOptionPane.showMessageDialog(this, Application.resources.getString(LanguageKey.COMNAMENOTBLANK));
				return false;
			}
			if (panel.colorSelection.getSelectedIndex() == -1) {
				JOptionPane.showMessageDialog(this, Application.resources.getString(LanguageKey.COLORMUSTBESELECTED));
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean save() {
		for (SingelComPanel panel : this.singelComPanels) {
			boolean success = Game.getInstance()
					.addComputer(panel.opponentName.getText(), (MyColor) panel.colorSelection.getSelectedItem(), Difficulty.getDifficultyWithIndex(panel.opponentLevel.getValue()));
			if (!success) { return false; }
		}
		return true;
	}

}
