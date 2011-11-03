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

/**
 * an IRC message with sender and timestamp
 *
 * @author hendrik
 */
class IrcMessage {

	private String sender;
	private String message;
	private long timestamp;

	/**
	 * creates a new IrcMessage
	 *
	 * @param sender  sender
	 * @param message message
	 */
	public IrcMessage(String sender, String message) {
		this.sender = sender;
		this.message = message;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * gets the sender
	 *
	 * @return sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * gets the message
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * gets the timestamp
	 *
	 * @return timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	
}
