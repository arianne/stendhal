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
package games.stendhal.tools.loganalyser.itemlog.consistency;


/**
 * represents an entry of the itemlog table.
 *
 * @author hendrik
 */
public class LogEntry {
	private final String timestamp;
	private final String id;
	private final String itemid;
	private final String source;
	private final String event;
	private final String param1;
	private final String param2;
	private final String param3;
	private final String param4;

	/**
	 * Creates a new LogEntry.
	 *
	 * @param id
	 * @param timestamp timestamp
	 * @param itemid id of the item
	 * @param source name of player
	 * @param event  name of event
	 * @param param1 additional param1
	 * @param param2 additional param2
	 * @param param3 additional param3
	 * @param param4 additional param4
	 */
	public LogEntry(final String id, final String timestamp, final String itemid, final String source, final String event, final String param1, final String param2, final String param3, final String param4) {
		this.id = id;
	    this.timestamp = timestamp;
	    this.itemid = itemid;
	    this.source = source;
	    this.event = event;
	    this.param1 = param1;
	    this.param2 = param2;
	    this.param3 = param3;
	    this.param4 = param4;
    }

	public String getItemid() {
		return itemid;
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

    public String getParam3() {
    	return param3;
    }

    public String getParam4() {
    	return param4;
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
    	sb.append(itemid);
    	sb.append('\t');
    	sb.append(source);
    	sb.append('\t');
    	sb.append(event);
    	sb.append('\t');
    	sb.append(param1);
    	sb.append('\t');
    	sb.append(param2);
    	sb.append('\t');
    	sb.append(param3);
    	sb.append('\t');
    	sb.append(param4);

    	return sb.toString();
    }

	public String getId() {
		return id;
	}
}
