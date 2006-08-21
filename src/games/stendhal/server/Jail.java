package games.stendhal.server;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.TurnNotifier;
import games.stendhal.server.entity.Player;
import games.stendhal.server.events.TurnListener;
import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.server.game.RPWorld;

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
	
	private RPWorld world;
	
	private StendhalRPRuleProcessor rules;
	
	public static Jail get(RPWorld world, StendhalRPRuleProcessor rules) {
		if (instance == null) {
			instance = new Jail(world, rules);
		}
		return instance;
	}
	
	public Jail(RPWorld world, StendhalRPRuleProcessor rules) {
		this.world = world;
		this.rules = rules;
	}
	
	/**
	 * @param criminalName The name of the player who should be jailed
	 * @param policeman The name of the admin who wants to jail the criminal
	 * @param minutes The duration of the sentence
	 */
	public void imprison(String criminalName, Player policeman, int minutes) {
		System.out.println(minutes);
		Player criminal = rules.getPlayer(criminalName);

		if (criminal == null) {
			String text = "Player " + criminalName + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
		}

		IRPZone.ID zoneid = new IRPZone.ID("-1_semos_jail");
		if (!world.hasRPZone(zoneid)) {
			String text = "Zone " + zoneid + " not found";
			policeman.sendPrivateText(text);
			logger.debug(text);
		}
		StendhalRPZone jail = (StendhalRPZone) world.getRPZone(zoneid);
		
		criminal.teleport(jail, 8, 2, Direction.DOWN, policeman, rules);

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
	 * @param inmateName the name of the inmate who should be released
	 */
	public void release(String inmateName) {
		Player inmate = rules.getPlayer(inmateName);
		if (inmate == null) {
			logger.debug("Player " + inmate + " not found");
		}

		// Only teleport the player to Semos if he is still in jail.
		// It could be that an admin has teleported him out earlier.
		if (world.getRPZone(inmate.getID()).getID().getID().endsWith("_jail")) {
			IRPZone.ID zoneid = new IRPZone.ID("0_semos_city");
			if (!world.hasRPZone(zoneid)) {
				String text = "Zone " + zoneid + " not found";
				logger.debug(text);
			}
			StendhalRPZone semosCity = (StendhalRPZone) world.getRPZone(zoneid);
			
			inmate.teleport(semosCity, 30, 40, Direction.UP, null, rules);
		}
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
