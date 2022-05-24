package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import application.Application;
import game.Game;
import game.Rules;
import game.cards.MissionCard.Distance;

public class DrawMissionCardDialog extends JDialog {

	private static final long serialVersionUID = 3371834066035120352L;

	private EnumMap<Distance, Integer> missionCards;

	private Map<Distance, JSlider> distanceSlider;

	public DrawMissionCardDialog(boolean start) {
		super(Application.frame, "Draw Mission Cards", true);
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = 0;
		gbc.gridy = 0;

		Distance[] distances = Distance.values();
		this.distanceSlider = new EnumMap<>(Distance.class);

		for (Distance distance : distances) {
			JPanel missionCardPanel = new JPanel();
			JLabel label = new JLabel(distance.cardLength);
			missionCardPanel.add(label);
			JSlider slider = this.getMissionCardSlider(distance);
			this.distanceSlider.put(distance, slider);
			missionCardPanel.add(slider);

			panel.add(missionCardPanel, gbc);
			gbc.gridy++;
		}

		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));

		JButton okButton = new JButton("OK");
		okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "OK");
		okButton.getActionMap().put("OK", new AbstractAction() {

			private static final long serialVersionUID = 5594240801235444580L;

			@Override
			public void actionPerformed(ActionEvent e) {
				DrawMissionCardDialog.this.testMissionCards();
			}
		});
		okButton.addActionListener(e -> this.testMissionCards());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> this.dispose());

		buttonPanel.add(okButton);
		if (!start) {
			buttonPanel.add(cancelButton);
		}

		panel.add(buttonPanel, gbc);

		this.add(panel);

		if (start) {
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		}

		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private void testMissionCards() {
		this.missionCards = new EnumMap<>(Distance.class);
		this.distanceSlider.entrySet().forEach(entry -> this.missionCards.put(entry.getKey(), entry.getValue().getValue()));
		if (this.missionCards.values().stream().collect(Collectors.summingInt(Integer::intValue)) == Rules.getInstance().getMissionCardsDrawing()) {
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(this, String.format("The sum of all Mission Cards must be %d", Rules.getInstance().getMissionCardsDrawing()));
		}
	}

	private JSlider getMissionCardSlider(Distance distance) {
		int distanceCards = Game.getInstance().getMissionCardCount(distance);
		int maxCardAmount = Math.min(distanceCards, Rules.getInstance().getMissionCardsDrawing());
		int value = Math.min(Math.min(distanceCards, Rules.getInstance().getMissionCardsDrawing()), 2);
		if ((distance == Distance.LONG) || (distance == Distance.EXTRA_LONG)) {
			value = 0;
		}
		JSlider slider = new JSlider(SwingConstants.HORIZONTAL, 0, maxCardAmount, value);
		if (distanceCards == 0) {
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

	public EnumMap<Distance, Integer> getSelectedMissionCards() {
		return this.missionCards;
	}

}
