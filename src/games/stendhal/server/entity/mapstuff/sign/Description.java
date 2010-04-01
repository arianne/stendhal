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
 * A sign (or transparent area) which is placed on the ground and can be looked at.
 */
public class Description extends Sign {
	/**
	 * Creates a sign.
	 */
	public Description() {
		put(Actions.ACTION, Actions.LOOK);
	}

	@Override
	public String describe() {
		final String text = getText();
		if (text == null) {
			return "You see a sign without any text";
		}

		return text;
	}
}
