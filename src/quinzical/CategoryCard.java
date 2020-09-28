package quinzical;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * A card on the select screen that lets users choose a question from their category
 */
public class CategoryCard extends Card {
	@FXML private VBox questions;

	public CategoryCard(Category category, Game game) throws IOException {
		super(category.name());
		
		// Sort questions by value
		List<Question> qs = category.questions();
		Collections.sort(qs);

		boolean firstButton = true;
		for (Question q : qs) {
			if (!q.isCompleted()) {
				Button button = new Button(Integer.toString(q.value()));
				// Enable the first unattempted question
				button.setDisable(!firstButton);
				firstButton = false;
				
				button.setOnAction(new EventHandler<>() {
					@Override
					public void handle(ActionEvent actionEvent) {
						game.questionPage().show(q);
					}
				});
				
				button.setStyle("-fx-font-size: 15");
				questions.getChildren().add(button);
			} else {
				// Attempted questions get a label
				Label label = new Label(Integer.toString(q.value()));
				
				String bg = (q.state() == Question.QuestionState.CORRECT)? "#30e60b" : "#ff0039";
				String fg = (q.state() == Question.QuestionState.CORRECT)? "#38383d" : "#ffffff";
				
				label.setTextFill(Color.web(fg));
				label.setStyle("-fx-font-size: 15;" +
							   "-fx-background-color: " + bg + "bb;" +
						   "-fx-padding: 5;");
		
				questions.getChildren().add(label);
			}
		}
	}
}
