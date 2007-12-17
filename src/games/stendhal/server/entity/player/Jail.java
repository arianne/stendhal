package games.stendhal.server.entity.player;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import games.stendhal.server.entity.mapstuff.office.ArrestWarrantList;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

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

	// package visibile because of tests
	static final String DEFAULT_JAIL_ZONE = "-1_semos_jail";
	static StendhalRPZone jailzone;
	ArrestWarrantList arrestWarrants;

	/** The Singleton instance. */
	private static Jail instance;

	/*
	 * TODO: Bad smell, hard coded list of points in the jail zone for where to
	 * land.
	 */
	private static List<Point> cellEntryPoints = Arrays.asList(
		new Point(3, 2),
		new Point(8, 2),
		// elf cell new Point(13, 2),
		new Point(18, 2), 
		new Point(23, 2), 
		new Point(28, 2), 
		new Point(8, 11),
		new Point(13, 11),
		new Point(18, 11),
		new Point(23, 11),
		new Point(28, 11)
	);

	private static Rectangle[] cellBlocks = { 
		new Rectangle(1, 1, 30, 3),
		new Rectangle(7, 10, 30, 12)
	};

	private final class Jailer implements TurnListener {

		private String criminalName;

		Jailer(String name) {
			criminalName = name;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Jailer) {
				Jailer other = (Jailer) obj;
				return criminalName.equals(other.criminalName);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return criminalName.hashCode();
		}

		public void onTurnReached(int currentTurn) {
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
		getJailzone();
		arrestWarrants = new ArrestWarrantList(jailzone);
		LoginNotifier.get().addListener(this);
	}

	/**
	 * @param criminalName
	 *            The name of the player who should be jailed
	 * @param policeman
	 *            The name of the admin who wants to jail the criminal
	 * @param minutes
	 *            The duration of the sentence
	 */
	public void imprison(final String criminalName, Player policeman,
			int minutes, String reason) {

		final Player criminal = StendhalRPRuleProcessor.get().getPlayer(
						criminalName);

		ArrestWarrant arrestWarrant = new ArrestWarrant(criminalName, policeman, minutes, reason);

		policeman.sendPrivateText("You have jailed " + criminalName
			+ " for " + minutes + " minutes. Reason: " + reason + ".");
		StendhalRPRuleProcessor.sendMessageToSupporters("JailKeeper",
			policeman.getName() + " jailed " + criminalName
			+ " for " + minutes + " minutes. Reason: " + reason
			+ ".");
		
		if (criminal == null) {
			String text = "Player " + criminalName + " is not online, but the arrest warrant has been recorded anyway.";
			policeman.sendPrivateText(text);
			logger.debug(text);
		} else {
			arrestWarrant.setStarted();
			criminal.sendPrivateText("You have been jailed by "
				+ policeman.getName() + " for " + minutes
				+ " minutes. Reason: " + reason + ".");
			imprison(criminal, policeman, minutes);
		}

		arrestWarrants.add(arrestWarrant);
	}

	protected void imprison(final Player criminal, Player policeman, int minutes) {

		getJailzone();

		if (jailzone == null) {
			String text = "Zone " + DEFAULT_JAIL_ZONE + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}
		boolean successful = teleportToAvailableCell(criminal, policeman);
		if (successful) {
			Jailer jailer = new Jailer(criminal.getName());
			TurnNotifier.get().dontNotify(jailer);

			// Set a timer so that the inmate is automatically released after
			// serving his sentence.
			TurnNotifier.get().notifyInSeconds(minutes * 60, jailer);
		} else {
			policeman.sendPrivateText("Could not find a cell for"
					+ criminal.getName());
		}
	}

	private boolean teleportToAvailableCell(final Player criminal,
			Player policeman) {
		Collections.shuffle(cellEntryPoints);
		for (Point cell : cellEntryPoints) {
			if (criminal.teleport(jailzone, cell.x, cell.y, Direction.DOWN,
					policeman)) {
				return true;
			}
		}

		return false;
	}

	private void getJailzone() {
		if (jailzone == null) {
			StendhalRPWorld world = StendhalRPWorld.get();
			jailzone = (StendhalRPZone) world.getRPZone(DEFAULT_JAIL_ZONE);
		}
	}

	/**
	 * Releases an inmate and teleports him to Semos city, but only if he is
	 * still in jail.
	 *
	 * @param inmateName
	 *            the name of the inmate who should be released
	 * @return true if the player has not logged out before he was released
	 */
	public boolean release(String inmateName) {

		Player inmate = StendhalRPRuleProcessor.get().getPlayer(inmateName);
		if (inmate == null) {
			logger.debug("Jailed player " + inmateName + "has logged out.");
			return false;
		}

		release(inmate);
		return true;
	}

	void release(Player inmate) {
		// Only teleport the player if he is still in jail.
		// It could be that an admin has teleported him out earlier.
		StendhalRPWorld world = StendhalRPWorld.get();
		if (isInJail(inmate)) {
			IRPZone.ID zoneid = new IRPZone.ID("-3_semos_jail");
			if (!world.hasRPZone(zoneid)) {
				logger.debug("Zone " + zoneid + " not found");
			}
			StendhalRPZone exitZone = (StendhalRPZone) world.getRPZone(zoneid);

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
	public static boolean isInJail(Player inmate) {

		if (inmate.getZone().equals(jailzone)) {
			for (Rectangle cellBlock : cellBlocks) {
				if (cellBlock.contains(inmate.getX(), inmate.getY())) {
					return true;
				}
			}
		}
		return false;
	}

	public void onLoggedIn(final Player player) {
		// we need to do this on the next turn because the
		// client does not get any private messages otherwise
		TurnNotifier.get().notifyInTurns(1, new TurnListener() {
			public void onTurnReached(int currentTurn) {
				String name = player.getName();
				ArrestWarrant arrestWarrant = arrestWarrants.getByName(name);
				if (arrestWarrant != null) {
					long timestamp = arrestWarrant.getTimestamp();
					if (timestamp + 30 * 24 * 60 * 60 * 100 < System.currentTimeMillis()) {
						arrestWarrants.removeByName(name);
					} else {
						player.sendPrivateText("You have been jailed by "
							+ arrestWarrant.getPoliceOfficer()
							+ " for " + arrestWarrant.getMinutes()
							+ " minutes on " + String.format("%tF", timestamp)
							+ ". Reason: " + arrestWarrant.getReason() + ".");
		
						if (arrestWarrant.isStarted()) {
							// Notify player that his sentences is starting again because he tried to escape by logging out
							player.sendPrivateText("Although you already spent some "
									+ "time in jail, your sentence has been restarted " 
									+ "because of your failed escape attempt.");
						} else {
			
							// Jail player who was offline at the time /jail was issued.
							arrestWarrant.setStarted();
						}
						imprison(player, player, arrestWarrant.getMinutes());
					}
				}
			}
		});
	}
}
