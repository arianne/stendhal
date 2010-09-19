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
package games.stendhal.common;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Vector;

/**
 * Generic command line parser considering quoted strings.
 *
 * @author Martin Fuchs
 */
public class CommandlineParser {

	/** a character iterator over the text */
	protected final CharacterIterator ci;

	/**
	 * Creates a new CommandlineParser
	 *
	 * @param text text to parse
	 */
	public CommandlineParser(final String text) {
		ci = new StringCharacterIterator(text);
	}

	/**
	 * Skip leading spaces.
	 */
	public void skipWhitespace() {
		while (Character.isWhitespace(ci.current())) {
			ci.next();
		}
    }

	/**
	 * Read next command line parameter considering quoting.
	 *
	 * @param errors
	 * @return parameter
	 */
	public String getNextParameter(final ErrorDrain errors) {
		skipWhitespace();

		char ch = ci.current();

		StringBuilder sbuf = null;
		char quote = CharacterIterator.DONE;

		while (ch != CharacterIterator.DONE) {
			if (sbuf == null) {
				sbuf = new StringBuilder();
			}

			if (ch == quote) {
				// End of quote
				quote = CharacterIterator.DONE;
			} else if (quote != CharacterIterator.DONE) {
				// Quoted character
				sbuf.append(ch);
			} else if ((ch == '"') || (ch == '\'')) {
				// Start of quote
				quote = ch;
			} else if (Character.isWhitespace(ch)) {
				// End of token
				break;
			} else {
				// Token character
				sbuf.append(ch);
			}

			ch = ci.next();
		}

		/*
		 * Unterminated quote?
		 */
		if (quote != CharacterIterator.DONE) {
			errors.setError("Unterminated quote");
		}

		if (sbuf != null) {
			return sbuf.toString();
		} else {
			return null;
		}
	}

	/**
	 * Read all remaining parameters into a String list.
	 *
	 * @param errors
	 * @return parameter list
	 */
	public List<String> readAllParameters(final ErrorDrain errors) {
		final List<String> params = new Vector<String>();

		do {
			skipWhitespace();

			final String param = getNextParameter(errors);

			if (param == null) {
				break;
			}

			params.add(param);
		} while (!errors.hasError());

		return params;
	}
}
