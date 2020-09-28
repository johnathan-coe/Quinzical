package quinzical;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The screen that users see once they've answered a question
 *
 * This screen can represent a correct or incorrect answer
 */
public class ResultDialog {
	private Scene scene;
	private Stage stage;
	private SelectPage selectPage;
	private Festival festival;

	@FXML private Label outcome;
	@FXML private Label consequence;

	public ResultDialog(Stage stage, SelectPage selectPage, Festival festival) throws IOException {
		this.stage = stage;
		this.selectPage = selectPage;
		this.festival = festival;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/result.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);
	}

	public void show(Question q) {
		boolean correct = (q.state() == Question.QuestionState.CORRECT);
		String message = (correct) ? "Correct!": "Incorrect!";
		outcome.setText(message);
		outcome.setStyle(String.format("-fx-text-fill: %s", (correct) ? "#35f523": "#ee2121"));
		
		if (q.value() == 0) { // Practice questions have a value of 0
			if (correct) { consequence.setText(""); }
			else {
				consequence.setText(capitalise(q.question()) + "\n\n" +
									"The correct answer was: " + capitalise(q.answer()));
			}
		} else {
			consequence.setText((correct) ? 
					String.format("You've earned $%d!", q.value())
					: String.format("The correct answer was '%s'.", capitalise(q.answer())));
		}
		
		stage.setScene(scene);
		festival.say(message);
	}

	@FXML public void okPressed() {
		selectPage.show();
	}
	
	/**
	 * Capitalise the first letter in a string
	 */
	protected String capitalise(String input) {
		return input.substring(0, 1).toUpperCase() +
				input.substring(1);
	}
}
