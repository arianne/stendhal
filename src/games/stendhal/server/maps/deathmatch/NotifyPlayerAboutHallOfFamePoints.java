/***************************************************************************
 *                    (C) Copyright 2007-2010 - Stendhal                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
 */
package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.dbcommand.ReadHallOfFamePointsCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.SpeakerNPC;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * tells the player about his score.
 *
 * @author hendrik
 */
class NotifyPlayerAboutHallOfFamePoints implements TurnListener {
	private SpeakerNPC npc;
	private String playerName;
	private ResultHandle handle;
	

	/**
	 * creates a new NotifyPlayerAboutHallOfFamePoints turn listener
	 *
	 * @param npc NPC to talk
	 * @param playerName name of player
	 * @param fametype type of fame
	 */
	public NotifyPlayerAboutHallOfFamePoints(SpeakerNPC npc, String playerName, String fametype) {
		this.npc = npc;
		this.playerName = playerName;
		this.handle = new ResultHandle();
		DBCommand command = new ReadHallOfFamePointsCommand(playerName, fametype);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
	}



	public void onTurnReached(int currentTurn) {
		// if there is no result, wait some more
		ReadHallOfFamePointsCommand command = DBCommandQueue.get().getOneResult(ReadHallOfFamePointsCommand.class, handle);
		if (command == null) {
			TurnNotifier.get().notifyInTurns(0, this);
			return;
		}

		// tell the player his score
		int points = command.getPoints();
		npc.say("Congratulations " + playerName + ", your score is now " + points + ".");
	}
}
