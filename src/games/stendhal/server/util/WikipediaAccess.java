package games.stendhal.server.util;

import games.stendhal.client.update.HttpClient;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Gets the first text paragraph from the specified Wikipedia article using the
 * MediaWiki bot api.
 * 
 * You can invoke the parser either inline using the method parse() or start it
 * in a new thread.
 * 
 * TODO: handle redirects (but take care, there might be two redirects that
 * point to each other).
 * 
 * @author hendrik
 */
public class WikipediaAccess extends DefaultHandler implements Runnable {

	private String title;

	private StringBuilder text = new StringBuilder();

	/** used by the parser to detect the right tag. */
	private boolean isContent;

	/** was the parsing completed. */
	private boolean finished;

	private String error;

	/**
	 * Creates a new WikipeidaAccess.
	 * 
	 * @param title
	 *            title of the page to access
	 */
	public WikipediaAccess(String title) {
		this.title = title;
	}

	@Override
	public void startElement(String namespaceURI, String lName, String qName,
			Attributes attrs) {
		isContent = qName.equals("content");
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (isContent) {
			text.append(ch, start, length);
		}
	}

	/**
	 * Returns the unparsed text.
	 * 
	 * @return content
	 */
	public String getText() {
		return text.toString();
	}

	/**
	 * Gets the last error message.
	 * 
	 * @return error message or <code>null</code> in case no error occurred
	 */
	public String getError() {
		return error;
	}

	/**
	 * Returns the first paragraph of the specified article without wiki code.
	 * 
	 * @return content
	 */
	public String getProcessedText() {
		String content = getText();
		if (content != null) {
			// remove image links
			content = content.replaceAll("\\[\\[[iI]mage:[^\\]]*\\]\\]", "");
			// remove comments
			// (?s) means that . should also match newlines (DOTALL mode).
			content = content.replaceAll("(?s)<!--.*?-->", "");
			// remove ref
			content = content.replaceAll("(?s)<ref>.*?</ref>", "");
			// remove templates
			// This doesn't work with templates inside templates.
			content = content.replaceAll("(?s)\\{\\{.*?\\}\\}", "");
			// remove tables
			// This doesn't work with templates inside templates.
			content = content.replaceAll("(?s)\\{\\|.*?\\|\\}", "");
			// remove complex links
			content = content.replaceAll("\\[\\[[^\\]]*\\|", "");
			// remove simple links
			content = content.replaceAll("\\[\\[", "");
			content = content.replaceAll("\\]\\]", "");
			// remove tags
			content = content.replaceAll("(?s)<.*?>", "");

			// ignore leading empty lines and spaces
			content = content.trim();

			// extract the first paragraph (ignoring very short ones but oposing
			// a max len)
			int size = content.length();
			int endOfFirstParagraph = content.indexOf("\n", 50);
			if (endOfFirstParagraph < 0) {
				endOfFirstParagraph = size;
			}
			content = content.substring(0, Math.min(endOfFirstParagraph, 1024));
		}
		return content;
	}

	/**
	 * Starts the parsing of the specified article.
	 * 
	 * @throws Exception
	 *             in case of an unexpected error
	 */
	public void parse() throws Exception {
		try {
			// look it up using the Wikipedia API
			HttpClient httpClient = new HttpClient(
					"http://en.wikipedia.org/w/query.php?format=xml&titles="
							+ title.replace(' ', '_').replace("%", "%25")
							+ "&what=content");
			SAXParserFactory factory = SAXParserFactory.newInstance();

			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(httpClient.getInputStream(), this);

			// finished
			finished = true;
		} catch (Exception e) {
			finished = true;
			error = e.toString();
			throw e;
		}
	}

	public void run() {
		try {
			parse();
		} catch (Exception e) {
			// ignore as they are already logged in the parse()-method itself
		}
	}

	/**
	 * Returns true when the XML response was completely parsed.
	 * 
	 * @return true if the parsing was completed, false otherwise
	 */
	public boolean isFinished() {
		return finished;
	}
}
