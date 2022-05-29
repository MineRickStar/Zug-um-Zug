package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import game.cards.MissionCard;

public abstract class JMissionCardPanel extends JPanel {

	private static final long serialVersionUID = 2579072798714564454L;

	public static final Dimension preferredSize = new Dimension(160, 100);

	public final MissionCard missionCard;

	private final JPanel missionPanel;
	private final GridBagConstraints gbc;

	protected JLabel distance;
	protected JLabel points;

	protected JLabel fromLocation;
	protected JLabel overLocations;
	protected JLabel toLocation;

	protected boolean withName;

	protected JMissionCardPanel(MissionCard missionCard, boolean withName) {
		super(new BorderLayout());
		this.missionPanel = new JPanel(new GridBagLayout(), true);
		this.gbc = new GridBagConstraints();
		this.missionCard = missionCard;
		this.withName = withName;

		this.distance = new JLabel(missionCard.distance.cardLength);
		this.points = new JLabel(missionCard.points + " Points", SwingConstants.TRAILING);
		this.fromLocation = new JLabel(withName ? missionCard.getFromLocation().name : missionCard.getFromLocation().abbreviation, SwingConstants.LEADING);
		this.toLocation = new JLabel(withName ? missionCard.getToLocation().name : missionCard.getToLocation().abbreviation, SwingConstants.TRAILING);
		this.overLocations = new JLabel(missionCard.getMidLocations().stream().map(l -> withName ? l.name : l.abbreviation).collect(Collectors.joining(", ", "Via: ", "")), SwingConstants.CENTER);
		this.missionPanel.setPreferredSize(JMissionCardPanel.preferredSize);
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

		if (!this.missionCard.getMidLocations().isEmpty()) {
			this.gbc.insets = new Insets(5, 0, 5, 0);
			this.gbc.gridy++;
			this.missionPanel.add(this.overLocations, this.gbc);
		}

		this.gbc.insets = new Insets(5, 0, 5, 5);
		this.gbc.gridy++;
		this.missionPanel.add(this.toLocation, this.gbc);
		this.add(this.missionPanel, BorderLayout.CENTER);
		this.validate();
	}

	public abstract boolean isPanelDisplayable();

	/**
	 * Returns the index on which Sub-Panel of the AllJMissionCardPanel this Panel
	 * needs to go.<br>
	 * Is all Indices are equal they are all put on the same Panel
	 *
	 * @return The Index of the Panel
	 */
	public int getIndex() {
		return -1;
	}

	public MissionCard getMissionCard() {
		return this.missionCard;
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension sup = super.getPreferredSize();
		if ((sup.height == 0) || (sup.width == 0)) { return this.missionPanel.getPreferredSize(); }
		return sup;
	}

	@Override
	public void setForeground(Color color) {
		super.setForeground(color);
		if (this.distance != null) {
			this.distance.setForeground(color);
			this.points.setForeground(color);
			this.fromLocation.setForeground(color);
			this.overLocations.setForeground(color);
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
		return this.missionCard.hashCode();
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