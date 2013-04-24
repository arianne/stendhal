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
package games.stendhal.server.entity.mapstuff.chest;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.PersonalChestSlot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * A PersonalChest is a Chest that can be used by everyone, but shows different
 * contents depending on the player who is currently using it. Thus, a player
 * can put in items into this chest and be sure that nobody else will be able to
 * take them out.
 * <p>
 * Caution: each PersonalChest must be placed in such a way that only one player
 * can stand next to it at a time, to prevent other players from stealing while
 * the owner is looking at his items. 
 */
public class PersonalChest extends Chest {
	
	/**
	 * The default bank slot name.
	 */
	public static final String DEFAULT_BANK = "bank";

	private static Logger LOGGER = Logger.getLogger(PersonalChest.class);

	
	private RPEntity attending;

	private final String bankName;

	private SyncContent chestSynchronizer;

	/**
	 * Create a personal chest using the default bank slot.
	 */
	public PersonalChest() {
		this(DEFAULT_BANK);
	}

	/**
	 * Create a personal chest using a specific bank slot.
	 * 
	 * @param bankName
	 *            The name of the bank slot.
	 */
	public PersonalChest(final String bankName) {
		this.bankName = bankName;
		attending = null;

		super.removeSlot("content");
		super.addSlot(new PersonalChestSlot(this));
	}

	/**
	 * Gets the entitiy which is currently served by this chest.
	 *
	 * @return Entity
	 */
	public RPEntity getAttending() {
		return attending;
	}

	/**
	 * Copies an item.
	 * 
	 * 
	 * @param item
	 *            item to copy
	 * @return copy
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private RPObject cloneItem(final RPObject item) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		final Class< ? > clazz = item.getClass();
		final Constructor< ? > ctor = clazz.getConstructor(clazz);
		final Item clone = (Item) ctor.newInstance(item);
		return clone;
	}

	/**
	 * Get the slot that holds items for this chest.
	 * 
	 * @return A per-player/per-bank slot.
	 */
	public RPSlot getBankSlot() {
		if (attending == null) {
			LOGGER.error("Calling getBankSlot on non-attending PersonalChest " + this, new Throwable());
			return null;
		}
		return attending.getSlot(bankName);
	}

	/**
	 * Sync the slot contents.
	 * 
	 * @return <code>true</code> if it should be called again.
	 */
	protected boolean syncContent() {
		if (attending != null) {
			/* Can be replaced when we add Equip event */
			/* Mirror chest content into player's bank slot */
			final RPSlot bank = getBankSlot();
			bank.clear();

			for (final RPObject item : getSlot("content")) {
				try {
					bank.addPreservingId(cloneItem(item));
				} catch (final Exception e) {
					LOGGER.error("Cannot clone item " + item, e);
				}
			}

			// Verify the user is next to the chest
			if (getZone().has(attending.getID()) && nextTo(attending)) {
				return true;
			} else {
				// If player is not next to depot, close it (also clears it).
				close();
				notifyWorldAboutChanges();
			}
		}

		return false;
	}

	/**
	 * Open the chest for an attending user.
	 * 
	 * @param user
	 *            The attending user.
	 */
	public void open(final RPEntity user) {
		attending = user;

		chestSynchronizer = new SyncContent();
		SingletonRepository.getTurnNotifier().notifyInTurns(0, chestSynchronizer);

		final RPSlot content = getSlot("content");
		content.clear();

		for (final RPObject item : getBankSlot()) {
			try {
				content.addPreservingId(cloneItem(item));
			} catch (final Exception e) {
				LOGGER.error("Cannot clone item " + item, e);
			}
		}

		super.open();
	}

	/**
	 * Close the chest.
	 */
	@Override
	public void close() {
		super.close();

		getSlot("content").clear();
		attending = null;

	}

	/**
	 * Don't let this be called directly for personal chests.
	 */
	@Override
	public void open() {
		throw new RuntimeException("User context required to open");
	}

	@Override
	public boolean onUsed(final RPEntity user) {
		if (user.nextTo(this)) {
			if (isOpen()) {
				close();
			} else {
				open(user);
			}

			notifyWorldAboutChanges();
			return true;
		}
		if (user instanceof Player) {
			final Player player = (Player) user;
			player.sendPrivateText("You cannot reach the chest from there.");
		}
		return false;
	}

	@Override
    public String getDescriptionName(final boolean definite) {
	    return Grammar.article_noun(bankName + " chest", definite);
    }

	/**
	 * A listener for syncing the slot contents.
	 */
	protected class SyncContent implements TurnListener {
		/**
		 * This method is called when the turn number is reached.
		 * 
		 * @param currentTurn
		 *            The current turn number.
		 */
		@Override
		public void onTurnReached(final int currentTurn) {
			if (syncContent()) {
				SingletonRepository.getTurnNotifier().notifyInTurns(0, this);
			}
		}
	}
	
	@Override
	public void onRemoved(final StendhalRPZone zone) {
		SingletonRepository.getTurnNotifier().dontNotify(chestSynchronizer);

		super.onRemoved(zone);
	}
}
