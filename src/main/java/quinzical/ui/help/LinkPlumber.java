package quinzical.ui.help;

import org.apache.commons.text.WordUtils;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebView;

/**
 * Make all links open markdown in the help window
 */
public class LinkPlumber implements ChangeListener<State> {
		HelpViewer viewer;
		WebView view;
		
		public LinkPlumber(HelpViewer viewer, WebView view) {
			this.viewer = viewer;
			this.view = view;
			
			// Add this plumber to the document
			view.getEngine().getLoadWorker().stateProperty().addListener(this);
		}
	
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
						viewer.showHeading(h);
						return null;
					}
				});
			}
		 };
		
		/**
		 * On a page load...
		 */
		@Override
		public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
			if (newState == Worker.State.SUCCEEDED) {
				 org.w3c.dom.Document doc = view.getEngine().getDocument();
				 NodeList links = doc.getElementsByTagName("a");

				 // For all links, add the onClick
				 for (int link = 0; link < links.getLength(); link++) {
					((EventTarget) links.item(link)).addEventListener("click", new onClick(), false);
				 }
			}
		}
}
