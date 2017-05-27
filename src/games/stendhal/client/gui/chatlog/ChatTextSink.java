/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.chatlog;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.textformat.AttributedTextSink;
import games.stendhal.client.gui.textformat.StyleSet;

/**
 * AttributedTextSink for writing to a styled document.
 */
public class ChatTextSink implements AttributedTextSink<StyleSet> {
	/** Logger instance. */
	private static final Logger logger = Logger.getLogger(ChatTextSink.class);

	/** DEstination document. */
	private final Document document;

	/**
	 * Create a new ChatTextSink.
	 *
	 * @param document destination document
	 */
	public ChatTextSink(Document document) {
		this.document = document;
	}

	@Override
	public void append(String s, StyleSet attrs) {
		try {
			document.insertString(document.getLength(), s, attrs.contents());
		} catch (BadLocationException e) {
			logger.error("Failed to insert text.", e);
		}
	}
}
