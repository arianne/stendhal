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
 * event used by postman to handle asynchronous operations
 *
 * @author hendrik
 */
enum EventType {

	/** irc identification */
	IRC_WHOIS,
	/** got op-permissions on a channel*/
	IRC_OP,
	/** got an adminlevel answer from the game server */
	GAME_ADMINLEVEL;
}
