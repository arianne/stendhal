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

/**
 * Parsing of formated text. 
 *
 * @author Martin Fuchs
 */
public abstract class FormatTextParser {
	/**
	 * Write text optionally colored, and strips '\' from the front of the '#' signs.
	 * 
	 * @param text the text to write
	 * @param color true if the text should be coloured
	 * @throws Exception
	 */
	private void writeText(final String text, final boolean color) throws Exception {
		if (color) {
			colorText(text.replaceAll("\\\\#", "#"));
		} else {
			normalText(text.replaceAll("\\\\#", "#"));
		}
	}

	public void format(String text) throws Exception {
		int startIndex = 0;
		int unquotedIndex, quotedIndex;
		boolean done = false;
		
		while (!done && text.length() > 0)  {
			unquotedIndex = text.indexOf('#', startIndex);
			if (unquotedIndex == -1) {
				writeText(text, false);
				done = true;
			} else {
				quotedIndex = text.indexOf("\\#", startIndex);
				if (quotedIndex != -1 && quotedIndex == (unquotedIndex - 1)) {
					// the next match is \#. skip it and let writeText replace it
					startIndex = quotedIndex + 2;
				} else {
					// found a lone #. start coloring
					// Write the text before the #
					if (unquotedIndex != 0) {
						//writeText(text.substring(0, unquotedIndex - 1), false);
						writeText(text.substring(0, unquotedIndex), false);
					}
					
					// Find the region to color
					char endMarker = ' ';
					int shift = 0;
					if ((text.length() > (unquotedIndex + 1)) && (text.charAt(unquotedIndex + 1) == '\'')) {
						endMarker = '\'';
						shift = 1;
					}
					// skip the #, and possible quoting
					text = text.substring(unquotedIndex + 1 + shift);
					final int stopAt = text.indexOf(endMarker);
					if (stopAt  == -1) {
						// No end marker found. the rest is colored (colors improperly quoted strings) 
						writeText(text, true);
						done = true;
					} else {
						// color until the endMarker, and skip the possible quoting
						writeText(text.substring(0, stopAt), true);
						text = text.substring(stopAt + shift);
					}
				}
			}
		}
	}

	public abstract void normalText(String txt) throws Exception;
	public abstract void colorText(String txt) throws Exception;
}
