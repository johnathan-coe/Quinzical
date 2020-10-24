package quinzical.ui;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import quinzical.Game;

import java.io.IOException;

/**
 * The screen users see to confirm a game reset
 */
public class ResetConfirmationPage extends Page {
	private StartPage startScene;

	public ResetConfirmationPage(Stage stage, Game game, StartPage startScene) throws IOException {
		super(game, stage, "/fxml/reset-confirmation.fxml");
		this.startScene = startScene;
	}

	@FXML
	private void helpPressed() {
		game.helpViewer().show("./wiki/docs/usage.md", "Reset");
	}
	
	@FXML public void onConfirm() {
		game.data().reset();
		startScene.show();
	}

	@FXML public void onCancel() {
		startScene.show();
	}
}
