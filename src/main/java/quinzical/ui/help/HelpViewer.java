package quinzical.ui.help;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
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
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.Event;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.text.WordUtils;

import quinzical.ui.scenes.Page;

/**
 * Viewer for Markdown documentation from within the app
 */
public class HelpViewer extends Page {
	private Parser parser;
	private HtmlRenderer renderer;
	private String file;

	@FXML private WebView view;

	public HelpViewer(Game game) throws IOException {
		// Place this helpviewer page on a new stage
		super(game, new Stage(), "/fxml/help-viewer.fxml");
		
		this.renderer = HtmlRenderer.builder().build();
		this.parser = Parser.builder().build();
		
		// When a page is loaded, ensure all links render markdown
		view.getEngine().getLoadWorker().stateProperty().addListener(new loadListener());
	}
	
	/**
	 * Display help
	 * 
	 * @param file Markdown file
	 * @param heading Heading in the markdown file to render
	 */
	public void show(String file, String heading) {
		this.file = file;
		
		super.show();
		showStage();		
		
		// Parse entire document
		Node document;
		try { document = parser.parseReader(new FileReader(file)); } 
		catch (Exception e) { e.printStackTrace(); return;	}
		
		// Go through nodes
		Node n = document.getFirstChild();
		while (n != null) {
			// Break when we hit a heading with the desired text
			if ((n instanceof Heading) && ((Text) n.getFirstChild()).getLiteral().equals(heading)) {
				break;
			}
			n = n.getNext();
		}
		
		// Excerpt we're interested in
		Document excerpt = new Document();
		
		// If we couldn't find a heading
		if (n == null) {
			excerpt.appendChild(new Text("Error! Help not found."));
		} else {
			// Add nodes until end of document
			while (n != null) {
				Node curr = n;
				n = n.getNext();
				excerpt.appendChild(curr);
				
				// Break at the next heading
				if (n instanceof Heading) { break; }
			}
		}
		
		// Update paths for images
		excerpt.accept(new ImagePathVisitor());
					
		// Render the excerpt
		String rendered = renderer.render(excerpt);

		// Load into webview
		view.getEngine().loadContent(rendered);
		
		// Style
		URL url = getClass().getResource("/css/markdown.css");
		view.getEngine().setUserStyleSheetLocation(url.toExternalForm());
	}
	
	// Show a heading in the current file
	public void showHeading(String h) {
		show(file, h);
	}
	
		 
	// On page load, make all links render markdown when clicked
	private class loadListener implements ChangeListener<State> {
		// Listens to a link click
		private class onClick implements org.w3c.dom.events.EventListener {
			@Override
			public void handleEvent(Event ev) {
				// Convert link content to a heading
				String heading = ev.getTarget().toString().substring(1);
				heading = heading.replace("-", " ");
				heading = WordUtils.capitalizeFully(heading);
				final String h = heading;
				
				Platform.runLater(new Task<Void>() {
					@Override
					protected Void call() {
						showHeading(h);
						return null;
					}
				});
			}
		 };
		
		@Override
		public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
			if (newState == Worker.State.SUCCEEDED) {
				 org.w3c.dom.Document doc = view.getEngine().getDocument();
				 NodeList links = doc.getElementsByTagName("a");

				 for (int link = 0; link < links.getLength(); link++) {
					((EventTarget) links.item(link)).addEventListener("click", new onClick(), false);
				 }
			}
		}
	}
	
	// Visits all images and corrects their path
	private class ImagePathVisitor extends AbstractVisitor {
	    @Override
	    public void visit(Image img) {
	    	// Correct path
	        File f = new File("wiki/docs/" + img.getDestination());
	        img.setDestination(f.toURI().toString());
	        
	        // Descend into children
	        visitChildren(img);
	    }
	}
}
