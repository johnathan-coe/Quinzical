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

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.node.Block;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * The start screen that is the first screen users see
 */
public class HelpViewer {
	private final Game game;
	private final Stage root;
	private final Stage stage;
	private final Scene scene;
	private Parser parser;
	private HtmlRenderer renderer;

	@FXML private WebView view;

	public HelpViewer(Game game, Stage root) throws IOException {
		this.game = game;
		this.root = root;
		this.stage = new Stage();
		this.renderer = HtmlRenderer.builder().build();
		this.parser = Parser.builder().build();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help-viewer.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		
		scene = new Scene(pane);
		stage.setScene(scene);
	}

	public void show(String file, String heading) {
		this.stage.show();
				
		File f = new File(file);
		FileReader reader;
		
		try {
			reader = new FileReader(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		Node document;
		try {
			document = parser.parseReader(reader);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		Document excerpt = new Document();
		
		Node n = document.getFirstChild();
		// Go through nodes until we hit the desired heading
		while (!((n instanceof Heading) && ((Text) n.getFirstChild()).getLiteral().equals(heading))
					&& n != null) {
			n = n.getNext();
		}
		
		// If we couldn't find a heading
		if (n == null) {
			// Insert an error message
			excerpt.appendChild(new Text("Error! Help file not found."));
		} else {
			// Otherwise go through nodes until we reach another heading or end of document
			do {
				Node old = n;
				n = n.getNext();
				excerpt.appendChild(old);
			} while (!(n instanceof Heading) && n != null);
		}
						
		String rendered = renderer.render(excerpt);
		view.getEngine().loadContent(rendered);
	}
}
