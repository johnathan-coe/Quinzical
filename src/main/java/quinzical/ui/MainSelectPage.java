package quinzical.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Category;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainSelectPage extends SelectPage {
	private final MainCategorySelectPage categorySelectPage;

	public MainSelectPage(Game game, Stage stage) throws IOException {
		super(game, stage);
		categorySelectPage = new MainCategorySelectPage(game, stage, this);
	}

	protected void refresh() {
		List<Pane> cards = new ArrayList<>();
		List<Pane> completed = new ArrayList<>(); // Put the completed panes at the end
		for (Category category: game.data().categories()) {
			try {
				CategoryCard card = new CategoryCard(category, game);
				if (category.isCompleted()) {
					completed.add(card.pane());
				} else {
					cards.add(card.pane());
				}
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}
		cards.addAll(completed);

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

	@FXML
	private void helpPressed() {
		game.helpViewer().show("./wiki/docs/usage.md", "Main Game");
	}
	
	@Override
	public void show() {
		if (game.data().categories().isEmpty()) {
			categorySelectPage.show();
			return;
		}
		if (game.data().isAllDone()) {
			game.rewardsPage().show();
			return;
		}
		super.show();
	}

}
