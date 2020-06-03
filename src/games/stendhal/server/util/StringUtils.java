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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * little methods to work with strings.
 *
 * @author hendrik
 */
public class StringUtils {
	private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * Counts the number of upper case characters.
	 *
	 * @param text text to count
	 * @return number of upper case characters
	 */
	public static int countUpperCase(final String text) {
		int count = 0;
		for (final char chr : text.toCharArray()) {
			if (Character.isUpperCase(chr)) {
				count++;
			}
		}
		return count;
	}


	/**
	 * Counts the number of lower case characters.
	 *
	 * @param text text to count
	 * @return number of lower case characters
	 */
	public static int countLowerCase(final String text) {
		int count = 0;
		for (final char chr : text.toCharArray()) {
			if (Character.isLowerCase(chr)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * creates a random string which only consists of letters
	 *
	 * @param count length of the string
	 * @return generated string
	 */
	public static String generateStringOfCharacters(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(CHARACTERS.charAt((int)(Math.random() * CHARACTERS.length())));
		}
		return sb.toString();
	}

    /**
     * Replaces [variables] in a string
     *
     * @param string with [variables]
     * @param params name1, value1, name2, value2, name3, value3
     * @return string with substituted parameters
     */
    public static String substitute(String string, Object... params)  {
    	Map<String, Object> map = new HashMap<String, Object>();
    	for (int i = 0; i < params.length / 2; i++) {
    		map.put(params[i*2].toString(), params[i*2+1]);
    	}
    	return substitute(string, map);
    }


    /**
     * Replaces [variables] in a string
     *
     * @param string with [variables]
     * @param params replacement parameters
     * @return string with substituted parameters
     */
    public static String substitute(String string, Map<String, ?> params)  {
    	if (params == null) {
    		return string;
    	}
        StringBuilder res = new StringBuilder();
        StringTokenizer st = new StringTokenizer(string, "[]", true);
        String lastToken = "";
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (lastToken.equals("[")) {

                Object temp = params.get(token);
                if (temp != null) {
                    token = temp.toString();
                } else {
                    token = "";
                }

            }
            lastToken = token.trim();
            if (token.equals("[") || token.equals("]")) {
                token = "";
            }
            res.append(token);
        }
        return res.toString();
    }

    /**
     * Converts a string to title case. Example: "hello world" is converted to "Hello World"
     *
     * @param st
     * 		String to be formatted.
     * @return
     * 		Title case formatted string.
     */
	public static String titleize(final String st) {
		final StringBuilder titleized = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c: st.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}

			titleized.append(c);
		}

		return titleized.toString();
	}

	/**
	 * trims a string to the specified size. It will not throw an error, if the string is already short enough.
	 *
	 * @param string string to trim down
	 * @param size size
	 * @return trimmed string
	 */
	public static String trimTo(String string, int size) {
		if (string == null) {
			return null;
		}
		String res = string.trim();
		if (res.length() > size) {
			return res.substring(0, size);
		}
		return res;
	}
}
