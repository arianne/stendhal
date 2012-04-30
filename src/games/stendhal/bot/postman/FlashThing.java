/***************************************************************************
 *                   (C) Copyright 2012 - Faiumoni e. V.                   *
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
 * clears the flood host list
 *
 * @author hendrik
 */
class FlashThing extends EventHandler {
	private String command;
	private String sender;
	private PostmanIRC postmanIRC;
	
	/**
	 * creates a flashthing handler
	 *
	 * @param command    supportanswer-command
	 * @param sender     sender
	 * @param postmanIRC PostmanIRC
	 */
	public FlashThing(String command, String sender, PostmanIRC postmanIRC) {
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

		postmanIRC.flashThing();
	}

}
