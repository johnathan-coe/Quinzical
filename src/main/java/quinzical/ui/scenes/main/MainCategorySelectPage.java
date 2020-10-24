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

public class MainCategorySelectPage extends SelectPage {
	private final Set<SelectCard> cards_ = new HashSet<>();
	private Set<SelectCard> selected = new HashSet<>();
	private final MainSelectPage mainSelectPage;

	@FXML private Label header;
	@FXML private ButtonBar bottomBar;

	public MainCategorySelectPage(Game game, Stage stage, MainSelectPage mainSelectPage) throws IOException {
		super(game, stage);
		this.mainSelectPage = mainSelectPage;


		// Alter the default text
		score.setText("Choose 5 categories or press the random button to randomly select some.");
		score.setStyle("-fx-text-fill: white; -fx-font-size: 15;");

		Button randomButton = new Button("Random");
		randomButton.getStyleClass().add("normal-button");
		randomButton.setStyle("-fx-font-size: 16px");
		bottomBar.getButtons().add(0, randomButton);
		randomButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Set<SelectCard> unselectedCards = new HashSet<>(cards_);
				unselectedCards.removeAll(selected);
				List<SelectCard> unselected = new ArrayList<>(unselectedCards);

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

		container.getChildren().clear();
		cards_.clear();

		CategoryParser parser = this.game.data().parser();
		if (parser == null) {
			return;
		}

		for (String cat: parser.categories()) {
			try {
				SelectCard card = new SelectCard(cat);
				Pane pane = card.pane();
				container.getChildren().add(pane);
				cards_.add(card);
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

	private void select(SelectCard card) {
		card.setSelected(true);
		selected.add(card);
	}

	private void showMainSelectPage() {
		for (SelectCard card: selected) {
			String title = card.getTitle();
			Map<String, String[]> categoryMap = game.data().parser().getCategory(title);
			Category category = new Category(title);
			List<Map.Entry<String, String[]>> entryList = new ArrayList<>(categoryMap.entrySet());
			for (int i = 0; i < 5; i++) {
				Map.Entry<String, String[]> entry = entryList.get((int) (Math.random() * entryList.size()));
				Question question = new Question((i+1) * 100, entry.getValue()[0], entry.getKey(), entry.getValue()[1], Question.QuestionState.UNATTEMPTED);
				category.addQuestion(question);
				entryList.remove(entry);
			}
			game.data().categories().add(category);
		}
		mainSelectPage.show();
	}

	private void refreshHeader() {
		header.setText(String.format("Choose 5 Categories - %d/5 Chosen", selected.size()));
	}

	@FXML private void helpPressed() {
		// TODO: 18/10/20  
	}

	private class SelectCard extends Card {
		private boolean selected = false;

		public SelectCard(String title) throws IOException {
			super(title);
		}

		public boolean isSelected() { return selected; }

		public void setSelected(boolean selected) {
			this.selected = selected;
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
