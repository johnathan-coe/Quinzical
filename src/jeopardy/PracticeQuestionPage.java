package jeopardy;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The page that asks the user practice questions
 *
 * Is also in charge of handling what happens if they get it right or wrong.
 */
public class PracticeQuestionPage extends QuestionPage {
	private int guesses;
	@FXML Button dontKnowButton;
	
	public PracticeQuestionPage(Game game, Stage stage, Festival festival) throws IOException {		
		super(game, stage, festival, game.practiceSelectPage());
		
		// Hide the "don't know" button in practice mode
		((HBox) dontKnowButton.getParent()).getChildren().remove(dontKnowButton);
	}

	/**
	 * Change the question displayed
	 */
	public void show(Question question) {
		super.show(question);
		
		// Reset number of guesses remaining
		guesses = 3;
	}

	/**
	 * Called when the answer button is pressed
	 */
	@Override
	@FXML public void answerSubmitted() {
		// Record guess
		guesses--;
		
		// Check answer
		boolean correct = question.check(guess.getText());
		// Set question state, the question is not a part of the
		// GameData object so this won't be saved
		question.setState((correct) ? Question.QuestionState.CORRECT : Question.QuestionState.INCORRECT);
		
		if (correct) {
			// Show correct dialog
			dialog.show(question);
		} else if (guesses == 0) {
			// Show incorrect dialog
			dialog.show(question);
		} else if (guesses == 1){
			// Give first letter as hint
			guess.setText(question.answer().substring(0, 1));
		}
		
		questionText.setText(capitalise(question.question()) + "\n\n" + Integer.toString(guesses) + " Guesses Left");
	}
	
	/**
	 * Called when the "don't know" button is pressed 
	 */
	@Override
	@FXML public void dontKnowPressed() {}
}
