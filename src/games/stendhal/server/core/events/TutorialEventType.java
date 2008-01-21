package games.stendhal.server.core.events;

/**
 * Event types used in the tutorial.
 * 
 * @author hendrik
 */
public enum TutorialEventType {

	FIRST_LOGIN(
			"Hi, welcome to Stendhal. You can move around using the arrow keys."),
	FIRST_MOVE(
			"You can talk to Hayunn Naratha by saying \"hi\"."),
	VISIT_SEMOS_CITY(
			"You can get a map of Semos from Monogenes. Start by saying \"hi\". Or you can go down the steps to the dungeons and fight some creatures."),
	VISIT_SEMOS_DUNGEON(
			"You can attack creatures by right clicking on them and choosing \"Attack\""),
	VISIT_SEMOS_DUNGEON_2(
			"Be careful. If you walk deeper and deeper, the creatures will get more powerful."),
	FIRST_ATTACKED(
			"Did you notice that creature walking towards you with the yellow square? It is attacking you."),
	FIRST_KILL(
			"Double click on the Corpse and drag the items with the mouse over your bag and drop them there."),
	FIRST_POISONED(
			"You've just been poisoned. If you didn't drink poison, it was probably a poisonous creature attacking you. Kill poisonous creatures quickly, as you lose more HP each time you are poisoned."),
	FIRST_PLAYER(
			"Have you noticed that this name is printed in white? It is another real human player."),
	FIRST_DEATH(
			"Oh, you have just died. But fortunately death is not permanent in this world."),
	TIMED_HELP(
			"There is a manual with many pictures on the first screen before you login."),
	TIMED_NAKED(
			"Oh, aren't you feeling cold? Right click on yourself and choose \"Set Outfit\" to get dressed."),
	TIMED_PASSWORD(
			"Remember to keep your password completely secret, never tell it to another friend, player, or even admin.");

	private String message;

	/**
	 * Creates a new TutorialEventType.
	 * 
	 * @param message
	 *            human readable message
	 */
	private TutorialEventType(String message) {
		this.message = message;
	}

	/**
	 * Gets the descriptive message.
	 * 
	 * @return message
	 */
	String getMessage() {
		return message;
	}
}
