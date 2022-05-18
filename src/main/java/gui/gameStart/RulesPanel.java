package gui.gameStart;

import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import game.Game;
import game.GameMap;

public class RulesPanel extends JPanel implements ITabbedPanel {

	private static final long serialVersionUID = 5877627924696089813L;

	private JLabel mapLabel;
	private JComboBox<GameMap> mapComboBox;

	public RulesPanel() {
		super(new GridBagLayout());
		this.layoutComponents();
	}

	private void layoutComponents() {

	}

	@Override
	public boolean isAllCorrect() {
		return true;
	}

	@Override
	public boolean save() {
		Game.getInstance().setMap(new GameMap("Germany original"));
		return true;
	}

}
