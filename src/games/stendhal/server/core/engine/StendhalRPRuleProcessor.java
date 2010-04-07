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
package games.stendhal.server.core.engine;

import games.stendhal.common.Debug;
import games.stendhal.common.NotificationType;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.account.AccountCreator;
import games.stendhal.server.core.account.CharacterCreator;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.PlayerLoggedOnEvent;
import games.stendhal.server.events.PlayerLoggedOutEvent;
import games.stendhal.server.extension.StendhalServerExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Configuration;
import marauroa.common.Pair;
import marauroa.common.game.AccountResult;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.io.UnicodeSupportingInputStreamReader;
import marauroa.server.game.Statistics;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPServerManager;

import org.apache.log4j.Logger;

public class StendhalRPRuleProcessor implements IRPRuleProcessor {

	/** only log the first exception while reading welcome URL. */
	private static boolean firstWelcomeException = true;
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPRuleProcessor.class);
	/** list of super admins read from admins.list. */
	private static List<String> adminNames;
	/** welcome message unless overwriten by an url */
	private static String welcomeMessage = "This release is EXPERIMENTAL. Need help? #http://stendhalgame.org/wiki/AskForHelp - please report problems, suggestions and bugs. Remember to keep your password completely secret, never tell to another friend, player, or admin.";

	/** The Singleton instance. */
	protected static StendhalRPRuleProcessor instance;

	private RPServerManager rpman;

	/** a list of online players */
	protected PlayerList onlinePlayers;
	private final List<Player> playersRmText;

	/**
	 * A list of RPEntities that were killed in the current turn, together with
	 * the Entity that killed it.
	 */
	private final List<Pair<RPEntity, Entity>> entityToKill;

	/** a list of zone that should be removed (like vaults) */
	private List<StendhalRPZone> zonesToRemove = new LinkedList<StendhalRPZone>();

	/**
	 * creates a new StendhalRPRuleProcessor
	 */
	protected StendhalRPRuleProcessor() {
		onlinePlayers = new PlayerList();
		playersRmText = new LinkedList<Player>();
		entityToKill = new LinkedList<Pair<RPEntity, Entity>>();
	}

	private void init() {
		String[] params = {};
		new GameEvent("server system", "startup", params).raise();
	}

	/**
	 * gets the singleton instance of StendhalRPRuleProcessor
	 *
	 * @return StendhalRPRuleProcessor
	 */
	public static StendhalRPRuleProcessor get() {
		if (instance == null) {
			StendhalRPRuleProcessor instance = new StendhalRPRuleProcessor();
			instance.init();
			StendhalRPRuleProcessor.instance = instance;
		}
		return instance;
	}

	/**
	 * gets a list of online players
	 *
	 * @return list of online players
	 */
	public PlayerList getOnlinePlayers() {
		return onlinePlayers;
	}

	public void setContext(final RPServerManager rpman) {
		try {
			/*
			 * Print version information.
			 */
			logger.info("Running Stendhal server VERSION " + Debug.VERSION);
			
			this.rpman = rpman;
			StendhalRPAction.initialize(rpman);
			/* Initialize quests */
			SingletonRepository.getStendhalQuestSystem().init();

			new ScriptRunner();

			final Configuration config = Configuration.getConfiguration();
			try {
				final String[] extensionsToLoad = config.get("server_extension").split(",");
				for (final String element : extensionsToLoad) {
					String extension = null;
					try {
						extension = element;
						if (extension.length() > 0) {
							StendhalServerExtension.getInstance(config.get(extension)).init();
						}
					} catch (final Exception ex) {
						logger.error("Error while loading extension: " + extension, ex);
					}
				}
			} catch (final Exception ep) {
				logger.info("No server extensions configured in ini file.");
			}

			/*
			 * Remove online info from database.
			 */
			DAORegister.get().get(StendhalWebsiteDAO.class).clearOnlineStatus();
		} catch (final Exception e) {
			logger.error("cannot set Context. exiting", e);
			System.exit(-1);
		}
	}

	public boolean checkGameVersion(final String game, final String version) {
		try {
			if (game.equals(Configuration.getConfiguration().get("server_typeGame", "stendhal"))) {
				return true;
			}
		} catch (IOException e) {
			logger.error(e, e);
			return false;
		}
		return false;
	}

	/**
	 * kills an RPEntity
	 *
	 * @param entity
	 * @param killer
	 */
	public void killRPEntity(final RPEntity entity, final Entity killer) {
		entityToKill.add(new Pair<RPEntity, Entity>(entity, killer));
	}

	/**
	 * Checks whether the given RPEntity has been killed this turn.
	 * 
	 * @param entity
	 *            The entity to check.
	 * @return pair of killed, killer if the specified entity did logout while dying, <code>null</code> otherwise.
	 */
	private Pair<RPEntity, Entity> removedKilled(final RPEntity entity) {
		Iterator<Pair<RPEntity, Entity>> itr = entityToKill.iterator();
		while (itr.hasNext()) {
			Pair<RPEntity, Entity> entry = itr.next();
			if (entity.equals(entry.first())) {
				itr.remove();
				return entry;
			}
		}
		return null;
	}

	public void removePlayerText(final Player player) {
		playersRmText.add(player);
	}

	/**
	 * Finds an online player with a specific name.
	 * 
	 * @param name
	 *            The player's name
	 * @return The player, or null if no player with the given name is currently
	 *         online.
	 */
	public Player getPlayer(final String name) {
		return onlinePlayers.getOnlinePlayer(name);
	}

	public boolean onActionAdd(final RPObject caster, final RPAction action, final List<RPAction> actionList) {
		return true;
	}

	public void execute(final RPObject caster, final RPAction action) {
		CommandCenter.execute(caster, action);
	}

	public int getTurn() {
		return rpman.getTurn();
	}

	/** Notify it when a new turn happens . */
	public synchronized void beginTurn() {
		final long start = System.nanoTime();

		try {
			
			destroyObsoleteZones();
			
			logNumberOfPlayersOnline();

			handleKilledEntities();

			executePlayerLogic();

			executeNPCsPreLogic();

			handlePlayersRmTexts();

		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		} finally {
			logger.debug("Begin turn: " + (System.nanoTime() - start) / 1000000.0);
		}
	}

	private void destroyObsoleteZones() {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		for (StendhalRPZone zone : zonesToRemove) {
			zone.onRemoved();
			world.removeZone(zone);
		}
		zonesToRemove.clear();
		
	}

	protected void logNumberOfPlayersOnline() {
		// We keep the number of players logged.
		Statistics.getStatistics().set("Players logged", getOnlinePlayers().size());
	}

	protected void handlePlayersRmTexts() {
		for (final Player player : playersRmText) {
			if (player.has("text")) {
				player.remove("text");
				player.notifyWorldAboutChanges();
			}
		}
		playersRmText.clear();
	}

	protected void executeNPCsPreLogic() {
		// SpeakerNPC logic
		final NPCList npcList = SingletonRepository.getNPCList();
		for (final SpeakerNPC npc : npcList) {
			npc.preLogic();
		}
	}

	protected void executePlayerLogic() {

		getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
			public void execute(final Player player) {

				try {
					player.logic();
				} catch (final Exception e) {
					logger.error("Error in player logic", e);
				}
			}
		});
	}

	protected void handleKilledEntities() {
		/*
		 * This is here because there is a split between last hit and the moment
		 * a entity die so that the last hit is visible on client.
		 */
		// In order for the last hit to be visible dead happens at two
		// steps.
		for (final Pair<RPEntity, Entity> entry : entityToKill) {
			try {
				entry.first().onDead(entry.second());
			} catch (final Exception e) {
				logger.error("error while handling killed entities", e);
			}
		}
		entityToKill.clear();
	}


	public synchronized void endTurn() {
		final int currentTurn = getTurn();
		try {

			SingletonRepository.getTurnNotifier().logic(currentTurn);

			for (final IRPZone zoneI : SingletonRepository.getRPWorld()) {
				final StendhalRPZone zone = (StendhalRPZone) zoneI;
				zone.logic();
			}

			// run registered object's logic method for this turn

		} catch (final Exception e) {
			logger.error("error in endTurn", e);
		}
	}
	
	/**
	 * reads the admins from admins.list.
	 * 
	 * @param player
	 *            Player to check for super admin status.
	 */
	static void readAdminsFromFile(final Player player) {
		if (adminNames == null) {
			adminNames = new LinkedList<String>();

			String adminFilename = "data/conf/admins.txt";
			final String adminFilenameAlt = "data/conf/admins.list";
			
			try {
				InputStream is = player.getClass().getClassLoader().getResourceAsStream(
						adminFilename);

				if (is == null) {
					// backwards compatibility for those who used to have admins.list
					is = player.getClass().getClassLoader().getResourceAsStream(
							adminFilenameAlt);
					if (is == null) {
						logger.info("Neither " + adminFilename + " nor " + adminFilenameAlt + " exist.");
					}
					// update filename for any error messages
					adminFilename = adminFilenameAlt;
				} else {

					final BufferedReader in = new BufferedReader(
							new UnicodeSupportingInputStreamReader(is));
					try {
						String line;
						while ((line = in.readLine()) != null) {
							adminNames.add(line);
						}
					} catch (final Exception e) {
						logger.error("Error loading admin names from: "
								+ adminFilename, e);
					}
					in.close();
				}
			} catch (final Exception e) {
				logger.error(
						"Error loading admin names from: " + adminFilename, e);
			}
		}

		if (adminNames.contains(player.getName())) {
			player.setAdminLevel(AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPER);
		}
	}

	public synchronized boolean onInit(final RPObject object) {
		try {
			if (object == null) {
				logger.error("onInit: object = null", new Throwable());
				return false;
			}

			if (!(object instanceof Player)) {
				logger.error("onInit: object is not an instance of Player: " + object, new Throwable());
			}
			Player player = (Player) object;
			
			playersRmText.add(player);

			getOnlinePlayers().add(player);

			if (!player.isGhost()) {
				notifyOnlineStatus(true, player);
				DAORegister.get().get(StendhalWebsiteDAO.class).setOnlineStatus(player, true);

			}
			String[] params = {};

			new GameEvent(player.getName(), "login", params).raise();
			SingletonRepository.getLoginNotifier().onPlayerLoggedIn(player);
			TutorialNotifier.login(player);

			readAdminsFromFile(player);
			welcome(player);
			return true;
		} catch (final Exception e) {
			String id = "<object is null>";
			if (object != null) {
				id = object.get("#db_id");
			}
			logger.error("There has been a severe problem loading player " + id, e);
			return false;
		}
	}
	/**
	 * send a welcome message to the player which can be configured in
	 * marauroa.ini file as "server_welcome". If the value is an http:// address,
	 * the first line of that address is read and used as the message
	 * 
	 * @param player
	 *            Player
	 */
	static void welcome(final Player player) {
		String msg = welcomeMessage;
		try {
			final Configuration config = Configuration.getConfiguration();
			if (config.has("server_welcome")) {
				msg = config.get("server_welcome");
				if (msg.startsWith("http://")) {
					final URL url = new URL(msg);
					HttpURLConnection.setFollowRedirects(false);
					final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					final BufferedReader br = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					msg = br.readLine();
					br.close();
					connection.disconnect();
				}
			}
		} catch (final Exception e) {
			if (firstWelcomeException) {
				logger.warn("Can't read server_welcome from marauroa.ini", e);
				firstWelcomeException = false;
			}
		}
		if (msg != null) {
			player.sendPrivateText(msg);
		}
	}
	public synchronized boolean onExit(final RPObject object) {
		try {
			final Player player = (Player) object;

			Pair<RPEntity, Entity> entry = removedKilled(player);
			if (entry != null) {
				logger.info(object.get("name") + " logged out shortly before death: Killing it now :)");
				entry.first().onDead(entry.second());
			}

			if (!player.isGhost()) {
				notifyOnlineStatus(false, player);
			}

			Player.destroy(player);
			getOnlinePlayers().remove(player);

			DAORegister.get().get(StendhalWebsiteDAO.class).setOnlineStatus(player, false);
			String[] params = {};
			
			new GameEvent(player.getName(), "logout", params).raise();
			logger.debug("removed player " + player);

			return true;
		} catch (final Exception e) {
			logger.error("error in onExit", e);
			return true;
		}
	}

	public synchronized void onTimeout(final RPObject object) {
		onExit(object);
	}

	public AccountResult createAccount(final String username, final String password, final String email) {
		final AccountCreator creator = new AccountCreator(username, password, email);
		return creator.create();
	}

	public CharacterResult createCharacter(final String username, final String character, final RPObject template) {
		final CharacterCreator creator = new CharacterCreator(username, character, template);
		return creator.create();
	}

	public RPServerManager getRPManager() {
		return rpman;
	}

	/**
	 * Tell this message all players.
	 * 
	 * @param message
	 *            Message to tell all players
	 */
	public void tellAllPlayers(final String message) {
		onlinePlayers.tellAllOnlinePlayers(message);
	}

	/**
	 * sends a message to all supporters.
	 * 
	 * @param message Support message
	 */
	public void sendMessageToSupporters(final String message) {
		getOnlinePlayers().forFilteredPlayersExecute(

		new Task<Player>() {

			public void execute(final Player player) {
				player.sendPrivateText(NotificationType.SUPPORT, message);
				player.notifyWorldAboutChanges();

			}

		},

		new FilterCriteria<Player>() {

			public boolean passes(final Player p) {
				return p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT;

			}

		});

	}
	
	/**
	 * sends a message to all supporters.
	 * 
	 * @param source
	 *            a player or script name
	 * @param message
	 *            Support message
	 */
	public void sendMessageToSupporters(final String source, final String message) {
		final String text = source + " asks for support to ADMIN: " + message;
		sendMessageToSupporters(text);

	}

	public static int getAmountOfOnlinePlayers() {
		return SingletonRepository.getRuleProcessor().onlinePlayers.size();
	}

	/**
	 * notifies buddies about going online/offline
	 *
	 * @param isOnline did the player login?
	 * @param playerToNotifyAbout name of the player
	 */
	public void notifyOnlineStatus(final boolean isOnline, final Player playerToNotifyAbout) {
		if (instance != null) {
			if (isOnline) {
				SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
					public void execute(final Player player) {
						player.notifyOnline(playerToNotifyAbout.getName());
						player.addEvent(new PlayerLoggedOnEvent(playerToNotifyAbout.getName()));
						playerToNotifyAbout.addEvent(new PlayerLoggedOnEvent(player.getName()));
					}
				});

			} else {
				SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
					public void execute(final Player player) {
						player.notifyOffline(playerToNotifyAbout.getName());
						player.addEvent(new PlayerLoggedOutEvent(playerToNotifyAbout.getName()));
					}
				});
			}
		}
	}

	/**
	 * removes a zone (like a personalized vault)
	 *
	 * @param zone StendhalRPZone to remove
	 */
	public void removeZone(final StendhalRPZone zone) {
		zonesToRemove.add(zone);
	}

	/**
	 * sets the welcome message
	 *
	 * @param msg welcome message
	 */
	public void setWelcomeMessage(String msg) {
		StendhalRPRuleProcessor.welcomeMessage = msg;
	}
}
