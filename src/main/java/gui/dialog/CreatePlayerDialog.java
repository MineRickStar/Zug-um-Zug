package gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import application.Application;
import game.Game;
import game.cards.ColorCard.MyColor;

public class CreatePlayerDialog extends JDialog {

	private static final long serialVersionUID = -2665746804719360721L;

	private static final int PLAYER_NAME_WIDTH = 30;

	private JPanel panel;

	private JLabel playerNameLabel;
	private JTextField playerName;
	private JComboBox<MyColor> playerColor;
	private JButton okButton;

	private boolean canceled = false;

	public CreatePlayerDialog() {
		super(Application.frame, "New Player", true);
		this.panel = new JPanel(new GridBagLayout());
		this.playerName = new JTextField("Patrick", CreatePlayerDialog.PLAYER_NAME_WIDTH);
		this.playerName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					CreatePlayerDialog.this.okButton.requestFocus();
				}
			}
		});
		this.playerNameLabel = new JLabel("Name:");
		DefaultComboBoxModel<MyColor> model = new DefaultComboBoxModel<>(MyColor.getNormalMyColors());
		this.playerColor = new JComboBox<>(model);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.panel.add(this.playerNameLabel, gbc);
		gbc.gridx = 1;
		this.panel.add(this.playerName, gbc);
		gbc.gridx = 2;
		this.panel.add(this.playerColor, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		this.panel.add(this.createButtonPanel(), gbc);

		this.add(this.panel);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				CreatePlayerDialog.this.canceled = true;
			}
		});
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private JPanel createButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(e -> this.addPlayerAndDispose());
		this.okButton.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					CreatePlayerDialog.this.addPlayerAndDispose();
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> {
			this.canceled = true;
			this.dispose();
		});

		buttonPanel.add(this.okButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void addPlayerAndDispose() {
		if (!this.testInputsAreCorrect()) { return; }
		Game.getInstance().addInstancePlayer(this.playerName.getText(), (MyColor) this.playerColor.getSelectedItem());
		this.canceled = false;
		this.dispose();
	}

	private boolean testInputsAreCorrect() {
		if (this.playerName.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Name must not be empty");
			return false;
		} else if (this.playerName.getText().isBlank()) {
			JOptionPane.showMessageDialog(this, "Name must not be blank");
			return false;
		}
		if (this.playerColor.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "Please choose a Color");
			return false;
		}
		return true;
	}

	public boolean wasCanceled() {
		return this.canceled;
	}

}
