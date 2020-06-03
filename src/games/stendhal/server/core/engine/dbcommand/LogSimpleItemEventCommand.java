/***************************************************************************
 *                    (C) Copyright 2007-2013 - Stendhal                   *
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
 * logs a simple item event
 *
 * @author hendrik
 */
public class LogSimpleItemEventCommand extends AbstractLogItemEventCommand {

	private RPObject item;
	private RPEntity player;
	private String event;
	private String param1;
	private String param2;
	private String param3;
	private String param4;

	/**
	 * creates a simple item log command
	 *
	 * @param item item
	 * @param player player object
	 * @param event  name of event
	 * @param param1 param 1
	 * @param param2 param 2
	 * @param param3 param 3
	 * @param param4 param 4
	 */
	public LogSimpleItemEventCommand(final RPObject item, final RPEntity player, final String event,
			final String param1, final String param2, final String param3, final String param4) {
		this.item = item;
		this.player = player;
		this.event = event;
		this.param1 = param1;
		this.param2 = param2;
		this.param3 = param3;
		this.param4 = param4;
	}


	@Override
	protected void log(final DBTransaction transaction) throws SQLException {
		// don't log the destruction of items that have not been logged prior.
		if (event.equals("destroy") && !item.has("logid")) {
			return;
		}
		StendhalItemDAO stendhalItemDAO = DAORegister.get().get(StendhalItemDAO.class);
		stendhalItemDAO.itemLogAssignIDIfNotPresent(transaction, item, getEnqueueTime());
		stendhalItemDAO.itemLogWriteEntry(transaction, getEnqueueTime(), item, player, event, param1, param2, param3, param4);
	}

	/**
	 * returns a string suitable for debug output of this DBCommand.
	 *
	 * @return debug string
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("player", player).add("event", event).toString();
	}
}
