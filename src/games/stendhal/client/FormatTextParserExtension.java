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
package games.stendhal.client;

import games.stendhal.client.gui.FormatTextParser;

public final class FormatTextParserExtension extends FormatTextParser {
	private final StringBuilder temp;

	public FormatTextParserExtension(StringBuilder temp) {
		this.temp = temp;
	}

	@Override
	public void normalText(final String tok) {
		temp.append(tok);
	}

	@Override
	public void colorText(final String tok) {
		temp.append(tok);
	}
}
