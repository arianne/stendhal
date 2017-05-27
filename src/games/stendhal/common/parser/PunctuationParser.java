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
package games.stendhal.common.parser;

/**
 * PunctuationParser is used to trim preceding and trailing punctuation
 * characters from a string.
 *
 */
public final class PunctuationParser {

	private String text;

	private String preceding = "";
	private String trailing = "";

	public PunctuationParser(final String s) {
		if (s != null) {
			parseString(s);
		}
	}

	private void parseString(final String string) {
		text = string;

		extractPreceedingAndTrimText();

		extractTrailingAndTrimText();
	}

	private void extractTrailingAndTrimText() {
		int i = text.length() - 1;
		while ((i >= 0) && isPunctuation(text.charAt(i))) {
			i--;
		}

		trailing = text.substring(i + 1);
		text = text.substring(0, i + 1);
	}

	private void extractPreceedingAndTrimText() {
		int i = 0;
		while ((i < text.length()) && isPunctuation(text.charAt(i))) {
			i++;
		}

		preceding = text.substring(0, i);
		text = text.substring(i, text.length());
	}

	/**
	 * Evaluates if the passed char is one of . , ! or ? .
	 * @param c
	 * @return true if one of  . , ! or ? .
	 */
	private boolean isPunctuation(final char c) {
		return ((c == '.') || (c == ',') || (c == '!') || (c == '?'));
	}

	/**
	 * Return preceding punctuation characters.
	 *
	 * @return a new string containing preceding punctuation
	 */
	public String getPrecedingPunctuation() {
		return preceding;
	}

	/**
	 * Return trailing punctuation characters.
	 *
	 * @return a new String containing trailing punctuation
	 */
	public String getTrailingPunctuation() {
		return trailing;
	}

	/**
	 * Return remaining text.
	 *
	 * @return a new string containg the stripped and trimmed text
	 */
	public String getText() {
		return text;
	}

}
