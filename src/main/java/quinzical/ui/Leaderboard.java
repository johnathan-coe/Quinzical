package quinzical.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;

import java.io.IOException;

/**
 * The page users get sent to once they've finished all the questions in the game
 */
public class Leaderboard {
	private final Game game;
	private final Stage stage;
	private final Scene scene;

	@FXML private Label winningsLabel;
	@FXML private VBox leaderList;

	public Leaderboard(Game game, Stage stage) throws IOException {
		this.game = game;
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/rewards.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);
	}

	public void show() {
		winningsLabel.setText(String.format("Earnings: $%d", game.data().score()));
		
		// Clear old scores
		leaderList.getChildren().clear();
		
		// Populate with new ones
		for (int i : game.data().leaders().leaders()) {
			Label score = new Label("$" + Integer.toString(i));
			score.setStyle("-fx-text-fill: white;");
			leaderList.getChildren().add(score);
		}
		
		stage.setScene(scene);
	}

	@FXML public void onMainMenuPressed() {
		game.startPage().show();
	}
}
