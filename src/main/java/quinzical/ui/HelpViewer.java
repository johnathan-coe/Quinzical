package quinzical.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import quinzical.Game;

import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Image;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Viewer for Markdown documentation from within the app
 */
public class HelpViewer {
	private final Stage stage;
	private final Scene scene;
	private Parser parser;
	private HtmlRenderer renderer;

	@FXML private WebView view;

	public HelpViewer(Game game, Stage root) throws IOException {
		this.stage = new Stage();
		this.renderer = HtmlRenderer.builder().build();
		this.parser = Parser.builder().build();

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help-viewer.fxml"));
		loader.setController(this);
		Pane pane = loader.load();
		
		scene = new Scene(pane);
		stage.setScene(scene);
	}

	/**
	 * Display help
	 * 
	 * @param file Markdown file
	 * @param heading Heading in the markdown file to render
	 */
	public void show(String file, String heading) {
		this.stage.show();
				
		File f = new File(file);
		FileReader reader;
		Node document;
		try {
			reader = new FileReader(f);
			document = parser.parseReader(reader);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// Go through nodes until we hit the desired heading
		Node n = document.getFirstChild();
		while (!((n instanceof Heading) && ((Text) n.getFirstChild()).getLiteral().equals(heading))
					&& n != null) {
			n = n.getNext();
		}
		
		// Excerpt we're interested in
		Document excerpt = new Document();
		
		// If we couldn't find a heading
		if (n == null) {
			// Insert an error message
			excerpt.appendChild(new Text("Error! Help not found."));
		} else {
			// Otherwise add nodes to excerpt until we reach another heading or the end of the document
			do {
				Node curr = n;
				n = n.getNext();
				excerpt.appendChild(curr);
			} while (!(n instanceof Heading) && n != null);
		}
		
		// Update paths for images
		excerpt.accept(new ImagePathVisitor());
					
		// Render the excerpt
		String rendered = renderer.render(excerpt);

		// Display help
		view.getEngine().loadContent(rendered);
		// Style content
		view.getEngine().setUserStyleSheetLocation("file:res/css/markdown.css");
	}
	
	// Visits all images and corrects their path
	private class ImagePathVisitor extends AbstractVisitor {
	    @Override
	    public void visit(Image img) {
	    	// Correct path
	        File f = new File("wiki/docs/" + img.getDestination());
	        img.setDestination(f.toURI().toString());
	        
	        // Descend into children (could be omitted in this case because Text nodes don't have children).
	        visitChildren(img);
	    }
	}
}
