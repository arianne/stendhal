/***************************************************************************
 *                   (C) Copyright 2003-2022 - Marauroa                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui;

import java.awt.Frame;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameLoop;
import games.stendhal.client.GameObjects;
import games.stendhal.client.PerceptionListenerImpl;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.UserContext;
import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.client.listener.PositionChangeListener;
import games.stendhal.client.listener.PositionChangeMulticaster;
import games.stendhal.client.sound.facade.SoundFileType;
import games.stendhal.client.sound.facade.SoundGroup;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.common.Debug;
import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.SoundLayer;
import marauroa.client.BannedAddressException;
import marauroa.client.TimeoutException;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;


/** The main class that create the screen and starts the arianne client. */
public class j2DClient implements UserInterface {
	static {
		// This is potentially the first loaded GUI component (happens when
		// using web start)
		Initializer.init();
	}
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(j2DClient.class);

	/**
	 * A shared [singleton] copy.
	 */
	private static j2DClient sharedUI;


	/** Chat channels. */
	private final NotificationChannelManager channelManager = new NotificationChannelManager();

	private User lastuser;
	private final PositionChangeMulticaster positionChangeListener = new PositionChangeMulticaster();
	private final J2DClientGUI gui;
	/**
	 * The stendhal client.
	 */
	private StendhalClient client;
	private SoundSystemFacade soundSystemFacade;
	private boolean gameRunning;

	/**
	 * Get the default UI.
	 *
	 * @return the instance
	 */
	public static j2DClient get() {
		return sharedUI;
	}

	/**
	 * Set the shared [singleton] value.
	 *
	 * @param sharedUI
	 *        The Stendhal UI.
	 */
	private static void setDefault(final j2DClient sharedUI) {
		j2DClient.sharedUI = sharedUI;
		ClientSingletonRepository.setUserInterface(sharedUI);
	}

	private final IPerceptionListener perceptionListener = new PerceptionListenerImpl() {
		int times;

		@Override
		public void onSynced() {
			gui.setOffline(false);
			times = 0;
			logger.debug("Synced with server state.");
			addEventLine(new HeaderLessEventLine("Synchronized",
					NotificationType.CLIENT));
		}

		@Override
		public void onUnsynced() {
			times++;

			if (times > 3) {
				logger.debug("Request resync");
				addEventLine(new HeaderLessEventLine(
						"Unsynced: Resynchronizing...",
						NotificationType.CLIENT));
			}
		}
	};

	/**
	 * A constructor for JUnit tests.
	 */
	public j2DClient(J2DClientGUI gui) {
		setDefault(this);
		this.gui = gui;
	}

	/**
	 * Create new j2DClient.
	 *
	 * @param client
	 * @param userContext
	 * @param splash
	 *        splash screen or <code>null</code>. If not
	 *        <code>null</code>, it will be used as the main window
	 */
	public j2DClient(final StendhalClient client,
			final UserContext userContext,
			JFrame splash) {
		this.client = client;
		setDefault(this);

		/*
		 * Register the slash actions in the client side command line parser.
		 * This needs to be at least before getting the actions to
		 * ChatCompletionHelper.
		 */
		SlashActionRepository.register();

		gui = new SwingClientGUI(client, userContext, channelManager, splash);

		for (PositionChangeListener listener : gui.getPositionChangeListeners()) {
			positionChangeListener.add(listener);
		}

		// Display a hint if this is a debug client
		if (Debug.PRE_RELEASE_VERSION != null) {
			addEventLine(new HeaderLessEventLine("This is a pre release test client: " + Debug.VERSION + " - " + Debug.PRE_RELEASE_VERSION, NotificationType.CLIENT));
		}

		checkAndComplainAboutJavaImplementation();
		positionChangeListener.add(getSoundSystemFacade());
	} // constructor



	/**
	 * Check the used java version, and show a warning if it's not known to be
	 * a compatible one.
	 */
	private void checkAndComplainAboutJavaImplementation() {
		final String vmName = System.getProperty("java.vm.name", "unknown").toLowerCase(Locale.ENGLISH);
		if ((vmName.indexOf("hotspot") < 0) && (vmName.indexOf("openjdk") < 0)) {
			final String text = "Stendhal is developed and tested on Sun Java and OpenJDK. You are using "
				+ System.getProperty("java.vm.vendor", "unknown") + " "
				+ System.getProperty("java.vm.name", "unknown")
				+ " so there may be some problems like a black or grey screen.\n"
				+ " If you have coding experience with your JDK, we are looking for help.";
			addEventLine(new HeaderLessEventLine(text, NotificationType.ERROR));
		}
	}

	/**
	 * Start the game loop thread.
	 */
	public void startGameLoop() {
		try {
			SoundGroup group = initSoundSystem();
			group.play("xylophone-1", 0, null, null, false, true);
		} catch (RuntimeException e) {
			logger.error(e, e);
		}

		GameLoop loop = GameLoop.get();
		GameObjects gameObjects = client.getGameObjects();

		loop.runAllways(delta -> gameLoop(delta, gameObjects));
		loop.runAtQuit(this::cleanup);
		gameRunning = true;
		loop.start();
	}

	/**
	 * Called at quit.
	 */
	private void cleanup() {
		// try to save the window configuration
		WtWindowManager.getInstance().save();

		// Fall back in case sound system hangs. Can happen at least when using
		// the pulseaudio driver and the sound daemon is shut down while the
		// client has the line open.
		Runnable quit = new Runnable() {
			@Override
			public void run() {
				logger.warn("Forced exit, sound system likely locked up");
				System.exit(1);
			}
		};
		Executors.newSingleThreadScheduledExecutor().schedule(quit, 3, TimeUnit.SECONDS);
		getSoundSystemFacade().exit();

		// Normal shutdown
		logger.debug("Exit");
		System.exit(0);
	}

	/**
	 * Main game loop contents. Updates objects, and requests redraws.
	 *
	 * @param delta difference to previous calling time
	 * @param gameObjects
	 */
	private void gameLoop(final int delta, final GameObjects gameObjects) {
		if (!gameRunning) {
			tryLogout();
		}

		// Shows a offline icon if the connection is broken
		gui.setOffline(!client.getConnectionState());
		gui.beforePainting();

		logger.debug("Move objects");
		gameObjects.update(delta);

		final User user = User.get();

		// check if the player object has changed.
		// Note: this is an exact object reference check
		if ((user != null) && (user != lastuser)) {
			gui.updateUser(user);
			lastuser = user;
		}

		gui.triggerPainting();

		logger.debug("Query network");

		client.loop(0);
		gui.afterPainting();
	}

	private void tryLogout() {
		logger.info("Request logout");
		try {
			/*
			 * We request server permision to logout. Server can deny
			 * it, unless we are already offline.
			 */
			if (gui.isOffline() || client.logout()) {
				GameLoop.get().stop();
			} else {
				logger.warn("You can't logout now.");
				gameRunning = true;
			}
		} catch (final InvalidVersionException|TimeoutException|BannedAddressException e) {
			/*
			 * If we get a timeout exception we accept exit request.
			 */
			logger.error(e, e);
			GameLoop.get().stop();
		}
	}

	/**
	 * Initialize the sounds used by the user interfase.
	 *
	 * @return user interface sound group
	 */
	private SoundGroup initSoundSystem() {
		SoundGroup group = getSoundSystemFacade().getGroup(SoundLayer.USER_INTERFACE.groupName);
		group.loadSound("xylophone-1", "xylophone-1.ogg", SoundFileType.OGG, false);
		group.loadSound("gui-window-fold", "gui-window-fold.ogg", SoundFileType.OGG, false);
		return group;
	}

	/**
	 * Shutdown the client. Save state and tell the main loop to stop.
	 */
	void shutdown() {
		gameRunning = false;
	}

	/**
	 * Add a new window.
	 *
	 * @param mw A managed window.
	 */
	public void addWindow(final ManagedWindow mw) {
		if (mw instanceof InternalManagedWindow) {
			gui.addDialog((InternalManagedWindow) mw);
		} else {
			throw new IllegalArgumentException("Unsupport ManagedWindow type: "
					+ mw.getClass().getName());
		}
	}

	public void requestQuit() {
		gui.requestQuit(client);
	}

	@Override
	public void addEventLine(final EventLine line) {
		channelManager.addEventLine(line);
	}

	@Deprecated
	@Override
	public void addGameScreenText(final double x, final double y, final String text, final NotificationType type,
			final boolean isTalking) {
		gui.addGameScreenText(x, y, text, type, isTalking);
	}

	@Deprecated
	@Override
	public void addGameScreenText(final Entity entity, final String text,
			final NotificationType type, final boolean isTalking) {
		gui.addGameScreenText(entity, text, type, isTalking);
	}

	@Override
	public void addAchievementBox(String title, String description, String category) {
		gui.addAchievementBox(title, description, category);
	}

	/**
	 * Initiate outfit selection by the user.
	 */
	public void chooseOutfit() {
		gui.chooseOutfit();
	}

	/**
	 * Get the main window component.
	 *
	 * @return main window
	 */
	public Frame getMainFrame() {
		return gui.getFrame();
	}

	/**
	 * Resets the clients width and height to their default values.
	 */
	public void resetClientDimensions() {
		gui.resetClientDimensions();
	}

	/**
	 * Set the input chat line text.
	 *
	 * @param text
	 *            The text.
	 */
	public void setChatLine(final String text) {
		gui.setChatLine(text);
	}

	/**
	 * Clear the visible channel log.
	 */
	public void clearGameLog() {
		channelManager.getVisibleChannel().clear();
	}

	/**
	 * Set the user's position.
	 *
	 * @param x
	 *            The user's X coordinate.
	 * @param y
	 *            The user's Y coordinate.
	 */
	public void setPosition(final double x, final double y) {
		positionChangeListener.positionChanged(x, y);
	}

	/**
	 * PerceptionListener for the game window.
	 *
	 * @return listener
	 */
	public IPerceptionListener getPerceptionListener() {
		return perceptionListener;
	}

	/**
	 * Get the client.
	 *
	 * @return The client.
	 */
	public StendhalClient getClient() {
		return client;
	}

	@Override
	public final SoundSystemFacade getSoundSystemFacade() {
		if (soundSystemFacade == null) {
			try {
				if ((DataLoader.getResource("data/sound/xylophone-1.ogg") != null)
						|| (DataLoader.getResource("data/music/the_old_tavern.ogg") != null)) {
					soundSystemFacade = new games.stendhal.client.sound.sound.SoundSystemFacadeImpl();
				} else {
					soundSystemFacade = new NoSoundFacade();
				}
			} catch (RuntimeException e) {
				soundSystemFacade = new NoSoundFacade();
				logger.error(e, e);
			}
		}
		return soundSystemFacade;
	}

	public void switchToSpellState(RPObject spell) {
		gui.switchToSpellState(spell);
	}
}
