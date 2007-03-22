package games.stendhal.server.maps.deathmatch;

/**
 * life cycle of the deathmatch
 */
public enum DeathmatchLifecycle {

	/** deathmatch has just been started */
	START("start");

	

	private String questString = null;

	private DeathmatchLifecycle(String questString) {
		this.questString = questString;
	}

	String toQuestString() {
		return questString;
	}
}
