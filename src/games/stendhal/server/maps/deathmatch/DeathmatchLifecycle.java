package games.stendhal.server.maps.deathmatch;


import org.apache.log4j.Logger;

/**
 * life cycle of the deathmatch.
 */
public enum DeathmatchLifecycle {

	/** player asked to bail but the deathmatch was not canceled yet. */
	BAIL("bail"),

	/** all creatures were removed becaused the player has asked to bail before. */
	CANCEL("cancel"),

	/** deathmatch was completed sucessfully and the player got his/her reward. */
	DONE("done"),

	/** deathmatch has been started and is active now. */
	START("start"),

	/** deathmatch was completed sucessfully but the player did not claim "victory" yet. */
	VICTORY("victory");

	private static Logger logger = Logger.getLogger(DeathmatchLifecycle.class);

	private String questString;

	private DeathmatchLifecycle(String questString) {
		this.questString = questString;
	}

	/**
	 * converts to a quest state string.
	 *
	 * @return questState
	 */
	String toQuestString() {
		return questString;
	}

	/**
	 * parses quest state string.
	 *
	 * @param questState quest state string
	 * @return DeathmatchLifecycle
	 */
	static DeathmatchLifecycle getFromQuestStateString(String questState) {
		try {
			return DeathmatchLifecycle.valueOf(questState.toUpperCase());
		} catch (Exception e) {
			logger.error("Unknown DeathmatchLifecycle " + questState);
			return DeathmatchLifecycle.DONE;
		}
	}
}
