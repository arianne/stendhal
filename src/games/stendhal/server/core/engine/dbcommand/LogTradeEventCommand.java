/***************************************************************************
 *                 (C) Copyright 2013-2013 - Faiumoni e. V.                *
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

import java.io.IOException;
import java.sql.SQLException;

import games.stendhal.server.core.engine.db.StendhalItemDAO;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.command.AbstractDBCommand;
import marauroa.server.game.db.DAORegister;

/**
 * Logs a Harold trade event
 *
 * @author hendrik
 */
public class LogTradeEventCommand extends AbstractDBCommand {
	private String charname;
	private Item item;
	private int quantity;
	private int price;

	/**
	 * logs a trade event
	 *
	 * @param player Player
	 * @param item   Item
	 * @param quantity quantity
	 * @param price price
	 */
	public LogTradeEventCommand(Player player, Item item, int quantity, int price) {
		this.charname = player.getName();
		this.item = item;
		this.quantity = quantity;
		this.price = price;
	}

	@Override
	public void execute(DBTransaction transaction) throws SQLException, IOException {
		StendhalItemDAO stendhalItemDao = DAORegister.get().get(StendhalItemDAO.class);
		stendhalItemDao.itemLogAssignIDIfNotPresent(transaction, item, getEnqueueTime());

		StendhalWebsiteDAO stendhalWebsiteDao = DAORegister.get().get(StendhalWebsiteDAO.class);
		String description = item.describe();
		String stats = "";
		int start = description.indexOf("Stats are (");
		if(start > -1) {
			stats = description.substring(start);
		}
		stendhalWebsiteDao.logTradeEvent(transaction, charname, item.getName(), item.getInt(StendhalItemDAO.ATTR_ITEM_LOGID), quantity, price, stats, getEnqueueTime());
	}

}
