package games.stendhal.server.entity.mapstuff.game.movevalidator;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.GameBoard;
import games.stendhal.server.entity.player.Player;

/**
 * Checks that the player is playing his own tokens
 *
 * @author hendrik
 */
public class PlayerIsPlayingRightTokenTypeValidator implements MoveValidator {
  
	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if (!token.getName().equals(board.getCurrentTokenType())) {
			player.sendPrivateText("Hey, you are playing the wrong token.");
			return false;
		}
		return true;
	}

}
