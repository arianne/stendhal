/* $Id$ */
/***************************************************************************
 *					(C) Copyright 2003-2011 - Stendhal					   *
 ***************************************************************************
 ***************************************************************************
 *																		   *
 *	 This program is free software; you can redistribute it and/or modify  *
 *	 it under the terms of the GNU General Public License as published by  *
 *	 the Free Software Foundation; either version 2 of the License, or	   *
 *	 (at your option) any later version.								   *
 *																		   *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.ItemTools;
import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.common.constants.Testing;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.KeyedSlot;
import games.stendhal.server.entity.slot.PlayerSlot;
import marauroa.common.Pair;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * converts player objects to the most recent version by adding attributes,
 * transforming quest states and similar migrations.
 */
public abstract class UpdateConverter {
	private static Logger logger = Logger.getLogger(UpdateConverter.class);

	private static final List<String> ITEM_NAMES_OLD = Arrays.asList(
			"flail_+2", "leather_armor_+1", "leather_cuirass_+1",
			"chain_armor_+1", "scale_armor_+1", "chain_armor_+3",
			"scale_armor_+2", "twoside_axe_+3", "elf_cloak_+2", "mace_+1",
			"mace_+2", "hammer_+3", "chain_helmet_+2", "golden_helmet_+3",
			"longbow_+1", "lion_shield_+1"
	);
	private static final List<String> ITEM_NAMES_NEW = Arrays.asList(
			"morning star", "leather scale armor", "pauldroned leather cuirass",
			"enhanced chainmail", "iron scale armor", "golden chainmail",
			"pauldroned iron cuirass", "golden twoside axe", "blue elf cloak", "enhanced mace",
			"golden mace", "golden hammer", "aventail", "horned golden helmet",
			"composite bow", "enhanced lion shield"
	);

	private static final List<String> ITEM_NAMES_OLD_0_66 = Arrays.asList(
			"key golden", "key silver", "book black", "book blue",
			"duergar elder", "duergar black", "giant elder",
			"chaos sorceror"
	);
	private static final List<String> ITEM_NAMES_NEW_0_66 = Arrays.asList(
			"golden key", "silver key", "black book", "blue book",
			"elder duergar", "black duergar", "elder giant",
			"chaos sorcerer"
	);

	private static final HashMap<String, String> ZONE_MAPPING = new HashMap<>();
	static {
		ZONE_MAPPING.put("0_deniran_n_w2", "0_deniran_forest_n2_w");
		ZONE_MAPPING.put("0_deniran_nw", "0_deniran_forest_nw");
		ZONE_MAPPING.put("0_deniran_w", "0_deniran_city_w");
		ZONE_MAPPING.put("0_deniran_sw", "0_deniran_city_sw");
		ZONE_MAPPING.put("0_deniran_river_w", "0_deniran_river_sw");
		ZONE_MAPPING.put("0_deniran_n2", "0_deniran_forest_n2");
		ZONE_MAPPING.put("0_deniran_n", "0_deniran_forest_n");
		ZONE_MAPPING.put("0_deniran", "0_deniran_city");
		ZONE_MAPPING.put("0_deniran_s", "0_deniran_city_s");
		ZONE_MAPPING.put("0_deniran_river_s", "0_deniran_river_s");
		ZONE_MAPPING.put("0_deniran_n_e2", "0_deniran_forest_n2_e");
		ZONE_MAPPING.put("0_deniran_ne", "0_deniran_forest_ne");
		ZONE_MAPPING.put("0_deniran_e", "0_deniran_city_e");
		ZONE_MAPPING.put("0_deniran_se", "0_deniran_city_se");
		ZONE_MAPPING.put("0_deniran_river_e", "0_deniran_river_se");
		ZONE_MAPPING.put("0_deniran_wall_n2", "0_deniran_forest_n2_e2");
		ZONE_MAPPING.put("0_deniran_wall_n", "0_deniran_forest_n_e2");
		ZONE_MAPPING.put("0_deniran_wall", "0_deniran_city_e2");
		ZONE_MAPPING.put("0_deniran_wall_s", "0_deniran_city_s_e2");
		ZONE_MAPPING.put("0_deniran_river_e2", "0_deniran_river_s_e2");
		ZONE_MAPPING.put("-1_deniran_lost_caves_nw2", "-1_deniran_lost_caves_n2_w");
		ZONE_MAPPING.put("-1_deniran_lost_caves_nw", "-1_deniran_lost_caves_nw");
		ZONE_MAPPING.put("-1_deniran_caves_w", "-1_deniran_caves_w");
		ZONE_MAPPING.put("-1_deniran_caves_sw", "-1_deniran_caves_sw");
		ZONE_MAPPING.put("-1_deniran_lost_caves_n2", "-1_deniran_lost_caves_n2");
		ZONE_MAPPING.put("-1_deniran_lost_caves_n", "-1_deniran_lost_caves_n");
		ZONE_MAPPING.put("-1_deniran_caves_deniran", "-1_deniran_caves");
		ZONE_MAPPING.put("-1_deniran_caves_s", "-1_deniran_caves_s");
		ZONE_MAPPING.put("-1_deniran_lost_caves_ne2", "-1_deniran_lost_caves_n2_e");
		ZONE_MAPPING.put("-1_deniran_lost_caves_ne", "-1_deniran_lost_caves_ne");
		ZONE_MAPPING.put("-1_deniran_caves_e", "-1_deniran_caves_e");
		ZONE_MAPPING.put("-1_deniran_caves_se", "-1_deniran_caves_se");
		ZONE_MAPPING.put("-1_lost_caves_wall_n2", "-1_deniran_lost_caves_n2_e2");
		ZONE_MAPPING.put("-1_lost_caves_wall_n", "-1_deniran_lost_caves_n_e2");
		ZONE_MAPPING.put("-1_deniran_caves_wall", "-1_deniran_caves_e2");
		ZONE_MAPPING.put("-1_deniran_caves_wall_s", "-1_deniran_caves_s_e2");
		ZONE_MAPPING.put("-2_deniran_lost_caves_nw2", "-2_deniran_lost_caves_n2_w");
		ZONE_MAPPING.put("-2_deniran_caves_w", "-2_deniran_caves_w");
		ZONE_MAPPING.put("-2_deniran_caves_sw", "-2_deniran_caves_sw");
		ZONE_MAPPING.put("-2_deniran_lost_caves", "-2_deniran_lost_caves_n");
		ZONE_MAPPING.put("-2_deniran_caves_deniran", "-2_deniran_caves");
		ZONE_MAPPING.put("-2_deniran_caves_s", "-2_deniran_caves_s");
		ZONE_MAPPING.put("-2_deniran_lost_caves_ne2", "-2_deniran_lost_caves_n2_e");
		ZONE_MAPPING.put("-2_deniran_lost_caves_ne", "-2_deniran_lost_caves_ne");
		ZONE_MAPPING.put("-2_deniran_caves_e", "-2_deniran_caves_e");
		ZONE_MAPPING.put("-2_deniran_caves_se", "-2_deniran_caves_se");
		ZONE_MAPPING.put("-2_lost_caves_wall_n2", "-2_deniran_lost_caves_n2_e2");
		ZONE_MAPPING.put("-2_lost_caves_wall_n", "-2_deniran_lost_caves_n_e2");
		ZONE_MAPPING.put("-2_deniran_caves_wall", "-2_deniran_caves_e2");
		ZONE_MAPPING.put("-2_deniran_caves_wall_s", "-2_deniran_caves_s_e2");
		ZONE_MAPPING.put("-3_deniran_lost_caves_nw2", "-3_deniran_lost_caves_n2_w");
		ZONE_MAPPING.put("-3_deniran_lost_caves_nw", "-3_deniran_lost_caves_nw");
		ZONE_MAPPING.put("-3_deniran_caves_w", "-3_deniran_caves_w");
		ZONE_MAPPING.put("-3_deniran_caves_sw", "-3_deniran_caves_sw");
		ZONE_MAPPING.put("-3_deniran_lost_caves_n2", "-3_deniran_lost_caves_n2");
		ZONE_MAPPING.put("-3_deniran_lost_caves_n", "-3_deniran_lost_caves_n");
		ZONE_MAPPING.put("-3_deniran_caves_deniran", "-3_deniran_caves");
		ZONE_MAPPING.put("-3_deniran_caves_s", "-3_deniran_caves_s");
		ZONE_MAPPING.put("-3_deniran_lost_caves_ne2", "-3_deniran_lost_caves_n2_e");
		ZONE_MAPPING.put("-3_deniran_lost_caves_ne", "-3_deniran_lost_caves_ne");
		ZONE_MAPPING.put("-3_deniran_caves_e", "-3_deniran_caves_e");
		ZONE_MAPPING.put("-3_deniran_caves_se", "-3_deniran_caves_se");
		ZONE_MAPPING.put("-3_lost_caves_wall_n2", "-3_deniran_lost_caves_n2_e2");
		ZONE_MAPPING.put("-3_lost_caves_wall_n", "-3_deniran_lost_caves_n_e2");
		ZONE_MAPPING.put("-3_deniran_caves_wall", "-3_deniran_caves_e2");
		ZONE_MAPPING.put("-3_deniran_caves_wall_s", "-3_deniran_caves_s_e2");

	}

	/**
	 * quest name, quest index, creatures to kill.
	 */
	private static final HashMap<String, Pair<Integer, List<String>>> KILL_QUEST_NAMES;
	static {
		KILL_QUEST_NAMES = new HashMap<String, Pair<Integer, List<String>>>();
		KILL_QUEST_NAMES.put("meet_hayunn",
				new Pair<Integer, List<String>>(1, Arrays.asList(
					"rat")));

		KILL_QUEST_NAMES.put("clean_storage",
				new Pair<Integer, List<String>>(1, Arrays.asList(
					"rat",
					"caverat","snake")));

		KILL_QUEST_NAMES.put("club_thorns",
				new Pair<Integer, List<String>>(1, Arrays.asList(
					"mountain orc chief")));

		KILL_QUEST_NAMES.put("kill_dhohr_nuggetcutter",
				new Pair<Integer, List<String>>(1, Arrays.asList(
					"Dhohr Nuggetcutter",
					"mountain dwarf",
					"mountain elder dwarf",
					"mountain hero dwarf",
					"mountain leader dwarf")));

		KILL_QUEST_NAMES.put("kill_gnomes",
				new Pair<Integer, List<String>>(1, Arrays.asList(
					"gnome",
					"infantry gnome",
					"cavalryman gnome")));

		KILL_QUEST_NAMES.put("sad_scientist",
				new Pair<Integer, List<String>>(1, Arrays.asList(
					"Sergej Elos")));
	}


	/**
	 * Update old item names to the current naming.
	 *
	 * @param name
	 * @return the currentName of an Item
	 */
	public static String updateItemName(String name) {
		if (name != null) {
    		// handle renamed items
    		int idx = ITEM_NAMES_OLD.indexOf(name);
    		if (idx != -1) {
    			name = ITEM_NAMES_NEW.get(idx);
    		}

    		// Remove underscore characters from old database item names - ConversationParser
    		// is now capable to work with space separated item names.
    		name = ItemTools.itemNameToDisplayName(name);

    		// rename some additional items to fix grammar in release 0.66
    		idx = ITEM_NAMES_OLD_0_66.indexOf(name);
    		if (idx != -1) {
    			name = ITEM_NAMES_NEW_0_66.get(idx);
    		}
		}

		return name;
	}

	public static Item updateItem(String name) {
		// process the old keys for houses, now that we have change locks implemented
		Item item;
		if (name.startsWith("private key ")) {
			// which zone the house is in
			final String zoneName;
			final String doorId;
			// number tracks the lock changes
			final int number = 0;
			final String[] parts = name.split(" ");
			if (parts.length > 2) {
			   	try {
					// house number
					final int id;
					id = Integer.parseInt(parts[2]);
					if (id < 26) {
						zoneName = "kalavan";
					} else if (id < 50) {
						zoneName = "kirdneh";
					} else {
						zoneName = "ados";
					}
					doorId = zoneName + " house " + Integer.toString(id);
					// now set the infostring of the house key to doorId;number;
					item = SingletonRepository.getEntityManager().getItem("house key");
					((HouseKey) item).setup(doorId, number, null);
				} catch (final NumberFormatException e) {
					// shouldn't happen - give up and this will generate a warning
					item = SingletonRepository.getEntityManager().getItem(name);
				}
			} else {
				// shouldn't happen - give up and this will generate a warning
				item = SingletonRepository.getEntityManager().getItem(name);
			}
		} else {
			// item wasn't private key, just make it as normal
			item = SingletonRepository.getEntityManager().getItem(name);
		}
		return item;
	}

	/**
     * Updates a player RPObject from an old version of Stendhal.
     *
     * @param object
     *            RPObject representing a player
     */
    public static void updatePlayerRPObject(final RPObject object) {
    	final String[] slotsNormal = { "bag", "rhand", "lhand", "head", "armor",
    			"legs", "feet", "finger", "cloak", "bank", "bank_ados", "bank_deniran",
    			"zaras_chest_ados", "bank_fado", "bank_nalwor", "spells",
    			"keyring", /*"portfolio", */ "trade", "pouch" };

    	final String[] slotsSpecial = { "!quests", "!kills", "!buddy", "!ignore",
    			"!visited", "skills", "!tutorial"};

    	// Port from 0.03 to 0.10
    	if (!object.has("base_hp")) {
    		object.put("base_hp", "100");
    		object.put("hp", "100");
    	}

    	// Port from 0.13 to 0.20
    	final Outfit tempOutfit = new Outfit();
    	if (!object.has("outfit")) {
    		object.put("outfit", tempOutfit.getCode());
    	}

    	// create slots if they do not exist yet:

    	// Port from 0.20 to 0.30: bag, rhand, lhand, armor, head, legs, feet
    	// Port from 0.44 to 0.50: cloak, bank
    	// Port from 0.57 to 0.58: bank_ados, bank_fado
    	// Port from 0.58 to ?: bank_nalwor, keyring, finger
    	// Port from 1.29 to 1.30: bank_deniran
    	for (final String slotName : slotsNormal) {
    		if (!object.hasSlot(slotName)) {
    			object.addSlot(new PlayerSlot(slotName));
    		}
    	}

    	// Port from 0.44 to 0.50: !buddy
    	// Port from 0.56 to 0.56.1: !ignore
    	// Port from 0.57 to 0.58: skills
    	for (final String slotName : slotsSpecial) {
    		if (!object.hasSlot(slotName)) {
    			object.addSlot(new KeyedSlot(slotName));
    		}
    		final RPSlot slot = object.getSlot(slotName);
    		if (slot.size() == 0) {
    			final RPObject singleObject = new RPObject();
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

		if (Testing.COMBAT && !object.has("ratk_xp")) {
			object.put("ratk_xp", "0");
		}

    	if (!object.has("age")) {
    		object.put("age", "0");
    	}

    	if (!object.has("karma")) {
    		// A little beginner's luck
    		object.put("karma", 10);
    	}
    	if (!object.has("mana")) {
    		object.put("mana", 0);
    	}
    	if (!object.has("base_mana")) {
    		object.put("base_mana", 0);
    	}

    	// Renamed to skills
    	if (object.has("!skills")) {
    		object.remove("!skills");
    	}

    	if (!object.has("height")) {
    		object.put("height", 2);
    	}
    	if (!object.has("width")) {
    		object.put("width", 1);
    	}

    	// port to 0.66
    	transformKillSlot(object);

    	// port to 0.81 because of a bug in 0.80 which allowed 0 hp by double killing on logout during dying
    	if (object.getInt("hp") <= 0) {
    		logger.warn("Setting hp to 1 for player " + object);
    		object.put("hp", 1);
    	}

    	// port to 0.85 added buddy list as map - copy buddies to map
    	if (object.hasSlot("!buddy")) {
    		for (RPObject buddy : object.getSlot("!buddy")) {
    			for (final String buddyname : buddy) {
    				if (buddyname.startsWith("_")) {
    					boolean online = false;
    					if (buddy.get(buddyname).equals("1")) {
    						online = true;
    					}
    					//strip out the _ in the beginning
    					object.put("buddies", buddyname.substring(1), online);
    				}
    			}
    			buddy.remove("_db_id");
    		}
    		// remove buddy slot for 0.87
    		object.removeSlot("!buddy");
		}
		object.remove("buddies", "db_id");

		//port to 0.86 & 1.32: port keymap to feature map, karma_indicator as feature
		if (object.hasSlot("!features")) {
			if (KeyedSlotUtil.getKeyedSlot(object, "!features", "keyring") != null) {
				object.put("features", "keyring", "");
			}
			/*
			if (KeyedSlotUtil.getKeyedSlot(object, "!features", "portfolio") != null) {
				object.put("features", "portfolio", "");
			}
			*/

	    	// money pouch
			if (KeyedSlotUtil.getKeyedSlot(object, "!features", "pouch") != null) {
				object.put("features", "pouch");
			}

			object.removeSlot("!features");
		}
		if (KeyedSlotUtil.getKeyedSlot(object, "!quests", "learn_karma") != null) {
			object.put("features", "karma_indicator", "");
		}

		// port to 0.89: fix age
		if (object.has("age")) {
			if (!object.has("release") || (object.get("release").compareTo("0.88") <= 0)) {
				object.put("age", object.getInt("age") * 180 / 200);
			}
		}

		// port to 0.97: expire all temporary outfits
		if (object.has("outfit_org") && !object.has("outfit_expire_age")) {
			object.put("outfit_expire_age", 0);
		}

		// port to 1.31: zone zones
		transformVisitedSlot(object);

		// port to 1.39: keyring size
		String keyring = object.get("features", "keyring");
		if (keyring != null && keyring.equals("")) {
			object.put("features", "keyring", "2 4");
		}
	}


	/**
	 * Transform kill slot content to the new kill recording system.
	 * @param object
	 */
	static void transformKillSlot(final RPObject object) {
		final RPObject kills = KeyedSlotUtil.getKeyedSlotObject(object, "!kills");

		if (kills != null) {
    		final RPObject newKills = new RPObject();
    		for (final String attr : kills) {
    			// skip "id" entries
    			if (!attr.equals("id")) {
        			String newAttr = attr;
        			String value = kills.get(attr);

        			// Is it stored using the old recording system without an dot?
        			if (attr.indexOf('.') < 0) {
        				newAttr = updateItemName(newAttr);
        				newAttr = value + "." + newAttr;
        				value = "1";
        			}

        			newKills.put(newAttr, value);
    			}
    		}

    		final RPSlot slot = object.getSlot("!kills");
    		slot.remove(kills.getID());
    		slot.add(newKills);
		}
	}


	private static void transformVisitedSlot(RPObject object) {
		final RPObject visited = KeyedSlotUtil.getKeyedSlotObject(object, "!visited");

		if (visited != null) {
    		final RPObject newVisited = new RPObject();
    		for (final String attr : visited) {
    			if (!attr.equals("id")) {
        			String value = visited.get(attr);
        			String newName = ZONE_MAPPING.get(attr);
        			if (newName == null) {
        				newName = attr;
        			}
        			newVisited.put(newName, value);
    			}
    		}

    		final RPSlot slot = object.getSlot("!visited");
    		slot.remove(visited.getID());
    		slot.add(newVisited);
		}
	}


	public static void updateItemAttributes(Item item) {
		String name = item.getName();
		if (!name.equals("marked scroll") && !name.equals("deniran city scroll")) {
			return;
		}

		String infostring = item.getInfoString();

		// infostring is null in tests
		if (infostring == null) {
			return;
		}

		String[] location = infostring.split(" ");
		String zone = ZONE_MAPPING.get(location[0]);
		if (zone != null) {
			infostring = zone + " " + location[1] + " " + location[2];
			item.setInfoString(infostring);
		}
	}


	/**
	 * Update the quest slot to the current version.
	 * @param player
	 */
	public static void updateQuests(final Player player) {
		final EntityManager entityMgr = SingletonRepository.getEntityManager();

		// rename old quest slot "Valo_concoct_potion" to "valo_concoct_potion"
		// We avoid to lose potion in case there is an entry with the old and the new name at the same
		// time by combining them by calculating the minimum of the two times and the sum of the two amounts.
		migrateSumTimedQuestSlot(player, "Valo_concoct_potion", "valo_concoct_potion");

		// From 0.66 to 0.67
		// update quest slot content,
		// replace "_" with " ", for item/creature names
		for (final String questSlot : player.getQuests()) {

			if (player.hasQuest(questSlot)) {
				final String itemString = player.getQuest(questSlot);

				final String[] parts = itemString.split(";");

				final StringBuilder buffer = new StringBuilder();
				boolean first = true;

				for (int i = 0; i < parts.length; ++i) {
					final String oldName = parts[i];

					// Convert old item names to their new representation with correct grammar
					// and without underscores.
					String newName = UpdateConverter.updateItemName(oldName);

					// check for valid item and creature names if the update converter changed the name
					if (!newName.equals(oldName)) {
						if (!entityMgr.isCreature(newName) && !entityMgr.isItem(newName)) {
							newName = oldName;
						}
					}

					if (first) {
						buffer.append(newName);
						first = false;
					} else {
						buffer.append(';');
						buffer.append(newName);
					}
				}

				player.setQuest(questSlot, buffer.toString());
			}
		}

		// fix quest slots for kills quests.
		fixKillQuestsSlots(player);

		// fix DailyMonsterQuest slot
		fixDailyMonsterQuestSlot(player);

		// fix Maze
		fixMazeQuestSlot(player);

	}

	/**
	 * Convert keyring feature to keyring item. Moves the contents of the
	 * keyring to  the newly created item and removes the feature and the legacy
	 * slot.
	 *
	 * @param player converted player
	 */
	public static void updateKeyring(Player player) {
		if (player.getFeature("keyring") != null) {
			/*
			 * There are many things that could go wrong. Try to bail harmlessly
			 * if that happens. The old keyrings still work.
			 */
			Item keyring = SingletonRepository.getEntityManager().getItem("keyring");
			if (keyring == null) {
				logger.error("Failed to create keyring item");
				return;
			}
			keyring.setBoundTo(player.getName());
			if (!player.hasSlot("belt")) {
				player.addSlot(new PlayerSlot("belt"));
			}
			if (!player.hasSlot("back")) {
				player.addSlot(new PlayerSlot("back"));
			}
			/*
			 * Belt *should* be empty and working, as this code should not be
			 * called unless belt is activated, and old keyring feature can no
			 * longer be set on by the quest. It's best to check it anyway.
			 */
			if (!player.equip("belt", keyring)) {
				logger.error("Failed to place keyring in belt: " + player);
				return;
			}

			RPSlot oldSlot = player.getSlot("keyring");
			EntitySlot newSlot = keyring.getEntitySlot("content");
			if (!"keyring".equals(newSlot.getContentSlotName())) {
				logger.error("Keyring has incorrect slot name: "
						+ newSlot.getContentSlotName() + ", item is: " + keyring);
				return;
			}
			ArrayList<RPObject> contents = new ArrayList<RPObject>(oldSlot.size());
			for (RPObject item : oldSlot) {
				contents.add(item);
			}
			for (RPObject item : contents) {
				oldSlot.remove(item.getID());
				newSlot.add(item);
			}
			oldSlot.clear();
			// Remove the old feature. After this the player won't be able to
			// use the old style keyring, so everything would better be OK now.
			player.setFeature("keyring", false);
		}
	}

	private static void fixMazeQuestSlot(Player player) {
		final String QUEST_SLOT = "maze";

		// if player didnt started quest --> exit
		if(!player.hasQuest(QUEST_SLOT)) {
			return;
		}

		final String questSlot = player.getQuest(QUEST_SLOT);

		// if player's quest slot is already updated --> exit
		if(Arrays.asList(questSlot.split(";")).size()>1) {
			return;
		}

		player.setQuest(QUEST_SLOT, 0, "start");
		player.setQuest(QUEST_SLOT, 1, questSlot);
		player.setQuest(QUEST_SLOT, 2, "0");

	}

	private static void fixDailyMonsterQuestSlot(final Player player) {
		final String QUEST_SLOT = "daily";

		// if player didnt started quest, exiting
		if(!player.hasQuest(QUEST_SLOT)) {
			return;
		}

		final String questInfo = player.getQuest(QUEST_SLOT, 0);

		// if player completed quest, exiting
		if(questInfo.equals("done")) {
			return;
		}
		// if player already updated, exiting
		if(Arrays.asList(questInfo.split(",")).size()==5) {
			return;
		}

		// now fix player's quest slot
		player.setQuest(QUEST_SLOT, 0, player.getQuest(QUEST_SLOT, 0)+",0,1,0,0");
	}

	/**
	 * fix old-style kill quests slots.
	 * @param player - player which quest slots will fix.
	 */
	private static void fixKillQuestsSlots(final Player player) {
		for(String questSlot: KILL_QUEST_NAMES.keySet()) {
			// if player have no extra info in quest slot, we will add it :-)
			if(player.getQuest(questSlot)==null) {
				continue;
			}
			if(player.getQuest(questSlot).equals("start")) {
				final List<String> creatures = KILL_QUEST_NAMES.get(questSlot).second();
				StringBuilder sb=new StringBuilder("");
				for(int i=0; i<creatures.size(); i++) {
					sb.append(creatures.get(i)+",0,1,0,0,");
				}
				final String result = sb.toString();
				player.setQuest(questSlot,
						KILL_QUEST_NAMES.get(questSlot).first(),
						// will not record last semicolon.
						result.substring(0, result.length()-1));
			}
		}
	}

	// FIXME: Remove?
	 // update the name of a quest to the new spelling
//	private static void renameQuestSlot(Player player, String oldName, String newName) {
//		String questState = player.getQuest(oldName);
//
//		if (questState != null) {
//			player.setQuest(newName, questState);
//			player.removeQuest(oldName);
//		}
//	}

	 // update the name of a quest to the new spelling and accumulate the content
	private static void migrateSumTimedQuestSlot(final Player player, final String oldName, final String newName) {
		final String oldState = player.getQuest(oldName);

		if (oldState != null) {
			String questState = oldState;
			final String newState = player.getQuest(newName);

			if (newState != null) {
				final String[] oldParts = oldState.split(";");
				final String[] newParts = newState.split(";");

				if ((oldParts.length == 3) && (newParts.length == 3)) {
					try {
        				final int oldAmount = Integer.parseInt(oldParts[0]);
        				int newAmount = Integer.parseInt(newParts[0]);
        				final String oldItem = oldParts[1];
        				final String newItem = newParts[1];
        				final long oldTime = Long.parseLong(oldParts[2]);
        				long newTime = Long.parseLong(newParts[2]);

        				if (oldItem.equals(newItem)) {
        					newAmount += oldAmount;

        					if (oldTime < newTime) {
        						newTime = oldTime;
        					}

        					questState = Integer.toString(newAmount) + ';' + newItem + ';' + Long.toString(newTime);
        				}
        			} catch (final NumberFormatException e) {
        				e.printStackTrace();
        			}
				}
			}

			player.setQuest(newName, questState);
			player.removeQuest(oldName);
		}
	}



}
