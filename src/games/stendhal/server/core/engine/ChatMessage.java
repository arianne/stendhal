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

public class ChatMessage {
	public final String source;
	public final String message;
	public final String timestamp;
	
	/**
	 * a chat message from a source at a time.
	 *
	 * @param source - who it is from
	 * @param message
	 * @param timestamp - when the message was created
	 */
	public ChatMessage(final String source, final String message, final String timestamp) {
		this.source = source;
		this.message = message;
		this.timestamp = timestamp;
	}
	
	/**
	 * @return source
	 */
	public String getSource() {
		return source;
	}
	
	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @return string of full ChatMessage details
	 */
	public String toString() {
		return source + " left message: " + message + " on " + timestamp;
	}
	
}
