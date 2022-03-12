/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

	public static final String ID_RATS = "fight.general.rats";
	public static final String ID_EXTERMINATOR = "fight.general.exterminator";
	public static final String ID_DEER = "fight.general.deer";
	public static final String ID_BOARS = "fight.general.boars";
	public static final String ID_BEARS = "fight.general.bears";
	public static final String ID_FOXES = "fight.general.foxes";
	public static final String ID_SAFARI = "fight.general.safari";
	public static final String ID_ENTS = "fight.general.ents";
	public static final String ID_POACHER = "fight.special.rare";
	public static final String ID_LEGEND = "fight.special.all";
	public static final String ID_TEAM_PLAYER = "fight.special.allshared";
	public static final String ID_GIANTS = "fight.solo.giant";
	public static final String ID_ANGELS = "fight.general.angels";
	public static final String ID_WEREWOLF = "fight.general.werewolf";
	public static final String ID_MERMAIDS = "fight.general.mermaids";
	public static final String ID_DEEPSEA = "fight.general.deepsea";
	public static final String ID_ZOMBIES = "fight.general.zombies";
	public static final String ID_FOWL = "fight.general.fowl";
	public static final String ID_PACHYDERM = "fight.general.pachyderm";

	public static final String[] ENEMIES_EXTERMINATOR = {
		"rat", "caverat", "razorrat", "venomrat", "zombie rat", "giantrat",
		"ratman", "ratwoman", "archrat"
	};

	public static final String[] ENEMIES_BEARS = {
		"bear", "black bear", "babybear"
	};

	// enemies required for David vs. Goliath
	public static final String[] ENEMIES_GIANTS = {
		"giant", "elder giant", "amazoness giant", "master giant", "black giant",
		"imperial general giant", "kasarkutominubat", "giant kobold", "giant dwarf",
		"Dhohr Nuggetcutter", "Lord Durin", "ice giant"
	};

	// enemies required for Heavenly Wrath
	public static final String[] ENEMIES_ANGELS = {
		"angel", "archangel", "dark angel", "dark archangel", "fallen angel",
		"baby angel"
	};

	// enemies required for Serenade the Siren
	public static final String[] ENEMIES_MERMAIDS = {
		"amethyst mermaid", "emerald mermaid", "ruby mermaid", "sapphire mermaid"
	};

	// enemies required for Deep Sea Fisherman
	public static final String[] ENEMIES_DEEPSEA = {
		"shark", "kraken", "neo kraken"
	};

	// enemies required for Zombie Apocalypse
	public static final String[] ENEMIES_ZOMBIES = {
		"zombie", "bloody zombie", "headless monster", "rotten zombie"
	};

	// enemies required for Pachyderm Mayhem
	public static final String[] ENEMIES_PACHYDERM = {
		"elephant", "bull elephant", "musth elephant", "woolly mammoth"
	};

	// enemies required for Chicken Nuggets
	public static final String[] ENEMIES_FOWL = {
		"chick", "chicken", "dodo", "mother hen", "penguin", "pigeon"
	};


	@Override
	protected Category getCategory() {
		return Category.FIGHTING;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			ID_RATS, "Rat Hunter",
			"Kill 15 rats",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition("rat", 15)));

		achievements.add(createAchievement(
			ID_EXTERMINATOR, "Exterminator",
			"Kill 10 rats of each kind",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(10, ENEMIES_EXTERMINATOR)));

		achievements.add(createAchievement(
			ID_DEER, "Deer Hunter",
			"Kill 25 deer",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition("deer", 25)));

		achievements.add(createAchievement(
			ID_BOARS, "Boar Hunter",
			"Kill 20 boar",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition("boar", 20)));

		achievements.add(createAchievement(
			ID_BEARS, "Bear Hunter",
			"Kill 10 black bears, 10 bears and 10 babybears",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(10, ENEMIES_BEARS)));

		achievements.add(createAchievement(
			ID_FOXES, "Fox Hunter",
			"Kill 20 foxes",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition("fox", 20)));

		achievements.add(createAchievement(
			ID_SAFARI, "Safari",
			"Kill 30 tigers, 30 lions and 50 elephants",
			Achievement.EASY_BASE_SCORE, true,
			new AndCondition(
				new PlayerHasKilledNumberOfCreaturesCondition("tiger", 30),
				new PlayerHasKilledNumberOfCreaturesCondition("lion", 30),
				new PlayerHasKilledNumberOfCreaturesCondition("elephant", 50))));

		achievements.add(createAchievement(
			ID_ENTS, "Wood Cutter",
			"Kill 10 ents, 10 entwives and 10 old ents",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(10, "ent", "entwife", "old ent")));

		achievements.add(createAchievement(
			ID_POACHER, "Poacher",
			"Kill any rare creature",
			Achievement.HARD_BASE_SCORE, true,
			new KilledRareCreatureCondition()));

		achievements.add(createAchievement(
			ID_LEGEND, "Legend",
			"Kill all creatures solo",
			Achievement.HARD_BASE_SCORE, true,
			new KilledSoloAllCreaturesCondition()));

		achievements.add(createAchievement(
			ID_TEAM_PLAYER, "Team Player",
			"Kill all creatures in a team",
			Achievement.HARD_BASE_SCORE, true,
			new KilledSharedAllCreaturesCondition()));

		achievements.add(createAchievement(
			ID_GIANTS, "David vs. Goliath",
			"Kill 20 of each type of giant solo",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(20, KillType.SOLO, ENEMIES_GIANTS)));

		achievements.add(createAchievement(
			ID_ANGELS, "Heavenly Wrath",
			"Kill 100 of each type of angel",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(100, ENEMIES_ANGELS)));

		achievements.add(createAchievement(
			ID_WEREWOLF, "Silver Bullet",
			"Kill 500 werewolves",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(500, "werewolf")));

		achievements.add(createAchievement(
			ID_MERMAIDS, "Serenade the Siren",
			"Kill 10,000 gem mermaids",
			Achievement.HARD_BASE_SCORE, true,
			new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
					int kills = 0;
					for (final String mermaid: ENEMIES_MERMAIDS) {
						kills += player.getSoloKill(mermaid) + player.getSharedKill(mermaid);
					}

					return kills >= 10000;
				}
			}));

		achievements.add(createAchievement(
			ID_DEEPSEA, "Deep Sea Fisherman",
			"Kill 500 sharks, 500 kraken and 500 neo kraken",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(500, ENEMIES_DEEPSEA)));

		achievements.add(createAchievement(
			ID_ZOMBIES, "Zombie Apocalypse",
			"Kill 500 zombies",
			Achievement.EASY_BASE_SCORE, true,
			new ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, Entity npc) {
					int kills = 0;
					for (final String zombie: ENEMIES_ZOMBIES) {
						kills += player.getSoloKill(zombie) + player.getSharedKill(zombie);
					}

					return kills >= 500;
				}
			}));

		achievements.add(createAchievement(
			ID_FOWL, "Chicken Nuggets",
			"Kill 100 of each type of fowl",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(100, ENEMIES_FOWL)));

		achievements.add(createAchievement(
			ID_PACHYDERM, "Pachyderm Mayhem",
			"Kill 100 of each type of pachyderm",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerHasKilledNumberOfCreaturesCondition(100, ENEMIES_PACHYDERM)));

		return achievements;
	}
}
