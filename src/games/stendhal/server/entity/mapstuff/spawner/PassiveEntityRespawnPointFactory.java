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
	public static PassiveEntityRespawnPoint create(final String clazz, final int type,
			final ID id, final int x, final int y) {
		PassiveEntityRespawnPoint passiveEntityrespawnPoint = null;

		if (clazz.contains("herb")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new VegetableGrower(
						"arandula");
				break;
			case 1:
				passiveEntityrespawnPoint = new VegetableGrower(
						"kekik");
				break;
			case 2:
				passiveEntityrespawnPoint = new VegetableGrower(
						"sclaria");
				break;
			}
		} else if (clazz.contains("corn")) {
			passiveEntityrespawnPoint = new GrainField();
		} else if (clazz.contains("mushroom")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new VegetableGrower(
						"button mushroom");
				break;
			case 1:
				passiveEntityrespawnPoint = new VegetableGrower(
						"porcini");
				break;
			case 2:
				passiveEntityrespawnPoint = new VegetableGrower(
						"toadstool");
				break;
			}
		} else if (clazz.contains("resources")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"wood", 1500);
				passiveEntityrespawnPoint.setDescription("You see a log shaped indent in the ground.");
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"iron ore", 3000);
				passiveEntityrespawnPoint.setDescription("You see a small vein of iron ore.");
				break;

			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"gold bar", 9000);
				passiveEntityrespawnPoint.setDescription("You see a trace of a gold shimmer.");
				break;
			case 3:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"mithril bar", 16000);
				passiveEntityrespawnPoint.setDescription("You see a trace of a silvery shimmer.");
				break;
			case 4:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"gold nugget", 6000);
				passiveEntityrespawnPoint.setDescription("You see tiny gold shards.");
				break;
			case 5:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"mithril nugget", 12000);
				passiveEntityrespawnPoint.setDescription("You see tiny pieces of mithril ore.");
				break;	
				
			}
		} else if (clazz.contains("sheepfood")) {
			passiveEntityrespawnPoint = new SheepFood();
		} else if (clazz.contains("vegetable")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"apple", 500);
				passiveEntityrespawnPoint.setDescription("You see a place where an apple looks likely to fall.");
				break;
			case 1:
				passiveEntityrespawnPoint = new VegetableGrower("carrot");
				break;
			case 2:
				passiveEntityrespawnPoint = new VegetableGrower(
						"salad");
				break;
			case 3:
				passiveEntityrespawnPoint = new VegetableGrower("broccoli");
				break;
			case 4:
				passiveEntityrespawnPoint = new VegetableGrower("cauliflower");
				break;
			case 5:
				passiveEntityrespawnPoint = new VegetableGrower(
						"chinese cabbage");
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
				passiveEntityrespawnPoint = new VegetableGrower(
						"spinach");
				break;
			case 10:
				passiveEntityrespawnPoint = new VegetableGrower("collard");
				break;
			}

		} else if (clazz.contains("jewelry")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"carbuncle", 6000);
				passiveEntityrespawnPoint.setDescription("You see trace elements of some red crystal.");
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"sapphire", 6000);
				passiveEntityrespawnPoint.setDescription("You see evidence of a sapphire stone being here recently.");
				break;
			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"emerald", 6000);
				passiveEntityrespawnPoint.setDescription("You see trace elements of the precious gem emerald.");
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
				passiveEntityrespawnPoint.setDescription("You see a place where a coconut looks likely to fall.");
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
		} else if (clazz.contains("meat_and_fish")) {
			switch (type) {
			case 0:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"meat", 100);
				break;
			case 1:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"ham", 100);
				break;
			case 2:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"chicken", 100);
				break;
			case 3:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"roach", 100);
				break;

			case 4:
				passiveEntityrespawnPoint = new PassiveEntityRespawnPoint(
						"char", 100);
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
