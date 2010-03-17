/***************************************************************************
 *                    (C) Copyright 2007-2010 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine.dbcommand;

import games.stendhal.server.entity.RPEntity;

import java.sql.SQLException;

import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;

/**
 * logs splitting off items from a stack.
 *
 * @author hendrik
 */
public class LogSplitItemEventCommand extends AbstractLogItemEventCommand {
	private RPObject liveItem;
	private RPObject liveNewItem;
	private RPObject frozenItem;
	private RPObject frozenNewItem;
	private RPEntity player;

	/**
	 * logs merging of item stacks
	 *
	 * @param player   Player performing the merge
	 * @param item     item to split out from
	 * @param newItem  new item created out of the old stack
	 */
	public LogSplitItemEventCommand(RPEntity player, RPObject item, RPObject newItem) {
		this.player = player;
		this.liveItem = item;
		this.liveNewItem = newItem;
		this.frozenItem = (RPObject) item.clone();
		this.frozenNewItem = (RPObject) newItem.clone();
	}

	@Override
	protected void log(DBTransaction transaction) throws SQLException {
		itemLogAssignIDIfNotPresent(transaction, liveItem);
		itemLogAssignIDIfNotPresent(transaction, liveNewItem);

		final String outlivingQuantity = getQuantity(frozenItem);
		final String newQuantity = getQuantity(frozenNewItem);
		final String oldQuantity = Integer.toString(Integer.parseInt(outlivingQuantity) + Integer.parseInt(newQuantity));
		itemLogWriteEntry(transaction, liveItem.getInt(ATTR_ITEM_LOGID), player, "split out",
				liveNewItem.get(ATTR_ITEM_LOGID), oldQuantity,
				outlivingQuantity, newQuantity);
		itemLogWriteEntry(transaction, liveNewItem.getInt(ATTR_ITEM_LOGID), player, "splitted out",
				liveItem.get(ATTR_ITEM_LOGID), oldQuantity,
				newQuantity, outlivingQuantity);

	}

}
