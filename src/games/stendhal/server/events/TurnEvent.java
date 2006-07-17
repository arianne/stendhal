package games.stendhal.server.events;

/**
 * Implementing classes can be notified that a turn number
 * has been reached.
 *
 * @author hendrik
 */
public interface TurnEvent {

	/**
	 * This method is called when the turn number is reached
	 *
	 * @param currentTurn current turn number
	 */
	public void onTurnReached(int currentTurn);
}
