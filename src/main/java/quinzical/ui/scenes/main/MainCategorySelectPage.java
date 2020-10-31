package quinzical.ui.scenes.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Category;
import quinzical.data.CategoryParser;
import quinzical.data.GameData;
import quinzical.data.Question;
import quinzical.ui.components.Card;
import quinzical.ui.scenes.SelectPage;

import java.io.IOException;
import java.util.*;

/**
 * The page that allows the user to select categories for the main game
 */
public class MainCategorySelectPage extends SelectPage {
	private final Set<SelectCard> cards_ = new HashSet<>();
	private Set<SelectCard> selected = new HashSet<>();
	private final MainSelectPage mainSelectPage;

	@FXML private Label header;
	@FXML private ButtonBar bottomBar;

	public MainCategorySelectPage(Game game, Stage stage, MainSelectPage mainSelectPage) throws IOException {
		super(game, stage);
		this.mainSelectPage = mainSelectPage;

		// Alter and style the default text for the main game
		score.setText("Choose 5 categories or randomly select some.");
		score.setStyle("-fx-text-fill: white; -fx-font-size: 15;");

		// Add a "random" button that selects the remaining categories for the user 
		Button randomButton = new Button("Random");
		randomButton.getStyleClass().add("normal-button");
		randomButton.setStyle("-fx-font-size: 16px");
		bottomBar.getButtons().add(0, randomButton);
		
		// When it's clicked
		randomButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Set<SelectCard> unselectedCards = new HashSet<>(cards_);
				unselectedCards.removeAll(selected);
				List<SelectCard> unselected = new ArrayList<>(unselectedCards);

				// Select and add a random category until we have 5
				while (5 - selected.size() > 0) {
					int index = (int)(Math.random() * unselected.size());
					SelectCard card = unselected.get(index);
					select(card);
					unselected.remove(card);
				}

				showMainSelectPage();
			}
		});
	}

	/**
	 * Fill the contents of the Select Page.
	 * <p>
	 * Note: Remember to clear the contents first!
	 */
	@Override
	protected void refresh() {
		if (cards_ == null) {
			return;
		}

		// Clear old data
		container.getChildren().clear();
		cards_.clear();

		CategoryParser parser = this.game.data().parser();
		if (parser == null) {
			return;
		}

		for (String cat: parser.categories()) {
			try {
				// Create a card for each category
				SelectCard card = new SelectCard(cat);
				Pane pane = card.pane();
				container.getChildren().add(pane);
				cards_.add(card);
				
				// When the card is clicked, toggle its selection
				pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent mouseEvent) {
						if (card.isSelected()) {
							card.setSelected(false);
							selected.remove(card);
							refreshHeader();
						} else if (selected.size() < 5) {
							select(card);
							refreshHeader();
							// When we've selected 5 categories
							if (selected.size() == 5) {
								showMainSelectPage();
							}
						}
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		refreshHeader();
	}

	/**
	 * Function to select a card
	 * @param card
	 */
	private void select(SelectCard card) {
		card.setSelected(true);
		selected.add(card);
	}

	/**
	 * Show main page, loading in selected categories
	 */
	private void showMainSelectPage() {
		// For each selected card
		for (SelectCard card: selected) {
			String title = card.getTitle();
			
			Map<String, String[]> categoryMap = game.data().parser().getCategory(title);
			List<Map.Entry<String, String[]>> entryList = new ArrayList<>(categoryMap.entrySet());
			
			// Build category
			Category category = new Category(title);
			for (int i = 0; i < 5; i++) {
				// Pick a random question
				Map.Entry<String, String[]> entry = entryList.get((int) (Math.random() * entryList.size()));
				// Assign a random value adn build a Question
				Question question = new Question((i+1) * 100, entry.getValue()[0], entry.getKey(), entry.getValue()[1], Question.QuestionState.UNATTEMPTED);
				category.addQuestion(question);
				
				entryList.remove(entry);
			}
			// Add category to game data
			game.data().categories().add(category);
		}
		mainSelectPage.show();
	}
	
	/**
	 * Refresh header displaying selection status when necessary
	 */
	private void refreshHeader() {
		header.setText(String.format("Choose 5 Categories - %d/5 Chosen", selected.size()));
	}

	@FXML private void helpPressed() {
		// TODO: 18/10/20  
	}

	/**
	 * Card representing a selectable category.
	 */
	private class SelectCard extends Card {
		private boolean selected = false;

		public SelectCard(String title) throws IOException {
			super(title);
		}

		public boolean isSelected() { return selected; }

		public void setSelected(boolean selected) {
			this.selected = selected;
			
			// Changing background on select
			if (selected) {
				pane().getChildren().get(0).setStyle("-fx-background-color: green; -fx-background-radius: 10");
			} else {
				pane().getChildren().get(0).setStyle("-fx-background-color: #38383d; -fx-background-radius: 10");
			}
		}

		public String getTitle() {
			return title;
		}
	}

	/**
	 * When we load a new game, set all cards to be unselected
	 */
	@Override
	public void handleGameDataChanged(GameData.GameDataChangedEvent event) {
		super.handleGameDataChanged(event);
		if (event == GameData.GameDataChangedEvent.LOADED) {
			for (SelectCard card: selected) {
				card.setSelected(false);
			}
			selected.clear();
		}
	}
}
