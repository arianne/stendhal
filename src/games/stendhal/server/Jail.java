package games.stendhal.server;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;

import org.apache.log4j.Logger;

/**
 * This class is responsible of keeping players who have misbehaved
 * in a special jail area where they can't do any harm.
 * The misbehaving player will be automatically released after a
 * specified number of minutes.
 * 
 * @author daniel
 */
public class Jail implements TurnListener {

	private static final Logger logger = Log4J.getLogger(Jail.class);
	
	/** The Singleton instance */
	private static Jail instance = null;
	
	private static List<Point> cellEntryPoints = Arrays.asList(
			new Point(3, 2),
			new Point(8, 2),
			new Point(13, 2),
			new Point(18, 2),
			new Point(23, 2),
			new Point(28, 2),
			new Point(8, 11),
			new Point(13, 11),
			new Point(18, 11),
			new Point(23, 11),
			new Point(28, 11));

	private static Rectangle[] cellBlocks = {
		new Rectangle(1,  1, 30,  3),
		new Rectangle(7, 10, 30, 12)};

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
	
	/**
	 * @param criminalName The name of the player who should be jailed
	 * @param policeman The name of the admin who wants to jail the criminal
	 * @param minutes The duration of the sentence
	 */
	public void imprison(String criminalName, Player policeman, int minutes) {
		StendhalRPWorld world = StendhalRPWorld.get();
		Player criminal = StendhalRPRuleProcessor.get().getPlayer(criminalName);

		if (criminal == null) {
			String text = "Player " + criminalName + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}

		IRPZone.ID zoneid = new IRPZone.ID("-1_semos_jail");
		if (!world.hasRPZone(zoneid)) {
			String text = "Zone " + zoneid + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
			return;
		}
		StendhalRPZone jail = (StendhalRPZone) world.getRPZone(zoneid);

		// TODO: fix endless loop if all cells are used
		boolean successful = false;
		while (!successful) {
			// repeat until we find a free cell
			int cellNumber = Rand.rand(cellEntryPoints.size());
			Point cell = cellEntryPoints.get(cellNumber);
			successful = criminal.teleport(jail, cell.x, cell.y, Direction.DOWN, policeman);
		}

		// Set a timer so that the inmate is automatically released after
		// serving his sentence. We're using the TurnNotifier; we use
		// the 'message' paramter to store the player's name, so that
		// we know who is to be released when onTurnReached() is called.
		// NOTE: The player won't be automatically released if the
		// server is restarted while the player is in jail, or if the
		// player is logged out at the time when the sentence is over. 
		// convert from minutes to turns.
		// TODO: don't hardcode 300 ms per turn
		int jailTime = minutes * 60 * 1000 / 300;
		TurnNotifier.get().notifyInTurns(jailTime, this, criminalName);
	}
	
	/**
	 * Releases an inmate and teleports him to Semos city, but only if
	 * he is still in jail.
	 *
	 * @param inmateName the name of the inmate who should be released
	 */
	public void release(String inmateName) {
		StendhalRPWorld world = StendhalRPWorld.get();
		Player inmate = StendhalRPRuleProcessor.get().getPlayer(inmateName);
		if (inmate == null) {
			logger.debug("Player " + inmate + " not found");
			return;
		}

		// Only teleport the player to Semos if he is still in jail.
		// It could be that an admin has teleported him out earlier.
		if (isInJail(inmate)) {
			IRPZone.ID zoneid = new IRPZone.ID("0_semos_city");
			if (!world.hasRPZone(zoneid)) {
				String text = "Zone " + zoneid + " not found";
				logger.debug(text);
			}
			StendhalRPZone semosCity = (StendhalRPZone) world.getRPZone(zoneid);
			
			inmate.teleport(semosCity, 30, 40, Direction.UP, null);
		}
	}

	/**
	 * Is player in a jail cell? Ignores visitors outside of cells.
	 *
	 * @param inmate player to check
	 * @return true, if it is in jail, false otherwise.
	 */
	private boolean isInJail(Player inmate) {
		StendhalRPWorld world = StendhalRPWorld.get();
		String zoneName = world.getRPZone(inmate.getID()).getID().getID();
		if ((zoneName != null) && zoneName.equals("-1_semos_jail")) {
			for (Rectangle cellBlock : cellBlocks) {
				if (cellBlock.contains(inmate.getX(), inmate.getY())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Is called when the time has come to release an inmate.
	 * @param turn
	 * @param message the inmate's name
	 */
	public void onTurnReached(int turn, String message) {
		release(message);
	}
}
