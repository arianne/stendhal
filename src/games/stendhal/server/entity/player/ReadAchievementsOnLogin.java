package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.dbcommand.ReadAchievementsForPlayerCommand;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;

import java.util.Set;

import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

public class ReadAchievementsOnLogin implements LoginListener, TurnListener {
	
	private ResultHandle handle = new ResultHandle();

	public void onLoggedIn(Player player) {
		DBCommand command = new ReadAchievementsForPlayerCommand(player);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
		TurnNotifier.get().notifyInTurns(1, this);
	}

	public void onTurnReached(int currentTurn) {
		ReadAchievementsForPlayerCommand command = DBCommandQueue.get().getOneResult(ReadAchievementsForPlayerCommand.class, handle);
		if (command == null) {
			TurnNotifier.get().notifyInTurns(0, this);
			return;
		}
		Player p = command.getPlayer();
		Set<String> identifiers = command.getIdentifiers();
		for (String identifier : identifiers) {
			p.addReachedAchievement(identifier);
		}
	}

}
