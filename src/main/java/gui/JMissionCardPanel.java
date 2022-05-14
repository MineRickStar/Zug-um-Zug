package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.cards.MissionCard;

public class JMissionCardPanel extends JPanel implements IMissionCardPanel {

	private static final long serialVersionUID = 2579072798714564454L;

	public final MissionCard missionCard;

	protected JLabel distance;
	protected JLabel points;

	protected JLabel fromLocation;
	protected List<JLabel> overLocations;
	protected JLabel toLocation;

	protected GridBagConstraints gbc;

	protected boolean finished;
	protected boolean cardVisible;

	public JMissionCardPanel(MissionCard missionCard) {
		super(new GridBagLayout());
		this.setBackground(Color.WHITE);
		this.missionCard = missionCard;
		this.cardVisible = true;

		this.distance = new JLabel(missionCard.distance.cardLength);
		this.points = new JLabel(missionCard.points + " Points", SwingConstants.TRAILING);
		this.fromLocation = new JLabel(missionCard.getFromLocation().name, SwingConstants.LEADING);
		this.toLocation = new JLabel(missionCard.getToLocation().name, SwingConstants.TRAILING);
		this.overLocations = missionCard.getMidLocations().stream().map(l -> new JLabel(l.name, SwingConstants.CENTER)).toList();
		this.gbc = new GridBagConstraints();
		this.display();
	}

	private void display() {
		this.gbc.weightx = 1;
		this.gbc.weighty = 1;
		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		this.gbc.insets = new Insets(5, 5, 5, 5);
		this.add(this.distance, this.gbc);

		this.gbc.gridx = 1;
		this.add(this.points, this.gbc);

		this.gbc.insets = new Insets(5, 5, 5, 0);
		this.gbc.gridwidth = 2;
		this.gbc.gridx = 0;

		this.gbc.gridy = 1;
		this.add(this.fromLocation, this.gbc);

		this.gbc.insets = new Insets(5, 0, 5, 0);
		this.overLocations.forEach(l -> {
			this.gbc.gridy++;
			this.add(l, this.gbc);
		});

		this.gbc.insets = new Insets(5, 0, 5, 5);
		this.gbc.gridy++;
		this.add(this.toLocation, this.gbc);
	}

	@Override
	public boolean isCardVisible() {
		return this.cardVisible;
	}

	@Override
	public void setCardVisible(boolean cardVisible) {
		this.cardVisible = cardVisible;
		this.revalidate();
		this.repaint();
	}

	@Override
	public void setCardFinished(boolean finished) {
		this.finished = finished;
	}

	@Override
	public boolean isCardFinished() {
		return this.finished;
	}

	@Override
	public Dimension getMinimumSize() {
		return this.getSize();
	}

	@Override
	public JPanel getPanel() {
		return this;
	}

	@Override
	public MissionCard getMissionCard() {
		return this.missionCard;
	}

}
