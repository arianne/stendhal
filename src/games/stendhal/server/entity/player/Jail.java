package games.stendhal.server.entity.player;

import games.stendhal.common.Direction;
import games.stendhal.server.actions.ChatAction;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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
	protected static final String DEFAULT_JAIL_ZONE = "-1_semos_jail";

	static StendhalRPZone jailzone;

	private final class Jailer implements TurnListener {
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Jailer) {
				Jailer other = (Jailer) obj;
				return _name.equals(other._name);
			}

			return false;
		}

		@Override
		public int hashCode() {

			return _name.hashCode();
		}

		String _name;

		private Jailer(String name) {

			_name = name;
		}

		public void onTurnReached(int currentTurn) {
			/*
			 * TODO: If player is not present, we should reset the sentence
			 * timer on login
			 */

			if (!release(_name)) {
				// The player has logged out. Release him when he logs in again.
				namesOfPlayersToRelease.add(_name);
			}

		}
	}

	private static final Logger logger = Logger.getLogger(Jail.class);

	/** The Singleton instance */
	private static Jail instance;

	/*
	 * TODO: Bad smell, hard coded list of points in the jail zone for where to
	 * land.
	 */
	private static List<Point> cellEntryPoints = Arrays.asList(new Point(3, 2),
			new Point(8, 2),
			// elf cell new Point(13, 2),
			new Point(18, 2), new Point(23, 2), new Point(28, 2), new Point(8,
					11), new Point(13, 11), new Point(18, 11),
			new Point(23, 11), new Point(28, 11));

	private static Rectangle[] cellBlocks = { new Rectangle(1, 1, 30, 3),
			new Rectangle(7, 10, 30, 12) };

	// TODO: make this persistent, Don't use quest for this.
	private List<String> namesOfPlayersToRelease;

	/**
	 * returns the Jail object (Singleton Pattern)
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
		namesOfPlayersToRelease = new ArrayList<String>();
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

		if (criminal == null) {
			String text = "Player " + criminalName + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}

		imprison(criminal, policeman, minutes, reason);
	}

	protected void imprison(final Player criminal, Player policeman,
			int minutes, String reason) {

		getJailzone();

		if (jailzone == null) {
			String text = "Zone " + DEFAULT_JAIL_ZONE + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}
		boolean successful = teleportToAvailableCell(criminal, policeman);
		if (successful) {
			policeman.sendPrivateText("You have jailed " + criminal.getName()
					+ " for " + minutes + " minutes. Reason: " + reason + ".");
			criminal.sendPrivateText("You have been jailed by "
					+ policeman.getTitle() + " for " + minutes
					+ " minutes. Reason: " + reason + ".");
			ChatAction.sendMessageToSupporters("JailKeeper",
					policeman.getTitle() + " jailed " + criminal.getName()
							+ " for " + minutes + " minutes. Reason: " + reason
							+ ".");

			Jailer jailer = new Jailer(criminal.getName());
			TurnNotifier.get().dontNotify(jailer);

			// Set a timer so that the inmate is automatically released after
			// serving his sentence. We're using the TurnNotifier; we use
			//
			// NOTE: The player won't be automatically released if the
			// server is restarted before the player could be released.
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
			StendhalRPZone semosCity = (StendhalRPZone) world.getRPZone(zoneid);

			inmate.teleport(semosCity, 6, 3, Direction.RIGHT, null);
			inmate.sendPrivateText("Your sentence is over. You can walk out now.");
			logger.debug("Player " + inmate.getName() + "released from jail.");
		}
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

	public void onLoggedIn(Player player) {
		/*
		 * TODO: Refactor We should be able to manipulate the offline object.
		 * 
		 */
		String name = player.getName();
		if (namesOfPlayersToRelease.contains(name)) {
			release(name);
			namesOfPlayersToRelease.remove(name);
		}
	}
}
