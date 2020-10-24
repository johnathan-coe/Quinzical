package quinzical.ui.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.GameData;
import quinzical.data.GameDataListener;

import java.io.IOException;

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

	private void refresh() {
		speedValueLabel.setText(String.format("%.2fx", currentSpeed));
		speedSlider.setValue(currentSpeed);
	}
	
	@FXML
	private void helpPressed() {
		game.helpViewer().show("./wiki/docs/usage.md", "Settings");
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

	@Override
	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		if (event == GameData.GameDataChangedEvent.LOADED) {
			currentSpeed = game.data().settings().speed();
			refresh();
		}
	}
}
