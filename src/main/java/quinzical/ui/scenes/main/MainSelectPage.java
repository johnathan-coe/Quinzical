package quinzical.ui.scenes.main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Category;
import quinzical.ui.components.CategoryCard;
import quinzical.ui.scenes.SelectPage;

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
		// Set the score
		score.setText("$" + game.data().score());
		
		// Shallow clone the category list
		List<Category> categories = new ArrayList<Category>(game.data().categories());
		
		// Count the number of completed categories
		int completedCount = 0;
		for (Category category: categories) {
			completedCount += category.isCompleted() ? 1 : 0;
		}
		
		// Add the international category if appropriate
		Category internationalCategory = game.data().internationalCategory();
		if (internationalCategory != null && completedCount >= 2) {
			categories.add(internationalCategory);
		}
		
		List<Pane> cards = new ArrayList<>();
		List<Pane> completed = new ArrayList<>();
		
		// Place a card for each category on the scene
		for (Category category: categories) {
			try {
				// Create a card
				CategoryCard card = new CategoryCard(category, game);
				// Place in the appropriate section
				(category.isCompleted() ? completed : cards).add(card.pane());
			} catch (IOException e) {
				System.err.println(e.toString());
			}
		}

		// If the "incomplete" section is empty
		if (cards.size() == 0) {
			Label label = new Label(
					"There doesn't seem to be any categories here!\n"
							+ "Maybe check to see if you have a 'categories' folder?\n"
							+ "Make sure to reset the game once you've added a categories folder."
			);
			container.getChildren().add(label);
		}
		
		container.getChildren().clear();
		container.getChildren().addAll(cards);
		container.getChildren().addAll(completed);
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
