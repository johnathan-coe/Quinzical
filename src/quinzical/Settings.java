package quinzical;

import java.io.*;

public class Settings {
	private float festivalSpeed = 1;

	private Settings() {}

	public static Settings loadBlocking() throws IOException {
		Settings settings = new Settings();
		File file = new File("./settings.json");

		if (!file.isFile()) {
			return settings;
		}

		JsonReader reader = new JsonReader(file);

		settings.festivalSpeed = reader.speed();

		return settings;
	}

	public void save() throws IOException {
		JsonWriter writer = new JsonWriter();
		writer.writeSpeed(festivalSpeed);
		writer.saveToFile("settings.json");
	}

	public float speed() { return festivalSpeed; }

	public void setSpeed(float speed) { festivalSpeed = speed; }

	private static class JsonReader extends quinzical.json.JsonReader {
		/**
		 * Create a new JsonReader that reads the given file
		 */
		public JsonReader(File file) throws IOException {
			super(file);
		}


		/**
		 * Gets the speed value
		 */
		public float speed() throws IOException {
			return Float.parseFloat(executeFilter(".speed", null).get(0));
		}
	}

	private static class JsonWriter extends quinzical.json.JsonWriter {
		/**
		 * Create a new writer
		 */
		public JsonWriter() {
			super();
		}

		/**
		 * Write the given speed to the JSON string
		 */
		public void writeSpeed(float speed) throws IOException {
			write(".speed = $SPEED", new String[]{
					"--argjson", "SPEED", Float.toString(speed)
			});
		}
	}
}
