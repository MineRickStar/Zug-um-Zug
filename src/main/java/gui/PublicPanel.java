package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import application.Application;
import application.PropertyEvent;
import game.Game;
import game.Rules;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import game.cards.MissionCard.Distance;
import gui.PlayerPanel.JColorCardButton;
import gui.PlayerPanel.JGradientButton;
import gui.dialog.DrawMissionCardDialog;

public class PublicPanel extends JPanel implements IUpdatePanel {

	private static final long serialVersionUID = 1601926777628155685L;

	private TitledBorder colorCardsBorder;

	private JPanel colorCardButtonPanel;
	private List<JColorCardButton> colorCardButtons;

	private JButton drawColorCardsButton;
	private JButton drawMissionCardsButton;

	private JLabel currentPlayerLabel;

	public PublicPanel() {
		super(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		this.colorCardsBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), this.getTitleString());
		this.colorCardButtons = new ArrayList<>(Rules.getInstance().getOpenColorCards());

		this.colorCardButtonPanel = new JPanel(new GridBagLayout());
		this.colorCardButtonPanel.setBorder(this.colorCardsBorder);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;

		this.drawColorCardButtonPanel();
		this.add(this.colorCardButtonPanel, gbc);

		gbc.weighty = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 20, 10, 20);
		this.add(this.createCardButtonPanel(), gbc);

		this.currentPlayerLabel = new JLabel("Current Player: ");
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 10, 0, 0);
		this.add(this.currentPlayerLabel, gbc);
	}

	private void drawColorCardButtonPanel() {
		this.colorCardButtonPanel.removeAll();
		this.colorCardButtons.clear();
		GridBagConstraints colorCardConstraints = new GridBagConstraints();
		colorCardConstraints.insets = new Insets(10, 10, 10, 10);
		colorCardConstraints.fill = GridBagConstraints.BOTH;
		colorCardConstraints.weighty = 1;
		colorCardConstraints.weightx = 1;

		List<ColorCard> colorCards = new ArrayList<>(Game.getInstance().getOpenCards());
		for (int i = 0; i < Math.min(Rules.getInstance().getOpenColorCards(), Game.getInstance().getRemainingCards()); i++) {
			ColorCard colorCard = colorCards.get(i);
			JColorCardButton colorCardButton;
			if (colorCard.color() == MyColor.RAINBOW) {
				colorCardButton = new JGradientButton(colorCard, i);
				if ((Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) < Rules.getInstance().getLocomotiveWorth()) {
					colorCardButton.setEnabled(false);
				}
			} else {
				colorCardButton = new JColorCardButton(colorCard, i);
			}
			colorCardButton.addActionListener(e -> {
				if (e.getSource() instanceof JColorCardButton button) {
					Game.getInstance().drawColorCardsFromOpenDeck(Game.getInstance().getInstancePlayer(), button.index);
				}
			});
			colorCardButton.setEnabled(Game.getInstance().isPlayersTurn());
			this.colorCardButtons.add(colorCardButton);

			colorCardConstraints.gridx = i;
			this.colorCardButtonPanel.add(colorCardButton, colorCardConstraints);
		}

	}

	private JPanel createCardButtonPanel() {
		JPanel cardButtonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
		this.drawColorCardsButton = new JButton("Draw Card");

		this.drawColorCardsButton.addActionListener(e -> {
			Game.getInstance().drawColorCardFromDeck(Game.getInstance().getInstancePlayer());
			this.colorCardsBorder.setTitle(this.getTitleString());
			this.drawColorCardButtonPanel();
		});
		this.drawMissionCardsButton = new JButton("Draw Mission Cards");
		Application.addCTRLShortcut(this.drawMissionCardsButton, KeyEvent.VK_D, e -> this.drawMissionCards());
		this.drawMissionCardsButton.addActionListener(e -> this.drawMissionCards());

		cardButtonPanel.add(this.drawColorCardsButton);
		cardButtonPanel.add(this.drawMissionCardsButton);
		return cardButtonPanel;
	}

	private void drawMissionCards() {
		DrawMissionCardDialog dialog = new DrawMissionCardDialog(false);
		EnumMap<Distance, Integer> missionCards = dialog.getSelectedMissionCards();
		if (missionCards != null) {
			Game.getInstance().drawMissionCards(Game.getInstance().getInstancePlayer(), missionCards);
		}
	}

	private String getTitleString() {
		String drawCards = "Drawcards (" + Game.getInstance().getRemainingCards() + ")";
		if (Game.getInstance().isPlayersTurn() && Game.getInstance().isCardAlreadyDrawn()) {
			drawCards += " Cards left to draw (" + (Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) + ")";
		}
		return drawCards;
	}

	private synchronized void setComponentsEnabled(boolean enabled) {
		this.drawColorCardsButton.setEnabled(enabled);
		this.drawMissionCardsButton.setEnabled(enabled);
		this.colorCardButtons.forEach(j -> j.setEnabled(enabled));
	}

	@Override
	public synchronized void update(PropertyEvent propertyEvent) {
		switch (propertyEvent.property) {
		case COLORCARDDRAWN:
			if (propertyEvent.player.equals(Game.getInstance().getInstancePlayer()) && !Game.getInstance().isCardAlreadyDrawn()) {
				this.drawMissionCardsButton.setEnabled(true);
			} else {
				this.drawMissionCardsButton.setEnabled(false);
			}
			this.colorCardsBorder.setTitle(this.getTitleString());
			this.drawColorCardButtonPanel();
			break;
		case PLAYERCHANGE:
			this.currentPlayerLabel.setText("Current Player: " + Game.getInstance().getCurrentPlayerName());
			this.setComponentsEnabled(Game.getInstance().isPlayersTurn());
			this.colorCardsBorder.setTitle(this.getTitleString());
			break;
		case COLORCARDADDED:
		case COLORCARDREMOVED:
		case MISSIONCARDADDED:
		case MISSIONCARDDRAWN:
		case MISSIONCARDEDITED:
		case MISSIONCARDFINISHED:
		case CONNECTIONBOUGHT:
			break;
		case GAMESTART:
			this.currentPlayerLabel.setText("Current Player: " + Game.getInstance().getCurrentPlayerName());
			this.setComponentsEnabled(Game.getInstance().isPlayersTurn());
			this.colorCardsBorder.setTitle(this.getTitleString());
			this.drawColorCardButtonPanel();
		}
	}

}
