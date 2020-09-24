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
		super(game, stage, festival);
		
		// Hide the "don't know" button in practice mode
		((HBox) dontKnowButton.getParent()).getChildren().remove(dontKnowButton);
		
		// Replace the result dialog with one that returns to the practice page
		dialog = new ResultDialog(stage, game.practiceSelectPage(), festival);
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
	
	/**
	 * Called when the "don't know" button is pressed 
	 */
	@Override
	@FXML public void dontKnowPressed() {}
}
