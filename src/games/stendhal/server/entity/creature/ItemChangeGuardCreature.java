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
package games.stendhal.server.entity.creature;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Killer;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;

/**
 * An ItemChangeGuardCreature is a creature that is responsible for guarding a
 * special item (e.g. a key). Once it is killed, a copy of this special item is
 * given to the player who killed it in case he/she has had an other specified
 * item in the first place.
 */
public class ItemChangeGuardCreature extends Creature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(ItemChangeGuardCreature.class);

	private final String itemType;

	private final String oldItemType;

	/**
	 * Creates a ItemGuardCreature.
	 *
	 * @param copy
	 *            base creature
	 * @param oldItemType
	 *            the quest item the player has to have in order to gain the new
	 *            one
	 * @param itemType
	 *            the quest item to drop on death
	 */
	public ItemChangeGuardCreature(final Creature copy, final String oldItemType,
			final String itemType) {
		super(copy);
		this.itemType = itemType;
		this.oldItemType = oldItemType;

		if (!SingletonRepository.getEntityManager().isItem(
				itemType)) {
			logger.error(copy.getName() + " drops nonexistent item " + itemType);
		}
	}

	@Override
	public Creature getNewInstance() {
		return new ItemChangeGuardCreature(this, oldItemType, itemType);
	}

	@Override
	public void onDead(final Killer killer, final boolean remove) {
		if (killer instanceof RPEntity) {
			final RPEntity killerRPEntity = (RPEntity) killer;

			if (killerRPEntity.drop(oldItemType)) {
				final Item item = SingletonRepository.getEntityManager().getItem(
						itemType);

				killerRPEntity.equipOrPutOnGround(item);
			}
		}
		super.onDead(killer, remove);
	}
}
