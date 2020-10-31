package quinzical.ui.scenes.meta;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.ui.scenes.Page;

import java.io.IOException;

/**
 * Page displaying the urrent state of the leaderboard
 */
public class Leaderboard extends Page {
	@FXML private Label winningsLabel;
	@FXML private VBox leaderList;
	@FXML private Label trophy;

	public Leaderboard(Game game, Stage stage) throws IOException {
		super(game, stage, "/fxml/rewards.fxml");
	}

	/**
	 * Show this page based on current data
	 */
	@Override
	public void show() {
		// Update the score 
		int score = game.data().score();
		winningsLabel.setText(String.format("$%d", score));
		
		// Grab and apply the trophy colour
		String trophyColour = quinzical.data.Leaderboard.trophyColour(score);
		trophy.setStyle("-fx-text-fill: " + trophyColour + ";");
		
		// Clear old scores
		leaderList.getChildren().clear();
		
		// Populate with new ones
		for (int i : game.data().leaders().leaders()) {
			Label scoreLabel = new Label("$" + Integer.toString(i));
			scoreLabel.setStyle("-fx-text-fill: white;");
			leaderList.getChildren().add(scoreLabel);
		}
		
		super.show();
	}

	@FXML public void onMainMenuPressed() {
		game.startPage().show();
	}
}
