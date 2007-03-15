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
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Configuration;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Handles the RPClass registration and updating old Player objects
 * created by an older version of Stendhal.
 */
class PlayerRPClass {
	private static Logger logger = Logger.getLogger(PlayerRPClass.class);

	/** list of super admins read from admins.list */
	private static List<String> adminNames = null;

	/** only log the first exception while reading welcome URL */
	private static boolean firstWelcomeException = true;

	/** these items should be bound */
	private static final List<String> itemsToBind = Arrays.asList(
					"dungeon_silver_key", "lich_gold_key",  
					"golden_armor", "golden_boots", "golden_helmet", "golden_legs", "golden_shield", 
					"steel_boots", "trophy_helmet",
					"marked_scroll");

	private static final List<String> itemNamesOld = Arrays.asList("flail_+2");
	private static final List<String> itemNamesNew = Arrays.asList("morning_star");

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
		player.addRPSlot("bank_ados", 20, RPClass.HIDDEN);
		player.addRPSlot("bank_fado", 20, RPClass.HIDDEN);

		// Kills recorder - needed for quest
		player.addRPSlot("!kills", 1, RPClass.HIDDEN);

		// We use this for the buddy system
		player.addRPSlot("!buddy", 1, RPClass.PRIVATE);
		player.addRPSlot("!ignore", 1, RPClass.HIDDEN);
		player.add("online", RPClass.LONG_STRING, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));
		player.add("offline", RPClass.LONG_STRING, (byte) (RPClass.PRIVATE | RPClass.VOLATILE));

		player.addRPSlot("!quests", 1, RPClass.HIDDEN);

		player.add("karma", RPClass.FLOAT, RPClass.PRIVATE);

		player.addRPSlot("skills", 1, RPClass.HIDDEN);

		// Non-removable while stored ones have values
		player.addRPSlot("!skills", 1, RPClass.HIDDEN);
                
                // Mana/Magic System
                player.add("mana", RPClass.INT);
                player.add("base_mana", RPClass.INT);
	}

	/**
	 * Updates a player RPObject from an old version of Stendhal.
	 *
	 * @param object RPObject representing a player
	 */
	static void updatePlayerRPObject(RPObject object) {
		String[] slotsNormal = {
			"bag", "rhand", "lhand", "head", "armor", "legs",
			"feet", "cloak", "bank", "bank_ados", "bank_fado"
		};

		String[] slotsSpecial = {
			"!quests", "!kills", "!buddy", "!ignore", "skills"
		};

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
		//     Port from 0.57 to 0.58: bank_ados, bank_fado
		for (String slotName : slotsNormal) {
			if (!object.hasSlot(slotName)) {
				object.addSlot(new RPSlot(slotName));
			}
		}
		//     Port from 0.44 to 0.50: !buddy
		//     Port from 0.56 to 0.56.1: !ignore
		//     Port from 0.57 to 0.58: skills
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

		if(!object.has("karma")) {
			// A little beginner's luck
			object.put("karma", 10);
		}
                if(!object.has("mana")) {
                    // Give the new users some mana to use... Can be set later..
                    object.put("mana", 100);
                }
                if (!object.has("base_mana")) {
                    // the first base mana stat
                    object.put("base_mana", 100);
                }

		// Renamed to skills
		if(object.has("!skills")) {
			object.remove("!skills");
		}
	}

	/**
	 * reads the admins from admins.list
	 *
	 * @param player Player to check for super admin status.
	 */
	static void readAdminsFromFile(Player player) {
		if (adminNames == null) {
			adminNames = new LinkedList<String>();
			
			String adminFilename="data/conf/admins.list";

			try {
				InputStream is = player.getClass().getClassLoader()
						.getResourceAsStream(adminFilename);

				if (is == null) {
					logger.info("data/conf/admins.list does not exist.");
				} else {
					
					BufferedReader in = new BufferedReader(
							new InputStreamReader(is));
					try {
						String line;
						while ((line = in.readLine()) != null) {
							adminNames.add(line);
						}
					} catch (Exception e) {
						logger.error("Error loading admin names from: "+adminFilename, e);
					}
					in.close();
				}
			} catch (Exception e) {
				logger.error("Error loading admin names from: "+adminFilename, e);
			}
		}

		boolean isAdmin = adminNames.contains(player.getName());

		if (isAdmin) {
			player.put("adminlevel", AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPER);
		} else {
			if (!player.has("adminlevel")) {
				player.put("adminlevel", "0");
			}
		}
	}

	/**
	 * Places the player (and his/her sheep if there is one) into the world on login
	 *
	 * @param object RPObject representing the player
	 * @param player Player-object
	 */
	static void placePlayerIntoWorldOnLogin(RPObject object, Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();

		boolean firstVisit = false;

		try {
			if (!object.has("zoneid") || !object.has("x") || !object.has("y")) {
				firstVisit = true;
			}

			boolean newReleaseHappened = !object.get("release").equals(
					Debug.VERSION);
			if (newReleaseHappened) {
				firstVisit = true;
				player.put("release", Debug.VERSION);
			}
			
			IRPZone tempZone = StendhalRPWorld.get().getRPZone(new IRPZone.ID(object.get("zoneid")));
			if (tempZone == null) {
				firstVisit = true;
			}

			if (firstVisit) {
				player.put("zoneid", "int_semos_townhall");
			}

			world.add(player);
		} catch (Exception e) { // If placing the player at its last position
								// fails, we reset it to city entry point
			logger.warn("cannot place player at its last position. reseting to semos city entry point",	e);

			firstVisit = true;
			player.put("zoneid", "int_semos_townhall");

			player.notifyWorldAboutChanges();
		}

		StendhalRPAction.transferContent(player);

		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());

		if (firstVisit) {
			zone.placeObjectAtEntryPoint(player);
		}

		int x = player.getX();
		int y = player.getY();

		// load sheep
		try {
			if (player.hasSheep()) {
				logger.debug("Player has a sheep");
				Sheep sheep = player.retrieveSheep();
				sheep.put("zoneid", object.get("zoneid"));
				if (!sheep.has("base_hp")) {
					sheep.put("base_hp", "10");
					sheep.put("hp", "10");
				}

				world.add(sheep);

				x = sheep.getX();
				y = sheep.getY();
				player.setSheep(sheep);

				StendhalRPAction.placeat(zone, sheep, x, y);
				zone.addPlayerAndFriends(sheep);
			}
		} catch (Exception e) { /**
								 * No idea how but some players get a sheep but
								 * they don't have it really. Me thinks that it
								 * is a player that has been running for a while
								 * the game and was kicked of server because
								 * shutdown on a pre 1.00 version of Marauroa.
								 * We shouldn't see this anymore.
								 */
			logger.error("Pre 1.00 Marauroa sheep bug. (player = "
					+ player.getName() + ")", e);

			if (player.has("sheep")) {
				player.remove("sheep");
			}

			if (player.hasSlot("#flock")) {
				player.removeSlot("#flock");
			}
		}

		StendhalRPAction.placeat(zone, player, x, y);
		zone.addPlayerAndFriends(player);

	}

	/**
	 * Loads the items into the slots of the player on login.
	 *
	 * @param player Player
	 */
	static void loadItemsIntoSlots(Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();

		// load items
		String[] slotsItems = {
			"bag", "rhand", "lhand", "head", "armor", "legs",
			"feet", "cloak", "bank", "bank_ados", "bank_fado" };

		for (String slotName : slotsItems) {
			try {
				if (player.hasSlot(slotName)) {
					RPSlot slot = player.getSlot(slotName);

					List<RPObject> objects = new LinkedList<RPObject>();
					for (RPObject objectInSlot : slot) {
						objects.add(objectInSlot);
					}
					slot.clear();

					for (RPObject item : objects) {
						try {
							// We simply ignore corpses...
							if (item.get("type").equals("item")) {

								// handle renamed items
								String name = item.get("name");
								if (itemNamesOld.indexOf(name) > -1) {
									name = itemNamesNew.get(itemNamesOld.indexOf(name));
								}

								Item entity = world.getRuleManager()
										.getEntityManager().getItem(
												name);

								// log removed items
								if (entity == null) {
									int quantity = 1;
									if (item.has("quantity")) {
										quantity = item.getInt("quantity");
									}
									logger.warn("Cannot restore " + quantity + " " + item.get("name")
											+ " on login of " + player.get("name") + " because this item"
											+ " was removed from items.xml");
									continue;
								}

								entity.setID(item.getID());

								if(item.has("persistent") && (item.getInt("persistent")==1)) {
									entity.fill(item);
								}

								if (entity instanceof StackableItem) {
									int quantity = 1;
									if (item.has("quantity")) {
										quantity = item.getInt("quantity");
									} else {
										logger.warn("Adding quantity=1 to " + item + ". Most likly cause is that this item was not stackable in the past");
									}
									((StackableItem) entity).setQuantity(quantity);
								}
								
								// make sure saved individual information is
								// restored
								String[] individualAttributes = {"infostring", "description", "bound"};
								for (String attribute : individualAttributes) {
									if (item.has(attribute)) {
										entity.put(attribute, item.get(attribute));
									}
								}
								
								boundOldItemsToPlayer(player, entity);

								slot.add(entity);
							}
						} catch (Exception e) {
							logger.error("Error adding " + item
									+ " to player slot" + slot, e);
						}
					}
				} else {
					logger.warn("player " + player.getName()
							+ " does not have the slot " + slotName);
				}
			} catch (RuntimeException e) {
				logger.error("cannot create player", e);
				if (player.hasSlot(slotName)) {
					RPSlot slot = player.getSlot(slotName);
					slot.clear();
				}
			}
		}
	}

	/**
	 * binds special items to the player.
	 *
	 * @param player Player
	 * @param item Item
	 */
	private static void boundOldItemsToPlayer(Player player, Item item) {

		// No special processing needed, if the item is already bound
		if (item.has("bound")) {
			return;
		}

		if (itemsToBind.contains(item.getName())) {
			item.put("bound", player.getName());
		}
	}

	/** 
	 * send a welcome message to the player which can be configured
	 * in marauroa.ini file as "server_welcome". If the value is
	 * an http:// adress, the first line of that adress is read
	 * and used as the message
	 *
	 * @param player Player
	 */
	static void welcome(Player player) {
		String msg = "This release is EXPERIMENTAL. Please report problems, suggestions and bugs. You can find us at IRC irc.freenode.net #arianne";
		try {
			Configuration config = Configuration.getConfiguration();
			if (config.has("server_welcome)")) {
				msg = config.get("server_welcome");
				if (msg.startsWith("http://")) {
					URL url = new URL(msg);
					HttpURLConnection.setFollowRedirects(false);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					BufferedReader br = new BufferedReader(new InputStreamReader(
							connection.getInputStream()));
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
