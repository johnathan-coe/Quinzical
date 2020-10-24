package quinzical.ui.scenes;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import quinzical.Game;

public abstract class Page {
    protected Game game;
    protected Parent root;
    private Stage stage;

    public Page(Game game, Stage stage, String fxml) throws IOException {
        this.game = game;
        this.stage = stage;
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
		loader.setController(this);
		root = loader.load();
    }
  
    // Ensure the stage is visible
    public void showStage() {
    	stage.show();
    }
    
    public void show() {
    	// Ensure stage has a root node in the scene graph
    	if (stage.getScene() == null) {
    		stage.setScene(new Scene(new Pane()));
    	}
    	
    	// Set the root node to the loaded fxml
        stage.getScene().setRoot(root);
    }
}
