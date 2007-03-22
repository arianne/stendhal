package games.stendhal.server.maps.deathmatch;

import java.util.Date;

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
	 * @param level
	 * @return start state
	 */
	public static DeathmatchState createStartState(int level) {
		DeathmatchState deathmatchState = new DeathmatchState();
		deathmatchState.lifecycleState = DeathmatchLifecycle.START;
		deathmatchState.date = new Date().getTime();
		deathmatchState.level = level - 2;
		if(deathmatchState.level < 1) {
			deathmatchState.level = 1;
		}
		return deathmatchState;
	}

	/**
	 * parses the questString
	 *
	 * @param questString quest string
	 * @return start state
	 */
	public static DeathmatchState createFromQuestString(String questString) {
		DeathmatchState deathmatchState = new DeathmatchState();
		String[] tokens = (questString+";0;0").split(";");
		deathmatchState.lifecycleState = DeathmatchLifecycle.getFromQuestStateString(tokens[0]);
		deathmatchState.level = Integer.parseInt(tokens[1]);
		deathmatchState.date = Long.parseLong(tokens[2]);
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
