package gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.cards.MissionCard;

public class JMissionCardPanel extends JPanel implements Comparable<JMissionCardPanel> {

	private static final long serialVersionUID = 2579072798714564454L;

	public final MissionCard missionCard;

	private JLabel distance;
	private JLabel points;

	private JLabel fromLocation;
	private JLabel toLocation;

	public JMissionCardPanel(MissionCard missionCard) {
		super(new GridBagLayout());
		this.setBackground(Color.LIGHT_GRAY);
		this.missionCard = missionCard;

		this.distance = new JLabel(missionCard.distance.cardLength);
		this.points = new JLabel(missionCard.points + " Points", SwingConstants.TRAILING);
		this.fromLocation = new JLabel("From: " + missionCard.getFromLocation().name, SwingConstants.CENTER);
		this.toLocation = new JLabel("To: " + missionCard.getToLocation().name, SwingConstants.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		this.add(this.distance, gbc);

		gbc.gridx = 1;
		this.add(this.points, gbc);

		gbc.insets = new Insets(5, 0, 5, 0);
		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 1;
		this.add(this.fromLocation, gbc);

		gbc.gridy = 2;
		this.add(this.toLocation, gbc);
	}

	@Override
	public int compareTo(JMissionCardPanel o) {
		return this.missionCard.compareTo(o.missionCard);
	}

}
