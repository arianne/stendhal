package games.stendhal.server.script;

import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Moves players away that spend to much time in an restricted area
 *
 * @author hendrik
 */
public class Unblock extends ScriptImpl implements TurnListener {
	private static final int CHECK_INTERVAL = 30;
	private List<PlayerPositionEntry> playerPositions = new LinkedList<PlayerPositionEntry>();

	/**
	 * records a player position in a specific turn
	 */
	static class PlayerPositionEntry {
		private String playerName;
		private String zoneName;
		private int x;
		private int y;
		private int turn;

		/**
		 * creates a new PlayerPositionEntry
		 * @param playerName name of player
		 * @param zoneName name of zone
		 * @param x x
		 * @param y y
		 * @param turn turn number
		 */
		public PlayerPositionEntry(String playerName, String zoneName, int x, int y, int turn) {
			super();
			this.playerName = playerName;
			this.zoneName = zoneName;
			this.x = x;
			this.y = y;
			this.turn = turn;
		}

		
	}

	/**
	 * checks
	 */
	public void onTurnReached(int currentTurn) {
		TurnNotifier.get().notifyInSeconds(CHECK_INTERVAL, this);
		cleanupList();
		teleportAway();
		record();
	}


	private void cleanupList() {
		// TODO Auto-generated method stub
		
	}


	private void teleportAway() {
		// TODO Auto-generated method stub
		
	}

	private void record() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * executes the script
	 */
	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		TurnNotifier.get().dontNotify(this);
		TurnNotifier.get().notifyInSeconds(CHECK_INTERVAL, this);
	}

	@Override
	public void unload(Player admin, List<String> args) {
		super.unload(admin, args);
		TurnNotifier.get().dontNotify(this);
	}

}
