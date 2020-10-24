package quinzical.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;

import java.io.IOException;

/**
 * The page users get sent to once they've finished all the questions in the game
 */
public class Leaderboard extends Page {
	@FXML private Label winningsLabel;
	@FXML private VBox leaderList;

	public Leaderboard(Game game, Stage stage) throws IOException {
		super(game, stage, "/fxml/rewards.fxml");
	}

	@Override
	public void show() {
		winningsLabel.setText(String.format("$%d", game.data().score()));
		
		// Clear old scores
		leaderList.getChildren().clear();
		
		// Populate with new ones
		for (int i : game.data().leaders().leaders()) {
			Label score = new Label("$" + Integer.toString(i));
			score.setStyle("-fx-text-fill: white;");
			leaderList.getChildren().add(score);
		}
		
		super.show();
	}

	@FXML public void onMainMenuPressed() {
		game.startPage().show();
	}
}
