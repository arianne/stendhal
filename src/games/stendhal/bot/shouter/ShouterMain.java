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
package games.stendhal.bot.shouter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

import games.stendhal.bot.core.StandardClientFramework;
import games.stendhal.tools.AdHocCoverage;
import marauroa.common.game.RPAction;

/**
 * Connects to the server and shouts a message.
 *
 * @author hendrik
 */
public class ShouterMain extends StandardClientFramework {

	/**
	 * Creates a ShouterMain.
	 *
	 * @param h
	 *            host
	 * @param u
	 *            username
	 * @param p
	 *            password
	 * @param c
	 *            character name
	 * @param P
	 *            port
	 * @throws SocketException
	 *             on an network error
	 */
	public ShouterMain(final String h, final String u, final String p, final String c, final String P) throws SocketException {
		super(h, u, p, c, P, false);
	}

	@Override
	public void execute() throws IOException, InterruptedException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		while (line != null) {
			if (line.trim().length() > 0) {
				shout(line);
			}
			Thread.sleep(1000);
			line = br.readLine();
		}
		br.close();
	}

	private void shout(final String message) {
		final RPAction chat = new RPAction();
		chat.put("type", "tellall");
		chat.put("text", message);
		this.send(chat);
	}

	/**
	 * Main entry point.
	 *
	 * @param args
	 *            see help
	 */
	public static void main(final String[] args) {
		AdHocCoverage coverage = new AdHocCoverage("ShouterMain-main", 11);

		try {
			coverage.branchReached(0);
			if (args.length > 0) {
				coverage.branchReached(1);
				int i = 0;
				String username = null;
				String password = null;
				String character = null;
				String host = null;
				String port = null;

				while (i != args.length) {
					coverage.branchReached(2);
					if (args[i].equals("-u")) {
						coverage.branchReached(3);
						username = args[i + 1];
					} else if (args[i].equals("-p")) {
						coverage.branchReached(4);
						password = args[i + 1];
					} else if (args[i].equals("-c")) {
						coverage.branchReached(5);
						character = args[i + 1];
					} else if (args[i].equals("-h")) {
						coverage.branchReached(6);
						host = args[i + 1];
					} else if (args[i].equals("-P")) {
						coverage.branchReached(7);
						port = args[i + 1];
					}
					i++;
				}

				if ((username != null) && (password != null)
						&& (character != null) && (host != null)
						&& (port != null)) {
					coverage.branchReached(8);
					final ShouterMain shouter = new ShouterMain(host, username,
							password, character, port);
					shouter.script();
					return;
				}
			}

			System.out.println("Stendhal textClient");
			System.out.println();
			System.out.println("  games.stendhal.bot.shouter.Shouter -u username -p pass -h host -P port -c character");
			System.out.println();
			System.out.println("Required parameters");
			StandardClientFramework.printConnectionParameters();
		} catch (final Exception e) {
			coverage.branchReached(10);
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}


}
