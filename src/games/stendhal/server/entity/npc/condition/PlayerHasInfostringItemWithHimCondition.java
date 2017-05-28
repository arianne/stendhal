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

import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Does the player carry the specified item with the specified infostring?
 *
 * @see games.stendhal.server.entity.npc.action.DropInfostringItemAction
 */
@Dev(category=Category.ITEMS_OWNED, label="Item?")
public class PlayerHasInfostringItemWithHimCondition implements ChatCondition {

	private final String itemName;
	private final String infostring;

	/**
	 * Creates a new PlayerHasInfostringItemWithHimCondition.
	 *
	 * @param itemName
	 *            name of item
     * @param infostring
	 *            infostring to check
	 */
	public PlayerHasInfostringItemWithHimCondition(final String itemName, final String infostring) {
		this.itemName = checkNotNull(itemName);
		this.infostring = checkNotNull(infostring);
	}

	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final List<Item> items = player.getAllEquipped(itemName);
		for (final Item item : items) {
			if (infostring.equalsIgnoreCase(item.getInfoString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "player has item <" + itemName + "> with infostring <" + infostring + ">";
	}

	@Override
	public int hashCode() {
		return 43867 * itemName.hashCode() + infostring.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof PlayerHasInfostringItemWithHimCondition)) {
			return false;
		}
		PlayerHasInfostringItemWithHimCondition other = (PlayerHasInfostringItemWithHimCondition) obj;
		return itemName.equals(other.itemName)
			&& infostring.equals(other.infostring);
	}
}
