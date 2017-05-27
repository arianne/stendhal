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
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Does the player carry the specified item?
 *
 * @see games.stendhal.server.entity.npc.action.DropItemAction
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerHasItemWithHimCondition implements ChatCondition {

	private final String itemName;
	private final int amount;

	/**
	 * Creates a new PlayerHasItemWithHim.
	 *
	 * @param itemName
	 *            name of item
	 */
	public PlayerHasItemWithHimCondition(final String itemName) {
		this.itemName = checkNotNull(itemName);
		this.amount = 1;
	}

	/**
	 * Creates a new PlayerHasItemWithHim.
	 *
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            required quantity of this item
	 */
	@Dev
	public PlayerHasItemWithHimCondition(final String itemName, @Dev(defaultValue="1") final int amount) {
		this.itemName = checkNotNull(itemName);
		this.amount = amount;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return player.isEquipped(itemName, amount);
	}

	@Override
	public String toString() {
		return "player has item <" + amount + " " + itemName + ">";
	}

	@Override
	public int hashCode() {
		return 43891 * itemName.hashCode() + amount;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasItemWithHimCondition)) {
			return false;
		}
		PlayerHasItemWithHimCondition other = (PlayerHasItemWithHimCondition) obj;
		return (amount == other.amount)
			&& itemName.equals(other.itemName);
	}
}
