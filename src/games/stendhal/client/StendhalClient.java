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
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.sound.SoundSystem;
import games.stendhal.client.update.HttpClient;
import games.stendhal.client.update.Version;
import games.stendhal.common.Debug;
import games.stendhal.common.Direction;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
import marauroa.client.ariannexp;
import marauroa.client.net.DefaultPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.Perception;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.net.MessageS2CPerception;
import marauroa.common.net.TransferContent;

import org.apache.log4j.Logger;

/**
 * This class is the glue to Marauroa, it extends ariannexp and allow us to
 * easily connect to an marauroa server and operate it easily.
 *
 * This class should be limited to functionality independant of the UI
 * (that goes in StendhalUI or a subclass). 
 */
public class StendhalClient extends ariannexp {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalClient.class);

	private Map<RPObject.ID, RPObject> world_objects;

	private PerceptionHandler handler;

	private RPObject player;

	private StaticGameLayers staticLayers;

	private GameObjects gameObjects;

	private static StendhalClient client=null;

	private Cache cache;

	private ArrayList<Direction> directions;

	private static final String LOG4J_PROPERTIES = "data/conf/log4j.properties";
	protected GameScreen screen;

	protected PerceptionListenerMulticaster listeners;

	private String userName="";

	private UserContext	userContext;

	public Vector <String> whoplayers;
	
	public void generateWhoPlayers(String text){
		
		Matcher matcher = Pattern.compile("^[0-9]+ Players online:( .+)$").matcher(text);

		if (matcher.find()) {
			String[] nombres = matcher.group(1).split(" ");

			whoplayers.removeAllElements();
			for (int i=0;i<nombres.length;i++){
				matcher = Pattern.compile("^([-_a-zA-Z0-9]+)\\([0-9]+\\)$").matcher(nombres[i]);;
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

	/**
	 * For testing purpose durkham 07.03.2007 //TODO: try to get rid of this
	 * without destroying the tests
	 */

	protected StendhalClient() {
		super(new String());
	}

	private StendhalClient(String loggingProperties) {
		super(loggingProperties);

		SoundSystem.get();

		world_objects = new HashMap<RPObject.ID, RPObject>();
		staticLayers = new StaticGameLayers();
		gameObjects = GameObjects.createInstance(staticLayers);
		userContext = new UserContext();

		listeners = new PerceptionListenerMulticaster();
		listeners.addListener(new StendhalPerceptionListener());

		handler = new PerceptionHandler(listeners);

		cache = new Cache();
		cache.init();

		directions = new ArrayList<Direction>(2);
	}


	public void addPerceptionListener(IPerceptionListener listener) {
		listeners.addListener(listener);
	}


	public void removePerceptionListener(IPerceptionListener listener) {
		listeners.removeListener(listener);
	}


	@Override
	protected String getGameName() {
		return "stendhal";
	}

	@Override
	protected String getVersionNumber() {
		return stendhal.VERSION;
	}

	public void setScreen(GameScreen screen) {
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
	 * connect to the Stendhal game server and if successfull, check, if the
	 * server runs StendhalHttpServer extension. In that case it checks, if
	 * server version equals the client's.
	 */
	@Override
	public void connect(String host, int port) throws java.net.SocketException {
		this.connect(host, port, true);
	}

	/**
	 * connect to the Stendhal game server and if successfull, check, if the
	 * server runs StendhalHttpServer extension. In that case it checks, if
	 * server version equals the client's.
	 */
	@Override
	public void connect(String host, int port, boolean protocol) throws java.net.SocketException {
		super.connect(host, port, true);
		// if connect was successfull try if server has http service, too
		String testServer = "http://" + host + "/";
		HttpClient httpClient = new HttpClient(testServer + "stendhal.version");
		String version = httpClient.fetchFirstLine();
		if (version != null) {
			if (!Version.checkCompatibility(version, stendhal.VERSION)) {
				// custom title, warning icon
				JOptionPane.showMessageDialog(null,
				        "Your client may not function properly.\nThe version of this server is " + version
				                + " but your client is version " + stendhal.VERSION
				                + ".\nPlease download the new version from http://arianne.sourceforge.net ",
				        "Version Mismatch With Server", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	@Override
	protected void onPerception(MessageS2CPerception message) {
		try {
			Log4J.startMethod(logger, "onPerception");
			if (logger.isDebugEnabled()) {
				logger.debug("message: " + message);
			}

			if (message.getPerceptionType() == 1/* Perception.SYNC */) {
				logger.debug("UPDATING screen position");

				// If player exists, notify zone leaving.
				if (!User.isNull()) {
					WorldObjects.fireZoneLeft(User.get().getID().getZoneID());
				}

				// Notify zone entering.
				WorldObjects.fireZoneEntered(message.getRPZoneID().getID());

				String zoneid = message.getRPZoneID().getID();
				staticLayers.setRPZoneLayersSet(zoneid);
				screen.setMaxWorldSize(staticLayers.getWidth(), staticLayers.getHeight());

				/** And finally place player in screen */
				Graphics2D g = screen.expose();
				g.setColor(Color.BLACK);
				g.fill(new Rectangle(0, 0, j2DClient.SCREEN_WIDTH, j2DClient.SCREEN_HEIGHT));
			}

			/** This code emulate a perception loss. */
			if (Debug.EMULATE_PERCEPTION_LOSS && (message.getPerceptionType() != Perception.SYNC)
			        && ((message.getPerceptionTimestamp() % 30) == 0)) {
				return;
			}

			handler.apply(message, world_objects);
		} catch (Exception e) {
			logger.fatal("error processing message " + message, e);
			System.exit(1);
		} finally {
			Log4J.finishMethod(logger, "onPerception");
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
		Log4J.startMethod(logger, "onTransferREQ");

		/** We clean the game object container */
		logger.debug("CLEANING static object list");
		staticLayers.clear();

		// TODO: Does this go back somewhere, or did I just enter
		// the wrong variable name???
		gameObjects.clear();

		for (TransferContent item : items) {

			InputStream is = cache.getItem(item);

			if (is != null) {
				item.ack = false;
				try {
					contentHandling(item.name, is);
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
					logger.fatal(e,e);
					System.exit(1);
				}
			} else {
				logger.debug("Content " + item.name + " is NOT on cache. We have to transfer");
				item.ack = true;
			}
		}
		Log4J.finishMethod(logger, "onTransferREQ");

		return items;
	}

	private void contentHandling(String name, InputStream in) throws IOException, ClassNotFoundException {
		staticLayers.addLayer(name, in);
		screen.setMaxWorldSize(staticLayers.getWidth(), staticLayers.getHeight());
	}

	@Override
	protected void onTransfer(List<TransferContent> items) {
		Log4J.startMethod(logger, "onTransfer");
		for (TransferContent item : items) {
			try {
				cache.store(item, item.data);
				contentHandling(item.name, new ByteArrayInputStream(item.data));
			} catch (Exception e) {
				logger.fatal("onTransfer", e);
				System.exit(2);
			}
		}
		Log4J.finishMethod(logger, "onTransfer");
	}

	@Override
	protected void onAvailableCharacters(String[] characters) {
		Log4J.startMethod(logger, "onAvailableCharacters");
		try {
			chooseCharacter(characters[0]);
		} catch (Exception e) {
			logger.error("StendhalClient::onAvailableCharacters", e);
		}

		Log4J.finishMethod(logger, "onAvailableCharacters");
	}

	@Override
	protected void onServerInfo(String[] info) {
		// TODO: handle this info
	}

	@Override
	protected void onError(int code, String reason) {
		logger.error("got error code: " + code + " reason: " + reason);
	}


	/**
	 * Add an active player movement direction.
	 *
	 * @param	dir		The direction.
	 * @param	face		If to face direction only.
	 */
	public void addDirection(Direction dir, boolean face) {
		RPAction action;
		Direction odir;
		int idx;

		/*
		 * Cancel existing opposite directions
		 */
		odir = dir.oppositeDirection();

		if(directions.remove(odir)) {
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
		if((idx = directions.indexOf(dir)) != -1) {
			/*
			 * Already highest priority? Don't send to server.
			 */
			if(idx == (directions.size() - 1)) {
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
	 * @param	dir		The direction.
	 * @param	face		If to face direction only.
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

		if ((size = directions.size()) == 0) {
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
	 * Handle player changes
	 */
	protected void setPlayer(RPObject object) {
		/*
		 * Ignore no-changes
		 */
		if(player != object) {
			player = object;

			firePlayerAssignment(player);
		}
	}



/*
	public void addPlayerChangeListener(PlayerChangeListener l) {
	}


	public void removePlayerChangeListener(PlayerChangeListener l) {
	}
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


	/**
	 * Dispatch object added event.
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void dispatchAdded(RPObject object, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") added to client");
			fireAdded(object, user);
		} catch (Exception e) {
			logger.error("onAdded failed, object is " + object, e);
		}
	}


	/**
	 * Dispatch object removed event.
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void dispatchRemoved(RPObject object, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") removed from client");
			fireRemoved(object, user);
		} catch (Exception e) {
			logger.error("onDeleted failed, object is " + object, e);
		}
	}


	/**
	 * Dispatch object added/changed attribute(s) event.
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 * @param	user		If this is the private user object.
	 */
	protected void dispatchModifyAdded(RPObject object, RPObject changes, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") modified in client");
			fireChangedAdded(object, changes, user);
			object.applyDifferences(changes, null);
		} catch (Exception e) {
			logger.debug("onModifiedAdded failed, object is " + object + ", changes is " + changes, e);
		}

	}


	/**
	 * Dispatch object removed attribute(s) event.
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 * @param	user		If this is the private user object.
	 */
	protected void dispatchModifyRemoved(RPObject object, RPObject changes, boolean user) {
		try {
			logger.debug("Object(" + object.getID() + ") modified in client");
			logger.debug("Original(" + object + ") modified in client");

			fireChangedRemoved(object, changes, user);
			object.applyDifferences(null, changes);

			logger.debug("Modified(" + object + ") modified in client");
			logger.debug("Changes(" + changes + ") modified in client");
		} catch (Exception e) {
			logger.error("onModifiedDeleted failed, object is " + object + ", changes is " + changes, e);
		}
	}


	/**
	 * Notify listeners that an object was added.
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void fireAdded(RPObject object, boolean user) {
		gameObjects.onAdded(object);
	}


	/**
	 * Notify listeners that an object was removed.
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void fireRemoved(RPObject object, boolean user) {
		gameObjects.onRemoved(object);
	}


	/**
	 * Notify listeners that an object changed attributes(s).
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void fireChanged(RPObject object, boolean user) {
// After listener split/changed
//		gameObjects.onChanged(object);
	}


	/**
	 * Notify listeners that an object added/changed attribute(s).
	 * This will cascade down slot trees.
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 * @param	user		If this is the private user object.
	 */
	protected void fireChangedAdded(RPObject object, RPObject changes, boolean user) {
		gameObjects.onChangedAdded(object, changes);

		if(user) {
			userContext.onChangedAdded(object, changes);
		}

		/*
		 * Walk each changed slot
		 */
		for(RPSlot cslot : changes.slots()) {
			if(cslot.size() == 0) {
				continue;
			}

			String slotName = cslot.getName();
			RPSlot slot;

			/*
			 * Find the original slot entry (if any)
			 */
			if(object.hasSlot(slotName)) {
				slot = object.getSlot(cslot.getName());
			} else {
				slot = null;
			}

			/*
			 * Walk the slot changes
			 */
			for(RPObject schanges : cslot) {
				RPObject.ID id = object.getID();
				RPObject sobject;

				if((slot != null) && slot.has(id)) {
					sobject = slot.get(id);
				} else {
					sobject = null;
				}

				gameObjects.onChangedAdded(object, slotName, sobject, schanges);

				if(user) {
					userContext.onChangedAdded(object, slotName, sobject, schanges);
				}

				if(sobject != null) {
					fireChangedAdded(sobject, schanges, user);
				}
			}
		}
	}


	/**
	 * Notify listeners that an object removed attribute(s).
	 * This will cascade down slot trees.
	 *
	 * @param	object		The base object.
	 * @param	changes		The changes.
	 * @param	user		If this is the private user object.
	 */
	protected void fireChangedRemoved(RPObject object, RPObject changes, boolean user) {
		gameObjects.onChangedRemoved(object, changes);

		if(user) {
			userContext.onChangedRemoved(object, changes);
		}

		/*
		 * Walk each changed slot
		 */
		for(RPSlot cslot : changes.slots()) {
			if(cslot.size() == 0) {
				continue;
			}

			String slotName = cslot.getName();
			RPSlot slot;

			/*
			 * Find the original slot entry (if any)
			 */
			if(object.hasSlot(slotName)) {
				slot = object.getSlot(cslot.getName());
			} else {
				slot = null;
			}

			/*
			 * Walk the slot changes
			 */
			for(RPObject schanges : cslot) {
				RPObject.ID id = object.getID();
				RPObject sobject;

				if((slot != null) && slot.has(id)) {
					sobject = slot.get(id);
				} else {
					sobject = null;
				}

				gameObjects.onChangedRemoved(object, slotName, sobject, schanges);

				if(user) {
					userContext.onChangedRemoved(object, slotName, sobject, schanges);
				}

				if(sobject != null) {
					fireChangedRemoved(sobject, schanges, user);
				}
			}
		}
	}

	//
	//

	class StendhalPerceptionListener extends DefaultPerceptionListener {

		@Override
		public boolean onAdded(RPObject object) {
			fireAdded(object, false);
			return false;
		}

		@Override
		public boolean onModifiedAdded(RPObject object, RPObject changes) {
			dispatchModifyAdded(object, changes, false);
			return true;
		}

		@Override
		public boolean onModifiedDeleted(RPObject object, RPObject changes) {
			dispatchModifyRemoved(object, changes, false);
			return true;
		}

		@Override
		public boolean onDeleted(RPObject object) {
			dispatchRemoved(object, false);
			return false;
		}

		@Override
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
					dispatchModifyRemoved(object, deleted, true);
				}

				if (added != null) {
					dispatchModifyAdded(object, added, true);
				}
			} catch (Exception e) {
				logger.error("onMyRPObject failed, added=" + added + " deleted=" + deleted, e);
			}

			return true;
		}

		@Override
		public int onTimeout() {
			logger.debug("Request resync because of timeout");

			StendhalUI.get().addEventLine("Timeout: Requesting synchronization", Color.gray);
			resync();
			return 0;
		}

		@Override
		public int onSynced() {
			times = 0;

			StendhalUI.get().setOffline(false);

			logger.debug("Synced with server state.");
			StendhalUI.get().addEventLine("Synchronized", Color.gray);
			return 0;
		}

		private int times;

		@Override
		public int onUnsynced() {
			times++;

			if (times > 3) {
				logger.debug("Request resync");
				StendhalUI.get().addEventLine("Unsynced: Resynchronizing...", Color.gray);
				resync();
			}
			return 0;
		}

		@Override
		public int onException(Exception e, marauroa.common.net.MessageS2CPerception perception) {
			logger.error("perception caused an error: " + perception, e);
			System.exit(-1);

			// Never executed
			return -1;
		}
	}

	public void setUserName(String username) {
		userName=username;
	    
    }

	public String getUserName() {
	 
	    return userName;
    }
}
