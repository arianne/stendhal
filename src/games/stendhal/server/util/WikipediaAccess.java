package games.stendhal.server.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Gets the first text paragraph from the specified Wikipedia article using
 * the MediaWiki bot api.
 *
 * @author hendrik
 */
public class WikipediaAccess extends DefaultHandler {
	private StringBuilder text = new StringBuilder();
	/** used by the parser to detect the right tag */
	private boolean isContent = false;
	/** was the parsing completed */
	private boolean finished = false;

	@Override
	public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) {
		isContent = qName.equals("content");
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (isContent) {
			text.append(ch, start, length);
		}
	}

	public String getText() {
		return text.toString();
	}
	
	public String getProcessedText() {
		String content = getText();
		// remove image links
		content = content.replaceAll("\\[\\[[iI]mage:[^\\]]*\\]\\]", "");
		// remove comments (note reg exp is incorret)
		content = content.replaceAll("<!--[^>]*-->", "");
		// remove templates (note reg exp is incorret)
		content = content.replaceAll("\\{\\{[^\\}]*\\}\\}", "");
		// remove complex links
		content = content.replaceAll("\\[\\[[^\\]]*\\|", "");
		// remove simple links
		content = content.replaceAll("\\[\\[", "");
		content = content.replaceAll("\\]\\]", "");
		
		// ignore leading empty lines and spaces
		content = content.trim();

		// extract the first paragraph (ignoring very short once but oposing a max len)
		int size = content.length();
		int endOfFirstParagraph = content.indexOf("\n", 50);
		if (endOfFirstParagraph < 0) {
			endOfFirstParagraph = size;
		}
		content = content.substring(0, Math.min(endOfFirstParagraph, 1024));
		return content;
	}

	/**
	 * starts the parsing of the specified article
	 *
	 * @param title
	 */
	public void startParsing(String title) {
		finished = true;
	}

	/**
	 * Returns true when the xml response was completly parsed
	 *
	 * @return true if the parsing was completed, false otherwise
	 */
	public boolean isFinished() {
		return finished;
	}
}
