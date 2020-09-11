package jeopardy;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ResetConfirmationPage {
	private Scene scene;
	private Stage stage;
	private StartPage startScene;
	private Game game;

	public ResetConfirmationPage(Stage stage, Game game, StartPage startScene) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/reset-confirmation.fxml"));
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

	@FXML public void onConfirm() {
		game.data().setLoaded(false);
		startScene.show();
		Task<GameData> task = GameData.freshLoad();
		task.setOnSucceeded(new EventHandler<>() {
			@Override
			public void handle(WorkerStateEvent workerStateEvent) {
				game.data().set(task.getValue());
			}
		});
	}

	@FXML public void onCancel() {
		startScene.show();
	}
}
