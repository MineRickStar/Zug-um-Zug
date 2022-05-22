package gui.gameStart;

import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import game.Game;
import game.Player;
import game.cards.ColorCard.MyColor;

public class PlayerPanel extends AbstractTabbedPanel {

	private static final long serialVersionUID = 5048988383300820279L;

	private static final int PLAYER_NAME_WIDTH = 30;

	private JLabel playerNameLabel;
	private JTextField playerName;
	private JComboBox<MyColor> playerColor;

	public PlayerPanel(GameStartDialog parent) {
		super(parent);
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
		this.layoutComponents();
	}

	@Override
	protected void layoutComponents() {
		this.gbc.insets = new Insets(10, 10, 10, 10);
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		this.add(this.playerNameLabel, this.gbc);
		this.gbc.gridx = 1;
		this.add(this.playerName, this.gbc);
		this.gbc.gridx = 2;
		this.add(this.playerColor, this.gbc);
		super.addTabbing(3);
	}

	@Override
	public String getDisplayName() {
		return "Player";
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
