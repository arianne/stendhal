/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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


/**
 * Class for simulating player vs. creature combat.
 *
 * Note: some of the following code is taken from games.stendhal.tools.BalanceRPGame
 *
 * Usage:
 *     games.stendhal.tools.SimulateCombat --lvl <lvl> --hp <hp> --atk <atk> --def <def>[ --rounds <rounds>]
 *     games.stendhal.tools.SimulateCombat --help
 *
 * If no equipment slots are specificed, player will be equipped with a default set
 * unless --barehanded or --fair is used.
 *
 * --noboost is implied if custom equipment slots are set.
 *
 * @param --lvl
 *     Level at which player & enemy should be set.
 * @param --hp
 *     HP value of enemy.
 * @param --atk
 *     Attack level of enemy.
 * @param --def
 *     Defense level of enemy.
 * @param --plvl
 *     Player level (overrides --lvl).
 * @param --php
 *     Overrides default player HP level.
 * @param --patk
 *     Overrides default player attack level.
 * @param --pdef
 *     Overrides default player defense level.
 * @param --rounds
 *     Number of rounds to simulate (default: 500).
 * @param --threshold
 *     Difference threshold used to determine if combat was balanced.
 * @param --creature
 *     Use a predefined creature as enemy.
 * @param --lhand
 *     Item to equip in `lhand` slot.
 * @param --rhand
 *     Item to equip in `rhand` slot.
 * @param --armor
 *     Item to equip in `armor` slot.
 * @param --head
 *     Item to equip in `head` slot.
 * @param --legs
 *     Item to equip in `legs` slot.
 * @param --boots
 *     Item to equip in `boots` slot.
 * @param --cloak
 *     Item to equip in `cloak` slot.
 * @param --finger
 *     Item to equip in `finger` slot.
 * @param --barehanded
 *     Entities will not be equipped with weapons & armor.
 * @param --equipsame
 *     Enemy will be equipped with same weapons & armor as player.
 * @param --noboost
 *     Player will not get boost from equipment.
 * @param --fair
 *     Gives player weapon with atk 5 & no other equipment (overrides --barehanded & assumes --noboost).
 * @param --boss
 *     Denotes enemy is boss type (currently doesn't affect anything).
 * @param --all
 *     Runs simulation for each predefined creature.
 * @param --help
 *     Show usage information & exit.
 */
public class SimulateCombat {

	private static final int default_rounds = 1000;
	private static int rounds = default_rounds;

	// percent value to gauge balancing (1-100)
	private static final int default_balance_threshold = 15;
	private static int balance_threshold = default_balance_threshold;

	private static Integer lvl;
	private static Integer hp;
	private static Integer atk;
	private static Integer def;
	private static Integer plvl;
	private static Integer php;
	private static Integer patk;
	private static Integer pdef;

	private static Player player;
	private static Creature enemy;
	private static String creature_name;

	/**
	 * If set to <code>true</code>, entities will not be equipped with weapons
	 * & armor.
	 */
	private static boolean barehanded = false;
	private static boolean equipsame = false;
	private static boolean noboost = false;
	private static boolean fair = false;
	private static boolean boss = false;
	private static boolean all = false;
	private static boolean verbose = false;

	/* manually set equipment */
	private static Map<String, String> equipment = new HashMap<String, String>();

	private static EntityManager em;
	private static List<DefaultCreature> creatures;
	private static final List<String> filtered_creatures = new ArrayList<>();

	/**
	 * If a round exceeds this number of turns round will be terminated.
	 *
	 * Used as protection against infinite loop.
	 */
	private static final int TURN_LIMIT = 1000;
	private static int incomplete_rounds = 0;

	private static int phits = 0; // total successful hits by player
	private static int pmisses = 0; // total misses by player
	private static int pblocked = 0; // total times player was blocked
	private static long pdamage = 0; // total damage done by player
	private static int ehits = 0; // total successful hits by enemy
	private static int emisses = 0; // total misses by enemy
	private static int eblocked = 0; // total times enemy was blocked
	private static long edamage = 0; // total damage done by enemy

	private static int total_turns = 0; // number of turns processed during simulation


	public static void main(final String[] argv) throws Exception {
		parseArgs(argv);

		if (rounds < 1) {
			showUsageErrorAndExit("rounds argument must be a postive number", 1);
		} else if (balance_threshold < 1 || balance_threshold > 100) {
			showUsageErrorAndExit("threshold argument must be a number between 1 & 100", 1);
		}

		if (creature_name == null && !all) {
			if (lvl == null) {
				showUsageErrorAndExit("lvl argument must be set", 1);
			} else if (hp == null) {
				showUsageErrorAndExit("hp argument must be set", 1);
			} else if (atk == null) {
				showUsageErrorAndExit("atk argument must be set", 1);
			} else if (def == null) {
				showUsageErrorAndExit("def argument must be set", 1);
			}
		}

		if (all) {
			runFullSimulation();
		} else {
			initEntities();
			runSimulation();
		}
	}

	private static void showDescription() {
		final StringBuilder sb = new StringBuilder("\nDescription:");

		sb.append("\n  A tool for simulating combat between a player & enemy.");
		sb.append("\n\n  It can be used to check if player vs. creature situation is balanced.");

		System.out.println(sb.toString());
	}

	private static void showUsage() {
		final String exe = SimulateCombat.class.getPackage().getName()
			+ "." + SimulateCombat.class.getSimpleName();

		System.out.println("\nUsage:"
			+ "\n\t" + exe
				+ " --lvl <lvl> --hp <hp> --atk <atk> --def <def>"
				+ "[ --rounds <rounds>][ --threshold <threshold>][ flags...]"
			+ "\n\t" + exe + " --creature <name>"
				+ "[ --rounds <rounds>][ --threshold <threshold>][ flags...]"
			+ "\n\t" + exe + " --all"
				+ "[ --rounds <rounds>][ --threshold <threshold>][ flags...][ names...]"
			+ "\n\t" + exe + " --help"
			+ "\n\nRegular Arguments:"
			+ "\n\t--lvl:        Level at which player & enemy should be set."
			+ "\n\t--hp:         HP value of enemy."
			+ "\n\t--atk:        Attack level of enemy."
			+ "\n\t--def:        Defense level of enemy."
			+ "\n\t--plvl:       Player level (overrides --lvl)."
			+ "\n\t--php:        Overrides default player HP level."
			+ "\n\t--patk:       Overrides default player attack level."
			+ "\n\t--pdef:       Overrides default player defense level."
			+ "\n\t--rounds:     Number of rounds to simulate (default: " + default_rounds + ")."
			+ "\n\t--threshold:  Difference threshold used to determine if combat was balanced (default: "
				+ default_balance_threshold + ")."
			+ "\n\t--creature:   Use a predefined creature as enemy."
			+ "\n\t--lhand:      Item to equip to `lhand` slot."
			+ "\n\t--rhand:      Item to equip to `rhand` slot."
			+ "\n\t--armor:      Item to equip to `armor` slot."
			+ "\n\t--head:       Item to equip to `head` slot."
			+ "\n\t--legs:       Item to equip to `legs` slot."
			+ "\n\t--boots:      Item to equip to `boots` slot."
			+ "\n\t--cloak:      Item to equip to `cloak` slot."
			+ "\n\t--finger:     Item to equip to `finger` slot."
			+ "\n\t--help|-h:    Show usage information & exit."
			+ "\n\nFlag Arguments:"
			+ "\n\t--barehanded: Entities will not be equipped with weapons & armor."
			+ "\n\t--equipsame:  Enemy will be equipped with same weapons & armor as player."
			+ "\n\t--noboost:    Player will not get boost from equipment."
			+ "\n\t--fair:       Gives player weapon with atk 5 & no other equipment (overrides --barehanded & assumes --noboost)."
			+ "\n\t--boss:       Denotes enemy is boss type (currently doesn't affect anything)."
			+ "\n\t--all:        Runs simulation for each predefined creature. If names are"
				+ " supplied, only those creatures will be simulated."
			+ "\n\t--verbose|-v  Output detailed round info.");
	}

	private static void showUsageErrorAndExit(final String msg, final int err) {
		System.out.println("\nERROR: " + msg);
		showUsage();
		System.exit(err);
	}

	private static void parseArgs(final String[] argv) {
		final List<String> unknownArgs = new ArrayList<>();

		for (int idx = 0; idx < argv.length; idx++) {
			final String st = argv[idx].toLowerCase();

			if (st.equals("--help") || st.equals("-h") || st.equals("help")) {
				showDescription();
				showUsage();
				System.exit(0);
			} else if (st.equals("--lvl") || st.equals("--level")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("lvl argument requires value", 1);
				}

				try {
					lvl = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("lvl argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--hp")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("hp argument requires value", 1);
				}

				try {
					hp = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("hp argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--atk") || st.equals("--attack")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("atk argument requires value", 1);
				}

				try {
					atk = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("atk argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--def") || st.equals("--defense")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("def argument requires value", 1);
				}

				try {
					def = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("def argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--plvl") || st.equals("--plevel")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("plvl argument requires value", 1);
				}

				try {
					plvl = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("plvl argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--php")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("php argument requires value", 1);
				}

				try {
					php = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("php argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--patk")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("patk argument requires value", 1);
				}

				try {
					patk = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("patk argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--pdef")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("pdef argument requires value", 1);
				}

				try {
					pdef = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("pdef argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--rounds")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("rounds argument requires value", 1);
				}

				try {
					rounds = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("rounds argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--threshold")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("threshold argument requires value", 1);
				}

				try {
					balance_threshold = Integer.parseInt(argv[idx + 1]);
				} catch (final NumberFormatException e) {
					showUsageErrorAndExit("threshold argument must be an integer number", 1);
				}

				idx++;
			} else if (st.equals("--creature")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("threshold argument requires value", 1);
				}

				creature_name = argv[idx + 1];

				idx++;
			} else if (st.equals("--lhand")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("lhand argument requires value", 1);
				}

				equipment.put("lhand", argv[idx + 1]);
				idx++;
			} else if (st.equals("--rhand")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("rhand argument requires value", 1);
				}

				equipment.put("rhand", argv[idx + 1]);
				idx++;
			} else if (st.equals("--armor")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("armor argument requires value", 1);
				}

				equipment.put("armor", argv[idx + 1]);
				idx++;
			} else if (st.equals("--head")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("head argument requires value", 1);
				}

				equipment.put("head", argv[idx + 1]);
				idx++;
			} else if (st.equals("--legs")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("legs argument requires value", 1);
				}

				equipment.put("legs", argv[idx + 1]);
				idx++;
			} else if (st.equals("--boots")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("boots argument requires value", 1);
				}

				equipment.put("boots", argv[idx + 1]);
				idx++;
			} else if (st.equals("--cloak")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("cloak argument requires value", 1);
				}

				equipment.put("cloak", argv[idx + 1]);
				idx++;
			} else if (st.equals("--finger")) {
				if (argv.length < idx + 2) {
					showUsageErrorAndExit("finger argument requires value", 1);
				}

				equipment.put("finger", argv[idx + 1]);
				idx++;
			} else if (st.equals("--barehanded")) {
				barehanded = true;
			} else if (st.equals("--equipsame")) {
				equipsame = true;
			} else if (st.equals("--noboost")) {
				noboost = true;
			} else if (st.equals("--fair")) {
				fair = true;
			} else if (st.equals("--boss")) {
				boss = true;
			} else if (st.equals("--all")) {
				all = true;
			} else if (st.equals("--verbose") || st.equals("-v")) {
				verbose = true;
			} else {
				if (all) {
					filtered_creatures.add(st);
				} else {
					unknownArgs.add(st);
				}
			}
		}

		if (unknownArgs.size() > 0) {
			showUsageErrorAndExit("Unknown argument: " + unknownArgs.get(0), 1);
		}
	}

	private static void initEntities() {
		if (em == null) {
			new RPClassGenerator().createRPClasses();
			em = SingletonRepository.getEntityManager();
		}

		final int HIGHEST_LEVEL = 597;

		final int[] atkLevels = new int[HIGHEST_LEVEL + 1];
		final int[] defLevels = new int[HIGHEST_LEVEL + 1];

		for (int l = 0; l < atkLevels.length; l++) {
			// help newbies a bit, so don't start at real stats, but a bit lower
			atkLevels[l] = (int) Math.round(Math.log(l + 4) * 9  - 10);
			defLevels[l] = (int) Math.round(Math.log(l + 4) * 20
					+ l - 26);
		}

		Item lhand = null;
		Item rhand = null;
		Item armor = null;
		Item helmet = null;
		Item legs = null;
		Item boots = null;
		Item cloak = null;
		Item finger = null;

		boolean custom_equip = false;
		if (equipment.containsKey("lhand")) {
			final String itemName = equipment.get("lhand");
			lhand = em.getItem(itemName);
			if (lhand == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("rhand")) {
			final String itemName = equipment.get("rhand");
			rhand = em.getItem(itemName);
			if (rhand == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("armor")) {
			final String itemName = equipment.get("armor");
			armor = em.getItem(itemName);
			if (armor == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("helmet")) {
			final String itemName = equipment.get("");
			helmet = em.getItem(itemName);
			if (helmet == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("legs")) {
			final String itemName = equipment.get("legs");
			legs = em.getItem(itemName);
			if (legs == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("boots")) {
			final String itemName = equipment.get("boots");
			boots = em.getItem(itemName);
			if (boots == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("cloak")) {
			final String itemName = equipment.get("cloak");
			cloak = em.getItem(itemName);
			if (cloak == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}
		if (equipment.containsKey("finger")) {
			final String itemName = equipment.get("finger");
			finger = em.getItem(itemName);
			if (finger == null) {
				System.out.println("Unknown item: " + itemName);
				System.exit(1);
			}
			custom_equip = true;
		}

		if (!custom_equip) {
			lhand = em.getItem("wooden shield");
			rhand = em.getItem("club");
			armor = em.getItem("dress");
			helmet = em.getItem("leather helmet");
			legs = em.getItem("leather legs");
			boots = em.getItem("leather boots");
		}

		if (creature_name == null) {
			enemy = new Creature("dummy", "dummy", "(generic creature)", hp, atk, atk, def, lvl,
				1, 1, 1, 1.0, new ArrayList<>(), new HashMap<>(), new LinkedHashMap<>(), 1, "dummy");
		} else {
			if (creatures == null) {
				creatures = new CreatureGroupsXMLLoader("/data/conf/creatures.xml").load();
			}

			if (enemy == null) {
				for (final DefaultCreature df: creatures) {
					if (df.getCreatureName().equals(creature_name)) {
						enemy = df.getCreature();
						boss = df.getAIProfiles().containsKey("boss");
						break;
					}
				}

				if (enemy == null) {
					System.out.println("\nERROR: unknown creature \"" + creature_name + "\"");
					System.exit(1);
				}
			}
		}

		System.out.println("\nSetting up for enemy: " + enemy.getName());

		player = (Player) new PlayerTransformer().transform(new RPObject());

		Integer p_lvl = plvl;
		if (p_lvl == null) {
			p_lvl = enemy.getLevel();
		}

		player.setLevel(p_lvl);

		if (php == null) {
			player.setBaseHP(100 + 10 * p_lvl);
		} else {
			player.setBaseHP(php);
		}

		int p_atk = atkLevels[p_lvl] + 8;
		int p_def = defLevels[p_lvl] + 8;

		if (p_lvl > 149) {
			// stat boost from Aenihata
			p_atk += 50;
			p_def += 100;
		}

		// overridden with manual values
		if (patk != null) {
			p_atk = patk;
		}
		if (pdef != null) {
			p_def = pdef;
		}

		player.setAtk(p_atk);
		player.setDef(p_def);

		if (fair) {
			player.equip("rhand", em.getItem("soul dagger"));
		} else if (!barehanded) {
			player.equip("lhand", lhand);
			player.equip("rhand", rhand);
			player.equip("armor", armor);
			player.equip("head", helmet);
			player.equip("legs", legs);
			player.equip("feet", boots);
			player.equip("cloak", cloak);
			player.equip("finger", finger);

			if (!noboost && !custom_equip) {
				// not sure what this does (copied from games.stendhal.tools.BalanceRPGame)
				player.getWeapon().put("atk", 7 + p_lvl * 2 / 6);
				if (p_lvl == 0) {
					player.getShield().put("def", 0);
				} else {
					player.getShield().put("def", 12 + p_lvl / 8);
				}
				player.getArmor().put("def", 1 + p_lvl / 4);
				player.getHelmet().put("def", 1 + p_lvl / 7);
				player.getLegs().put("def", 1 + p_lvl / 7);
				player.getBoots().put("def", 1 + p_lvl / 10);
			}
		}

		if (!barehanded && equipsame) {
			// doesn't appear to actually do anything
			final Item pWeapon = player.getWeapon();
			if (pWeapon != null) {
				enemy.equip("rhand", pWeapon);
			}
			if (player.hasShield()) {
				enemy.equip("lhand", player.getShield());
			}
			if (player.hasArmor()) {
				enemy.equip("armor", player.getArmor());
			}
			if (player.hasHelmet()) {
				enemy.equip("head", player.getHelmet());
			}
			if (player.hasLegs()) {
				enemy.equip("legs", player.getLegs());
			}
			if (player.hasBoots()) {
				enemy.equip("feet", player.getBoots());
			}
			if (player.hasRing()) {
				enemy.equip("finger", player.getRing());
			}
		}
	}

	private static void runSimulation() {
		if (!all) {
			if (creature_name != null) {
				System.out.println("\nRunning simulation for " + creature_name + " (" + rounds + " rounds) ...");
			} else {
				System.out.println("\nRunning simulation ...");
			}
		}

		int wins = 0;
		int losses = 0;
		int ties = 0;

		phits = 0;
		pmisses = 0;
		pblocked = 0;
		pdamage = 0;
		ehits = 0;
		emisses = 0;
		eblocked = 0;
		edamage = 0;

		total_turns = 0;

		int ridx;
		for (ridx = 0; ridx < rounds; ridx++) {
			final Pair<Integer, Integer> result = simulateRound();

			final int playerHP = result.first();
			final int enemyHP = result.second();

			String winner = "tie";
			if (playerHP > enemyHP) {
				winner = "player";
			} else if (playerHP < enemyHP) {
				winner = "enemy";
			}

			// output detailed round info
			if (verbose) {
				System.out.println("\nRound " + (ridx+1) + "/" + rounds + " winner: " + winner
					+ "\n  player HP: " + playerHP + "\n  enemy  HP: " + enemyHP);
			}

			if (playerHP > enemyHP) {
				wins++;
			} else if (playerHP < enemyHP) {
				losses++;
			} else {
				ties++;
			}
		}

		final long win_ratio = Math.round((Double.valueOf(wins) / rounds) * 100);
		final long loss_ratio = Math.round((Double.valueOf(losses) / rounds) * 100);
		final long tie_ratio = Math.round((Double.valueOf(ties) / rounds) * 100);

		System.out.println("\nFINAL RESULT (" + ridx + " rounds):");

		final int pAtk = player.getAtk();
		final int pDef = player.getDef();
		final double pItemAtk = player.getItemAtk();
		final double pItemDef = player.getItemDef();
		final double pAtkTotal = pAtk * (pItemAtk + 1);
		final double pDefTotal = pDef * (pItemDef + 1);

		final int eAtk = enemy.getAtk();
		final int eDef = enemy.getDef();
		final double eItemAtk = enemy.getItemAtk();
		final double eItemDef = enemy.getItemDef();
		final double eAtkTotal = eAtk * (eItemAtk + 1);
		final double eDefTotal = eDef * (eItemDef + 1);

		final List<String> equip_types = Arrays.asList("weapon", "shield",
			"helmet", "armor", "legs", "boots", "cloak", "ring");

		final Map<String, Item> pEquip = new HashMap<String, Item>() {{
			put("weapon", player.getWeapon());
			put("shield", player.getShield());
			put("helmet", player.getHelmet());
			put("armor", player.getArmor());
			put("legs", player.getLegs());
			put("boots", player.getBoots());
			put("cloak", player.getCloak());
			put("ring", player.getRing());
		}};
		final Map<String, Item> eEquip = new HashMap<String, Item>() {{
			put("weapon", enemy.getWeapon());
			put("shield", enemy.getShield());
			put("helmet", enemy.getHelmet());
			put("armor", enemy.getArmor());
			put("legs", enemy.getLegs());
			put("boots", enemy.getBoots());
			put("cloak", enemy.getCloak());
			put("ring", enemy.getRing());
		}};


		// *** player info ***

		final StringBuilder sb = new StringBuilder("\n  Player stats:"
			+ "\n    Level: " + player.getLevel()
			+ "\n    HP:    " + player.getBaseHP()
			+ "\n    ATK:   " + pAtk
			+ "\n           (item: " + pItemAtk + ", total: " + pAtkTotal + ")"
			+ "\n    DEF:   " + pDef
			+ "\n           (item: " + pItemDef + ", total: " + pDefTotal + ")"
			+ "\n    Equip: ");

		boolean has_equip = false;
		final StringBuilder equip_sb = new StringBuilder();
		for (final String item_type: equip_types) {
			final Item e = pEquip.get(item_type);
			if (e != null) {
				final String e_name = e.getName();
				if (e_name != null && e_name != "") {
					if (has_equip) {
						equip_sb.append(", ");
					}

					equip_sb.append(item_type + "=" + e_name);
					has_equip = true;
				}
			}
		}
		if (!has_equip) {
			equip_sb.append("none");
		}
		sb.append(equip_sb.toString());
		System.out.println(sb.toString());


		// *** enemy info ***

		sb.delete(0, sb.length()); // reset info

		sb.append("\n  Enemy stats:"
			+ "\n    Name:  " + enemy.getName());
		if (boss) {
			sb.append(" (boss)");
		}
		sb.append("\n    Level: " + enemy.getLevel()
			+ "\n    HP:    " + enemy.getBaseHP()
			+ "\n    ATK:   " + eAtk
			+ "\n           (item: " + eItemAtk + ", total: " + eAtkTotal + ")"
			+ "\n    DEF:   " + eDef
			+ "\n           (item: " + eItemDef + ", total: " + eDefTotal + ")"
			+ "\n    Equip: ");

		// reset equipment string
		has_equip = false;
		equip_sb.delete(0, equip_sb.length());
		for (final String item_type: equip_types) {
			final Item e = eEquip.get(item_type);
			if (e != null) {
				String e_name = e.getName();
				if (e_name != null && e_name != "") {
					if (has_equip) {
						equip_sb.append(", ");
					}

					if (item_type.equals("weapon")) {
						final Item e_weapon = enemy.getWeapon();
						if (e_weapon != null && e_weapon.getName() == "") {
							e_name = "unnamed weapon";
						}
					}

					equip_sb.append(item_type + "=" + e_name);
					has_equip = true;
				}
			}
		}
		if (!has_equip) {
			equip_sb.append("none");
		}
		sb.append(equip_sb.toString());
		System.out.println(sb.toString());


		System.out.println("\n  Player wins:       " + wins + " (" + win_ratio + "%)"
			+ "\n  Enemy wins:        " + losses + " (" + loss_ratio + "%)"
			+ "\n  Ties:              " + ties + " (" + tie_ratio + "%)"
			+ "\n  Incomplete rounds: " + incomplete_rounds
			+ "\n  Total turns:       " + total_turns
			+ "\n  Turns per round:   " + (total_turns / (double) rounds));

		long diff_ratio = 0;
		String beneficiary = "none";
		if (wins > losses) {
			diff_ratio = win_ratio - loss_ratio - tie_ratio;
			beneficiary = "player";
		} else if (wins < losses) {
			diff_ratio = loss_ratio - win_ratio - tie_ratio;
			beneficiary = "enemy";
		}

		System.out.println("\n  Resulting difference ratio: " + Math.abs(diff_ratio) + "%");
		if (diff_ratio <= balance_threshold) {
			System.out.println("    Result is within balance threshold of "
				+ balance_threshold + "%");
		} else {
			System.out.println("    Result is not within balance threshold of "
				+ balance_threshold + "%");
		}
		System.out.println("    Beneficiary: " + beneficiary);

		System.out.println("\n  Total damage done by player: " + pdamage
		+ "\n    Hits:    " + phits
		+ "\n    Misses:  " + pmisses
		+ "\n    Blocked: " + pblocked
		+ "\n  Total damage done by enemy: " + edamage
		+ "\n    Hits:    " + ehits
		+ "\n    Misses:  " + emisses
		+ "\n    Blocked: " + eblocked);
	}

	private static Pair<Integer, Integer> simulateRound() {
		// make sure entities have full HP for each round
		player.heal();
		enemy.heal();

		int turn = 0;
		while (player.getHP() > 0 && enemy.getHP() > 0) {
			turn++;

			int damageDealt = 0;
			int damageReceived = 0;

			if (!player.canHit(enemy)) {
				pmisses++;
			} else {
				damageDealt = player.damageDone(enemy, player.getItemAtk(), player.getDamageType());

				if (damageDealt > 0 ) {
					phits++;
					pdamage += damageDealt;
				} else {
					pblocked++;
				}
			}

			if (!enemy.canHit(player)) {
				emisses++;
			} else {
				damageReceived = enemy.damageDone(player, enemy.getItemAtk(), player.getDamageType());

				if (damageReceived > 0 ) {
					ehits++;
					edamage += damageReceived;
				} else {
					eblocked++;
				}
			}

			player.setHP(player.getHP() - damageReceived);
			enemy.setHP(enemy.getHP() - damageDealt);

			total_turns++;

			if (turn == TURN_LIMIT && player.getHP() > 0 && enemy.getHP() > 0) {
				System.out.println("\nWARNING: Turn limit reached (" + TURN_LIMIT + "), terminating round ...");
				incomplete_rounds++;
				break;
			}
		}

		// calculate damage within range of entity's HP
		final int res_p_hp = player.getHP();
		final int res_e_hp = enemy.getHP();
		if (res_p_hp < 0) {
			edamage += res_p_hp;
		}
		if (res_e_hp < 0) {
			pdamage += res_e_hp;
		}

		return new Pair<Integer, Integer>(player.getHP(), enemy.getHP());
	}

	private static void runFullSimulation() {
		new RPClassGenerator().createRPClasses();
		em = SingletonRepository.getEntityManager();
		creatures = new CreatureGroupsXMLLoader("/data/conf/creatures.xml").load();

		Collections.sort(creatures, new Comparator<DefaultCreature>() {
			@Override
			public int compare(final DefaultCreature o1, final DefaultCreature o2) {
				return o1.getLevel() - o2.getLevel();
			}
		});

		boolean filtered = false;
		int c_count = filtered_creatures.size();
		if (c_count > 0) {
			filtered = true;
		} else {
			c_count = creatures.size();
		}

		if (filtered) {
			System.out.println("\nRunning simulation of select predefined creatures ...");
		} else {
			System.out.println("\nRunning simulation of all predefined creatures ...");
		}

		int c_idx = 0;

		for (final DefaultCreature df: creatures) {
			creature_name = df.getCreatureName();
			if (filtered && !filtered_creatures.contains(creature_name)) {
				continue;
			}

			c_idx++;
			enemy = df.getCreature();
			boss = df.getAIProfiles().containsKey("boss");

			initEntities();

			System.out.println("\nRunning simulation for " + creature_name
				+ " (" + c_idx + "/" + c_count + ") ...");
			runSimulation();

			if (filtered) {
				filtered_creatures.remove(creature_name);
			}
		}

		if (filtered_creatures.size() > 0) {
			System.out.println("\nSkipped unknown creatures:");
			for (final String c_name: filtered_creatures) {
				System.out.println("  " + c_name);
			}
		}
	}
}
