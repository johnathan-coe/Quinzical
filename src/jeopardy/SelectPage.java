package jeopardy;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The screen that users use to select a category and question
 */
public class SelectPage implements GameDataListener {
	private final Game game;
	private final Scene scene;
	private final Stage stage;

	@FXML Pane container;

	public SelectPage(Game game, Stage stage) throws IOException {
		this.stage = stage;
		this.game = game;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/select.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		scene = new Scene(pane);
		game.data().addListener(this);

		refresh();
	}

	public void show() {
		refresh();
		stage.setScene(scene);
	}

	private void refresh() {
		List<Pane> cards = new ArrayList<>();
		for (Category category: game.data().categories()) {
			try {
				CategoryCard card = new CategoryCard(category, game);
				cards.add(card.pane());
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}
		container.getChildren().clear();
		if (cards.size() == 0) {
			Label label = new Label(
				"There doesn't seem to be any categories here!\n"
					+ "Maybe check to see if you have a 'categories' folder?\n"
					+ "Make sure to reset the game once you've added a categories folder."
			);
			container.getChildren().add(label);
		}
		container.getChildren().addAll(cards);
	}

	@Override
	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		if (event == GameData.GameDataChangedEvent.LOADED) { // Only refresh if a whole new game has been loaded
			refresh();
		}
	}

	@FXML public void backPressed() {
		game.startPage().show();
	}
}
