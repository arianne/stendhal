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
package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasKilledNumberOfCreaturesCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for fighting achievements
 *  
 * @author madmetzger
 */
public class FightingAchievementFactory extends AchievementFactory {
	
	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> fightingAchievements = new LinkedList<Achievement>();
		fightingAchievements.add(createAchievement("fight.general.rats", "Rat Hunter", "Kill 15 rats", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition("rat", 15)));
		fightingAchievements.add(createAchievement("fight.general.exterminator", "Exterminator", "Kill 10 rats of each kind", Achievement.MEDIUM_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "rat", "caverat", "venomrat", "zombie rat", "venom rat", "giantrat", "ratman", "ratwoman", "archrat")));
		fightingAchievements.add(createAchievement("fight.general.deer", "Deer Hunter", "Kill 25 deer", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition("deer", 25)));
		fightingAchievements.add(createAchievement("fight.general.boars", "Boar Hunter", "Kill 20 boar", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition("boar", 20)));
		fightingAchievements.add(createAchievement("fight.general.bears", "Bear Hunter", "Kill 10 black bears, 10 bears and 10 babybears", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "bear", "black bear", "baby bear")));
		fightingAchievements.add(createAchievement("fight.general.foxes", "Fox Hunter", "Kill 20 foxes", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition("fox", 20)));
		fightingAchievements.add(createAchievement("fight.general.safari", "Safari", "Kill 30 tigers, 30 lions and 50 elephants", Achievement.EASY_BASE_SCORE,
													new AndCondition(
															new PlayerHasKilledNumberOfCreaturesCondition("tiger", 30),
															new PlayerHasKilledNumberOfCreaturesCondition("lion", 30),
															new PlayerHasKilledNumberOfCreaturesCondition("elephant", 50)
															)));
		fightingAchievements.add(createAchievement("fight.general.ents", "Wood Cutter", "Kill 10 ents, 10 entwifes and 10 old ents", Achievement.MEDIUM_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "ent", "entwife", "old ent")));
		return fightingAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.FIGHTING;
	}

}
