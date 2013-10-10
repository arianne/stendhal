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
package games.stendhal.server.entity.npc.action;

import static com.google.common.base.Preconditions.checkNotNull;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Drops the specified item with the specified infostring
 *
 * @see games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition
 */
@Dev(category=Category.ITEMS_OWNED, label="Item-")
public class DropInfostringItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropItemAction.class);
	private final String itemName;
	private final String infostring;
	private final int amount;

	/**
	 * Creates a new DropInfostringItemAction.
	 *
	 * @param itemName
	 *            name of item
	 * @param infostring
	 *            infostring of the dropped item
	 */
	public DropInfostringItemAction(final String itemName, final String infostring) {
		this.itemName = checkNotNull(itemName);
		this.amount = 1;
		this.infostring = checkNotNull(infostring);
	}

	/**
	 * Creates a new DropInfostringItemAction.
	 *
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            amount of item
	 * @param infostring
	 *            infostring of the dropped item
	 */
	@Dev
	public DropInfostringItemAction(final String itemName, @Dev(defaultValue="1") final int amount, final String infostring) {
		this.itemName = checkNotNull(itemName);
		this.amount = amount;
		this.infostring = checkNotNull(infostring);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		final List<Item> items = player.getAllEquipped(itemName);
		boolean res = false;
		for (final Item item : items) {
			if (infostring.equalsIgnoreCase(item.getInfoString())) {
				res = player.drop(item.getName(), amount);
				break;
			}
		}

		if (!res) {
			logger.error("Cannot drop " + itemName, new Throwable());
		}
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "drop " + amount + " of item <" + itemName + "> with infostring <" + infostring + ">";
	}

	@Override
	public int hashCode() {
		return 5113 * (itemName.hashCode() + 5119 * (infostring.hashCode() + 5147 * amount));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DropInfostringItemAction)) {
			return false;
		}
		DropInfostringItemAction other = (DropInfostringItemAction) obj;
		return (amount == other.amount)
			&& itemName.equals(other.itemName)
			&& infostring.equals(other.infostring);
	}

}
