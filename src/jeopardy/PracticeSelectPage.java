package jeopardy;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PracticeSelectPage extends SelectPage {
	@FXML Label header;
	
	public PracticeSelectPage(Game game, Stage stage) throws IOException {
		super(game, stage);
		
		// Alter the default text
		header.setText("Select a category");
	}
	
	/**
	 * Clear and populate the page with category buttons
	 */
	@Override
	protected void refresh() {
		// Delete old buttons
		container.getChildren().clear();
		
		for (Category cat : game.data().categories()) {
			// Button for the category
			Button b = new Button(cat.name());
			
			// When the button is pressed, show a question from the category
			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					showQuestion(cat);
				}
			});
			
			// Fill width
			b.setPrefWidth(200.0);
			container.getChildren().add(b);
		}
	}
	
	/**
	 * Display a randomly selected question from the chosen category 
	 * 
	 * @param c Category object
	 */
	void showQuestion(Category c) {
		Random rand = new Random();
		
		// Get all questions in the category
		List<Question> questions = c.questions();
		
		// Choose a random question
		Question q = questions.get(rand.nextInt(questions.size()));
		
		// Display
		game.practiceQuestionPage().show(q);
	}
}
