package games.stendhal.server.entity.mapstuff.game.movevalidator;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.player.Player;

/**
 * Is the movement target on board?
 *
 * @author hendrik
 */
public class MovementTargetIsOnBoardValidator implements MoveValidator {

	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if ((xIndex < 0) || (yIndex < 0)) {
			player.sendPrivateText("Please drop the token onto the game board.");
			return false;
		}
		return true;
	}

}
