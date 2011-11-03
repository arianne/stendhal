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
 * bans an ip-address
 *
 * @author hendrik
 */
class IPBan extends EventHandler {
	private String command;
	private String sender;
	private PostmanIRC postmanIRC;
	
	/** ban target hours message */
	static final Pattern patternIPBan = Pattern.compile("^ipban ([^ ]*) ([^ ]*) (.*)$");

	/**
	 * creates a ban handler
	 *
	 * @param command    command
	 * @param sender     sender
	 * @param postmanIRC PostmanIRC
	 */
	public IPBan(String command, String sender, PostmanIRC postmanIRC) {
		this.command = command;
		this.sender = sender;
		this.postmanIRC = postmanIRC;
	}

	@Override
	public void fire(EventType eventType, String eventDetail, String ircAccountName) {
		String charname = extractGameAccount(postmanIRC, sender, ircAccountName);
		if (charname == null) {
			return;
		}
		Matcher matcher = patternIPBan.matcher(command);
		if (matcher.find()) {
			String ip = matcher.group(1);
			String mask = matcher.group(2);
			String comment = matcher.group(3);
			Postman.get().ipban(charname, ip, mask, comment);
		}
	}

}
