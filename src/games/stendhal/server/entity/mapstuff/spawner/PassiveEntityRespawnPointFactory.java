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
package games.stendhal.server.entity.mapstuff.spawner;

import java.util.Arrays;

import org.apache.log4j.Logger;

import marauroa.common.game.IRPZone.ID;

/**
 * creates a PassiveEntityRespawnPoint.
 */
public class PassiveEntityRespawnPointFactory {
	private static Logger logger = Logger
			.getLogger(PassiveEntityRespawnPointFactory.class);

	/**
	 * creates a PassiveEntityRespawnPoint.
	 *
	 * @param clazz
	 *            class
	 * @param type
	 *            type
	 * @param id
	 *            zone id
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return PassiveEntityRespawnPoint or null in case some error occurred
	 */
	public static PassiveEntityRespawnPoint create(final String clazz,
			final int type, final ID id, final int x, final int y) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint = null;

		if (clazz.contains("herb")) {
			passiveEntityrespawnPoint = createHerb(type);

		} else if (clazz.contains("corn")) {
			passiveEntityrespawnPoint = createGrain(type);

		} else if (clazz.contains("mushroom")) {
			passiveEntityrespawnPoint = createMushroom(type);

		} else if (clazz.contains("resources")) {
			passiveEntityrespawnPoint = createResource(type);

		} else if (clazz.contains("sheepfood")) {
			passiveEntityrespawnPoint = new SheepFood();

		} else if (clazz.contains("vegetable")) {
			passiveEntityrespawnPoint = createVegetable(type);

		} else if (clazz.contains("jewelry")) {
			passiveEntityrespawnPoint = createJewelry(type);

		} else if (clazz.contains("sign")) {
			/*
			 * Ignore signs. The way to go is XML.
			 */
			return null;

		} else if (clazz.contains("fruits")) {
			passiveEntityrespawnPoint = createFruit(type);

		} else if (clazz.contains("meat_and_fish")) {
			passiveEntityrespawnPoint = createMeatAndFish(type);

		} else if (clazz.contains("dairy")) {
			passiveEntityrespawnPoint = createDairy(type);
		}

		if (passiveEntityrespawnPoint == null) {
			logger.error("Unknown Entity (class/type: " + clazz + ":" + type
					+ ") at (" + x + "," + y + ") of " + id + " found");
			return null;
		}

		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createDairy(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("egg", 900);
			passiveEntityrespawnPoint
					.setDescription("If you were a hen, you'd like to drop an egg in this spot once in a while.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createMeatAndFish(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("meat", 100);
			passiveEntityrespawnPoint.setDescription("You see some leftovers of animal food. Might be meat.");
			break;
		case 1:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("ham", 100);
			passiveEntityrespawnPoint.setDescription("You see some leftovers of animal food. Looks like ham.");
			break;
		case 2:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("chicken", 100);
			passiveEntityrespawnPoint.setDescription("You see some leftovers of animal food. Maybe chicken?");
			break;
		case 3:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("roach", 100);
			passiveEntityrespawnPoint.setDescription("You see some shimmering fish scales like from a roach.");
			break;

		case 4:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("char", 100);
			passiveEntityrespawnPoint.setDescription("You see some red fish scales like from a char.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createFruit(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("coconut", 800);
			passiveEntityrespawnPoint
					.setDescription("You see a place where a coconut looks likely to fall.");
			break;
		case 1:
			passiveEntityrespawnPoint = new VegetableGrower("tomato");
			passiveEntityrespawnPoint
					.setDescription("You see a tomato plant.");
			break;
		case 2:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("pineapple", 1200);
			break;
		case 3:
			passiveEntityrespawnPoint = new VegetableGrower("watermelon");
			passiveEntityrespawnPoint
					.setDescription("You see a watermelon vine.");
			break;
		case 4:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("banana", 1000);
			passiveEntityrespawnPoint
					.setDescription("You see a place where a banana looks likely to fall.");
			break;

		case 5:
			passiveEntityrespawnPoint = new VegetableGrower("grapes", "There are no grapes on this vine yet.");
			passiveEntityrespawnPoint
					.setDescription("You see a grape vine.");
			break;
		/**
		case 5:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("grapes", 120);
			passiveEntityrespawnPoint
					.setDescription("You see a grape vine.");
			break;
			**/
		case 6:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("pear", 500);
			passiveEntityrespawnPoint
					.setDescription("You see a place where a pear looks likely to fall.");
			break;
		case 7:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("pomegranate", 800);
			passiveEntityrespawnPoint
					.setDescription("You see a place where a pomegranate looks likely to fall.");
			break;
		case 8:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("olive", 1200);
			passiveEntityrespawnPoint
					.setDescription("You see a place where an olive is likely to fall.");
			break;
		case 9:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("cherry", 1000);
			passiveEntityrespawnPoint
					.setDescription("You see a place where a cherry looks likely to fall.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createJewelry(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("carbuncle", 6000);
			passiveEntityrespawnPoint.setDescription("You see trace elements of some red crystal.");
			break;
		case 1:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("sapphire", 6000);
			passiveEntityrespawnPoint.setDescription("You see evidence of a sapphire stone being here recently.");
			break;
		case 2:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("emerald", 6000);
			passiveEntityrespawnPoint.setDescription("You see trace elements of the precious gem emerald.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createVegetable(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("apple", 500);
			passiveEntityrespawnPoint.setDescription("You see a place where an apple looks likely to fall.");
			break;
		case 1:
			passiveEntityrespawnPoint = new VegetableGrower("carrot");
			passiveEntityrespawnPoint.put("menu", "Pick|Use");
			break;
		case 2:
			passiveEntityrespawnPoint = new VegetableGrower("salad");
			break;
		case 3:
			passiveEntityrespawnPoint = new VegetableGrower("broccoli");
			break;
		case 4:
			passiveEntityrespawnPoint = new VegetableGrower("cauliflower");
			break;
		case 5:
			passiveEntityrespawnPoint = new VegetableGrower("chinese cabbage");
			break;
		case 6:
			passiveEntityrespawnPoint = new VegetableGrower("leek");
			break;
		case 7:
			passiveEntityrespawnPoint = new VegetableGrower("onion");
			break;
		case 8:
			passiveEntityrespawnPoint = new VegetableGrower("courgette");
			break;
		case 9:
			passiveEntityrespawnPoint = new VegetableGrower("spinach");
			break;
		case 10:
			passiveEntityrespawnPoint = new VegetableGrower("collard");
			break;
		case 11:
			passiveEntityrespawnPoint = new VegetableGrower("garlic");
			break;
		case 12:
			passiveEntityrespawnPoint = new VegetableGrower("artichoke");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createResource(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new VegetableGrower("wood", "There isn't anything to pick yet but wood might fall down soon.");
			passiveEntityrespawnPoint.put("menu", "Pick|Use");
			passiveEntityrespawnPoint.setDescription("You see a log shaped indent in the ground.");
			break;
		case 1:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("iron ore", 3000);
			passiveEntityrespawnPoint.setDescription("You see a small vein of iron ore.");
			break;

		case 2:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("gold bar", 9000);
			passiveEntityrespawnPoint.setDescription("You see a trace of a gold shimmer.");
			break;
		case 3:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("mithril bar", 16000);
			passiveEntityrespawnPoint.setDescription("You see a trace of a silvery shimmer.");
			break;
		case 4:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("gold nugget", 6000);
			passiveEntityrespawnPoint.setDescription("You see tiny gold shards.");
			break;
		case 5:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("mithril nugget", 12000);
			passiveEntityrespawnPoint.setDescription("You see tiny pieces of mithril ore.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;

		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createMushroom(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new VegetableGrower("button mushroom");
			passiveEntityrespawnPoint.setDescription("You notice a tiny button mushroom growing in the soil.");
			break;
		case 1:
			passiveEntityrespawnPoint = new VegetableGrower("porcini");
			passiveEntityrespawnPoint.setDescription("You notice a tiny porcini growing in the soil.");
			break;
		case 2:
			passiveEntityrespawnPoint = new VegetableGrower("toadstool");
			passiveEntityrespawnPoint.setDescription("You notice a tiny toadstool growing in the soil.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createHerb(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new VegetableGrower("arandula");
			break;
		case 1:
			passiveEntityrespawnPoint = new VegetableGrower("kekik");
			break;
		case 2:
			passiveEntityrespawnPoint = new VegetableGrower("sclaria");
			break;
		case 3:
			passiveEntityrespawnPoint = new VegetableGrower("mandragora");
			break;
		case 4:
			passiveEntityrespawnPoint = new PassiveEntityRespawnPoint("reindeer moss", 300);
			passiveEntityrespawnPoint
					.setDescription("You see a place where reindeer moss grows.");
			break;
		default:
			passiveEntityrespawnPoint = null;
			break;
		}
		return passiveEntityrespawnPoint;
	}

	private static PassiveEntityRespawnPoint createGrain(final int type) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint;
		switch (type) {
		case 0:
			passiveEntityrespawnPoint = new GrainField("grain", Arrays.asList("scythe", "old scythe", "black scythe"));
			break;

		case 1:
			passiveEntityrespawnPoint = new GrainField("sugar cane", Arrays.asList("sickle"));
			break;

		case 2:
			passiveEntityrespawnPoint = new GrainField("corn", Arrays.asList("sickle"));
			break;

		default:
			passiveEntityrespawnPoint = null;
			break;

		}
		return passiveEntityrespawnPoint;
	}
}
