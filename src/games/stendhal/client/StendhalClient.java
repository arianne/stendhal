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
import games.stendhal.client.gui.StendhalFirstScreen;
import games.stendhal.client.gui.login.CharacterDialog;
import games.stendhal.client.listener.FeatureChangeListener;
import games.stendhal.client.update.HttpClient;
import games.stendhal.client.update.Version;
import games.stendhal.common.Debug;
import games.stendhal.common.Direction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import marauroa.client.BannedAddressException;
import marauroa.client.ClientFramework;
import marauroa.client.TimeoutException;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.Perception;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.net.InvalidVersionException;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

import org.apache.log4j.Logger;

/**
 * This class is the glue to Marauroa, it extends ClientFramework and allow us
 * to easily connect to an marauroa server and operate it easily.
 * 
 */
public class StendhalClient extends ClientFramework {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalClient.class);

	final Map<RPObject.ID, RPObject> world_objects;

	private final PerceptionHandler handler;

	private final RPObjectChangeDispatcher rpobjDispatcher;

	private final StaticGameLayers staticLayers;

	private final GameObjects gameObjects;

	protected static StendhalClient client;

	private final Cache cache;

	private final ArrayList<Direction> directions;

	private static final String LOG4J_PROPERTIES = "data/conf/log4j.properties";

	protected IGameScreen screen;

	private String userName = "";

	private String character = null;

	
	
	private final UserContext userContext;

	
	
	

	/**
	 * The amount of content yet to be transfered.
	 */
	private int contentToLoad;

	/**
	 * Whether the client is in a batch update.
	 */
	private boolean batchUpdate;

	private final StendhalPerceptionListener stendhalPerceptionListener;

	public static StendhalClient get() {
		return client;
	}

	public StendhalClient(final UserContext userContext, final PerceptionDispatcher perceptionDispatcher) {
		super(LOG4J_PROPERTIES);
		client = this;
		ClientSingletonRepository.setClientFramework(this);

		world_objects = new HashMap<RPObject.ID, RPObject>();
		staticLayers = new StaticGameLayers();
		gameObjects = GameObjects.createInstance(staticLayers);
		this.userContext = userContext;

		rpobjDispatcher = new RPObjectChangeDispatcher(gameObjects, userContext);
		final PerceptionToObject po = new PerceptionToObject();
		po.setObjectFactory(new ObjectFactory());
		perceptionDispatcher.register(po);
		stendhalPerceptionListener = new StendhalPerceptionListener(perceptionDispatcher, rpobjDispatcher, userContext, world_objects);
		handler = new PerceptionHandler(stendhalPerceptionListener);

		cache = new Cache();
		cache.init();

		directions = new ArrayList<Direction>(2);
	}

	@Override
	protected String getGameName() {
		return stendhal.GAME_NAME.toLowerCase();
	}

	@Override
	protected String getVersionNumber() {
		return stendhal.VERSION;
	}

	public void setScreen(final IGameScreen screen) {
		this.screen = screen;
	}

	public StaticGameLayers getStaticGameLayers() {
		return staticLayers;
	}

	public GameObjects getGameObjects() {
		return gameObjects;
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
		for (final RPObject object : world_objects.values()) {
			if (object != userContext.getPlayer()) {
				rpobjDispatcher.dispatchRemoved(object);
			}
		}

		if (userContext.getPlayer() != null) {
			rpobjDispatcher.dispatchRemoved(userContext.getPlayer());
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
	public void connect(final String host, final int port) throws IOException {
		super.connect(host, port);
		// if connect was successful try if server has http service, too
		String urlHost = host;
		if (host.indexOf(":") > -1) {
			urlHost = "[" + host + "]";
		}
		final String testServer = "http://" + urlHost + "/";
		final HttpClient httpClient = new HttpClient(testServer + "stendhal.version");
		final String version = httpClient.fetchFirstLine();
		if (version != null) {
			if (!Version.checkCompatibility(version, stendhal.VERSION)) {
				// custom title, warning icon
				JOptionPane.showMessageDialog(
						null,
						"Your client may not function properly.\nThe version of this server is "
								+ version
								+ " but your client is version "
								+ stendhal.VERSION
								+ ".\nYou can download version " + version + " from http://arianne.sourceforge.net ",
						"Version Mismatch With Server",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	@Override
	protected void onPerception(final MessageS2CPerception message) {
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
		} catch (final Exception e) {
			logger.error("error processing message " + message, e);
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(final List<TransferContent> items) {
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
			final String name = items.get(0).name;

			final int i = name.indexOf('.');

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

		for (final TransferContent item : items) {
			final InputStream is = cache.getItem(item);

			if (is != null) {
				item.ack = false;

				try {
					contentHandling(item.name, is);
					is.close();
				} catch (final Exception e) {
					e.printStackTrace();
					logger.error(e, e);

					// request retransmission
					item.ack = true;
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

	private void contentHandling(final String name, final InputStream in)
			throws IOException, ClassNotFoundException {
		final int i = name.indexOf('.');

		if (i == -1) {
			logger.error("Old server, please upgrade");
			return;
		}

		final String area = name.substring(0, i);
		final String layer = name.substring(i + 1);

		staticLayers.addLayer(area, layer, in);
	}

	@Override
	protected void onTransfer(final List<TransferContent> items) {
		batchUpdate = true;
		for (final TransferContent item : items) {
			try {
				cache.store(item, item.data);
				contentHandling(item.name, new ByteArrayInputStream(item.data));
			} catch (final Exception e) {
				logger.error("onTransfer", e);
			}
		}
		staticLayers.markAreaChanged();

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
	protected void onAvailableCharacterDetails(Map<String, RPObject> characters) {
		logger.info(characters);

		// if there are no characters, create one with the specified name automatically
		if (characters.size() == 0) {
			if (character == null) {
				character = getAccountUsername();
			}
			logger.warn("The requested character is not available, trying to create character " + character);
			final RPObject template = new RPObject();
			try {
				final CharacterResult result = createCharacter(character, template);
				if (result.getResult().failed()) {
					logger.error(result.getResult().getText());
				}
			} catch (final Exception e) {
				logger.error(e, e);
			}
			return;
		}

		// TODO: remove compatibility code after Stendhal 0.85
		if ((character == null) && (characters.size() == 1) && characters.entrySet().iterator().next().getValue().isEmpty()) {
			character = getAccountUsername();
		}


		// autologin if a valid character was specified.
		if ((character != null) && (characters.keySet().contains(character))) {
			try {
				chooseCharacter(character);
				stendhal.doLogin = true;
				if (StendhalFirstScreen.get() != null) {
					StendhalFirstScreen.get().dispose();
				}
			} catch (final Exception e) {
				logger.error("StendhalClient::onAvailableCharacters", e);
			}
			return;
		}

		// show character dialog
		new CharacterDialog(characters, StendhalFirstScreen.get());
	}

	@Override
	protected void onServerInfo(final String[] info) {
		// ignore server response
	}

	@Override
	protected void onPreviousLogins(final List<String> previousLogins) {
		// TODO: display this to the player
	}

	/**
	 * Add an active player movement direction.
	 * 
	 * @param dir
	 *            The direction.
	 * @param face
	 *            If to face direction only.
	 */
	public void addDirection(final Direction dir, final boolean face) {
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

	public void addFeatureChangeListener(final FeatureChangeListener l) {
		userContext.addFeatureChangeListener(l);
	}

	public void removeFeatureChangeListener(final FeatureChangeListener l) {
		userContext.removeFeatureChangeListener(l);
	}


	//
	//

	public void setAccountUsername(final String username) {
		userContext.setName(username);
		userName = username;
	}

	public String getCharacter() {
		return character;
	}

	public void setCharacter(String character) {
		this.character = character;
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
	static class MoveRPAction extends RPAction {
		public MoveRPAction(final Direction dir) {
			put("type", "move");
			put("dir", dir.get());
		}
	}

	static class FaceRPAction extends RPAction {
		public FaceRPAction(final Direction dir) {
			put("type", "face");
			put("dir", dir.get());
		}
	}

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

	
}
