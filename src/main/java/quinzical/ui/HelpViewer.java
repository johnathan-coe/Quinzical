package quinzical.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import quinzical.Game;
import org.commonmark.parser.Parser;

import java.io.IOException;

/**
 * The start screen that is the first screen users see
 */
public class HelpViewer {
	private final Game game;
	private final Stage root;
	private final Stage stage;
	private final Scene scene;

	@FXML private WebView view;

	public HelpViewer(Game game, Stage root) throws IOException {
		this.game = game;
		this.root = root;
		this.stage = new Stage();
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help-viewer.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		
		scene = new Scene(pane);
		stage.setScene(scene);
	}

	public void show(String file, String heading) {
		this.stage.show();

		String rendered = "<h1>Hello</h1>";
		view.getEngine().loadContent(rendered);
	}
}
