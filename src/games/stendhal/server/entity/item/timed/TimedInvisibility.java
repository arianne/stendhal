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
package games.stendhal.server.entity.item.timed;

import java.util.Map;

import games.stendhal.server.entity.player.Player;

public class TimedInvisibility extends TimedStackableItem {

	public TimedInvisibility(final TimedStackableItem item) {
		super(item);
	}

	public TimedInvisibility(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	@Override
	public boolean useItem(final Player player) {
		if (player == null) {
			return false;
		}
		player.setInvisible(true);
		return true;
	}

	@Override
	public void itemFinished(final Player player) {
		if (player != null) {
			player.sendPrivateText("You don't feel so secure anymore.");
			player.setInvisible(false);
		}
	}
}
