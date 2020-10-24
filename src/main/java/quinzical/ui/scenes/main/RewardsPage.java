package quinzical.ui.scenes.main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.ui.scenes.Leaderboard;

import java.io.IOException;

/**
 * The page users get sent to once they've finished all the questions in the game
 */
public class RewardsPage extends Leaderboard {
	private final Game game;

	@FXML private Label winningsLabel;
	@FXML private Label title;
	@FXML private Label trophyText;
	
	public RewardsPage(Game game, Stage stage) throws IOException {
		super(game, stage);
		title.setText("Game Over!");
		trophyText.setText("Trophy Awarded");
		this.game = game;
	}

	@Override
	public void show() {
		// Add score to the leaderboard
		game.data().leaders().add(game.data().score());
		super.show();
		
		winningsLabel.setText(String.format("You've earned $%d", game.data().score()));
		
		// Reset the game
		game.data().reset();
	}
}
