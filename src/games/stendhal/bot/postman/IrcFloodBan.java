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
 * creates a ban on irc for flooding
 *
 * @author hendrik
 */
public class IrcFloodBan extends EventHandler {
	private String hostname;
	private PostmanIRC postmanIRC;

	/**
	 * creates a IrcBan instance
	 *
	 * @param ip ip address to ban
	 * @param postmanIRC PostmanIRC
	 */
	public IrcFloodBan(String ip, PostmanIRC postmanIRC) {
		this.hostname = ip;
		this.postmanIRC = postmanIRC;
	}

	@Override
	public void fire(EventType eventType, String channel, String furtherData) {
		postmanIRC.ban(channel, "*!*@" + hostname);
	}

}
