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
	RETURN_GUARDHOUSE(
			"Talk to Hayunn Naratha again by saying \"hi\"."),
	VISIT_SEMOS_CITY(
			"You can get a map of Semos from Monogenes. Start by saying \"hi\". Or you can go down the steps to the dungeons and fight some creatures."),
	VISIT_SEMOS_DUNGEON(
			"You can attack creatures by right clicking on them and choosing \"Attack\""),
	VISIT_SEMOS_DUNGEON_2(
			"Be careful. If you walk deeper and deeper, the creatures will get more powerful."),
	VISIT_SEMOS_TAVERN(
			"You can trade with an NPC by saying \"hi\" then asking for their \"offer\". If you want to buy something, perhaps some cheese, say \"buy cheese\"."),
	FIRST_ATTACKED(
			"Did you notice that creature walking towards you with the yellow square? It is attacking you."),
	FIRST_KILL(
			"Double click on the Corpse and drag the items with the mouse over your bag and drop them there."),
    FIRST_PLAYER_KILL(
			   "You have been marked with the red skull of a player killer. You may find that people are wary of you now. To get it removed, you may speak to Io Flotto in Semos temple."),
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
				   "Remember to keep your password completely secret, never tell it to another friend, player, or even admin."),
	TIMED_OUTFIT(
				   "Do you like your outfit? If not, you can change it. Right click on yourself and choose \"Set Outfit\" to experiment with new hair, face, clothes and body."),
	TIMED_RULES(
				    "Thank you for continuing to play Stendhal. Now that you have played for some time, it's important that you read the rules at #http://stendhal.game-host.org/wiki/index.php/StendhalRuleSystem"),
	NEW_RELEASE77(
			"Wedding rings have changed for this release. Marriage is a role playing feature so that you can marry a friend in the game. Unfortunately many people have married clones who are not real players - just a tool for free teleport and they remain low level. How to solve this unfair use of wedding rings? After careful consideration we have implemented a time delay between wedding ring use. The delay depends on the level ratio between you and your spouse; those with really low level clones will be able to get a free teleport much less often. For genuine couples who are close in level the delay can be as little as 5 minutes before you can use the ring again. Remember when we add new features: if you abuse it, you lose it.");
	private String message;

	/**
	 * Creates a new TutorialEventType.
	 * 
	 * @param message
	 *            human readable message
	 */
	private TutorialEventType(final String message) {
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
