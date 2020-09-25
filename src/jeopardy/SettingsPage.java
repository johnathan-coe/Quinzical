package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsPage {
	private final Game game;
	private final Stage stage;
	private final Scene scene;

	private float currentSpeed = 1;

	@FXML private Slider speedSlider;
	@FXML private Label speedValueLabel;
	@FXML private Label speedLabel;

	public SettingsPage(Game game, Stage stage) throws IOException {
		this.game = game;
		this.stage = stage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/settings.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);

		refresh();
		speedLabel.setText(String.format("Set Speed (Default %.2fx)", 1.0));
	}

	public void show() {
		stage.setScene(scene);
	}

	private void refresh() {
		speedValueLabel.setText(String.format("%.2fx", currentSpeed));
		speedSlider.setValue(currentSpeed);
	}

	@FXML private void backButtonPressed() {
		game.startPage().show();
		game.festival().setSpeed(currentSpeed);
	}

	@FXML private void speedSelected() {
		currentSpeed = (float)speedSlider.getValue();
		refresh();
	}

	@FXML private void speedDefaultPressed() {
		currentSpeed = 1;
		refresh();
	}
}
