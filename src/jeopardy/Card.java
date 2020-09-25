package jeopardy;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class Card {
	private final Pane pane;
	@FXML private Label category_name;
	
	Card(String title) throws IOException {
		// Pull layout from file
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/question-card.fxml"));
		loader.setController(this);
		pane = loader.load();

		category_name.setText(title);
		
		// Set pane background to an image
		String imageUrl = String.format("./res/images/%s.jpg", title.replace(" ", "_"));
		String styleString = String.format("-fx-background-image: url('file:%s');", imageUrl);
		if (!Files.exists(Path.of(imageUrl))) {
			styleString = "-fx-background-color: grey;";
		}
		pane().setStyle(styleString + "-fx-background-size: cover;");
	}
	
	public Pane pane() {
		return pane;
	}
}
