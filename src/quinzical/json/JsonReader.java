package quinzical.json;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class JsonReader {
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
	 * Executes a `jq` process with the provided filter and extra arguments
	 */
	protected List<String> executeFilter(String filter, String[] extra) throws IOException {
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
