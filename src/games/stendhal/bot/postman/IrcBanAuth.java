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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * creates a ban on irc
 *
 * @author hendrik
 */
public class IrcBanAuth extends EventHandler {
	private String command;
	private String sender;
	private PostmanIRC postmanIRC;

	/** ircban ip */
	static final Pattern patternIrcBan = Pattern.compile("(?i)^ircban (.*)$");

	/**
	 * creates a IrcBan instance
	 *
	 * @param command ip address to ban
	 * @param sender     sender
	 * @param postmanIRC PostmanIRC
	 */
	public IrcBanAuth(String command, String sender, PostmanIRC postmanIRC) {
		this.command = command;
		this.sender = sender;
		this.postmanIRC = postmanIRC;
	}

	@Override
	public void fire(EventType eventType, String channel, String ircAccountName) {
		String charname = extractGameAccount(postmanIRC, sender, ircAccountName);
		if (charname == null) {
			return;
		}
		Matcher matcher = patternIrcBan.matcher(command);
		if (matcher.find()) {
			String ip = matcher.group(1);
			postmanIRC.sendSupportMessage(charname + " banned " + ip + " on irc.");
			postmanIRC.banIp(ip);
		}
	}

}
