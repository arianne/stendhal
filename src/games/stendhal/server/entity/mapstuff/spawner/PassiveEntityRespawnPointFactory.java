package games.stendhal.server.entity.mapstuff.spawner;

import marauroa.common.game.IRPZone.ID;

import org.apache.log4j.Logger;

/**
 * creates a PassiveEntityRespawnPoint.
 */
public class PassiveEntityRespawnPointFactory {
	private static Logger logger = Logger.getLogger(PassiveEntityRespawnPointFactory.class);

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
	 * @return PassiveEntityRespawnPoint or null in case some error occured
	 */
	public static PassiveEntityRespawnPoint create(String clazz, int type,
			ID id, int x, int y) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint = null;

		if (clazz.contains("herb")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"arandula", 400);
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"kekik", 800);
				break;
			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"sclaria", 800);
				break;
			}
		} else if (clazz.contains("corn")) {
			passiveEntityrespawnPoint = new GrainField();
		} else if (clazz.contains("mushroom")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"button_mushroom", 500);
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"porcini", 1000);
				break;
			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"toadstool", 1000);
				break;
			}
		} else if (clazz.contains("resources")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"wood", 1500);
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"iron_ore", 3000);
				// TODO: This is only a workaround. We should find a
				// better name than "plant grower", as we're also
				// using them for resources, teddies and whatever.
				// We should also consider making them non-clickable.
				passiveEntityrespawnPoint.setDescription("You see a small vein of iron ore.");
				break;
			}
		} else if (clazz.contains("sheepfood")) {
			passiveEntityrespawnPoint = new SheepFood();
		} else if (clazz.contains("vegetable")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"apple", 500);
				break;
			case 1:
				passiveEntityrespawnPoint = new VegetableGrower("carrot");
				break;
			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"salad", 1500);
				break;
			case 3:
				passiveEntityrespawnPoint = new VegetableGrower("broccoli");
				break;
			case 4:
				passiveEntityrespawnPoint = new VegetableGrower("cauliflower");
				break;
			case 5:
				passiveEntityrespawnPoint = new VegetableGrower(
						"chinese_cabbage");
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
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"spinach", 1500);
				break;
			case 10:
				passiveEntityrespawnPoint = new VegetableGrower("collard");
				break;
			}

		} else if (clazz.contains("sign")) {
			/*
			 * Ignore signs. The way to go is XML.
			 */
			return null;
		} else if (clazz.contains("fruits")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"coconut", 800);
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"tomato", 800);
				break;
			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"pineapple", 1200);
				break;

			}
		}

		if (passiveEntityrespawnPoint == null) {
			logger.error("Unknown Entity (class/type: " + clazz + ":" + type
					+ ") at (" + x + "," + y + ") of " + id + " found");
			return null;
		}

		return passiveEntityrespawnPoint;
	}
}
