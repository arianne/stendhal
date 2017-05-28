/***************************************************************************
 *                 (C) Copyright 2003-2013 - Stendhal team                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.chat;

import java.util.regex.Pattern;

class QuoteSpecials {
	private static final Pattern BSLASH = Pattern.compile("\\\\");
	private static final Pattern ITEM_MARKUP = Pattern.compile("§");

	/**
	 * Quotes player supplied text so that the markup characters in it appear to
	 * have no special meaning when interpreted by the client. '#' is still
	 * allowed, as player are used to have it.
	 *
	 * @param text player supplied text
	 * @return quoted text
	 */
	public static String quote(String text) {
		// Note the special meaning both within source, and for the regexp
		// engine. '\\\\\\\\ 'ends up meaning just '\\'
		text = BSLASH.matcher(text).replaceAll("\\\\\\\\");
		return ITEM_MARKUP.matcher(text).replaceAll("\\\\§");
	}
}
