/* $Id$ */
/***************************************************************************
 *                 (C) Copyright 2003-2010 - Arianne Project               *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.textclient;

import games.stendhal.bot.core.StandardClientFramework;
import games.stendhal.server.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TextClient {


	public static void main(final String[] args) {
		try {
			if (args.length > 0) {
				int i = 0;
				String username = null;
				String password = null;
				String character = null;
				String host = null;
				String port = "32160";
				boolean showWorld = false;
				boolean createAccount = false;

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
					} else if (args[i].equals("-a")) {
						createAccount = true;
					}
					i++;
				}

				if (createAccount) {
					username = "testuser" + StringUtils.generateStringOfCharacters(10);
					password = username;
					character = username;
					System.out.println(username);
				}

				if ((username != null)
						&& (character != null) && (host != null)
						&& (port != null)) {
					if (password == null) {
						System.out.print("Password: \u001B[37;47m");
						BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						password = br.readLine();
						System.out.print("\u001B[m");

					}
					System.out.println("Connecting");
					new TextUI();
					Thread thread = new Thread(new InputReader(), "TextClient InputReader");
					thread.setDaemon(true);
					thread.start();

					TextClientFramework client = new TextClientFramework(host, username, password, character, port, showWorld, createAccount);
					client.script();

					return;
				}
			}

			System.out.println("Stendhal textClient");
			System.out.println();
			System.out.println("  games.stendhal.textClient -u username -p pass -h host -P port -c character");
			System.out.println();
			System.out.println("Required parameters");
			StandardClientFramework.printConnectionParameters();
			System.out.println("Optional parameters");
			System.out.println("* -W\tShow world content? 0 or 1");
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
