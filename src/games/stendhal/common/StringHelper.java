/***************************************************************************
 *                      (C) Copyright 2010 - Stendhal                      *
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

/**
 * helper functions for working with strings.
 *
 * @author hendrik
 */
public class StringHelper {

	/**
	 * removes single and double quotes around a string
	 *
	 * @param text text to unquote
	 * @return unquoted text
	 */
	public static String unquote(String text) {
		if ((text == null) || text.length() < 2) {
			return text;
		}

		if (text.charAt(0) == text.charAt(text.length() - 1)) {
			if (text.charAt(0) == '"' || text.charAt(0) == '\'') {
				return text.substring(1, text.length() -1);
			}
		}
		return text;
	}
}
