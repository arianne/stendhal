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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Drops the specified item.
 *
 * @see games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition
 */
@Dev(category=Category.ITEMS_OWNED, label="Item-")
public class DropItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropItemAction.class);
	private final String itemName;
	private final int amount;

	/**
	 * Creates a new DropItemAction.
	 *
	 * @param itemName
	 *            name of item
	 */
	public DropItemAction(final String itemName) {
		this.itemName = checkNotNull(itemName);
		this.amount = 1;
	}

	/**
	 * Creates a new DropItemAction.
	 *
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            for StackableItems
	 */
	@Dev
	public DropItemAction(final String itemName, @Dev(defaultValue="1") final int amount) {
		this.itemName = checkNotNull(itemName);
		this.amount = amount;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		final boolean res = player.drop(itemName, amount);
		if (!res) {
			logger.error("Cannot drop " + amount + " " + itemName,
					new Throwable());
		}
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "drop item <" + amount + " " + itemName + ">";
	}

	@Override
	public int hashCode() {
		return 5153 * (itemName.hashCode() + 5167 * amount);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DropItemAction)) {
			return false;
		}
		DropItemAction other = (DropItemAction) obj;
		return (amount == other.amount)
			&& itemName.equals(other.itemName);
	}

}
