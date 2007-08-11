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
import marauroa.common.Log4J;
import marauroa.common.Logger;
import marauroa.common.game.Perception;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.net.message.MessageS2CPerception;
import marauroa.common.net.message.TransferContent;

/**
 * This class is the glue to Marauroa, it extends ClientFramework and allow us to
 * easily connect to an marauroa server and operate it easily.
 *
 * This class should be limited to functionality independant of the UI
 * (that goes in StendhalUI or a subclass). 
 */
public class StendhalClient extends ClientFramework {

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

//	protected PerceptionListenerMulticaster listeners;

	private String userName="";

	private UserContext	userContext;

	public Vector <String> whoplayers;

	/**
	 * The amount of content yet to be transfered.
	 */
	private int contentToLoad = 0;


	public void generateWhoPlayers(String text){
		
		Matcher matcher = Pattern.compile("^[0-9]+ Players online:( .+)$").matcher(text);

		if (matcher.find()) {
			String[] nombres = matcher.group(1).split(" ");

			whoplayers.removeAllElements();
			for (int i=0;i<nombres.length;i++){
				matcher = Pattern.compile("^([-_a-zA-Z0-9]+)\\([0-9]+\\)$").matcher(nombres[i]);
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

		SoundSystem.get();

		world_objects = new HashMap<RPObject.ID, RPObject>();
		staticLayers = new StaticGameLayers();
		gameObjects = GameObjects.createInstance(staticLayers);
		userContext = new UserContext();

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
	 * @throws IOException 
	 */
	@Override
	public void connect(String host, int port) throws IOException {
		super.connect(host, port);
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
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
		/** We clean the game object container */
		logger.debug("CLEANING static object list");
		staticLayers.clear();

		// TODO: Does this go back somewhere, or did I just enter
		// the wrong variable name???
		gameObjects.clear();

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
					logger.fatal(e,e);
					System.exit(1);
				}
			} else {
				logger.debug("Content " + item.name + " is NOT on cache. We have to transfer");
				item.ack = true;
			}

			if(item.ack) {
				contentToLoad++;
			}
		}

		/*
		 * All done, or onTransfer() yet to be called?
		 */
		if(contentToLoad == 0) {
			staticLayers.invalidate();
			screen.setMaxWorldSize(staticLayers.getWidth(), staticLayers.getHeight());
			screen.clear();
			screen.center();
		}

		return items;
	}


	/**
	 * Determine if we are in the middle of transfering new content.
	 *
	 * @return	<code>true</code> if more content is to be transfered.
	 */
	public boolean isInTransfer() {
		return (contentToLoad != 0);
	}


	private void contentHandling(String name, InputStream in) throws IOException, ClassNotFoundException {
		staticLayers.addLayer(name, in);
	}

	@Override
	protected void onTransfer(List<TransferContent> items) {
		for (TransferContent item : items) {
			try {
				cache.store(item, item.data);
				contentHandling(item.name, new ByteArrayInputStream(item.data));
			} catch (Exception e) {
				logger.fatal("onTransfer", e);
				System.exit(2);
			}
		}

		contentToLoad -= items.size();

		/*
		 * Sanity check
		 */
		if(contentToLoad < 0) {
			logger.warn("More data transfer than expected");
			contentToLoad = 0;
		}

		if(contentToLoad == 0) {
			staticLayers.invalidate();
			screen.setMaxWorldSize(staticLayers.getWidth(), staticLayers.getHeight());
			screen.clear();
			screen.center();
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
	    // TODO Auto-generated method stub	    
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
			fixContainers(object);
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
			fixContainers(object);
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
			fixContainers(object);
			fixContainers(changes);
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

			fixContainers(object);
			fixContainers(changes);
			fireChangedRemoved(object, changes, user);
			object.applyDifferences(null, changes);

			logger.debug("Modified(" + object + ") modified in client");
			logger.debug("Changes(" + changes + ") modified in client");
		} catch (Exception e) {
			logger.error("onModifiedDeleted failed, object is " + object + ", changes is " + changes, e);
		}
	}


	/**
	 * Dump an object out in an easily readable format.
	 * TEMP!! TEST METHOD - USED FOR DEBUGING.
	 * Probably should be in a common util class if useful long term.
	 */
	public static void dumpObject(RPObject object) {
		System.err.println(object.getRPClass().getName() + "[" + object.getID().getObjectID() + "]");

		for(String name : object) {
			System.err.println("  " + name + ": " + object.get(name));
		}

		System.err.println();
	}


	/**
	 * Fix parent <-> child linkage.
	 * THIS WILL PROBABLY NOT BE NEEDED AFTER 2.0's FIXES.
	 */
	protected void fixContainers(final RPObject object) {
		for(RPSlot slot : object.slots()) {
			for(RPObject sobject : slot) {
				if(!sobject.isContained()) {
					logger.debug("Fixing container: " + slot);
					sobject.setContainer(object, slot);
				}

				fixContainers(sobject);
			}
		}
	}


	/**
	 * Notify listeners that an object was added.
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void fireAdded(RPObject object, boolean user) {
// TEST CODE:
//System.err.println("fireAdded()");
//dumpObject(object);
		gameObjects.onAdded(object);

// NEW CODE:
//		/*
//		 * Walk each slot
//		 */
//		for(RPSlot slot : object.slots()) {
//			for(RPObject sobject : slot) {
//				fireAdded(sobject, user);
//			}
//		}
	}


	/**
	 * Notify listeners that an object was removed.
	 *
	 * @param	object		The object.
	 * @param	user		If this is the private user object.
	 */
	protected void fireRemoved(RPObject object, boolean user) {
// TEST CODE:
//System.err.println("fireRemoved()");
//dumpObject(object);

// NEW CODE:
//		/*
//		 * Walk each slot
//		 */
//		for(RPSlot slot : object.slots()) {
//			for(RPObject sobject : slot) {
//				fireRemoved(sobject, user);
//			}
//		}

		gameObjects.onRemoved(object);
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
		 * Walk each slot
		 */
		for(RPSlot cslot : changes.slots()) {
			if(cslot.size() != 0) {
				fireChangedAdded(object, cslot, user);
			}
		}
	}


	/**
	 * Notify listeners that an object slot added/changed attribute(s).
	 * This will cascade down object trees.
	 *
	 * @param	object		The base object.
	 * @param	cslot		The changes slot.
	 * @param	user		If this is the private user object.
	 */
	protected void fireChangedAdded(RPObject object, RPSlot cslot, boolean user) {
		String slotName = cslot.getName();
		RPSlot slot;

		/*
		 * Find the original slot entry (if any)
		 */
		if(object.hasSlot(slotName)) {
			slot = object.getSlot(slotName);
		} else {
			slot = null;
		}

		/*
		 * Walk the changes
		 */
		for(RPObject schanges : cslot) {
			RPObject.ID id = object.getID();

			if((slot != null) && slot.has(id)) {
				RPObject sobject = slot.get(id);

				gameObjects.onChangedAdded(object, slotName, sobject, schanges);

				if(user) {
					userContext.onChangedAdded(object, slotName, sobject, schanges);
				}

				fireChangedAdded(sobject, schanges, user);
			} else {
//				gameObjects.onAdded(object, slotName, schanges);
//
//				if(user) {
//					userContext.onAdded(object, slotName, schanges);
//				}

if(!schanges.isContained()) {
logger.warn("!!! Not contained! - " + schanges);
}

// NEW CODE:
//				fireAdded(schanges, user);
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
		 * Walk each slot
		 */
		for(RPSlot cslot : changes.slots()) {
			if(cslot.size() != 0) {
				fireChangedRemoved(object, cslot, user);
			}
		}
	}


	/**
	 * Notify listeners that an object slot removed attribute(s).
	 * This will cascade down object trees.
	 *
	 * @param	object		The base object.
	 * @param	cslot		The changes slot.
	 * @param	user		If this is the private user object.
	 */
	protected void fireChangedRemoved(RPObject object, RPSlot cslot, boolean user) {
		String slotName = cslot.getName();

		/*
		 * Find the original slot entry
		 */
		RPSlot slot = object.getSlot(slotName);

		/*
		 * Walk the changes
		 */
		for(RPObject schanges : cslot) {
			RPObject sobject = slot.get(schanges.getID());

			/*
			 * Remove attrs vs. object [see applyDifferences()]
			 */
			if(schanges.size() > 1) {
				gameObjects.onChangedRemoved(object, slotName, sobject, schanges);

				if(user) {
					userContext.onChangedRemoved(object, slotName, sobject, schanges);
				}

				fireChangedRemoved(sobject, schanges, user);
			} else {
//				gameObjects.onRemoved(object, slotName, sobject);
//
//				if(user) {
//					userContext.onRemoved(object, slotName, sobject);
//				}

// NEW CODE:
//				fireRemoved(sobject, user);
			}
		}
	}

	//
	//

	class StendhalPerceptionListener implements IPerceptionListener {

		public boolean onAdded(RPObject object) {
			fireAdded(object, false);
			return false;
		}

		public boolean onModifiedAdded(RPObject object, RPObject changes) {
			dispatchModifyAdded(object, changes, false);
			return true;
		}

		public boolean onModifiedDeleted(RPObject object, RPObject changes) {
			dispatchModifyRemoved(object, changes, false);
			return true;
		}

		public boolean onDeleted(RPObject object) {
			dispatchRemoved(object, false);
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

		public void onSynced() {
			times = 0;

			StendhalUI.get().setOffline(false);

			logger.debug("Synced with server state.");
			StendhalUI.get().addEventLine("Synchronized", NotificationType.CLIENT);
		}

		private int times;

		public void onUnsynced() {
			times++;

			if (times > 3) {
				logger.debug("Request resync");
				StendhalUI.get().addEventLine("Unsynced: Resynchronizing...", NotificationType.CLIENT);
			}
		}

		public void onException(Exception e, marauroa.common.net.message.MessageS2CPerception perception) {
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
		userName=username;
	}

	public String getAccountUsername() {
		return userName;
	}
}
