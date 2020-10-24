package quinzical.ui.help;

import java.io.File;
import java.io.FileReader;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.Image;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class Markdown {
	private Parser parser;
	private HtmlRenderer renderer;
	
	public Markdown() {
		renderer = HtmlRenderer.builder().build();
		parser = Parser.builder().build();
	}
	
	/**
	 * Render Markdown file from the heading specified until the next heading
	 * 
	 * @param file File path
	 * @param heading Heading to start from
	 * @return HTML out
	 */
	public String render(String file, String heading) {
		// Parse entire document
		Node document;
		try { document = parser.parseReader(new FileReader(file)); } 
		catch (Exception e) { e.printStackTrace(); return "Exception while reading Markdown file...";	}
		
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
		return renderer.render(excerpt);
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
