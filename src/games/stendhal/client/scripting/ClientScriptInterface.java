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
package games.stendhal.client.scripting;

import org.apache.log4j.Logger;

/**
 * Interface used by client side scripts to interact with the game.
 *
 * @author hendrik
 */
public class ClientScriptInterface {

	private static Logger logger = Logger.getLogger(ClientScriptInterface.class);

	/**
	 * handles a string command in the same way the chat line does.
	 *
	 * @param input
	 *            String to parse and handle
	 */
	public void invoke(final String input) {
		ChatLineParser.parseAndHandle(input);
		sleepMillis(300);
	}

	/**
	 * waits the specified number of milliseconds.
	 *
	 * @param millis
	 *            milliseconds to wait
	 */
	public void sleepMillis(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (final InterruptedException e) {
			logger.error(e, e);
		}
	}

	/**
	 * waits the specified number of seconds.
	 *
	 * @param seconds
	 *            seconds to wait
	 */
	public void sleepSeconds(final long seconds) {
		sleepMillis(seconds * 1000);
	}

	/**
	 * waits the specified number of turns.
	 *
	 * @param turns
	 *            turns to wait
	 */
	public void sleepTurns(final long turns) {
		sleepMillis(turns * 300);
	}
}
