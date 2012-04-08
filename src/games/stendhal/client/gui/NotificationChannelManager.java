/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import games.stendhal.client.gui.chatlog.EventLine;

/**
 * Container for NotificationChannels.
 */
public class NotificationChannelManager {
	/** All available channels */
	private final List<NotificationChannel> channels = new CopyOnWriteArrayList<NotificationChannel>();
	
	/**
	 * Add a new channel.
	 * 
	 * @param channel
	 */
	void addChannel(NotificationChannel channel) {
		channels.add(channel);
	}
	
	/**
	 * Get available channels.
	 * 
	 * @return list of available channels
	 */
	List<NotificationChannel> getChannels() {
		return channels;
	}
	
	/**
	 * Add an event line to be displayed at channels that should show events
	 * of the type of the EventLine.
	 *
	 * @param line
	 */
	void addEventLine(final EventLine line) {
		for (NotificationChannel channel : channels) {
			channel.addEventLine(line);
		}
	}
}
