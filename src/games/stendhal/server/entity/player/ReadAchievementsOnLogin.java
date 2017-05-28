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
package games.stendhal.server.entity.player;

import java.util.Set;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.ReadAchievementsForPlayerCommand;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

public class ReadAchievementsOnLogin implements LoginListener, TurnListener {

	private ResultHandle handle = new ResultHandle();

	@Override
	public void onLoggedIn(Player player) {
		DBCommand command = new ReadAchievementsForPlayerCommand(player);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(1, new TurnListenerDecorator(this));
	}

	@Override
	public void onTurnReached(int currentTurn) {
		ReadAchievementsForPlayerCommand command = DBCommandQueue.get().getOneResult(ReadAchievementsForPlayerCommand.class, handle);
		if (command == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}
		Player p = command.getPlayer();
		Set<String> identifiers = command.getIdentifiers();
		p.initReachedAchievements();
		for (String identifier : identifiers) {
			p.addReachedAchievement(identifier);
		}
		SingletonRepository.getAchievementNotifier().onLogin(command.getPlayer());
	}

}
