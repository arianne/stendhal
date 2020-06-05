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
		StendhalItemDAO stendhalItemDAO = DAORegister.get().get(StendhalItemDAO.class);
		stendhalItemDAO.itemLogAssignIDIfNotPresent(transaction, liveItem, getEnqueueTime());
		stendhalItemDAO.itemLogAssignIDIfNotPresent(transaction, liveNewItem, getEnqueueTime());

		final String outlivingQuantity = getQuantity(frozenItem);
		final String newQuantity = getQuantity(frozenNewItem);
		final String oldQuantity = Integer.toString(Integer.parseInt(outlivingQuantity) + Integer.parseInt(newQuantity));
		stendhalItemDAO.itemLogWriteEntry(transaction, getEnqueueTime(), liveItem.getInt(StendhalItemDAO.ATTR_ITEM_LOGID), player, "split out",
				liveNewItem.get(StendhalItemDAO.ATTR_ITEM_LOGID), oldQuantity,
				outlivingQuantity, newQuantity);
		stendhalItemDAO.itemLogWriteEntry(transaction, getEnqueueTime(), liveNewItem.getInt(StendhalItemDAO.ATTR_ITEM_LOGID), player, "splitted out",
				liveItem.get(StendhalItemDAO.ATTR_ITEM_LOGID), oldQuantity,
				newQuantity, outlivingQuantity);

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
