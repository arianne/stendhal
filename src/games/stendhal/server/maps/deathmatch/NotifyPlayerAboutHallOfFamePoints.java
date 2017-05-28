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

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.dbcommand.ReadHallOfFamePointsCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.rp.achievement.AchievementNotifier;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
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
	private String questSlot;


	/**
	 * creates a new NotifyPlayerAboutHallOfFamePoints turn listener
	 *
	 * @param npc NPC to talk
	 * @param playerName name of player
	 * @param fametype type of fame
	 * @param questSlot name of a slot to store the points to
	 */
	public NotifyPlayerAboutHallOfFamePoints(SpeakerNPC npc, String playerName, String fametype, String questSlot) {
		this.npc = npc;
		this.playerName = playerName;
		this.handle = new ResultHandle();
		this.questSlot = questSlot;
		DBCommand command = new ReadHallOfFamePointsCommand(playerName, fametype);
		DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
	}



	@Override
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

		// save the progress to a quest slot
		if (questSlot != null) {
			Player player = StendhalRPRuleProcessor.get().getPlayer(playerName);
			if (player != null) {
				player.setQuest(questSlot, Integer.toString(points));
				AchievementNotifier.get().onFinishQuest(player);
			}
		}
	}
}
