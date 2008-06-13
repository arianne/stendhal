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

import games.stendhal.client.entity.User;
import games.stendhal.client.events.BuddyChangeListener;
import games.stendhal.client.events.FeatureChangeListener;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.client.update.HttpClient;
import games.stendhal.client.update.Version;
import games.stendhal.common.Debug;
import games.stendhal.common.Direction;
import games.stendhal.common.NotificationType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import marauroa.client.ClientFramework;
import marauroa.client.net.IPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.Perception;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

import org.apache.log4j.Logger;

/**
 * This class is the glue to Marauroa, it extends ClientFramework and allow us
 * to easily connect to an marauroa server and operate it easily.
 * 
 * This class should be limited to functionality independant of the UI (that
 * goes in StendhalUI or a subclass).
 */
public class StendhalClient extends ClientFramework {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalClient.class);

	private Map<RPObject.ID, RPObject> world_objects;

	private PerceptionHandler handler;

	private RPObjectChangeDispatcher rpobjDispatcher;

	private RPObject player;

	private StaticGameLayers staticLayers;

	private GameObjects gameObjects;

	protected static StendhalClient client;

	private Cache cache;

	private ArrayList<Direction> directions;

	private static final String LOG4J_PROPERTIES = "data/conf/log4j.properties";

	protected IGameScreen screen;

	// protected PerceptionListenerMulticaster listeners;

	private String userName = "";

	private UserContext userContext;

	public Vector<String> whoplayers;

	/**
	 * The amount of content yet to be transfered.
	 */
	private int contentToLoad;

	/**
	 * Whether the client is in a batch update.
	 */
	private boolean batchUpdate;

	public void generateWhoPlayers(String text) {

		Matcher matcher = Pattern.compile("^[0-9]+ Players online:( .+)$").matcher(
				text);

		if (matcher.find()) {
			String[] names = matcher.group(1).split("\\s+");

			whoplayers.removeAllElements();
			for (int i = 0; i < names.length; i++) {
				/*
				 * NOTE: On the future Players names won't have any non ascii
				 * character.
				 */
				matcher = Pattern.compile(
						"^([-_a-zA-Z0-9äöüßÄÖÜ]+)\\([0-9]+\\)$").matcher(
						names[i]);
				if (matcher.find()) {
					whoplayers.addElement(matcher.group(1));
				}
			}
		}

	}

	public static StendhalClient get() {
		if (client == null) {
			client = new StendhalClient(LOG4J_PROPERTIES);
		}

		return client;
	}

	protected StendhalClient(String loggingProperties) {
		super(loggingProperties);

		// TODO: Move this to the UI init code
		SoundSystem.get();

		world_objects = new HashMap<RPObject.ID, RPObject>();
		staticLayers = new StaticGameLayers();
		gameObjects = GameObjects.createInstance(staticLayers);
		userContext = new UserContext();

		rpobjDispatcher = new RPObjectChangeDispatcher(gameObjects, userContext);

		handler = new PerceptionHandler(new StendhalPerceptionListener());

		cache = new Cache();
		cache.init();

		directions = new ArrayList<Direction>(2);
	}

	@Override
	protected String getGameName() {
		return "stendhal";
	}

	@Override
	protected String getVersionNumber() {
		return stendhal.VERSION;
	}

	public void setScreen(IGameScreen screen) {
		this.screen = screen;
	}

	public StaticGameLayers getStaticGameLayers() {
		return staticLayers;
	}

	public GameObjects getGameObjects() {
		return gameObjects;
	}

	public RPObject getPlayer() {
		return player;
	}

	/**
	 * Check if the client is in the middle of a batch update. A batch update
	 * starts when a content transfer starts and end on the first perception
	 * event.
	 * 
	 * @return <code>true</code> if in a batch update.
	 */
	public boolean isInBatchUpdate() {
		return batchUpdate;
	}

	/**
	 * Handle sync events before they are dispatched.
	 * 
	 * @param zoneid
	 *            The zone entered.
	 */
	protected void onBeforeSync(final String zoneid) {
		/*
		 * Simulate object disassembly
		 */
		for (RPObject object : world_objects.values()) {
			if (object != player) {
				rpobjDispatcher.dispatchRemoved(object, false);
			}
		}

		if (player != null) {
			rpobjDispatcher.dispatchRemoved(player, true);
		}

		gameObjects.clear();

		// If player exists, notify zone leaving.
		if (!User.isNull()) {
			WorldObjects.fireZoneLeft(User.get().getID().getZoneID());
		}

		// Notify zone entering.
		WorldObjects.fireZoneEntered(zoneid);
	}

	/**
	 * connect to the Stendhal game server and if successful, check, if the
	 * server runs StendhalHttpServer extension. In that case it checks, if
	 * server version equals the client's.
	 * 
	 * @throws IOException
	 */
	@Override
	public void connect(String host, int port) throws IOException {
		super.connect(host, port);
		// if connect was successful try if server has http service, too
		String testServer = "http://" + host + "/";
		HttpClient httpClient = new HttpClient(testServer + "stendhal.version");
		String version = httpClient.fetchFirstLine();
		if (version != null) {
			if (!Version.checkCompatibility(version, stendhal.VERSION)) {
				// custom title, warning icon
				JOptionPane.showMessageDialog(
						null,
						"Your client may not function properly.\nThe version of this server is "
								+ version
								+ " but your client is version "
								+ stendhal.VERSION
								+ ".\nPlease download the new version from http://arianne.sourceforge.net ",
						"Version Mismatch With Server",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	@Override
	protected void onPerception(MessageS2CPerception message) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("message: " + message);
			}

			/*
			 * End any batch updates if not transfering
			 */
			if (batchUpdate && !isInTransfer()) {
				logger.debug("Batch update finished");
				batchUpdate = false;
			}

			if (message.getPerceptionType() == Perception.SYNC) {
				onBeforeSync(message.getRPZoneID().getID());
			}

			/** This code emulate a perception loss. */
			if (Debug.EMULATE_PERCEPTION_LOSS
					&& (message.getPerceptionType() != Perception.SYNC)
					&& ((message.getPerceptionTimestamp() % 30) == 0)) {
				return;
			}

			handler.apply(message, world_objects);
		} catch (Exception e) {
			logger.error("error processing message " + message, e);
			System.exit(1);
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
		/*
		 * A batch update has begun
		 */
		batchUpdate = true;
		logger.debug("Batch update started");

		/** We clean the game object container */
		logger.debug("CLEANING static object list");
		staticLayers.clear();

		/*
		 * Set the new area name
		 */
		if (!items.isEmpty()) {
			String name = items.get(0).name;

			int i = name.indexOf('.');

			if (i == -1) {
				logger.error("Old server, please upgrade");
				return items;
			}

			staticLayers.setAreaName(name.substring(0, i));
		}

		/*
		 * Remove screen objects (like text bubbles)
		 */
		logger.debug("CLEANING screen object list");
		screen.removeAll();

		contentToLoad = 0;

		for (TransferContent item : items) {
			InputStream is = cache.getItem(item);

			if (is != null) {
				item.ack = false;

				try {
					contentHandling(item.name, is);
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e, e);
					System.exit(1);
				}
			} else {
				logger.debug("Content " + item.name
						+ " is NOT on cache. We have to transfer");
				item.ack = true;
			}

			if (item.ack) {
				contentToLoad++;
			}
		}

		return items;
	}

	/**
	 * Determine if we are in the middle of transfering new content.
	 * 
	 * @return <code>true</code> if more content is to be transfered.
	 */
	public boolean isInTransfer() {
		return (contentToLoad != 0);
	}

	private void contentHandling(String name, InputStream in)
			throws IOException, ClassNotFoundException {
		/*
		 * TODO: Encode area name into the data sent from server, so it is
		 * simpler to extract area/layer parts.
		 */
		int i = name.indexOf('.');

		if (i == -1) {
			logger.error("Old server, please upgrade");
			return;
		}

		String area = name.substring(0, i);
		String layer = name.substring(i + 1);

		staticLayers.addLayer(area, layer, in);
	}

	@Override
	protected void onTransfer(List<TransferContent> items) {
		for (TransferContent item : items) {
			try {
				cache.store(item, item.data);
				contentHandling(item.name, new ByteArrayInputStream(item.data));
			} catch (Exception e) {
				logger.error("onTransfer", e);
				System.exit(2);
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
	protected void onAvailableCharacters(String[] characters) {
		/*
		 * Check we have characters and if not offer us to create one.
		 */
		if (characters.length > 0) {
			try {
				chooseCharacter(characters[0]);
			} catch (Exception e) {
				logger.error("StendhalClient::onAvailableCharacters", e);
			}
		} else {
			RPObject template = new RPObject();
			// TODO: Account Username can be != of Character username.
			try {
				createCharacter(getAccountUsername(), template);
				// TODO: check result of createCharacter
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onServerInfo(String[] info) {
		// TODO: handle this info
	}

	@Override
	protected void onPreviousLogins(List<String> previousLogins) {

	}

	/**
	 * Add an active player movement direction.
	 * 
	 * @param dir
	 *            The direction.
	 * @param face
	 *            If to face direction only.
	 */
	public void addDirection(Direction dir, boolean face) {
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
				return;
			}

			/*
			 * Move to end
			 */
			directions.remove(idx);
		}

		directions.add(dir);

		action = new RPAction();
		action.put("type", face ? "face" : "move");
		action.put("dir", dir.get());

		send(action);
	}

	/**
	 * Remove a player movement direction.
	 * 
	 * @param dir
	 *            The direction.
	 * @param face
	 *            If to face direction only.
	 */
	public void removeDirection(Direction dir, boolean face) {
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
		action = new RPAction();
		size = directions.size();
		if (size == 0) {
			action.put("type", "stop");
		} else {
			action.put("type", face ? "face" : "move");
			action.put("dir", directions.get(size - 1).get());
		}

		send(action);
	}

	/**
	 * Stop the player.
	 */
	public void stop() {
		directions.clear();

		RPAction rpaction = new RPAction();

		rpaction.put("type", "stop");
		rpaction.put("attack", "");

		send(rpaction);
	}

	/**
	 * Handle player changes.
	 * @param object the player object
	 */
	protected void setPlayer(RPObject object) {
		/*
		 * Ignore no-changes
		 */
		if (player != object) {
			player = object;

			firePlayerAssignment(player);
		}
	}

	/*
	 * public void addPlayerChangeListener(PlayerChangeListener l) { }
	 * 
	 * 
	 * public void removePlayerChangeListener(PlayerChangeListener l) { }
	 */

	public void addFeatureChangeListener(FeatureChangeListener l) {
		userContext.addFeatureChangeListener(l);
	}

	public void removeFeatureChangeListener(FeatureChangeListener l) {
		userContext.removeFeatureChangeListener(l);
	}

	public void addBuddyChangeListener(BuddyChangeListener l) {
		userContext.addBuddyChangeListener(l);
	}

	public void removeBuddyChangeListener(BuddyChangeListener l) {
		userContext.removeBuddyChangeListener(l);
	}

	protected void firePlayerAssignment(RPObject object) {
	}

	//
	//

	class StendhalPerceptionListener implements IPerceptionListener {

		public boolean onAdded(RPObject object) {
			rpobjDispatcher.dispatchAdded(object, isUser(object));
			return false;
		}

		public boolean onModifiedAdded(RPObject object, RPObject changes) {
			rpobjDispatcher.dispatchModifyAdded(object, changes, false);
			return true;
		}

		public boolean onModifiedDeleted(RPObject object, RPObject changes) {
			rpobjDispatcher.dispatchModifyRemoved(object, changes, false);
			return true;
		}

		public boolean onDeleted(RPObject object) {
			rpobjDispatcher.dispatchRemoved(object, isUser(object));
			return false;
		}

		public boolean onMyRPObject(RPObject added, RPObject deleted) {
			try {
				RPObject.ID id = null;

				if (added != null) {
					id = added.getID();
				}

				if (deleted != null) {
					id = deleted.getID();
				}

				if (id == null) {
					// Unchanged.
					return true;
				}

				RPObject object = world_objects.get(id);

				setPlayer(object);

				if (deleted != null) {
					rpobjDispatcher.dispatchModifyRemoved(object, deleted, true);
				}

				if (added != null) {
					rpobjDispatcher.dispatchModifyAdded(object, added, true);
				}
			} catch (Exception e) {
				logger.error("onMyRPObject failed, added=" + added
						+ " deleted=" + deleted, e);
			}

			return true;
		}

		public void onSynced() {
			times = 0;

			StendhalUI.get().setOffline(false);

			logger.debug("Synced with server state.");
			StendhalUI.get().addEventLine("Synchronized",
					NotificationType.CLIENT);
		}

		private int times;

		public void onUnsynced() {
			times++;

			if (times > 3) {
				logger.debug("Request resync");
				StendhalUI.get().addEventLine("Unsynced: Resynchronizing...",
						NotificationType.CLIENT);
			}
		}

		public void onException(Exception e,
				marauroa.common.net.message.MessageS2CPerception perception) {
			logger.error("perception caused an error: " + perception, e);
			System.exit(-1);
		}

		public boolean onClear() {
			return false;
		}

		public void onPerceptionBegin(byte type, int timestamp) {
		}

		public void onPerceptionEnd(byte type, int timestamp) {
		}
	}

	public void setAccountUsername(String username) {
		userName = username;
	}

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
	public boolean isUser(final RPObject object) {
		if (object.getRPClass().subclassOf("player")) {
			return getAccountUsername().equalsIgnoreCase(object.get("name"));
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
}
