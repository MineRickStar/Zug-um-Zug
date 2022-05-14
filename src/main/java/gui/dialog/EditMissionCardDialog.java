package gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import application.Application;
import gui.AllJMissionCardsPanel;
import gui.IMissionCardPanel;
import gui.JMissionCardPanel;

public class EditMissionCardDialog extends JDialog {

	private static final long serialVersionUID = -957166312584855702L;

	private JPanel panel;

	private final AllJMissionCardsPanel originalPanel;

	private AllJMissionCardsPanel missionCardPanel;

	private JMissionCardPanelWithCheckBox selectedMissionCard;

	private EditorPanel editorPanel;

	public EditMissionCardDialog(AllJMissionCardsPanel missionCardPanel) {
		super(Application.frame, "Edit Mission Cards", true);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK && !e.isControlDown()) {
					EditMissionCardDialog.this.resetSelectedMissions();
				}
			}
		});
		this.originalPanel = missionCardPanel;
		this.panel = new JPanel(new GridBagLayout());
		List<IMissionCardPanel> missions = this.originalPanel.getMissionCardPanelList();
		List<IMissionCardPanel> missionCards = new ArrayList<>(missions.stream().map(JMissionCardPanelWithCheckBox::new).toList());
		missionCards.forEach(j -> j.getPanel().addMouseListener(new MouseHighlighter()));
		this.missionCardPanel = new AllJMissionCardsPanel();
		this.missionCardPanel.setIgnoreMissionCardVisibility(true);
		this.missionCardPanel.addAllMissionPanel(missionCards);
		this.missionCardPanel.addMouseListener(new MouseConsumer());

		this.editorPanel = new EditorPanel();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridy = 0;
		gbc.gridx = 0;
		this.panel.add(this.missionCardPanel, gbc);
		gbc.gridy = 1;
		this.panel.add(this.editorPanel, gbc);
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.panel.add(this.getButtonPanel(), gbc);
		this.add(this.panel);
		((JPanel) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		((JPanel) this.getContentPane()).getActionMap().put("Escape", new AbstractAction() {
			private static final long serialVersionUID = 5126913784742276734L;

			@Override
			public void actionPerformed(ActionEvent e) {
				EditMissionCardDialog.this.resetSelectedMissions();
			}
		});
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private void move(int direction) {
		int index = this.missionCardPanel.getMissionCardPanelList().indexOf(this.selectedMissionCard);
		if (index != -1 && this.testAction(index, direction)) {
			int newIndex = this.newIndex(index, direction);
			JMissionCardPanelWithCheckBox missionCards = (JMissionCardPanelWithCheckBox) this.missionCardPanel.getMissionCardPanelList().remove(index);
			this.missionCardPanel.getMissionCardPanelList().add(newIndex, missionCards);
			this.missionCardPanel.update();
			this.editorPanel.updateEditorPanel(newIndex);
			this.revalidate();
			this.repaint();
		}
	}

	private int newIndex(int currentIndex, int direction) {
		if (!this.testAction(currentIndex, direction)) { return currentIndex; }
		switch (direction) {
		case GridBagConstraints.NORTH:
			return currentIndex - this.originalPanel.getColumnCount();
		case GridBagConstraints.EAST:
			return currentIndex + 1;
		case GridBagConstraints.SOUTH:
			return Math.min(currentIndex + this.originalPanel.getColumnCount(), this.missionCardPanel.getMissionCardCount() - 1);
		case GridBagConstraints.WEST:
			return currentIndex - 1;
		default:
			return currentIndex;
		}
	}

	private boolean testAction(int index, int direction) {
		switch (direction) {
		case GridBagConstraints.NORTH:
			return index >= this.originalPanel.getColumnCount();
		case GridBagConstraints.EAST:
			return index < this.missionCardPanel.getMissionCardCount() - 1;
		case GridBagConstraints.SOUTH:
			return index < this.originalPanel.getColumnCount() * (this.originalPanel.getRowCount() - 1);
		case GridBagConstraints.WEST:
			return index > 0;
		default:
			return false;
		}
	}

	private JPanel getButtonPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			this.originalPanel.setMissionCardPanelList(this.missionCardPanel.getMissionCardPanelList().stream().map(j -> (IMissionCardPanel) ((JMissionCardPanelWithCheckBox) j).panel).toList());
			this.originalPanel.update();
			this.dispose();
			Application.frame.revalidate();
			Application.frame.repaint();
		});
		JButton cancelButton = new JButton("Cancle");
		cancelButton.addActionListener(e -> this.dispose());
		panel.add(okButton);
		panel.add(cancelButton);
		return panel;
	}

	private void resetSelectedMissions() {
		if (this.selectedMissionCard != null) {
			this.selectedMissionCard.setBackground(Color.WHITE);
			this.selectedMissionCard = null;
			this.editorPanel.resetEditorButtons();
		}
	}

	private class JMissionCardPanelWithCheckBox extends JMissionCardPanel implements IMissionCardPanel {

		private JMissionCardPanel panel;
		private static final long serialVersionUID = 8349711138698426199L;
		private JCheckBox box;

		public JMissionCardPanelWithCheckBox(IMissionCardPanel panel) {
			super(panel.getMissionCard());
			this.panel = (JMissionCardPanel) panel;
			this.setCardVisible(panel.isCardVisible());
			this.setCardFinished(panel.isCardFinished());
			this.box = new JCheckBox("Hide Mission");
			this.box.setForeground(panel.isCardVisible() ? Color.BLACK : Color.LIGHT_GRAY);
			this.box.setBackground(Color.WHITE);
			this.box.setSelected(!panel.isCardVisible());
			this.box.setFocusable(false);
			this.box.addActionListener(e -> {
				panel.setCardVisible(!this.box.isSelected());
				this.setCardVisible(!this.box.isSelected());
				this.box.setForeground(this.box.isSelected() ? Color.LIGHT_GRAY : Color.BLACK);
			});
			this.gbc.gridy++;
			this.add(this.box, this.gbc);
		}

		@Override
		public void setCardVisible(boolean cardVisible) {
			if (cardVisible) {
				this.distance.setForeground(Color.BLACK);
				this.points.setForeground(Color.BLACK);
				this.fromLocation.setForeground(Color.BLACK);
				this.overLocations.forEach(j -> j.setForeground(Color.BLACK));
				this.toLocation.setForeground(Color.BLACK);
			} else {
				this.distance.setForeground(Color.LIGHT_GRAY);
				this.points.setForeground(Color.LIGHT_GRAY);
				this.fromLocation.setForeground(Color.LIGHT_GRAY);
				this.overLocations.forEach(j -> j.setForeground(Color.LIGHT_GRAY));
				this.toLocation.setForeground(Color.LIGHT_GRAY);
			}
			super.setCardVisible(cardVisible);
		}

		@Override
		public void setBackground(Color color) {
			if (this.box != null) {
				this.box.setBackground(color);
			}
			super.setBackground(color);
		}

	}

	private class EditorPanel extends JPanel {

		private static final long serialVersionUID = -8788756576662888192L;

		JButton moveUp = new JButton("^ Move Up ^");
		JButton moveDown = new JButton("v Move Down v");
		JButton moveRigth = new JButton("> Move Rigth >");
		JButton moveLeft = new JButton("< Move Left <");

		private EditorPanel() {
			super(new BorderLayout(10, 10));
			this.addMouseListener(new MouseConsumer());
			this.moveUp.addActionListener(e -> EditMissionCardDialog.this.move(GridBagConstraints.NORTH));
			this.moveDown.addActionListener(e -> EditMissionCardDialog.this.move(GridBagConstraints.SOUTH));
			this.moveRigth.addActionListener(e -> EditMissionCardDialog.this.move(GridBagConstraints.EAST));
			this.moveLeft.addActionListener(e -> EditMissionCardDialog.this.move(GridBagConstraints.WEST));
			this.add(this.moveUp, BorderLayout.NORTH);
			this.add(this.moveLeft, BorderLayout.WEST);
			this.add(this.moveRigth, BorderLayout.EAST);
			this.add(this.moveDown, BorderLayout.SOUTH);
			this.resetEditorButtons();
		}

		private void updateEditorPanel(int index) {
			this.moveUp.setEnabled(EditMissionCardDialog.this.testAction(index, GridBagConstraints.NORTH));
			this.moveDown.setEnabled(EditMissionCardDialog.this.testAction(index, GridBagConstraints.SOUTH));
			this.moveRigth.setEnabled(EditMissionCardDialog.this.testAction(index, GridBagConstraints.EAST));
			this.moveLeft.setEnabled(EditMissionCardDialog.this.testAction(index, GridBagConstraints.WEST));
		}

		private void resetEditorButtons() {
//			this.moveUp.setEnabled(true);
//			this.moveLeft.setEnabled(true);
//			this.moveRigth.setEnabled(true);
//			this.moveDown.setEnabled(true);
//			if (EditMissionCardDialog.this.originalPanel.getMissionCardCount() == 1) {
			this.moveUp.setEnabled(false);
			this.moveLeft.setEnabled(false);
			this.moveRigth.setEnabled(false);
			this.moveDown.setEnabled(false);
//			}
//			if (EditMissionCardDialog.this.originalPanel.getRowCount() == 1) {
//				this.moveUp.setEnabled(false);
//				this.moveDown.setEnabled(false);
//			}
//			if (EditMissionCardDialog.this.originalPanel.getColumnCount() == 1) {
//				this.moveLeft.setEnabled(false);
//				this.moveRigth.setEnabled(false);
//			}
		}
	}

	private class MouseConsumer extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			e.consume();
		}
	}

	private class MouseHighlighter extends MouseAdapter {
		@Override
		public void mouseExited(MouseEvent e) {
			JMissionCardPanelWithCheckBox source = (JMissionCardPanelWithCheckBox) e.getSource();
			if (!source.equals(EditMissionCardDialog.this.selectedMissionCard)) {
				source.setBackground(Color.WHITE);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JMissionCardPanelWithCheckBox source = (JMissionCardPanelWithCheckBox) e.getSource();
			if (!source.equals(EditMissionCardDialog.this.selectedMissionCard)) {
				source.setBackground(Color.LIGHT_GRAY.darker());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			JMissionCardPanelWithCheckBox source = (JMissionCardPanelWithCheckBox) e.getSource();
			EditMissionCardDialog.this.resetSelectedMissions();
			EditMissionCardDialog.this.selectedMissionCard = source;
			EditMissionCardDialog.this.selectedMissionCard.setBackground(Color.DARK_GRAY);
			EditMissionCardDialog.this.editorPanel.updateEditorPanel(EditMissionCardDialog.this.missionCardPanel.getMissionCardPanelList().indexOf(source));
		}
	}

}
