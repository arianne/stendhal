package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Moves players away that spend to much time in an restricted area
 *
 * @author hendrik
 */
public class Unblock extends ScriptImpl implements TurnListener {
	private static final int CHECK_INTERVAL = 30;
	private Set<PlayerPositionEntry> playerPositions = new HashSet<PlayerPositionEntry>();

	/**
	 * records a player position in a specific turn
	 */
	static class PlayerPositionEntry {
		private String playerName;
		private String zoneName;
		private int x;
		private int y;
		private int turn;
		private KeepFreeArea keepFreeArea;

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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result+ playerName.hashCode();
			result = prime * result + turn;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + zoneName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof PlayerPositionEntry)) {
				return false;
			}
			PlayerPositionEntry other = (PlayerPositionEntry) obj;
			if (!playerName.equals(other.playerName)) {
				return false;
			}
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			if (!zoneName.equals(other.zoneName)) {
				return false;
			}
			return true;
		}

		/**
		 * has the player moved away including leaving the zone or logging out
		 *
		 * @return true if the player moved away, false if he is still in the restricted area
		 */
		public boolean hasPlayerMovedAway() {
			Player player = SingletonRepository.getRuleProcessor().getPlayer(playerName);
			if (player == null) {
				return true;
			}
			if (!player.getZone().getName().equals(zoneName)) {
				return true;
			}
			if (player.getX() != x) {
				return true;
			}
			if (player.getY() != y) {
				return true;
			}
			return false;
		}

		
	}

	/**
	 * an area to keep free with an associated teleportation target spot
	 */
	static class KeepFreeArea {
		private Area area;
		private int x;
		private int y;

		/**
		 * creates a new KeepFreeArea
		 *
		 * @param area area to keep free
		 * @param x teleportation target x
		 * @param y teleportation target y
		 */
		public KeepFreeArea(Area area, int x, int y) {
			super();
			this.area = area;
			this.x = x;
			this.y = y;
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

	/**
	 * removes players that have moved away from the list
	 */
	private void cleanupList() {
		Iterator<PlayerPositionEntry> itr = playerPositions.iterator();
		while (itr.hasNext()) {
			PlayerPositionEntry entry = itr.next();
			if (entry.hasPlayerMovedAway()) {
				itr.remove();
			}
		}
		
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
