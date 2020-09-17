package jeopardy;

import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The page that asks the user the question
 *
 * Is also in charge of handling what happens if they get it right or wrong.
 */
public class PracticeQuestionPage extends QuestionPage {
	private int guesses;
	
	public PracticeQuestionPage(Game game, Stage stage, Festival festival) throws IOException {		
		super(game, stage, festival);
		dialog = new ResultDialog(stage, game.practiceSelectPage(), festival);
	}

	public void show(Question question) {
		super.show(question);
		guesses = 3;
	}

	@Override
	@FXML public void answerSubmitted() {
		// Get user guess
		String guessedVal = guess.getText().strip().toLowerCase();
		// Record guess
		guesses--;
		
		// Check answer
		boolean correct = guessedVal.equals(question.answer());
		
		if (correct) {
			// Show correct dialog
			dialog.show(correct, 0, question.answer());
		} else if (guesses == 0) {
			// Show incorrect dialog
			dialog.show(correct, 0, question.answer());
		} else if (guesses == 1){
			// Give first letter as hint
			guess.setText(question.answer().substring(0, 1));
		}
		
		questionText.setText(question.question() + "\n\n" + Integer.toString(guesses) + " Guesses Left");
	}
	
	@Override
	@FXML public void dontKnowPressed() {}
}
