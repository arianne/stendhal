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

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

public class PlayerHasCompletedAchievementsCondition implements ChatCondition {
	
	private final List<String> achievements;
	
	private final int minimumToComplete;
	
	public PlayerHasCompletedAchievementsCondition(int minimum, String... achievmentIdentifiers) {
		achievements = Arrays.asList(achievmentIdentifiers);
		minimumToComplete = minimum;
	}

	public PlayerHasCompletedAchievementsCondition(String... achievementIdentifiers) {
		this(achievementIdentifiers.length, achievementIdentifiers);
	}
	
	public boolean fire(Player player, Sentence sentence, Entity npc) {
		int reached = 0;
		for (String achievementIdentifier : achievements) {
			if(player.hasReachedAchievement(achievementIdentifier)) {
				reached = reached + 1;
			}
			if(reached >= minimumToComplete) {
				return true;
			}
		}
		return false;
	}

}
