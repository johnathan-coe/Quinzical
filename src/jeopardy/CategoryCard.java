package jeopardy;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A card on the select screen that lets users choose a question from their category
 */
public class CategoryCard {
	private final Category category;
	private final Pane pane;

	@FXML private Label category_name;
	@FXML private VBox questions;

	public CategoryCard(Category category, Game game) throws IOException {
		this.category = category;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/question-card.fxml"));
		loader.setController(this);
		pane = loader.load();

		category_name.setText(category.name());
		List<Integer> valueList = new ArrayList<>();
		for (Question q: category.questions()) {
			valueList.add(q.value());
		}
		boolean lowestScore = false;
		valueList.sort(Integer::compareTo);
		for (Integer val: valueList) {
			Question q = category.getQuestion(val);
			if (!q.isCompleted()) {
				Button button = new Button(Integer.toString(q.value()));
				button.setDisable(lowestScore);
				button.setStyle("-fx-font-size: 15");
				button.setOnAction(new EventHandler<>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						Question question = category.getQuestion(val);
						game.questionPage().show(question);
					}
				});
				questions.getChildren().add(button);
				lowestScore = true;
			} else {
				Label label = new Label(Integer.toString(q.value()));
				label.setTextFill((q.state() == Question.QuestionState.CORRECT)? Color.GREEN: Color.RED);
				label.setStyle("-fx-font-size: 15");
				questions.getChildren().add(label);
			}
		}
	}

	public Pane pane() { return pane; }

}
