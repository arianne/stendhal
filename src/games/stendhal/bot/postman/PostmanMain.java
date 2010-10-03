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
package games.stendhal.bot.postman;

import games.stendhal.bot.core.PerceptionErrorListener;
import games.stendhal.client.update.Version;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.Log4J;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

/**
 * Starts Postman and connect to server.
 * 
 * @author hendrik
 */
public class PostmanMain extends Thread {

	protected String character;
	
	protected Postman postman;

	protected long lastPerceptionTimestamp;

	protected Map<RPObject.ID, RPObject> world_objects;

	protected marauroa.client.ClientFramework clientManager;

	protected PerceptionHandler handler;
	
	private final String host;

	private final String username;

	private final String password;

	

	private final String port;

	

	/**
	 * Creates a PostmanMain.
	 * 
	 * @param h
	 *            host
	 * @param u
	 *            user name
	 * @param p
	 *            password
	 * @param c
	 *            character name
	 * @param P
	 *            port
	 * @throws SocketException
	 *             on an network error
	 */
	public PostmanMain(final String h, final String u, final String p, final String c, final String P)
			throws SocketException {
		host = h;
		username = u;
		password = p;
		character = c;
		port = P;

		world_objects = new HashMap<RPObject.ID, RPObject>();

		handler = new PerceptionHandler(new PerceptionErrorListener());

		clientManager = new marauroa.client.ClientFramework(
				"games/stendhal/log4j.properties") {

			@Override
			protected String getGameName() {
				return "stendhal";
			}

			@Override
			protected String getVersionNumber() {
				return Version.getVersion();
			}

			@Override
			protected void onPerception(final MessageS2CPerception message) {
				lastPerceptionTimestamp = System.currentTimeMillis();
				try {
					handler.apply(message, world_objects);
					for (final RPObject object : world_objects.values()) {
						for (final RPEvent event : object.events()) {
							if (event.getName().equals("private_text")) {
								postman.processPrivateTalkEvent(object,
										event.get("texttype"),
										event.get("text"));
							}
						}
						if (object.has("text")) {
							postman.processPublicTalkEvent(object);
						}
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected List<TransferContent> onTransferREQ(
					final List<TransferContent> items) {
				for (final TransferContent item : items) {
					item.ack = true;
				}

				return items;
			}

			@Override
			protected void onServerInfo(final String[] info) {
				// do nothing
			}

			@Override
			protected void onAvailableCharacters(final String[] characters) {
				try {
					chooseCharacter(character);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onTransfer(final List<TransferContent> items) {
				// do nothing
			}

			@Override
			protected void onPreviousLogins(final List<String> previousLogins) {
				// do nothing
			}
		};
	}

	@Override
	public void run() {
		try {
			clientManager.connect(host, Integer.parseInt(port));
			clientManager.login(username, password);
			final PostmanIRC postmanIRC = new PostmanIRC(host);
			postmanIRC.connect();
			postman = new Postman(clientManager, postmanIRC);
			postman.teleportPostman();
		} catch (final SocketException e) {
			System.err.println("Socket Exception");
			e.printStackTrace();
			Runtime.getRuntime().halt(1);
			return;
		} catch (final TimeoutException e) {
			System.err.println("Cannot connect to Stendhal server. Server is down?");
			// TODO: shutdown cleanly
			// return;
			Runtime.getRuntime().halt(1);
		} catch (final Exception e) {
			System.out.println(e);
			e.printStackTrace(System.err);
			Runtime.getRuntime().halt(1);
		}

		final boolean cond = true;
		while (cond) {
			clientManager.loop(0);

			if ((lastPerceptionTimestamp > 0)
					&& (lastPerceptionTimestamp + 30 * 1000 < System.currentTimeMillis())) {
				System.err.println("Timeout");
				Runtime.getRuntime().halt(1);
			}

			try {
				sleep(200);
			} catch (final InterruptedException e) {
				// ignore
			}
		}
	}

	/**
	 * Main entry point.
	 * 
	 * @param args
	 *            see help
	 */
	public static void main(final String[] args) {
		Log4J.init("marauroa/server/log4j.properties");

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
					final PostmanMain postmanMain = new PostmanMain(host, username,
							password, character, port);
					postmanMain.start();
					return;
				}
			}

			System.out.println("Stendhal textClient");
			System.out.println();
			System.out.println("  games.stendhal.bot.PostmanMain -u username -p pass -h host -P port -c character");
			System.out.println();
			System.out.println("Required parameters");
			System.out.println("* -h\tHost that is running Marauroa server");
			System.out.println("* -P\tPort on which Marauroa server is running");
			System.out.println("* -u\tUsername to log into Marauroa server");
			System.out.println("* -p\tPassword to log into Marauroa server");
			System.out.println("* -c\tCharacter used to log into Marauroa server");
			System.out.println("Optional parameters");
			System.out.println("* -t\tuse tcp-connection to server");
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
