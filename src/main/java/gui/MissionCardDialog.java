package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import application.Application;
import application.Property;
import game.Game;
import game.Rules;
import game.cards.MissionCard.Distance;

public class MissionCardDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 3371834066035120352L;

	private final boolean start;

	private EnumMap<Distance, Integer> missionCards;

	// TODO rework because missionCards are not drawn when selected
	public MissionCardDialog(boolean start) {
		super(Application.frame, "Draw Mission Cards", true);
		this.start = start;
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MissionCardDialog.this.missionCards = new EnumMap<>(Distance.class);
			}
		});

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;

		Distance[] distances = Distance.values();
		Map<Distance, JSlider> distanceSlider = new EnumMap<>(Distance.class);

		for (Distance distance : distances) {
			JPanel missionCardPanel = new JPanel();
			JLabel label = new JLabel(distance.cardLength);
			missionCardPanel.add(label);
			JSlider slider = this.getMissionCardSlider(distance);
			distanceSlider.put(distance, slider);
			missionCardPanel.add(slider);

			panel.add(missionCardPanel, gbc);
			gbc.gridy++;
		}

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			this.missionCards = new EnumMap<>(Distance.class);
			distanceSlider.entrySet()
					.forEach(entry -> this.missionCards.put(entry.getKey(), entry.getValue()
							.getValue()));
			if (this.missionCards.values()
					.stream()
					.collect(Collectors.summingInt(Integer::intValue)) == Rules.getInstance()
							.getMissionCardsDrawing()) {
				Game.getInstance()
						.fireAction(this, Property.DRAWMISSIONCARDS, null, this.missionCards);
				this.dispose();
			} else {
				JOptionPane.showMessageDialog(this, "The sum of all Mission Cards must be " + Rules.getInstance()
						.getMissionCardsDrawing());
			}
		});

		JButton cancleButton = new JButton("Cancle");
		cancleButton.addActionListener(e -> {
			if (this.start) {
				JOptionPane.showMessageDialog(this, "Cancle not Possible at start of Game");
				return;
			}
			this.dispose();
		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancleButton);

		panel.add(buttonPanel, gbc);

		this.add(panel);

		this.pack();
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private JSlider getMissionCardSlider(Distance distance) {
		int distanceCards = Game.getInstance()
				.getMissionCardCount(distance);
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, Math.min(distanceCards, Rules.getInstance()
				.getMissionCardsDrawing()), Math.min(
						Math.min(distanceCards, Rules.getInstance()
								.getMissionCardsDrawing()),
						2));
		if (Game.getInstance()
				.getMissionCardCount(distance) == 0) {
			slider.setEnabled(false);
		}
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(1);
		slider.setDoubleBuffered(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPaintTrack(true);
		return slider;
	}

	public EnumMap<Distance, Integer> getMissionCards() {
		return this.missionCards;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {}

}
