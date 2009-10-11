package games.stendhal.server.entity.mapstuff.game.movevalidator;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.player.Player;

/**
 * Is this it this player's turn in game?
 *
 * @author hendrik
 */
public class PlayersTurnValidator implements MoveValidator {

	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if (!player.getName().equals(board.getCurrentPlayer())) {
			player.sendPrivateText("It is not your turn. Please wait for your opponent to complete his or her move.");
			return false;
		}
		return true;
	}

}
