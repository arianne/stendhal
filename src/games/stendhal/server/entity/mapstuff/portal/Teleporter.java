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
package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.engine.Spot;
import games.stendhal.server.entity.player.Player;

public class Teleporter extends Portal {

	private Spot spot;

	public Teleporter(final Spot spot) {
		this.spot = spot;

	}

	/**
	 * Use the portal.
	 *
	 * @param player
	 *            the Player who wants to use this portal
	 * @return <code>true</code> if the portal worked, <code>false</code>
	 *         otherwise.
	 */
	@Override
	protected boolean usePortal(final Player player) {
		if (!nextTo(player)) {
			// Too far to use the portal
			return false;
		}

		if (player.teleport(spot.getZone(), spot.getX(), spot.getY(), null, null)) {
			player.stop();

		}
		return true;
	}

}
