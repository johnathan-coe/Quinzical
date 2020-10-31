package quinzical.ui.scenes.meta;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.GameData;
import quinzical.data.GameDataListener;
import quinzical.ui.scenes.Page;

import java.io.IOException;

/**
 * The start screen that is the first screen users see
 */
public class StartPage extends Page implements GameDataListener {
	private final ResetConfirmationPage resetConfirmation;

	@FXML private Label currentScoreLabel;
	@FXML private Button playButton;
	@FXML private Button practiceButton;
	@FXML private Button resetButton;

	public StartPage(Game game, Stage stage) throws IOException {
		super(game, stage, "/fxml/start.fxml");

		game.data().addListener(this);
		refresh();

		resetConfirmation = new ResetConfirmationPage(stage, game, this);
	}

	// Redirect user to the appropriate scene on a button press
	@FXML public void playPressed() { game.selectPage().show(); }
	@FXML public void practicePressed() { game.practiceSelectPage().show();	}
	@FXML public void resetPressed() { resetConfirmation.show(); }
	@FXML public void settingsPressed() { game.settingsPage().show(); }
	@FXML public void leaderboardPressed() { game.leaderboard().show();	}

	/**
	 * Display a help dialog
	 */
	@FXML public void helpPressed() {
		game.helpViewer().show("./wiki/docs/usage.md", "Start Screen");
	}
	
	/**
	 * Disable pages that depend on GameData until
	 * the JSON is loaded in.
	 */
	public void refresh() {
		String message;
		if (game.data().isLoading()) {
			message = "Loading save file...";
			playButton.setDisable(true);
			practiceButton.setDisable(true);
			resetButton.setDisable(true);
		} else {
			message = String.format("You currently have $%d", game.data().score());
			playButton.setDisable(false);
			practiceButton.setDisable(false);
			resetButton.setDisable(false);
		}
		currentScoreLabel.setText(message);
	}

	/**
	 * When the game is loaded, refresh
	 */
	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		refresh();
	}
}
