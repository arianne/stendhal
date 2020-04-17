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
package games.stendhal.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.config.CreatureGroupsXMLLoader;
import games.stendhal.server.core.engine.RPClassGenerator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.transformer.PlayerTransformer;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;
import marauroa.common.game.RPObject;

/*
 * Running:
 * 	java -cp $CLASSPATH java -cp games/stendhal/tools/BalanceRPGame
 * 		calculates balanced atk and def values to all creatures up to HIGHEST_LEVEL
 * 		(defined in the code below)
 * 	java -cp $CLASSPATH java -cp games/stendhal/tools/BalanceRPGame creature ...
 * 		calculates the values only for the specified creatures
 *
 * 	CLASSPATH should be (with appropriate changes for OS and versions), assuming
 * 		the compiled .class for this file is under "bin":
 *		.:bin:libs/marauroa.jar:libs/log4j.jar:libs/mysql-connector-java-5.1.5-bin.jar:build/lib/stendhal-stendhal-server-0.71.jar
 */
/** * NOTE: AWFUL CODE FOLLOWS. YOU ARE NOT SUPPOSED TO READ THIS ;P ** */

public class BalanceRPGame {

	// suggested stats output at end of run
	private static final List<String> suggestions = new LinkedList<>();


	/**
	 * A Simple (dumb) optimizer to adjust creature stats.
	 */
	private static class Optimizer {
		Creature creature;

		/**
		 * Create an optimizer for a creature.
		 *
		 * @param creature
		 */
		public Optimizer(final Creature creature) {
			this.creature = creature;
		}

		/**
		 * Adjust creature stats using results from the previous run.
		 *
		 * @param leftHP the mean amount of HP the player had left when the
		 * fights ended
		 * @param rounds the amount of turns the fights took on average
		 */
		public void step(final int leftHP, final int rounds) {
			float stepSize = leftHP / (float) player.getBaseHP();
			stepSize = Math.signum(stepSize) * Math.min(Math.abs(stepSize), 0.5f);

			final int oldAtk = creature.getAtk();
			int newAtk = Math.max(1, Math.round(creature.getAtk()
					+ stepSize * creature.getAtk()));
			// Always ensure trying to get out of dead area
			if ((leftHP < 0) && (newAtk == oldAtk)) {
				newAtk--;
			}

			final int level = creature.getLevel();
			final int oldDef = creature.getDef();
			int newDef = oldDef;
			final double preferred = preferredDuration(level);
			if (!isWithinDurationRange(preferred, rounds)) {
				// Don't grow it the monster is already stronger than the player
				if ((leftHP > 0) || (preferred < rounds)) {
				newDef = Math.max(1, (int) (creature.getDef()
						+ preferred - rounds + 0.5));
				}
			} else {
				newDef = 	Math.max(1, (int) (creature.getDef()
					+ 5 * stepSize * creature.getDef() + 0.5f));
			}
			// Don't change too fast
			if (newDef > 1.1 * oldDef) {
				newDef = Math.max((int) (1.1 * oldDef), oldDef + 1);
			} else if (newDef < 0.9 * oldDef) {
				newDef = Math.max(1, Math.min((int) (0.9 * oldDef), oldDef - 1));
			}

			creature.setAtk(newAtk);
			creature.setDef(newDef);
		}
	}

	private static final int ROUNDS = 100;
	private static final int HIGHEST_LEVEL = 500;
	private static final double DEFAULT_DURATION_THRESHOLD = 0.2;
	private static double durationThreshold;
	private static Player player;

	public static void main(final String[] args) throws Exception {
		new RPClassGenerator().createRPClasses();
		final CreatureGroupsXMLLoader loader = new CreatureGroupsXMLLoader("/data/conf/creatures.xml");
		final List<DefaultCreature> creatures = loader.load();

		Collections.sort(creatures, new Comparator<DefaultCreature>() {
			@Override
			public int compare(final DefaultCreature o1, final DefaultCreature o2) {
				return o1.getLevel() - o2.getLevel();
			}
		});

		final int[] atkLevels = new int[HIGHEST_LEVEL + 1];
		final int[] defLevels = new int[HIGHEST_LEVEL + 1];

		for (int level = 0; level < atkLevels.length; level++) {
			// help newbies a bit, so don't start at real stats, but a bit lower
			atkLevels[level] = (int) Math.round(Math.log(level + 4) * 9  - 10);
			defLevels[level] = (int) Math.round(Math.log(level + 4) * 20
					+ level - 26);
		}

		final EntityManager em = SingletonRepository.getEntityManager();

		final Item shield = em.getItem("wooden shield");
		final Item armor = em.getItem("dress");
		final Item helmet = em.getItem("leather helmet");
		final Item legs = em.getItem("leather legs");
		final Item boots = em.getItem("leather boots");

		player = (Player) new PlayerTransformer().transform(new RPObject());
		player.equip("lhand", shield);
		player.equip("rhand", em.getItem("club"));
		player.equip("armor", armor);
		player.equip("head", helmet);
		player.equip("legs", legs);
		player.equip("feet", boots);

		// Setup the list of creatures to balance
		Collection<DefaultCreature> creaturesToBalance;
		if (args.length > 0) {
			final List<String> names = new ArrayList<>();
			names.addAll(Arrays.asList(args));
			creaturesToBalance = new ArrayList<DefaultCreature>();

			for (DefaultCreature creature : creatures) {
				final String creatureName = creature.getCreatureName();
				if (names.contains(creatureName)) {
					creaturesToBalance.add(creature);
					names.removeAll(Collections.singleton(creatureName));
				}
			}

			if (!names.isEmpty()) {
				final StringBuilder sb = new StringBuilder("\nWARNING: Unknown creature(s): ");
				final int unknownCount = names.size();
				for (int idx = 0; idx < unknownCount; idx ++) {
					sb.append(names.get(idx));
					if (idx < unknownCount - 1) {
						sb.append(", ");
					}
				}
				System.out.println(sb.toString() + "\n");
			}
		} else {
			// default to all of them
			creaturesToBalance = creatures;
		}

		for (final DefaultCreature creature : creaturesToBalance) {
			final int level = creature.getLevel();

			if (creature.getLevel() > HIGHEST_LEVEL) {
				continue;
			}

			final Creature target = creature.getCreature();
			final Optimizer optimizer = new Optimizer(target);

			player.setLevel(level);
			player.setBaseHP(100 + 10 * level);
			player.setAtk(atkLevels[level]);
			player.setDef(defLevels[level]);

			equip(player, level);

			System.out.println("\nPlayer(" + level + ") vs "
					+ creature.getCreatureName());

			durationThreshold = DEFAULT_DURATION_THRESHOLD;

			Integer proposedXPValue = null;

			boolean balanced = false;
			int tries = 0;
			while (!balanced) {
				final Pair<Integer, Integer> results = combat(player, target,
						ROUNDS);
				final int meanTurns = results.first();
				final int meanLeftHP = results.second();

				proposedXPValue = (int) ((2 * creature.getLevel() + 1) * (meanTurns / 2.0));
				creature.setLevel(creature.getLevel(), proposedXPValue);

				System.out.println("Target ATK: "
						+ target.getAtk()
							+ "/DEF: "
							+ target.getDef()
							+ "/HP: "
							+ target.getBaseHP()
							+ "\t Turns: "
							+ meanTurns
							+ "\tLeft HP:"
							+ meanLeftHP);

				if (isCorrectResult(level, meanTurns, meanLeftHP / (double) player.getBaseHP())) {
					balanced = true;
				} else {
					optimizer.step(meanLeftHP, meanTurns);

					System.out.println("New ATK: " + target.getAtk()
							+ "/DEF: " + target.getDef() + "/HP: "
							+ target.getBaseHP());
				}

				// relax convergence criteria for pathological cases
				tries++;
				if (tries % 200 == 0) {
					durationThreshold *= 1.1;
					System.out.println(target.getName() + ": changed threshold to " + durationThreshold);
				}
			}

			boolean changed = false;

			if (creature.getAtk() != target.getAtk()) {
				changed = true;
			}

			if (creature.getDef() != target.getDef()) {
				changed = true;
			}

			if (creature.getHP() != target.getBaseHP()) {
				changed = true;
			}

			System.out.println(creature.getCreatureName() + " done!");

			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(creature.getCreatureName());
			stringBuilder.append(" (level ");
			stringBuilder.append(creature.getLevel());
			stringBuilder.append("):");
			if (changed) {
				stringBuilder.append(" *\t");
			} else {
				stringBuilder.append("  \t");
			}
			stringBuilder.append("ATK: ");
			stringBuilder.append(target.getAtk());
			stringBuilder.append("\t\tDEF: ");
			stringBuilder.append(target.getDef());
			stringBuilder.append("\t\tHP: ");
			stringBuilder.append(target.getBaseHP());

			if (System.getProperty("showxp") != null && proposedXPValue != null) {
				stringBuilder.append("\t\tXP: " + proposedXPValue);
			}

			suggestions.add(stringBuilder.toString());
		}

		if (suggestions.isEmpty()) {
			System.out.println("\nNo suggestions available\n");
		} else {
			System.out.println("\nSuggested values:");
			for (final String s: suggestions) {
				System.out.println("\t" + s);
			}
			System.out.println();
		}

		// FIXME: why does balancer not exit automatically?
	}

	private static Pair<Integer, Integer> combat(final Player player, final Creature target) {
		target.setHP(target.getBaseHP());
		player.setHP(player.getBaseHP());

		boolean combatFinishedWinPlayer = false;
		int turns = 0;
		int healAmount = 0;
		int healRate = 0;

		String healer = target.getAIProfile("heal");
		if (healer != null) {
			final String[] healingAttributes = healer.split(",");
			healAmount = Integer.parseInt(healingAttributes[0]);
			healRate = Integer.parseInt(healingAttributes[1]);
		}

		while (!combatFinishedWinPlayer) {
			turns++;
			if ((healAmount != 0) && (turns % healRate == 0)) {
				final int newHP = target.getHP() + healAmount;
				target.setHP(Math.min(target.getBaseHP(), newHP));
			}

			if ((turns % 5 == 0) && player.canHit(target)) {
				int damage = player.damageDone(target, player.getItemAtk(), player.getDamageType());
				if (damage < 0) {
					damage = 0;
				}

				target.setHP(target.getHP() - damage);

				if (target.getHP() <= 0) {
					combatFinishedWinPlayer = true;
					break;
				}
			}

			if ((turns % target.getAttackRate() == 0) && target.canHit(player)) {
				int damage = target.damageDone(player, target.getItemAtk(), player.getDamageType());
				if (damage < 0) {
					damage = 0;
				}
				player.setHP(player.getHP() - damage);
				target.handleLifesteal(target, target.getWeapons(), damage);

				if (player.getHP() <= 0) {
					combatFinishedWinPlayer = true;
					break;
				}
			}
		}

		return new Pair<Integer, Integer>(turns, player.getHP());
	}

	private static Pair<Integer, Integer> combat(final Player player, final Creature target,
			final int rounds) {
		int meanTurns = 0;
		int meanLeftHP = 0;

		for (int i = 0; i < rounds; i++) {
			final Pair<Integer, Integer> results = combat(player, target);
			meanTurns += results.first();
			meanLeftHP += results.second();
		}

		meanTurns = (int) (meanTurns / (rounds * 1.0));
		meanLeftHP = (int) (meanLeftHP / (rounds * 1.0));

		return new Pair<Integer, Integer>(meanTurns, meanLeftHP);
	}

	private static void equip(final Player p, final int level) {
		p.getWeapon().put("atk", 7 + level * 2 / 6);
		if (level == 0) {
			p.getShield().put("def", 0);
		} else {
			p.getShield().put("def", 12 + level / 8);
		}
		p.getArmor().put("def", 1 + level / 4);
		p.getHelmet().put("def", 1 + level / 7);
		p.getLegs().put("def", 1 + level / 7);
		p.getBoots().put("def", 1 + level / 10);
	}

	private static boolean isCorrectResult(final int level,
			final int meanTurns, final double relativeLeftHP) {
		if (!isWithinDurationRange(preferredDuration(level), meanTurns)) {
			return false;
		}

		return (relativeLeftHP <= 0.1) && (relativeLeftHP >= 0);
	}

	private static boolean isWithinDurationRange(final double preferred,
			final double real) {
		return (real < (1.0 + durationThreshold) * preferred)
			&& (real > (1.0 - durationThreshold) * preferred);
	}

	private static double preferredDuration(final int level) {
		return 150 + level;
	}
}
