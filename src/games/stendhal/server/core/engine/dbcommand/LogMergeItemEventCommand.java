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

import java.sql.SQLException;

import com.google.common.base.MoreObjects;

import games.stendhal.server.core.engine.db.StendhalItemDAO;
import games.stendhal.server.entity.RPEntity;
import marauroa.common.game.RPObject;
import marauroa.server.db.DBTransaction;
import marauroa.server.game.db.DAORegister;

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
		StendhalItemDAO stendhalItemDAO = DAORegister.get().get(StendhalItemDAO.class);
		stendhalItemDAO.itemLogAssignIDIfNotPresent(transaction, liveOldItem, getEnqueueTime());
		stendhalItemDAO.itemLogAssignIDIfNotPresent(transaction, liveOutlivingItem, getEnqueueTime());

		final String oldQuantity = getQuantity(frozenOldItem);
		final String oldOutlivingQuantity = getQuantity(frozenOutlivingItem);
		final String newQuantity = Integer.toString(Integer.parseInt(oldQuantity) + Integer.parseInt(oldOutlivingQuantity));

		stendhalItemDAO.itemLogWriteEntry(transaction, getEnqueueTime(), liveOldItem.getInt(StendhalItemDAO.ATTR_ITEM_LOGID), player, "merge in",
				liveOutlivingItem.get(StendhalItemDAO.ATTR_ITEM_LOGID), oldQuantity,
				oldOutlivingQuantity, newQuantity);
		stendhalItemDAO.itemLogWriteEntry(transaction, getEnqueueTime(), liveOutlivingItem.getInt(StendhalItemDAO.ATTR_ITEM_LOGID), player, "merged in",
				liveOldItem.get(StendhalItemDAO.ATTR_ITEM_LOGID), oldOutlivingQuantity,
				oldQuantity, newQuantity);
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("player", player).toString();
	}
}
