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

import games.stendhal.common.Rand;
import games.stendhal.common.Version;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;

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

public class Player extends RPEntity {
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
	 * Shows how many more turns an antidote or another anti-poisonous item
	 * that the player has consumed will be effective.
	 */
	private int turnsLeftOfImmunity;
  
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
		// Port from 0.03 to 0.10
		if (!object.has("base_hp")) {
			object.put("base_hp", "100");
			object.put("hp", "100");
		}

		// Port from 0.13 to 0.20
		if (!object.has("outfit")) {
			object.put("outfit", 0);
		}

		// Port from 0.20 to 0.30
		if (!object.hasSlot("rhand")) {
			object.addSlot(new RPSlot("rhand"));
		}

		if (!object.hasSlot("lhand")) {
			object.addSlot(new RPSlot("lhand"));
		}

		if (!object.hasSlot("armor")) {
			object.addSlot(new RPSlot("armor"));
		}

		if (!object.hasSlot("head")) {
			object.addSlot(new RPSlot("head"));
		}

		if (!object.hasSlot("legs")) {
			object.addSlot(new RPSlot("legs"));
		}

		if (!object.hasSlot("feet")) {
			object.addSlot(new RPSlot("feet"));
		}

		if (!object.hasSlot("bag")) {
			object.addSlot(new RPSlot("bag"));
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

			world.add(player);
		}

		StendhalRPAction.transferContent(player);

		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());

		if (firstVisit) {
			zone.placeObjectAtEntryPoint(player);
		}

		int x = player.getx();
		int y = player.gety();

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

				x = sheep.getx();
				y = sheep.gety();
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

		String[] slots = { "bag", "rhand", "lhand", "head", "armor", "legs",
				"feet", "cloak", "bank" };

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
				if (name.charAt(0) == '_') {
					boolean online = false;
					for (Player buddy : rp.getPlayers()) {
						if (name.equals("_" + buddy.getName())) {
							player.notifyOnline(buddy.getName());
							online = true;
							break;
						}
					}

					if (!online) {
						player.notifyOffline(name.substring(1));
					}
				}
			}
		}
		player.updateItemAtkDef();

		if (!player.hasSlot("!quests")) {
			player.addSlot(new RPSlot("!quests"));
			RPSlot slot = player.getSlot("!quests");

			RPObject quests = new RPObject();
			slot.assignValidID(quests);
			slot.add(quests);
		}
		if (!player.hasSlot("!kills")) {
			player.addSlot(new RPSlot("!kills"));
			RPSlot slot = player.getSlot("!kills");
			RPObject kills = new RPObject();
			slot.assignValidID(kills);
			slot.add(kills);
		}
    
		player.welcome();

		logger.debug("Finally player is :" + player);
		return player;
	}

	public static void destroy(Player player) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());
		zone.removePlayerAndFriends(player);
		try {
			if (player.hasSheep()) {
				Sheep sheep = (Sheep) world.remove(player.getSheep());
				player.storeSheep(sheep);
				rp.removeNPC(sheep);
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

		if (has("age"))
			age = getInt("age");
	}

	public void sendPrivateText(String text) {
        if (has("private_text")) {
            text = get("private_text") + "\r\n" + text;
        }
		put("private_text", text);
		rp.removePlayerText(this);
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
	public void onDead(RPEntity who) {
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

		world.modify(who);

		StendhalRPAction.changeZone(this, "int_afterlife");
		StendhalRPAction.transferContent(this);
	}

	@Override
	protected void dropItemsOn(Corpse corpse) {
		int amount = Rand.rand(4);

		if (amount == 0) {
			return;
		}

		String[] slots = { "bag", "rhand", "lhand", "head", "armor", "legs",
				"feet", "cloak" };

		for (String slotName : slots) {
			if (hasSlot(slotName)) {
				RPSlot slot = getSlot(slotName);

				List<RPObject> objects = new LinkedList<RPObject>();
				for (RPObject objectInSlot : slot) {
					if (amount == 0) {
						break;
					}

					if (Rand.roll1D6() < 4) {
						objects.add(objectInSlot);
						amount--;
					}
				}

				for (RPObject object : objects) {
					if (object instanceof StackableItem) {
						StackableItem item = (StackableItem) object;

						double percentage = (Rand.rand(40) + 10) / 100.0;
						int quantity = item.getQuantity();

						item.setQuantity((int) (quantity * (1.0 - percentage)));

						StackableItem rest_item = (StackableItem) world
								.getRuleManager().getEntityManager().getItem(
										object.get("name"));
						rest_item.setQuantity((int) (quantity * percentage));
						corpse.add(rest_item);
					} else if (object instanceof PassiveEntity) {
						slot.remove(object.getID());

						corpse.add((PassiveEntity) object);
						amount--;
					}

					if (corpse.isFull())
						return;
				}
			}

			if (amount == 0) {
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
		rp.removeNPC(sheep);

		Log4J.finishMethod(logger, "removeSheep");
	}

	public boolean hasSheep() {
		return has("sheep");
	}

	public void setSheep(Sheep sheep) {
		Log4J.startMethod(logger, "setSheep");
		put("sheep", sheep.getID().getObjectID());

		rp.addNPC(sheep);

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
					world.modify(this);
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
					world.modify(this);
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
	 * @param status the player's status in the quest
	 */
	public void setQuest(String name, String status) {
		RPSlot slot = getSlot("!quests");
		RPObject quests = slot.iterator().next();
		quests.put(name, status);
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
		if (turnsLeftOfImmunity == 0) {
			put("poisoned", "0");
			poisonToConsume.add(item);
			return true;
		}
		return false;
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

			if (!nextto((Entity) base, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		} else {
			if (!nextto(item, 0.25)) {
				logger.debug("Consumable item is too far");
				return;
			}
		}

		/*
		 * NOTE: We have a bug when consuming a stackableItem as when the first
		 * item runs out the other ones also runs out. Perhaps this must be
		 * fixed inside StackableItem itself
		 */
		ConsumableItem soloItem = (ConsumableItem) world.getRuleManager()
				.getEntityManager().getEntity(item.getName());

		logger.debug("Consuming item: " + soloItem.getAmount());
		if (soloItem.getRegen() > 0) {
			put("eating", 0);
			itemsToConsume.add(soloItem);
		} else if (soloItem.getRegen() == 0) // if regen==0 is antidote
		{
			poisonToConsume.clear();
			turnsLeftOfImmunity = soloItem.getAmount();
		} else if (turnsLeftOfImmunity == 0) {
			poison(soloItem);
		} else {
			// Player was poisoned, but antidote saved it.
		}

		Collections.sort(itemsToConsume, new Comparator<ConsumableItem>() {
			public int compare(ConsumableItem o1, ConsumableItem o2) {
				return Math.abs(o2.getRegen()) - Math.abs(o1.getRegen());
			}

			public boolean equals(Object obj) {
				return true;
			}
		});

		item.removeOne();
	}

	public void consume(int turn) {
		if (turnsLeftOfImmunity > 0) {
			turnsLeftOfImmunity--;
		}

		if (has("poisoned") && poisonToConsume.size() == 0) {
			remove("poisoned");
			world.modify(this);
		}

		if (has("eating") && itemsToConsume.size() == 0) {
			remove("eating");
			world.modify(this);
		}

		while (poisonToConsume.size() > 0) {
			ConsumableItem consumableItem = poisonToConsume.get(0);

			if (turn % consumableItem.getFrecuency() != 0) {
				break;
			}

			if (!consumableItem.consumed()) {
				consumableItem.consume();
				int amount = consumableItem.getRegen();
				put("poisoned", amount);

				if (getHP() + amount > 0) {
					setHP(getHP() + amount);
				} else {
					kill(this);
				}

				world.modify(this);
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
				consumableItem.consume();
				int amount = consumableItem.getRegen();
				put("eating", amount);

				if (getHP() + amount < getBaseHP()) {
					setHP(getHP() + amount);
				} else {
					setHP(getBaseHP());
					itemsToConsume.clear();
				}

				world.modify(this);
				break;
			} else {
				logger.debug("Consumed completly item: " + consumableItem);
				itemsToConsume.remove(0);
			}
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
}
