package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * The page users get sent to once they've finished all the questions in the game
 */
public class RewardsPage {
	private final Game game;
	private final Stage stage;
	private final Scene scene;

	@FXML private Label winningsLabel;

	public RewardsPage(Game game, Stage stage) throws IOException {
		this.game = game;
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/rewards.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);
	}

	public void show() {
		winningsLabel.setText(String.format("You've earned $%d", game.data().score()));
		game.data().reset();
		stage.setScene(scene);
	}

	@FXML public void onMainMenuPressed() {
		game.startPage().show();
	}
}
