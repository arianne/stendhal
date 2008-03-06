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
import games.stendhal.client.soundreview.SoundMaster;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.client.update.Version;
import games.stendhal.common.Debug;

import java.security.AccessControlException;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class stendhal extends Thread {

	private static final Logger logger = Logger.getLogger(stendhal.class);

	public static boolean doLogin;

	public static String STENDHAL_FOLDER;

	// detect web start sandbox and init STENDHAL_FOLDER otherwise
	static {
		try {
			System.getProperty("user.home");
		} catch (AccessControlException e) {
			Debug.WEB_START_SANDBOX = true;
		}

		/** We set the main game folder to the game name */
		String gameName = ClientGameConfiguration.get("GAME_NAME");
		STENDHAL_FOLDER = "/" + gameName.toLowerCase() + "/";
	}

	public static final String VERSION = Version.VERSION;

	public static String SCREEN_SIZE = "640x480";

	public static final boolean SHOW_COLLISION_DETECTION = false;

	public static final boolean SHOW_EVERYONE_ATTACK_INFO = false;

	public static final boolean FILTER_ATTACK_MESSAGES = true;

	public static final int FPS_LIMIT = 25;

	/**
	 * Parses command line arguments.
	 * 
	 * @param args
	 *            command line arguments
	 */
	private static void parseCommandlineArguments(String[] args) {
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
	}

	/**
	 * Starts the LogSystem.
	 */
	private static void startLogSystem() {
		Log4J.init("data/conf/log4j.properties");

		logger.info("Setting base at :" + STENDHAL_FOLDER);
		logger.info("Stendhal " + VERSION);

		String patchLevel = System.getProperty("sun.os.patch.level");
		if ((patchLevel == null) || (patchLevel.equals("unknown"))) {
			patchLevel = "";
		}

		logger.info("OS: " + System.getProperty("os.name") + " " + patchLevel
				+ " " + System.getProperty("os.version") + " "
				+ System.getProperty("os.arch"));
		logger.info("Java-Runtime: " + System.getProperty("java.runtime.name")
				+ " " + System.getProperty("java.runtime.version") + " from "
				+ System.getProperty("java.home"));
		logger.info("Java-VM: " + System.getProperty("java.vm.vendor") + " "
				+ System.getProperty("java.vm.name") + " "
				+ System.getProperty("java.vm.version"));

		// TODO: Add some kind of user visible warning for non suppored java
		// vms.
	}

	// /**
	// * Try to use the system look and feel.
	// */
	// private static void startSwingLookAndFeel() {
	// try {
	// // only enable SystemLookAndFeelClassName for MS Windows because of
	// // bug
	// //
	// http://sourceforge.net/tracker/index.php?func=detail&aid=1601437&group_id=1111&atid=101111
	// /*
	// * if (System.getProperty("os.name",
	// * "").toLowerCase().indexOf("windows") > -1) {
	// * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
	// */
	// } catch (Exception e) {
	// logger.error(
	// "Can't change Look&Feel to match your OS. Using the Cross-Platform look &
	// feel",
	// e);
	// }
	// }

	/**
	 * Starts the client and show the first screen.
	 * 
	 * @return StendhalClient
	 */
	private static StendhalClient startClient() {
		StendhalClient client = StendhalClient.get();
		new StendhalFirstScreen(client);
		return client;
	}

	/**
	 * A loop which simply waits for the login to be completed.
	 */
	private static void waitForLogin() {
		while (!doLogin) {
			try {
				Thread.sleep(200);
			} catch (Exception e) {
				// simply ignore it
			}
		}
	}

	/**
	 * Starts the real game gui.
	 * 
	 * @param client
	 *            StendhalClient
	 */
	private static void startGameGUI(StendhalClient client) {
		j2DClient locclient= new j2DClient(client);
		locclient.initialize();
		locclient.gameLoop();
		locclient.cleanup();
	}

	/**
	 * Main Entry point.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {
		// get size string
		if (System.getProperty("stendhal.refactoringgui") != null) {
			SCREEN_SIZE = "1000x480";
		} else {
			SCREEN_SIZE = "640x480";
		}
		parseCommandlineArguments(args);
		startLogSystem();

		StendhalClient client = startClient();
		waitForLogin();
		startSoundMaster();
		startGameGUI(client);
	}

	private static void startSoundMaster() {
		SoundMaster sm = new SoundMaster();
		sm.init();
		Thread th = new Thread(sm);
		th.start();

	}
}
