package quinzical.ui;

import javafx.scene.Parent;
import javafx.stage.Stage;
import quinzical.Game;

public class Page {
    protected Game game;
    protected Parent root;
    private Stage stage;

    public Page(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
    }

    public void show() {
        stage.getScene().setRoot(root);
    }
}
