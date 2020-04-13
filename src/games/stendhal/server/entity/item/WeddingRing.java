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

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.ItemLogger;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TeleportNotifier;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.SlotOwner;

/**
 * A special ring that allows the owner to teleport to his or her spouse. The
 * spouse's name is engraved into the ring. Technically, the name is stored in
 * the item's infostring.
 *
 * Wedding rings should always be bound to the owner.
 *
 * @author daniel
 */
public class WeddingRing extends Item {
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
		final RPObject base = getBaseContainer();

		if ((user instanceof Player) && user.nextTo((Entity) base)) {
			return teleportToSpouse((Player) user);
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
		final double levelRatio = (Math.max(level1, level2) + 1.0) / (Math.min(level1, level2) + 1.0);

		return (int) (MIN_COOLING_PERIOD * levelRatio * levelRatio);
	}

	/**
	 * Teleports the given player to his/her spouse, but only if the spouse is
	 * also wearing the wedding ring.
	 *
	 * @param player
	 *            The ring's owner.
	 * @return <code>true</code> if the teleport was successful, otherwise
	 * 	<code>false</code>
	 */
	private boolean teleportToSpouse(final Player player) {
		// don't allow use if on the ground
		if (!isContained()) {
			player.sendPrivateText("You should pick the wedding ring up to use it.");
			return false;
		}

		// check if pets and sheep are near
		if (!player.isZoneChangeAllowed()) {
			player.sendPrivateText("You were told to watch your pet, weren't you?");
			return false;
		}

		final String spouseName = getInfoString();

		if (spouseName == null) {
			player.sendPrivateText("This wedding ring hasn't been engraved yet.");
			logger.debug(player.getName()
					+ "tried to use a wedding ring without a spouse name engraving.");
			return false;
		}

		final Player spouse = SingletonRepository.getRuleProcessor().getPlayer(spouseName);
		if (spouse == null) {
			player.sendPrivateText(spouseName + " is not online.");
			return false;
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
				return false;
			} else if (!(weddingRing.getInfoString().equals(player.getName()))) {
				// divorced and remarried
				player.sendPrivateText("Sorry, " + spouseName
						+ " has divorced you and is now remarried.");

				return false;
			}

		} else {
			// This means trouble ;)
			player.sendPrivateText(spouseName
					+ " is not wearing the wedding ring.");
			return false;
		}

		final int secondsNeeded = getLastUsed() + getCoolingPeriod(player, spouse) - (int) (System.currentTimeMillis() / 1000);
		if (secondsNeeded > 0) {
			player.sendPrivateText("The ring has not yet regained its power. You think it might be ready in "
					+ TimeUtil.approxTimeUntil(secondsNeeded) + ".");

			return false;
		}

		final StendhalRPZone sourceZone = player.getZone();
		if (!sourceZone.isTeleportOutAllowed(player.getX(), player.getY())) {
			player.sendPrivateText("The strong anti magic aura in this area prevents the wedding ring from working!");
			return false;
		}

		final StendhalRPZone destinationZone = spouse.getZone();
		final int x = spouse.getX();
		final int y = spouse.getY();
		if (!destinationZone.isTeleportInAllowed(x, y)) {
			player.sendPrivateText("The strong anti magic aura in the destination area prevents the wedding ring from working!");
			return false;
		}

		final String zoneName =  destinationZone.getName();
		// check if player has visited zone before
		if (player.getKeyedSlot("!visited", zoneName) == null) {
			player.sendPrivateText("Although you have heard a lot of rumors about the destination, "
								+ "you cannot join " + spouseName + " there because it is still an unknown place for you.");
			return false;
		}

		final Direction dir = spouse.getDirection();

		if (player.teleport(destinationZone, x, y, dir, player)) {
			TeleportNotifier.get().notify(player, true);
			storeLastUsed();
			return true;
		}

		return false;
	}

	@Override
	public String describe() {
		final String spouseName = getInfoString();

		if (spouseName != null) {
			return "You see a §'wedding ring'. Its engraving says: \"In eternal love to "
					+ spouseName + "\".";
		} else {
			return "You see a §'wedding ring'.";
		}
	}

	// Check if there are more rings in the slot where this ring was added
	@Override
	public void setContainer(final SlotOwner container, final RPSlot slot) {
		WeddingRing oldRing = null;
		// only bound rings destroy others
		if ((slot != null) && (getBoundTo() != null)) {
			for (final RPObject object : slot) {
				if ((object instanceof WeddingRing)
						&& (!getID().equals(object.getID()))) {
					final WeddingRing ring = (WeddingRing) object;
					if (getBoundTo().equals(ring.getBoundTo())) {
						oldRing = (WeddingRing) object;
						break;
					}
				}
			}
		}

		if (oldRing != null) {
			// The player is cheating with multiple rings. Explode the
			// old ring, and use up the energy of this one
			destroyRing(container, oldRing, slot);
			storeLastUsed();
		}

		super.setContainer(container, slot);
	}

	/**
	 * Destroy a wedding ring.
	 * To be used when a ring is put in a same slot with another.
	 *
	 * @param container
	 * @param ring the ring to be destroyed
	 * @param slot the slot holding the ring
	 */
	private void destroyRing(SlotOwner container, final WeddingRing ring, final RPSlot slot) {
		// The players need to be told first, while the ring still
		// exist in the world
		informNearbyPlayers(ring);

		RPEntity player = null;
		if (container instanceof RPEntity) {
			player = (RPEntity) container;
		}

		new ItemLogger().destroy(player, slot, ring, "another ring");
		ring.removeFromWorld();
		logger.info("Destroyed a wedding ring: " + ring);
	}

	/**
	 * Give a nice message to nearby players when rings get destroyed.
	 *
	 * @param ring the ring that got destroyed
	 */
	private void informNearbyPlayers(final WeddingRing ring) {
		try {
			final Entity container = (Entity) ring.getBaseContainer();
			final StendhalRPZone zone = getZone();

			if (zone != null) {
				for (final Player player : zone.getPlayers()) {
					if (player.nextTo(container)) {
						player.sendPrivateText(NotificationType.SCENE_SETTING,
						"There's a flash of light when a wedding ring disintegrates in a magical conflict.");
					}
				}
			}
		} catch (final Exception e) {
			logger.error(e);
		}
	}
}
