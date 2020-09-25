package jeopardy;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PracticeCategoryCard extends Card {
	@FXML private Label category_name;

	PracticeCategoryCard(String catName) throws IOException {
		super(catName);
		System.out.println(catName);
		
		//category_name.setStyle("");
		
	}	
}
