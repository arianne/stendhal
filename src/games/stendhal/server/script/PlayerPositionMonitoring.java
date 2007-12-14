package games.stendhal.server.script;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

/**
 * List the players and their positions over a period of time.
 * 
 * @author hendrik
 */
public class PlayerPositionMonitoring extends ScriptImpl {

	/**
	 * Listener for turn events
	 */
	protected static class PlayerPositionListener implements TurnListener {

		// 5 10 15 30 60 120, 300
		private final int[] INTERVALS = new int[] { 5, 5, 5, 15, 30, 60, 280 };

		private Player admin;

		private int counter;

		/**
		 * creates a new PlayerPositionListener
		 * 
		 * @param admin
		 *            the admin to notify
		 */
		protected PlayerPositionListener(Player admin) {
			this.admin = admin;
		}

		private void list() {
			// create player list
			StringBuilder sb = new StringBuilder(String.valueOf(counter));
			sb.append(": ");

			for (Player player : StendhalRPRuleProcessor.get().getPlayers()) {
				if (sb.length() > 10) {
					sb.append(", ");
				}

				sb.append(player.getTitle());
				sb.append(' ');

				StendhalRPZone zone = player.getZone();

				if (zone != null) {
					sb.append(zone.getName());
				} else {
					sb.append("(none)");
				}

				sb.append(' ');
				sb.append(player.getX());
				sb.append(' ');
				sb.append(player.getY());
			}

			admin.sendPrivateText(sb.toString());
		}

		public void onTurnReached(int currentTurn) {
			list();
			if (counter < INTERVALS.length) {
				TurnNotifier.get().notifyInTurns(
						(int) (INTERVALS[counter] * 1000 / 300f), this);
			}
			counter++;
		}
	}

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		TurnNotifier.get().notifyInTurns(1, new PlayerPositionListener(admin));
	}

}
