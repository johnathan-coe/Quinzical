package quinzical.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.GameData;
import quinzical.data.GameDataListener;

import java.io.IOException;

/**
 * The screen that users use to select a category and question
 */
public abstract class SelectPage extends Page implements GameDataListener {
	@FXML protected Pane container;
	@FXML protected Label score;

	public SelectPage(Game game, Stage stage) throws IOException {
		super(game, stage, "/fxml/select.fxml");
		game.data().addListener(this);
		refresh();
	}

	@Override
	public void show() {
		refresh();
		super.show();
	}

	/**
	 * Fill the contents of the Select Page.
	 *
	 * Note: Remember to clear the contents first!
	 */
	protected abstract void refresh();

	@Override
	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		if (event == GameData.GameDataChangedEvent.LOADED) { // Only refresh if a whole new game has been loaded
			// Populate the categories
			refresh();
		}
	}

	@FXML public void backPressed() {
		game.startPage().show();
	}
}
