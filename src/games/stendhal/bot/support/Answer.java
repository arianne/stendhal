/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.support;

import games.stendhal.bot.core.StandardClientFramework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

import marauroa.common.game.RPAction;

/**
 * Connects to the server and asks for support.
 *
 * @author hendrik
 */
public class Answer extends StandardClientFramework {
	private final String targetPlayer;

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
	 * @param targetPlayer
	 *            player to answer to
	 * @throws SocketException
	 *             on an network error
	 */
	public Answer(final String h, final String u, final String p, final String c, final String P, String targetPlayer) throws SocketException {
		super(h, u, p, c, P, false);
		this.targetPlayer = targetPlayer;
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
		chat.put("type", "supportanswer");
		chat.put("target", targetPlayer);
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
		try {
			if (args.length > 0) {
				int i = 0;
				String username = null;
				String password = null;
				String character = null;
				String host = null;
				String port = null;
				String targetPlayer = null;

				while (i != args.length) {
					if (args[i].equals("-u")) {
						username = args[i + 1];
					} else if (args[i].equals("-p")) {
						password = args[i + 1];
					} else if (args[i].equals("-c")) {
						character = args[i + 1];
					} else if (args[i].equals("-h")) {
						host = args[i + 1];
					} else if (args[i].equals("-P")) {
						port = args[i + 1];
					} else if (args[i].equals("-t")) {
						targetPlayer = args[i + 1];
					}
					i++;
				}

				if ((username != null) && (password != null)
						&& (character != null) && (host != null)
						&& (port != null)) {
					final Answer shouter = new Answer(host, username, password, character, port, targetPlayer);
					shouter.script();
					return;
				}
			}

			System.out.println("Stendhal textClient");
			System.out.println();
			System.out.println("  games.stendhal.bot.support.Answer -u username -p pass -h host -P port -c character -t targetPlayer");
			System.out.println();
			System.out.println("Required parameters");
			StandardClientFramework.printConnectionParameters();
			System.out.println("* -t\tTarget player to answer to");
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}


}
