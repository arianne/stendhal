/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Handles the RPClass registration and updating old Player objects
 * created by an older version of Stendhal.
 */
class PlayerRPClass {

	/**
	 * Generates the RPClass and specifies slots and attributes.
	 */
	static void generateRPClass() {
		RPClass player = new RPClass("player");
		player.isA("rpentity");
		player.add("text", RPClass.LONG_STRING, RPClass.VOLATILE);
		player.add("private_text", RPClass.LONG_STRING, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));

		player.add("poisoned", RPClass.SHORT, RPClass.VOLATILE);
		player.add("eating", RPClass.SHORT, RPClass.VOLATILE);

		player.add("dead", RPClass.FLAG, RPClass.PRIVATE);

		player.add("outfit", RPClass.INT);
		player.add("outfit_org", RPClass.INT);

		// Use this for admin menus and usage.
		player.add("admin", RPClass.FLAG);
		player.add("adminlevel", RPClass.INT);
		player.add("invisible", RPClass.FLAG, RPClass.HIDDEN);
		player.add("ghostmode", RPClass.FLAG, RPClass.HIDDEN);

		player.add("release", RPClass.STRING, RPClass.HIDDEN);

		player.add("age", RPClass.INT);

		// Store sheep at DB
		player.addRPSlot("#flock", 1, RPClass.HIDDEN);
		player.add("sheep", RPClass.INT);

		// Bank system
		player.addRPSlot("bank", 20, RPClass.HIDDEN);

		// Kills recorder - needed for quest
		player.addRPSlot("!kills", 1, RPClass.HIDDEN);

		// We use this for the buddy system
		player.addRPSlot("!buddy", 1, RPClass.PRIVATE);
		player.addRPSlot("!ignore", 1, RPClass.HIDDEN);
		player.add("online", RPClass.LONG_STRING, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));
		player.add("offline", RPClass.LONG_STRING, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));

		player.addRPSlot("!quests", 1, RPClass.HIDDEN);
	}

	/**
	 * Updates a player RPObject from an old version of Stendhal.
	 *
	 * @param object RPObject representing a player
	 */
	static void updatePlayerRPObject(RPObject object) {
		String[] slotsNormal = { "bag", "rhand", "lhand", "head", "armor", "legs", "feet", "cloak", "bank" };

		String[] slotsSpecial = { "!quests", "!kills", "!buddy", "!ignore" };

		// Port from 0.03 to 0.10
		if (!object.has("base_hp")) {
			object.put("base_hp", "100");
			object.put("hp", "100");
		}

		// Port from 0.13 to 0.20
		if (!object.has("outfit")) {
			object.put("outfit", 0);
		}

		// create slots if they do not exist yet:

		//     Port from 0.20 to 0.30: bag, rhand, lhand, armor, head, legs, feet
		//     Port from 0.44 to 0.50: cloak, bank
		for (String slotName : slotsNormal) {
			if (!object.hasSlot(slotName)) {
				object.addSlot(new RPSlot(slotName));
			}
		}
		//     Port from 0.44 to 0.50: !buddy
		//     Port from 0.56 to 0.56.1: !ignore
		for (String slotName : slotsSpecial) {
			if (!object.hasSlot(slotName)) {
				object.addSlot(new RPSlot(slotName));
			}
			RPSlot slot = object.getSlot(slotName);
			if (slot.size() == 0) {
				RPObject singleObject = new RPObject();
				slot.assignValidID(singleObject);
				slot.add(singleObject);
			}
		}

		// Port from 0.30 to 0.35
		if (!object.has("atk_xp")) {
			object.put("atk_xp", "0");
			object.put("def_xp", "0");
		}

		if (object.has("devel")) {
			object.remove("devel");
		}

		// From 0.44 to 0.50
		if (!object.has("release")) {
			object.put("release", "0.00");
			object.put("atk", "10");
			object.put("def", "10");
		}

		if (!object.has("age")) {
			object.put("age", "0");
		}
	}

}
