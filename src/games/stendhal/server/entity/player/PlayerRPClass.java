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

import games.stendhal.common.Debug;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.slot.BankSlot;
import games.stendhal.server.entity.slot.Banks;
import games.stendhal.server.entity.slot.EntitySlot;
import games.stendhal.server.entity.slot.KeyedSlot;
import games.stendhal.server.entity.slot.PlayerSlot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Configuration;
import marauroa.common.game.Definition;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.io.UnicodeSupportingInputStreamReader;

import org.apache.log4j.Logger;

/**
 * Handles the RPClass registration and updating old Player objects created by
 * an older version of Stendhal.
 */
class PlayerRPClass {

	private static Logger logger = Logger.getLogger(PlayerRPClass.class);

	/** list of super admins read from admins.list */
	private static List<String> adminNames;

	/** only log the first exception while reading welcome URL */
	private static boolean firstWelcomeException = true;

	/** these items should be bound */
	private static final List<String> ITEMS_TO_BIND = Arrays.asList(
			"dungeon_silver_key", "lich_gold_key", "trophy_helmet",
			"lucky_charm", "soup");

	/*
	 *
	 * leather_armor_+1 leather_scale_armor leather_cuirass_+1
	 * pauldroned_leather_cuirass chain_armor_+1 enhanced_chainmail
	 * scale_armor_+1 iron_scale_armor chain_armor_+3 golden_chainmail
	 * scale_armor_+2 pauldroned_iron_cuirass twoside_axe_+3 golden_twoside_axe
	 * elf_cloak_+2 blue_elf_cloak mace_+1 enhanced_mace mace_+2 golden_mace
	 * hammer_+3 golden_hammer chain_helmet_+2 aventail golden_helmet_+3
	 * horned_golden_helmet longbow_+1 composite_bow lion_shield_+1
	 * enhanced_lion_shield
	 */
	private static final List<String> ITEM_NAMES_OLD = Arrays.asList(
			"flail_+2", "leather_armor_+1", "leather_cuirass_+1",
			"chain_armor_+1", "scale_armor_+1", "chain_armor_+3",
			"scale_armor_+2", "twoside_axe_+3", "elf_cloak_+2", "mace_+1",
			"mace_+2", "hammer_+3", "chain_helmet_+2", "golden_helmet_+3",
			"longbow_+1", "lion_shield_+1");

	private static final List<String> ITEM_NAMES_NEW = Arrays.asList(
			"morning_star", "leather_scale_armor",
			"pauldroned_leather_cuirass", "enhanced_chainmail",
			"iron_scale_armor", "golden_chainmail", "pauldroned_iron_cuirass",
			"golden_twoside_axe", "blue_elf_cloak", "enhanced_mace",
			"golden_mace", "golden_hammer", "aventail", "horned_golden_helmet",
			"composite_bow", "enhanced_lion_shield");

	/**
	 * Generates the RPClass and specifies slots and attributes.
	 */
	static void generateRPClass() {
		RPClass player = new RPClass("player");
		player.isA("rpentity");
		player.addAttribute("text", Type.LONG_STRING, Definition.VOLATILE);
		
		player.addRPEvent("private_text", Definition.PRIVATE);

		player.addAttribute("poisoned", Type.SHORT, Definition.VOLATILE);
		player.addAttribute("eating", Type.SHORT, Definition.VOLATILE);

		player.addAttribute("dead", Type.FLAG, Definition.PRIVATE);

		player.addAttribute("outfit", Type.INT);
		player.addAttribute("outfit_org", Type.INT);
		// player.addAttribute("outfit_path", Type.STRING);

		player.addAttribute("away", Type.LONG_STRING, Definition.VOLATILE);
		player.addAttribute("grumpy", Type.LONG_STRING, Definition.VOLATILE);

		// Use this for admin menus and usage.
		player.addAttribute("admin", Type.FLAG);
		player.addAttribute("adminlevel", Type.INT);
		player.addAttribute("invisible", Type.FLAG, Definition.HIDDEN);
		player.addAttribute("ghostmode", Type.FLAG);
		player.addAttribute("teleclickmode", Type.FLAG);

		player.addAttribute("release", Type.STRING, Definition.PRIVATE);

		player.addAttribute("age", Type.INT);

		// Store sheep at DB
		player.addRPSlot("#flock", 1, Definition.HIDDEN);
		player.addAttribute("sheep", Type.INT);

		// Store pets at DB
		player.addRPSlot("#pets", 1, Definition.HIDDEN);
		player.addAttribute("pet", Type.INT);
		player.addAttribute("cat", Type.INT);
		player.addAttribute("baby_dragon", Type.INT);

		// Bank system
		player.addRPSlot("bank", 30, Definition.HIDDEN);
		player.addRPSlot("bank_ados", 30, Definition.HIDDEN);
		player.addRPSlot("zaras_chest_ados", 30, Definition.HIDDEN);
		player.addRPSlot("bank_fado", 30, Definition.HIDDEN);
		player.addRPSlot("bank_nalwor", 30, Definition.HIDDEN);

		// Kills recorder - needed for quest
		player.addRPSlot("!kills", 1, Definition.HIDDEN);

		// We use this for the buddy system
		player.addRPSlot("!buddy", 1, Definition.PRIVATE);
		player.addRPSlot("!ignore", 1, Definition.HIDDEN);
		player.addAttribute("online", Type.LONG_STRING,
				(byte) (Definition.PRIVATE | Definition.VOLATILE));
		player.addAttribute("offline", Type.LONG_STRING,
				(byte) (Definition.PRIVATE | Definition.VOLATILE));

		player.addRPSlot("!quests", 1, Definition.HIDDEN);
		player.addRPSlot("!tutorial", 1, Definition.HIDDEN);

		player.addAttribute("karma", Type.FLOAT, Definition.PRIVATE);

		player.addRPSlot("skills", 1, Definition.HIDDEN);

		// Non-removable while stored ones have values
		player.addRPSlot("!skills", 1,
				(byte) (Definition.HIDDEN | Definition.VOLATILE));

		player.addRPSlot("!visited", 1, Definition.HIDDEN);

		// This is the RPSlot for the spells. It's main purpose is to let us add
		// a GUI for the spells later on.
		player.addRPSlot("spells", 9, Definition.PRIVATE);

		// The guild name
		player.addAttribute("guild", Type.STRING);

		// Player features
		player.addRPSlot("!features", 1, Definition.PRIVATE);

		// Last time this player attacked another player
		player.addAttribute("last_pvp_action_time", Type.FLOAT, Definition.HIDDEN);
		
		player.addRPEvent("transition_graph", Definition.STANDARD);
		player.addRPEvent("examine", Definition.STANDARD);
		
	    generateRPEvent();		
	}

    private static void generateRPEvent() {
		RPClass rpclass = new RPClass("private_text");
		rpclass.add(DefinitionClass.RPEVENT, "text", Type.LONG_STRING, Definition.PRIVATE);
		rpclass.add(DefinitionClass.RPEVENT, "texttype", Type.STRING, Definition.PRIVATE);

		rpclass = new RPClass("examine");
        rpclass.add(DefinitionClass.RPEVENT, "path", Type.STRING, Definition.PRIVATE);
        rpclass.add(DefinitionClass.RPEVENT, "alt", Type.STRING, Definition.PRIVATE);
        rpclass.add(DefinitionClass.RPEVENT, "title", Type.STRING, Definition.PRIVATE);
        rpclass.add(DefinitionClass.RPEVENT, "text", Type.LONG_STRING, Definition.PRIVATE);
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

	}

	/**
	 * reads the admins from admins.list
	 *
	 * @param player
	 *            Player to check for super admin status.
	 */
	static void readAdminsFromFile(Player player) {
		if (adminNames == null) {
			adminNames = new LinkedList<String>();

			String adminFilename = "data/conf/admins.list";

			try {
				InputStream is = player.getClass().getClassLoader().getResourceAsStream(
						adminFilename);

				if (is == null) {
					logger.info("data/conf/admins.list does not exist.");
				} else {

					BufferedReader in = new BufferedReader(
							new UnicodeSupportingInputStreamReader(is));
					try {
						String line;
						while ((line = in.readLine()) != null) {
							adminNames.add(line);
						}
					} catch (Exception e) {
						logger.error("Error loading admin names from: "
								+ adminFilename, e);
					}
					in.close();
				}
			} catch (Exception e) {
				logger.error(
						"Error loading admin names from: " + adminFilename, e);
			}
		}

		boolean isAdmin = adminNames.contains(player.getName());

		if (isAdmin) {
			player.setAdminLevel(AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPER);
		} else {
			// TODO: Needed? Player should be fine without it
			if (!player.has("adminlevel")) {
				player.put("adminlevel", "0");
			}
		}
	}

	public static final String DEFAULT_ENTRY_ZONE = "int_semos_townhall";

	/**
	 * Places the player (and his/her sheep if there is one) into the world on
	 * login
	 *
	 * @param object
	 *            RPObject representing the player
	 * @param player
	 *            Player-object
	 */
	static void placePlayerIntoWorldOnLogin(RPObject object, Player player) {
		StendhalRPZone zone = null;

		try {
			if (object.has("zoneid") && object.has("x") && object.has("y")) {
				if (!object.get("release").equals(Debug.VERSION)) {
					player.put("release", Debug.VERSION);
				} else {
					zone = StendhalRPWorld.get().getZone(object.get("zoneid"));
				}
			}
		} catch (RuntimeException e) {
			// TODO: Is this catch needed?
			//
			// If placing the player at its last position
			// fails, we reset to default zone
			logger.warn(
					"Cannot place player at its last position. Using default",
					e);
		}

		if (zone != null) {
			/*
			 * Put the player in their zone (use placeat() for collision rules)
			 */
			if (!StendhalRPAction.placeat(zone, player, player.getX(),
					player.getY())) {
				logger.warn("Cannot place player at their last position: "
						+ player.getName());
				zone = null;
			}
		}

		if (zone == null) {
			/*
			 * Fallback to default zone
			 */
			zone = StendhalRPWorld.get().getZone(DEFAULT_ENTRY_ZONE);

			if (zone == null) {
				logger.error("Unable to locate default zone ["
						+ DEFAULT_ENTRY_ZONE + "]");
				return;
			}

			zone.placeObjectAtEntryPoint(player);
		}

		// load sheep
		Sheep sheep = player.getPlayerSheepManager().retrieveSheep();

		if (sheep != null) {
			logger.debug("Player has a sheep");

			// TODO: Is this needed?
			if (!sheep.has("base_hp")) {
				sheep.initHP(10);
			}

			if (placeAnimalIntoWorld(sheep, player)) {
				player.setSheep(sheep);
			} else {
				logger.warn("Could not place sheep: " + sheep);
				player.sendPrivateText("You can not seem to locate your "
						+ sheep.getTitle() + ".");
			}

			sheep.notifyWorldAboutChanges();
		}

		// load pet
		Pet pet = player.getPlayerPetManager().retrievePet();

		if (pet != null) {
			logger.debug("Player has a pet");

			// TODO: Is this needed?
			if (!pet.has("base_hp")) {
				pet.initHP(200);
			}

			if (placeAnimalIntoWorld(pet, player)) {
				player.setPet(pet);
			} else {
				logger.warn("Could not place pet: " + pet);
				player.sendPrivateText("You can not seem to locate your "
						+ pet.getTitle() + ".");
			}

			pet.notifyWorldAboutChanges();
		}

		player.notifyWorldAboutChanges();
		StendhalRPAction.transferContent(player);
	}

	/**
	 * Places a domestic animal in the world. If it matches it's owner's zone,
	 * then try to keep it's position.
	 *
	 * @param animal
	 *            The domestic animal.
	 * @param player
	 *            The owner.
	 *
	 * @return <code>true</code> if placed.
	 */
	protected static boolean placeAnimalIntoWorld(final DomesticAnimal animal,
			final Player player) {
		StendhalRPZone playerZone = player.getZone();

		/*
		 * Only add directly if required attributes are present
		 */
		if (animal.has("zoneid") && animal.has("x") && animal.has("y")) {
			StendhalRPZone zone = StendhalRPWorld.get().getZone(
					animal.get("zoneid"));

			/*
			 * Player could have been forced to change zones
			 */
			if (zone == playerZone) {
				if (StendhalRPAction.placeat(zone, animal, animal.getX(),
						animal.getY())) {
					return true;
				}
			}
		}

		return StendhalRPAction.placeat(playerZone, animal, player.getX(),
				player.getY());
	}

	/**
	 * Loads the items into the slots of the player on login.
	 *
	 * @param player
	 *            Player
	 */
	static void loadItemsIntoSlots(Player player) {

		// load items
		String[] slotsItems = { "bag", "rhand", "lhand", "head", "armor",
				"legs", "feet", "finger", "cloak", "keyring" };

		try {
			for (String slotName : slotsItems) {
				RPSlot slot = player.getSlot(slotName);
				RPSlot newSlot = new PlayerSlot(slotName);
				loadSlotContent(player, slot, newSlot);
			}

			for (Banks bank : Banks.values()) {
				RPSlot slot = player.getSlot(bank.getSlotName());
				RPSlot newSlot = new BankSlot(bank);
				loadSlotContent(player, slot, newSlot);
			}
		} catch (RuntimeException e) {
			logger.error("cannot create player", e);
		}
	}

	/**
	 * Loads the items into the slots of the player on login.
	 *
	 * @param player
	 *            Player
	 * @param slot
	 *            original slot
	 * @param newSlot
	 *            new Stendhal specific slot
	 */
	private static void loadSlotContent(Player player, RPSlot slot, RPSlot newSlot) {
		StendhalRPWorld world = StendhalRPWorld.get();
		List<RPObject> objects = new LinkedList<RPObject>();
		for (RPObject objectInSlot : slot) {
			objects.add(objectInSlot);
		}
		slot.clear();
		player.removeSlot(slot.getName());
		player.addSlot(newSlot);

		for (RPObject item : objects) {
			try {
				// We simply ignore corpses...
				if (item.get("type").equals("item")) {
					// TODO: Move to Item.create(RPObject)?

					// handle renamed items
					String name = item.get("name");
					if (ITEM_NAMES_OLD.indexOf(name) > -1) {
						name = ITEM_NAMES_NEW.get(ITEM_NAMES_OLD.indexOf(name));
					}

					Item entity = world.getRuleManager().getEntityManager().getItem(
							name);

					// log removed items
					if (entity == null) {
						int quantity = 1;
						if (item.has("quantity")) {
							quantity = item.getInt("quantity");
						}
						logger.warn("Cannot restore " + quantity + " " + name
								+ " on login of " + player.getName()
								+ " because this item"
								+ " was removed from items.xml");
						continue;
					}

					entity.setID(item.getID());

					if (item.has("persistent")
							&& (item.getInt("persistent") == 1)) {
						/*
						 * Keep [new] rpclass
						 */
						RPClass rpclass = entity.getRPClass();
						entity.fill(item);
						entity.setRPClass(rpclass);
					}

					if (entity instanceof StackableItem) {
						int quantity = 1;
						if (item.has("quantity")) {
							quantity = item.getInt("quantity");
						} else {
							logger.warn("Adding quantity=1 to "
									+ item
									+ ". Most likely cause is that this item was not stackable in the past");
						}
						((StackableItem) entity).setQuantity(quantity);

						if (quantity <= 0) {
							logger.warn("Ignoring item "
									+ name
									+ " on login of player "
									+ player.getName()
									+ " because this item has an invalid quantity: "
									+ quantity);
							continue;
						}
					}

					// make sure saved individual information is
					// restored
					String[] individualAttributes = { "infostring",
							"description", "bound", "undroppableondeath" };
					for (String attribute : individualAttributes) {
						if (item.has(attribute)) {
							entity.put(attribute, item.get(attribute));
						}
					}

					boundOldItemsToPlayer(player, entity);

					newSlot.add(entity);
				} else {
					logger.warn("Non-item object found in " + player.getName()
							+ "[" + slot.getName() + "]: " + item);
				}
			} catch (Exception e) {
				logger.error("Error adding " + item + " to player slot" + slot,
						e);
			}
		}
	}

	/**
	 * binds special items to the player.
	 *
	 * @param player
	 *            Player
	 * @param item
	 *            Item
	 */
	private static void boundOldItemsToPlayer(Player player, Item item) {

		// No special processing needed, if the item is already bound
		if (item.getBoundTo() != null) {
			return;
		}

		if (ITEMS_TO_BIND.contains(item.getName())) {
			item.setBoundTo(player.getName());
		}
	}

	/**
	 * send a welcome message to the player which can be configured in
	 * marauroa.ini file as "server_welcome". If the value is an http:// adress,
	 * the first line of that adress is read and used as the message
	 *
	 * @param player
	 *            Player
	 */
	static void welcome(Player player) {
		String msg = "This release is EXPERIMENTAL. Please report problems, suggestions and bugs. You can find us at IRC irc.freenode.net #arianne. Note: remember to keep your password completely secret, never tell it to another friend, player, or even admin.";
		try {
			Configuration config = Configuration.getConfiguration();
			if (config.has("server_welcome)")) {
				msg = config.get("server_welcome");
				if (msg.startsWith("http://")) {
					URL url = new URL(msg);
					HttpURLConnection.setFollowRedirects(false);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(connection.getInputStream()));
					msg = br.readLine();
					br.close();
					connection.disconnect();
				}
			}
		} catch (Exception e) {
			if (PlayerRPClass.firstWelcomeException) {
				logger.warn("Can't read server_welcome from marauroa.ini", e);
				PlayerRPClass.firstWelcomeException = false;
			}
		}
		if (msg != null) {
			player.sendPrivateText(msg);
		}
	}

}
