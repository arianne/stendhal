/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.pvp;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.player.Player;

public class PlayerVsPlayerChallengeCreatorTurnListener implements TurnListener {

	private final Player challenger;
	private final Player challenged;

	public PlayerVsPlayerChallengeCreatorTurnListener(Player challenger, Player challenged) {
		this.challenger = challenger;
		this.challenged = challenged;
	}

	@Override
	public void onTurnReached(int currentTurn) {
		SingletonRepository.getChallengeManager().createChallenge(challenger, challenged, currentTurn);
	}
}
