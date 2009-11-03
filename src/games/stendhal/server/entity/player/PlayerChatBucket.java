package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.util.IntRingBuffer;

/**
 * a bucket to manage chat traffic shaping.
 *
 * @author hendrik
 */
public class PlayerChatBucket {
	private static final int TIMEFRAME_IN_TURNS = 10000 / 300;

	private IntRingBuffer lastChatTurns = new IntRingBuffer(5);

	public boolean checkAndAdd() {
		int turn = SingletonRepository.getRuleProcessor().getTurn();
		boolean res = check(turn);
		if (res) {
			add(turn);
		}
		return res;
	}

	/**
	 * checks if this player is allowed to chat
	 *
	 * @param turn currentTurn
	 * @return true, if the player is allowed to chat, false otherwise.
	 */
	private boolean check(int turn) {
		lastChatTurns.removeSmaller(turn - TIMEFRAME_IN_TURNS);
		return !lastChatTurns.isFull();
	}

	/**
	 * adds the current turn to the ring buffer
	 *
	 * @param turn currentTurn
	 */
	private void add(int turn) {
		lastChatTurns.add(turn);
	}
}
