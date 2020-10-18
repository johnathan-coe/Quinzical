package quinzical.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Question;
import quinzical.festival.Festival;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The page that asks the user the question
 *
 * Is also in charge of handling what happens if they get it right or wrong.
 */
public abstract class QuestionPage {
	protected Game game;
	private Stage stage;
	private Scene scene;
	protected Festival festival;
	private Pane pane;
	
	protected Question question;
	protected ResultDialog dialog;

	@FXML protected Label questionText;
	@FXML protected VBox practiceContainer;
	@FXML protected TextField guess;
	@FXML protected Label timerLabel;
	@FXML protected Label prompt;

	public QuestionPage(Game game, Stage stage, Festival festival, SelectPage selectPage) throws IOException {
		this.game = game;
		this.stage = stage;
		this.festival = festival;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/question-page.fxml"));
		loader.setController(this);
		pane = loader.load();
 
		scene = new Scene(pane);
		dialog = new ResultDialog(stage, selectPage, festival);
	}

	public void show(Question question, String cat) {
		// Set pane background to an image
		String imageUrl = String.format("/images/%s.jpg", cat.replace(" ", "_"));
		URL url = getClass().getResource(imageUrl);
		
		String styleString;
		if (url == null) {
			// Grey background if an image is not found
			styleString = "-fx-background-color: grey;";
		} else {
			styleString = String.format("-fx-background-image: url('%s');", url.toExternalForm());
		}
		
		pane.setStyle(styleString + "-fx-background-size: cover;");
		
		
		// Now deal with question
		this.question = question;
		questionText.setText(capitalise(question.question()));
		prompt.setText(capitalise(question.prompt()));
		guess.setText("");
		stage.setScene(scene);
		festival.say(question.question());
	}

	/**
	 * Capitalise the first letter in a string
	 */
	private String capitalise(String input) {
		return input.substring(0, 1).toUpperCase() +
				input.substring(1);
	}
	
	@FXML public abstract void answerSubmitted();

	@FXML public abstract void dontKnowPressed();

	@FXML public void playCue() {
		game.festival().say(question.question());
	}
}