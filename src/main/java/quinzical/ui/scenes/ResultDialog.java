package quinzical.ui.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Question;
import quinzical.festival.Festival;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.capitalize;

/**
 * The screen that users see once they've answered a question
 *
 * This screen can represent a correct or incorrect answer
 */
public class ResultDialog extends Page {
	private SelectPage selectPage;
	private Festival festival;

	@FXML private Label outcome;
	@FXML private Label consequence;

	public ResultDialog(Game game, Stage stage, SelectPage selectPage, Festival festival) throws IOException {
		super(game, stage, "/fxml/result.fxml");
		this.selectPage = selectPage;
		this.festival = festival;
	}

	/**
	 * Show this dialog, loading in the outcome
	 * @param q
	 */
	public void show(Question q) {
		// Style dialog depending on whether the user got the question correct
		boolean correct = (q.state() == Question.QuestionState.CORRECT);
		String message = (correct) ? "Correct!": "Incorrect!";
		outcome.setText(message);
		outcome.getStyleClass().removeAll("correct", "incorrect");
		outcome.getStyleClass().add((correct) ? "correct": "incorrect");

		// Display the outcome
		if (q.value() == 0) { // Practice questions have a value of 0
			if (correct) { consequence.setText(""); }
			else {
				consequence.setText(capitalize(q.question()) + "\n\n" +
									"The correct answer was: " + capitalize(q.answer()));
			}
		} else {
			consequence.setText((correct) ? 
					String.format("You've earned $%d!", q.value())
					: String.format("The correct answer was '%s'.", capitalize(q.answer())));
		}
		
		// Run TTS
		festival.say(message);

		show();
	}

	@FXML public void okPressed() {
		selectPage.show();
	}
}
