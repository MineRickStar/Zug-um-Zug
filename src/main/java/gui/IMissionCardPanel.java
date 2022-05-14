package gui;

import javax.swing.JPanel;

import game.cards.MissionCard;

public interface IMissionCardPanel {

	public JPanel getPanel();

	public MissionCard getMissionCard();

	public void setCardVisible(boolean visible);

	public boolean isCardVisible();

	public void setCardFinished(boolean finished);

	public boolean isCardFinished();

}
