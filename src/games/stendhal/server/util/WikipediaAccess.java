/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import games.stendhal.client.update.HttpClient;

/**
 * Gets the first text paragraph from the specified Wikipedia article using the
 * MediaWiki bot API.
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

	private final String title;

	private final StringBuilder text = new StringBuilder();

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
	public WikipediaAccess(final String title) {
		this.title = title;
	}

	@Override
	public void startElement(final String namespaceURI, final String lName, final String qName,
			final Attributes attrs) {
		isContent = qName.equals("rev");
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
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
			// remove REDIRECT headers
			if (content.startsWith("#REDIRECT")) {
				content = content.replaceFirst(".*\n", "");
			}

			content = wikiToPlainText(content);
		}

		return content;
	}

	/**
	 * Extract plain text from Wikipedia article content.
	 *
	 * @param content
	 * @return plain text
	 */
	private static String wikiToPlainText(String content) {
		// remove image links
		content = content.replaceAll("\\[\\[[iI]mage:[^\\]]*\\]\\]", "");
		// remove comments
		// (?s) means that . should also match newlines (DOTALL mode).
		content = content.replaceAll("(?s)<!--.*?-->", "");
		// remove ref
		content = content.replaceAll("(?s)<ref>.*?</ref>", "");

		// remove templates
		// first for three level deep templates
		content = content.replaceAll("(?s)\\{\\{([^{}]*?\\{\\{([^{}]*?\\{\\{[^{}]*?\\}\\})+[^{}]*?\\}\\})+[^{}]*?}}", "");
		// then for two level deep templates
		content = content.replaceAll("(?s)\\{\\{([^{}]*?\\{\\{[^{}]*?\\}\\})+[^{}].*?\\}\\}", "");
		// then handle one level templates (This doesn't work with templates inside templates.)
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

		// extract the first paragraph (ignoring very short ones but opposing a max len)
		final int size = content.length();
		int endOfFirstParagraph = content.indexOf("\n", 50);
		if (endOfFirstParagraph < 0) {
			endOfFirstParagraph = size;
		}
		content = content.substring(0, Math.min(endOfFirstParagraph, 1024));

		return content;
	}

	/**
	 * Starts the parsing of the specified article.
	 *
	 * @return <code>true</code> on successful parsing, <code>false</code> on
	 * 	failure
	 */
	private boolean parse() {
		String keyword = title;
		boolean success;

		try {
			while(keyword != null) {
				// look it up using the Wikipedia API
				final HttpClient httpClient = new HttpClient(
						"https://en.wikipedia.org/w/api.php?action=query&titles="
								+ keyword.replace(' ', '_').replace("%", "%25")
								+ "&prop=revisions&rvprop=content&format=xml");
				final SAXParserFactory factory = SAXParserFactory.newInstance();
				factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

				// Parse the input
				final SAXParser saxParser = factory.newSAXParser();
				saxParser.parse(httpClient.getInputStream(), this);

				final String response = getText();

				if (response.startsWith("#REDIRECT")) {
					// extract the new keyword
					final String redirect = wikiToPlainText(response).substring(9).replaceAll("\\n.*", "").trim();
					if (keyword.equalsIgnoreCase(redirect)) {
						// stop to avoid an infinite loop
						keyword = null;
					} else {
						reset();
						keyword = redirect;
					}
				} else {
					// finished
					keyword = null;
				}
			}

			success = true;
		} catch (final Exception e) { // SAXException, IOException
			error = e.toString();
			success = false;
		} finally {
			finished = true;
		}

		return success;
	}

	/**
	 * Reset internal state to repeat a query.
	 */
	private void reset() {
		isContent = false;
		finished = false;
		text.setLength(0);
	}

	@Override
	public void run() {
		parse();

		// ignore failures as they are already logged in the parse()-method itself
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
