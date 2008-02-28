package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.KeyedSlot;

import java.util.Arrays;
import java.util.List;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public abstract class UpdateConverter {

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

	/**
	 * Update old item names to the current naming.
	 *
	 * @param name
	 * @return 
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
    		name = transformItemName(name);

    		// rename some additional items to fix grammar in release 0.66
    		idx = ITEM_NAMES_OLD_0_66.indexOf(name);
    		if (idx != -1) {
    			name = ITEM_NAMES_NEW_0_66.get(idx);
    		}
		}

		return name;
	}

	/**
     * Updates a player RPObject from an old version of Stendhal.
     * 
     * @param object
     *            RPObject representing a player
     */
    static void updatePlayerRPObject(RPObject object) {
    	String[] slotsNormal = { "bag", "rhand", "lhand", "head", "armor",
    			"legs", "feet", "finger", "cloak", "bank", "bank_ados",
    			"zaras_chest_ados", "bank_fado", "bank_nalwor", "spells",
    			"keyring" };
    
    	String[] slotsSpecial = { "!quests", "!kills", "!buddy", "!ignore",
    			"!visited", "skills", "!tutorial", "!features" };
    
    	// Port from 0.03 to 0.10
    	if (!object.has("base_hp")) {
    		object.put("base_hp", "100");
    		object.put("hp", "100");
    	}
    
    	// Port from 0.13 to 0.20
    	if (!object.has("outfit")) {
    		object.put("outfit", new Outfit().getCode());
    	}
    
    	// create slots if they do not exist yet:
    
    	// Port from 0.20 to 0.30: bag, rhand, lhand, armor, head, legs, feet
    	// Port from 0.44 to 0.50: cloak, bank
    	// Port from 0.57 to 0.58: bank_ados, bank_fado
    	// Port from 0.58 to ?: bank_nalwor, keyring, finger
    	for (String slotName : slotsNormal) {
    		if (!object.hasSlot(slotName)) {
    			object.addSlot(new EntitySlot(slotName));
    		}
    	}
    
    	// Port from 0.44 to 0.50: !buddy
    	// Port from 0.56 to 0.56.1: !ignore
    	// Port from 0.57 to 0.58: skills
    	for (String slotName : slotsSpecial) {
    		if (!object.hasSlot(slotName)) {
    			object.addSlot(new KeyedSlot(slotName));
    		}
    		RPSlot slot = object.getSlot(slotName);
    		if (slot.size() == 0) {
    			RPObject singleObject = new RPObject();
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
    }

	/**
	 * Transform kill slot content to the new kill recording system.
	 */
	static void transformKillSlot(RPObject object) {
		RPObject kills = Player.getKeyedSlotObject(object, "!kills");

		if (kills != null) {
    		RPObject newKills = new RPObject();
    		for (String attr : kills) {
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

    		RPSlot slot = object.getSlot("!kills");
    		slot.remove(kills.getID());
    		slot.add(newKills);
		}
	}

	/**
	 * Replace underscores in the given String by spaces.
	 * This is used to replace underscore characters in compound item and creature names
	 * after loading data from the database.
	 * 
	 * @param name
	 * @return transformed String if name contained an underscore,
	 * 			or unchanged String object
	 * 			or null if name was null
	 */
	public static String transformItemName(String name) {
		if (name != null) {
			if (name.indexOf('_') != -1) {
				name = name.replace('_', ' ');
			}
		}

		return name;
	}

	/**
	 * Update the quest slot to the current version.
	 * @param player
	 */
	public static void updateQuests(Player player) {
		EntityManager entityMgr = SingletonRepository.getEntityManager();

		// rename old quest slot "Valo_concoct_potion" to "valo_concoct_potion"
		// We avoid to lose potion in case there is an entry with the old and the new name at the same
		// time by combining them by calculating the minimum of the two times and the sum of the two amounts.
		migrateSumTimedQuestSlot(player, "Valo_concoct_potion", "valo_concoct_potion");

		// From 0.66 to 0.67
		// update quest slot content, 
		// replace "_" with " ", for item/creature names
		for (String questSlot : player.getQuests()) {
			if (player.hasQuest(questSlot)) {
				String itemString = player.getQuest(questSlot);

				String[] parts = itemString.split(";");

				StringBuilder buffer = new StringBuilder();
				boolean first = true;

				for(int i=0; i<parts.length; ++i) {
					String oldName = parts[i];

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
    }

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
	private static void migrateSumTimedQuestSlot(Player player, String oldName, String newName) {
		String oldState = player.getQuest(oldName);

		if (oldState != null) {
			String questState = oldState;
			String newState = player.getQuest(newName);

			if (newState != null) {
				String[] oldParts = oldState.split(";");
				String[] newParts = newState.split(";");

				if (oldParts.length==3 && newParts.length==3) {
					try {
        				int oldAmount = Integer.parseInt(oldParts[0]);
        				int newAmount = Integer.parseInt(newParts[0]);
        				String oldItem = oldParts[1];
        				String newItem = newParts[1];
        				long oldTime = Long.parseLong(oldParts[2]);
        				long newTime = Long.parseLong(newParts[2]);

        				if (oldItem.equals(newItem)) {
        					newAmount += oldAmount;

        					if (oldTime < newTime) {
        						newTime = oldTime;
        					}

        					questState = Integer.toString(newAmount) + ';' + newItem + ';' + Long.toString(newTime);
        				}
        			} catch(NumberFormatException e) {
        				e.printStackTrace();
        			}
				}
			}

			player.setQuest(newName, questState);
			player.removeQuest(oldName);
		}
	}

}
