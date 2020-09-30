package quinzical.ui;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Question;
import quinzical.festival.Festival;

import java.io.IOException;

public class MainQuestionPage extends QuestionPage {
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
		dialog.show(question);
	}


	@Override
	public void dontKnowPressed() {
		question.setState(Question.QuestionState.INCORRECT);
		dialog.show(question);
	}

	@Override
	public void show(Question question, String cat) {
		super.show(question, cat);
		question.setState(Question.QuestionState.INCORRECT);
	}
}
