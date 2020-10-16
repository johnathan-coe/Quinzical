package quinzical.ui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Question;
import quinzical.festival.Festival;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainQuestionPage extends QuestionPage {
	private Timer timer;
	private final static int TIME_LIMIT = 30; // 30 seconds

	public MainQuestionPage(Game game, Stage stage, Festival festival) throws IOException {
		super(game, stage, festival, game.selectPage());

		// Remove the question text
		((VBox) super.practiceContainer.getParent()).getChildren().remove(super.practiceContainer);
	}

	@Override
	public void answerSubmitted() {
		boolean correct = question.check(guess.getText());
		
		question.setState((correct)? Question.QuestionState.CORRECT: Question.QuestionState.INCORRECT);
		int score = game.data().score() + (question.value() * ((correct)? 1: 0));
		game.data().setScore(score);
		showDialog();
	}


	@Override
	public void dontKnowPressed() {
		question.setState(Question.QuestionState.INCORRECT);
		showDialog();
	}

	private void showDialog() {
		dialog.show(question);
		timer.cancel();
	}

	@Override
	public void show(Question question, String cat) {
		super.show(question, cat);
		question.setState(Question.QuestionState.INCORRECT);

		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int n = TIME_LIMIT;

			@Override
			public void run() {
				if (n <= 0) {
					Platform.runLater(new Task<Void>() {
						@Override
						protected Void call() {
							dontKnowPressed();
							return null;
						}
					});
				}
				n--;
			}
		}, 0, 1000);
	}
}
