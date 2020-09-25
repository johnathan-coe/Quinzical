package jeopardy;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public abstract class Card {
	private final Pane pane;
	@FXML private Label category_name;
	
	Card(String title) throws IOException {
		// Pull layout from file
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/question-card.fxml"));
		loader.setController(this);
		pane = loader.load();

		category_name.setText(title);
		pane().setStyle(String.format("-fx-background-image: url('res/images/%s.jpg');", title.replace(" ", "_"))+
				  					  "-fx-background-size: cover;");
		category_name.setStyle("-fx-background-color: #38383daa;"
							 + "-fx-text-fill: white;"
							 + "-fx-padding: 5;");
	}
	
	public Pane pane() {
		return pane;
	}
}
