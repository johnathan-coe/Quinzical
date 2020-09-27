package jeopardy.json;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JsonWriter {
	private String jsonString;

	/**
	 * Create a new JsonWriter with the provided score
	 *
	 * The file is not yet written to the disk (See {@link #saveToFile(String)}).
	 */
	public JsonWriter() {
		jsonString = "{}";
	}

	/**
	 * Execute a `jq` folder to replace our current internal JSON string with a new one
	 */
	protected void write(String filter, String[] extra) throws IOException {
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


	/**
	 * Save the string we've been creating to disk
	 * <p>
	 * Use this _after_ you've written everything.
	 */
	public void saveToFile(String filename) throws IOException {
		FileWriter file = new FileWriter(filename);
		file.write(jsonString);
		file.flush();
		file.close();
	}
}