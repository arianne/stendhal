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

import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.rule.EntityManager;

import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Represents an empty/unmarked teleport scroll.
 */
public class EmptyScroll extends Scroll {
//	private static final Logger logger = Logger.getLogger(EmptyScroll.class);

	/**
	 * Creates a new empty scroll
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public EmptyScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}


	/**
	 * Use a [empty] scroll.
	 *
	 * @param player
	 * @return always true
	 */
	protected boolean useScroll(Player player) {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
		if (zone.isTeleportable()) {
			Item markedScroll = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("marked_scroll");
			markedScroll.put("infostring", player.getID().getZoneID() + " "	+ player.getX() + " " + player.getY());
			markedScroll.put("bound", player.getName());
			player.equip(markedScroll, true);
			return true;
		} else {
			player.sendPrivateText("The strong anti magic aura in this aera prevents the scroll from working!");
			return false;
		}
	}
}
