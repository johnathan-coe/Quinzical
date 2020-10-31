package quinzical.festival;

import javafx.concurrent.Task;
import quinzical.Game;
import quinzical.data.Settings;

import java.io.*;

/**
 * An object that keeps a Festival process open and pipes in different text to say
 */
public class Festival {
	private final Game game;
	private final ProcessBuilder processBuilder;
	private Process process;
	private Task<Boolean> task;
	private String voice;
	private BufferedWriter writer;

	/**
	 * Auckland New Zealand Female Voice
	 */
	public static String AKL_NZ_CW_CG_CG = "akl_nz_cw_cg_cg";
	/**
	 * Auckland New Zealand Male Voice
	 */
	public static String AKL_NZ_JDT_DIPHONE = "akl_nz_jdt_diphone";

	public Festival(Game game) {
		processBuilder = new ProcessBuilder("festival", "--pipe");
		this.game = game;

		setVoice(AKL_NZ_JDT_DIPHONE);
	}

	/**
	 * Say a line of text using festival
	 * @param text Text to say
	 * @return Task that completes when the line has been said
	 */
	public Task<Boolean> say(String text) {
		try {
			// Kill the old task and spin up a new one
			destroy();
			process = processBuilder.start();
			writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			if (voice != null) {
				command(String.format("(voice_%s)", voice));
			}
			
			// Apply settings
			command(String.format("(Parameter.set 'Duration_Stretch (/ 1 %f))", game.data().settings().speed()));
			
			// Say the text
			command(String.format("(SayText \"%s\")", text.replace("\"", "")));
			writer.close();
			writer = null;
			
			// Task that completed when the line has been said
			task = new Task<>() {
				@Override
				protected Boolean call() {
					try {
						process.getInputStream().readAllBytes();
						return !isCancelled();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			new Thread(task).start();
			return task;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Set the speed of the speech.
	 *
	 * 1.0 = Normal Speed.
	 * 2.0 = 2x as Fast.
	 * 0.5 = 2x as Slow.
	 */
	public void setSpeed(float speed) {
		Settings settings = game.data().settings();
		if (speed == settings.speed()) { return; }
		settings.setSpeed(speed);
	}

	/**
	 * Set the voice of festival.
	 *
	 * See AKL_NZ_CW_CG_CG and AKL_NZ_JDT_DIPHONE for some possible voices that can be used (if installed).
	 *
	 * If the voice does not exist, the previous voice will remain being used.
	 */
	public void setVoice(String voice) {
		this.voice = voice;
	}

	/**
	 * Pipe command to the process
	 * @param cmd String for the command
	 * @throws IOException
	 */
	private void command(String cmd) throws IOException {
		writer.write(cmd);
		writer.flush();
	}

	/**
	 * Kill the process
	 * @throws IOException
	 */
	private void destroy() throws IOException {
		if (task != null) {
			task.cancel(false);
		}
		if (writer != null) {
			writer.close();
		}
		if (process != null) {
			process.destroy();
		}
	}
}
