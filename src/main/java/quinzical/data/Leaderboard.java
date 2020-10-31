package quinzical.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of leaderboard values.
 */
public class Leaderboard {
	private final int MAX_LEADERS = 5;
	private List<Integer> leaders;

	private Leaderboard() {
		leaders = new ArrayList<Integer>();
	}
	
	// Get list of leaders
	public List<Integer> leaders() { return leaders; }
	
	/**
	 * Trophies available
	 */
	private enum Trophy {BRONZE, SILVER, GOLD, NONE};
	
	/**
	 * Get trophy applicable to a score
	 * @param Score
	 * @return Trophy
	 */
	private static Trophy trophyFor(int score) {
		if (score >= 5625) {
			return Trophy.GOLD;
		} else if (score >= 3750) {
			return Trophy.SILVER;
		} else if (score >= 1875) {
			return Trophy.BRONZE;
		} else {
			return Trophy.NONE;
		}
	}
	
	/**
	 * Get trophy colour from score
	 * @param score
	 * @return Colour string
	 */
	public static String trophyColour(int score) {
		switch (trophyFor(score)) {
			case GOLD:
				return "#D4AF37";
			case SILVER:
				return "#A8A9AD";
			case BRONZE:
				return "#AA7042";
			default:
				return "white";
		}
	}
	
	/**
	 * Get a progress message for a score
	 * @param score
	 * @return A progress string
	 */
	public static String progressMessage(int score) {
		String distString = "";
		
		switch (trophyFor(score)) {
			case GOLD:
				distString = Integer.toString(7500-score);
				break;
			case SILVER:
				distString = Integer.toString(5625-score);
				break;
			case BRONZE:
				distString = Integer.toString(3750-score);
				break;
			default:
				distString = Integer.toString(1875-score);
		}

		return "-$" + distString + "  ";
	}
	
	/**
	 * Load the leaderboard from file
	 * @return A leaderboard
	 * @throws IOException
	 */
	public static Leaderboard loadBlocking() throws IOException {
		Leaderboard l = new Leaderboard();
		File file = new File("./storage/leaders.json");

		if (!file.isFile()) {
			return l;
		}

		// If the file exists, pull leaders from file
		JsonReader reader = new JsonReader(file);
		l.leaders = reader.leaders();

		return l;
	}
	
	/**
	 * Called on game exit
	 */
	public void save() throws IOException {
		JsonWriter writer = new JsonWriter();
		writer.writeLeaders(leaders);
		writer.saveToFile("./storage/leaders.json");
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

	/**
	 * A reader that pulls data from the JSON
	 */
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

	/**
	 * Writer tha puts the leaderboard into the file.
	 */
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
