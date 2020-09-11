package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class StartPage implements GameDataListener {
	private final Game game;
	private final Stage stage;
	private final Scene scene;
	private final ResetConfirmationPage resetConfirmation;

	@FXML private Label currentScoreLabel;
	@FXML private Button playButton;
	@FXML private Button resetButton;

	public StartPage(Game game, Stage stage) throws IOException {
		this.game = game;
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/start.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);
		game.data().addListener(this);
		refresh();

		resetConfirmation = new ResetConfirmationPage(stage, game, this);
	}

	@FXML
	public void playPressed() {
		game.selectPage().show();
	}

	@FXML
	public void resetPressed() {
		resetConfirmation.show();
	}

	public void refresh() {
		String message;
		if (game.data().isLoading()) {
			message = "Loading save file...";
			playButton.setDisable(true);
			resetButton.setDisable(true);
		} else {
			message = String.format("You currently have $%d", game.data().score());
			playButton.setDisable(false);
			resetButton.setDisable(false);
		}
		currentScoreLabel.setText(message);
	}

	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		refresh();
	}

	public void show() {
		stage.setScene(scene);
	}
}
