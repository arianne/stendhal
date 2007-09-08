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
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

import marauroa.common.Log4J;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * A special ring that allows the owner to teleport to his or her spouse.
 * The spouse's name is engraved into the ring. Technically, the name is
 * stored in the item's infostring.
 *
 * Wedding rings should always be bound to the owner.
 *
 * @author daniel
 */
public class WeddingRing extends Ring {

	private static final Logger logger = Log4J.getLogger(WeddingRing.class);

	/**
	 * Creates a new wedding ring.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public WeddingRing(String name, String clazz, String subclass, Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 *
	 * @param item item to copy
	 */
	public WeddingRing(WeddingRing item) {
		super(item);
	}

	@Override
	public void onUsed(RPEntity user) {
		RPObject base = this;
		// Find the top container
		while (base.isContained()) {
			base = base.getContainer();
		}

		if (user instanceof Player && user.nextTo((Entity) base)) {
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
		if (!has("infostring")) {
			player.sendPrivateText("This wedding ring hasn't been engraved yet.");
			logger.debug(player.getName() + "tried to use a wedding ring without a spouse name engraving.");
			return;
		}

		String spouseName = get("infostring");
		Player spouse = StendhalRPRuleProcessor.get().getPlayer(spouseName);
		if (spouse == null) {
			player.sendPrivateText(spouseName + " is not online.");
			return;
		}

		if (!spouse.isEquipped("wedding_ring")) {
			// This means trouble ;)
			player.sendPrivateText(spouseName + " is not wearing the wedding ring.");
			return;
		}

		StendhalRPZone sourceZone = player.getZone();
		if (!sourceZone.isTeleportAllowed()) {
			player.sendPrivateText("The strong anti magic aura in this area prevents the wedding ring from working!");
			return;
		}

		StendhalRPZone destinationZone = spouse.getZone();
		if (!destinationZone.isTeleportAllowed()) {
			player.sendPrivateText("The strong anti magic aura in the destination area prevents the wedding ring from working!");
			return;
		}

		int x = spouse.getX();
		int y = spouse.getY();
		Direction dir = spouse.getDirection();

		player.teleport(destinationZone, x, y, dir, player);
	}

	@Override
	public String describe() {
		if (has("infostring")) {
			return "You see a wedding ring. Its engraving says: \"In eternal love to " + get("infostring") + "\".";
		} else {
			return "You see a wedding ring.";
		}
	}
}
