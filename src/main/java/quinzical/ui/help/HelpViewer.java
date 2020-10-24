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

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Image;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.Event;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.text.WordUtils;

import quinzical.ui.scenes.Page;

/**
 * Viewer for Markdown documentation from within the app
 */
public class HelpViewer extends Page {
	private String file;
	private Markdown md;

	@FXML private WebView view;

	public HelpViewer(Game game) throws IOException {
		// Place this helpviewer page on a new stage
		super(game, new Stage(), "/fxml/help-viewer.fxml");
		
		md = new Markdown();
		
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

		// Render to HTML
		String rendered = md.render(file, heading);
		
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
}
