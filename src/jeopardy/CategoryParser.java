package jeopardy;

import javafx.concurrent.Task;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Parses all the category files in the categories folder
 */
public class CategoryParser {
	private final Map<String, Map<String, String>> categories = new HashMap<>();

	/**
	 * This is a debug function to ensure that the files are being parsed correctly
	 *
	 * todo: Remove when being used in production
	 */
	public static void main(String[] args) throws FileNotFoundException {
		CategoryParser parser = CategoryParser.loadBlocking();
		for (Map.Entry<String, Map<String, String>> c: parser.categories.entrySet()) {
			System.out.println(c.getKey()+":");
			for (Map.Entry<String, String> q: c.getValue().entrySet()) {
				System.out.println("  "+q.getKey()+"=>"+q.getValue());
			}
		}
	}

	private CategoryParser(){}

	/**
	 * Loads the file and returns a Task - this _can_ be used in the UI thread.
	 */
	public static Task<CategoryParser> load() {
		Task<CategoryParser> task = new Task<>() {
			@Override
			protected CategoryParser call() throws Exception {
				return CategoryParser.loadBlocking();
			}
		};
		new Thread(task).start();
		return task;
	}

	/**
	 * Loads the file but is *blocking* therefore it should not be used in a UI thread
	 */
	public static CategoryParser loadBlocking() throws FileNotFoundException {
		CategoryParser parser = new CategoryParser();
		File directory = new File("categories/");
		if (!directory.isDirectory()) {
			System.err.println("The categories folder does not exist!");
		}
		for (File file: directory.listFiles()) {
			Scanner scanner = new Scanner(file);
			String categoryName = file.getName().split("\\.")[0];
			Map<String, String> category = new HashMap<>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] lineSplit = line.split("\\(");
				String question = lineSplit[0].strip();
				lineSplit = lineSplit[1].split("\\)");
				String answer = lineSplit[1].strip();
				category.put(question, answer);
			}
			parser.categories.put(categoryName, category);
		}
		return parser;
	}

	public void removeCategory(String categoryName) { categories.remove(categoryName); }

	/**
	 * Gets the categories that have been loaded
	 */
	public Set<String> categories() {
		return categories.keySet();
	}

	/**
	 * Get the questions and answers from a category
	 */
	public Map<String, String> getCategory(String category) {
		return categories.get(category);
	}
}
