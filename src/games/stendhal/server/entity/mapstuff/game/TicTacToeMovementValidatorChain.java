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
package games.stendhal.server.entity.mapstuff.game;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.game.movevalidator.GameIsActiveValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.MoveValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.MovementSourceIsHomeValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.MovementTargetEmptyValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.MovementTargetIsOnBoardValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.PlayerIsParticipatingValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.PlayerIsPlayingRightTokenTypeValidator;
import games.stendhal.server.entity.mapstuff.game.movevalidator.PlayersTurnValidator;
import games.stendhal.server.entity.player.Player;

/**
 * a validator chain for the Tick Tack Toe game.
 *
 * @author hendrik
 */
public class TicTacToeMovementValidatorChain implements MoveValidator {
	private List<MoveValidator> validators = new LinkedList<MoveValidator>();

	/**
	 * creates a new TickTackToeMovementValidatorChain.
	 */
	public TicTacToeMovementValidatorChain() {
		validators.add(new GameIsActiveValidator());
		validators.add(new PlayerIsParticipatingValidator());
		validators.add(new PlayersTurnValidator());
		validators.add(new PlayerIsPlayingRightTokenTypeValidator());
		validators.add(new MovementSourceIsHomeValidator());
		validators.add(new MovementTargetIsOnBoardValidator());
		validators.add(new MovementTargetEmptyValidator());
	}

	@Override
	public boolean validate(GameBoard board, Player player, BoardToken token, int xIndex, int yIndex) {
		for (MoveValidator validator : validators) {
			if (!validator.validate(board, player, token, xIndex, yIndex)) {
				return false;
			}
		}
		return true;
	}

}
