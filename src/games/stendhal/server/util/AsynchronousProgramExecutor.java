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
package games.stendhal.server.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import marauroa.common.Configuration;

/**
 * Executes an external program
 *
 * @author hendrik
 */
public class AsynchronousProgramExecutor extends Thread {
	private static Logger logger = Logger.getLogger(AsynchronousProgramExecutor.class);
	private String message;
	private String account;

	/**
	 * Creates a new AsynchronousProgramExecutor
	 *
	 * @param account
	 *            account to use
	 * @param message
	 *            message to tweet
	 */
	public AsynchronousProgramExecutor(String account, String message) {
		this.account = account;
		this.message = message;
	}

	/**
	 * Executes the program. Use "start()" for asynchronous access.
	 */
	@Override
	public void run() {
		Configuration configuration;
		try {
			configuration = Configuration.getConfiguration();
		} catch (IOException e1) {
			logger.error(e1, e1);
			return;
		}

		// check that a password for this account was configured
		if (!configuration.has("stendhal.program." + account)) {
			return;
		}

		String cmd = configuration.get("stendhal.program." + account);
		send(cmd);
	}

	/**
	 * sends the message to the twitter account
	 *
	 * @param cmd command
	 */
	private void send(String cmd) {
		try {
			String[] args = new String[2];
			args[0] = cmd;
			args[1] = message;
			Process p = Runtime.getRuntime().exec(args);
			p.getErrorStream().close();
			p.getOutputStream().close();
			p.getInputStream().close();
		} catch (IOException e) {
			logger.error(e, e);
		}
	}
}
