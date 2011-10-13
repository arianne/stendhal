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
package games.stendhal.server.core.engine;

import java.util.List;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.dbcommand.LogGameEventCommand;


public class GameEvent {
	public final String source;
	public final String event;
	public final String[] params;

	public GameEvent(final String source, final String event, final String... params) {
		this.source = source;
		this.event = event;
		this.params = params;
	}
	
	public GameEvent(final String source, final String event, final List<String> params) {
		this.source = source;
		this.event = event;
		String[] a= new String[0];
		this.params = params.toArray(a);
	}

	public void raise() {
		DBCommand command = new LogGameEventCommand(source, event, params);
		DBCommandQueue.get().enqueue(command);
	}
}
