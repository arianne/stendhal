/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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

import java.util.List;
import java.util.Objects;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Does the player carry the specified item with the specified description.
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerHasDescriptionItemWithHimCondition implements ChatCondition {

	private final String itemName;
	private final String itemDescr;

	/**
	 * Creates a new PlayerHasDescriptionItemWithHimCondition.
	 *
	 * @param itemName
	 * 			name of item
	 * @param itemDescr
	 * 			description to check
	 */
	public PlayerHasDescriptionItemWithHimCondition(final String itemName, final String itemDescr) {
		this.itemName = checkNotNull(itemName);
		this.itemDescr = checkNotNull(itemDescr);
	}

	@Override
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		final List<Item> items = player.getAllEquipped(itemName);
		for (final Item item : items) {
			if (itemName.equals(item.getName()) && itemDescr.equals(item.getDescription())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "player has item <" + itemName + "> with description <" + itemDescr + ">";
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemName, itemDescr);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasDescriptionItemWithHimCondition)) {
			return false;
		}
		PlayerHasDescriptionItemWithHimCondition other = (PlayerHasDescriptionItemWithHimCondition) obj;
		return itemName.equals(other.itemName)
			&& itemDescr.equals(other.itemDescr);
	}
}
