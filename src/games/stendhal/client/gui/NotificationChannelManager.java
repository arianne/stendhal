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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.common.NotificationType;

/**
 * Container for NotificationChannels.
 */
class NotificationChannelManager {
	/** All available channels. */
	private final List<NotificationChannel> channels = new ArrayList<NotificationChannel>();
	/** Channel listeners. */
	private final List<HiddenChannelListener> listeners = new CopyOnWriteArrayList<HiddenChannelListener>();
	/** The channel with the currently visible log. */
	private NotificationChannel visibleChannel;

	/**
	 * Add a new channel.
	 *
	 * @param channel
	 */
	void addChannel(NotificationChannel channel) {
		channels.add(channel);
	}

	/**
	 * Add a listener for following changes on hidden channel logs.
	 *
	 * @param listener
	 */
	void addHiddenChannelListener(HiddenChannelListener listener) {
		listeners.add(listener);
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
		// Pass client messages only to the visible channel
		if (line.getType() == NotificationType.CLIENT) {
			visibleChannel.addEventLine(line);
		} else {
			// Everything else to all the channels (that listen to the type)
			int i = 0;
			for (NotificationChannel channel : channels) {
				if (channel.addEventLine(line) && (channel != visibleChannel)) {
					fireHiddenChannelModified(i);
				}
				i++;
			}
		}
	}

	/**
	 * Get the currently visible channel.
	 *
	 * @return visible channel
	 */
	NotificationChannel getVisibleChannel() {
		return visibleChannel;
	}

	/**
	 * Set the visible channel.
	 *
	 * @param channel
	 */
	void setVisibleChannel(NotificationChannel channel) {
		visibleChannel = channel;
	}

	/**
	 * Called when a hidden channel is modified.
	 *
	 * @param index index of the channel
	 */
	private void fireHiddenChannelModified(int index) {
		for (HiddenChannelListener l : listeners) {
			l.channelModified(index);
		}
	}

	/**
	 * Listener for channel modifications on channels whose log is not visible.
	 */
	interface HiddenChannelListener {
		/**
		 * Called when a channel whose log is not visible is modified.
		 *
		 * @param index index of the channel
		 */
		void channelModified(int index);
	}
}
