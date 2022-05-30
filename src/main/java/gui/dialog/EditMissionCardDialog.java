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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import application.Application;
import game.Game;
import game.cards.MissionCard;
import gui.DefaultAllJMissionCardsScrollPanel;
import gui.JMissionCardPanel;
import language.MyResourceBundle.LanguageKey;

public class EditMissionCardDialog extends JDialog {

	private static final long serialVersionUID = -957166312584855702L;

	private static EditMissionCardDialog instance;

	public static EditMissionCardDialog create() {
		if (EditMissionCardDialog.instance == null) {
			EditMissionCardDialog.instance = new EditMissionCardDialog();
		}
		EditMissionCardDialog.instance.update();
		EditMissionCardDialog.instance.setVisible(true);
		return EditMissionCardDialog.instance;
	}

	private List<JMissionCardPanel> oldMissionCards = Collections.emptyList();

	private EditAllJMissionCardsPanel missionCardPanel;
	private JScrollPane editAllJMissionCardsPanelScrollPane;

	private EditorPanel editorPanel;

	private Map<JMissionCardPanel, Boolean> missionCardPanelVisibility;
	private Map<Integer, MissionCard> missionCardPanelIndexes;

	private boolean edited;

	private EditMissionCardDialog() {
		super(Application.frame, Application.resources.getString(LanguageKey.EDITMISSIONCARDS), true);
		this.setLayout(new GridBagLayout());
		this.addListenersToDialog();

		this.missionCardPanel = new EditAllJMissionCardsPanel(-1, 4);
		this.missionCardPanel.addMouseListener(new MouseConsumer());

		this.editAllJMissionCardsPanelScrollPane = new JScrollPane(this.missionCardPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		this.editorPanel = new EditorPanel();

		this.addComponents();
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
	}

	private void addListenersToDialog() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				EditMissionCardDialog.this.reset();
				EditMissionCardDialog.this.setVisible(false);
			}

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
		JPanel contentPane = (JPanel) this.getContentPane();
		Application.addShortcut(contentPane, KeyEvent.VK_ESCAPE, e -> EditMissionCardDialog.this.setSelectedMissionPanel(null));
		Application.addCTRLShortcut(contentPane, KeyEvent.VK_C, e -> EditMissionCardDialog.this.dispose());
	}

	private void addComponents() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridy = 0;
		gbc.gridx = 0;
		this.add(this.editAllJMissionCardsPanelScrollPane, gbc);
		gbc.gridy = 1;
		this.add(this.editorPanel, gbc);
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(this.getButtonPanel(), gbc);
	}

	private void update() {
		this.edited = true;
		// Add all new MissionCards
		List<MissionCard> missionCards = Game.getInstance().getInstancePlayer().getMissionCards();
		List<JMissionCardPanel> panels = this.missionCardPanel.getMissionCardPanelList();
		this.missionCardPanel.addMissionCards(missionCards.stream().filter(m -> panels.stream().noneMatch(j -> j.missionCard.equals(m))).map(m -> {
			EditableMissionCardPanel panel = new EditableMissionCardPanel(m);
			panel.addMouseListener(new MouseHighlighter());
			return panel;
		}).toList());
		// Remove all Finished Missioncards
		List<MissionCard> finishedCards = Game.getInstance().getInstancePlayer().getFinishedMissionCards();
		this.missionCardPanel.getMissionCardPanelList().removeIf(j -> finishedCards.contains(j.missionCard));
		// Create new Objects for Old Cards
		this.oldMissionCards = new ArrayList<>(this.missionCardPanel.getMissionCardPanelList().stream().map(j -> {
			EditableMissionCardPanel panel = new EditableMissionCardPanel(j.missionCard);
			if (((EditableMissionCardPanel) j).checkBox.isSelected()) {
				panel.checkBox.doClick();
			}
			panel.addMouseListener(new MouseHighlighter());
			return panel;
		}).toList());
		this.missionCardPanelVisibility = null;
		this.missionCardPanelIndexes = null;
		this.missionCardPanel.update();
		this.pack();
		this.setLocationRelativeTo(Application.frame);
	}

	private JPanel getButtonPanel() {
		JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));

		JButton okButton = new JButton(Application.resources.getString(LanguageKey.OK));
		okButton.addActionListener(e -> this.getOKAction().actionPerformed(e));
		Application.addCTRLEnterShortcut(okButton, this.getOKAction());

		JButton cancelButton = new JButton(Application.resources.getString(LanguageKey.CANCEL));
		cancelButton.addActionListener(e -> {
			this.reset();
			this.setVisible(false);
		});
		panel.add(okButton);
		panel.add(cancelButton);
		return panel;
	}

	private AbstractAction getOKAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = -293749696673130324L;

			@Override
			public void actionPerformed(ActionEvent e) {
				EditMissionCardDialog.this.missionCardPanelVisibility = EditMissionCardDialog.this.missionCardPanel.getMissionCardPanelList()
						.stream()
						.map(j -> (EditableMissionCardPanel) j)
						.filter(j -> {
							Optional<JMissionCardPanel> optional = EditMissionCardDialog.this.oldMissionCards.stream().filter(o -> o.missionCard.equals(j.missionCard)).findAny();
							if (optional.isPresent()) {
								EditableMissionCardPanel oldPanel = (EditableMissionCardPanel) optional.get();
								return j.checkBox.isSelected() != oldPanel.checkBox.isSelected();
							}
							return j.checkBox.isSelected();
						})
						.collect(Collectors.toMap(j -> j, j -> j.checkBox.isSelected()));
				List<JMissionCardPanel> allPanels = EditMissionCardDialog.this.missionCardPanel.getMissionCardPanelList();
				EditMissionCardDialog.this.missionCardPanelIndexes = allPanels.stream().collect(Collectors.toMap(t -> allPanels.indexOf(t), t -> t.missionCard));
				EditMissionCardDialog.this.setVisible(false);
			}
		};
	}

	private void reset() {
		this.edited = false;
		this.setSelectedMissionPanel(null);
		this.missionCardPanel.setMissionCardPanelList(this.oldMissionCards);
	}

	public Map<JMissionCardPanel, Boolean> getMissionCardPanelVisibility() {
		return this.missionCardPanelVisibility;
	}

	public Map<Integer, MissionCard> getMissionCardPanelIndexes() {
		return this.missionCardPanelIndexes;
	}

	public boolean isEdited() {
		return this.edited;
	}

	private EditableMissionCardPanel selectedMissionCard;

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
			super(missioncard, true);
			this.checkBox = new JCheckBox(Application.resources.getString(LanguageKey.HIDE));
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

		JButton moveFirst = new JButton("<< " + Application.resources.getString(LanguageKey.MOVEFIRST) + " <<");
		JButton moveUp = new JButton("^ " + Application.resources.getString(LanguageKey.MOVEUP) + " ^");
		JButton moveDown = new JButton("v " + Application.resources.getString(LanguageKey.MOVEDOWN) + " v");
		JButton moveRigth = new JButton("> " + Application.resources.getString(LanguageKey.MOVERIGHT) + " >");
		JButton moveLeft = new JButton("< " + Application.resources.getString(LanguageKey.MOVELEFT) + " <");
		JButton moveLast = new JButton(">> " + Application.resources.getString(LanguageKey.MOVELAST) + " >>");

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

	private static class MouseConsumer extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			e.consume();
		}
	}

	private static class EditAllJMissionCardsPanel extends DefaultAllJMissionCardsScrollPanel {

		private static final long serialVersionUID = 2793926444890417556L;

		public EditAllJMissionCardsPanel(int rowCount, int columnCount) {
			super(rowCount, columnCount);
			this.setMaxWidth(0);
		}

		public int indexOf(JMissionCardPanel panel) {
			return this.missionCardPanelList.indexOf(panel);
		}

		public void movePanel(int currentIndex, int direction) {
			if ((currentIndex != -1) && this.testAction(currentIndex, direction)) {
				int newIndex = this.newIndex(currentIndex, direction);
				JMissionCardPanel missionCards = this.missionCardPanelList.remove(currentIndex);
				this.missionCardPanelList.add(newIndex, missionCards);
				this.update();
				this.revalidate();
				this.repaint();
			}
		}

		public int newIndex(int currentIndex, int direction) {
			if (!this.testAction(currentIndex, direction)) { return currentIndex; }
			switch (direction) {
			case GridBagConstraints.FIRST_LINE_START:
				return 0;
			case GridBagConstraints.NORTH:
				return currentIndex - this.getColumnCount();
			case GridBagConstraints.EAST:
				return currentIndex + 1;
			case GridBagConstraints.SOUTH:
				return Math.min(currentIndex + this.getColumnCount(), this.missionCardPanelList.size() - 1);
			case GridBagConstraints.WEST:
				return currentIndex - 1;
			case GridBagConstraints.LAST_LINE_END:
				return this.missionCardPanelList.size() - 1;
			default:
				return currentIndex;
			}
		}

		public boolean testAction(int index, int direction) {
			if (index == -1) { return false; }
			switch (direction) {
			case GridBagConstraints.NORTH:
				return index >= this.getColumnCount();
			case GridBagConstraints.LAST_LINE_END:
			case GridBagConstraints.EAST:
				return index < (this.missionCardPanelList.size() - 1);
			case GridBagConstraints.SOUTH:
				return index < (this.getColumnCount() * (this.getRowCount() - 1));
			case GridBagConstraints.FIRST_LINE_START:
			case GridBagConstraints.WEST:
				return index > 0;
			default:
				return false;
			}
		}
	}

}
