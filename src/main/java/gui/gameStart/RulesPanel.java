package gui.gameStart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import game.Rules;

public class RulesPanel extends AbstractTabbedPanel {

	private static final long serialVersionUID = 3247134518993048509L;

	private PlayerRulesPanel playerRulesPanel;
	private CardRulesPanel cardRulesPanel;

	private JButton resetButton;

	public RulesPanel(GameStartDialog parent) {
		super(parent);
		this.playerRulesPanel = new PlayerRulesPanel();
		this.cardRulesPanel = new CardRulesPanel();
		this.resetButton = new JButton("Reset");
		this.resetButton.addActionListener(e -> {
			this.playerRulesPanel.reset();
			this.cardRulesPanel.reset();
		});
		this.layoutComponents();
	}

	@Override
	protected void layoutComponents() {
		this.gbc.insets = new Insets(10, 10, 10, 10);
		this.gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		this.add(this.playerRulesPanel, this.gbc);
		this.gbc.gridy = 1;
		this.add(this.cardRulesPanel, this.gbc);
		this.gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.gbc.gridx = 1;
		this.add(this.resetButton, this.gbc);
		this.gbc.anchor = GridBagConstraints.CENTER;
		this.gbc.gridy = 0;
		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.weightx = 1;
		this.gbc.weighty = 1;
		this.add(new JPanel(), this.gbc);
	}

	@Override
	public String getDisplayName() {
		return "Rules";
	}

	@Override
	public boolean isAllCorrect() {
		return true;
	}

	@Override
	public boolean save() {
		boolean saved = this.playerRulesPanel.save();
		saved &= this.cardRulesPanel.save();
		return saved;
	}

	private class PlayerRulesPanel extends JPanel {
		private static final long serialVersionUID = -2005382497137013160L;

		JLabel playerCardLimitLabel;
		JLabel playerMissioncardDrawingLabel;
		JLabel firstMissionCardKeepingLabel;
		JLabel firstColorCardsLabel;

		JLabel locomotivCardLimitLabel;
		// Nothing here in the Second Column
		JLabel defaultMissionCardKeepingLabel;
		JLabel drawColorCardsLabel;

		MySpinner normalCardLimit;
		MySpinner missioncardDrawing;
		MySpinner firstMissionCardKeeping;
		MySpinner firstColorCards;

		MySpinner locomotiveCardLimit;
		// Nothing here in the Second Column
		MySpinner defaultMissionCardKeeping;
		MySpinner drawColorCards;

		private PlayerRulesPanel() {
			super(new GridBagLayout());
			this.setBorder(RulesPanel.this.getTitleBorder("Playerrules"));
			this.playerCardLimitLabel = new JLabel("Normal Card Limit:");
			this.playerMissioncardDrawingLabel = new JLabel("Missioncard Drawing:");
			this.firstMissionCardKeepingLabel = new JLabel("First Missioncard Keeping:");
			this.firstColorCardsLabel = new JLabel("First Color Cards Drawing:");

			this.locomotivCardLimitLabel = new JLabel("Locomotiv Card Limit:");
			// Nothing here in the Second Column
			this.defaultMissionCardKeepingLabel = new JLabel("Default Missioncard Keeping:");
			this.drawColorCardsLabel = new JLabel("Default Color Cards Drawing:");

			Rules rules = Rules.getInstance();
			this.normalCardLimit = new MySpinner(rules.getCardsLimit(), 0, 100);
			this.missioncardDrawing = new MySpinner(rules.getMissionCardsDrawing(), 1, 10);
			this.firstMissionCardKeeping = new MySpinner(rules.getFirstMissionCardsKeeping(), 1, rules.getMissionCardsDrawing());
			this.firstColorCards = new MySpinner(rules.getFirstColorCards(), 1, 10);

			this.locomotiveCardLimit = new MySpinner(rules.getLocomotivCardsLimit(), 0, 100);
			// Nothing here in the Second Column
			this.defaultMissionCardKeeping = new MySpinner(rules.getDefaultMissionCardsKeeping(), 1, rules.getMissionCardsDrawing());
			this.drawColorCards = new MySpinner(rules.getColorCardsDrawing(), 1, 10);

			this.addConstraints();
			this.layoutComponents();
		}

		private void addConstraints() {
			this.missioncardDrawing.addChangeListener(e -> {
				int value = (int) this.missioncardDrawing.getValue();
				SpinnerNumberModel model1 = (SpinnerNumberModel) this.firstMissionCardKeeping.getModel();
				model1.setMaximum(value);
				model1.setValue(Math.min((int) model1.getValue(), value));
				SpinnerNumberModel model2 = (SpinnerNumberModel) this.defaultMissionCardKeeping.getModel();
				model2.setMaximum(value);
				model2.setValue(Math.min((int) model2.getValue(), value));
			});
			this.drawColorCards.addChangeListener(e -> {
				int value = (int) this.drawColorCards.getValue();
				SpinnerNumberModel model1 = (SpinnerNumberModel) RulesPanel.this.cardRulesPanel.locomotiveWorth.getModel();
				model1.setMaximum(value);
				model1.setValue(Math.min((int) model1.getValue(), value));
			});
		}

		private void layoutComponents() {
			RulesPanel.this.gbc.anchor = GridBagConstraints.LINE_START;
			RulesPanel.this.gbc.insets = new Insets(2, 5, 2, 5);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(2, 5, 2, 5);
			gbc.anchor = GridBagConstraints.LINE_START;
			RulesPanel.this.nextColumn(this, gbc, this.playerCardLimitLabel, this.playerMissioncardDrawingLabel, this.firstMissionCardKeepingLabel, this.firstColorCardsLabel);
			RulesPanel.this.nextColumn(this, gbc, this.normalCardLimit, this.missioncardDrawing, this.firstMissionCardKeeping, this.firstColorCards);
			RulesPanel.this.nextColumn(this, gbc, this.locomotivCardLimitLabel, null, this.defaultMissionCardKeepingLabel, this.drawColorCardsLabel);
			RulesPanel.this.nextColumn(this, gbc, this.locomotiveCardLimit, null, this.defaultMissionCardKeeping, this.drawColorCards);
		}

		private void reset() {
			this.normalCardLimit.reset();
			this.missioncardDrawing.reset();
			this.firstMissionCardKeeping.reset();
			this.firstColorCards.reset();

			this.locomotiveCardLimit.reset();
			// Nothing here in the Second Column
			this.defaultMissionCardKeeping.reset();
			this.drawColorCards.reset();
		}

		private boolean save() {
			Rules rules = Rules.getInstance();
			rules.setCardsLimit(this.normalCardLimit.getIntValue());
			rules.setLocomotiveCardsLimit(this.locomotiveCardLimit.getIntValue());
			rules.setMissionCardsDrawing(this.missioncardDrawing.getIntValue());
			rules.setFirstMissionCardsKeeping(this.firstMissionCardKeeping.getIntValue());
			rules.setDefaultMissionCardsKeeping(this.defaultMissionCardKeeping.getIntValue());
			rules.setFirstColorCards(this.firstColorCards.getIntValue());
			rules.setColorCardsDrawing(this.drawColorCards.getIntValue());
			return true;
		}

	}

	private class CardRulesPanel extends JPanel {
		private static final long serialVersionUID = 2823816375176339742L;

		private JLabel openColorCardsLabel;
		private JLabel locomotiveWorthLabel;
		private JLabel shuffleCardsWhenMaxLocomotivesLabel;
		private JPanel maxOpenLocomotivesLabelPanel;
		private JLabel maxOpenLocomotivesLabel;

		private MySpinner openColorCards;
		private MySpinner locomotiveWorth;
		private JCheckBox shuffleCardsWhenMaxLocomotives;
		private JPanel maxOpenLocomotivesSpinnerPanel;
		private MySpinner maxOpenLocomotives;

		private CardRulesPanel() {
			super(new GridBagLayout());
			this.setBorder(RulesPanel.this.getTitleBorder("Cardrules"));
			this.openColorCardsLabel = new JLabel("Open Cards Laying Down:");
			this.locomotiveWorthLabel = new JLabel("Locomotive Worth:");
			this.locomotiveWorthLabel.setToolTipText("Test");
			this.shuffleCardsWhenMaxLocomotivesLabel = new JLabel("Shuffle With Locomotives:");
			this.maxOpenLocomotivesLabelPanel = new JPanel(new GridLayout(1, 1));
			this.maxOpenLocomotivesLabel = new JLabel("Max Locomotives:");

			Rules rules = Rules.getInstance();
			this.openColorCards = new MySpinner(rules.getOpenColorCards(), 1, 10);
			this.locomotiveWorth = new MySpinner(rules.getLocomotiveWorth(), 1, rules.getColorCardsDrawing());
			this.shuffleCardsWhenMaxLocomotives = new JCheckBox("", rules.isShuffleWithMaxOpenLocomotives());
			this.maxOpenLocomotivesSpinnerPanel = new JPanel(new GridLayout(1, 1));
			this.maxOpenLocomotives = new MySpinner(rules.getMaxOpenLocomotives(), 1, rules.getOpenColorCards());

			this.addConstraints();
			this.layoutComponents();
		}

		private void addConstraints() {
			this.openColorCards.addChangeListener(e -> {
				int value = (int) this.openColorCards.getValue();
				SpinnerNumberModel model1 = (SpinnerNumberModel) this.maxOpenLocomotives.getModel();
				model1.setMaximum(value);
				model1.setValue(Math.min((int) model1.getValue(), value));
			});
			this.shuffleCardsWhenMaxLocomotives.addActionListener(e -> {
				if (this.shuffleCardsWhenMaxLocomotives.isSelected()) {
					this.maxOpenLocomotivesSpinnerPanel.add(this.maxOpenLocomotivesLabel);
					this.maxOpenLocomotivesLabelPanel.add(this.maxOpenLocomotives);
				} else {
					this.maxOpenLocomotivesSpinnerPanel.removeAll();
					this.maxOpenLocomotivesLabelPanel.removeAll();
				}
				this.maxOpenLocomotivesSpinnerPanel.repaint();
				this.maxOpenLocomotivesLabelPanel.repaint();
			});
		}

		private void layoutComponents() {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(2, 5, 2, 5);
			gbc.anchor = GridBagConstraints.LINE_START;
			this.maxOpenLocomotivesSpinnerPanel.add(this.maxOpenLocomotivesLabel);
			this.maxOpenLocomotivesLabelPanel.add(this.maxOpenLocomotives);
			RulesPanel.this.nextColumn(this, gbc, this.openColorCardsLabel, this.locomotiveWorthLabel, this.shuffleCardsWhenMaxLocomotivesLabel);
			RulesPanel.this.nextColumn(this, gbc, this.openColorCards, this.locomotiveWorth, this.shuffleCardsWhenMaxLocomotives);
			RulesPanel.this.nextColumn(this, gbc, null, null, this.maxOpenLocomotivesSpinnerPanel);
			RulesPanel.this.nextColumn(this, gbc, null, null, this.maxOpenLocomotivesLabelPanel);
		}

		private void reset() {
			this.openColorCards.reset();
			this.locomotiveWorth.reset();
			if (this.shuffleCardsWhenMaxLocomotives.isSelected() != Rules.getInstance().isShuffleWithMaxOpenLocomotives()) {
				this.shuffleCardsWhenMaxLocomotives.doClick();
			}
			this.maxOpenLocomotives.reset();
		}

		private boolean save() {
			Rules rules = Rules.getInstance();
			rules.setOpenColorCards(this.openColorCards.getIntValue());
			rules.setLocomotiveWorth(this.locomotiveWorth.getIntValue());
			rules.setShuffleWithMaxOpenLocomotives(this.shuffleCardsWhenMaxLocomotives.isSelected());
			rules.setMaxOpenLocomotives(this.maxOpenLocomotives.getIntValue());
			return true;
		}

	}

}
