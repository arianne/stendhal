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


/**
 * a game event for logging
 */
public class GameEvent {
	private final String source;
	private final String event;
	private final String[] params;

	/**
	 * creates a new GameEvent object
	 *
	 * @param source source of the event, usually a character
	 * @param event  name of event
	 * @param params paramter
	 */
	public GameEvent(final String source, final String event, final String... params) {
		this.source = source;
		this.event = event;
		this.params = params;
	}
	
	/**
	 * creates a new GameEvent object
	 *
	 * @param source source of the event, usually a character
	 * @param event  name of event
	 * @param params paramter
	 */
	public GameEvent(final String source, final String event, final List<String> params) {
		this.source = source;
		this.event = event;
		this.params = params.toArray(new String[params.size()]);
	}

	/**
	 * writes the envent to the database
	 */
	public void raise() {
		DBCommand command = new LogGameEventCommand(source, event, params);
		DBCommandQueue.get().enqueue(command);
	}
}
