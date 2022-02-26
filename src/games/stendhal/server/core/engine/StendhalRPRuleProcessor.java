/***************************************************************************
 *                    (C) Copyright 2003-2020 - Marauroa                   *
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import games.stendhal.common.Debug;
import games.stendhal.common.NotificationType;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.account.AccountCreator;
import games.stendhal.server.core.account.CharacterCreator;
import games.stendhal.server.core.engine.db.StendhalWebsiteDAO;
import games.stendhal.server.core.engine.dbcommand.SetOnlineStatusCommand;
import games.stendhal.server.core.engine.transformer.PlayerTransformer;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.scripting.ScriptRunner;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour.ExpireOutfit;
import games.stendhal.server.entity.player.AfkTimeouter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.PlayerLoggedOnEvent;
import games.stendhal.server.events.PlayerLoggedOutEvent;
import games.stendhal.server.extension.StendhalServerExtension;
import marauroa.common.Configuration;
import marauroa.common.Pair;
import marauroa.common.game.AccountResult;
import marauroa.common.game.CharacterResult;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.io.UnicodeSupportingInputStreamReader;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandPriority;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.Statistics;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.dbcommand.LogGameEventCommand;
import marauroa.server.game.rp.IRPRuleProcessor;
import marauroa.server.game.rp.RPServerManager;

/**
 * adds game rules for Stendhal to the marauroa environment.
 *
 * @author hendrik
 */
public class StendhalRPRuleProcessor implements IRPRuleProcessor {

	/** The logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPRuleProcessor.class);

	/** The Singleton instance. */
	protected static StendhalRPRuleProcessor instance;

	/** only log the first exception while reading welcome URL. */
	private static boolean firstWelcomeException = true;
	/** list of super admins read from admins.list. */
	private static Map<String, String> adminNames;
	/** welcome message unless overwritten by an URL */
	private static String welcomeMessage = "Welcome to Stendhal. Need help? #https://stendhalgame.org/player-guide/ask-for-help.html - please report problems, suggestions and bugs. Remember to keep your password completely secret, never tell to another friend, player, or admin.";

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
	private final List<StendhalRPZone> zonesToRemove = new LinkedList<StendhalRPZone>();

	private LinkedList<marauroa.server.game.rp.GameEvent> gameEvents = new LinkedList<>();


	/**
	 * gets the singleton instance of StendhalRPRuleProcessor
	 *
	 * @return StendhalRPRuleProcessor
	 */
	public static StendhalRPRuleProcessor get() {
		synchronized(StendhalRPRuleProcessor.class) {
			if (instance == null) {
				StendhalRPRuleProcessor instance = new StendhalRPRuleProcessor();
				StendhalRPRuleProcessor.instance = instance;
				new GameEvent("server system", "startup").raise();
				AfkTimeouter.create();
			}
		}

		return instance;
	}

	/**
	 * creates a new StendhalRPRuleProcessor
	 */
	protected StendhalRPRuleProcessor() {
		onlinePlayers = new PlayerList();
		playersRmText = new LinkedList<Player>();
		entityToKill = new LinkedList<Pair<RPEntity, Entity>>();
	}

	/**
	 * gets a list of online players
	 *
	 * @return list of online players
	 */
	public PlayerList getOnlinePlayers() {
		return onlinePlayers;
	}

	@Override
	public void setContext(final RPServerManager rpman) {
		try {
			/*
			 * Print version information.
			 */
			String preRelease = "";
			if (Debug.PRE_RELEASE_VERSION != null) {
				preRelease = " - " + preRelease;
			}
			logger.info("Running Stendhal server VERSION " + Debug.VERSION + preRelease);
			Translate.init();

			this.rpman = rpman;
			StendhalRPAction.initialize(rpman);

			/* Initialize quests */
			final StendhalQuestSystem questSystem = StendhalQuestSystem.get();
			questSystem.init();

			new ScriptRunner().init();

			/* initialize quests stored in cache */
			questSystem.loadCachedQuests();

			/* actions registered to be executed at end of server startup */
			CachedActionManager.get().run();

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
					} catch (final RuntimeException ex) {
						logger.error("Error while loading extension: " + extension, ex);
					}
				}
			} catch (final RuntimeException ep) {
				logger.info("No server extensions configured in ini file.");
			}

			// Remove online info from database.
			DAORegister.get().get(StendhalWebsiteDAO.class).clearOnlineStatus();
		} catch (final Exception e) {
			logger.error("cannot set Context. exiting", e);
			System.exit(-1);
		}
	}

	@Override
	public boolean checkGameVersion(final String game, final String version) {
		try {
			if (!game.equals(Configuration.getConfiguration().get("server_typeGame", "stendhal"))) {
				logger.warn("Client for game " + game + " is trying to login to server for game "
						+ Configuration.getConfiguration().get("server_typeGame", "stendhal")
						+ ", as defined in server configuration file (usually server.ini) with key server_typeGame (defaults to \"stendhal\" if not present).");
				return false;
			}
			if (Debug.VERSION.compareTo(version) > 0) {
				logger.warn("Client version: " + version);
			}
			return true;
		} catch (IOException e) {
			logger.error(e, e);
			return false;
		}
	}

	/**
	 * Kills an RPEntity.
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

	@Override
	public boolean onActionAdd(final RPObject caster, final RPAction action, final List<RPAction> actionList) {
		return true;
	}

	@Override
	public void execute(final RPObject caster, final RPAction action) {
		if (caster instanceof Player) {
			((Player) caster).setLastClientActionTimestamp(System.currentTimeMillis());
		}
		CommandCenter.execute(caster, action);
	}

	public int getTurn() {
		return rpman.getTurn();
	}

	/** Notify it when a new turn happens. */
	@Override
	public synchronized void beginTurn() {
		final long start = System.nanoTime();

		try {
			destroyObsoleteZones();
		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		}

		try {
			logNumberOfPlayersOnline();
		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		}

		try {
			handleKilledEntities();
		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		}

		try {
			executePlayerLogic();
		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		}

		try {
			executeNPCsPreLogic();
		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		}

		try {
			handlePlayersRmTexts();
		} catch (final Exception e) {
			logger.error("error in beginTurn", e);
		}
		logger.debug("Begin turn: " + (System.nanoTime() - start) / 1000000.0);
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
		final Set<String> npcs = npcList.getNPCs();
		for (final String npc : npcs) {
			npcList.get(npc).preLogic();
		}
	}

	protected void executePlayerLogic() {
		getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
			@Override
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


	@Override
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
	private static void readAdminsFromFile(final Player player) {
		synchronized(StendhalRPRuleProcessor.class) {
			if (adminNames == null) {
				Map<String, String> tempAdminNames = new HashMap<String, String>();

				String adminFilename = "data/conf/admins.txt";

				try {
					InputStream is = player.getClass().getClassLoader().getResourceAsStream(
							adminFilename);

					if (is == null) {
						logger.info(adminFilename + " does not exist.");
						return;
					}

					final BufferedReader in = new BufferedReader(
							new UnicodeSupportingInputStreamReader(is));
					try {
						String line;
						while ((line = in.readLine()) != null) {
							String[] tokens = line.split("=");
							if (tokens.length >= 2) {
								tempAdminNames.put(tokens[0].trim(), tokens[1].trim());
							} else {
								tempAdminNames.put(tokens[0].trim(), Integer.toString(AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPER));
							}
						}
					} catch (final Exception e) {
						logger.error("Error loading admin names from: "+ adminFilename, e);
					} finally {
						in.close();
					}

					adminNames = tempAdminNames;
				} catch (final IOException e) {
					logger.error("Error loading admin names from: " + adminFilename, e);
				}
			}
		}

		if (adminNames.get(player.getName()) != null) {
			player.setAdminLevel(Integer.parseInt(adminNames.get(player.getName())));
		}
	}

	@Override
	public synchronized boolean onInit(final RPObject object) {
		try {
			if (object == null) {
				logger.error("onInit: object = null", new Throwable());
				return false;
			}

			if (object instanceof Player) {
				Player player = (Player) object;

				playersRmText.add(player);

				// place the player and his pets into the world
				PlayerTransformer.placePlayerIntoWorldOnLogin(object, player);
				PlayerTransformer.placeSheepAndPetIntoWorld(player);
				player.notifyWorldAboutChanges();
				StendhalRPAction.transferContent(player);

				getOnlinePlayers().add(player);

				if (!player.isGhost()) {
					notifyOnlineStatus(true, player);
					DBCommand command = new SetOnlineStatusCommand(player.getName(), true);
					DBCommandQueue.get().enqueue(command);
				}
				updatePlayerNameListForPlayersOnLogin(player);
				String[] params = {};

				new GameEvent(player.getName(), "login", params).raise();
				SingletonRepository.getLoginNotifier().onPlayerLoggedIn(player);
				TutorialNotifier.login(player);

				readAdminsFromFile(player);
				welcome(player);

				// expire outfits
				if (player.has("outfit_expire_age")) {
					int expire = player.getInt("outfit_expire_age") - player.getAge();
					ExpireOutfit expireOutfit = new ExpireOutfit(player.getName());
					SingletonRepository.getTurnNotifier().dontNotify(expireOutfit);
					SingletonRepository.getTurnNotifier().notifyInSeconds(Math.max(0, expire * 60), expireOutfit);
				}
				return true;
			} else {
				logger.error("onInit: object is not an instance of Player: " + object, new Throwable());
				return false;
			}
		} catch (final Exception e) {
			final String id;
			if (object != null) {
				id = object.get("#db_id");
			} else {
				 id = "<object is null>";
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
				if (msg.startsWith("http://") || msg.startsWith("https://")) {
					final URL url = new URL(msg);
					HttpURLConnection.setFollowRedirects(false);
					final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    try {
                        final BufferedReader br = new BufferedReader(
							new InputStreamReader(connection.getInputStream(), "UTF-8"));
                        try {
                            msg = br.readLine();
                        } finally {
                            br.close();
                        }
                    } finally {
				        connection.disconnect();
                    }
				}
			}
		} catch (final IOException e) {
			if (firstWelcomeException) {
				logger.warn("Can't read server_welcome from marauroa.ini", e);
				firstWelcomeException = false;
			}
		}
		if (msg != null) {
			/*
			 * Avoid spamming all client channels. Very old clients do not
			 * recognize SERVER type, but they just log an error. Client version
			 * information has not been received yet.
			 */
			player.sendPrivateText(NotificationType.SERVER, msg);
		}
	}

	@Override
	public synchronized boolean onExit(final RPObject object) {
		return onLogout(object, "logout");
	}

	private boolean onLogout(final RPObject object, String reason) {
		if (object instanceof Player) {
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
				updatePlayerNameListForPlayersOnLogout(player);

				Player.destroy(player);
				getOnlinePlayers().remove(player);

				DBCommand command = new SetOnlineStatusCommand(player.getName(), false);
				DBCommandQueue.get().enqueue(command);

				new GameEvent(player.getName(), "logout", reason).raise();
				logger.debug("removed player " + player);

				SingletonRepository.getLogoutNotifier().onPlayerLoggedOut(player);
				return true;
			} catch (final Exception e) {
				logger.error("error in onExit", e);
				return true;
			}
		} else {
			logger.error("object is no Player, but: " + object, new Throwable());
			return true;
		}
	}

	@Override
	public synchronized void onTimeout(final RPObject object) {
		onLogout(object, "timeout");
	}

	@Override
	public AccountResult createAccount(final String username, final String password, final String email) {
		final AccountCreator creator = new AccountCreator(username, password, email);
		return creator.create();
	}

	@Override
	public AccountResult createAccountWithToken(String username, String tokenType, String token) {
		return null;
	}

	@Override
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
	 * @param notificationType type of notification message, usually NotificationType.PRIVMSG
	 * @param message
	 *            Message to tell all players
	 */
	public void tellAllPlayers(NotificationType notificationType, final String message) {
		onlinePlayers.tellAllOnlinePlayers(notificationType, message);
	}

	/**
	 * Sends a message to all supporters.
	 *
	 * @param message Support message
	 */
	public void sendMessageToSupporters(final String message) {
		getOnlinePlayers().forFilteredPlayersExecute(

		new Task<Player>() {

			@Override
			public void execute(final Player player) {
				player.sendPrivateText(NotificationType.SUPPORT, message);
				player.notifyWorldAboutChanges();
			}

		},

		new FilterCriteria<Player>() {

			@Override
			public boolean passes(final Player p) {
				return p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT;
			}

		});

		logger.log(Level.toLevel(System.getProperty("stendhal.support.loglevel"), Level.DEBUG), message);
	}

	/**
	 * Sends a message to all supporters.
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
	 * Notifies buddies about going online/offline.
	 *
	 * @param isOnline did the player login?
	 * @param playerToNotifyAbout name of the player
	 */
	public void notifyOnlineStatus(final boolean isOnline, final Player playerToNotifyAbout) {
		if (instance != null) {
			if (isOnline) {
				SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
					@Override
					public void execute(final Player player) {
						player.notifyOnline(playerToNotifyAbout.getName());
					}
				});

			} else {
				SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
					@Override
					public void execute(final Player player) {
						player.notifyOffline(playerToNotifyAbout.getName());
					}
				});
			}
		}
	}

	/**
	 * Update all player's lists of online player names on login of a new player
	 *
	 * @param playerToNotifyAbout
	 */
	private void updatePlayerNameListForPlayersOnLogin(final Player playerToNotifyAbout) {
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
			@Override
			public void execute(final Player player) {
				if(playerToNotifyAbout.isGhost()) {
					playerToNotifyAbout.addEvent(new PlayerLoggedOnEvent(player.getName()));
					playerToNotifyAbout.notifyWorldAboutChanges();
					if (player.isGhost() && (player != playerToNotifyAbout)) {
						player.addEvent(new PlayerLoggedOnEvent(playerToNotifyAbout.getName()));
						player.notifyWorldAboutChanges();
					}
				} else {
					player.addEvent(new PlayerLoggedOnEvent(playerToNotifyAbout.getName()));
					player.notifyWorldAboutChanges();
					if (!player.isGhost() && (player != playerToNotifyAbout)) {
						playerToNotifyAbout.addEvent(new PlayerLoggedOnEvent(player.getName()));
						playerToNotifyAbout.notifyWorldAboutChanges();
					}
				}
			}
		});
	}

	/**
	 * Update all player's lists of online player names on login of a new player
	 *
	 * @param playerToNotifyAbout
	 */
	private void updatePlayerNameListForPlayersOnLogout(final Player playerToNotifyAbout) {
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(new Task<Player>() {
			@Override
			public void execute(final Player player) {
				player.addEvent(new PlayerLoggedOutEvent(playerToNotifyAbout.getName()));
				player.notifyWorldAboutChanges();
			}
		});
	}

	/**
	 * Removes a zone (like a personalized vault).
	 *
	 * @param zone StendhalRPZone to remove
	 */
	public void removeZone(final StendhalRPZone zone) {
		zonesToRemove.add(zone);
	}

	/**
	 * Sets the welcome message.
	 *
	 * @param msg welcome message
	 */
	public static void setWelcomeMessage(String msg) {
		StendhalRPRuleProcessor.welcomeMessage = msg;
	}

	/**
	 * logs a gameEvent
	 *
	 * @param gameEvent a game event
	 */
	public void logGameEvent(GameEvent gameEvent) {
		this.logGameEvent(gameEvent.getSource(), gameEvent.getEvent(), gameEvent.getParams());
	}

	/**
	 * logs a game event
	 *
	 * @param source source
	 * @param event  event
	 * @param params parameters
	 */
	public void logGameEvent(String source, String event, String... params) {
		this.gameEvents.add(new marauroa.server.game.rp.GameEvent(source, event, params));

		// we collect one second of game events and write them as batch to the database
		if (this.gameEvents.size() == 1) {
			TurnNotifier.get().notifyInSeconds(1, new TurnListener() {
				@Override
				public void onTurnReached(int currentTurn) {
					DBCommand command = new LogGameEventCommand(gameEvents);
					gameEvents.clear();
					DBCommandQueue.get().enqueue(command, DBCommandPriority.LOW);

				}
			});
		}
	}

	/**
	 * gets the content type for the requested resource
	 *
	 * @param resource name of resource
	 * @return mime content/type or <code>null</code>
	 */
	@Override
	public String getMimeTypeForResource(String resource) {
		if (resource.endsWith(".tmx")) {
			return "text/xml";
		} else if (resource.endsWith(".tmx")) {
			return "audio/ogg";
		} else if (resource.endsWith(".png")) {
			return "image/png";
		}
		return null;
	}

	/**
	 * gets an input stream to the requested resource
	 *
	 * @param resource name of resource
	 * @return InputStream or <code>null</code>
	 */
	@Override
	public InputStream getResource(String resource) {
		if (resource.startsWith("/tiled") || resource.startsWith("/data")) {
			return StendhalRPRuleProcessor.class.getClassLoader().getResourceAsStream(resource.substring(1));
		}
		if (resource.startsWith("/tileset")) {
			return StendhalRPRuleProcessor.class.getClassLoader().getResourceAsStream("tiled" + resource);
		}
		return null;
	}
}
