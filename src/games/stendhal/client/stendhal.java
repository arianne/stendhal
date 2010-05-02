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

import static java.io.File.separator;
import games.stendhal.client.gui.StendhalFirstScreen;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.login.LoginDialog;
import games.stendhal.client.gui.login.Profile;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.client.update.Version;

import java.awt.Dimension;
import java.security.AccessControlException;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class stendhal {

	private static final Logger logger = Logger.getLogger(stendhal.class);

	public static boolean doLogin;

	public static String STENDHAL_FOLDER;
	public static final String GAME_NAME;
	/**
	 * Just a try to get Webstart working without additional rights.
	 */
	static boolean WEB_START_SANDBOX = false;

	// detect web start sandbox and init STENDHAL_FOLDER otherwise
	static {
		try {
			System.getProperty("user.home");
		} catch (final AccessControlException e) {
			WEB_START_SANDBOX = true;
		}

		/** We set the main game folder to the game name */
		GAME_NAME = ClientGameConfiguration.get("GAME_NAME");
		STENDHAL_FOLDER = separator + GAME_NAME.toLowerCase() + separator;
	}

	public static final String VERSION = Version.VERSION;

	public static Dimension screenSize = new Dimension(640, 480);
	
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
	private static void parseCommandlineArguments(final String[] args) {
		String size = null;
		int i = 0;

		while (i != args.length) {
			if (args[i].equals("-s")) {
				size = args[i + 1];
			}
			i++;
		}

		if (size != null) {
			String[] tempsize = size.split("x");
			screenSize = new Dimension(Integer.parseInt(tempsize[0]), Integer.parseInt(tempsize[1]));
			
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
		LogUncaughtExceptionHandler.setup();
	}

	/**
	 * A loop which simply waits for the login to be completed.
	 */
	private static void waitForLogin() {
		while (!doLogin) {
			try {
				Thread.sleep(200);
			} catch (final Exception e) {
				// simply ignore it
			}
		}
	}

	/**
	 * Main Entry point.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) {
		// get size string
		parseCommandlineArguments(args);
		startLogSystem();
		UserContext userContext = new UserContext();
		PerceptionDispatcher perceptionDispatch = new PerceptionDispatcher();
		final StendhalClient client = new StendhalClient(userContext, perceptionDispatch);

		Profile profile = Profile.createFromCommandline(args);
		if (profile.isValid()) {
			new LoginDialog(null, client).connect(profile);
		} else {
			new StendhalFirstScreen(client);
		}
		
		waitForLogin();
		IDSend.send();
		GameScreen gameScreen = GameScreen.get();
		
		final j2DClient locclient = new j2DClient(client, gameScreen, userContext);
		perceptionDispatch.register(locclient.getPerceptionListener());
		locclient.gameLoop(gameScreen);
		locclient.cleanup();
	}
}
