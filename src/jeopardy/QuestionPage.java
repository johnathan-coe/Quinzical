package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class QuestionPage {
	private Question question;
	private Game game;
	private Stage stage;
	private Scene scene;
	private ResultDialog dialog;
	private Festival festival;

	@FXML private Label questionText;
	@FXML private TextField guess;

	public QuestionPage(Game game, Stage stage, Festival festival) throws IOException {
		this.game = game;
		this.stage = stage;
		this.festival = festival;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/question-page.fxml"));
		loader.setController(this);
		Pane pane = loader.load();

		scene = new Scene(pane);
		dialog = new ResultDialog(stage, game.selectPage(), festival);
	}

	public void show(Question question) {
		this.question = question;
		questionText.setText(question.question());
		guess.setText("");
		stage.setScene(scene);
		festival.say(question.question());
	}

	@FXML public void answerSubmitted() {
		String guessedVal = guess.getText().strip().toLowerCase();
		question.complete();
		boolean correct = guessedVal.equals(question.answer());
		int score = game.data().score() + (question.value() * ((correct)? 1: -1));
		game.data().setScore(score);
		dialog.show(correct, question.value(), question.answer());
	}
}