package games.stendhal.server.maps.deathmatch;

/**
 * life cycle of the deathmatch
 */
public enum DeathmatchLifecycle {

	/** player asked to bail but the deathmatch was not canceled yet */
	BAIL("bail"),

	/** all creatures were removed becaused the player has asked to bail before */
	CANCEL("cancel"),

	/** deathmatch was completed sucessfully and the player got his/her reward */
	DONE("done"),

	/** deathmatch has been started and is active now */
	START("start"),

	/** deathmatch was completed sucessfully but the player did not claim "victory" yet */
	VICTORY("victory");

	private String questString = null;

	private DeathmatchLifecycle(String questString) {
		this.questString = questString;
	}

	String toQuestString() {
		return questString;
	}
}
