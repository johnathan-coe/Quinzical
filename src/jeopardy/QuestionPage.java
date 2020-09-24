package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The page that asks the user the question
 *
 * Is also in charge of handling what happens if they get it right or wrong.
 */
public abstract class QuestionPage {
	protected Game game;
	private Stage stage;
	private Scene scene;
	private Festival festival;
	
	protected Question question;
	protected ResultDialog dialog;

	@FXML protected Label questionText;
	@FXML protected TextField guess;

	public QuestionPage(Game game, Stage stage, Festival festival, SelectPage selectPage) throws IOException {
		this.game = game;
		this.stage = stage;
		this.festival = festival;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/question-page.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
 
		scene = new Scene(pane);
		dialog = new ResultDialog(stage, selectPage, festival);
	}

	public void show(Question question) {
		this.question = question;
		questionText.setText(capitalise(question.question()));
		guess.setText("");
		stage.setScene(scene);
		festival.say(question.question());
	}

	/**
	 * Capitalise the first letter in a string
	 */
	protected String capitalise(String input) {
		return input.substring(0, 1).toUpperCase() +
				input.substring(1);
	}
	
	@FXML public abstract void answerSubmitted();

	@FXML public abstract void dontKnowPressed();
}
