package quinzical.ui.scenes.main;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Question;
import quinzical.festival.Festival;
import quinzical.ui.scenes.QuestionPage;

import static org.apache.commons.lang3.StringUtils.capitalize;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Displays a question in the main game.
 */
public class MainQuestionPage extends QuestionPage {
	private Timer timer;
	private final static int TIME_LIMIT = 30; // 30 seconds
	private int remaining;

	public MainQuestionPage(Game game, Stage stage, Festival festival) throws IOException {
		super(game, stage, festival, game.selectPage());

		// Remove the question text
		((VBox) super.attemptCounter.getParent()).getChildren().remove(super.attemptCounter);

		super.questionText.setPadding(new Insets(0, 0, 10, 0));
	}

	/**
	 * When the answer is submitted, check its validity and
	 * update the user's score. Additionally, show a dialog
	 * with the outcome.
	 */
	@Override
	public void answerSubmitted() {
		boolean correct = question.check(guess.getText());
		
		question.setState((correct)? Question.QuestionState.CORRECT: Question.QuestionState.INCORRECT);
		
		// Calculate reward
		int reward = (getRewardNow() * ((correct) ? 1: 0));
		
		// Add to score
		int score = game.data().score() + reward;
		game.data().setScore(score);
		
		// Update on board
		question.setValue(reward);
		showDialog();
	}

	/**
	 * When the don't know button is pressed,
	 * set its status to incorrect and the reward to 0.
	 * Additionally, show a dialog with the outcome.
	 */
	@Override
	public void dontKnowPressed() {
		question.setState(Question.QuestionState.INCORRECT);
		question.setValue(0);
		showDialog();
	}

	/**
	 * Show a dialog with the outcome
	 */
	private void showDialog() {
		dialog.show(question);
		timer.cancel();
	}

	/**
	 * When the TTS has finished, start a
	 * count down timer.
	 */
	protected void finishedSpeaking() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				// If we're out of time
				if (remaining <= 0) {
					Platform.runLater(new Task<Void>() {
						@Override
						protected Void call() {
							dontKnowPressed();
							return null;
						}
					});
				} else {
					// Refresh label
					Platform.runLater(new Task<Void>() {
						@Override
						protected Void call() {
							refreshTimerLabel();
							return null;
						}
					});
				}
				
				// Count down
				remaining--;
			}
		}, 0, 1000);
	}

	/**
	 * Get current reward based on timer value
	 * @return Reward at this moment
	 */
	private int getRewardNow() {
		return (int) (question.value() * (float) remaining / TIME_LIMIT);
	}

	/**
	 * When this page is shown, populate the window
	 */
	@Override
	public void show(Question question, String cat) {
		super.show(question, cat);

		// Show category instead of question
		questionText.setText(capitalize(cat));

		// Set to incorrect initially so exiting the application will mark it as incorrect
		question.setState(Question.QuestionState.INCORRECT);

		remaining = TIME_LIMIT;
		refreshTimerLabel();
	}

	/**
	 * Refresh the timer label with the current time left
	 */
	private void refreshTimerLabel() {
		timerLabel.setText(String.format("祥 %ds - $%d", remaining, getRewardNow()));
	}
}
