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
package games.stendhal.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.client.net.IPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

public class textClient extends Thread {

	private final String host;

	private final String username;

	private final String password;

	private final String character;

	private final String port;

	private static boolean showWorld;

	private final Map<RPObject.ID, RPObject> world_objects;

	private final marauroa.client.ClientFramework clientManager;

	private final PerceptionHandler handler;

	public textClient(final String h, final String u, final String p, final String c, final String P,
			final boolean t) {
		host = h;
		username = u;
		password = p;
		character = c;
		port = P;

		world_objects = new HashMap<RPObject.ID, RPObject>();

		handler = new PerceptionHandler(new IPerceptionListener() {

			@Override
			public boolean onAdded(final RPObject object) {
				return false;
			}

			@Override
			public boolean onClear() {
				return false;
			}

			@Override
			public boolean onDeleted(final RPObject object) {
				return false;
			}

			@Override
			public void onException(final Exception exception,
					final MessageS2CPerception perception) {
				exception.printStackTrace();
			}

			@Override
			public boolean onModifiedAdded(final RPObject object, final RPObject changes) {
				return false;
			}

			@Override
			public boolean onModifiedDeleted(final RPObject object, final RPObject changes) {
				return false;
			}

			@Override
			public boolean onMyRPObject(final RPObject added, final RPObject deleted) {
				return false;
			}

			@Override
			public void onPerceptionBegin(final byte type, final int timestamp) {
			}

			@Override
			public void onPerceptionEnd(final byte type, final int timestamp) {
			}

			@Override
			public void onSynced() {
			}

			@Override
			public void onUnsynced() {
			}
		});

		clientManager = new marauroa.client.ClientFramework(
				"games/stendhal/log4j.properties") {

			@Override
			protected String getGameName() {
				return "stendhal";
			}

			@Override
			protected String getVersionNumber() {
				return stendhal.VERSION;
			}

			@Override
			protected void onPerception(final MessageS2CPerception message) {
				try {
					System.out.println("Received perception "
							+ message.getPerceptionTimestamp());

					handler.apply(message, world_objects);
					final int i = message.getPerceptionTimestamp();

					final RPAction action = new RPAction();
					if (i % 50 == 0) {
						action.put("type", "move");
						action.put("dy", "-1");
						clientManager.send(action);
					} else if (i % 50 == 20) {
						action.put("type", "move");
						action.put("dy", "1");
						clientManager.send(action);
					}
					if (showWorld) {
						System.out.println("<World contents ------------------------------------->");
						int j = 0;
						for (final RPObject object : world_objects.values()) {
							j++;
							System.out.println(j + ". " + object);
						}
						System.out.println("</World contents ------------------------------------->");
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
			protected void onTransfer(final List<TransferContent> items) {
				System.out.println("Transfering ----");
				for (final TransferContent item : items) {
					System.out.println(item);
				}
			}

			@Override
			protected void onAvailableCharacters(final String[] characters) {
				System.out.println("Characters available");
				for (final String characterAvail : characters) {
					System.out.println(characterAvail);
				}

				try {
					chooseCharacter(character);
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			protected void onServerInfo(final String[] info) {
				System.out.println("Server info");
				for (final String info_string : info) {
					System.out.println(info_string);
				}
			}

			@Override
			protected void onPreviousLogins(final List<String> previousLogins) {
				System.out.println("Previous logins");
				for (final String info_string : previousLogins) {
					System.out.println(info_string);
				}
			}
		};

	}

	@Override
	public void run() {
		try {
			clientManager.connect(host, Integer.parseInt(port));
			clientManager.login(username, password);
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		}

		final boolean cond = true;

		while (cond) {
			clientManager.loop(0);
			try {
				sleep(100);
			} catch (final InterruptedException e) {
			}
		}
	}

	public static void main(final String[] args) {
		try {
			if (args.length > 0) {
				int i = 0;
				String username = null;
				String password = null;
				String character = null;
				String host = null;
				String port = null;
				boolean tcp = false;

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
					} else if (args[i].equals("-W")) {
						if ("1".equals(args[i + 1])) {
							showWorld = true;
						}
					} else if (args[i].equals("-t")) {
						tcp = true;
					}
					i++;
				}

				if ((username != null) && (password != null)
						&& (character != null) && (host != null)
						&& (port != null)) {
					System.out.println("Parameter operation");
					new textClient(host, username, password, character, port,
							tcp).start();
					return;
				}
			}

			System.out.println("Stendhal textClient");
			System.out.println();
			System.out.println("  games.stendhal.textClient -u username -p pass -h host -P port -c character");
			System.out.println();
			System.out.println("Required parameters");
			System.out.println("* -h\tHost that is running Marauroa server");
			System.out.println("* -P\tPort on which Marauroa server is running");
			System.out.println("* -u\tUsername to log into Marauroa server");
			System.out.println("* -p\tPassword to log into Marauroa server");
			System.out.println("* -c\tCharacter used to log into Marauroa server");
			System.out.println("Optional parameters");
			System.out.println("* -W\tShow world content? 0 or 1");
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
