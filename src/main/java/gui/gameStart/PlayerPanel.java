package gui.gameStart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import game.Game;
import game.Player;
import game.cards.ColorCard.MyColor;

public class PlayerPanel extends JPanel implements ITabbedPanel {

	private static final long serialVersionUID = 5048988383300820279L;

	private static final int PLAYER_NAME_WIDTH = 30;

	private JLabel playerNameLabel;
	private JTextField playerName;
	private JComboBox<MyColor> playerColor;

	public PlayerPanel() {
		super(new GridBagLayout());
		this.playerNameLabel = new JLabel("Name:");
		this.playerName = new JTextField("Patrick", PlayerPanel.PLAYER_NAME_WIDTH);
		this.playerName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					PlayerPanel.this.isAllCorrect();
				}
			}
		});
		DefaultComboBoxModel<MyColor> model = new DefaultComboBoxModel<>(MyColor.getNormalMyColors());
		this.playerColor = new JComboBox<>(model);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(this.playerNameLabel, gbc);
		gbc.gridx = 1;
		this.add(this.playerName, gbc);
		gbc.gridx = 2;
		this.add(this.playerColor, gbc);
		gbc.gridx = 3;
		gbc.weightx = 1;
		this.add(new JPanel(), gbc);
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weighty = 1;
		this.add(new JPanel(), gbc);
	}

	@Override
	public boolean isAllCorrect() {
		if (this.playerName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Playername must not be empty");
			return false;
		} else if (this.playerName.getText().isBlank()) {
			JOptionPane.showMessageDialog(this, "Playername must not be blank");
			return false;
		}
		if (this.playerColor.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "Please choose a Color for you");
			return false;
		}
		return true;
	}

	@Override
	public boolean save() {
		Player p = Game.getInstance().addInstancePlayer(this.playerName.getText(), (MyColor) this.playerColor.getSelectedItem());
		return p != null;
	}

}
