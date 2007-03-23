package games.stendhal.server.maps.deathmatch;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;

/**
 * Teleport the player far away if he/she had logged out in the deathmatch arena.
 */
public class DealWithLogoutCoward implements TurnListener {

	private Player player = null;

	/**
	 * creates a new DealWithLogoutCoward turn listener
	 *
	 * @param player player
	 */
	public DealWithLogoutCoward(Player player) {
		this.player = player;
	}

	public void onTurnReached(int currentTurn, String message) {
		for (DeathmatchInfo deathmatchInfo : DeathmatchInfo.getDeathmatches()) {
			if (deathmatchInfo.getArena().contains(player)) {
				StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone("0_semos_mountain_n2_w");
				player.teleport(zone, 104, 123, Direction.DOWN, player);
				player
				        .sendPrivateText("You wake up far away from the city in the mountains. But you don't know what happened.");
				break;
			}
		}
	}
}
