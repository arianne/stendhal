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

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.UpdateConverter;

import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * An ItemGuardCreature is a creature that is responsible for guarding a special
 * item (e.g. a key). Once it is killed, a copy of this special item is given to
 * the player who killed it.
 */
public class ItemGuardCreature extends Creature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(ItemGuardCreature.class);

	private String itemType;

	/**
	 * Creates an ItemGuardCreature.
	 * 
	 * @param copy
	 *            base creature
	 * @param itemType
	 *            the quest item to drop on death
	 */
	public ItemGuardCreature(Creature copy, String itemType) {
		super(copy);

		// replace underscores by spaces, if still present (should no more be the case)
		itemType = UpdateConverter.transformItemName(itemType);

		this.itemType = itemType;

		noises = new LinkedList<String>(noises);
		noises.add("Thou shall not obtain the " + itemType + "!");

		if (!StendhalRPWorld.get().getRuleManager().getEntityManager().isItem(
				itemType)) {
			logger.error(copy.getName() + " drops unexisting item " + itemType);
		}
	}

	@Override
	public Creature getInstance() {
		return new ItemGuardCreature(this, itemType);
	}

	@Override
	public void onDead(Entity killer) {
		if (killer instanceof RPEntity) {
			RPEntity killerRPEntity = (RPEntity) killer;
			if (!killerRPEntity.isEquipped(itemType)) {
				Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
						itemType);
				item.setBoundTo(killerRPEntity.getName());
				killerRPEntity.equip(item, true);
			}
		}
		super.onDead(killer);
	}
}
