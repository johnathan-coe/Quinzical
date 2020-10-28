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


	@Override
	public void dontKnowPressed() {
		question.setState(Question.QuestionState.INCORRECT);
		question.setValue(0);
		showDialog();
	}

	private void showDialog() {
		dialog.show(question);
		timer.cancel();
	}

	protected void finishedSpeaking() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (remaining <= 0) {
					Platform.runLater(new Task<Void>() {
						@Override
						protected Void call() {
							dontKnowPressed();
							return null;
						}
					});
				} else {
					Platform.runLater(new Task<Void>() {
						@Override
						protected Void call() {
							refreshTimerLabel();
							return null;
						}
					});
				}
				remaining--;
			}
		}, 0, 1000);
	}

	// Get current reward based on timer value
	private int getRewardNow() {
		return (int) (question.value() * (float) remaining / TIME_LIMIT);
	}

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

	private void refreshTimerLabel() {
		timerLabel.setText(String.format("⏱ %ds - $%d", remaining, getRewardNow()));
	}
}
