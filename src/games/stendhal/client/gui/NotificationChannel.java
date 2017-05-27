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

import java.util.EnumSet;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.NotificationType;

/**
 * A chat log container that allows filtering by event type.
 */
class NotificationChannel {
	private static final Logger logger = Logger.getLogger(NotificationChannel.class);

	/** Name of the channel. */
	private final String name;
	/** Chat log where to write allowed content. */
	private final KTextEdit channel;
	/** Event types that should be displayed at the channel. */
	private final Set<NotificationType> eventTypes;

	/**
	 * Create a new NotificationChannel.
	 *
	 * @param channelName name of the channel
	 * @param channel text area for showing the event log
	 * @param blackList if <code>true</code>, the channel will default to
	 *	showing everything that has not been explicitly blacklisted. Otherwise
	 *	it'll show only whitelisted content. The main channel should default
	 *	to blacklisting, as it should show types that have been added in new
	 *	game versions
	 * @param defaultTypes default value of the saved notification type list
	 * 	(white- or blacklist depending on the value of <code>showUnknown</code>)
	 */
	NotificationChannel(String channelName, KTextEdit channel,
			boolean blackList, String defaultTypes) {
		name = channelName;
		this.channel = channel;
		if (blackList) {
			eventTypes = EnumSet.allOf(NotificationType.class);
		} else {
			eventTypes = EnumSet.noneOf(NotificationType.class);
		}

		// Load
		WtWindowManager wm = WtWindowManager.getInstance();
		String value = wm.getProperty("ui.channel." + name, defaultTypes);
		for (String typeString : value.split(",")) {
			/*
			 * String.split is unfortunately unable to return empty arrays when
			 * applied on empty string. Work around it.
			 */
			if ("".equals(typeString)) {
				continue;
			}
			try {
				NotificationType type = NotificationType.valueOf(typeString);
				setTypeFiltering(type, !blackList);
			} catch (RuntimeException e) {
				logger.error("Unrecognized notification type '" + typeString + "'", e);
			}
		}
	}

	/**
	 * Get the name of the channel.
	 *
	 * @return channel name
	 */
	String getName() {
		return name;
	}

	/**
	 * Set filtering of a notification type.
	 *
	 * @param type
	 * @param allow if <code>true</code> then messages of the type are
	 * 	displayed, otherwise not
	 */
	final void setTypeFiltering(NotificationType type, boolean allow) {
		if (allow) {
			eventTypes.add(type);
		} else {
			eventTypes.remove(type);
		}
	}

	/**
	 * Add an event line to the channel, if it's of type that should be
	 * displayed.
	 *
	 * @param line
	 * @return <code>true</code> if the channel accepted the message,
	 * 	<code>false</code> otherwise
	 */
	boolean addEventLine(final EventLine line) {
		if (eventTypes.contains(line.getType())) {
			channel.addLine(line);
			return true;
		}
		return false;
	}

	/**
	 * Clear the channel log.
	 */
	void clear() {
		channel.clear();
	}
}
