/* $Id$ */
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
package games.stendhal.server.entity.item;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.scroll.Scroll;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.UseListener;

import java.util.Map;

import org.apache.log4j.Logger;

import marauroa.common.game.RPObject;

/**
 * A special ring that allows the owner to teleport to his or her spouse.
 * The spouse's name is engraved into the ring. Technically, the name is
 * stored in the item's infostring.
 * 
 * Wedding rings should always be bound to the owner.
 * 
 * @author daniel
 */
public class WeddingRing extends Item implements UseListener {
	
	private static final Logger logger = Logger.getLogger(Scroll.class);

	/**
	 * Creates a new wedding ring.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public WeddingRing(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}
	
	public void onUsed(RPEntity user) {
		RPObject base = this;
		// Find the top container
		while (base.isContained()) {
			base = base.getContainer();
		}

		if (user instanceof Player && user.nextTo((Entity) base, 0.25)) {
			teleportToSpouse((Player) user);
		}
	}
	
	/**
	 * Teleports the given player to his/her spouse, but only if the spouse
	 * is also wearing the wedding ring.
	 * 
	 * @param player The ring's owner.
	 */
	private void teleportToSpouse(Player player) {
		if (has("infostring")) {
			String spouseName = get("infostring");
			Player spouse = StendhalRPRuleProcessor.get().getPlayer(spouseName);
			if (spouse == null) {
				player.sendPrivateText(spouseName + " is not online.");
			} else if (spouse.isEquipped("wedding_ring")){
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
						.getRPZone(spouse.getID());
				int x = spouse.getX();
				int y = spouse.getY();
				Direction dir = spouse.getDirection();

				player.teleport(zone, x, y, dir, player);
			} else {
				// This means trouble ;)
				player.sendPrivateText(spouseName + " is not wearing the wedding ring.");
			}
		} else {
			player.sendPrivateText("This wedding ring hasn't been engraved yet.");
			logger.debug(player.getName() + "tried to use a wedding ring without a spouse name engraving.");
		}
	}

	@Override
	public String describe() {
		if (has("infostring")) {
			return "You see a wedding ring. Its engraving says: \"In eternal love to "
					+ get("infostring")
					+ "\".";
		} else {
			return "You see a wedding ring.";
		}
	}
}