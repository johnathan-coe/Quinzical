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
		// Return the class as a placeholder class which will be filled in later on
		final GameData data = new GameData();

		// Load the GameData in a background thread
		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				// Open the save file
				File file = new File("./storage/save.json");

				// Load the settings and leaderboard files in another thread
				// These are static fields and should only be loaded once in the application lifetime
				Thread settingsThread = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// Load the settings file
							settings = Settings.loadBlocking();
							// Load the leaderboard file afterwards
							leaders = Leaderboard.loadBlocking();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				settingsThread.start();

				GameData newData;
				if (!file.isFile()) { // If the save file doesn't exist, use default values
					System.out.println("No `save.json` file found, assuming fresh game...");
					newData = freshLoadBlocking();
				} else {
					final GameData newDataTemp = new GameData(); // Load the data into this class

					// Load the category files in another thread
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

					// Read the save.json file
					JsonReader reader = new JsonReader(file);
					newDataTemp.score = reader.score();
					List<String> categories = reader.categories();
					for (String c: categories) {
						Category category = new Category(c);
						category.addQuestions(reader.questions(c));
						if (c.equals("International")) { // The international category doesn't go with the others
							newDataTemp.internationalCategory = category;
							continue;
						}
						newDataTemp.categories.add(category);
					}

					// Wait for the category files to be loaded
					categoryThread.join();

					// Set the newData to our temporary class
					newData = newDataTemp;
				}

				// Wait for the thread that loads the settings and leaderboard to finish
				settingsThread.join();

				// Set all the fields of the class we made at the start with our loaded data
				// Do this on the JavaFX ui thread
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

		// Load a category from the JService api in another thread
		final Category[] category = new Category[]{ null };
		Thread jserviceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Load the data from the JService API
					category[0] = JService.load();
				} catch (Exception e) {
					// Loading failed so we have to default to the local backup one
					System.err.println("Accessing the JService API. Check your connection to the internet.");
					System.err.println("Defaulting to local backup international category file...");
					try {
						File file = new File("categories/International");
						Map<String, String[]> parsedMap = CategoryParser.loadCategoryBlocking(file);
						category[0] = Category.fromMapWithRandomValues("International", parsedMap);
					} catch (FileNotFoundException fileNotFoundException) {
						// Loading failed so we should just not have an International category
						fileNotFoundException.printStackTrace();
						category[0] = null;
					}
				}
			}
		});
		jserviceThread.start();

		// Load the category files from disk
		newData.categoryParser = CategoryParser.loadBlocking();

		// Wait for the JService data to load and then set the category loaded as the class's internationalCategory
		jserviceThread.join();
		newData.internationalCategory = category[0];

		// Mark the class as loaded and return it
		newData.loaded = true;
		return newData;
	}

	/**
	 * Save the GameData object into a `save.json` file
	 * and save the settings into a `settings.json` file
	 */
	public void save() throws IOException {
		System.out.println("Saving...");
		
		// Ensure save directory exists
		new File("./storage/").mkdir();

		// Save the settings and leaderboard in another file - do this concurrently in another thread
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("Saving to `settings.json`");
				try {
					// Save the settings file
					settings.save();
					// Save the leaderboard file
					leaders.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Saved to `settings.json`!");
			}
		}).start();

		// Create a JSON writer object to save the game data to a save.json file
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
		writer.saveToFile("./storage/save.json");
		System.out.println("Saved to `save.json`!");
	}

	/**
	 * All the categories have been completed
	 */
	public boolean isAllDone() {
		for (Category category: categories) {
			for (Question question: category.questions()) {
				if (!question.isCompleted()) {
					return false;
				}
			}
		}
		// If there is an international category, check if it is completed
		return internationalCategory == null || internationalCategory.isCompleted();
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
		publish(GameDataChangedEvent.LOADED); // Tell all the GameDataListeners that the GameData has been loaded
	}

	/**
	 * Get the category parser used to load the categories
	 */
	public CategoryParser parser() { return categoryParser;	}

	/**
	 * Get the settings loaded from the settings file
	 */
	public Settings settings() { return settings;	}

	/**
	 * Get the leaderboard loaded from the leaderboard file
	 */
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
		 * Get all the questions of a specified category
		 */
		public List<Question> questions(String category) throws IOException {
			List<String> stringList = executeFilter(".categories[($CATEGORY)][] | .value, .question, .prompt, .answer, .completed", new String[]{
					"-r",
					"--arg", "CATEGORY", category,
			});

			// stringList will contain 5n strings where n is the number of questions
			// The fields are in the order of
			// - value
			// - question
			// - prompt
			// - answer
			// - completed

			List<Question> questionList = new ArrayList<>();
			int i = 0;
			while (i < stringList.size()) {
				int value = Integer.parseInt(stringList.get(i));
				String questionText = stringList.get(i+1);
				String prompt = stringList.get(i+2);
				String answer = stringList.get(i+3);
				Question.QuestionState completed = Question.QuestionState.parse(stringList.get(i+4));
				if (completed == null) {
					System.err.printf("Cannot parse `%s` into a QuestionState.%n", stringList.get(i+3));
					continue;
				}
				questionList.add(new Question(value, prompt, questionText, answer, completed));
				i += 5;
			}

			return questionList;
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
			write(".categories += {($CATEGORY_NAME): []}", new String[]{
					"--arg", "CATEGORY_NAME", categoryName
			});
		}

		/**
		 * Write a question into the category provided
		 */
		public void writeQuestion(String categoryName, Question question) throws IOException {
			write(".categories[$CATEGORY_NAME] += [ { \"question\": $QUESTION, \"prompt\": $PROMPT, \"answer\": $ANSWER, \"completed\": $COMPLETED, \"value\": $VALUE } ]", new String[]{
					"--arg", "CATEGORY_NAME", categoryName,
					"--arg", "VALUE", Integer.toString(question.value()),
					"--arg", "QUESTION", question.question(),
					"--arg", "PROMPT", question.prompt(),
					"--arg", "ANSWER", question.answer(),
					"--arg", "COMPLETED", question.state().toString(),
					"--arg", "VALUE", Integer.toString(question.value()),
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
