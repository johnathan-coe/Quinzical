package quinzical.ui.help;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import quinzical.Game;
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
		
		// Markdown renderer
		md = new Markdown();
		
		// Plumb all links on the webview to the Markdown viewer
		new LinkPlumber(this, view);
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
}
