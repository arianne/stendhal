package games.stendhal.server.events;

/**
 * Event types used in the tutorial
 *
 * @author hendrik
 */
public enum TutorialEventType {
	
	FIRST_LOGIN   ("Hi, welcome to Stendhal. You can move arround using the arrow keys."),
	FIRST_MOVE    ("Please walk south (down) to go out of this building into the city."),
	VISIT_SEMOS_CITY ("You should talk to Monogeneses by saying \"hi\". He has a white beard and is south west (down left) of here.");
	
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
