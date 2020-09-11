package jeopardy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Festival {
	private Process process;
	private BufferedWriter writer;

	public Festival() throws IOException {
		process = new ProcessBuilder("festival", "--pipe").start();
		writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}

	public void say(String text) {
		try {
			writer.write(String.format("(SayText \"%s\")", text));
			writer.flush();
		} catch (IOException e) {
			System.err.println("Could not use festival: " + e.toString());
		}
	}

	public void destroy() throws IOException {
		writer.close();
		process.destroy();
	}
}
