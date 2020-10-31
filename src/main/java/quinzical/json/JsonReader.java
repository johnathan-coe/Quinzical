package quinzical.json;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Defines template for a class that uses 'jq' to read
 * JSON data from a file.
 */
public class JsonReader {
	private String jsonString;

	/**
	 * Create a new JsonReader that reads the given file
	 * @param File object
	 */
	public JsonReader(File file) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		Scanner scanner = new Scanner(file);
		
		// Read whole file into a string
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			stringBuilder.append("\n").append(line);
		}
		
		jsonString = new String(stringBuilder);
	}

	/**
	 * Creates a new JsonReader that reads from a given string
	 * @param json Raw json String 
	 */
	public JsonReader(String json) {
		jsonString = json;
	}

	/**
	 * Executes a `jq` process with the provided filter and extra arguments
	 * @param filter Filter string to pass to 'jq'
	 * @param extra Extra arguments to pass
	 * @return Output as a list of strings
	 */
	protected List<String> executeFilter(String filter, String[] extra) throws IOException {
		// Build a command string array
		List<String> command = new ArrayList<>();
		command.add("jq");
		if (extra != null) {
			command.addAll(Arrays.asList(extra));
		}
		command.add(filter);

		// Execute command
		Process proc = new ProcessBuilder(command).start();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		writer.write(jsonString+"\n");
		writer.flush();
		writer.close();

		// Split output into lines
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
