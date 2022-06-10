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
package games.stendhal.server.maps.ados.wall;

import java.awt.geom.Rectangle2D;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AdosDeathmatch;
import games.stendhal.server.util.Area;

/**
 * Ados Wall North population - Deathmatch.
 *
 * @author hendrik
 */
public class Deathmatch implements ZoneConfigurator {

	private static Area arena;


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final Rectangle2D shape = new Rectangle2D.Double();
		shape.setRect(88, 77, 112 - 88 + 1, 94 - 77 + 1);
		arena = new Area(zone, shape);
		final AdosDeathmatch deathmatch = new AdosDeathmatch(zone, arena);
		deathmatch.createHelmet(102, 75);
		deathmatch.createNPC("Thanatos", 98, 77);
	}

	/**
	 * Checks if player is within boundaries of deathmatch arena.
	 *
	 * @param player
	 *     Player whose position is to be checked.
	 * @return
	 *     <code>true</code> if player is in arena.
	 */
	public static boolean playerInArena(final Player player) {
		return arena != null && arena.contains(player);
	}
}
