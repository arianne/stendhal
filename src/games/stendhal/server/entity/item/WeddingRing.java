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
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * A special ring that allows the owner to teleport to his or her spouse. The
 * spouse's name is engraved into the ring. Technically, the name is stored in
 * the item's infostring.
 * 
 * Wedding rings should always be bound to the owner.
 * 
 * @author daniel
 */
public class WeddingRing extends Ring {

	private static final Logger logger = Logger.getLogger(WeddingRing.class);

	/**
	 * Creates a new wedding ring.
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public WeddingRing(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param item
	 *            item to copy
	 */
	public WeddingRing(final WeddingRing item) {
		super(item);
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		RPObject base = this;
		// Find the top container
		while (base.isContained()) {
			base = base.getContainer();
		}

		if ((user instanceof Player) && user.nextTo((Entity) base)) {
			teleportToSpouse((Player) user);
			return true;
		}
		return false;
	}

	/**
	 * Teleports the given player to his/her spouse, but only if the spouse is
	 * also wearing the wedding ring.
	 * 
	 * @param player
	 *            The ring's owner.
	 */
	private void teleportToSpouse(final Player player) {
		// check if pets and sheep are near
		if (!player.isZoneChangeAllowed()) {
			player.sendPrivateText("You were told to watch your pet, weren't you?");
			return;
		}

		final String spouseName = getInfoString();

		if (spouseName == null) {
			player.sendPrivateText("This wedding ring hasn't been engraved yet.");
			logger.debug(player.getName()
					+ "tried to use a wedding ring without a spouse name engraving.");
			return;
		}

		final Player spouse = SingletonRepository.getRuleProcessor().getPlayer(spouseName);
		if (spouse == null) {
			player.sendPrivateText(spouseName + " is not online.");
			return;
		}

		if (spouse.isEquipped("wedding ring")) { 
			// spouse is equipped with ring but could be divorced and
			// have another

			final Item weddingRing = spouse.getFirstEquipped("wedding ring");

			if (weddingRing.getInfoString() == null) { 
				// divorced with ring and engaged again
				player.sendPrivateText("Sorry, "
						+ spouseName
						+ " has divorced you and is now engaged to someone else.");
				return;
			} else if (!(weddingRing.getInfoString().equals(player.getName()))) { 
				// divorced and remarried
				player.sendPrivateText("Sorry, " + spouseName
						+ " has divorced you and is now remarried.");

				return;
			}

		} else {
			// This means trouble ;)
			player.sendPrivateText(spouseName
					+ " is not wearing the wedding ring.");
			return;
		}

		final StendhalRPZone sourceZone = player.getZone();
		if (!sourceZone.isTeleportOutAllowed()) {
			player.sendPrivateText("The strong anti magic aura in this area prevents the wedding ring from working!");
			return;
		}

		final StendhalRPZone destinationZone = spouse.getZone();
		if (!destinationZone.isTeleportInAllowed()) {
			player.sendPrivateText("The strong anti magic aura in the destination area prevents the wedding ring from working!");
			return;
		}

		final String zoneName =  destinationZone.getName();
		// check if player has visited zone before
		if (player.getKeyedSlot("!visited", zoneName) == null) {
			player.sendPrivateText("Although you have heard a lot of rumors about the destination, "
								+ "you cannot join  " + spouseName + " there because it is still an unknown place for you.");
			return;
		}

		final int x = spouse.getX();
		final int y = spouse.getY();
		final Direction dir = spouse.getDirection();

		player.teleport(destinationZone, x, y, dir, player);
	}

	@Override
	public String describe() {
		final String spouseName = getInfoString();

		if (spouseName != null) {
			return "You see a wedding ring. Its engraving says: \"In eternal love to "
					+ spouseName + "\".";
		} else {
			return "You see a wedding ring.";
		}
	}
}
