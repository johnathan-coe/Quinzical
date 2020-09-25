package jeopardy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * An object that keeps a Festival process open and pipes in different text to say
 */
public class Festival {
	private Process process;
	private BufferedWriter writer;
	private float currentSpeed = 1.0f;

	/**
	 * Auckland New Zealand Female Voice
	 */
	public static String AKL_NZ_CW_CG_CG = "akl_nz_cw_cg_cg";
	/**
	 * Auckland New Zealand Male Voice
	 */
	public static String AKL_NZ_JDT_DIPHONE = "akl_nz_jdt_diphone";

	public Festival() throws IOException {
		process = new ProcessBuilder("festival", "--pipe").start();
		writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

		setVoice(AKL_NZ_JDT_DIPHONE);
	}

	public void say(String text) {
		command(String.format("(SayText \"%s\")", text));
	}

	/**
	 * Set the speed of the speech.
	 *
	 * 1.0 = Normal Speed.
	 * 2.0 = 2x as Fast.
	 * 0.5 = 2x as Slow.
	 */
	public void setSpeed(float speed) {
		if (speed == currentSpeed) { return; }
		currentSpeed = speed;
		command(String.format("(Parameter.set 'Duration_Stretch (/ 1 %f))", speed));
	}

	/**
	 * Set the voice of festival.
	 *
	 * See AKL_NZ_CW_CG_CG and AKL_NZ_JDT_DIPHONE for some possible voices that can be used (if installed).
	 *
	 * If the voice does not exist, the previous voice will remain being used.
	 */
	public void setVoice(String voice) {
		command(String.format("(voice_%s)", voice));
	}

	private void command(String cmd) {
		try {
			writer.write(cmd);
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
