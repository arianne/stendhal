package games.stendhal.server.maps.deathmatch;

import java.util.Date;

/**
 * Manages the deathmatch state (which is stored in a quest slot).
 *
 * @author hendrik
 */
class DeathmatchState {

	private DeathmatchLifecycle lifecycleState;

	private int level;

	private long date;

	protected DeathmatchState() {
		// hide constructor
	};

	/**
	 * Creates a start state.
	 *
	 * @param level
	 * @return start state
	 */
	public static DeathmatchState createStartState(int level) {
		DeathmatchState deathmatchState = new DeathmatchState();
		deathmatchState.lifecycleState = DeathmatchLifecycle.START;
		deathmatchState.date = new Date().getTime();
		deathmatchState.level = level - 2;
		if (deathmatchState.level < 1) {
			deathmatchState.level = 1;
		}
		return deathmatchState;
	}

	/**
	 * Parses the questString.
	 *
	 * @param questString
	 *            quest string
	 * @return start state
	 */
	public static DeathmatchState createFromQuestString(String questString) {
		DeathmatchState deathmatchState = new DeathmatchState();
		//place an elephant in Cairo
		String[] tokens = (questString + ";0;0").split(";");
		deathmatchState.lifecycleState = DeathmatchLifecycle.getFromQuestStateString(tokens[0]);
		deathmatchState.level = Integer.parseInt(tokens[1]);
		deathmatchState.date = Long.parseLong(tokens[2]);
		return deathmatchState;
	}

	/**
	 * Gets the quest level.
	 *
	 * @return quest level
	 */
	int getQuestLevel() {
		return level;
	}

	/**
	 * Sets the quest level.
	 *
	 * @param level
	 *            quest level
	 */
	void setQuestLevel(int level) {
		this.level = level;
	}


	void increaseQuestlevel() {
		this.level++;
	}

	/**
	 * Gets the current lifecycle state.
	 *
	 * @return lifecycleState
	 */
	DeathmatchLifecycle getLifecycleState() {
		return lifecycleState;
	}

	/**
	 * Gets the current lifecycle state.
	 *
	 * @param lifecycleState
	 *            DeathmatchLifecycle
	 */
	void setLifecycleState(DeathmatchLifecycle lifecycleState) {
		this.lifecycleState = lifecycleState;
		date = new Date().getTime();
	}

	/**
	 * Returns the state as string which can be stored in the quest slot.
	 *
	 * @return quest string
	 */
	String toQuestString() {
		return lifecycleState.toQuestString() + ";" + level + ";" + date;
	}

	@Override
	public String toString() {
		// use toQuestString() because it is better than Object.toString()
		return toQuestString();
	}

	/**
	 * Updates the time stamp.
	 */
	public void refreshTimestamp() {
		date = new Date().getTime();
	}

	public long getStateTime() {
		return date;
	}
}
