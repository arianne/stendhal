package games.stendhal.server.maps.deathmatch;

import java.util.Date;

import games.stendhal.server.entity.player.Player;

/**
 * manages the deathmatch state (which is store in a quest slot)
 *
 * @author hendrik
 */
public class DeathmatchState {

	private DeathmatchLifecycle lifecycleState = null;
	private int level = 0;
	private long date = 0;

	/**
	 * creates a start state
	 *
	 * @param player Player
	 * @return start state
	 */
	public static DeathmatchState createStartState(Player player) {
		DeathmatchState deathmatchState = new DeathmatchState();
		deathmatchState.lifecycleState = DeathmatchLifecycle.START;
		deathmatchState.date = new Date().getTime();
		int level = player.getLevel() - 2;
		if(level < 1) {
			level = 1;
		}
		deathmatchState.level = level;
		return deathmatchState;
	}

	/**
	 * return the state as string which can be stored in the quest slot
	 *
	 * @return quest string
	 */
	public String toQuestString() {
		return lifecycleState.toQuestString() + ";" + level + ";" + date;
	}

	@Override
	public String toString() {
		// use toQuestString() because it is better than Object.toString()
		return toQuestString();
	}
}
