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
package games.stendhal.tools.loganalyser.gameevents;

/**
 * represents an entry of the gameEvent table.
 *
 * @author hendrik
 */
public class GameEventEntry {
	private final String timestamp;
	private final String id;
	private final String source;
	private final String event;
	private final String param1;
	private final String param2;

	/**
	 * Creates a new GameEventEntry.
	 *
	 * @param id itemid id of the item
	 * @param timestamp timestamp
	 * @param source name of player
	 * @param event  name of event
	 * @param param1 additional param1
	 * @param param2 additional param2
	 */
	public GameEventEntry(final String id, final String timestamp,
			final String source, final String event, final String param1, final String param2) {
		this.id = id;
		this.timestamp = timestamp;
		this.source = source;
		this.event = event;
		this.param1 = param1;
		this.param2 = param2;
	}

	public String getEvent() {
		return event;
	}

	public String getParam1() {
		return param1;
	}

	public String getParam2() {
		return param2;
	}

	public String getSource() {
		return source;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(timestamp);
		sb.append('\t');
		sb.append(source);
		sb.append('\t');
		sb.append(event);
		sb.append('\t');
		sb.append(param1);
		sb.append('\t');
		sb.append(param2);

		return sb.toString();
	}

	public String getId() {
		return id;
	}
}
