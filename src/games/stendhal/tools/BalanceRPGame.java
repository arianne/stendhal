package games.stendhal.tools;

import games.stendhal.server.core.config.CreatureGroupsXMLLoader;
import games.stendhal.server.core.engine.RPClassGenerator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import marauroa.common.Pair;

/* 
 * Running:
 * 	java -cp $CLASSPATH java -cp games/stendhal/tools/BalanceRPGame
 * 		calculates balanced atk and def values to all creatures up to HIGHEST_LEVEL
 * 		(defined in the code below)
 * 	java -cp $CLASSPATH java -cp games/stendhal/tools/BalanceRPGame creature
 * 		calculates the values only for the specified creature
 * 
 * 	CLASSPATH should be (with appropriate changes for OS and versions), assuming
 * 		the compiled .class for this file is under "bin":
 *		.:bin:libs/marauroa.jar:libs/log4j.jar:libs/mysql-connector-java-5.1.5-bin.jar:build/lib/stendhal-stendhal-server-0.71.jar
 */
/** * NOTE: AWFUL CODE FOLLOWS. YOU ARE NOT SUPPOSED TO READ THIS ;P ** */

public class BalanceRPGame {
	/** 
	 * A Simple (dumb) optimizer to adjust creature stats.
	 */
	private static class Optimizer {
		Creature creature;
		
		public Optimizer(final Creature creature) {
			this.creature = creature;
		}
		
		public void step(final int leftHP, final int rounds) {
			float stepSize = leftHP / (float) player.getBaseHP();
			stepSize = Math.signum(stepSize) * Math.min(Math.abs(stepSize), 0.5f);
			
			final int oldAtk = creature.getATK();
			int newAtk = Math.max(1, Math.round(creature.getATK()
					+ stepSize * creature.getATK()));
			// Always ensure trying to get out of dead area
			if ((leftHP < 0) && (newAtk == oldAtk)) {
				newAtk--;
			}
			
			final int level = creature.getLevel();
			final int oldDef = creature.getDEF();
			int newDef = oldDef;
			final double preferred = preferredDuration(level);
			if (!isWithinDurationRange(preferred, rounds)) {
				// Don't grow it the monster is already stronger than the player
				if ((leftHP > 0) || (preferred < rounds)) {
				newDef = Math.max(1, (int) (creature.getDEF()
						+ preferred - rounds + 0.5));
				}
			} else {
				newDef = 	Math.max(1, (int) (creature.getDEF() 
					+ 5 * stepSize * creature.getDEF() + 0.5f));
			}
			// Don't change too fast
			if (newDef > 1.1 * oldDef) {
				newDef = Math.max((int) (1.1 * oldDef), oldDef + 1);
			} else if (newDef < 0.9 * oldDef) {
				newDef = Math.max(1, Math.min((int) (0.9 * oldDef), oldDef - 1));
			}
	
			creature.setATK(newAtk);
			creature.setDEF(newDef);
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

		player = Player.createZeroLevelPlayer("Tester", null);

		player.equipToInventoryOnly(shield);
		player.equipToInventoryOnly(armor);
		player.equipToInventoryOnly(helmet);
		player.equipToInventoryOnly(legs);
		player.equipToInventoryOnly(boots);

		final boolean found = false;

		for (final DefaultCreature creature : creatures) {
			final int level = creature.getLevel();
			
			if (args.length > 0) {
				if (!args[0].equals(creature.getCreatureName()) && !found) {
					continue;
				}
			}
			if (creature.getLevel() > HIGHEST_LEVEL) {
				continue;
			}

			final Creature target = creature.getCreature();
			final Optimizer optimizer = new Optimizer(target);
			
			player.setLevel(level);
			player.setBaseHP(100 + 10 * level);
			player.setATK(atkLevels[level]);
			player.setDEF(defLevels[level]);

			equip(player, level);

			System.out.println("Player(" + level + ") vs "
					+ creature.getCreatureName());

			durationThreshold = DEFAULT_DURATION_THRESHOLD;
			
			boolean balanced = false;
			int tries = 0;
			while (!balanced) {
				final Pair<Integer, Integer> results = combat(player, target,
						ROUNDS);
				final int meanTurns = results.first();
				final int meanLeftHP = results.second();

				final int proposedXPValue = (int) ((2 * creature.getLevel() + 1) * (meanTurns / 2.0));
				creature.setLevel(creature.getLevel(), proposedXPValue);
				
				System.out.println("Target ATK: "
						+ target.getATK()
							+ "/DEF: "
							+ target.getDEF()
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

					System.out.println("New ATK: " + target.getATK()
							+ "/DEF: " + target.getDEF() + "/HP: "
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

			if (creature.getATK() != target.getATK()) {
				changed = true;
			}

			if (creature.getDEF() != target.getDEF()) {
				changed = true;
			}

			if (creature.getHP() != target.getBaseHP()) {
				changed = true;
			}

			System.out.print("BALANCED: ");
			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(creature.getCreatureName());
			stringBuilder.append("(");
			stringBuilder.append(creature.getLevel());
			stringBuilder.append(")\t");
			if (changed) {
				stringBuilder.append("*\t");
			} else {
				stringBuilder.append(" \t");
			}
			stringBuilder.append("ATK: ");
			stringBuilder.append(target.getATK());
			stringBuilder.append("\t\tDEF: ");
			stringBuilder.append(target.getDEF());
			stringBuilder.append("\t\tHP: ");
			stringBuilder.append(target.getBaseHP());
			stringBuilder.append("\t\tXP: ");
			stringBuilder.append(creature.getXP());
			System.out.println(stringBuilder.toString());
		}
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
				int damage = player.damageDone(target);
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
				int damage = target.damageDone(player);
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
		p.getWeapons().get(0).put("atk", 7 + level * 2 / 6);
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

		if ((relativeLeftHP > 0.1) || (relativeLeftHP < 0)) {
			return false;
		}

		return true;
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
