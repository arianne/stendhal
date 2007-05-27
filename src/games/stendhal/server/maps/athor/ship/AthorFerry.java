package games.stendhal.server.maps.athor.ship;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.util.TimeUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class simulates a ferry going back and forth between the mainland
 * and the island. Note that, even though this class lies in a maps package,
 * this is not a zone configurator.
 * 
 * NPCs that have to do with the ferry:
 * Eliza   - brings players from the mainland docks to the ferry.
 * Jessica - brings players from the island docks to the ferry.
 * Jackie  - brings players from the ferry to the docks.
 * Captain - the ship captain.
 * Laura   - the ship galley maid.
 * Ramon   - offers blackjack on the ship. 
 * 
 * @see games.stendhal.server.maps.athor.ship.CaptainNPC
 * @author daniel
 *
 */
public class AthorFerry implements TurnListener {

	public static abstract class FerryAnnouncerNPC extends SpeakerNPC {
    
    	public FerryAnnouncerNPC(String name) {
    		super(name);
    	}
    
    	public abstract void onNewFerryState(int status);
    }

	public static final int ANCHORED_AT_MAINLAND = 0;

	public static final int DRIVING_TO_ISLAND = 1;

	public static final int ANCHORED_AT_ISLAND = 2;

	public static final int DRIVING_TO_MAINLAND = 3;

	/** The Singleton instance. */
	private static AthorFerry instance;

	/**
	 * A list of non-player characters that get notice when the ferry
	 * arrives or departs, so that they can react accordingly, e.g.
	 * inform nearby players.
	 */
	private List<AthorFerry.FerryAnnouncerNPC> listeners;
	
	private int state;

	/** How much it costs to board the ferry */
    public static final int PRICE = 25;

	/**
	 * Maps each step (anchoring/driving) to the time (in seconds)
	 * it takes.
	 */
	private static Map<Integer, Integer> durations;

	private static Map<Integer, String> descriptions;

	private AthorFerry() {
		durations = new HashMap<Integer, Integer>();
		durations.put(ANCHORED_AT_MAINLAND, 2 * 60);
		durations.put(DRIVING_TO_ISLAND, 5 * 60);
		durations.put(ANCHORED_AT_ISLAND, 2 * 60);
		durations.put(DRIVING_TO_MAINLAND, 5 * 60);

		descriptions = new HashMap<Integer, String>();
		descriptions.put(ANCHORED_AT_MAINLAND,
				"The ferry is currently anchored at the mainland. It will take off in %s.");
		descriptions.put(DRIVING_TO_ISLAND,
		        "The ferry is currently sailing to the island. It will arrive in %s.");
		descriptions.put(ANCHORED_AT_ISLAND,
		        "The ferry is currently anchored at the island. It will take off in %s.");
		descriptions.put(DRIVING_TO_MAINLAND,
		        "The ferry is currently sailing to the mainland. It will arrive in %s.");
		state = DRIVING_TO_MAINLAND;
		
		listeners = new LinkedList<AthorFerry.FerryAnnouncerNPC>();
		// initiate the turn notification cycle
		TurnNotifier.get().notifyInSeconds(1, this);
	}

	/**
	 * @return The Singleton instance.
	 */
	public static AthorFerry get() {
		if (instance == null) {
			instance = new AthorFerry();
		}
		return instance;
	}
	
	/**
	 * @return one of ANCHORED_AT_MAINLAND, DRIVING_TO_ISLAND,
	 *         ANCHORED_AT_ISLAND, and DRIVING_TO_MAINLAND.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Gets a textual description of the ferry's status.
	 * @return A String representation of the ferry's current state.
	 */
	public String getCurrentDescription() {
		int secondsUntilNextState = TurnNotifier.get().getRemainingSeconds(this);
		return String.format(descriptions.get(state), TimeUtil.approxTimeUntil(secondsUntilNextState));	}

	/**
	 * Is called when the ferry has either arrived at or departed from
	 * a harbor.
	 */
	public void onTurnReached(int currentTurn, String message) {
		// cycle to the next state
		state = (state + 1) % 4;
		for (AthorFerry.FerryAnnouncerNPC npc: listeners) {
			npc.onNewFerryState(state);
		}
		TurnNotifier.get().notifyInSeconds(durations.get(state), this);
	}
	
	public void addListener(AthorFerry.FerryAnnouncerNPC npc) {
		listeners.add(npc);
	}

	public void boardFerry(Player player) {
		StendhalRPZone shipZone = (StendhalRPZone) StendhalRPWorld.get()
				.getRPZone("0_athor_ship_w2");
		player.teleport(shipZone, 27, 33, Direction.LEFT, null);
	}
	
	public void disembarkToMainland(Player player) {
		StendhalRPZone mainlandDocksZone = (StendhalRPZone) StendhalRPWorld
				.get().getRPZone("0_ados_coast_s_w2");
		player.teleport(mainlandDocksZone, 100, 100, Direction.LEFT, null);
	}

	public void disembarkToIsland(Player player) {
		StendhalRPZone islandDocksZone = (StendhalRPZone) StendhalRPWorld
				.get().getRPZone("0_athor_island");
		player.teleport(islandDocksZone, 16, 89, Direction.LEFT, null);
	}

}