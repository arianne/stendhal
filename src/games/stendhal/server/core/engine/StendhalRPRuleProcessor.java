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
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.account.AccountCreator;
import games.stendhal.server.core.account.CharacterCreator;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.extension.StendhalServerExtension;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Configuration;
import marauroa.common.Pair;
import marauroa.common.game.AccountResult;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.server.game.Statistics;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;
import marauroa.server.game.db.Transaction;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPServerManager;

import org.apache.log4j.Logger;

public class StendhalRPRuleProcessor implements IRPRuleProcessor {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPRuleProcessor.class);

	/** The Singleton instance. */
	protected static StendhalRPRuleProcessor instance;

	private StendhalPlayerDatabase database;

	private RPServerManager rpman;

	protected PlayerList onlinePlayers;
	private final List<Player> playersRmText;

	/**
	 * A list of RPEntities that were killed in the current turn, together with
	 * the Entity that killed it.
	 */
	private final List<Pair<RPEntity, Entity>> entityToKill;

	public PlayerList getOnlinePlayers() {
		return onlinePlayers;
	}

	protected StendhalRPRuleProcessor() {
		onlinePlayers = new PlayerList();
		playersRmText = new LinkedList<Player>();
		entityToKill = new LinkedList<Pair<RPEntity, Entity>>();
	}

	private void init() {
		database = (StendhalPlayerDatabase) SingletonRepository.getPlayerDatabase();

		instance = this;
		addGameEvent("server system", "startup");
	}

	public static StendhalRPRuleProcessor get() {
		if (instance == null) {
			instance = new StendhalRPRuleProcessor();
			instance.init();
		}
		return instance;
	}

	public void addGameEvent(final String source, final String event, final String... params) {
		try {
			database.addGameEvent(source, event, params);
		} catch (final Exception e) {
			logger.warn("Can't store game event", e);
		}
	}

	/**
	 * Gets the points of named player in the specified hall of fame.
	 * 
	 * @param playername
	 *            name of the player
	 * @param fametype
	 *            type of the hall of fame
	 * @return points points to add
	 */
	public int getHallOfFamePoints(final String playername, final String fametype) {
		int res = 0;
		try {
			final Transaction transaction = database.getTransaction();
			res = database.getHallOfFamePoints(transaction, playername, fametype);
			transaction.commit();
		} catch (final Exception e) {
			logger.warn("Can't store game event", e);
		}
		return res;
	}

	/**
	 * Add points to the named player in the specified hall of fame.
	 * 
	 * @param playername
	 *            name of the player
	 * @param fametype
	 *            type of the hall of fame
	 * @param points
	 *            points to add
	 */
	public void addHallOfFamePoints(final String playername, final String fametype, final int points) {
		try {
			final Transaction transaction = database.getTransaction();
			final int oldPoints = database.getHallOfFamePoints(transaction, playername, fametype);
			final int totalPoints = oldPoints + points;
			database.setHallOfFamePoints(transaction, playername, fametype, totalPoints);
			transaction.commit();
		} catch (final Exception e) {
			logger.warn("Can't store game event", e);
		}
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
			database.clearOnlineStatus();
		} catch (final Exception e) {
			logger.error("cannot set Context. exiting", e);
			System.exit(-1);
		}
	}

	public boolean checkGameVersion(final String game, final String version) {
		if (game.equals("stendhal")) {
			return true;
		}
		return false;
	}

	public void killRPEntity(final RPEntity entity, final Entity killer) {
		entityToKill.add(new Pair<RPEntity, Entity>(entity, killer));
	}

	/**
	 * Checks whether the given RPEntity has been killed this turn.
	 * 
	 * @param entity
	 *            The entity to check.
	 * @return true if the given entity has been killed this turn.
	 */
	private boolean wasKilled(final RPEntity entity) {
		for (final Pair<RPEntity, Entity> entry : entityToKill) {
			if (entity.equals(entry.first())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the entity which has killed the given RPEntity this turn.
	 * 
	 * @param entity
	 *            The killed RPEntity.
	 * @return The killer, or null if the given RPEntity hasn't been killed this
	 *         turn.
	 */
	private Entity killerOf(final RPEntity entity) {
		for (final Pair<RPEntity, Entity> entry : entityToKill) {
			if (entity.equals(entry.first())) {
				return entry.second();
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

		debugOutput();

		try {
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

	protected void logNumberOfPlayersOnline() {
		// We keep the number of players logged.
		Statistics.getStatistics().set("Players logged", getOnlinePlayers().size());
	}

	protected void handlePlayersRmTexts() {
		/*
		 * TODO: Refactor Removable once RPEvent for chat is added.
		 */
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
		/*
		 * TODO: Refactor May be done by the zone itself.
		 */

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
		 * TODO: Refactor. Entities should care about really dying themselves.
		 * This is here because there is a split between last hit and the moment
		 * a entity die so that the last hit is visible on client.
		 */
		// In order for the last hit to be visible dead happens at two
		// steps.
		for (final Pair<RPEntity, Entity> entry : entityToKill) {
			try {
				entry.first().onDead(entry.second());
			} catch (final Exception e) {
				logger.error("Player has logout before dead", e);
			}
		}
		entityToKill.clear();
	}

	private void debugOutput() {
		/*
		 * Debug statement for inspecting list of things. Most of our memories
		 * leaks came from list keep adding and adding elements.
		 */
		if (Debug.SHOW_LIST_SIZES && (rpman.getTurn() % 1000 == 0)) {
			int objects = 0;

			for (final IRPZone zone : SingletonRepository.getRPWorld()) {
				objects += zone.size();
			}

			final StringBuilder os = new StringBuilder();
			os.append("entityToKill: " + entityToKill.size() + "\n");
			os.append("players: " + getOnlinePlayers().size() + "\n");
			os.append("playersRmText: " + playersRmText.size() + "\n");

			os.append("objects: " + objects + "\n");
			logger.info(os);
		}
	}

	public synchronized void endTurn() {
		final int currentTurn = getTurn();
		try {
			for (final IRPZone zoneI : SingletonRepository.getRPWorld()) {
				final StendhalRPZone zone = (StendhalRPZone) zoneI;
				zone.logic();
			}

			// run registered object's logic method for this turn
			SingletonRepository.getTurnNotifier().logic(currentTurn);

		} catch (final Exception e) {
			logger.error("error in endTurn", e);
		}
	}

	public synchronized boolean onInit(final RPObject object) {
		try {
			final PlayerEntry entry = PlayerEntryContainer.getContainer().get(object);

			/*
			 * TODO: This is a hack, it should use instead RPObjectFactory.
			 */
			final Player player = Player.create(object);
			entry.object = player;

			/*
			 * TODO: Removable once RPEvent chat is added.
			 */
			playersRmText.add(player);

			getOnlinePlayers().add(player);

			if (!player.isGhost()) {
				notifyOnlineStatus(true, player.getName());
			}

			addGameEvent(player.getName(), "login");
			SingletonRepository.getLoginNotifier().onPlayerLoggedIn(player);
			TutorialNotifier.login(player);

			database.setOnlineStatus(player, true);

			return true;
		} catch (final Exception e) {
			logger.error("There has been a severe problem loading player " + object.get("#db_id"), e);
			return false;
		}
	}

	public synchronized boolean onExit(final RPObject object) {
		try {
			final Player player = (Player) object;
			if (wasKilled(player)) {
				logger.info("Logged out shortly before death: Killing it now :)");
				player.onDead(killerOf(player));
			}
			if (!player.isGhost()) {
				notifyOnlineStatus(false, player.getName());
			}

			Player.destroy(player);
			getOnlinePlayers().remove(player);

			database.setOnlineStatus(player, false);
			
			addGameEvent(player.getName(), "logout");
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
	 * @param message
	 *            Support message
	 */
	public static void sendMessageToSupporters(final String message) {
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forFilteredPlayersExecute(

		new Task<Player>() {

			public void execute(final Player player) {
				player.sendPrivateText(message);
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
	public static void sendMessageToSupporters(final String source, final String message) {

		final String text = source + " asks for support to ADMIN: " + message;
		sendMessageToSupporters(text);

	}

	public static int getAmountOfOnlinePlayers() {
		return SingletonRepository.getRuleProcessor().onlinePlayers.size();
	}

	public static void notifyOnlineStatus(final boolean isOnline, final String name) {
		if (instance != null) {
			if (isOnline) {
				SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
					public void execute(final Player player) {
						player.notifyOnline(name);

					}
				});

			} else {
				SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
					public void execute(final Player player) {
						player.notifyOffline(name);

					}
				});
			}
		}
	}

}
