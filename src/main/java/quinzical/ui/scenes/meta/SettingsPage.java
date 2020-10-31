package quinzical.ui.scenes.meta;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.GameData;
import quinzical.data.GameDataListener;
import quinzical.ui.scenes.Page;

import java.io.IOException;

/**
 * Page that allows users to adjust the game settings
 */
public class SettingsPage extends Page implements GameDataListener {
	private float currentSpeed = 1;

	@FXML private Slider speedSlider;
	@FXML private Label speedValueLabel;
	@FXML private Label speedLabel;

	public SettingsPage(Game game, Stage stage) throws IOException {
		super(game, stage, "/fxml/settings.fxml");

		game.data().addListener(this);
		refresh();
		speedLabel.setText(String.format("Set Speed (Default %.2fx)", 1.0));
	}

	/**
	 * Update page with current speed value
	 */
	private void refresh() {
		speedValueLabel.setText(String.format("%.2fx", currentSpeed));
		speedSlider.setValue(currentSpeed);
	}
	
	/**
	 * Open a help dialog
	 */
	@FXML
	private void helpPressed() {
		game.helpViewer().show("./wiki/docs/usage.md", "Settings");
	}

	/**
	 * When we go back, apply the current speed
	 */
	@FXML private void backButtonPressed() {
		game.startPage().show();
		game.festival().setSpeed(currentSpeed);
	}

	/**
	 * Update speed on selection
	 */
	@FXML private void speedSelected() {
		currentSpeed = (float)speedSlider.getValue();
		refresh();
	}

	/**
	 * Reset to default speed value
	 */
	@FXML private void speedDefaultPressed() {
		currentSpeed = 1;
		refresh();
	}

	/**
	 * When a new game is loaded, pull speed from game data
	 */
	@Override
	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		if (event == GameData.GameDataChangedEvent.LOADED) {
			currentSpeed = game.data().settings().speed();
			refresh();
		}
	}
}
