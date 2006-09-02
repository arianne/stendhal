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
package games.stendhal.server.entity;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.common.Version;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Configuration;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

public class Player extends RPEntity implements TurnListener {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(Player.class);

	/**
	 * The number of minutes that this player has been logged in on the
	 * server.
	 */
	private int age;

	/**
	 * Food, drinks etc. that the player wants to consume and has not finished
	 * with.
	 */
	private List<ConsumableItem> itemsToConsume;

	/**
	 * Poisonous items that the player still has to consume. This also
	 * includes poison that was the result of fighting against a poisonous
	 * creature.  
	 */
	private List<ConsumableItem> poisonToConsume;

	/**
	 * Shows if this player is currently under the influence of an
	 * antidote, and thus immune from poison.
	 */
	private boolean isImmune;
  
  private static boolean firstWelcomeException = true;

	public static void generateRPClass() {
		try {
			RPClass player = new RPClass("player");
			player.isA("rpentity");
			player.add("text", RPClass.LONG_STRING, RPClass.VOLATILE);
			player.add("private_text", RPClass.LONG_STRING,
					(byte) (RPClass.PRIVATE | RPClass.VOLATILE));

			player.add("poisoned", RPClass.SHORT, RPClass.VOLATILE);
			player.add("eating", RPClass.SHORT, RPClass.VOLATILE);

			player.add("dead", RPClass.FLAG, RPClass.PRIVATE);

			player.add("outfit", RPClass.INT);

			// Use this for admin menus and usage.
			player.add("admin", RPClass.FLAG);
			player.add("adminlevel", RPClass.INT);
			player.add("invisible", RPClass.FLAG, RPClass.HIDDEN);

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
			player.add("online", RPClass.LONG_STRING,
					(byte) (RPClass.PRIVATE | RPClass.VOLATILE));
			player.add("offline", RPClass.LONG_STRING,
					(byte) (RPClass.PRIVATE | RPClass.VOLATILE));

			player.addRPSlot("!quests", 1, RPClass.HIDDEN);
		} catch (RPClass.SyntaxException e) {
			logger.error("cannot generateRPClass", e);
		}
	}

	public static Player create(RPObject object) {
		String[] slots = { "bag", "rhand", "lhand", "head", "armor", "legs",
				"feet", "cloak", "bank" };
		
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
		
		// Port from 0.20 to 0.30: bag, rhand, lhand, armor, head, legs, feet
		// Port from 0.44 to 0.50: cloak, bank
		for (String slotName : slots) {
			if (!object.hasSlot(slotName)) {
				object.addSlot(new RPSlot(slotName));
			}

		}

		// Port from 0.30 to 0.35
		if (!object.hasSlot("!buddy")) {
			object.addSlot(new RPSlot("!buddy"));
		}

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

		if (!object.hasSlot("cloak")) {
			object.addSlot(new RPSlot("cloak"));
		}

		if (!object.hasSlot("bank")) {
			object.addSlot(new RPSlot("bank"));
		}

		if (object.hasSlot("!buddy")) {
			RPSlot buddy = object.getSlot("!buddy");
			if (buddy.size() == 0) {
				RPObject data = new RPObject();
				buddy.assignValidID(data);
				buddy.add(data);
			}
		}

		if (!object.has("age")) {
			object.put("age", "0");
		}

		StendhalRPWorld world = StendhalRPWorld.get();
		Player player = new Player(object);
		// Port from 0.48 to 0.50
		player.readAdminsFromFile();

		player.stop();
		player.stopAttack();

		boolean firstVisit = false;

		try {
			if (!object.has("zoneid") || !object.has("x") || !object.has("y")) {
				firstVisit = true;
			}

			boolean newReleaseHappened = !object.get("release").equals(
					Version.VERSION);
			if (newReleaseHappened) {
				firstVisit = true;
				player.put("release", Version.VERSION);
			}

			if (firstVisit) {
				player.put("zoneid", "int_semos_townhall");
			}

			world.add(player);
		} catch (Exception e) { // If placing the player at its last position
								// fails we reset it to city entry point
			logger.warn(
					"cannot place player at its last position. reseting to semos city entry point",
					e);

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

		for (String slotName : slots) {
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
							if (item.get("type").equals("item")) {  // We simply
																	// ignore
																	// corpses...
								Item entity = world.getRuleManager()
										.getEntityManager().getItem(
												item.get("name"));

								entity.setID(item.getID());

								if(item.has("persistent") && item.getInt("persistent")==1) {
									entity.fill(item);
								}

								if (entity instanceof StackableItem) {
									StackableItem money = (StackableItem) entity;
									money.setQuantity(item.getInt("quantity"));
								}
								// make sure saved individual information is
								// restored
								if (item.has("infostring")) {
									entity.put("infostring", item
											.get("infostring"));
								}
								if (item.has("description")) {
									entity.put("description", item
											.get("description"));
								}

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
			} catch (Exception e) {
				logger.error("cannot create player", e);
				if (player.hasSlot(slotName)) {
					RPSlot slot = player.getSlot(slotName);
					slot.clear();
				}
			}
		}

		if (player.getSlot("!buddy").size() > 0) {
			RPObject buddies = player.getSlot("!buddy").iterator().next();
			for (String name : buddies) {
				// what is this underscore supposed to do?
				if (name.charAt(0) == '_') {
					// cut off the strange underscore
					Player buddy = StendhalRPRuleProcessor.get().getPlayer(name.substring(1));
					if (buddy != null) {
							player.notifyOnline(buddy.getName());
					} else {
						player.notifyOffline(name.substring(1));
					}
				}
			}
		}
		player.updateItemAtkDef();

		// create (or repair) !quests and !kills slots
		String[] slotsWithOneObject = {"!quests", "!kills"};
		for (String slotName : slotsWithOneObject) {
			if (!player.hasSlot(slotName)) {
				player.addSlot(new RPSlot(slotName));
			}
			RPSlot questSlot = player.getSlot(slotName);
			if (!questSlot.iterator().hasNext()) {
				RPObject quests = new RPObject();
				questSlot.assignValidID(quests);
				questSlot.add(quests);
			}
		}

		player.welcome();

		logger.debug("Finally player is :" + player);
		return player;
	}

	public static void destroy(Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());
		zone.removePlayerAndFriends(player);
		try {
			if (player.hasSheep()) {
				Sheep sheep = (Sheep) world.remove(player.getSheep());
				player.storeSheep(sheep);
				StendhalRPRuleProcessor.get().removeNPC(sheep);
				zone.removePlayerAndFriends(sheep);
			} else {
				// Bug on pre 0.20 released
				if (player.hasSlot("#flock")) {
					player.removeSlot("#flock");
				}
			}
		} catch (Exception e) /**
								 * No idea how but some players get a sheep but
								 * they don't have it really. Me thinks that it
								 * is a player that has been running for a while
								 * the game and was kicked of server because
								 * shutdown on a pre 1.00 version of Marauroa.
								 * We shouldn't see this anymore.
								 */
		{
			logger.error("Pre 1.00 Marauroa sheep bug. (player = "
					+ player.getName() + ")", e);

			if (player.has("sheep")) {
				player.remove("sheep");
			}

			if (player.hasSlot("#flock")) {
				player.removeSlot("#flock");
			}
		}

		player.stop();
		player.stopAttack();

		world.remove(player.getID());
	}

	public Player(RPObject object) throws AttributeNotFoundException {
		super(object);
		put("type", "player");
		// HACK: postman as NPC
		if (object.has("name") && object.get("name").equals("postman")) {
			put("title_type", "npc");
		}
		
		itemsToConsume = new LinkedList<ConsumableItem>();
		poisonToConsume = new LinkedList<ConsumableItem>();

		update();
	}

	@Override
	public void update() throws AttributeNotFoundException {
		super.update();

		if (has("age")) {
			age = getInt("age");
		}
	}

	public void sendPrivateText(String text) {
        if (has("private_text")) {
            text = get("private_text") + "\r\n" + text;
        }
		put("private_text", text);
		StendhalRPRuleProcessor.get().removePlayerText(this);
	}

	/** send a welcome message to the player which can be configured
	 *  in marauroa.ini file as "server_welcome". If the value is
	 *  an http:// adress, the first line of that adress is read
	 *  and used as the message 
	 */
	public void welcome() {
		String msg = "This release is EXPERIMENTAL. Please report problems, suggestions and bugs. You can find us at IRC irc.freenode.net #arianne";
		try {
			Configuration config = Configuration.getConfiguration();
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
		} catch (Exception e) {
			if (Player.firstWelcomeException) {
				logger.warn("Can't read server_welcome from marauroa.ini", e);
				Player.firstWelcomeException = false;
			}
		}
		if (msg != null) {
			sendPrivateText(msg);
		}
	}
  
	private static List<String> adminNames;

	private void readAdminsFromFile() {
		if (adminNames == null) {
			adminNames = new LinkedList<String>();
			
			String adminFilename="data/conf/admins.list";

			try {
				InputStream is = getClass().getClassLoader()
						.getResourceAsStream(adminFilename);

				if (is == null) {
					logger.info("data/conf/admins.list does not exist.");
				} else {
					
					BufferedReader in = new BufferedReader(
							new InputStreamReader(is));
	
					String line;
					while ((line = in.readLine()) != null) {
						adminNames.add(line);
					}
					in.close();
				}
			} catch (Exception e) {
				logger.error("Error loading admin names from: "+adminFilename, e);
			}
		}

		boolean isAdmin = adminNames.contains(getName());

		if (isAdmin) {
			put("adminlevel", AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPER);
		} else {
			if (!has("adminlevel")) {
				put("adminlevel", "0");
			}
		}
	}

	/**
	 * Returns the admin level of this user. See AdministrationAction.java for details.
	 *
	 * @return adminlevel
	 */
	public int getAdminLevel() {
		// normal user are adminlevel 0.
		if (!has("adminlevel")) {
			return 0;
		}
		return getInt("adminlevel");
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y + 1, 1, 1);
	}

	@Override
	public void onDead(Entity who) {
		StendhalRPWorld world = StendhalRPWorld.get();
		put("dead", "");

		if (hasSheep()) {
			// We make the sheep ownerless so someone can use it
			if (world.has(getSheep())) {
				Sheep sheep = (Sheep) world.get(getSheep());
				sheep.setOwner(null);
			} else {
				logger.warn("INCOHERENCE: Player has sheep but sheep doesn't exists");
			}
			remove("sheep");
		}

		// We stop eating anything
		itemsToConsume.clear();
		poisonToConsume.clear();

		super.onDead(who, false);

		// Penalize: Respawn on afterlive zone and 10% less experience
		subXP((int) (getXP() * 0.1));
		setATKXP((int) (getATKXP() * 0.9));
		setDEFXP((int) (getDEFXP() * 0.9));

		setHP(getBaseHP());

		StendhalRPAction.changeZone(this, "int_afterlife");
		StendhalRPAction.transferContent(this);
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		int maxItemsToDrop = Rand.rand(4);

		String[] slots = { "bag", "rhand", "lhand", "head", "armor", "legs",
				"feet", "cloak" };

		for (String slotName : slots) {
			if (hasSlot(slotName)) {
				RPSlot slot = getSlot(slotName);

				// a list that will contain the objects that will
				// be dropped.
				List<RPObject> objects = new LinkedList<RPObject>();
				
				// get a random set of items to drop
				for (RPObject objectInSlot : slot) {
					if (maxItemsToDrop == 0) {
						break;
					}

					if (Rand.throwCoin() == 1) {
						objects.add(objectInSlot);
						maxItemsToDrop--;
					}
				}

				// now actually drop them
				for (RPObject object : objects) {
					if (object instanceof StackableItem) {
						StackableItem item = (StackableItem) object;

						// We won't drop the full quantity, but only a percentage.
						// Get a random percentage between 26 % and 75 % to drop  
						double percentage = (Rand.rand(50) + 25) / 100.0;
						int quantity = item.getQuantity();
						int quantityToDrop = (int) Math.round(quantity * percentage);
						int remainingQuantity = quantity - quantityToDrop;

						if (remainingQuantity > 0) {
							item.setQuantity(quantity - quantityToDrop);
						} else {
							drop(item);
						}

						if (quantityToDrop > 0) {
							StackableItem restItem = (StackableItem) StendhalRPWorld.get()
									.getRuleManager().getEntityManager().getItem(
											object.get("name"));
							restItem.setQuantity(quantityToDrop);
							if (item.has("infostring")) {
								restItem.put("infostring", item.get("infostring"));
							}
							if (item.has("description")) {
								restItem.put("description", item.get("description"));
							}
							corpse.add(restItem);
						}
					} else if (object instanceof PassiveEntity) {
						slot.remove(object.getID());

						corpse.add((PassiveEntity) object);
						maxItemsToDrop--;
					}

					if (corpse.isFull()) {
						// This shouldn't happen because we have only chosen
						// 4 items, and corpses can handle this.
						return;
					}
				}
			}

			if (maxItemsToDrop == 0) {
				return;
			}
		}
	}

	public void removeSheep(Sheep sheep) {
		Log4J.startMethod(logger, "removeSheep");
		if (has("sheep")) {
			remove("sheep");
		} else {
			logger.warn("Called removeSheep but player has not sheep: " + this);
		}
		StendhalRPRuleProcessor.get().removeNPC(sheep);

		Log4J.finishMethod(logger, "removeSheep");
	}

	public boolean hasSheep() {
		return has("sheep");
	}

	public void setSheep(Sheep sheep) {
		Log4J.startMethod(logger, "setSheep");
		put("sheep", sheep.getID().getObjectID());

		StendhalRPRuleProcessor.get().addNPC(sheep);

		Log4J.finishMethod(logger, "setSheep");
	}

	public static class NoSheepException extends RuntimeException {
		private static final long serialVersionUID = -6689072547778842040L;

		public NoSheepException() {
			super();
		}
	}

	public RPObject.ID getSheep() throws NoSheepException {
		return new RPObject.ID(getInt("sheep"), get("zoneid"));
	}

	public void storeSheep(Sheep sheep) {
		Log4J.startMethod(logger, "storeSheep");
		if (!hasSlot("#flock")) {
			addSlot(new RPSlot("#flock"));
		}

		RPSlot slot = getSlot("#flock");
		slot.clear();
		slot.add(sheep);
		put("sheep", sheep.getID().getObjectID());
		Log4J.finishMethod(logger, "storeSheep");
	}

	public Sheep retrieveSheep() throws NoSheepException {
		Log4J.startMethod(logger, "retrieveSheep");
		try {
			if (hasSlot("#flock")) {
				RPSlot slot = getSlot("#flock");
				if (slot.size() > 0) {
					RPObject object = slot.getFirst();
					slot.remove(object.getID());

					Sheep sheep = new Sheep(object, this);

					removeSlot("#flock");
					return sheep;
				}
			}

			throw new NoSheepException();
		} finally {
			Log4J.finishMethod(logger, "retrieveSheep");
		}
	}

	/**
	 * Gets the number of minutes that this player has been logged in on the
	 * server.
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Sets the number of minutes that this player has been logged in on the
	 * server.
	 * @param age minutes
	 */
	public void setAge(int age) {
		this.age = age;
		put("age", age);
	}

	public void notifyOnline(String who) {
		String playerOnline = "_" + who;

		boolean found = false;
		RPSlot slot = getSlot("!buddy");
		if (slot.size() > 0) {
			RPObject buddies = slot.iterator().next();
			for (String name : buddies) {
				if (playerOnline.equals(name)) {
					buddies.put(playerOnline, 1);
					notifyWorldAboutChanges();
					found = true;
					break;
				}
			}
		}
		if (found) {
			if (has("online")) {
				put("online", get("online") + "," + who);
			} else {
				put("online", who);
			}
		}
	}

	public void notifyOffline(String who) {
		String playerOffline = "_" + who;

		boolean found = false;
		RPSlot slot = getSlot("!buddy");
		if (slot.size() > 0) {
			RPObject buddies = slot.iterator().next();
			for (String name : buddies) {
				if (playerOffline.equals(name)) {
					buddies.put(playerOffline, 0);
					notifyWorldAboutChanges();
					found = true;
					break;
				}
			}
		}
		if (found) {
			if (has("offline")) {
				put("offline", get("offline") + "," + who);
			} else {
				put("offline", who);
			}
		}
	}

	/**
	 * Checks whether the player has completed the given quest or not.
	 * @param name The quest's name
	 * @return true iff the quest has been completed by the player
	 */
	public boolean isQuestCompleted(String name) {
		if (hasQuest(name)) {
			RPSlot slot = getSlot("!quests");
			RPObject quests = slot.iterator().next();

			if (quests.get(name).equals("done")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether the player has made any progress in the given
	 * quest or not. For many quests, this is true right after the quest
	 * has been started. 
	 * @param name The quest's name
	 * @return true iff the player has made any progress in the quest
	 */
	public boolean hasQuest(String name) {
		if (!hasSlot("!quests")) {
			logger.error("Expected to find !quests slot");
			return false;
		}
		RPSlot slot = getSlot("!quests");
		if (slot.size() == 0) {
			logger.error("Expected to find something !quests slot");
			return false;
		}
		RPObject quests = slot.iterator().next();
		return quests.has(name);
	}

	/**
	 * Gets the player's current status in the given quest. 
	 * @param name The quest's name
	 * @return the player's status in the quest
	 */
	public String getQuest(String name) {
		if (hasQuest(name)) {
			RPSlot slot = getSlot("!quests");
			RPObject quests = slot.iterator().next();

			return quests.get(name);
		} else {
			return null;
		}
	}

	/**
	 * Allows to store the player's current status in a quest in a string.
	 * This string may, for instance, be "started", "done", a semicolon-
	 * separated list of items that need to be brought/NPCs that need to be
	 * met, or the number of items that still need to be brought. Note that
	 * the string "done" has a special meaning: see isQuestComplete().
	 * @param name The quest's name
	 * @param status the player's status in the quest. Set it to null to
	 *        completely reset the player's status for the quest.
	 */
	public void setQuest(String name, String status) {
		RPSlot slot = getSlot("!quests");
		RPObject quests = slot.iterator().next();
		if (status != null ) {
			quests.put(name, status);
		} else {
			quests.remove(name);
		}
	}

	public List<String> getQuests() {
		RPSlot slot = getSlot("!quests");
		RPObject quests = slot.iterator().next();

		List<String> questsList = new LinkedList<String>();
		for (String quest : quests) {
			// why are id and zoneid stored in the quest slot?
			// -- DHerding@gmx.de
			if (!quest.equals("id") && !quest.equals("zoneid")) {
				questsList.add(quest);
			}
		}
		return questsList;
	}

	public void removeQuest(String name) {
		if (hasQuest(name)) {
			RPSlot slot = getSlot("!quests");
			RPObject quests = slot.iterator().next();

			quests.remove(name);
		}
	}

	/**
	 * Is the named quest in one of the listed states?
	 *
	 * @param name   quest
	 * @param states valid states
	 * @return true, if the quest is in one of theses states, false otherwise
	 */
	public boolean isQuestInState(String name, String ... states) {
		if (!hasQuest(name)) {
			return false;
		}
		String questState = getQuest(name);
		boolean res = false;
		for (String state : states) {
			res = questState.equals(state);
			if (res) {
				break;
			}
		}
		
		return res;
	}

	/**
	 * This probably checks if the player has killed a creature with the
	 * given name without the help of any other player (?)
	 */
	public boolean hasKilledSolo(String name) {
		if (hasKilled(name)) {
			RPSlot slot = getSlot("!kills");
			RPObject kills = slot.iterator().next();
			if (kills.get(name).equals("solo")) {
				return true;
			}
		}
		return false;
	}

	public boolean hasKilled(String name) {
		if (!hasSlot("!kills")) {
			logger.error("Expected to find !kills slot");
			return false;
		}
		RPSlot slot = getSlot("!kills");
		if (slot.size() == 0) {
			logger.error("Expected to find something !kills slot");
			return false;
		}
		RPObject kills = slot.iterator().next();
		if (kills.has(name)) {
			return true;
		} else {
			return false;
		}
	}

	public String getKill(String name) {
		if (hasKilled(name)) {
			RPSlot slot = getSlot("!kills");
			RPObject kills = slot.iterator().next();
			return kills.get(name);
		} else {
			return null;
		}
	}

	public void setKill(String name, String mode) {
		RPSlot slot = getSlot("!kills");
		RPObject kills = slot.iterator().next();
		kills.put(name, mode);
	}

	public List<String> getKills() {
		RPSlot slot = getSlot("!kills");
		RPObject kills = slot.iterator().next();
		List<String> killsList = new LinkedList<String>();
		for (String k : kills) {
			if (!k.equals("id") && !k.equals("zoneid")) {
				killsList.add(k);
			}
		}
		return killsList;
	}

	public void removeKill(String name) {
		if (hasKilled(name)) {
			RPSlot slot = getSlot("!kills");
			RPObject kills = slot.iterator().next();
			kills.remove(name);
		}
	}

	/**
	 * Checks whether the player is still suffering from the effect of a
	 * poisonous item/creature or not.
	 */
	public boolean isPoisoned() {
		return !(poisonToConsume.size() == 0);
	}

	/**
	 * Disburdens the player from the effect of a poisonous item/creature.
	 */
	public void healPoison() {
		poisonToConsume.clear();
	}

	/**
	 * Poisons the player with a poisonous item.
	 * Note that this method is also used when a player has been poisoned
	 * while fighting against a poisonous creature. 
	 * @param item the poisonous item
	 * @return true iff the poisoning was effective, i.e. iff the player is
	 *         not immune 
	 */
	public boolean poison(ConsumableItem item) {
		if (isImmune) {
			return false;
		} else {
			// put("poisoned", "0");
			poisonToConsume.add(item);
			return true;
		}
	}

	public void consumeItem(ConsumableItem item) {
		if (item.getRegen() > 0 && itemsToConsume.size() > 5
				&& !item.getName().contains("potion")) {
			sendPrivateText("You can't consume anymore");
			return;
		}
		if (item.isContained()) {
			// We modify the base container if the object change.
			RPObject base = item.getContainer();

			while (base.isContained()) {
				base = base.getContainer();
			}

			if (!nextTo((Entity) base, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		} else {
			if (!nextTo(item, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		}

		/*
		 * NOTE: We have a bug when consuming a stackableItem as when the first
		 * item runs out the other ones also runs out. Perhaps this must be
		 * fixed inside StackableItem itself
		 */
		ConsumableItem soloItem = (ConsumableItem) StendhalRPWorld.get().getRuleManager()
				.getEntityManager().getEntity(item.getName());

		logger.debug("Consuming item: " + soloItem.getAmount());
		if (soloItem.getRegen() > 0) {
			put("eating", 0);
			itemsToConsume.add(soloItem);
		} else if (soloItem.getRegen() == 0) { // if regen==0, it's an antidote
			poisonToConsume.clear();
			isImmune = true;
			// set a timer to remove the immunity effect after some time
			TurnNotifier notifier = TurnNotifier.get();
			// first remove all effects from previously used immunities to
			// restart the timer
			notifier.dontNotify(this, "end_immunity");
			notifier.notifyInTurns(soloItem.getAmount(), this, "end_immunity");
		} else if (!isImmune) {
			// Player was poisoned and is currently not immune
			poison(soloItem);
		} else {
			// Player was poisoned, but antidote saved it.
		}

		Collections.sort(itemsToConsume, new Comparator<ConsumableItem>() {
			public int compare(ConsumableItem o1, ConsumableItem o2) {
				return Math.abs(o2.getRegen()) - Math.abs(o1.getRegen());
			}

			@Override
			public boolean equals(Object obj) {
				return true;
			}
		});

		item.removeOne();
	}

	public void consume(int turn) {
		if (has("poisoned") && poisonToConsume.size() == 0) {
			remove("poisoned");
			notifyWorldAboutChanges();
		}

		if (has("eating") && itemsToConsume.size() == 0) {
			remove("eating");
			notifyWorldAboutChanges();
		}

		while (poisonToConsume.size() > 0) {
			ConsumableItem poison = poisonToConsume.get(0);

			if (turn % poison.getFrecuency() != 0) {
				break;
			}

			if (!poison.consumed()) {
				int amount = poison.consume();
				put("poisoned", amount);

				if (getHP() + amount > 0) {
					setHP(getHP() + amount);
				} else {
					kill(poison);
				}

				notifyWorldAboutChanges();
				break;
			} else {
				poisonToConsume.remove(0);
			}
		}

		while (itemsToConsume.size() > 0) {
			ConsumableItem consumableItem = itemsToConsume.get(0);
			logger.debug("Consuming item: " + consumableItem);

			if (turn % consumableItem.getFrecuency() != 0) {
				break;
			}

			if (!consumableItem.consumed()) {
				logger.debug("Consumed item: " + consumableItem);
				int amount = consumableItem.consume();
				put("eating", amount);

				if (getHP() + amount < getBaseHP()) {
					setHP(getHP() + amount);
				} else {
					setHP(getBaseHP());
					itemsToConsume.clear();
				}

				notifyWorldAboutChanges();
				break;
			} else {
				logger.debug("Consumed completly item: " + consumableItem);
				itemsToConsume.remove(0);
			}
		}
	}
	
	// TODO: use the turn notifier for consumable items to
	// get rid of Player.consume().
	public void onTurnReached(int turn, String message) {
		if ("end_immunity".equals(message)) {
			isImmune = false;
		}
	}

	@Override
	public String describe() {
		int hours = age / 60;
		int minutes = age % 60;
		String time = hours + " hours and " + minutes + " minutes";
		String text = "You see " + getName() + ".";
		if (hasDescription()) {
			text = getDescription();
		}
		text += "\n" + getName() + " is level " + getLevel()
				+ " and has been playing " + time + ".";
		return (text);
	}
	
	/**
	 * Teleports this player to the given destination.
	 * @param zone The zone where this player should be teleported to.
	 * @param x The destination's x coordinate
	 * @param y The destination's y coordinate
	 * @param dir The direction in which the player should look after
	 *            teleporting, or null if the direction shouldn't change
	 * @param teleporter The player who initiated the teleporting, or null
	 *                   if no player is responsible. This is only to give
	 *                   feedback if something goes wrong. If no feedback is
	 *                   wanted, use null.
	 * @return true iff teleporting was successful
	 */
	public boolean teleport(StendhalRPZone zone, int x, int y, Direction dir, Player teleporter) {
		if (StendhalRPAction.placeat(zone, this, x, y)) {
			StendhalRPAction.changeZone(this, zone.getID().getID());
			StendhalRPAction.transferContent(this);
			if (dir != null) {
				this.setDirection(dir);
			}
			
			String teleporterName;
			if (teleporter == null) {
				teleporterName = "null";
			} else {
				teleporterName = teleporter.getName();
			}

			StendhalRPRuleProcessor.get().addGameEvent(teleporterName, "teleport", this
					.getName());

			notifyWorldAboutChanges();
			return true;
		} else {
			String text = "Position [" + x + "," + y + "] is occupied";
			if (teleporter != null) {
				teleporter.sendPrivateText(text);
			} else {
				this.sendPrivateText(text);
			}
			return false;
		}	
		
	}
}
