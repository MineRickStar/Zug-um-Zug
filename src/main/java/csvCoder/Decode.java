package csvCoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Decode implements Iterator<String[]> {

	public final String[] header;
	public final Object[][] data;
	private int counter;

	private Decode(String[] header, int lines) {
		this.header = header;
		this.data = new Object[lines][];
		this.counter = 0;
	}

	private void addLine(Object[] dataLine) {
		for (int i = 0; i < this.data.length; i++) {
			if (this.data[i] == null) {
				this.data[i] = dataLine;
				break;
			}
		}
	}

	public static Decode decode(InputStream input) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		List<String> lines = reader.lines().collect(Collectors.toList());
		reader.close();

		if (lines.size() == 0) { return new EmptyDecode(); }

		String[] header = lines.remove(0).split(",");
		Decode decode = new Decode(header, lines.size());
		lines.stream().sequential().map(line -> line.split(",")).forEach(decode::addLine);
		return decode;
	}

	@Override
	public boolean hasNext() {
		return this.counter < this.data.length;
	}

	@Override
	public String[] next() {
		return (String[]) this.data[this.counter++];
	}

	private static class EmptyDecode extends Decode {

		private EmptyDecode() {
			super(null, 0);
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public String[] next() {
			return null;
		}

	}

}
