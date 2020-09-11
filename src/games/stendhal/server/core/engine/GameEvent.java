/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
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
	 * @param params parameters
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
	 * @param params parameters
	 */
	public GameEvent(final String source, final String event, final List<String> params) {
		this.source = source;
		this.event = event;
		this.params = params.toArray(new String[params.size()]);
	}

	/**
	 * writes the event to the database
	 */
	public void raise() {
		StendhalRPRuleProcessor.get().logGameEvent(this);
	}

	public String[] getParams() {
		return params;
	}

	public String getSource() {
		return source;
	}

	public String getEvent() {
		return event;
	}
}
