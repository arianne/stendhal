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

import games.stendhal.client.gui.StendhalFirstScreen;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.common.Version;
import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class stendhal extends Thread {
	private static final Logger logger = Log4J.getLogger(stendhal.class);

	public static boolean doLogin = false;

	public static final String[] SERVERS_LIST = { 
		"stendhal.ath.cx",
		"localhost" };

	public static final String STENDHAL_FOLDER = System
			.getProperty("user.home")
			+ "/stendhal/";

	public static final String VERSION = Version.VERSION;

	public static final String VERSION_LOCATION = "http://arianne.sourceforge.net/stendhal.version";

	public static String SCREEN_SIZE = "640x480";

	public static final boolean SHOW_COLLISION_DETECTION = false;

	public static final boolean SHOW_EVERYONE_ATTACK_INFO = false;

	public static final boolean FILTER_ATTACK_MESSAGES = true;

	public static final int FPS_LIMIT = 25;

	public static void main(String args[]) {
		String size = null;
		int i = 0;

		while (i != args.length) {
			if (args[i].equals("-s")) {
				size = args[i + 1];
			}
			i++;
		}

		if (size != null) {
			SCREEN_SIZE = size;
		}

		Log4J.init("data/conf/log4j.properties");

		logger.info("Setting base at :" + STENDHAL_FOLDER);
		logger.info("Stendhal " + VERSION);
		logger.info("OS: " + System.getProperty("os.name") + " "
				+ System.getProperty("os.version"));
		logger.info("Java: " + System.getProperty("java.version"));

		StendhalClient client = StendhalClient.get();
		new StendhalFirstScreen(client);

		while (!doLogin) {
			try {
				Thread.sleep(200);
			} catch (Exception e) {
			}
		}

		new j2DClient(client);
	}
}
