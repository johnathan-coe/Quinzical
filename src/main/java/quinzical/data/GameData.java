package quinzical.data;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import quinzical.jservice.JService;

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
	 * The {@link CategoryParser} used by the GameData
	 */
	private CategoryParser categoryParser;
	/**
	 * The settings for the game
	 */
	private static Settings settings;
	/**
	 * Leaderboard
	 */
	private static Leaderboard leaders;
	/**
	 * The international category fetched from the internet
	 */
	private Category internationalCategory;

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

				Thread settingsThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							settings = Settings.loadBlocking();
							leaders = Leaderboard.loadBlocking();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				settingsThread.start();

				GameData newData;
				if (!file.isFile()) { // If the file doesn't exist, keep the default values
					System.out.println("No `save.json` file found, assuming fresh game...");
					newData = freshLoadBlocking();
				} else {
					final GameData newDataTemp = new GameData();
					Thread categoryThread = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								newDataTemp.categoryParser = CategoryParser.loadBlocking();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
					}); // We still need to read the CategoryParser
					categoryThread.start();
					JsonReader reader = new JsonReader(file);
					newDataTemp.score = reader.score();
					List<String> categories = reader.categories();
					Category internationalCategory = null;
					for (String c: categories) {
						Category category = new Category(c);
						for (String question: reader.getAvailableQuestions(c)) {
							Question q = reader.getQuestion(c, question);
							category.addQuestion(q);
						}
						if (c.equals("International")) { // The international category doesn't go with the others
							newDataTemp.internationalCategory = category;
							continue;
						}
						newDataTemp.categories.add(category);
					}
					categoryThread.join();
					newData = newDataTemp;
				}
				settingsThread.join();

				final GameData newData2 = newData;
				Platform.runLater(new Task<Void>() {
					@Override
					protected Void call() {
						newData2.loaded = true;
						data.set(newData2);
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
	private static GameData freshLoadBlocking() throws IOException, InterruptedException {
		GameData newData = new GameData();

		// Load a category from the jservice api in another thread
		final Category[] category = new Category[]{ null };
		Thread jserviceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					category[0] = JService.load();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		jserviceThread.start();

		// Load the categories
		//		List<String> categories = new ArrayList<>(parser.categories());
//		for (int j = 0; j < 5; j++) {
//			int categoryIndex = (int)(categories.size() * Math.random());
//			String categoryName = categories.get(categoryIndex);
//			Category category = new Category(categoryName);
//			Map<String, String[]> questionSet = parser.getCategory(categoryName);
//			List<String> questions = new ArrayList<>(questionSet.keySet());
//			for (int i = 100; i <= 500; i += 100) {
//				int index = (int) (Math.random() * questionSet.size());
//				String question = questions.get(index);
//
//				String prompt = questionSet.get(question)[0];
//				String answer = questionSet.get(question)[1];
//				category.addQuestion(new Question(i, prompt, question, answer, Question.QuestionState.UNATTEMPTED));
//				questionSet.remove(question);
//				questions.remove(question);
//			}
//			newData.categories().add(category);
//			categories.remove(categoryIndex);
//		}
		newData.categoryParser = CategoryParser.loadBlocking();

		jserviceThread.join();
		newData.internationalCategory = category[0];

		newData.loaded = true;
		return newData;
	}

	/**
	 * Save the GameData object into a `save.json` file
	 * and save the settings into a `settings.json` file
	 */
	public void save() throws IOException {
		System.out.println("Saving...");

		new Thread(new Runnable() { // Save the settings in another file - do this concurrently
			@Override
			public void run() {
				System.out.println("Saving to `settings.json`");
				try {
					settings.save();
					leaders.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Saved to `settings.json`!");
			}
		}).start();

		JsonWriter writer = new JsonWriter(score);
		List<Category> categories_ = categories;
		if (internationalCategory != null) { // Save the international category as well if it exists
			categories_ = new ArrayList<>(categories_); // We need to make a new copy so that the original isn't edited
			categories_.add(internationalCategory);
		}
		for (Category category: categories_) {
			writer.writeCategory(category.name());
			for (Question q: category.questions()) {
				writer.writeQuestion(category.name(), q);
			}
		}
		writer.saveToFile("save.json");
		System.out.println("Saved to `save.json`!");
	}

	public boolean isAllDone() {
		for (Category category: categories) {
			for (Question question: category.questions()) {
				if (!question.isCompleted()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Resets the GameData object _in another thread_
	 */
	public void reset() {
		this.setLoaded(false);
		Task<GameData> task = freshLoad();
		task.setOnSucceeded(new EventHandler<>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				set(task.getValue());
			}
		});
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
		this.categoryParser = data.categoryParser;
		this.loaded = data.loaded;
		this.internationalCategory = data.internationalCategory;
		publish(GameDataChangedEvent.LOADED);
	}

	public CategoryParser parser() { return categoryParser;	}

	public Settings settings() { return settings;	}
	public Leaderboard leaders() { return leaders;	}

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
	 * Gets the international category
	 */
	public Category internationalCategory() { return internationalCategory; }

	/**
	 * A utility class to read the save file
	 */
	private static class JsonReader extends quinzical.json.JsonReader {

		/**
		 * Create a new JsonReader that reads the given file
		 */
		public JsonReader(File file) throws IOException {
			super(file);
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
			List<String> lines = executeFilter(".categories[($CATEGORY)][$VALUE] | (.question, .prompt, .answer, .completed)", new String[]{
				"-r",
				"--arg", "CATEGORY", category,
				"--arg", "VALUE", value
			});

			Question.QuestionState state = Question.QuestionState.UNATTEMPTED;
			switch (lines.get(3)) {
				case "UNATTEMPTED" -> state = Question.QuestionState.UNATTEMPTED;
				case "CORRECT" -> state = Question.QuestionState.CORRECT;
				case "INCORRECT" -> state = Question.QuestionState.INCORRECT;
				default -> {
					System.err.printf("Could not parse question state `%s`%n", lines.get(3));
					System.exit(1);
				}
			}

			return new Question(
				Integer.parseInt(value),
				lines.get(1),
				lines.get(0),
				lines.get(2),
				state
			);
		}
	}

	/**
	 * A utility class for writing the JSON file
	 */
	private static class JsonWriter extends quinzical.json.JsonWriter {
		/**
		 * Create a new JsonWriter with the provided score
		 * <p>
		 * The file is not yet written to the disk (See {@link #saveToFile(String)}).
		 */
		public JsonWriter(int score) throws IOException {
			super();
			write("{\"score\": $SCORE}", new String[]{
					"--argjson", "SCORE", Integer.toString(score),
			});
		}

		/**
		 * Write an empty category into the save file
		 */
		public void writeCategory(String categoryName) throws IOException {
			write(".categories += {($CATEGORY_NAME): {}}", new String[]{
					"--arg", "CATEGORY_NAME", categoryName
			});
		}

		/**
		 * Write a question into the category provided
		 */
		public void writeQuestion(String categoryName, Question question) throws IOException {
			write(".categories[$CATEGORY_NAME] += { ($VALUE): { \"question\": $QUESTION, \"prompt\": $PROMPT, \"answer\": $ANSWER, \"completed\": $COMPLETED }}", new String[]{
					"--arg", "CATEGORY_NAME", categoryName,
					"--arg", "VALUE", Integer.toString(question.value()),
					"--arg", "QUESTION", question.question(),
					"--arg", "PROMPT", question.prompt(),
					"--arg", "ANSWER", question.answer(),
					"--arg", "COMPLETED", question.state().toString(),
			});
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
