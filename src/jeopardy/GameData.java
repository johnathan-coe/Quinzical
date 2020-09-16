package jeopardy;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.util.*;

/**
 * Represents the data of a current game
 */
public class GameData {
	/**
	 * Whether the GameData has been loaded from the file
	 */
	private boolean loaded = false;
	/**
	 * The current score of the user
	 */
	private int score = 0;
	/**
	 * All the objects listening to changes in the game data
	 */
	private final List<GameDataListener> listeners = new ArrayList<>();
	/**
	 * The categories that can be chosen in the game
	 */
	private List<Category> categories = new ArrayList<>();

	/**
	 * You should be using {@link #load()} to create your GameData objects
	 */
	private GameData() {	}

	/**
	 * Load the GameData from a `save.json` file
	 */
	public static GameData load() {
		final GameData data = new GameData();
		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				File file = new File("save.json");

				GameData newData = null;
				if (!file.isFile()) { // If the file doesn't exist, keep the default values
					System.out.println("No `save.json` file found, assuming fresh game...");
					newData = freshLoadBlocking();
				} else {
					JsonReader reader = new JsonReader(file);
					newData = new GameData();
					newData.score = reader.score();
					List<String> categories = reader.categories();
					for (String c: categories) {
						Category category = new Category(c);
						for (String question: reader.getAvailableQuestions(c)) {
							Question q = reader.getQuestion(c, question);
							category.addQuestion(q);
						}
						newData.categories.add(category);
					}
				}

				final GameData newData2 = newData;
				Platform.runLater(new Task<Void>() {
					@Override
					protected Void call() {
						if (newData2 != null) {
							newData2.loaded = true;
							data.set(newData2);
						}
						return null;
					}
				});

				System.out.println("`save.json` file loaded!");
				return null;
			}
		}).start();
		return data;
	}

	/**
	 * Create a fresh game from the category files
	 * @return A task that will eventually return a GameData object
	 */
	public static Task<GameData> freshLoad() {
		Task<GameData> task = new Task<>() {
			@Override
			protected GameData call() throws Exception {
				return freshLoadBlocking();
			}
		};
		new Thread(task).start();
		return task;
	}

	/**
	 * Loads the csv files from the categories folder.
	 *
	 * Note: This will _block_ the thread it is called in so you shouldn't run it in a UI thread.
	 */
	private static GameData freshLoadBlocking() throws IOException {
		GameData newData = new GameData();
		CategoryParser parser = CategoryParser.loadBlocking();
		List<String> categories = new ArrayList<>(parser.categories());
		for (int j = 0; j < 5; j++) {
			int categoryIndex = (int)(categories.size() * Math.random());
			String categoryName = categories.get(categoryIndex);
			Category category = new Category(categoryName);
			Map<String, String> questionSet = parser.getCategory(categoryName);
			List<String> questions = new ArrayList<>(questionSet.keySet());
			for (int i = 100; i <= 500; i += 100) {
				int index = (int) (Math.random() * questionSet.size());
				String question = questions.get(index);
				String answer = questionSet.get(question);
				category.addQuestion(new Question(i, question, answer, Question.QuestionState.UNATTEMPTED));
				questionSet.remove(question);
				questions.remove(question);
			}
			newData.categories().add(category);
			categories.remove(categoryIndex);
			parser.categories().remove(categoryName);
		}
		newData.loaded = true;
		return newData;
	}

	/**
	 * Save the GameData object into a `save.json` file
	 */
	public void save() throws IOException {
		System.out.println("Saving...");
		JsonWriter writer = new JsonWriter(score);
		for (Category category: categories) {
			writer.writeCategory(category.name());
			for (Question q: category.questions()) {
				writer.writeQuestion(category.name(), q);
			}
		}
		writer.saveToFile();
		System.out.println("Saved to `save.json`!");
	}

	/**
	 * Adds a listener object which will listen to any changes in the GameData object
	 */
	public void addListener(GameDataListener listener) {
		listeners.add(listener);
	}

	/**
	 * This sends a GameDataChangedEvent object to all listeners
	 *
	 * This should be used when you've changed the GameData object
	 */
	private void publish(GameDataChangedEvent event) {
		for (GameDataListener listener: listeners) {
			listener.handleGameDataChanged(event);
		}
	}

	/**
	 * All the values of the GameData get set to the same values as the GameData passed in
	 *
	 * This should be used when you wish to set the GameData to a new value but do not wish to change the
	 * reference other objects hold.
	 */
	public void set(GameData data) {
		this.score = data.score;
		this.categories = data.categories;
		this.loaded = data.loaded;
		publish(GameDataChangedEvent.LOADED);
	}

	/**
	 * Returns whether the GameData is being loaded or not
	 */
	public boolean isLoading() { return !loaded; }

	/**
	 * Sets whether the GameData has been loaded or not
	 *
	 */
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
		publish((loaded)? GameDataChangedEvent.LOADED: GameDataChangedEvent.LOADING);
	}

	/**
	 * Gets the current score of the user
	 */
	public int score() { return score; }

	/**
	 * Sets the current score of the user
	 */
	public void setScore(int score) {
		this.score = score;
		publish(GameDataChangedEvent.SCORE_UPDATED);
	}

	/**
	 * Gets a list of categories that have been loaded
	 */
	public List<Category> categories() { return categories; }

	/**
	 * A utility class to read the save file
	 */
	private static class JsonReader {
		private String jsonString;

		/**
		 * Create a new JsonReader that reads the given file
		 */
		public JsonReader(File file) throws IOException {
			StringBuilder stringBuilder = new StringBuilder();
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				stringBuilder.append("\n").append(line);
			}
			jsonString = new String(stringBuilder);
		}

		/**
		 * Gets the save file's score
		 */
		public int score() throws IOException {
			return Integer.parseInt(executeFilter(".score", null).get(0));
		}

		/**
		 * Gets the categories in the save file
		 *
		 * e.g. ["Animals", "Countries"]
		 */
		public List<String> categories() throws IOException {
			return executeFilter(".categories | keys[]", new String[]{ "-r" });
		}

		/**
		 * Gets all the questions of the provided category in the save file
		 *
		 * Note: All values are denoted by their score.
		 *
		 * e.g. ["100", "200", "300"]
		 */
		public List<String> getAvailableQuestions(String category) throws IOException {
			return executeFilter(".categories[($CATEGORY)] | keys[]", new String[]{ "-r", "--arg", "CATEGORY", category });
		}

		/**
		 * Get a question form the save file by providing the category it belongs to and how much it's worth
		 */
		public Question getQuestion(String category, String value) throws IOException {
			List<String> lines = executeFilter(".categories[($CATEGORY)][$VALUE] | (.question, .answer, .completed)", new String[]{
				"-r",
				"--arg", "CATEGORY", category,
				"--arg", "VALUE", value
			});

			Question.QuestionState state = Question.QuestionState.UNATTEMPTED;
			switch (lines.get(2)) {
				case "UNATTEMPTED":
					state = Question.QuestionState.UNATTEMPTED;
					break;
				case "CORRECT":
					state = Question.QuestionState.CORRECT;
					break;
				case "INCORRECT":
					state = Question.QuestionState.INCORRECT;
					break;
				default:
					System.err.printf("Could not parse question state `%s`%n", lines.get(2));
					System.exit(1);
			}

			return new Question(
				Integer.parseInt(value),
				lines.get(0),
				lines.get(1),
				state
			);
		}

		/**
		 * Executes a `jq` process with the provided filter and extra arguments
		 */
		private List<String> executeFilter(String filter, String[] extra) throws IOException {
			List<String> command = new ArrayList<>();
			command.add("jq");
			if (extra != null) {
				command.addAll(Arrays.asList(extra));
			}
			command.add(filter);

			Process proc = new ProcessBuilder(command).start();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			writer.write(jsonString+"\n");
			writer.flush();
			writer.close();

			List<String> lines = new ArrayList<>();
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}

			proc.destroy();
			return lines;
		}
	}

	/**
	 * A utility class for writing the JSON file
	 */
	private static class JsonWriter {
		private String jsonString;

		/**
		 * Create a new JsonWriter with the provided score
		 *
		 * The file is not yet written to the disck (See {@link #saveToFile()}).
		 */
		public JsonWriter(int score) throws IOException {
			jsonString = "{}";
			write("{\"score\": $SCORE}", new String[]{
				"--argjson", "SCORE", Integer.toString(score),
			});
		}

		/**
		 * Save the string we've been creating to disk
		 *
		 * Use this _after_ you've written everything.
		 */
		public void saveToFile() throws IOException {
			FileWriter file = new FileWriter("save.json");
			file.write(jsonString);
			file.flush();
			file.close();
		}

		/**
		 * Write an empty category into the save file
		 */
		public void writeCategory(String categoryName) throws IOException {
			write(".categories += {($CATEGORY_NAME): {}}", new String[] {
				"--arg", "CATEGORY_NAME", categoryName
			});
		}

		/**
		 * Write a question into the category provided
		 */
		public void writeQuestion(String categoryName, Question question) throws IOException {
			write(".categories[$CATEGORY_NAME] += { ($VALUE): { \"question\": $QUESTION, \"answer\": $ANSWER, \"completed\": $COMPLETED }}", new String[] {
					"--arg", "CATEGORY_NAME", categoryName,
					"--arg", "VALUE", Integer.toString(question.value()),
					"--arg", "QUESTION", question.question(),
					"--arg", "ANSWER", question.answer(),
					"--arg", "COMPLETED", question.state().toString(),
			});
		}

		/**
		 * Execute a `jq` folder to replace our current internal JSON string with a new one
		 */
		private void write(String filter, String[] extra) throws IOException {
			List<String> command = new ArrayList<>();
			command.add("jq");
			command.add("-c");
			if (extra != null) {
				command.addAll(Arrays.asList(extra));
			}
			command.add(filter);

			Process proc = new ProcessBuilder(command).start();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			writer.write(jsonString+"\n");
			writer.flush();
			writer.close();

			List<String> lines = new ArrayList<>();
			jsonString = reader.readLine();
			proc.destroy();
		}
	}

	/**
	 * The possible events that can be produced by GameData
	 */
	public enum GameDataChangedEvent {
		/**
		 * A fresh GameData has been updated
		 */
		LOADED,
		/**
		 * The score has been updated
		 */
		SCORE_UPDATED,
		/**
		 * The GameData is being loaded
		 */
		LOADING
	}
}
