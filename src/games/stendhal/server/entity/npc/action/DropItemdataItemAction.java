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
 * Drops the specified item with the specified itemdata
 *
 * @see games.stendhal.server.entity.npc.condition.PlayerHasItemdataItemWithHimCondition
 */
@Dev(category=Category.ITEMS_OWNED, label="Item-")
public class DropItemdataItemAction implements ChatAction {
	private static Logger logger = Logger.getLogger(DropItemdataItemAction.class);
	private final String itemName;
	private final String itemdata;
	private final int amount;

	/**
	 * Creates a new DropItemdataItemAction.
	 *
	 * @param itemName
	 *            name of item
	 * @param itemdata
	 *            itemdata of the dropped item
	 */
	public DropItemdataItemAction(final String itemName, final String itemdata) {
		this.itemName = checkNotNull(itemName);
		this.amount = 1;
		this.itemdata = checkNotNull(itemdata);
	}

	/**
	 * Creates a new DropItemdataItemAction.
	 *
	 * @param itemName
	 *            name of item
	 * @param amount
	 *            amount of item
	 * @param itemdata
	 *            itemdata of the dropped item
	 */
	@Dev
	public DropItemdataItemAction(final String itemName, @Dev(defaultValue="1") final int amount, final String itemdata) {
		this.itemName = checkNotNull(itemName);
		this.amount = amount;
		this.itemdata = checkNotNull(itemdata);
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
		boolean res = false;
		if (player.isEquippedWithItemdata(itemName, itemdata, amount)) {
			res = player.dropWithItemdata(itemName, itemdata, amount);
		}

		if (!res) {
			logger.error("Cannot drop " + itemName, new Throwable());
		}
		player.notifyWorldAboutChanges();
	}

	@Override
	public String toString() {
		return "drop " + amount + " of item <" + itemName + "> with itemdata <" + itemdata + ">";
	}

	@Override
	public int hashCode() {
		return 5113 * (itemName.hashCode() + 5119 * (itemdata.hashCode() + 5147 * amount));
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof DropItemdataItemAction)) {
			return false;
		}
		DropItemdataItemAction other = (DropItemdataItemAction) obj;
		return (amount == other.amount)
			&& itemName.equals(other.itemName)
			&& itemdata.equals(other.itemdata);
	}

}
