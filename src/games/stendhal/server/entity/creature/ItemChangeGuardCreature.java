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

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Item;

import org.apache.log4j.Logger;

/**
 * An ItemChangeGuardCreature is a creature that is responsible for guarding a
 * special item (e.g. a key). Once it is killed, a copy of this special item
 * is given to the player who killed it in place of another specified
 * item which the killer must have equipped.
 */
public class ItemChangeGuardCreature extends Creature {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(ItemChangeGuardCreature.class);

	private String itemType;

	private String oldItemType;

	/**
	 * Creates a ItemGuardCreature
	 * @param copy      base creature
	 * @param oldItemType the quest item the player has to have in order to gain the new one
	 * @param itemType  the quest item to drop on death
	 */
	public ItemChangeGuardCreature(Creature copy, String oldItemType, String itemType) {
		super(copy);
		this.itemType = itemType;
		this.oldItemType = oldItemType;

		if (!StendhalRPWorld.get().getRuleManager().getEntityManager().isItem(itemType)) {
			logger.error(copy.getName() + " drops nonexistent item " + itemType);
		}
	}

	@Override
	public Creature getInstance() {
		return new ItemChangeGuardCreature(this, oldItemType, itemType);
	}

	@Override
	public void onDead(Entity killer) {
		if (killer instanceof RPEntity) {
			RPEntity killerRPEntity = (RPEntity) killer;
			if (killerRPEntity.drop(oldItemType)) {
				Item item = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(itemType);
				killerRPEntity.equip(item, true);
			}
		}
		super.onDead(killer);
	}
}
