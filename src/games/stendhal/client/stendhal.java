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

import static games.stendhal.common.constants.Actions.MOVE_CONTINUOUS;
import static java.io.File.separator;

import java.awt.Dimension;
import java.io.File;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;

import games.stendhal.client.actions.MoveContinuousAction;
import games.stendhal.client.gui.StendhalFirstScreen;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.login.LoginDialog;
import games.stendhal.client.gui.login.Profile;
import games.stendhal.client.gui.styled.StyledLookAndFeel;
import games.stendhal.client.gui.styled.styles.StyleFactory;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.common.Debug;
import games.stendhal.common.MathHelper;
import games.stendhal.common.Version;
import marauroa.common.Log4J;
import marauroa.common.MarauroaUncaughtExceptionHandler;

/**
 * Main game class.
 */
public final class stendhal {

	private static final String LOG_FOLDER = "log/";
	private static final Logger logger = Logger.getLogger(stendhal.class);

	// Use getGameFolder() where you need the real game data location
	private static final String STENDHAL_FOLDER;
	public static final String GAME_NAME;
	/**
	 * Directory for storing the persistent game data.
	 */
	private static String gameFolder;
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
		STENDHAL_FOLDER = separator + GAME_NAME.toLowerCase(Locale.ENGLISH) + separator;
		initGameFolder();
	}

	public static final String VERSION = Version.getVersion();

	/** Display sizes optimized for different screen resolutions */
	private static final List<Dimension> displaySizes = new ArrayList<Dimension>(3);
	public static final Integer SIZE_INDEX = 0;
	public static final Integer SIZE_INDEX_LARGE = 1;
	public static final Integer SIZE_INDEX_WIDE = 2;
	public static final Integer DISPLAY_SIZE_INDEX = SIZE_INDEX;

	public static final boolean SHOW_COLLISION_DETECTION = false;

	public static final boolean SHOW_EVERYONE_ATTACK_INFO = false;

	public static final boolean FILTER_ATTACK_MESSAGES = true;

	static final int FPS_LIMIT = 25;
	/** For keeping the login status. Blocks until logged in. */
	private static final CountDownLatch latch = new CountDownLatch(1);

	/**
	 * Make the class non-instantiable.
	 */
	private stendhal() {
	}

	/**
	 * Initialize the client game directory.
	 * <p>
	 * NOTE: IF YOU CHANGE THIS, CHANGE ALSO CORRESPONDING CODE IN
	 * Bootstrap.java
	 */
	private static void initGameFolder() {
		String defaultFolder = System.getProperty("user.home") + STENDHAL_FOLDER;
		/*
		 * Add any previously unrecognized unix like systems here. These will
		 * try to use ~/.config/stendhal if the user does not have saved data
		 * in ~/stendhal.
		 *
		 * OS X is counted in here too, but should it?
		 *
		 * List taken from:
		 * 	http://mindprod.com/jgloss/properties.html#OSNAME
		 */
		String unixLikes = "AIX|Digital Unix|FreeBSD|HP UX|Irix|Linux|Mac OS X|Solaris";
		String system = System.getProperty("os.name");
		if (system.matches(unixLikes)) {
			// Check first if the user has important data in the default folder.
			File f = new File(defaultFolder + "user.dat");
			if (!f.exists()) {
				gameFolder = System.getProperty("user.home") + separator
				+ ".config" + separator + STENDHAL_FOLDER;
				return;
			}
		}
		// Everyone else should use the default top level directory in $HOME
		gameFolder = defaultFolder;
	}

	/**
	 * Notify that the login has been performed and the client should proceed
	 * to show the game window.
	 */
	public static void setDoLogin()	{
		latch.countDown();
	}

	/**
	 * Get the maximum size of the visible game area.
	 *
	 * @return screen dimensions
	 */
	public static Dimension getDisplaySize() {
		String spec = System.getProperty("display.index");
		int sizeIndex = MathHelper.parseIntDefault(spec, DISPLAY_SIZE_INDEX);

		try {
			return displaySizes.get(sizeIndex);
		} catch (IndexOutOfBoundsException e) {
			logger.error("Invalid client size index: " + spec + " (" + sizeIndex + ")", e);
		}

		return displaySizes.get(DISPLAY_SIZE_INDEX);
	}

	/**
	 * Initialize list of dimensions that can be used for the clients
	 * display area.
	 */
	private static void initUsableDisplaySizes() {
		// Optimized display dimensions for display resolutions
		displaySizes.add(new Dimension(640, 480)); // Smaller 4:3 (1024x768 and smaller)
		displaySizes.add(new Dimension(800, 600)); // Larger 4:3 (1280x1024)
		displaySizes.add(new Dimension(864, 486)); // Larger 16:9 (1366x768 and larger)
	}

	/**
	 * Starts the LogSystem.
	 */
	private static void startLogSystem() {
		prepareLoggingSystemEnviroment();

		logger.debug("XXXXXXX");

		logger.info("-Setting base at :" + STENDHAL_FOLDER);

		Log4J.init("data/conf/log4j.properties");
		logger.debug("XXXXXXX");

		logger.info("Setting base at :" + STENDHAL_FOLDER);
		logger.info("Stendhal " + VERSION);
		logger.info(Debug.PRE_RELEASE_VERSION);
		logger.info("Logging to directory: " + getLogFolder());

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
	 * Initialize the logging system.
	 */
	private static void prepareLoggingSystemEnviroment() {
		// property configuration relies on this parameter
		System.setProperty("log.directory", getLogFolder());
		//create the log directory if not yet existing:
		//removed code as log4j is now capable of doing that automatically
	}

	/**
	 * @return the name of the log folder
	 */
	private static String getLogFolder() {
		return getGameFolder() + LOG_FOLDER;
	}

	/**
	 * A loop which simply waits for the login to be completed.
	 */
	private static void waitForLogin() {
		try {
			latch.await();
		} catch (final InterruptedException e) {
			logger.error("Unexpected interrupt", e);
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * Get the location of persistent game client data.
	 *
	 * @return game's home directory
	 */
	public static String getGameFolder() {
		return gameFolder;
	}

	/**
	 * Main Entry point.
	 *
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) {
		startLogSystem();
		MarauroaUncaughtExceptionHandler.setup(false);
		initUsableDisplaySizes();
		new Startup(args);
	}

	private static class Startup {
		StendhalFirstScreen splash;

		Startup(String[] args) {
			final UserContext userContext = UserContext.get();
			final PerceptionDispatcher perceptionDispatch = new PerceptionDispatcher();
			final StendhalClient client = new StendhalClient(userContext, perceptionDispatch);

			try {
				WtWindowManager wm = WtWindowManager.getInstance();
				String style = wm.getProperty("ui.style", "Wood (default)");
				StyledLookAndFeel look = new StyledLookAndFeel(StyleFactory.createStyle(style));
				UIManager.setLookAndFeel(look);
				/*
				 * Prevents the click event at closing a popup menu by clicking
				 * outside it being passed to the component underneath.
				 */
				UIManager.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);
				int fontSize = wm.getPropertyInt("ui.font_size", 12);
				look.setDefaultFontSize(fontSize);
			} catch (UnsupportedLookAndFeelException e) {
				/*
				 * Should not happen as StyledLookAndFeel always returns true for
				 * isSupportedLookAndFeel()
				 */
				logger.error("Failed to set Look and Feel", e);
			}

			UIManager.getLookAndFeelDefaults().put("ClassLoader", stendhal.class.getClassLoader());

			final Profile profile = Profile.createFromCommandline(args);

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (profile.isValid()) {
						new LoginDialog(null, client).connect(profile);
					} else {
						splash = new StendhalFirstScreen(client);
					}
				}
			});

			waitForLogin();
			CStatusSender.send();

			/*
			 * Pass the continuous movement setting is to the server.
			 * It is done in game loop to ensure that the server version is
			 * known before sending the command, to avoid sending invalid
			 * commands.
			 */
			GameLoop.get().runOnce(new Runnable() {
				@Override
				public void run() {
					boolean moveContinuous = WtWindowManager.getInstance().getPropertyBoolean(MOVE_CONTINUOUS, false);
					if (moveContinuous) {
						new MoveContinuousAction().sendAction(true, false);
					}
				}
			});

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					j2DClient locclient = new j2DClient(client, userContext, splash);
					perceptionDispatch.register(locclient.getPerceptionListener());
					locclient.startGameLoop();
				}
			});
		}
	}
}
