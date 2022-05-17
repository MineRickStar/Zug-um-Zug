package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.cards.MissionCard;

public abstract class JMissionCardPanel extends JPanel {

	private static final long serialVersionUID = 2579072798714564454L;

	public final MissionCard missionCard;

	private final JPanel missionPanel;
	private final GridBagConstraints gbc;

	protected JLabel distance;
	protected JLabel points;

	protected JLabel fromLocation;
	protected List<JLabel> overLocations;
	protected JLabel toLocation;

	protected JMissionCardPanel(MissionCard missionCard) {
		this(missionCard, false);
	}

	protected JMissionCardPanel(MissionCard missionCard, boolean withBorder) {
		super(new BorderLayout());
		this.missionPanel = new JPanel(new GridBagLayout(), true);
		this.gbc = new GridBagConstraints();
		if (withBorder) {
			this.missionPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		}
		this.missionCard = missionCard;

		this.distance = new JLabel(missionCard.distance.cardLength);
		this.points = new JLabel(missionCard.points + " Points", SwingConstants.TRAILING);
		this.fromLocation = new JLabel(missionCard.getFromLocation().name, SwingConstants.LEADING);
		this.toLocation = new JLabel(missionCard.getToLocation().name, SwingConstants.TRAILING);
		this.overLocations = missionCard.getMidLocations().stream().map(l -> new JLabel(l.name, SwingConstants.CENTER)).toList();
		this.display();
	}

	private void display() {
		this.gbc.weightx = 1;
		this.gbc.weighty = 1;
		this.gbc.fill = GridBagConstraints.BOTH;
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		this.gbc.insets = new Insets(5, 5, 5, 5);
		this.missionPanel.add(this.distance, this.gbc);

		this.gbc.gridx = 1;
		this.missionPanel.add(this.points, this.gbc);

		this.gbc.insets = new Insets(5, 5, 5, 0);
		this.gbc.gridwidth = 2;
		this.gbc.gridx = 0;

		this.gbc.gridy = 1;
		this.missionPanel.add(this.fromLocation, this.gbc);

		this.gbc.insets = new Insets(5, 0, 5, 0);
		this.overLocations.forEach(l -> {
			this.gbc.gridy++;
			this.missionPanel.add(l, this.gbc);
		});

		this.gbc.insets = new Insets(5, 0, 5, 5);
		this.gbc.gridy++;
		this.missionPanel.add(this.toLocation, this.gbc);
		this.add(this.missionPanel, BorderLayout.CENTER);
	}

	public abstract boolean isPanelDisplayable();

	public MissionCard getMissionCard() {
		return this.missionCard;
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		if (this.distance != null) {
			this.distance.setForeground(color);
			this.points.setForeground(color);
			this.fromLocation.setForeground(color);
			this.overLocations.forEach(j -> j.setForeground(color));
			this.toLocation.setForeground(color);
		}
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (this.missionPanel != null) {
			this.missionPanel.setBackground(color);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.missionCard);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (this.getClass() != obj.getClass()) { return false; }
		JMissionCardPanel other = (JMissionCardPanel) obj;
		return Objects.equals(this.missionCard, other.missionCard);
	}

}