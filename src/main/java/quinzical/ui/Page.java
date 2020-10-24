package quinzical.ui;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import quinzical.Game;

public class Page {
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

    public void show() {
        stage.getScene().setRoot(root);
    }
}
