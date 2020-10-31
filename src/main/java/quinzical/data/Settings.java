package quinzical.data;

import java.io.*;

/**
 * Settings data for the game
 * 
 * This currently only includes the speech speed.
 */
public class Settings {
	private float festivalSpeed = 1;

	private Settings() {}

	/**
	 * Blocking load from settings file
	 * @return Settings object
	 * @throws IOException
	 */
	public static Settings loadBlocking() throws IOException {
		Settings settings = new Settings();
		File file = new File("./storage/settings.json");

		if (!file.isFile()) {
			return settings;
		}

		JsonReader reader = new JsonReader(file);

		settings.festivalSpeed = reader.speed();

		return settings;
	}

	/**
	 * Save current data to file
	 * @throws IOException
	 */
	public void save() throws IOException {
		JsonWriter writer = new JsonWriter();
		writer.writeSpeed(festivalSpeed);
		writer.saveToFile("./storage/settings.json");
	}

	// Get and set the current speed for TTS
	public float speed() { return festivalSpeed; }
	public void setSpeed(float speed) { festivalSpeed = speed; }

	/**
	 * Reader that pulls settings from JSON file
	 */
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

	/**
	 * Writer that pushes settings to JSON file
	 */
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
