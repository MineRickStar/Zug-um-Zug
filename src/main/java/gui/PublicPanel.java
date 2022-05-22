package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import application.Application;
import application.Property;
import game.Computer;
import game.Game;
import game.Player;
import game.Rules;
import game.cards.ColorCard;
import game.cards.ColorCard.MyColor;
import gui.PlayerPanel.JColorCardButton;
import gui.PlayerPanel.JGradientButton;
import gui.dialog.DrawMissionCardDialog;

public class PublicPanel extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 1601926777628155685L;

	private TitledBorder colorCardsBorder;

	private JPanel colorCardButtonPanel;
	private List<JColorCardButton> colorCardButtons;

	private JPanel cardButtonPanel;

	private JButton drawColorCardsButton;
	private JButton drawMissionCardsButton;

	private JLabel currentPlayerLabel;

	public PublicPanel() {
		super(new GridBagLayout());
		this.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		this.setDoubleBuffered(true);
		this.colorCardButtons = new ArrayList<>(Rules.getInstance().getColorCardsLayingDown());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;

		this.createColorCardButtonPanel();
		this.add(this.colorCardButtonPanel, gbc);

		this.createCardButtonPanel();
		gbc.weighty = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 20, 10, 20);
		this.add(this.cardButtonPanel, gbc);

		this.currentPlayerLabel = new JLabel("Current Player: ");
		gbc.gridy = 2;
		gbc.insets = new Insets(0, 10, 0, 0);
		this.add(this.currentPlayerLabel, gbc);
		Game.getInstance().addPropertyChangeListener(Property.COLORCARDCHANGE, this);
		Game.getInstance().addPropertyChangeListener(Property.PLAYERCHANGE, this);

	}

	private void createColorCardButtonPanel() {
		this.colorCardButtonPanel = new JPanel(new GridBagLayout());
		this.colorCardButtonPanel.setBackground(Color.LIGHT_GRAY);
		this.colorCardsBorder = BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), this.getTitleString());
		this.colorCardButtonPanel.setBorder(this.colorCardsBorder);
		this.colorCardButtonPanel.setDoubleBuffered(true);

		this.drawColorCardButtonPanel();
	}

	private List<ColorCard> previousColorCards = null;

	private void drawColorCardButtonPanel() {
		if (Game.getInstance().getOpenCards().equals(this.previousColorCards)) {
			boolean enabled = (Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) >= Rules.getInstance().getLocomotiveWorth();
			this.colorCardButtons.forEach(b -> {
				if (b.colorCard.color() == MyColor.RAINBOW) {
					b.setEnabled(enabled);
				}
			});
			return;
		}
		this.colorCardButtonPanel.removeAll();
		GridBagConstraints colorCardConstraints = new GridBagConstraints();
		colorCardConstraints.insets = new Insets(10, 10, 10, 10);
		colorCardConstraints.fill = GridBagConstraints.BOTH;
		colorCardConstraints.weighty = 1;
		colorCardConstraints.weightx = 1;

		this.previousColorCards = new ArrayList<>(Game.getInstance().getOpenCards());
		for (int i = 0; i < Math.min(Rules.getInstance().getColorCardsLayingDown(), Game.getInstance().getRemainingCards()); i++) {
			colorCardConstraints.gridx = i;

			ColorCard colorCard = this.previousColorCards.get(i);
			JColorCardButton colorCardButton;
			if (colorCard.color() == MyColor.RAINBOW) {
				colorCardButton = new JGradientButton(colorCard, i);
			} else {
				colorCardButton = new JColorCardButton(colorCard, i);
			}
			colorCardButton.addActionListener(e -> {
				if (e.getSource() instanceof JColorCardButton button) {
					Game.getInstance().drawColorCardsFromOpenDeck(Game.getInstance().getInstancePlayer(), button.index);
					this.updateColorCardPanelTitle();
				}
			});
			colorCardButton.setEnabled(Game.getInstance().isPlayersTurn());
			if (colorCard.color() == MyColor.RAINBOW) {
				if ((Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) < Rules.getInstance().getLocomotiveWorth()) {
					colorCardButton.setEnabled(false);
				}
			}
			this.colorCardButtons.add(colorCardButton);

			this.colorCardButtonPanel.add(colorCardButton, colorCardConstraints);
		}

	}

	private void createCardButtonPanel() {
		this.cardButtonPanel = new JPanel(new GridLayout(1, 2, 20, 20));
		this.drawColorCardsButton = new JButton("Draw Card");

		this.drawColorCardsButton.addActionListener(e -> {
			Game.getInstance().drawColorCardFromDeck(Game.getInstance().getInstancePlayer());
			this.updateColorCardPanelTitle();
			this.drawColorCardsButton.setEnabled(Game.getInstance().getRemainingCards() > 0);
		});
		this.drawMissionCardsButton = new JButton("Draw Mission Cards");
		Application.addCTRLShortcut(this.drawMissionCardsButton, KeyEvent.VK_D, e -> new DrawMissionCardDialog(false));
		this.drawMissionCardsButton.addActionListener(e -> new DrawMissionCardDialog(false));

		this.cardButtonPanel.add(this.drawColorCardsButton);
		this.cardButtonPanel.add(this.drawMissionCardsButton);
	}

	void updateColorCardPanelTitle() {
		this.colorCardsBorder.setTitle(this.getTitleString());
		this.drawColorCardButtonPanel();
		this.revalidate();
		this.repaint();
	}

	private String getTitleString() {
		String drawCards = "Drawcards (" + Game.getInstance().getRemainingCards() + ")";
		if (Game.getInstance().isPlayersTurn() && (Game.getInstance().getCurrentPlayerColorCardDraws() > 0)) {
			drawCards += " Cards left to draw (" + (Rules.getInstance().getColorCardsDrawing() - Game.getInstance().getCurrentPlayerColorCardDraws()) + ")";
		}
		return drawCards;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (Property.COLORCARDCHANGE.name().equals(evt.getPropertyName())) {
			if (evt.getSource() instanceof Computer) {
				this.updateColorCardPanelTitle();
			}
		} else if (Property.PLAYERCHANGE.name().equals(evt.getPropertyName())) {
			Player newPlayer = (Player) evt.getNewValue();
			this.currentPlayerLabel.setText("Current Player: " + newPlayer.getName());
			boolean isPlayer = !(newPlayer instanceof Computer);
			SwingUtilities.invokeLater(() -> {
				this.drawColorCardsButton.setEnabled(isPlayer);
				this.drawMissionCardsButton.setEnabled(isPlayer);
				this.colorCardButtons.forEach(j -> j.setEnabled(isPlayer));
			});
		}
	}

}
