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
		itemLogAssignIDIfNotPresent(transaction, item);
		itemLogWriteEntry(transaction, item, player, event, param1, param2, param3, param4);
	}

}
