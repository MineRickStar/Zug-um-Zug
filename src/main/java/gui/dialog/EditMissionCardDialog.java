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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import application.Application;
import application.Property;
import game.Game;
import game.cards.MissionCard;
import gui.AllJMissionCardsPanel;
import gui.JMissionCardPanel;

public class EditMissionCardDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = -957166312584855702L;

	private static EditMissionCardDialog instance;

	public static void create() {
		if (EditMissionCardDialog.instance == null) {
			EditMissionCardDialog.instance = new EditMissionCardDialog(Game.getInstance().getInstancePlayer().getMissionCards());
		}
		EditMissionCardDialog.instance.oldMissionCards = new ArrayList<>(EditMissionCardDialog.instance.missionCardPanel.getMissionCardPanelList().stream().map(j -> {
			EditableMissionCardPanel panel = new EditableMissionCardPanel(j.missionCard);
			panel.checkBox.setSelected(((EditableMissionCardPanel) j).checkBox.isSelected());
			return panel;
		}).toList());
		EditMissionCardDialog.instance.setVisible(true);
	}

	private List<JMissionCardPanel> oldMissionCards = Collections.emptyList();

	private AllJMissionCardsPanel missionCardPanel;

	private EditableMissionCardPanel selectedMissionCard;

	private EditorPanel editorPanel;

	private EditMissionCardDialog(List<MissionCard> missionCards) {
		super(Application.frame, "Edit Mission Cards", true);
		this.setLayout(new GridBagLayout());
		this.addListeners();

		this.missionCardPanel = new AllJMissionCardsPanel();
		missionCards.stream().map(m -> {
			EditableMissionCardPanel panel = new EditableMissionCardPanel(m);
			panel.addMouseListener(new MouseHighlighter());
			return panel;
		}).forEach(this.missionCardPanel::addMissionCard);
		this.missionCardPanel.addMouseListener(new MouseConsumer());

		this.editorPanel = new EditorPanel();

		this.addComponents();
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
	}

	private void addListeners() {
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDSADDED, this);
		Game.getInstance().addPropertyChangeListener(Property.MISSIONCARDFINISHED, this);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e) {
				EditMissionCardDialog.this.setSelectedMissionPanel(null);
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if ((e.getModifiersEx() & InputEvent.BUTTON1_DOWN_MASK) == InputEvent.BUTTON1_DOWN_MASK) {
					EditMissionCardDialog.this.setSelectedMissionPanel(null);
				}
			}
		});
		((JPanel) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Escape");
		((JPanel) this.getContentPane()).getActionMap().put("Escape", new AbstractAction() {
			private static final long serialVersionUID = 5126913784742276734L;

			@Override
			public void actionPerformed(ActionEvent e) {
				EditMissionCardDialog.this.setSelectedMissionPanel(null);
			}
		});
		((JPanel) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK), "Close");
		((JPanel) this.getContentPane()).getActionMap().put("Close", new AbstractAction() {
			private static final long serialVersionUID = 5126913784742276734L;

			@Override
			public void actionPerformed(ActionEvent e) {
				EditMissionCardDialog.this.dispose();
			}
		});
	}

	private void addComponents() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridy = 0;
		gbc.gridx = 0;
		this.add(this.missionCardPanel, gbc);
		gbc.gridy = 1;
		this.add(this.editorPanel, gbc);
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(this.getButtonPanel(), gbc);
	}

	private JPanel getButtonPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> this.getOKAction().actionPerformed(e));
		okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "OK");
		okButton.getActionMap().put("OK", this.getOKAction());

		JButton cancelButton = new JButton("Cancle");
		cancelButton.addActionListener(e -> this.setVisible(false));
		panel.add(okButton);
		panel.add(cancelButton);
		return panel;
	}

	private AbstractAction getOKAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = -293749696673130324L;

			@Override
			public void actionPerformed(ActionEvent e) {
				EditMissionCardDialog.this.missionCardPanel.getMissionCardPanelList().stream().map(j -> (EditableMissionCardPanel) j).filter(j -> {
					Optional<JMissionCardPanel> optional = EditMissionCardDialog.this.oldMissionCards.stream().filter(o -> o.missionCard.equals(j.missionCard)).findAny();
					if (optional.isPresent()) {
						EditableMissionCardPanel oldPanel = (EditableMissionCardPanel) optional.get();
						return j.checkBox.isSelected() != oldPanel.checkBox.isSelected();
					}
					return j.checkBox.isSelected();
				}).forEach(missionCard -> Game.getInstance().fireAction(missionCard.missionCard, Property.MISSIONCARDHIDDEN, !missionCard.checkBox.isSelected(), missionCard.checkBox.isSelected()));
				List<JMissionCardPanel> allPanels = EditMissionCardDialog.this.missionCardPanel.getMissionCardPanelList();
				List<Integer> oldLocations = new ArrayList<>(allPanels.size());
				List<Integer> newLocations = new ArrayList<>(allPanels.size());
				for (int i = 0, max = EditMissionCardDialog.this.oldMissionCards.size(); i < max; i++) {
					oldLocations.add(i);
					newLocations.add(allPanels.indexOf(EditMissionCardDialog.this.oldMissionCards.get(i)));
				}
				Game.getInstance()
					.fireAction(EditMissionCardDialog.this, Property.MISSIONCARDINDEXCHANGE, oldLocations.stream().mapToInt(i -> i).toArray(), newLocations.stream().mapToInt(i -> i).toArray());
				EditMissionCardDialog.this.setVisible(false);
				Application.frame.revalidate();
				Application.frame.repaint();
			}
		};
	}

	private void setSelectedMissionPanel(EditableMissionCardPanel newPanel) {
		if (this.selectedMissionCard != null) {
			this.selectedMissionCard.setBackground(Color.WHITE);
		}
		this.selectedMissionCard = newPanel;
		EditMissionCardDialog.this.editorPanel.updateEditorPanel(EditMissionCardDialog.this.missionCardPanel.indexOf(this.selectedMissionCard));
	}

	private static class EditableMissionCardPanel extends JMissionCardPanel {

		private static final long serialVersionUID = -6237869940616496318L;

		private JPanel dummyPanel;
		private JCheckBox checkBox;

		private EditableMissionCardPanel(MissionCard missioncard) {
			super(missioncard);
			this.checkBox = new JCheckBox("Hide");
			this.checkBox.setFocusable(false);
			this.checkBox.addActionListener(e -> {
				if (this.checkBox.isSelected()) {
					this.setForeground(Color.LIGHT_GRAY);
				} else {
					this.setForeground(Color.BLACK);
				}
				this.revalidate();
				this.repaint();
			});
			this.dummyPanel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 5, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			this.dummyPanel.add(this.checkBox, gbc);
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.gridx = 1;
			this.dummyPanel.add(new JLabel(), gbc);
			this.add(this.dummyPanel, BorderLayout.SOUTH);
			this.setBackground(Color.WHITE);
		}

		@Override
		public void setBackground(Color color) {
			super.setBackground(color);
			if (this.dummyPanel != null) {
				this.dummyPanel.setBackground(color);
			}
			if (this.checkBox != null) {
				this.checkBox.setBackground(color);
			}
		}

		@Override
		public void setForeground(Color color) {
			super.setForeground(color);
			if (this.checkBox != null) {
				this.checkBox.setForeground(color);
			}
		}

		@Override
		public boolean isPanelDisplayable() {
			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.checkBox);
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}

	}

	private class EditorPanel extends JPanel {

		private static final long serialVersionUID = -8788756576662888192L;

		JButton moveFirst = new JButton("<< Move First <<");
		JButton moveUp = new JButton("^ Move Up ^");
		JButton moveDown = new JButton("v Move Down v");
		JButton moveRigth = new JButton("> Move Rigth >");
		JButton moveLeft = new JButton("< Move Left <");
		JButton moveLast = new JButton(">> Move Last >>");

		private EditorPanel() {
			super(new BorderLayout(10, 10));
			this.addMouseListener(new MouseConsumer());
			this.moveFirst.addActionListener(e -> this.move(GridBagConstraints.FIRST_LINE_START));
			this.moveUp.addActionListener(e -> this.move(GridBagConstraints.NORTH));
			this.moveDown.addActionListener(e -> this.move(GridBagConstraints.SOUTH));
			this.moveRigth.addActionListener(e -> this.move(GridBagConstraints.EAST));
			this.moveLeft.addActionListener(e -> this.move(GridBagConstraints.WEST));
			this.moveLast.addActionListener(e -> this.move(GridBagConstraints.LAST_LINE_END));

			JPanel innerPanel = new JPanel(new BorderLayout(10, 10));
			innerPanel.add(this.moveUp, BorderLayout.NORTH);
			innerPanel.add(this.moveLeft, BorderLayout.WEST);
			innerPanel.add(this.moveRigth, BorderLayout.EAST);
			innerPanel.add(this.moveDown, BorderLayout.SOUTH);

			this.add(this.moveFirst, BorderLayout.NORTH);
			this.add(innerPanel, BorderLayout.CENTER);
			this.add(this.moveLast, BorderLayout.SOUTH);
			this.resetEditorButtons();
		}

		private void move(int direction) {
			int index = EditMissionCardDialog.this.missionCardPanel.indexOf(EditMissionCardDialog.this.selectedMissionCard);
			EditMissionCardDialog.this.missionCardPanel.movePanel(index, direction);
			this.updateEditorPanel(EditMissionCardDialog.this.missionCardPanel.newIndex(index, direction));
		}

		private void updateEditorPanel(int index) {
			this.moveFirst.setEnabled(EditMissionCardDialog.this.missionCardPanel.testAction(index, GridBagConstraints.FIRST_LINE_START));
			this.moveUp.setEnabled(EditMissionCardDialog.this.missionCardPanel.testAction(index, GridBagConstraints.NORTH));
			this.moveDown.setEnabled(EditMissionCardDialog.this.missionCardPanel.testAction(index, GridBagConstraints.SOUTH));
			this.moveRigth.setEnabled(EditMissionCardDialog.this.missionCardPanel.testAction(index, GridBagConstraints.EAST));
			this.moveLeft.setEnabled(EditMissionCardDialog.this.missionCardPanel.testAction(index, GridBagConstraints.WEST));
			this.moveLast.setEnabled(EditMissionCardDialog.this.missionCardPanel.testAction(index, GridBagConstraints.LAST_LINE_END));
		}

		private void resetEditorButtons() {
			this.moveFirst.setEnabled(false);
			this.moveUp.setEnabled(false);
			this.moveLeft.setEnabled(false);
			this.moveRigth.setEnabled(false);
			this.moveDown.setEnabled(false);
			this.moveLast.setEnabled(false);
		}
	}

	private class MouseHighlighter extends MouseAdapter {
		@Override
		public void mouseExited(MouseEvent e) {
			JMissionCardPanel source = (JMissionCardPanel) e.getComponent();
			if (!source.equals(EditMissionCardDialog.this.selectedMissionCard)) {
				source.setBackground(Color.WHITE);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			JMissionCardPanel source = (JMissionCardPanel) e.getComponent();
			if (!source.equals(EditMissionCardDialog.this.selectedMissionCard)) {
				source.setBackground(Color.LIGHT_GRAY.darker());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			EditableMissionCardPanel source = (EditableMissionCardPanel) e.getComponent();
			EditMissionCardDialog.this.setSelectedMissionPanel(source);
			source.setBackground(Color.DARK_GRAY);
		}
	}

	private class MouseConsumer extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			e.consume();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.MISSIONCARDSADDED.name().equals(evt.getPropertyName())) {
			Stream.of((MissionCard[]) evt.getNewValue()).map(m -> {
				EditableMissionCardPanel panel = new EditableMissionCardPanel(m);
				panel.addMouseListener(new MouseHighlighter());
				return panel;
			}).forEach(this.missionCardPanel::addMissionCard);
			this.pack();
			this.setLocationRelativeTo(Application.frame);
		} else if (Property.MISSIONCARDFINISHED.name().equals(evt.getPropertyName())) {
			this.oldMissionCards.removeIf(j -> j.missionCard.equals(evt.getNewValue()));
			this.missionCardPanel.getMissionCardPanelList().removeIf(j -> j.missionCard.equals(evt.getNewValue()));
			this.missionCardPanel.update();
		}
	}

}
