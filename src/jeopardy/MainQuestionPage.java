package jeopardy;

import javafx.stage.Stage;

import java.io.IOException;

public class MainQuestionPage extends QuestionPage {
	public MainQuestionPage(Game game, Stage stage, Festival festival) throws IOException {
		super(game, stage, festival);
	}

	@Override
	public void answerSubmitted() {
		String guessedVal = guess.getText().strip().toLowerCase();
		boolean correct = guessedVal.equals(question.answer());
		question.setState((correct)? Question.QuestionState.CORRECT: Question.QuestionState.INCORRECT);
		int score = game.data().score() + (question.value() * ((correct)? 1: 0));
		game.data().setScore(score);
		dialog.show(correct, question.value(), question.answer());
	}


	@Override
	public void dontKnowPressed() {
		question.setState(Question.QuestionState.INCORRECT);
		dialog.show(false, question.value(), question.answer());
	}
}
