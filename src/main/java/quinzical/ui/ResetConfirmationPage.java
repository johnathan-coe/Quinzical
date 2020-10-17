package quinzical.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;

import java.io.IOException;

/**
 * The screen users see to confirm a game reset
 */
public class ResetConfirmationPage {
	private Scene scene;
	private Stage stage;
	private StartPage startScene;
	private Game game;

	public ResetConfirmationPage(Stage stage, Game game, StartPage startScene) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/reset-confirmation.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);
		this.stage = stage;
		this.startScene = startScene;
		this.game = game;
	}

	public void show() {
		stage.setScene(scene);
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
