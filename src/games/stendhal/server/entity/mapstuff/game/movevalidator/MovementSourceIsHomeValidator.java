package games.stendhal.server.entity.mapstuff.game.movevalidator;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.player.Player;

/**
 * Was this item pulled from its home place?
 *
 * @author hendrik
 */
public class MovementSourceIsHomeValidator implements MoveValidator {

	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if (!token.wasMovedFromHomeInLastMove()) {
			player.sendPrivateText("You can only move tokens that are on the pile outside the game board.");
			return false;
		}
		return true;
	}

}
