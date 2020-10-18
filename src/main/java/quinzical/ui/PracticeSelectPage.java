package quinzical.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;
import quinzical.data.Category;
import quinzical.data.CategoryParser;
import quinzical.data.Question;

public class PracticeSelectPage extends SelectPage {
	private CategoryParser parser;
	private Random rand;
	@FXML Label header;
	
	public PracticeSelectPage(Game game, Stage stage) throws IOException {
		super(game, stage);
		
		rand = new Random();
		
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
		
		// Category for questions the user got incorrect
		if (getIncorrect().size() > 0) {
			Pane incorrect;
			
			try { incorrect = new Card("Incorrect").pane();
			} catch (IOException e) { e.printStackTrace(); return; }
			
			incorrect.setOnMouseClicked(
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e) {
							showIncorrect();
					}
				}
			);
			
			container.getChildren().add(incorrect);
		}
		
		// All categories
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
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	@FXML
	private void helpPressed() {
		game.helpViewer().show("./wiki/docs/usage.md", "Practice Mode");
	}
	
	/**
	 * Gets a list of all questions the user got incorrect
	 */
	private List<Question> getIncorrect() {
		List<Question> incorrect = new ArrayList<Question>();
		
		// For all loaded categories
		for (Category cat : game.data().categories()) {
			// For all questions in the cat
			for (Question q : cat.questions()) {
				// If we got it incorrect
				if (q.state() == Question.QuestionState.INCORRECT) {
					// Add to list, cloning as to not alter main game state
					incorrect.add(
						new Question(0, q.prompt(), q.question(), q.answer(), Question.QuestionState.INCORRECT)
					);
				}
			}
		}
		
		return incorrect;
	}
	
	// Random object from collection
	private Object randomElement(Collection<?> l) {
		Object[] arr = l.toArray();
		return arr[rand.nextInt(arr.length)];
	}
	
	/**
	 * Display a randomly selected incorrect question
	 */
	private void showIncorrect() {
		// Get all incorrect questions
		List<Question> incorrect = getIncorrect();
		
		// Select a random question
		Question randomQ = (Question) randomElement(incorrect);
		
		// Display
		game.practiceQuestionPage().show(randomQ, "Incorrect");
	}
	
	/**
	 * Display a randomly selected question from the chosen category
	 *
	 * @param c Category object
	 */
	private void showQuestion(String c) {
		// Get all questions in the category
		Map<String, String[]> questions = parser.getCategory(c);
		
		// Select a random question
		String randomQ = (String) randomElement(questions.keySet());
		
		// Create a question object
		Question q = new Question(0, questions.get(randomQ)[0], randomQ, questions.get(randomQ)[1], Question.QuestionState.UNATTEMPTED);
		
		// Display
		game.practiceQuestionPage().show(q, c);
	}
}
