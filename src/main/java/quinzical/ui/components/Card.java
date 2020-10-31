package quinzical.ui.components;

import java.io.IOException;
import java.net.URL;

import javafx.scene.shape.Rectangle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * A component that displays a title and image for a category.
 * 
 * Subclass this if you want to add content to the 'questions' VBox.
 */
public class Card {
	private final Pane pane;
	@FXML private Label category_name;
	@FXML private VBox questions;
	protected String title;
	
	/**
	 * Build a card from a category name.
	 * The image to use is resolved from the category name passed in.
	 * @param title Title of the category
	 * @throws IOException
	 */
	public Card(String title) throws IOException {
		this.title = title;

		// Pull layout from file
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/question-card.fxml"));
		loader.setController(this);
		pane = loader.load();

		category_name.setText(title);
		
		// Set pane background to an image
		String imageUrl = String.format("/images/%s.jpg", title.replace(" ", "_"));
		URL url = getClass().getResource(imageUrl);
		
		String styleString;
		if (url == null) {
			// Grey background if an image is not found
			styleString = "-fx-background-color: grey;";
		} else {
			styleString = String.format("-fx-background-image: url('%s');", url.toExternalForm());
		}
		
		questions.setStyle(styleString + "-fx-background-size: cover;");
	
		// Round background image
		Rectangle clip = new Rectangle (200, 230);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
		questions.setClip(clip);
	}
	
	public Pane pane() {
		return pane;
	}
}
