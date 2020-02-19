/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.server.core.engine.dbcommand.DeletePendingAchievementDetailsCommand;
import games.stendhal.server.core.engine.dbcommand.ReadPendingAchievementDetailsCommand;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * Retrieves pending or partial achievement information from the database on login
 * Updates the player object if necessary
 * Deletes pending achievements from database so that they are not reapplied next login
 *
 * @author kymara
 */
public class UpdatePendingAchievementsOnLogin implements LoginListener, TurnListener {

	private ResultHandle handle = new ResultHandle();

	@Override
	public void onLoggedIn(Player player) {
		DBCommand command = new ReadPendingAchievementDetailsCommand(player);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(1, new TurnListenerDecorator(this));
	}

	@Override
	public void onTurnReached(int currentTurn) {
		ReadPendingAchievementDetailsCommand command = DBCommandQueue.get().getOneResult(ReadPendingAchievementDetailsCommand.class, handle);

		if (command == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}
		Player player = command.getPlayer();

		final AchievementNotifier notifier = AchievementNotifier.get();
		notifier.onLevelChange(player);
		notifier.onKill(player);
		notifier.onFinishQuest(player);
		notifier.onZoneEnter(player);
		notifier.onAge(player);
		notifier.onItemLoot(player);
		notifier.onProduction(player);
		notifier.onObtain(player);

		// delete the entries. We don't need feedback
		DBCommand deletecommand = new DeletePendingAchievementDetailsCommand(player);
		DBCommandQueue.get().enqueue(deletecommand);
	}
}
