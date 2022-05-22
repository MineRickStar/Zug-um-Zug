package gui.gameStart;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import application.Application;
import game.Game;

public class GameStartDialog extends JDialog {

	private static final long serialVersionUID = -8181710220690272213L;

	private JTabbedPane tabbedPane;

	private PlayerPanel playerPanel;
	private ComPanel comPanel;
	private MapPanel mapPanel;
	private RulesPanel rulesPanel;

	public GameStartDialog() {
		super(Application.frame, "New Game", true);
		ToolTipManager.sharedInstance().setInitialDelay(0);
		this.setLayout(new GridBagLayout());
		this.tabbedPane = new JTabbedPane();

		this.playerPanel = new PlayerPanel(this);
		this.comPanel = new ComPanel(this);
		this.mapPanel = new MapPanel(this);
		this.rulesPanel = new RulesPanel(this);

		this.addPanel(this.playerPanel);
		this.addPanel(this.comPanel);
		this.addPanel(this.mapPanel);
		this.addPanel(this.rulesPanel);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(this.tabbedPane, gbc);
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridy = 1;
		this.add(this.getButtonPanel(), gbc);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(Application.frame);
		this.setVisible(true);
	}

	private void addPanel(AbstractTabbedPanel panel) {
		this.tabbedPane.addTab(panel.getDisplayName(), panel);
	}

	private JPanel getButtonPanel() {
		JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));

		JButton okButton = new JButton("OK");
		okButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_DOWN_MASK), "OK");
		okButton.getActionMap().put("OK", new AbstractAction() {
			private static final long serialVersionUID = 5594240801235444580L;

			@Override
			public void actionPerformed(ActionEvent e) {
				GameStartDialog.this.testOK();
			}
		});
		okButton.addActionListener(e -> this.testOK());

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> this.dispose());

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void testOK() {
		boolean rules = this.confirmDialog(this.rulesPanel);
		boolean player = this.confirmDialog(this.playerPanel);
		boolean coms = this.confirmDialog(this.comPanel);
		boolean map = this.confirmDialog(this.mapPanel);
		if (rules && player && coms && map) {
			this.rulesPanel.save();
			this.playerPanel.save();
			this.comPanel.save();
			this.mapPanel.save();
			this.dispose();
			Game.getInstance().startGame();
		}
	}

	private boolean confirmDialog(AbstractTabbedPanel tabbedPanel) {
		if (!tabbedPanel.isAllCorrect()) {
			this.tabbedPane.setSelectedComponent(tabbedPanel);
			return false;
		}
		return true;
	}

}
