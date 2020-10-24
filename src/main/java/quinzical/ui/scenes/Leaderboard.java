package quinzical.ui.scenes;

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
	@FXML private Label trophy;

	public Leaderboard(Game game, Stage stage) throws IOException {
		super(game, stage, "/fxml/rewards.fxml");
	}

	@Override
	public void show() {
		int score = game.data().score();
		winningsLabel.setText(String.format("$%d", score));
		
		String trophyColour;
		if (score > 5625) {
			trophyColour = "#D4AF37";
		} else if (score > 3750) {
			trophyColour = "#A8A9AD";
		} else if (score > 1875) {
			trophyColour = "#AA7042";
		} else {
			trophyColour = "white";
		}
		
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
