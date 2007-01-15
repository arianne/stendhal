/* $Id$
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
 * Represents a teleport scroll.
 */
public class TeleportScroll extends InfoStringScroll {
	private static final Logger logger =
				Logger.getLogger(TeleportScroll.class);

	/**
	 * Creates a new teleport scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public TeleportScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}


	/**
	 * Is invoked when a teleporting scroll is used. Tries to put the
	 * player on the scroll's destination, or near it. 
	 * @param player The player who used the scroll and who will be teleported
	 * @return true iff teleport was successful
	 */
	protected boolean useScroll(Player player) {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
		if (!zone.isTeleportable()) {
			player.sendPrivateText("The strong anti magic aura in this aera prevents the scroll from working!");
			return false;
		}
		
		// init as home_scroll
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_city");
		int x = 30;
		int y = 40;

		// Is it a marked scroll? Marked scrolls have a destination which
		// is stored in the infostring, existing of a zone name and x and y
		// coordinates
		if (has("infostring")) {
			String infostring = get("infostring");
			StringTokenizer st = new StringTokenizer(infostring);
			if (st.countTokens() == 3) {
				StendhalRPZone temp = (StendhalRPZone) StendhalRPWorld.get().getRPZone(st.nextToken());
				if (temp != null) {
					x = Integer.parseInt(st.nextToken());
					y = Integer.parseInt(st.nextToken());
					if (!zone.isTeleportable()) {
						player.sendPrivateText("The strong anti magic aura in the destination aera prevents the scroll from working!");
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
