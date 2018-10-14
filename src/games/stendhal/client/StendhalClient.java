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

import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import games.stendhal.client.entity.User;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.login.CharacterDialog;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.client.update.ClientGameConfiguration;
import games.stendhal.client.update.HttpClient;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;
import games.stendhal.common.Version;
import marauroa.client.BannedAddressException;
import marauroa.client.ClientFramework;
import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.Perception;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

/**
 * This class is the glue to Marauroa, it extends ClientFramework and allows us
 * to easily connect to an marauroa server and operate it easily.
 */
public class StendhalClient extends ClientFramework {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalClient.class);

	private final Map<RPObject.ID, RPObject> worldObjects;

	private final PerceptionHandler handler;

	private final RPObjectChangeDispatcher rpobjDispatcher;

	private final StaticGameLayers staticLayers;

	private final GameObjects gameObjects;

	protected static StendhalClient client;

	private final Cache cache;

	private final List<Direction> directions;

	/** List of keys that are currently in the "pressed" state. */
	private static List<Integer> pressedStateKeys = new ArrayList<Integer>();

	private static final String LOG4J_PROPERTIES = "data/conf/log4j.properties";

	private String userName = "";

	private String character;

	private final UserContext userContext;

	/** Listeners to be called when zone changes. */
	private final List<ZoneChangeListener> zoneChangeListeners = new ArrayList<ZoneChangeListener>();

	/**
	 * The amount of content yet to be transfered.
	 */
	private int contentToLoad;

	/**
	 * Whether the client is in a batch update.
	 */
	private boolean inBatchUpdate;
	private final ReentrantLock drawingSemaphore = new ReentrantLock();

	/** The zone currently under loading. */
	private Zone currentZone;

	private JFrame splashScreen;

	/**
	 * Get the client instance.
	 *
	 * @return client instance
	 */
	public static StendhalClient get() {
		return client;
	}

	/**
	 * Set the client instance to <code>null</code>.
	 */
	public static void resetClient() {
		client = null;
	}

	/**
	 * Create a new StendhalClient.
	 *
	 * @param userContext
	 * @param perceptionDispatcher
	 */
	StendhalClient(final UserContext userContext, final PerceptionDispatcher perceptionDispatcher) {
		super(LOG4J_PROPERTIES);
		client = this;
		ClientSingletonRepository.setClientFramework(this);

		worldObjects = new HashMap<RPObject.ID, RPObject>();
		staticLayers = new StaticGameLayers();
		gameObjects = GameObjects.createInstance(staticLayers);
		this.userContext = userContext;

		rpobjDispatcher = new RPObjectChangeDispatcher(gameObjects, userContext);
		final PerceptionToObject po = new PerceptionToObject();
		perceptionDispatcher.register(po);
		StendhalPerceptionListener perceptionListener = new StendhalPerceptionListener(perceptionDispatcher, rpobjDispatcher, userContext, worldObjects);
		handler = new PerceptionHandler(perceptionListener);

		cache = new Cache();
		cache.init();

		directions = new ArrayList<Direction>(2);
	}

	@Override
	protected String getGameName() {
		return stendhal.GAME_NAME.toLowerCase(Locale.ENGLISH);
	}

	@Override
	protected String getVersionNumber() {
		return stendhal.VERSION;
	}

	/**
	 * Get the map layers.
	 *
	 * @return map layers
	 */
	public StaticGameLayers getStaticGameLayers() {
		return staticLayers;
	}

	/**
	 * Get the game objects container.
	 *
	 * @return game objects
	 */
	public GameObjects getGameObjects() {
		return gameObjects;
	}

	/**
	 * Handle sync events before they are dispatched.
	 */
	private void onBeforeSync() {
		/*
		 * Simulate object disassembly
		 */
		for (final RPObject object : worldObjects.values()) {
			if (object != userContext.getPlayer()) {
				rpobjDispatcher.dispatchRemoved(object);
			}
		}

		if (userContext.getPlayer() != null) {
			rpobjDispatcher.dispatchRemoved(userContext.getPlayer());
		}

		gameObjects.clear();
	}

	@Override
	protected void onPerception(final MessageS2CPerception message) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("message: " + message);
			}

			if (message.getPerceptionType() == Perception.SYNC) {
				onBeforeSync();
			}

			handler.apply(message, worldObjects);
		} catch (final Exception e) {
			logger.error("error processing message " + message, e);
		}

		if (inBatchUpdate && (contentToLoad == 0)) {
			validateAndUpdateZone(currentZone);
			inBatchUpdate = false;
			/*
			 * Rapid zone change can cause two content transfers in a row.
			 * Similarly a zone update that happens when the player changes
			 * zones. Only the latter will ever get a perception, so we need to
			 * release any locks we are holding, or the game screen will be
			 * permanently frozen.
			 */
			while (drawingSemaphore.getHoldCount() > 0) {
				drawingSemaphore.unlock();
			}
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(final List<TransferContent> items) {
		// A batch update has begun
		inBatchUpdate = true;
		logger.debug("Batch update started");

		String oldZone = (currentZone != null) ? currentZone.getName() : null;

		// Set the new area name
		for (TransferContent item : items) {
			final String name = item.name;
			final int i = name.indexOf(".0_floor");
			if (i > -1) {
				currentZone = new Zone(name.substring(0, i));
				break;
			}
		}

		// Is it just a reload for new coloring?
		if (currentZone != null) {
			boolean isZoneChange = !currentZone.getName().equals(oldZone);
			currentZone.setUpdate(!isZoneChange);
			if (isZoneChange) {
				logger.debug("Preparing for zone change");
				// Only true zone changes need to lock drawing
				drawingSemaphore.lock();
				staticLayers.clear();
				for (ZoneChangeListener listener : zoneChangeListeners) {
					listener.onZoneChange(currentZone);
				}
			}
		}

		contentToLoad = 0;

		for (final TransferContent item : items) {
			if ((item.name != null) && item.name.endsWith(".data_map")) {
				// Tell the zone to be invalid until the data layer has been
				// added
				currentZone.requireDataLayer();
			}
			final InputStream is = cache.getItem(item);

			if (is != null) {
				item.ack = false;

				try {
					contentHandling(item.name, is);
					is.close();
				} catch (final Exception e) {
					logger.error(e, e);

					// request retransmission
					item.ack = true;
				}
			} else {
				logger.debug("Content " + item.name + " is NOT on cache. We have to transfer");
				item.ack = true;
			}

			if (item.ack) {
				contentToLoad++;
			}
		}

		return items;
	}

	/**
	 * Add a listener to be called when the player changes zone.
	 *
	 * @param listener added listener
	 */
	public void addZoneChangeListener(ZoneChangeListener listener) {
		zoneChangeListeners.add(listener);
	}

	/**
	 * Determine if we are in the middle of transferring new content that should
	 * suppress drawing during the transfer.
	 *
	 * @return <code>true</code> if more content is to be transfered.
	 */
	public boolean isInTransfer() {
		// Keep drawing normally during coloring update transfers.
		return ((contentToLoad != 0) && (currentZone != null) && !currentZone.isUpdate());
	}

	/**
	 * Load layer data.
	 *
	 * @param name name of the layer
	 * @param in data source
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void contentHandling(final String name, final InputStream in) throws IOException, ClassNotFoundException {
		final int i = name.indexOf('.');

		if (name.endsWith(".jar")) {
			in.close();
			DataLoader.addJarFile(cache.getFilename(name));
		} else {
			if (i > -1) {
				final String layer = name.substring(i + 1);
				currentZone.addLayer(layer, in);
			}
		}
	}

	@Override
	protected void onTransfer(final List<TransferContent> items) {
		for (final TransferContent item : items) {
			try {
				if (item.cacheable) {
					cache.store(item, item.data);
				}
				/*
				 * For laggy connections: in two fast consecutive zone changes
				 * it can happen that the data for the first zone arrives only
				 * after the data for the second has been already offered. Ie.
				 * the zone has changed but the client receives data for the
				 * previous zone. Discard that and keep waiting for the real
				 * data.
				 */
				if (item.name.startsWith(currentZone.getName() + ".")) {
					contentHandling(item.name, new ByteArrayInputStream(item.data));
				} else {
					// Still waiting for the real data
					contentToLoad++;
				}
			} catch (final Exception e) {
				logger.error("onTransfer", e);
			}
		}

		contentToLoad -= items.size();

		/*
		 * Sanity check
		 */
		if (contentToLoad < 0) {
			logger.warn("More data transfer than expected");
			contentToLoad = 0;
		}
	}

	@Override
	protected void onAvailableCharacters(final String[] characters) {
		// see onAvailableCharacterDetails
	}

	@Override
	protected void onAvailableCharacterDetails(final Map<String, RPObject> characters) {

		// if there are no characters, create one with the specified name automatically
		if (characters.isEmpty()) {
			if (character == null) {
				character = getAccountUsername();
			}
			logger.warn("The requested character is not available, trying to create character " + character);
			final RPObject template = new RPObject();
			try {
				final CharacterResult result = createCharacter(character, template);
				if (result.getResult().failed()) {
					logger.error(result.getResult().getText());
					JOptionPane.showMessageDialog(splashScreen, result.getResult().getText());
				}
			} catch (final Exception e) {
				logger.error(e, e);
			}
			return;
		}

		// autologin if a valid character was specified.
		if ((character != null) && (characters.containsKey(character))) {
			try {
				chooseCharacter(character);
				stendhal.setDoLogin();
				if (splashScreen != null) {
					splashScreen.dispose();
				}
			} catch (final Exception e) {
				logger.error("StendhalClient::onAvailableCharacters", e);
			}
			return;
		}

		// show character dialog
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CharacterDialog(characters, splashScreen);
			}
		});
	}

	@Override
	protected void onServerInfo(final String[] info) {
		// ignore server response
	}

	@Override
	protected void onPreviousLogins(final List<String> previousLogins) {
		GameLoop.get().runOnce(new Runnable() {
			@Override
			public void run() {
				for (String login : previousLogins) {
					NotificationType type = (login.indexOf("FAILED") != -1) ? NotificationType.WARNING : NotificationType.SERVER;
					ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine("Previous " + login, type));
				}
			}
		});
	}

	/**
	 * Add an active player movement direction.
	 *
	 * @param dir
	 *            The direction.
	 * @param face If to face direction only.
	 *
	 * @return <code>true</code> if an action was sent, otherwise <code>false</code>
	 */
	public boolean addDirection(final Direction dir, final boolean face) {
		RPAction action;
		Direction odir;
		int idx;

		/*
		 * Cancel existing opposite directions
		 */
		odir = dir.oppositeDirection();

		if (directions.remove(odir)) {
			/*
			 * Send direction release
			 */
			action = new RPAction();
			action.put("type", "move");
			action.put("dir", -odir.get());

			send(action);
		}

		/*
		 * Handle existing
		 */
		idx = directions.indexOf(dir);
		if (idx != -1) {
			/*
			 * Already highest priority? Don't send to server.
			 */

			if (idx == (directions.size() - 1)) {
				logger.debug("Ignoring same direction: " + dir);
				return false;
			}

			/*
			 * Move to end
			 */
			directions.remove(idx);
		}

		directions.add(dir);

		if (face) {
			action = new FaceRPAction(dir);
		} else {
			action = new MoveRPAction(dir);
		}

		send(action);

		return true;
	}


/**
	 * Remove a player movement direction.
	 *
	 * @param dir
	 *            The direction.
	 * @param face
	 *            If to face direction only.
	 */
	public void removeDirection(final Direction dir, final boolean face) {
		RPAction action;
		int size;

		/*
		 * Send direction release
		 */
		action = new RPAction();
		action.put("type", "move");
		action.put("dir", -dir.get());

		send(action);

		/*
		 * Client side direction tracking (for now)
		 */
		directions.remove(dir);

		// Existing one reusable???
		size = directions.size();
		if (size == 0) {
			action = new RPAction();
			action.put("type", "stop");
		} else {
			if (face) {
				action = new FaceRPAction(directions.get(size - 1));
			} else {
				action = new MoveRPAction(directions.get(size - 1));
			}
		}

		send(action);
	}

	/**
	 * Stop the player.
	 */
	public void stop() {
		directions.clear();

		final RPAction rpaction = new RPAction();

		rpaction.put("type", "stop");
		rpaction.put("attack", "");

		send(rpaction);
	}

	/**
	 * Set the account name.
	 *
	 * @param username account name
	 */
	public void setAccountUsername(final String username) {
		userContext.setName(username);
		userName = username;
	}

	/**
	 * Get the character name.
	 *
	 * @return character name
	 */
	public String getCharacter() {
		return character;
	}

	/**
	 * Set the character name.
	 *
	 * @param character name
	 */
	public void setCharacter(String character) {
		this.character = character;
	}

	/**
	 * Set the splash screen window. Used for transient windows.
	 *
	 * @param splash first screen window
	 */
	public void setSplashScreen(JFrame splash) {
		splashScreen = splash;
	}

	/**
	 * Get the account name.
	 *
	 * @return account name
	 */
	public String getAccountUsername() {
		return userName;
	}

	/**
	 * Check to see if the object is the connected user. This is an ugly hack
	 * needed because the perception protocol distinguishes between normal and
	 * private (my) object changes, but not full add/removes.
	 *
	 * @param object
	 *            An object.
	 *
	 * @return <code>true</code> if it is the user object.
	 */
	boolean isUser(final RPObject object) {
		if (object.getRPClass().subclassOf("player")) {
			return getCharacter().equalsIgnoreCase(object.get("name"));
		} else {
			return false;
		}
	}

	/**
	 * Return the Cache instance.
	 *
	 * @return cache
	 */
	public Cache getCache() {
		return cache;
	}

	/**
	 * Action for moving by direction.
	 */
	private static final class MoveRPAction extends RPAction {
		/**
		 * Create a MoveRPAction.
		 *
		 * @param dir movement direction
		 */
		private MoveRPAction(final Direction dir) {
			put("type", "move");
			put("dir", dir.get());
		}
	}

	/**
	 * Action for turning the player.
	 */
	private static final class FaceRPAction extends RPAction {
		/**
		 * Create a FaceRPAction.
		 *
		 * @param dir looking direction
		 */
		private FaceRPAction(final Direction dir) {
			put("type", "face");
			put("dir", dir.get());
		}
	}

	/**
	 * Get the RPObject of the user.
	 *
	 * @return player object
	 */
	public RPObject getPlayer() {
		return userContext.getPlayer();
	}

	@Override
	public synchronized boolean chooseCharacter(String character)
			throws TimeoutException, InvalidVersionException,
			BannedAddressException {
		boolean res = super.chooseCharacter(character);
		if (res) {
			this.character = character;
		}
		return res;
	}

	/**
	 * Release the drawing semaphore.
	 */
	public void releaseDrawingSemaphore() {
		drawingSemaphore.unlock();
	}

	/**
	 * Try to acquire the drawing semaphore.
	 *
	 * @return <code>true</code> if the semaphore was acquired, otherwise
	 * 	<code>false</code>
	 */
	public boolean tryAcquireDrawingSemaphore() {
		return drawingSemaphore.tryLock();
	}

	/**
	 * Interface for listeners that need to be informed when the user is
	 * changing zone.
	 */
	public interface ZoneChangeListener {
		/**
		 * Called when the user is changing zone.
		 *
		 * @param zone the new zone to be changed to. <b>This is not guaranteed
		 * 	to have complete zone data at this stage.</b>
		 */
		void onZoneChange(Zone zone);
		/**
		 * Called when the user has changed zone.
		 *
		 * @param zone the new zone
		 */
		void onZoneChangeCompleted(Zone zone);
		/**
		 * Called when the zone is updated, such as when the coloring changes.
		 *
		 * @param zone the updated zone
		 */
		void onZoneUpdate(Zone zone);
	}

	/**
	 * Validate a zone (prepare the tilesets), and set it as the current zone.
	 * Zones that are just updates (changed colors, for example), get validated
	 * in a background thread.
	 *
	 * @param zone zone to be validated
	 */
	private void validateAndUpdateZone(final Zone zone) {
		// Build the tileset data in a background thread for updates, so that
		// the client does not pause when zone colors change.
		if (zone.isUpdate()) {
			Thread worker = new Thread() {
				@Override
				public void run() {
					zone.validate();
					// Push "zone change" back to the game loop, so that zone
					// name checking works correctly, and that there aren't two
					// zone changes happening from two different threads.
					GameLoop.get().runOnce(new Runnable() {
						@Override
						public void run() {
							if (!zone.getName().equals(staticLayers.getAreaName())) {
								/*
								 * The player has changed zones while the zone,
								 * update was running. Just throw the zone away
								 * as outdated.
								 */
								return;
							}
							staticLayers.setZone(zone);
							for (ZoneChangeListener listener : zoneChangeListeners) {
								listener.onZoneUpdate(zone);
							}
						}
					});
				}
			};
			worker.start();
		} else {
			zone.validate();
			staticLayers.setZone(zone);
			for (ZoneChangeListener listener : zoneChangeListeners) {
				listener.onZoneChangeCompleted(zone);
			}
		}
	}

	/**
	 * Connect to the server, and if our version is too outdated, display a message.
	 *
	 * @param host host name
	 * @param port host port
	 * @throws IOException in case of an input/output error
	 */
	@Override
	public void connect(final String host, final int port) throws IOException {
		String gameName = ClientGameConfiguration.get("GAME_NAME").toLowerCase(Locale.ENGLISH);

		// include gamename, so that arianne.sf.net can ignore non stendhal games
		// include server name and port because we want to support different versions for
		// the main server and the test server
		String url = "http://arianne.sourceforge.net/versioncheck/"
				+ URLEncoder.encode(gameName, "UTF-8") + "/"
				+ URLEncoder.encode(host, "UTF-8") + "/"
				+ URLEncoder.encode(Integer.toString(port), "UTF-8") + "/"
				+ URLEncoder.encode(Version.getVersion(), "UTF-8");
		HttpClient httpClient = new HttpClient(url);
		String message = httpClient.fetchFirstLine();
		if ((message != null) && (message.trim().length() > 0)) {
			JOptionPane.showMessageDialog(splashScreen,
				new JLabel(message), "Version Check",
				JOptionPane.WARNING_MESSAGE);
		}

		super.connect(host, port);
	}

	@Override
	public void send(RPAction action) {

		// work around a bug in the chat-action definition in 0.98 and below
		String type = action.get("type");
		if (serverVersionAtLeast("0.99") && (RPClass.getRPClass(type) != null)) {
			action.setRPClass(type);
			action.remove("type");
		}
		super.send(action);
	}

	/**
	 * Check if the connected server is of at least as recent as the specified
	 * version.
	 *
	 * @param required string representation of required server version
	 * @return <code>true</code> if the server is new enough, or the version
	 * is unknown, <code>false</code> if the server is older than the required
	 * version
	 */
	public static boolean serverVersionAtLeast(String required) {
		String serverVersion = User.getServerRelease();
		return (serverVersion == null) || (Version.compare(serverVersion, required) >= 0);
	}

	/**
	 * Check if a keyboard key is in the "pressed" state.
	 *
	 * @param keyCode
	 *        The integer code for the key
	 * @return
	 *         <b>true</b> if code is found in pressedStateKeys list
	 */
	public boolean keyIsPressed(final int keyCode) {
		return pressedStateKeys.contains(keyCode);
	}

	/**
	 * Check if any direction key is in "pressed" state.
	 *
	 * @return
	 *         Direction key found in pressedStateKeys list
	 */
	public boolean directionKeyIsPressed() {
		return pressedStateKeys.contains(KeyEvent.VK_UP)
				|| pressedStateKeys.contains(KeyEvent.VK_DOWN)
				|| pressedStateKeys.contains(KeyEvent.VK_LEFT)
				|| pressedStateKeys.contains(KeyEvent.VK_RIGHT)
				|| pressedStateKeys.contains(KeyEvent.VK_KP_LEFT)
				|| pressedStateKeys.contains(KeyEvent.VK_KP_RIGHT)
				|| pressedStateKeys.contains(KeyEvent.VK_KP_UP)
				|| pressedStateKeys.contains(KeyEvent.VK_KP_DOWN);
	}

	/**
	 * Add a keypress to pressedStateKeys list.
	 *
	 * @param keyCode
	 *        Key to add
	 */
	public void onKeyPressed(final int keyCode) {
		if (!pressedStateKeys.contains(keyCode)) {
			pressedStateKeys.add(keyCode);
		}
	}

	/**
	 * Remove a keypress from pressedStateKeys list.
	 *
	 * @param keyCode
	 *        Key to remove
	 */
	public void onKeyReleased(final int keyCode) {
		if (pressedStateKeys.contains(keyCode)) {
			pressedStateKeys.removeAll(Collections.singleton(keyCode));
		} else {
			logger.warn("Released key " + Integer.toString(keyCode)
					+ " was not found in pressedStateKeys list");
		}
	}
}
