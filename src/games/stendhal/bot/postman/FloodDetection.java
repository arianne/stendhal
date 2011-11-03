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

import marauroa.common.Pair;

/**
 * Detects floods
 *
 * @author hendrik
 */
class FloodDetection {
	private List<Pair<String, String>> lastMessages = new LinkedList<Pair<String, String>>();

	/**
	 * adds a new message to the memory
	 * 
	 * @param sender  sender of the message
	 * @param message message
	 */
	public void add(String sender, String message) {
		lastMessages.add(new Pair<String, String>(sender, message));
		if (lastMessages.size() > 20) {
			lastMessages.remove(0);
		}
	}

	/**
	 * counts the number of recent identical messages from the same sender
	 *
	 * @param sender   sender
	 * @param message  messages
	 * @return count
	 */
	public int count(String sender, String message) {
		int res = 0;
		for (Pair<String, String> pair : lastMessages) {
			if (pair.first().equals(sender) && pair.second().equals(message)) {
				res++;
			}
		}
		return res;
	}

	/**
	 * removes messages from a sender from the list
	 *
	 * @param sender sender
	 */
	public void clear(String sender) {
		Iterator<Pair<String, String>> itr = lastMessages.iterator();
		while (itr.hasNext()) {
			if (itr.next().first().equals(sender)) {
				itr.remove();
			}
		}
	}
}
