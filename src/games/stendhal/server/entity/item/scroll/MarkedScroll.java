/*
 * $Id$
 */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.item.scroll;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * Represents a marked teleport scroll.
 */
public class MarkedScroll extends TeleportScroll {
	private static final Logger logger =
				Logger.getLogger(MarkedScroll.class);

	/**
	 * Creates a new marked teleport scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public MarkedScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}


	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the
	 * player on the scroll's destination, or near it. 
	 * @param player The player who used the scroll and who will be teleported
	 * @return true iff teleport was successful
	 */
	@Override
	protected boolean useTeleportScroll(Player player) {
		// init as home_scroll
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_city");
		int x = 30;
		int y = 40;

		/*
		 * Marked scrolls have a destination which is stored in the
		 * infostring, existing of a zone name and x and y coordinates
		 */
		if (has("infostring")) {
			String infostring = get("infostring");
			StringTokenizer st = new StringTokenizer(infostring);
			if (st.countTokens() == 3) {
				StendhalRPZone temp = (StendhalRPZone) StendhalRPWorld.get().getRPZone(st.nextToken());
				if (temp != null) {
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					if (!zone.isTeleportable()) {
						player.sendPrivateText("The strong anti magic aura in the destination area prevents the scroll from working!");
						logger.warn("marked_scroll to zone " + infostring + " teleported " + player.getName() + " to Semos instead");
						return false;
					} else {
						zone = temp;
					}
				} else {
					// invalid zone (the scroll may have been marked in an
					// old version and the zone was removed)
					player.sendPrivateText("Oh oh. For some strange reason the scroll did not teleport me to the right place.");
					logger.warn("marked_scroll to unknown zone " + infostring + " teleported " + player.getName() + " to Semos instead");
				}
			}
		}
		// we use the player as teleporter (last parameter) to give feedback
		// if something goes wrong.
		return player.teleport(zone, x, y, null, player);
	}
}
