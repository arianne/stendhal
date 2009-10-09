package games.stendhal.server.entity.mapstuff.game.movevalidator;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.player.Player;

/**
 * validates token moves
 *
 * @author hendrik
 */
public interface MoveValidator {

	/**
	 * validates a move
	 *
	 * @param board the game board
	 * @param player player doing the move
	 * @param token the token which was moved
	 * @param xIndex target x-index
	 * @param yIndex target y-index
	 * @return true, if the move is valid, false otherwise
	 */
	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex);
}
