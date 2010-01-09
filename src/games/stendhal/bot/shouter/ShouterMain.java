/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.bot.core.PerceptionErrorListener;
import games.stendhal.bot.core.StandardClientFramework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;

import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPAction;

/**
 * Connects to the server and shouts a message.
 * 
 * @author hendrik
 */
public class ShouterMain {

	private final String host;

	private final String username;

	private final String password;

	protected String character;

	private final String port;

	protected marauroa.client.ClientFramework clientManager;

	protected PerceptionHandler handler;

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
	public ShouterMain(final String h, final String u, final String p, final String c, final String P)
			throws SocketException {
		host = h;
		username = u;
		password = p;
		character = c;
		port = P;

		handler = new PerceptionHandler(new PerceptionErrorListener());

		clientManager = new StandardClientFramework(character, handler);
	}

	public void script() {
		try {
			clientManager.connect(host, Integer.parseInt(port));
			clientManager.login(username, password);
			readMessagesAndShoutThem();
			clientManager.logout();
			System.exit(0);

			// exit with an exit code of 1 on error
		} catch (final SocketException e) {
			System.err.println("Socket Exception");
			Runtime.getRuntime().halt(1);
		} catch (final TimeoutException e) {
			System.err.println("Cannot connect to Stendhal server. Server is down?");
			Runtime.getRuntime().halt(1);
		} catch (final Exception e) {
			System.out.println(e);
			e.printStackTrace(System.err);
			Runtime.getRuntime().halt(1);
		}
	}

	private void readMessagesAndShoutThem() throws IOException, InterruptedException {
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
		clientManager.send(chat);
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
					}
					i++;
				}

				if ((username != null) && (password != null)
						&& (character != null) && (host != null)
						&& (port != null)) {
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
			System.out.println("* -h\tHost that is running Marauroa server");
			System.out.println("* -P\tPort on which Marauroa server is running");
			System.out.println("* -u\tUsername to log into Marauroa server");
			System.out.println("* -p\tPassword to log into Marauroa server");
			System.out.println("* -c\tCharacter used to log into Marauroa server");
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
