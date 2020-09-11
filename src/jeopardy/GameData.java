package jeopardy;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GameData {
	private boolean loaded = false;
	private int score = 0;
	private final List<GameDataListener> listeners = new ArrayList<>();
	private List<Category> categories = new ArrayList<>();

	public GameData() {	}

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

	public static Task<GameData> freshLoad() {
		Task<GameData> task = new Task<GameData>() {
			@Override
			protected GameData call() throws Exception {
				return freshLoadBlocking();
			}
		};
		new Thread(task).start();
		return task;
	}

	private static GameData freshLoadBlocking() throws IOException {
		GameData newData = new GameData();
		File directory = new File("categories/");
		if (!directory.isDirectory()) {
			System.err.println("Error: The `categories` folder does not exit!");
			newData.loaded = true;
			return newData;
		}
		for (File file: directory.listFiles()) {
			String categoryName = file.getName().split("\\.")[0];
			Category category = new Category(categoryName);
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] data = line.split(",");
				int score = Integer.parseInt(data[0]);
				String question = data[1];
				String answer = data[2];
				Question q = new Question(score, question, answer, false);
				category.addQuestion(q);
			}
			newData.categories().add(category);
		}
		newData.loaded = true;
		return newData;
	}

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

	public void addListener(GameDataListener listener) {
		listeners.add(listener);
	}

	private void publish(GameDataChangedEvent event) {
		for (GameDataListener listener: listeners) {
			listener.handleGameDataChanged(event);
		}
	}

	public void set(GameData data) {
		this.score = data.score;
		this.categories = data.categories;
		this.loaded = data.loaded;
		publish(GameDataChangedEvent.LOADED);
	}

	public boolean isLoading() { return !loaded; }

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
		publish((loaded)? GameDataChangedEvent.LOADED: GameDataChangedEvent.LOADING);
	}

	public int score() { return score; }

	public void setScore(int score) {
		this.score = score;
		publish(GameDataChangedEvent.SCORE_UPDATED);
	}

	public List<Category> categories() { return categories; }

	private static class JsonReader {
		private String jsonString;

		public JsonReader(File file) throws IOException {
			StringBuilder stringBuilder = new StringBuilder();
			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				stringBuilder.append("\n").append(line);
			}
			jsonString = new String(stringBuilder);
		}

		public int score() throws IOException {
			return Integer.parseInt(executeFilter(".score", null).get(0));
		}

		public List<String> categories() throws IOException {
			return executeFilter(".categories | keys[]", new String[]{ "-r" });
		}

		public List<String> getAvailableQuestions(String category) throws IOException {
			return executeFilter(".categories[($CATEGORY)] | keys[]", new String[]{ "-r", "--arg", "CATEGORY", category });
		}

		public Question getQuestion(String category, String value) throws IOException {
			List<String> lines = executeFilter(".categories[($CATEGORY)][$VALUE] | (.question, .answer, .completed)", new String[]{
				"-r",
				"--arg", "CATEGORY", category,
				"--arg", "VALUE", value
			});

			String completedLine = lines.get(2);

			return new Question(
				Integer.parseInt(value),
				lines.get(0),
				lines.get(1),
				completedLine.equals("true")
			);
		}

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

	private static class JsonWriter {
		private String jsonString;

		public JsonWriter(int score) throws IOException {
			jsonString = "{}";
			write("{\"score\": $SCORE}", new String[]{
				"--argjson", "SCORE", Integer.toString(score),
			});
		}

		public void saveToFile() throws IOException {
			FileWriter file = new FileWriter("save.json");
			file.write(jsonString);
			file.flush();
			file.close();
		}

		public void writeCategory(String categoryName) throws IOException {
			write(".categories += {($CATEGORY_NAME): {}}", new String[] {
				"--arg", "CATEGORY_NAME", categoryName
			});
		}

		public void writeQuestion(String categoryName, Question question) throws IOException {
			write(".categories[$CATEGORY_NAME] += { ($VALUE): { \"question\": $QUESTION, \"answer\": $ANSWER, \"completed\": $COMPLETED }}", new String[] {
					"--arg", "CATEGORY_NAME", categoryName,
					"--arg", "VALUE", Integer.toString(question.value()),
					"--arg", "QUESTION", question.question(),
					"--arg", "ANSWER", question.answer(),
					"--argjson", "COMPLETED", Boolean.toString(question.isCompleted()),
			});
		}

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

	public enum GameDataChangedEvent {
		LOADED, SCORE_UPDATED, LOADING
	}
}
