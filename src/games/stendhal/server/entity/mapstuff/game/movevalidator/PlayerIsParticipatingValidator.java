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
 * Is this player participating in game?
 *
 * @author hendrik
 */
public class PlayerIsParticipatingValidator implements MoveValidator {

	@Override
	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		if (!board.getPlayers().contains(player.getName())) {
			player.sendPrivateText("Your are not participating in this game.");
			return false;
		}
		return true;
	}

}
