/* $Id$ */
/***************************************************************************
 *                    (C) Copyright 2003-2010 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.sign;

import games.stendhal.common.constants.Actions;

/**
 * A Reader is a sign that defaults to "transparent" and 0 resistance.
 * It only displays what is written in the "text" attribute.
 */
public class Reader extends Sign {

	/**
	 * Creates a Reader.
	 */
	public Reader() {
		setRPClass("sign");
		put(Actions.TYPE, "sign");
		put(Actions.ACTION, Actions.READ);
		put("class", "transparent");
		setResistance(0);
	}

	@Override
	public String describe() {
		final String text = getText();
		if (text == null) {
			return "The text is empty.";
		}

		return text;
	}

}
