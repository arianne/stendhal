package games.stendhal.server;

/**
 * Other classes can register here to be notified sometime in the future.
 *
 * @author hendrik
 */
public class TurnNotifier {
	private static TurnNotifier turnNotifier = null;
	private int currentTurn = -1;

	private TurnNotifier() {
		// signleton
	}

	/**
	 * Return the TurnNotifier
	 *
	 * @return TurnNotifier
	 */
	public TurnNotifier get() {
		if (turnNotifier == null) {
			turnNotifier = new TurnNotifier();
		}
		return turnNotifier;
	}

	/**
	 * This method is invoked by endTurn()
	 *
	 * @param currentTurn currentTurn
	 */
	public void logic(int currentTurn) {
		this.currentTurn = currentTurn;
	}

}
