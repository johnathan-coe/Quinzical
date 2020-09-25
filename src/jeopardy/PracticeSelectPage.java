package jeopardy;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PracticeSelectPage extends SelectPage {
	private CategoryParser parser;
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
		// Delete old cards
		container.getChildren().clear();

		// Grab the parser
	    parser = game.data().parser();
		if (parser == null) { return; }
		
		for (String cat : parser.categories()) {
			try {
				Pane p = new Card(cat).pane();
				p.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
							showQuestion(cat);
					}
				});
				container.getChildren().add(p);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Display a randomly selected question from the chosen category
	 *
	 * @param c Category object
	 */
	private void showQuestion(String c) {
		Random rand = new Random();
	
		// Get all questions in the category
		Map<String, String> questions = parser.getCategory(c);
		// Select a random question
		Object[] qArray = questions.keySet().toArray();
		String randomQ = (String) qArray[rand.nextInt(qArray.length)];
		
		// Create a question object
		Question q = new Question(0, randomQ, questions.get(randomQ), Question.QuestionState.UNATTEMPTED);
		
		// Display
		game.practiceQuestionPage().show(q);
	}
}
