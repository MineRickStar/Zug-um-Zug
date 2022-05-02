package application;

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class StopWatch {

	private final List<List<Long>> allTimes;
	private final ArrayList<String> names;
	private ArrayList<Long> fastest;
	private ArrayList<Long> times;

	private long prev;

	public StopWatch() {
		this.allTimes = new ArrayList<>();
		this.names = new ArrayList<>();
		this.fastest = new ArrayList<>();
		this.times = new ArrayList<>();
	}

	public void newWatch() {
		this.allTimes.add(this.times);
		this.times = new ArrayList<>();
		this.prev = System.nanoTime();
	}

	public void round() {
		long c = System.nanoTime();
		long diff = c - this.prev;
		this.times.add(diff);
		if (this.fastest.size() > this.times.size()) {
			long fastest = this.fastest.get(this.times.size() - 1);
			if (diff < fastest) {
				this.fastest.add(this.times.size() - 1, diff);
			}
		} else {
			this.fastest.add(diff);
		}
		this.prev = c;
	}

	public void round(String name) {
		if (!this.names.contains(name)) {
			this.names.add(name);
		}
		this.round();
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(", ");
		for (int i = 0; i < this.times.size(); i++) {
			long time = this.times.get(i);
			LocalTime t = LocalTime.ofNanoOfDay(time);
			long millis = t.getLong(ChronoField.MILLI_OF_SECOND);
			String timeString = t.getSecond() + "." + millis;
			if (millis == 0) {
				timeString += "00." + t.getLong(ChronoField.MICRO_OF_SECOND);
			}
			timeString += "s";
			sj.add(this.names.get(i) + " " + timeString + " (" + this.fastest.get(i) + ")");
		}
		return sj.toString();
	}

}
