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
package games.stendhal.server.entity.npc.condition;

import static com.google.common.base.Preconditions.checkNotNull;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Can the player equip the specified item? (has enough space in his bag or other slots)
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerCanEquipItemCondition implements ChatCondition {

	private final String itemName;

	/**
	 * Creates a new PlayerCanEquipItemCondition
	 *
	 * @param itemName
	 *            name of item
	 */
	public PlayerCanEquipItemCondition(final String itemName) {
		this.itemName = checkNotNull(itemName);
	}


	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final Item item = SingletonRepository.getEntityManager().getItem(itemName);
		return player.getSlotToEquip(item) != null;
	}

	@Override
	public String toString() {
		return "player can equip item <" + itemName + ">";
	}

	@Override
	public int hashCode() {
		return 43787 * itemName.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerCanEquipItemCondition)) {
			return false;
		}
		PlayerCanEquipItemCondition other = (PlayerCanEquipItemCondition) obj;
		return itemName.equals(other.itemName);
	}
}
