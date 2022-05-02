package application;

import java.util.ArrayList;

import game.Player;

public final class ApplicationLog {

	private static ApplicationLog instance;

	public static ApplicationLog getInstance() {
		if (ApplicationLog.instance == null) {
			ApplicationLog.instance = new ApplicationLog();
		}
		return ApplicationLog.instance;
	}

	private ArrayList<String> logs;

	private ApplicationLog() {
		this.logs = new ArrayList<>();
	}

	public void addLog(Player player, GameMove gameMove) {
		StringBuilder sb = new StringBuilder(100);
		sb.append("Player: ");
		sb.append(player.getName());
		sb.append(", Move: ");
		sb.append(gameMove.name());
		this.logs.add(sb.toString());
	}

	public String getLatestLog() {
		return this.logs.get(this.logs.size() - 1);
	}

	enum GameMove {
		COLORCARDS_DRAWN, MISSIONCARDS_DRAWN, BUYING_CONNECTION,;
	}

}
