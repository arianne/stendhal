package games.stendhal.tools;

import marauroa.common.Pair;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.slot.EntitySlot;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import marauroa.common.game.RPObject;

/** * NOTE: AWFUL CODE FOLLOWS. YOU ARE NOT SUPPOSED TO READ THIS ;P ** */

public class BalanceRPGame {

	private static int ROUNDS = 100;

	public static Pair<Integer, Integer> combat(Player player, Creature target,
			int rounds) {
		int meanTurns = 0;
		int meanLeftHP = 0;

		for (int i = 0; i < rounds; i++) {
			Pair<Integer, Integer> results = combat(player, target);
			meanTurns += results.first();
			meanLeftHP += results.second();
		}

		meanTurns = (int) (meanTurns / (rounds * 1.0));
		meanLeftHP = (int) (meanLeftHP / (rounds * 1.0));

		return new Pair<Integer, Integer>(meanTurns, meanLeftHP);
	}

	public static Pair<Integer, Integer> combat(Player player, Creature target) {
		target.setHP(target.getBaseHP());
		player.setHP(player.getBaseHP());

		boolean combatFinishedWinPlayer = false;
		int turns = 0;

		while (!combatFinishedWinPlayer) {
			turns++;

			if (StendhalRPAction.riskToHit(player, target)) {
				int damage = StendhalRPAction.damageDone(player, target);
				if (damage < 0) {
					damage = 0;
				}

				target.setHP(target.getHP() - damage);
			}

			if (target.getHP() <= 0) {
				combatFinishedWinPlayer = true;
				break;
			}

			if (StendhalRPAction.riskToHit(target, player)) {
				int damage = StendhalRPAction.damageDone(target, player);
				if (damage < 0) {
					damage = 0;
				}
				player.setHP(player.getHP() - damage);
			}

			if (player.getHP() <= 0) {
				combatFinishedWinPlayer = true;
				break;
			}
		}

		return new Pair<Integer, Integer>(turns, player.getHP());
	}

	public static void main(String[] args) throws Exception {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		StendhalRPZone area = new StendhalRPZone("test");
		world.addRPZone(area);

		List<DefaultCreature> creatures = SingletonRepository.getCreaturesXMLLoader().load(
				"data/conf/creatures.xml");

		Collections.sort(creatures, new Comparator<DefaultCreature>() {

			public int compare(DefaultCreature o1, DefaultCreature o2) {
				return o1.getLevel() - o2.getLevel();
			}

			@Override
			public boolean equals(Object obj) {
				return true;
			}
		});

		int[] atkLevels = new int[110];
		int[] defLevels = new int[110];

		for (int i = 0; i < atkLevels.length; i++) {
			atkLevels[i] = 10 + (int) Math.round(Math.log(i + 1) / Math.log(10)
					* 7);
			defLevels[i] = 10 + (int) Math.round(Math.log(i + 1) / Math.log(10)
					* 14);
		}

		EntityManager em = SingletonRepository.getEntityManager();
		Item weapon = em.getItem("club");
		area.assignRPObjectID(weapon);

		Item shield = em.getItem("wooden shield");
		area.assignRPObjectID(shield);

		Item armor = em.getItem("dress");
		area.assignRPObjectID(armor);

		Item helmet = em.getItem("leather helmet");
		area.assignRPObjectID(helmet);

		Item legs = em.getItem("leather legs");
		area.assignRPObjectID(legs);

		Item boots = em.getItem("leather boots");
		area.assignRPObjectID(boots);

		Player player = new Player(new RPObject());
		player.addSlot(new EntitySlot("lhand"));
		player.addSlot(new EntitySlot("rhand"));
		player.addSlot(new EntitySlot("armor"));
		player.addSlot(new EntitySlot("head"));
		player.addSlot(new EntitySlot("legs"));
		player.addSlot(new EntitySlot("feet"));

		player.equip(weapon);
		player.equip(shield);
		player.equip(armor);
		player.equip(helmet);
		player.equip(legs);
		player.equip(boots);

		// for(int level=0;level<60;level++)
		// {
		// player.setBaseHP(100+10*level);
		// player.setATK(atkLevels[level]);
		// player.setDEF(defLevels[level]);
		// equip(player,level);
		// System.out.println ("("+level+")\tATK: "+player.getATK()+"\tDEF:
		// "+player.getDEF()+"\tHP: "+player.getBaseHP()+
		// "\tWeapon: "+player.getWeapon().getAttack()+"\tShield:
		// "+player.getShield().getDefense()+"\tArmor:
		// "+player.getArmor().getDefense()+
		// "\tHelmet: "+player.getHelmet().getDefense()+"\tLegs:
		// "+player.getLegs().getDefense()+"\tBoots:
		// "+player.getBoots().getDefense());
		// }
		//
		// System.exit(0);

		StringBuffer st = new StringBuffer("Creatures done: \n");

		boolean found = false;

		for (DefaultCreature creature : creatures) {
			if (args.length > 0) {
				if (!args[0].equals(creature.getCreatureName()) && !found) {
					continue;
				}
				// else
				// {
				// found=true;
				// }
			}

			// OUTPUT: System.out.println ("--
			// "+creature.getCreatureName()+"("+creature.getLevel()+")");

			Creature target = creature.getCreature();

			int minlevel = creature.getLevel() - 2;
			if (minlevel < 0) {
				minlevel = 0;
			}

			int maxlevel = creature.getLevel() + 2;

			for (int level = minlevel; level < maxlevel; level++) {
				boolean balanced = false;

				while (!balanced) {
					player.setLevel(level);
					player.setBaseHP(100 + 10 * level);
					player.setATK(atkLevels[level]);
					player.setDEF(defLevels[level]);

					equip(player, level);

					Pair<Integer, Integer> results = combat(player, target,
							ROUNDS);
					int meanTurns = results.first();
					int meanLeftHP = results.second();

					if (level == creature.getLevel()) {
						int proposedXPValue = 1 * (int) ((creature.getLevel() + 1) * (meanTurns / 2.0));
						// OUTPUT: System.out.println ("Proposed XP:
						// "+proposedXPValue+"\t Actual XP: "+creature.getXP());
						creature.setLevel(creature.getLevel(), proposedXPValue);
					}

					System.out.println("Player("
							+ level
							+ ") VS "
							+ creature.getCreatureName()
							+ "\t Turns: "
							+ meanTurns
							+ "\tLeft HP:"
							+ Math.round(100 * meanLeftHP
									/ (1.0 * player.getBaseHP())));

					if (isCorrectResult(level, level - creature.getLevel(),
							meanTurns, meanLeftHP / (1.0 * player.getBaseHP()))) {
						balanced = true;
					} else {
						double best = Double.MAX_VALUE;
						Creature bestCreature = null;

						for (Creature child : children(target)) {
							results = combat(player, child, ROUNDS);

							int turns = results.first();
							int leftHP = results.second();

							double childScore = score(turns, leftHP
									/ (1.0 * player.getBaseHP()), level, child);
							System.out.println("Child ATK: "
									+ child.getATK()
									+ "/DEF: "
									+ child.getDEF()
									+ "/HP: "
									+ child.getBaseHP()
									+ "\t scored "
									+ childScore
									+ "\t Turns: "
									+ turns
									+ "\tLeft HP:"
									+ Math.round(100 * leftHP
											/ (1.0 * player.getBaseHP())));

							if (childScore < best) {
								best = childScore;
								bestCreature = child;
							}
						}

						target = bestCreature;
						level = minlevel;

						System.out.println("New ATK: " + target.getATK()
								+ "/DEF: " + target.getDEF() + "/HP: "
								+ target.getBaseHP());
					}
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
			System.out.println(creature.getCreatureName() + "("
					+ creature.getLevel() + ")\t" + (changed ? "*\t" : " \t")
					+ "ATK: " + target.getATK() + "\t\tDEF: " + target.getDEF()
					+ "\t\tHP: " + target.getBaseHP() + "\t\tXP: "
					+ creature.getXP());
			st.append("BALANCED: " + creature.getCreatureName() + "("
					+ creature.getLevel() + ")\tATK: " + target.getATK()
					+ "\tDEF: " + target.getDEF() + "\tHP: "
					+ target.getBaseHP() + "\tXP: " + creature.getXP() + "\n");
		}

		// OUTPUT: System.out.println (st);
	}

	private static double score(int turns, double leftHP, int level,
			Creature creature) {
		double score = 0;

		int creatureLevel = creature.getLevel();

		if (level - creatureLevel < 0) {
			// Weaker than creature.
			score = leftHP * 100 + (turns / 30.0);
		}

		if (level - creatureLevel == 0) {
			if (leftHP < 0.1) {
				score = 1000 - leftHP * 100 + (Math.abs(turns - 30) * 3);
			} else if (leftHP < 0.7) {
				score = 500 - leftHP * 100 + (Math.abs(turns - 30) * 3);
			} else if ((leftHP >= 0.7) && (turns >= 30) && (turns <= 40)) {
				score = Math.abs(leftHP * 100 - 85) + Math.abs(turns - 40) * 3;
			} else {
				score = Math.abs(leftHP * 100 - 85) + Math.abs(turns - 40) * 6;
			}
		}

		if (level - creatureLevel > 0) {
			// Stronger than creature.
			score = (1 - leftHP) * 100 + (turns / 5.0);
		}

		return score;
	}

	private static Creature[] children(Creature creature) {
		Creature[] creatures = new Creature[9];

		for (int i = 0; i < 9; i++) {
			creatures[i] = new Creature(creature);
			creatures[i].setATK(creature.getATK() + Rand.roll1D6() - 3);
			creatures[i].setDEF(creature.getDEF() + Rand.roll1D6() - 3);
			creatures[i].setBaseHP(creature.getBaseHP() + Rand.roll1D20() - 10);
		}

		return creatures;
	}

	private static boolean isCorrectResult(int level, int levelDiff,
			int meanTurns, double meanLeftHP) {
		if ((levelDiff > 0) && (meanTurns > 100 + level / 10.0)) {
			// OUTPUT: System.out.println ("FAILED beacause takes too much time
			// to kill");
			return false;
		}

		if ((levelDiff == 0) && (meanTurns > 30 + level / 10.0)) {
			// OUTPUT: System.out.println ("FAILED beacause takes too much time
			// to kill");
			return false;
		}

		if ((levelDiff == 0) && (meanLeftHP > 1 - level / 100.0)) {
			// OUTPUT: System.out.println ("CORRECT");
			return true;
		}

		if ((levelDiff < 0) && (meanLeftHP > 1 - level / 100.0)) {
			// OUTPUT: System.out.println ("FAILED beacause takes makes LITTLE
			// damage to player at same level");
			return false;
		}

		if ((levelDiff > 0) && (meanLeftHP < 0.90 - level / 100.0)) {
			// OUTPUT: System.out.println ("FAILED beacause takes makes MUCH
			// damage to player at same level");
			return false;
		}

		// OUTPUT: System.out.println ("CORRECT: No reason");
		return true;
	}

	static void equip(Player p, int level) {
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
}
