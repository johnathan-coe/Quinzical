package quinzical.ui.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Question;
import quinzical.festival.Festival;

import java.io.IOException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.capitalize;

/**
 * The page that asks the user the question
 *
 * Is also in charge of handling what happens if they get it right or wrong.
 */
public abstract class QuestionPage extends Page {
	protected Festival festival;

	protected Question question;
	protected ResultDialog dialog;

	@FXML protected Label questionText;
	@FXML protected VBox practiceContainer;
	@FXML protected TextField guess;
	@FXML protected Label timerLabel;
	@FXML protected Label prompt;
	@FXML protected Label attemptCounter;

	public QuestionPage(Game game, Stage stage, Festival festival, SelectPage selectPage) throws IOException {
		super(game, stage, "/fxml/question-page.fxml");
		
		this.festival = festival;
		dialog = new ResultDialog(game, stage, selectPage, festival);
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
		
		root.setStyle(styleString + "-fx-background-size: cover;");
		
		
		// Now deal with question
		this.question = question;
		questionText.setText(capitalize(question.question()));
		prompt.setText(capitalize(question.prompt()));
		guess.setText("");
		festival.say(question.question());

		super.show();
	}
	
	@FXML public abstract void answerSubmitted();

	@FXML public abstract void dontKnowPressed();

	@FXML public void playCue() {
		game.festival().say(question.question());
	}
}
