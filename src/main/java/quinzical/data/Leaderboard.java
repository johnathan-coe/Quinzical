package quinzical.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
	private final int MAX_LEADERS = 5;
	private List<Integer> leaders;

	private Leaderboard() {
		leaders = new ArrayList<Integer>();
	}
	
	public static Leaderboard loadBlocking() throws IOException {
		Leaderboard l = new Leaderboard();
		File file = new File("./leaders.json");

		if (!file.isFile()) {
			return l;
		}

		// If the file exists, pull leaders from file
		JsonReader reader = new JsonReader(file);
		l.leaders = reader.leaders();

		return l;
	}

	public List<Integer> leaders() { return leaders; }
	
	/**
	 * Called on game exit
	 */
	public void save() throws IOException {
		JsonWriter writer = new JsonWriter();
		writer.writeLeaders(leaders);
		writer.saveToFile("leaders.json");
	}

	/**
	 * Add a score to the leaderboard
	 * @param score
	 */
	public void add(int score) {
		for (int i = 0; i <= leaders.size(); i++) {
			// Insert score in leaderboard
			if (i == leaders.size() || score > leaders.get(i)) {
				leaders.add(i, score);
				break;
			}
		}
		
		leaders = leaders.subList(0, leaders.size() < MAX_LEADERS ? leaders.size() : MAX_LEADERS); 
	}

	private static class JsonReader extends quinzical.json.JsonReader {
		/**
		 * Create a new JsonReader that reads the given file
		 */
		public JsonReader(File file) throws IOException {
			super(file);
		}

		public List<Integer> leaders() throws IOException {
			List<String> l = executeFilter(".leaders[]", null);
			List<Integer> out = new ArrayList<Integer>();
			for (String s : l) {
				out.add(Integer.valueOf(s));
			}
			return out;
		}
	}

	private static class JsonWriter extends quinzical.json.JsonWriter {
		/**
		 * Create a new writer
		 */
		public JsonWriter() {
			super();
		}

		/**
		 * Write leaders to file
		 */
		public void writeLeaders(List<Integer> l) throws IOException {
			write(".leaders = []", null);
			for (int i : l) {
				write(".leaders += [$AMOUNT]", new String[]{
						"--argjson", "AMOUNT", Integer.toString(i)
				});
			}
		}
	}
}