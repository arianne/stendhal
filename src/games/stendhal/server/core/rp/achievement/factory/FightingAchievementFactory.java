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
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.constants.KillType;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.core.rp.achievement.condition.KilledRareCreatureCondition;
import games.stendhal.server.core.rp.achievement.condition.KilledSharedAllCreaturesCondition;
import games.stendhal.server.core.rp.achievement.condition.KilledSoloAllCreaturesCondition;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasKilledNumberOfCreaturesCondition;
import games.stendhal.server.entity.player.Player;
/**
 * Factory for fighting achievements
 *
 * @author madmetzger
 */
public class FightingAchievementFactory extends AbstractAchievementFactory {

	// enemies required for David vs. Goliath
	public static final String[] ENEMIES_GIANTS = {
			"giant", "elder giant", "amazoness giant", "master giant", "black giant",
			"imperial general giant", "kasarkutominubat", "giant kobold", "giant dwarf",
			"Dhohr Nuggetcutter", "Lord Durin"
	};
	public static final String ID_GIANTS = "fight.solo.giant";
	public static final int COUNT_GIANTS = 20;

	// enemies required for Heavenly Wrath
	public static final String[] ENEMIES_ANGELS = {
			"angel", "archangel", "dark angel", "dark archangel", "fallen angel"
	};
	public static final String ID_ANGELS = "fight.general.angels";
	public static final int COUNT_ANGELS = 100;

	public static final String ID_WEREWOLF = "fight.general.werewolf";
	public static final int COUNT_WEREWOLF = 500;

	// enemies required for Serenade the Siren
	public static final String[] ENEMIES_MERMAIDS = {
			"amethyst mermaid", "emerald mermaid", "ruby mermaid", "sapphire mermaid"
	};
	public static final String ID_MERMAIDS = "fight.general.mermaids";
	public static final int COUNT_MERMAIDS = 10000;


	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> fightingAchievements = new LinkedList<Achievement>();
		fightingAchievements.add(createAchievement("fight.general.rats", "Rat Hunter", "Kill 15 rats", Achievement.EASY_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition("rat", 15)));
		fightingAchievements.add(createAchievement("fight.general.exterminator", "Exterminator", "Kill 10 rats of each kind", Achievement.MEDIUM_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "rat", "caverat", "razorrat", "venomrat", "zombie rat", "giantrat", "ratman", "ratwoman", "archrat")));
		fightingAchievements.add(createAchievement("fight.general.deer", "Deer Hunter", "Kill 25 deer", Achievement.EASY_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition("deer", 25)));
		fightingAchievements.add(createAchievement("fight.general.boars", "Boar Hunter", "Kill 20 boar", Achievement.EASY_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition("boar", 20)));
		fightingAchievements.add(createAchievement("fight.general.bears", "Bear Hunter", "Kill 10 black bears, 10 bears and 10 babybears", Achievement.EASY_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "bear", "black bear", "babybear")));
		fightingAchievements.add(createAchievement("fight.general.foxes", "Fox Hunter", "Kill 20 foxes", Achievement.EASY_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition("fox", 20)));
		fightingAchievements.add(createAchievement("fight.general.safari", "Safari", "Kill 30 tigers, 30 lions and 50 elephants", Achievement.EASY_BASE_SCORE, true,
													new AndCondition(
															new PlayerHasKilledNumberOfCreaturesCondition("tiger", 30),
															new PlayerHasKilledNumberOfCreaturesCondition("lion", 30),
															new PlayerHasKilledNumberOfCreaturesCondition("elephant", 50)
															)));
		fightingAchievements.add(createAchievement("fight.general.ents", "Wood Cutter", "Kill 10 ents, 10 entwifes and 10 old ents", Achievement.MEDIUM_BASE_SCORE, true,
													new PlayerHasKilledNumberOfCreaturesCondition(10, "ent", "entwife", "old ent")));
		fightingAchievements.add(createAchievement("fight.special.rare", "Poacher", "Kill any rare creature", Achievement.HARD_BASE_SCORE, true,
				new KilledRareCreatureCondition()));

		fightingAchievements.add(createAchievement("fight.special.all", "Legend", "Kill all creatures solo", Achievement.HARD_BASE_SCORE, true,
				new KilledSoloAllCreaturesCondition()));
		fightingAchievements.add(createAchievement("fight.special.allshared", "Team Player", "Kill all creatures in a team", Achievement.HARD_BASE_SCORE, true,
				new KilledSharedAllCreaturesCondition()));

		fightingAchievements.add(createAchievement(
				ID_GIANTS, "David vs. Goliath", "Kill 20 of each type of giant solo",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerHasKilledNumberOfCreaturesCondition(COUNT_GIANTS, KillType.SOLO, ENEMIES_GIANTS)));

		fightingAchievements.add(createAchievement(
				ID_ANGELS, "Heavenly Wrath", "Kill 100 of each type of angel",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerHasKilledNumberOfCreaturesCondition(COUNT_ANGELS, ENEMIES_ANGELS)));

		fightingAchievements.add(createAchievement(
				ID_WEREWOLF, "Silver Bullet", "Kill 500 werewolves",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerHasKilledNumberOfCreaturesCondition(COUNT_WEREWOLF, "werewolf")));

		fightingAchievements.add(createAchievement(
				ID_MERMAIDS, "Serenade the Siren", "Kill 10,000 gem mermaids",
				Achievement.HARD_BASE_SCORE, true,
				new ChatCondition() {
					@Override
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						int kills = 0;

						for (final String mermaid: ENEMIES_MERMAIDS) {
							kills += player.getSoloKill(mermaid) + player.getSharedKill(mermaid);
						}

						return kills >= COUNT_MERMAIDS;
					}
				}));

		return fightingAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.FIGHTING;
	}

}
