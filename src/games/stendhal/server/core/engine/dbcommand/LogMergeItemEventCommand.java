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
 * logs merging of items into a stack
 *
 * @author hendrik
 */
public class LogMergeItemEventCommand extends AbstractLogItemEventCommand {
	private RPObject liveOldItem;
	private RPObject liveOutlivingItem;
	private RPObject frozenOldItem;
	private RPObject frozenOutlivingItem;
	private RPEntity player;

	/**
	 * logs merging of item stacks
	 *
	 * @param player   Player performing the merge
	 * @param oldItem  old item being destroyed during the merge
	 * @param outlivingItem item which survives the merge
	 */
	public LogMergeItemEventCommand(RPEntity player, RPObject oldItem, RPObject outlivingItem) {
		this.player = player;
		this.liveOldItem = oldItem;
		this.liveOutlivingItem = outlivingItem;
		this.frozenOldItem = (RPObject) oldItem.clone();
		this.frozenOutlivingItem = (RPObject) outlivingItem.clone();
	}

	@Override
	protected void log(DBTransaction transaction) throws SQLException {
		itemLogAssignIDIfNotPresent(transaction, liveOldItem);
		itemLogAssignIDIfNotPresent(transaction, liveOutlivingItem);

		final String oldQuantity = getQuantity(frozenOldItem);
		final String oldOutlivingQuantity = getQuantity(frozenOutlivingItem);
		final String newQuantity = Integer.toString(Integer.parseInt(oldQuantity) + Integer.parseInt(oldOutlivingQuantity));

		itemLogWriteEntry(transaction, liveOldItem.getInt(ATTR_ITEM_LOGID), player, "merge in", 
				liveOutlivingItem.get(ATTR_ITEM_LOGID), oldQuantity, 
				oldOutlivingQuantity, newQuantity);
		itemLogWriteEntry(transaction, liveOutlivingItem.getInt(ATTR_ITEM_LOGID), player, "merged in",
				liveOldItem.get(ATTR_ITEM_LOGID), oldOutlivingQuantity, 
				oldQuantity, newQuantity);
	}

}
