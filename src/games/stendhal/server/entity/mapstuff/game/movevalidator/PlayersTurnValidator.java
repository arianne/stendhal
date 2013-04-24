/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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

	@Override
	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if (!player.getName().equals(board.getCurrentPlayer())) {
			player.sendPrivateText("It is not your turn. Please wait for your opponent to complete his or her move.");
			return false;
		}
		return true;
	}

}
