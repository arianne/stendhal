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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Does the player owns a item (including the bank)?
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerOwnsItemIncludingBankCondition implements ChatCondition {

	private final String itemName;
	private final int amount;

	/**
	 * Creates a new PlayerOwnsItemIncludingBankCondition.
	 *
	 * @param itemName
	 *            name of item
	 */
	public PlayerOwnsItemIncludingBankCondition(final String itemName) {
		this.itemName = itemName;
		this.amount = 1;
	}

	/**
	 * Creates a new PlayerOwnsItemIncludingBankCondition.
	 *
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            required amount
	 */
	@Dev
	public PlayerOwnsItemIncludingBankCondition(final String itemName, @Dev(defaultValue="1") final int amount) {
		this.itemName = itemName;
		this.amount = amount;
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return playerOwnsItemsInAnySlot(player, itemName, amount);
	}

	private static boolean playerOwnsItemsInAnySlot(final Player player, String name, int amount) {
		if (amount <= 0) {
			return false;
		}
		int found = 0;
		Iterator<RPSlot> itr = player.slotsIterator();
		while (itr.hasNext()) {
			RPSlot slot = itr.next();

			for (final RPObject object : slot) {
				if (!(object instanceof Item)) {
					continue;
				}

				final Item item = (Item) object;

				if (item.getName().equals(name)) {
					found += item.getQuantity();

					if (found >= amount) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "player owns item <" + amount + " " + itemName + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerOwnsItemIncludingBankCondition.class);
	}
}
