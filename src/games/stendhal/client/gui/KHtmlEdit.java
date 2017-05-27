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
package games.stendhal.client.gui;

import java.awt.Color;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import org.apache.log4j.Logger;

import games.stendhal.client.StendhalClient;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPAction;

/**
 * A HTML implementation of a KTextEdit component.
 *
 * TODO: Many of the general HTML functions can be moved to a common utility
 * class.
 *
 * TODO: Move the message formatting (and setup) code to a common class so that
 * the in-game text bubbles can use the same code for rendering.
 */
public class KHtmlEdit extends KTextEdit {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -8415450500521691744L;

	private static Logger logger = Logger.getLogger(KHtmlEdit.class);

	KHtmlEdit() {
		textPane.addHyperlinkListener(new ActivateLinkCB());
	}

	//
	// KHtmlEdit
	//

	/**
	 * Handle hypertext link activation.
	 *
	 * @param ev
	 *            The link event data.
	 */
	protected void activateLink(final HyperlinkEvent ev) {
		String text;
		final URL url = ev.getURL();

		if (url != null) {
			if (url.getProtocol().equals("say")) {
				text = url.getPath();

				try {
					text = URLDecoder.decode(text, "UTF-8");
				} catch (final UnsupportedEncodingException ex) {
					// Leave text as-is and hope for best
				}
			} else {
				// TODO: Activate browser (in a portable way)
				getToolkit().beep();
				return;
			}
		} else {
			text = ev.getDescription();

			if (text.startsWith("say:")) {
				text = text.substring(4);

				try {
					text = URLDecoder.decode(text, "UTF-8");
				} catch (final UnsupportedEncodingException ex) {
					// Leave text as-is and hope for best
				}
			}
		}

		/*
		 * Chat link
		 */
		final RPAction rpaction = new RPAction();

		rpaction.put("type", "chat");
		rpaction.put("text", text);

		StendhalClient.get().send(rpaction);
	}

	/**
	 * Append HTML text to the end of the content. Note: Currently elements must
	 * be complete to be added correctly.
	 *
	 * @param text
	 *            The HTML text to add.
	 */
	protected void appendString(final String text) {
		final HTMLDocument doc = (HTMLDocument) textPane.getDocument();

		try {
			final Element root = doc.getParagraphElement(0);
			doc.insertBeforeEnd(root, text);
		} catch (final BadLocationException e) {
			logger.error(e, e);
		} catch (final IOException e) {
			logger.error(e, e);
		}
	}

	/**
	 * Append a character to a buffer, escaping HTML meta-characters when
	 * needed.
	 * @param sbuf
	 * @param ch
	 *
	 */
	protected void appendHTML(final StringBuilder sbuf, final char ch) {
		switch (ch) {
		case '<':
			sbuf.append("&lt;");
			break;

		case '>':
			sbuf.append("&gt;");
			break;

		case '&':
			sbuf.append("&amp;");
			break;

		default:
			sbuf.append(ch);
			break;
		}
	}

	/**
	 * Escape text as HTML, escaping meta-characters.
	 * @param sbuf
	 *
	 * @param text
	 *            Raw text.
	 *
	 */
	protected void appendHTML(final StringBuilder sbuf, final String text) {
		final StringCharacterIterator ci = new StringCharacterIterator(text);
		char ch = ci.current();

		while (ch != CharacterIterator.DONE) {
			appendHTML(sbuf, ch);
			ch = ci.next();
		}
	}

	/**
	 * Translate a standard Stendhal encoded to HTML encoded.
	 *
	 * @param text
	 *            The text to encode.
	 *
	 * @return HTML encoded text.
	 */
	protected String translateToHTML(final String text) {
		final StringBuilder sbuf = new StringBuilder();

//TODO use common utility class FormatTextParser instead of StringCharacterIterator
//		try {
//			new FormatTextParser() {
//				public void normalText(String txt) throws BadLocationException {
//				}
//
//				public void colorText(String txt) throws BadLocationException {
//				}
//			}.format(text);
//		} catch (Exception ble) { // BadLocationException
//			System.err.println("Couldn't insert initial text.");
//		}

		final StringCharacterIterator ci = new StringCharacterIterator(text);
		char ch = ci.current();

		while (ch != CharacterIterator.DONE) {
			// display text after "#" as link
			if (ch == '#') {
				ch = ci.next();

				/*
				 * '##' means just a single '#'
				 */
				if (ch == '#') {
					appendHTML(sbuf, ch);
					ch = ci.next();
				} else {
					final String link = extractLink(ci);

					/*
					 * Emit link (if any)
					 */
					if (link != null) {
						buildLink(sbuf, link);
					}

					ch = ci.current();
				}
			} else {
				appendHTML(sbuf, ch);
				ch = ci.next();
			}
		}

		return sbuf.toString();
	}

	/**
	 * Extract link content from a character iterator. It is assumed that the
	 * '#' has already been eaten. It leaves the character iterator at the first
	 * character after the link text.
	 *
	 * @param ci
	 *            The character iterator.
	 *
	 * @return Link text (or an empty string).
	 */
	protected String extractLink(final CharacterIterator ci) {
		final StringBuilder sbuf = new StringBuilder();
		char ch = ci.current();
		char terminator = ' ';

		// color quoted compound words like "#'iron sword'"
		if (ch == '\'') {
			terminator = ch;
		}

		while (ch != CharacterIterator.DONE) {
			if (ch == terminator) {
				if (terminator == ' ') {
    				/*
    				 * Continued link (#abc #def)?
    				 */
    				ch = ci.next();

    				if (ch == '#') {
    					ch = ' ';
    				} else {
    					ci.previous();
    					break;
    				}
				} else {
					break;
				}
			}

			sbuf.append(ch);
			ch = ci.next();
		}

		/*
		 * Don't treat word delimiter(s) on the end as link text
		 */
		int len = sbuf.length();

		while (len != 0) {
			if (!isWordDelim(sbuf.charAt(--len))) {
				len++;
				break;
			}

			sbuf.setLength(len);
			ci.previous();
		}

		/*
		 * Nothing found?
		 */
		if (len == 0) {
			return null;
		}

		return sbuf.toString();
	}

	/**
	 * Determine is a character is a word delimiter when followed by a space or
	 * end-of-line. Care should be taken to avoid matching characters that are
	 * typically at the end of valid URL's.
	 *
	 * @param ch
	 *            A character;
	 *
	 * @return <code>true</code> if a word delimiter.
	 */
	protected boolean isWordDelim(final char ch) {
		switch (ch) {
		case '.':
		case ',':
		case '!':
		case '?':
		case ';':
			return true;

		default:
			return false;
		}
	}

	/**
	 * Convert a text "link" to an HTML link. For well-known URL's, the link is
	 * taken literally, otherwise a <code>say:</code> URL will be generated.
	 *
	 * @param sbuf
	 *            The string buffer to append to.
	 * @param text
	 *            The text to convert.
	 */
	protected void buildLink(final StringBuilder sbuf, final String text) {
		sbuf.append("<a href='");

		if (text.startsWith("http://") || text.startsWith("https://")
				|| text.startsWith("ftp://")) {
			sbuf.append(text);
		} else {
			sbuf.append("say:");

			try {
				sbuf.append(URLEncoder.encode(text, "UTF-8"));
			} catch (final UnsupportedEncodingException ex) {
				// Nothing left to try
				sbuf.append(text);
			}
		}

		sbuf.append("'>");
		appendHTML(sbuf, text);
		sbuf.append("</a>");
	}

	/**
	 * Convert a color to a CSS color attribute value.
	 *
	 * @param color
	 *            An AWT color.
	 *
	 * @return A <code>color:</code> CSS attribute value.
	 */
	protected String colorToRGB(final Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(),
				color.getBlue());
	}

	//
	// KTextEdit
	//
	@Override
	protected void initStylesForTextPane(final JTextPane textPane, int mainTextSize) {
		textPane.setContentType("text/html");

		final HTMLDocument doc = (HTMLDocument) textPane.getDocument();
		final StyleSheet css = doc.getStyleSheet();

		/*
		 * Configure standard styles
		 */
		css.addRule("body { font-family: Dialog; font-size: " + (mainTextSize + 1)
				+ "pt }");
		css.addRule("a { color: blue; font-style: italic }");

		css.addRule("._timestamp { color: " + colorToRGB(HEADER_COLOR)
				+ "; font-size: " + (mainTextSize - 1)
				+ "pt; font-style: italic }");
		css.addRule("._header { color: " + colorToRGB(HEADER_COLOR) + " }");

		/*
		 * Configure notification types
		 */
		for (final NotificationType type : NotificationType.values()) {
			final Color color = type.getColor();

			if (color != null) {
				css.addRule("." + type.getMnemonic() + " { color: "
						+ colorToRGB(color) + "; font-weight: bold; }");
			}
		}
	}

	@Override
	protected void insertHeader(final String text) {
		if ((text != null) && (text.length() != 0)) {
			final StringBuilder sbuf = new StringBuilder();

			sbuf.append("<span class='_header'>");
			sbuf.append("&lt;");
			appendHTML(sbuf, text);
			sbuf.append("&gt;");
			sbuf.append("</span>");

			appendString(sbuf.toString());
		}
	}

	@Override
	protected void insertNewline() {
		appendString("<br>\n");
	}

	/**
	 * Insert the text portion of the line using a specified notification type
	 * for style.
	 *
	 * @param text
	 *            The text to insert.
	 * @param type
	 *            The notification type.
	 */
	@Override
	protected void insertText(final String text, final NotificationType type) {
		final StringBuilder sbuf = new StringBuilder();

		sbuf.append("<span class='");
		sbuf.append(type.getMnemonic());
		sbuf.append("'>");
		sbuf.append(translateToHTML(text));
		sbuf.append("</span>");

		appendString(sbuf.toString());
	}

	@Override
	protected void insertTimestamp(final String text) {
		final StringBuilder sbuf = new StringBuilder();

		sbuf.append("<span class='_timestamp'>");
		appendHTML(sbuf, text);
		sbuf.append("</span>");

		appendString(sbuf.toString());
	}

	//
	//

	/**
	 * A hyperlink listener for link activation.
	 */
	private class ActivateLinkCB implements HyperlinkListener {
		@Override
		public void hyperlinkUpdate(final HyperlinkEvent ev) {
			if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				activateLink(ev);
			}
		}
	}
}
