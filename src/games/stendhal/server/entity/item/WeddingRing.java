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
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

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
	/** The cooling period of players of same level in seconds */ 
	private static final long MIN_COOLING_PERIOD = 5 * 60;
	
	private static final String LAST_USE = "amount";

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
		setPersistent(true);
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
	 * Get the last use time in seconds
	 * @return last use time
	 */
	private int getLastUsed() {
		if (has(LAST_USE)) {
			return getInt(LAST_USE);
		} else {
			return -1;
		}
	}
	
	/**
	 * Store current system time as the last used
	 */
	private void storeLastUsed() {
		put(LAST_USE, (int) (System.currentTimeMillis() / 1000));
	}
	
	/**
	 * Get the required cooling period for wedding ring use between players
	 * @param player1 either player using the ring or the spouse 
	 * @param player2 either player using the ring or the spouse
	 * @return Required cooling time
	 */
	private int getCoolingPeriod(final Player player1, final Player player2) {
		final int level1 = player1.getLevel();
		final int level2 = player2.getLevel();
		double levelRatio = (Math.max(level1, level2) + 1.0) / (Math.min(level1, level2) + 1.0);
		
		return (int) (MIN_COOLING_PERIOD * levelRatio * levelRatio);
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

		final int secondsNeeded = getLastUsed() + getCoolingPeriod(player, spouse) - (int) (System.currentTimeMillis() / 1000);
		if (secondsNeeded > 0) {
			player.sendPrivateText("The ring has not yet regained its power. You think it might be ready in " 
					+ TimeUtil.approxTimeUntil(secondsNeeded) + ".");
			
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
								+ "you cannot join " + spouseName + " there because it is still an unknown place for you.");
			return;
		}

		final int x = spouse.getX();
		final int y = spouse.getY();
		final Direction dir = spouse.getDirection();

		if (player.teleport(destinationZone, x, y, dir, player)) {
			storeLastUsed();
		}
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
	
	// Check if there are more rings in the slot where this ring was added
	@Override
	public void setContainer(RPObject container, RPSlot slot) {
		WeddingRing oldRing = null; 
		if (slot != null) {
			for (RPObject object : slot) {
				if ((object instanceof WeddingRing) && (!getID().equals(object.getID()))) {
					oldRing = (WeddingRing) object;
					break;
				}
			}
		}
	
		if (oldRing != null) {
			// The player is cheating with multiple rings. Explode the 
			// old ring, and use up the energy of this one
			destroyRing(oldRing);
			storeLastUsed();
		}
		
		super.setContainer(container, slot);
	}
	
	/**
	 * Destroy a wedding ring.
	 * To be used when a ring is put in a same slot with another.
	 * 
	 * @param rings the ring to be destroyed
	 */
	private void destroyRing(WeddingRing ring) {
		// The players need to be told first, while the ring still
		// exist in the world
		informNearbyPlayers(ring);
		ring.removeFromWorld();
		logger.info("Destroyed a wedding ring: " + ring);
	}
	
	/**
	 * Give a nice message to nearby players when rings get destroyed.
	 */
	private void informNearbyPlayers(WeddingRing ring) {
		try {
			Entity container = (Entity) ring.getBaseContainer();
			StendhalRPZone zone = getZone();
			
			if (zone != null) {
				for (Player player : zone.getPlayers()) {
					if (player.nextTo(container)) {
						player.sendPrivateText(NotificationType.SCENE_SETTING, 
						"There's a flash of light when a wedding ring disintegrates in a magical conflict.");
					}
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
