package games.stendhal.server.maps.quests.allotment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.GateKey;
import games.stendhal.server.entity.mapstuff.ExpirationTracker;
import games.stendhal.server.entity.mapstuff.area.Allotment;
import games.stendhal.server.entity.mapstuff.portal.Gate;
import games.stendhal.server.entity.player.Player;

public class AllotmentUtilities implements TurnListener {

	/**
	 * Singleton instance to return
	 */
	private static AllotmentUtilities instance;

	/**
	 * Name of the quest used for quest slot
	 */
	public static String QUEST_SLOT = "allotment_rental";

	/**
	 * Prefix that allotment names have
	 */
	private static String ALLOTMENT_PREFIX = "Allotment ";

	/**
	 * Time interval to check if any allotment rentals have expired
	 */
	private static int EXPIRATION_CHECKING_PERIOD = MathHelper.SECONDS_IN_ONE_MINUTE;

	/**
	 * Amount of time which an allotment can be rented out
	 */
	public static long RENTAL_TIME = MathHelper.MILLISECONDS_IN_ONE_MINUTE * 2;

	/**
	 *
	 */
	private static int WARN_TIME = MathHelper.SECONDS_IN_ONE_MINUTE / 2;

	/**
	 * List of zones which have allotments that can be rented
	 */
	private static String[] zones = { "0_semos_mountain_w2" };


	/**
	 * Singleton getter
	 *
	 * @return the instance of this singleton
	 */
	public static AllotmentUtilities get() {
		if (instance == null) {
			instance = new AllotmentUtilities();
		}

		return instance;
	}

	/**
	 * Private constructor, starts periodically checking for rental expirations
	 */
	private AllotmentUtilities() {
		// Gets the ball rolling for periodic checks
		SingletonRepository.getTurnNotifier().notifyInSeconds(1, this);
	}

	/**
	 * Gets the specified zone
	 *
	 * @param zoneName the name of the zone to get.
	 * @return the zone with the given name
	 */
	private StendhalRPZone getZone(final String zoneName) {
		return (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(zoneName);
	}

	/**
	 * Helper method to get all allotments in a given zone
	 *
	 * @param zoneName the zone for which to get allotments
	 * @return a list of allotments in the zone
	 */
	private List<Entity> getAllotments(final String zoneName) {
		return getZone(zoneName).getFilteredEntities(new FilterCriteria<Entity>() {
			@Override
			public boolean passes(Entity o) {
				return o instanceof Allotment;
			}
		});
	}

	/**
	 * Helper method to get all gates in a given zone
	 *
	 * @param zoneName the zone for which to get gates
	 * @return a list of gates in the zone
	 */
	private List<Entity> getGates(final String zoneName) {
		return getZone(zoneName).getFilteredEntities(new FilterCriteria<Entity>() {
			@Override
			public boolean passes(Entity o) {
				return o instanceof Gate;
			}
		});
	}

	/**
	 * Helper method to get all expiration trackers in a given zone
	 *
	 * @param zoneName the zone for which to get trackers
	 * @return a list of trackers in the zone
	 */
	private List<Entity> getTrackers(final String zoneName) {
		return getZone(zoneName).getFilteredEntities(new FilterCriteria<Entity>() {
			@Override
			public boolean passes(Entity o) {
				return o instanceof ExpirationTracker;
			}
		});
	}

	/**
	 * Helper method to get all players in a zone
	 *
	 * @param zoneName the zone for which to get players
	 * @return a list of players in the zone
	 */
	private List<Player> getPlayers(final String zoneName) {
		return getZone(zoneName).getPlayers();
	}

	/**
	 * Sets the new expiration time for an allotment
	 *
	 * @param zoneName the zone where the allotment is
	 * @param allotment the name of the allotment (found in the zone's xml file)
	 * @param player name of the renting player
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean setExpirationTime(final String zoneName, final String allotment, final String player) {
		for (Entity entity : getTrackers(zoneName)) {
			ExpirationTracker tracker = (ExpirationTracker) entity;

			if (tracker.getIdentifier().equals(ALLOTMENT_PREFIX + allotment)) {
				tracker.setExpirationTime(System.currentTimeMillis() + RENTAL_TIME);
				tracker.setPlayerName(player);

				return true;
			}
		}

		return false;
	}

	/**
	 * Gets a key to an allotment that has been rented out
	 *
	 * @param zoneName the zone the allotment is in
	 * @param player
	 *
	 * @return a key to the allotment
	 */
	public GateKey getKey(final String zoneName, final String player) {
		for (Entity e : getTrackers(zoneName)) {
			ExpirationTracker tracker = (ExpirationTracker) e;
			if (tracker.getPlayerName().equals(player)) {

				final GateKey key = (GateKey) SingletonRepository.getEntityManager().getItem("gate key");
				key.setup(ALLOTMENT_PREFIX + tracker.getIdentifier(), tracker.getExpirationTime());

				return key;
			}
		}

		return null;
	}

	/**
	 * Gets the rental time left given a player
	 *
	 * @param zoneName zone where the allotment is
	 * @param player the player who is enquiring about the time left
	 *
	 * @return the time remaining till the allotment expires
	 */
	public long getTimeLeftPlayer(final String zoneName, final String player) {
		for (Entity e : getTrackers(zoneName)) {
			ExpirationTracker tracker = (ExpirationTracker) e;
			if (tracker.getPlayerName().equals(player)) {
				return tracker.getExpirationTime() - System.currentTimeMillis();
			}
		}

		return 0;
	}

	/**
	 * Gets the time that the next allotment expires
	 *
	 * @param zoneName allotment zone
	 * @return the expiry time for the next allotment that expires
	 */
	public long getNextExpiryTime(String zoneName) {
		ArrayList<Long> times = new ArrayList<Long>();
		for (Entity e : getTrackers(zoneName)) {
			times.add(((ExpirationTracker) e).getExpirationTime());
		}

		Collections.sort(times);

		return times.get(0);
	}

	/**
	 * Gets the rental time left given an allotment
	 *
	 * @param zoneName allotment zone
	 * @param allotment the allotment to check
	 * @return the time remaining till the allotment expires
	 */
	public long getTimeLeftAllotment(final String zoneName, final String allotment) {
		for (Entity e : getTrackers(zoneName)) {
			ExpirationTracker tracker = (ExpirationTracker) e;
			if (tracker.getIdentifier().equals(ALLOTMENT_PREFIX + allotment)) {
				return tracker.getExpirationTime() - System.currentTimeMillis();
			}
		}

		return 0;
	}

	/**
	 * Checks if an allotment exists
	 *
	 * @param zoneName the zone where the allotment is in
	 * @param allotment
	 * @return boolean
	 */
	public boolean isValidAllotment(final String zoneName, final String allotment) {
		for (Entity e : getTrackers(zoneName)) {
			if (((ExpirationTracker) e).getIdentifier().equals(ALLOTMENT_PREFIX + allotment)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if an allotment is available
	 *
	 * @param zoneName the zone where the allotment is in
	 * @param allotment
	 * @return boolean
	 */
	public boolean isAvailableAllotment(final String zoneName, final String allotment) {
		for (Entity e : getTrackers(zoneName)) {
			if (((ExpirationTracker) e).getIdentifier().equals(ALLOTMENT_PREFIX + allotment) &&
				((ExpirationTracker) e).getExpirationTime() < System.currentTimeMillis()) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Get all available allotments in a zone
	 *
	 * @param zoneName the zone to get allotments for
	 * @return a list of available allotment numbers
	 */
	public List<String> getAvailableAllotments(final String zoneName) {
		List<String> available = new ArrayList<String>();

		for (Entity e : getTrackers(zoneName)) {
			if (((ExpirationTracker) e).getIdentifier().startsWith(ALLOTMENT_PREFIX) &&
				((ExpirationTracker) e).getExpirationTime() < System.currentTimeMillis()) {

				available.add(((ExpirationTracker) e).getIdentifier().replace(ALLOTMENT_PREFIX, ""));
			}
		}

		return available;
	}

	/**
	 * Callback to kick players when rental time expires
	 */
	private TurnListener kickPlayers = new TurnListener() {
		@Override
		public void onTurnReached(int currentTurn) {
			// Kick players if they're in an allotment that has expired
			for (String zoneName : zones) {
				for (Entity trackerEntity : getTrackers(zoneName)) {
					ExpirationTracker tracker = (ExpirationTracker) trackerEntity;
					boolean renterFound = false;

					if (tracker.getExpirationTime() <= System.currentTimeMillis()) {
						final String name = tracker.getIdentifier();

						//close the gate
						for (Entity gateEntity : getGates(zoneName)) {
							if (name.equals(gateEntity.get("identifier"))) {
								((Gate) gateEntity).close();
							}
						}

						//clear the players
						for (Entity allotmentEntity : getAllotments(zoneName)) {
							Allotment allotment = (Allotment) allotmentEntity;

							if (allotment.has("name") && name.equals(allotment.getName())) {
								for (Player player : getPlayers(zoneName)) {
									if (player.getArea().intersects(allotment.getArea())) {
										player.teleport(getZone(zoneName), tracker.getX(), tracker.getY(), null, null);
										player.sendPrivateText("The rental time has expired. You will now be excorted out of the allotment. Thank you for your cooperation.");
									}

									if (player.getName().equals(tracker.getPlayerName())) {
										renterFound = true;
									}
								}
							}
						}

						// tell the renter if they're not in the allotment
						if (!renterFound) {
							Player player = SingletonRepository.getRuleProcessor().getPlayer(tracker.getPlayerName());

							if (player != null) {
								player.sendPrivateText("This is just to notify you that the allotment that you rented has expired.");
							} else {
								//TODO: schedule a message at login
							}
						}
					}
				}
			}
		}
	};

	/**
	 * Callback to warn players that rental time is expiring
	 */
	private TurnListener warnPlayers = new TurnListener() {
		@Override
		public void onTurnReached(int currentTurn) {
			// Kick players if they're in an allotment that has expired
			for (String zoneName : zones) {
				for (Entity trackerEntity : getTrackers(zoneName)) {
					ExpirationTracker tracker = (ExpirationTracker) trackerEntity;
					boolean renterFound = false;

					if (tracker.getExpirationTime() - WARN_TIME * 1000 <= System.currentTimeMillis()) {
						final String name = tracker.getIdentifier();

						//warn the players
						for (Entity allotmentEntity : getAllotments(zoneName)) {
							Allotment allotment = (Allotment) allotmentEntity;

							if (allotment.has("name") && name.equals(allotment.getName())) {
								for (Player player : getPlayers(zoneName)) {
									if (player.getArea().intersects(allotment.getArea())) {
										player.sendPrivateText("You have " + WARN_TIME + " minutes left before your rental time expires. You are kindly asked to finish up and exit in an orderly fashion. Kind regards, Semos Allotment Rentals Staff.");
									}

									if (player.getName().equals(tracker.getPlayerName())) {
										renterFound = true;
									}
								}
							}
						}

						// tell the renter if they're not in the allotment
						if (!renterFound) {
							Player player = SingletonRepository.getRuleProcessor().getPlayer(tracker.getPlayerName());

							if (player != null) {
								player.sendPrivateText("This is just to notify you that the allotment that you rented is going to expire in " + WARN_TIME + "minutes.");
							}
						}
					}
				}
			}
		}
	};

	/**
	 * Callback for TurnNotifier - periodically checks if any allotment has
	 * expired and kicks players if it has
	 */
	@Override
	public void onTurnReached(int currentTurn) {
		//Set up the next check
		SingletonRepository.getTurnNotifier().notifyInSeconds(EXPIRATION_CHECKING_PERIOD, this);

		for (String zoneName : zones) {
			for (Entity trackerEntity : new HashSet<Entity>(getTrackers(zoneName))) {
				ExpirationTracker tracker = (ExpirationTracker) trackerEntity;

				// if a warning happens within the next checking period schedule a warn
				if (tracker.getExpirationTime() - WARN_TIME * 1000 <= System.currentTimeMillis()
						+ EXPIRATION_CHECKING_PERIOD * 1000 &&
					tracker.getExpirationTime() - WARN_TIME * 1000 > System.currentTimeMillis()) {

					long diff = tracker.getExpirationTime() - WARN_TIME * 1000 - System.currentTimeMillis();
					SingletonRepository.getTurnNotifier().notifyInSeconds((int) diff / 1000, warnPlayers);
				}

				// if an expiration happens within the next checking period schedule a kick
				if (tracker.getExpirationTime() <= System.currentTimeMillis()
						+ EXPIRATION_CHECKING_PERIOD  * 1000 &&
					tracker.getExpirationTime() > System.currentTimeMillis()) {

					long diff = tracker.getExpirationTime() - System.currentTimeMillis();
					SingletonRepository.getTurnNotifier().notifyInSeconds((int) diff / 1000, kickPlayers);
				}
			}
		}
	}
}
