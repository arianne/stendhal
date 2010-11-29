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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.core.engine.dbcommand.ReadHallOfFamePointsCommand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * Check if a player has a minimum hall of fame score for a given fametype
 * 
 * @author madmetzger
 */
public class PlayerHallOfFameScoreGreaterThanCondition implements ChatCondition {
	
	private final String fametype;
	
	private final int score;

	private ResultHandle handle;
	
	/**
	 * Create a new PlayerHallOfFameScoreGreaterThanCondition
	 * 
	 * @param fametype
	 * @param score
	 */
	public PlayerHallOfFameScoreGreaterThanCondition(String fametype, int score) {
		this.fametype = fametype;
		this.score = score;
	}

	public boolean fire(Player player, Sentence sentence, Entity npc) {
		handle = new ResultHandle();
		DBCommandQueue.get().enqueueAndAwaitResult(new ReadHallOfFamePointsCommand(player.getName(), fametype), handle);
		ReadHallOfFamePointsCommand command = null;
		while(command == null) {
			command = DBCommandQueue.get().getOneResult(ReadHallOfFamePointsCommand.class, handle);
		}
		int result = command.getPoints();
		return result > score;
	}

}
