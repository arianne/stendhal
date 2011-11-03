/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.postman;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Detects floods
 *
 * @author hendrik
 */
class FloodDetection {
	private List<IrcMessage> lastMessages = new LinkedList<IrcMessage>();

	/**
	 * adds a new message to the memory
	 * 
	 * @param sender  sender of the message
	 * @param message message
	 */
	public void add(String sender, String message) {
		lastMessages.add(new IrcMessage(sender, message));
		if (lastMessages.size() > 100) {
			lastMessages.remove(0);
		}
	}

	/**
	 * counts the number of recent identical messages from the same sender
	 *
	 * @param sender   sender
	 * @param timestamp look only for mesasge newer than this
	 * @param message  messages
	 * @return count
	 */
	private int countIdenticalMessages(String sender, long timestamp, String message) {
		int res = 0;
		for (IrcMessage msg : lastMessages) {
			if (msg.getSender().equals(sender) && (msg.getTimestamp() > timestamp) && msg.getMessage().equals(message)) {
				res++;
			}
		}
		return res;
	}

	/**
	 * counts the number of recent message from the same sender
	 *
	 * @param sender   sender
	 * @param timestamp look only for mesasge newer than this
	 * @return count
	 */
	private int countMessagesFromSender(String sender, long timestamp) {
		int res = 0;
		for (IrcMessage msg : lastMessages) {
			if (msg.getSender().equals(sender) && (msg.getTimestamp() > timestamp)) {
				res++;
			}
		}
		return res;
	}

	/**
	 * is this user flooding
	 *
	 * @param sender   sender
	 * @param message  messages
	 * @return true, if the user is flooding, false otherwise
	 */
	public boolean isFlooding(String sender, String message) {
		return (this.countIdenticalMessages(sender, System.currentTimeMillis() - 60*1000, message) > 5)
			|| (this.countIdenticalMessages(sender, System.currentTimeMillis() - 3 * 60*1000, message) > 10)
			|| (this.countMessagesFromSender(sender, System.currentTimeMillis() - 60*1000) > 20)
			|| (this.countMessagesFromSender(sender, System.currentTimeMillis() - 3 * 60*1000) > 50);
	}

	/**
	 * removes messages from a sender from the list
	 *
	 * @param sender sender
	 */
	public void clear(String sender) {
		Iterator<IrcMessage> itr = lastMessages.iterator();
		while (itr.hasNext()) {
			if (itr.next().getSender().equals(sender)) {
				itr.remove();
			}
		}
	}
}
