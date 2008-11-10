package games.stendhal.server.entity.player;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrantList;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import marauroa.common.game.IRPZone;

import org.apache.log4j.Logger;

/**
 * This class is responsible of keeping players who have misbehaved in a special
 * jail area where they can't do any harm. The misbehaving player will be
 * automatically released after a specified number of minutes.
 *
 * @author daniel
 */
public class Jail implements LoginListener {

	private static final Logger logger = Logger.getLogger(Jail.class);

	// package visible because of tests
	public static final String DEFAULT_JAIL_ZONE = "-1_semos_jail";
	static StendhalRPZone jailzone;
	ArrestWarrantList arrestWarrants;

	/** The Singleton instance. */
	private static Jail instance;


	private static List<Point> cellEntryPoints = Arrays.asList(
		new Point(3, 3),
		new Point(8, 3),
		// elf cell(13, 3),
		new Point(18, 3), 
		new Point(23, 3), 
		new Point(28, 3), 
		new Point(8, 11),
		new Point(13, 11),
		new Point(18, 11),
		new Point(23, 11),
		new Point(28, 11)
	);

	private static Rectangle[] cellBlocks = { 
		new Rectangle(1, 1, 30, 3),
		new Rectangle(7, 10, 30, 3)
	};

	private final class Jailer implements TurnListener {

		private final String criminalName;

		Jailer(final String name) {
			criminalName = name;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj instanceof Jailer) {
				final Jailer other = (Jailer) obj;
				return criminalName.equals(other.criminalName);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return criminalName.hashCode();
		}

		public void onTurnReached(final int currentTurn) {
			release(criminalName);
		}
	}

	/**
	 * returns the Jail object (Singleton Pattern).
	 *
	 * @return Jail
	 */
	public static Jail get() {
		if (instance == null) {
			instance = new Jail();
		}
		return instance;
	}

	// singleton
	private Jail() {
		
		arrestWarrants = new ArrestWarrantList(getJailzone());
		SingletonRepository.getLoginNotifier().addListener(this);
	}
	/**
	 * @param criminalName
	 *            The name of the player who should be jailed
	 * @param policeman
	 *            The name of the admin who wants to jail the criminal
	 * @param minutes
	 *            The duration of the sentence
	 * @param reason why criminal was jailed
	 */
	public void imprison(final String criminalName, final Player policeman,
			final int minutes, final String reason) {

		final Player criminal = SingletonRepository.getRuleProcessor().getPlayer(
						criminalName);

		arrestWarrants.removeByName(criminalName);
		final ArrestWarrant arrestWarrant = new ArrestWarrant(criminalName, policeman, minutes, reason);

		policeman.sendPrivateText("You have jailed " + criminalName
			+ " for " + minutes + " minutes. Reason: " + reason + ".");
		StendhalRPRuleProcessor.sendMessageToSupporters("JailKeeper",
			policeman.getName() + " jailed " + criminalName
			+ " for " + minutes + " minutes. Reason: " + reason
			+ ".");

		if (criminal == null) {
			final String text = "Player " + criminalName + " is not online, but the arrest warrant has been recorded anyway.";
			policeman.sendPrivateText(text);
			logger.debug(text);
		} else {
			arrestWarrant.setStarted();
			imprison(criminal, policeman, minutes);
			criminal.sendPrivateText("You have been jailed by "
					+ policeman.getName() + " for " + minutes
					+ " minutes. Reason: " + reason + ".");
			
			
		}
		arrestWarrants.add(arrestWarrant);
	}

	protected void imprison(final Player criminal, final Player policeman, final int minutes) {

		if (getJailzone() == null) {
			final String text = "Zone " + DEFAULT_JAIL_ZONE + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}
		final boolean successful = teleportToAvailableCell(criminal, policeman);
		if (successful) {
			final Jailer jailer = new Jailer(criminal.getName());
			SingletonRepository.getTurnNotifier().dontNotify(jailer);

			// Set a timer so that the inmate is automatically released after
			// serving his sentence.
			SingletonRepository.getTurnNotifier().notifyInSeconds(minutes * 60, jailer);
		} else {
			policeman.sendPrivateText("Could not find a cell for "
					+ criminal.getName());
		}
	}

	private boolean teleportToAvailableCell(final Player criminal,
			final Player policeman) {
		Collections.shuffle(cellEntryPoints);
		for (final Point cell : cellEntryPoints) {
			if (criminal.teleport(getJailzone(), cell.x, cell.y, Direction.DOWN,
					policeman)) {
				return true;
			}
		}

		return false;
	}

	private static StendhalRPZone getJailzone() {
		if (jailzone == null) {
			final StendhalRPWorld world = SingletonRepository.getRPWorld();
			jailzone = (StendhalRPZone) world.getRPZone(DEFAULT_JAIL_ZONE);
		}

		return jailzone;
	}

	/**
	 * Releases an inmate and teleports him to Semos city, but only if he is
	 * still in jail.
	 *
	 * @param inmateName
	 *            the name of the inmate who should be released
	 * @return true if the player has not logged out before he was released
	 */
	public boolean release(final String inmateName) {
		final Player inmate = SingletonRepository.getRuleProcessor().getPlayer(inmateName);
		if (inmate == null) {
			logger.debug("Jailed player " + inmateName + "has logged out.");
			return false;
		}

		release(inmate);
		return true;
	}

	void release(final Player inmate) {
		// Only teleport the player if he is still in jail.
		// It could be that an admin has teleported him out earlier.
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		if (isInJail(inmate)) {
			final IRPZone.ID zoneid = new IRPZone.ID("-3_semos_jail");
			if (!world.hasRPZone(zoneid)) {
				logger.debug("Zone " + zoneid + " not found");
			}
			final StendhalRPZone exitZone = (StendhalRPZone) world.getRPZone(zoneid);

			inmate.teleport(exitZone, 6, 3, Direction.RIGHT, null);
			inmate.sendPrivateText("Your sentence is over. You can walk out now.");
			logger.debug("Player " + inmate.getName() + "released from jail.");
		}

		// The player completed his sentence and did not logout
		// so destroy the ArrestWarrant
		arrestWarrants.removeByName(inmate.getName());
	}

	/**
	 * Is player in a jail cell? Ignores visitors outside of cells.
	 *
	 * @param inmate
	 *            player to check
	 * @return true, if it is in jail, false otherwise.
	 */
	public static boolean isInJail(final Player inmate) {
		final StendhalRPZone zone = inmate.getZone();

		if ((zone != null) && zone.equals(getJailzone())) {
			for (final Rectangle cellBlock : cellBlocks) {
				if (cellBlock.contains(inmate.getX(), inmate.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Destroy the arrest warrent so that the player is not rejailed on next login.
	 * @param player
	 */
	public void grantParoleIfPlayerWasAPrisoner(final Player player) {
		
		arrestWarrants.removeByName(player.getName());
	}

	public void onLoggedIn(final Player player) {
		// we need to do this on the next turn because the
		// client does not get any private messages otherwise
		// sometime the client does not get the map content
		// if it gets a cross zone teleport too early. so we wait
		// 5 seconds.
		SingletonRepository.getTurnNotifier().notifyInSeconds(5, new TurnListener() {
			public void onTurnReached(final int currentTurn) {
				final String name = player.getName();

				final ArrestWarrant arrestWarrant = arrestWarrants.getByName(name);
				if (arrestWarrant == null) {
					return;
				}

				if (removeVeryOldWarrants(arrestWarrant)) {
					return;
				}

				final long timestamp = arrestWarrant.getTimestamp();
				player.sendPrivateText("You have been jailed by "
					+ arrestWarrant.getPoliceOfficer()
					+ " for " + arrestWarrant.getMinutes()
					+ " minutes on " + String.format("%tF", timestamp)
					+ ". Reason: " + arrestWarrant.getReason() + ".");

				handleEscapeMessages(arrestWarrant);
				imprison(player, player, arrestWarrant.getMinutes());
			}

			public boolean removeVeryOldWarrants(final ArrestWarrant arrestWarrant) {
				final long timestamp = arrestWarrant.getTimestamp();
				if (timestamp + 30 * MathHelper.MILLISECONDS_IN_ONE_DAY < System.currentTimeMillis()) {
					arrestWarrants.removeByName(arrestWarrant.getCriminal());
					return true;
				}

				return false;
			}
			

			public void handleEscapeMessages(final ArrestWarrant arrestWarrant) {
				if (arrestWarrant.isStarted()) {
					// Notify player that his sentences is starting again because he tried to escape by logging out
					player.sendPrivateText("Although you already spent some "
							+ "time in jail, your sentence has been restarted " 
							+ "because of your failed escape attempt.");
				} else {
					// Jail player who was offline at the time /jail was issued.
					arrestWarrant.setStarted();
				}
			}
		});
	}

	public String listJailed() {
		if (arrestWarrants != null) {
			return arrestWarrants.toString();
		}
		return "jail not inited ?";
	}
	
	/**
	 * Get the ArrestWarrant of a jailed player.
	 * 
	 * @param name the name of the player
	 * @return the ArrestWarrant for the player, or null if there is none
	 */
	public ArrestWarrant getWarrant(String name) {
		return arrestWarrants.getByName(name);
	}
}
