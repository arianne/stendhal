package games.stendhal.server.entity.mapstuff.game.movevalidator;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.player.Player;

/**
 * checks that the game is currently in progress
 *
 * @author hendrik
 */
public class GameIsActiveValidator implements MoveValidator {

	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if (!board.isGameActive()) {
			player.sendPrivateText("Please start the game first.");
			return false;
		}
		return true;
	}

}
