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
 * ban
 *
 * @author hendrik
 */
class Ban extends EventHandler {
	private String command;
	private String sender;
	private PostmanIRC postmanIRC;
	
	/** ban target hours message */
	static final Pattern patternBan = Pattern.compile("(?i)^ban ([^ ]*) ([^ ]*) (.*)$");

	/**
	 * creates a ban handler
	 *
	 * @param command    supportanswer-command
	 * @param sender     sender
	 * @param postmanIRC PostmanIRC
	 */
	public Ban(String command, String sender, PostmanIRC postmanIRC) {
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
		Matcher matcher = patternBan.matcher(command);
		if (matcher.find()) {
			String target = matcher.group(1);
			String hours = matcher.group(2);
			String message = matcher.group(3);
			Postman.get().ban(charname, target, hours, message);
		}
	}

}
