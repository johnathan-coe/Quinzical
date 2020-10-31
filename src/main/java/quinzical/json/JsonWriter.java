package quinzical.json;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Defines template for a class that uses 'jq' to write
 * JSON data to a file.
 */
public class JsonWriter {
	private String jsonString;

	/**
	 * Create a new JsonWriter
	 *
	 * The file is not yet written to the disk (See {@link #saveToFile(String)}).
	 */
	public JsonWriter() {
		jsonString = "{}";
	}

	/**
	 * Execute a `jq` folder to replace our current internal JSON string with a new one
	 * 
	 * @param filter Filter string to pass to 'jq'
	 * @param extra Extra command line options to pass to jq
	 */
	protected void write(String filter, String[] extra) throws IOException {
		// Build a command String list
		List<String> command = new ArrayList<>();
		command.add("jq");
		command.add("-c");
		if (extra != null) {
			command.addAll(Arrays.asList(extra));
		}
		command.add(filter);
		
		// Run the command
		Process proc = new ProcessBuilder(command).start();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		writer.write(jsonString+"\n");
		writer.flush();
		writer.close();

		// Split output into lines
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