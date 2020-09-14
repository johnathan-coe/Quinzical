package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

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

	public void show(boolean correct, int value, String actualAnswer) {
		String message = (correct)? "Correct!": "Incorrect!";
		outcome.setText(message);
		outcome.setStyle(String.format("-fx-text-fill: %s", (correct)? "#35f523": "#ee2121"));
		consequence.setText((correct)? String.format("You've earned $%d!", value): String.format("The correct answer was '%s'.\nYou've lost $%d.", actualAnswer, value));
		stage.setScene(scene);
		festival.say(message);
	}

	@FXML public void okPressed() {
		selectPage.show();
	}
}