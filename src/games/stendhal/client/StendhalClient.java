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

import games.stendhal.client.gui.InGameGUI;
import games.stendhal.client.gui.OutfitDialog;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.core.WtWindowManager;
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import marauroa.client.ariannexp;
import marauroa.client.net.DefaultPerceptionListener;
import marauroa.client.net.PerceptionHandler;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;
import marauroa.common.game.Perception;
import marauroa.common.game.RPObject;
import marauroa.client.net.IPerceptionListener;
import marauroa.common.net.MessageS2CPerception;
import marauroa.common.net.TransferContent;

import org.apache.log4j.Logger;

/**
 * This class is the glue to Marauroa, it extends ariannexp and allow us to
 * easily connect to an marauroa server and operate it easily.
 */
public class StendhalClient extends ariannexp {

	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalClient.class);

	private Map<RPObject.ID, RPObject> world_objects;

	private PerceptionHandler handler;

	private RPObject player;

	private StaticGameLayers staticLayers;

	private GameObjects gameObjects;

	private boolean keepRunning = true;

	private InGameGUI gameGUI;

	private JFrame frame;

	private static StendhalClient client=null;

	private Cache cache;

	private ArrayList<Direction> directions;

	private static final String LOG4J_PROPERTIES = "data/conf/log4j.properties";
	protected GameScreen screen;

	protected PerceptionListenerMulticaster listeners;

	private String userName="";

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

		listeners = new PerceptionListenerMulticaster();
		listeners.addListener(new StendhalPerceptionListener());

		handler = new PerceptionHandler(listeners);

		gameGUI = null;

		cache = new Cache();
		cache.init();

		directions = new ArrayList<Direction>(4);
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

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public void setScreen(GameScreen screen) {
		this.screen = screen;
	}

	public GameScreen getScreen() {
		return screen;
	}

	public void setGameGUI(InGameGUI gui) {
		gameGUI = gui;
	}

	public InGameGUI getGameGUI() {
		return gameGUI;
	}

	public OutfitDialog getOutfitDialog(int outfit) {
		// int outfit, int total_hairs, int total_heads, int total_bodies, int
		// total_clothes) {
		return new OutfitDialog(frame, "Set outfit", outfit, 23, 16, 11, 24);
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

	public boolean isAdmin() {
		return (player != null) && player.has("adminlevel") && (player.getInt("adminlevel") >= 600);
	}

	/**
	 * connect to the Stendhal game server and if successfull, check, if the
	 * server runs StendhalHttpServer extension. In that case it checks, if
	 * server version equals the client's.
	 */
	@Override
	public void connect(String host, int port, boolean protocol) throws java.net.SocketException {
		super.connect(host, port, protocol);
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
				if (player != null) {
					WorldObjects.fireZoneLeft(player.getID().getZoneID());
				}

				// Notify zone entering.
				WorldObjects.fireZoneEntered(message.getRPZoneID().getID());

				/** Full object is normal object+hidden objects */
				RPObject hidden = message.getMyRPObjectAdded();
				RPObject object = null;

				for (RPObject search : message.getAddedRPObjects()) {
					if (search.getID().equals(hidden.getID())) {
						object = (RPObject) search.clone();
						break;
					}
				}

				/** We clean the game object container */
				logger.debug("CLEANING static object list");
				gameObjects.clear();

				String zoneid = message.getRPZoneID().getID();
				staticLayers.setRPZoneLayersSet(zoneid);
				screen.setMaxWorldSize((int) staticLayers.getWidth(), (int) staticLayers.getHeight());

				/** And finally place player in screen */
				Graphics2D g = screen.expose();
				g.setColor(Color.BLACK);
				g.fill(new Rectangle(0, 0, j2DClient.SCREEN_WIDTH, j2DClient.SCREEN_HEIGHT));

				double x = object.getDouble("x") - screen.getWidth() / 2;
				double y = object.getDouble("y") - screen.getHeight() / 2;

				if (x < 0) {
					x = 0;
				} else if ((staticLayers.getWidth() != 0) && (x + screen.getWidth() > staticLayers.getWidth())) {
					x = staticLayers.getWidth() - screen.getWidth();
				}

				if (y < 0) {
					y = 0;
				} else if ((staticLayers.getHeight() != 0) && (y + screen.getHeight() > staticLayers.getHeight())) {
					y = staticLayers.getHeight() - screen.getHeight();
				}

				screen.place(x, y);
				screen.move(0, 0);
			}

			/** This code emulate a perception loss. */
			if (Debug.EMULATE_PERCEPTION_LOSS && (message.getPerceptionType() != Perception.SYNC)
			        && ((message.getPerceptionTimestamp() % 30) == 0)) {
				return;
			}

			handler.apply(message, world_objects);
		} catch (Exception e) {
			logger.debug("error processing message " + message, e);
			System.exit(-1);
		} finally {
			Log4J.finishMethod(logger, "onPerception");
		}
	}

	@Override
	protected List<TransferContent> onTransferREQ(List<TransferContent> items) {
		Log4J.startMethod(logger, "onTransferREQ");
		for (TransferContent item : items) {

			InputStream is = cache.getItem(item);

			if (is != null) {
				item.ack = false;
				try {
					Reader reader = new InputStreamReader(is);
					contentHandling(item.name, reader);
					reader.close();
				} catch (java.io.IOException e) {
					e.printStackTrace();
					System.exit(0);
				}
			} else {
				logger.debug("Content " + item.name + " is NOT on cache. We have to transfer");
				item.ack = true;
			}
		}
		Log4J.finishMethod(logger, "onTransferREQ");

		return items;
	}

	private void contentHandling(String name, Reader reader) throws IOException {
		staticLayers.addLayer(reader, name);
		screen.setMaxWorldSize((int) staticLayers.getWidth(), (int) staticLayers.getHeight());
	}

	@Override
	protected void onTransfer(List<TransferContent> items) {
		Log4J.startMethod(logger, "onTransfer");
		for (TransferContent item : items) {
			try {
				cache.store(item, item.data);
				contentHandling(item.name, new InputStreamReader(new ByteArrayInputStream(item.data)));
			} catch (java.io.IOException e) {
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

	public void requestLogout() {
		Log4J.startMethod(logger, "requestLogout");
		keepRunning = false;
		// try to save the window configuration
		WtWindowManager.getInstance().save();
		Log4J.finishMethod(logger, "requestLogout");
	}

	public boolean shouldContinueGame() {
		return keepRunning;
	}


	/**
	 * Clear any active text bubbles.
	 */
	public void clearTextBubbles() {
		gameObjects.clearTexts();
	}


	/**
	 * Add an active player movement direction.
	 *
	 * @param	dir		The direction.
	 * @param	face		If to face direction only.
	 */
	public void addDirection(Direction dir, boolean face) {
		RPAction action;

		/*
		 * Move to end
		 */
		directions.remove(dir);
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

	//
	//

	class StendhalPerceptionListener extends DefaultPerceptionListener {

		@Override
		public boolean onAdded(RPObject object) {
			try {
				logger.debug("Object(" + object.getID() + ") added to Game Objects container");
				gameObjects.add(object);
			} catch (Exception e) {
				logger.error("onAdded failed, object is " + object, e);
			}
			return false;
		}

		@Override
		public boolean onModifiedAdded(RPObject object, RPObject changes) {
			// NOTE: We do handle the perception here ourselves. See that we
			// return true
			try {
				logger.debug("Object(" + object.getID() + ") modified in Game Objects container");
				gameObjects.modifyAdded(object, changes);
				object.applyDifferences(changes, null);
			} catch (Exception e) {
				logger.debug("onModifiedAdded failed, object is " + object + ", changes is " + changes, e);
			}
			return true;
		}

		@Override
		public boolean onModifiedDeleted(RPObject object, RPObject changes) {
			try {
				logger.debug("Object(" + object.getID() + ") modified in Game Objects container");
				logger.debug("Original(" + object + ") modified in Game Objects container");

				gameObjects.modifyRemoved(object, changes);
				object.applyDifferences(null, changes);

				logger.debug("Modified(" + object + ") modified in Game Objects container");
				logger.debug("Changes(" + changes + ") modified in Game Objects container");
			} catch (Exception e) {
				logger.error("onModifiedDeleted failed, object is " + object + ", changes is " + changes, e);
			}
			return true;
		}

		@Override
		public boolean onDeleted(RPObject object) {
			try {
				logger.debug("Object(" + object.getID() + ") removed from Static Objects container");
				gameObjects.remove(object.getID());
			} catch (Exception e) {
				logger.error("onDeleted failed, object is " + object, e);
			}
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

				player = world_objects.get(id);

				if (deleted != null) {
					gameObjects.modifyRemoved(player, deleted);
					player.applyDifferences(null, deleted);
				}

				if (added != null) {
					gameObjects.modifyAdded(player, added);
					player.applyDifferences(added, null);
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

			gameGUI.online();

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
			logger.fatal("perception caused an error: " + perception, e);
			System.exit(-1);

			// Never executed
			return -1;
		}
	}

	public void setUserName(String username) {
		userName=username;
	    
    }

	public Object getUserName() {
	 
	    return userName;
    }
}
