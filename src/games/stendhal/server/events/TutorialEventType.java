package games.stendhal.server.events;

/**
 * Event types used in the tutorial
 *
 * @author hendrik
 */
public enum TutorialEventType {
	
	FIRST_LOGIN   ("Hi, welcome to Stendhal. You can move arround using the arrow keys."),
	FIRST_MOVE    ("Please walk south (down) to go out of this building into the city."),
	VISIT_SEMOS_CITY ("You can talk to Monogeneses a little south west (down left) by saying \"hi\". Or you can walk a bit more south to fight some creatures in the dungeons"),
	VISIT_SEMOS_DUNGEON ("You can attack creatures by right clicking on them and choosing \"Attack\""),
	VISIT_SEMOS_DUNGEON_2 ("Be careful. If you walk deeper and deeper, the creatures will get more powerful."),
	FIRST_ATTACKED ("Did you notice that creature walking towards you with the yellow square? It is attacking you."),
	FIRST_KILL ("Double click on the Corpse and drag the items with the mouse over your bag and drop them there."),
	FIRST_PLAYER ("Have you notices that this name is printed in white? It is another real human player"),
	FIRST_DEATH ("Oh, you have just died. But fortunatally death is not permanent in this world.");
	
	private String message = null;

	/**
	 * create a new TutorialEventType
	 * @param message human readable message
	 */
	private TutorialEventType(String message) {
		this.message = message;
	}

	/**
	 * get the descriptive message
	 * @return message
	 */
	String getMessage() {
		return message;
	}
}
