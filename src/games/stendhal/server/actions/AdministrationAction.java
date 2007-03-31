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
package games.stendhal.server.actions;

import games.stendhal.common.Grammar;
import games.stendhal.server.Jail;
import games.stendhal.server.StendhalQuestSystem;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.rule.EntityManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import marauroa.common.Log4J;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.apache.log4j.Logger;

/**
 * Most /commands for admins are handled here. 
 */
public class AdministrationAction implements ActionListener {

	private static final Logger logger = Log4J.getLogger(AdministrationAction.class);

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPPORT = 100;

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPER = 5000;

	private static final Map<String, Integer> REQUIRED_ADMIN_LEVELS = new HashMap<String, Integer>();

	public static void register() {
		AdministrationAction administration = new AdministrationAction();
		StendhalRPRuleProcessor.register("inspect", administration);
		StendhalRPRuleProcessor.register("destroy", administration);
		StendhalRPRuleProcessor.register("supportanswer", administration);
		StendhalRPRuleProcessor.register("tellall", administration);
		StendhalRPRuleProcessor.register("teleport", administration);
		StendhalRPRuleProcessor.register("teleportto", administration);
		StendhalRPRuleProcessor.register("adminlevel", administration);
		StendhalRPRuleProcessor.register("alter", administration);
		StendhalRPRuleProcessor.register("summon", administration);
		StendhalRPRuleProcessor.register("summonat", administration);
		StendhalRPRuleProcessor.register("invisible", administration);
		StendhalRPRuleProcessor.register("ghostmode", administration);
		StendhalRPRuleProcessor.register("jail", administration);

		REQUIRED_ADMIN_LEVELS.put("adminlevel", 0);
		REQUIRED_ADMIN_LEVELS.put("support", 100);
		REQUIRED_ADMIN_LEVELS.put("supportanswer", 50);
		REQUIRED_ADMIN_LEVELS.put("tellall", 200);
		REQUIRED_ADMIN_LEVELS.put("teleportto", 300);
		REQUIRED_ADMIN_LEVELS.put("teleport", 400);
		REQUIRED_ADMIN_LEVELS.put("jail", 400);
		REQUIRED_ADMIN_LEVELS.put("invisible", 500);
		REQUIRED_ADMIN_LEVELS.put("ghostmode", 500);
		REQUIRED_ADMIN_LEVELS.put("inspect", 600);
		REQUIRED_ADMIN_LEVELS.put("destroy", 700);
		REQUIRED_ADMIN_LEVELS.put("summon", 800);
		REQUIRED_ADMIN_LEVELS.put("summonat", 800);
		REQUIRED_ADMIN_LEVELS.put("alter", 900);
		REQUIRED_ADMIN_LEVELS.put("super", 5000);
	}

	public static void registerCommandLevel(String command, int minLevel) {
		REQUIRED_ADMIN_LEVELS.put(command, minLevel);
	}

	public static boolean isPlayerAllowedToExecuteAdminCommand(Player player, String command, boolean verbose) {
		// get adminlevel of player and required adminlevel for this command
		int adminlevel = player.getAdminLevel();
		Integer required = REQUIRED_ADMIN_LEVELS.get(command);

		// check that we know this command
		if (required == null) {
			logger.error("Unknown command " + command);
			if (verbose) {
				player.sendPrivateText("Sorry, command \"" + command + "\" is unknown.");
			}
			return false;
		}

		if (adminlevel < required.intValue()) {
			// not allowed
			logger.warn("Player " + player.getName() + " with admin level " + adminlevel
			        + " tried to run admin command " + command + " which requires level " + required + ".");

			// Notify the player if verbose is set.
			if (verbose) {

				// is this player an admin at all?
				if (adminlevel == 0) {
					player.sendPrivateText("Sorry, you need to be an admin to run \"" + command + "\".");
				} else {
					player.sendPrivateText("Your admin level is only " + adminlevel + ", but a level of " + required
					        + " is required to run \"" + command + "\".");
				}
			}
			return false;
		}

		// OK
		return true;
	}

	public void onAction(Player player, RPAction action) {

		String type = action.get("type");
		if (!isPlayerAllowedToExecuteAdminCommand(player, type, true)) {
			return;
		}

		if (type.equals("tellall")) {
			onTellEverybody(player, action);
		} else if (type.equals("supportanswer")) {
			onSupportAnswer(player, action);
		} else if (type.equals("teleport")) {
			onTeleport(player, action);
		} else if (type.equals("teleportto")) {
			onTeleportTo(player, action);
		} else if (type.equals("adminlevel")) {
			onAdminLevel(player, action);
		} else if (type.equals("alter")) {
			onAlter(player, action);
		} else if (type.equals("summon")) {
			onSummon(player, action);
		} else if (type.equals("summonat")) {
			onSummonAt(player, action);
		} else if (type.equals("invisible")) {
			onInvisible(player, action);
		} else if (type.equals("ghostmode")) {
			onGhostMode(player, action);
		} else if (type.equals("inspect")) {
			onInspect(player, action);
		} else if (type.equals("destroy")) {
			onDestroy(player, action);
		} else if (type.equals("jail")) {
			onJail(player, action);
		}
	}

	private void onSupportAnswer(Player player, RPAction action) {
		Log4J.startMethod(logger, "supportanswer");

		if (action.has("target") && action.has("text")) {
			String message = player.getName() + " answers " + Grammar.suffix_s(action.get("target"))
			        + " support question: " + action.get("text");

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "supportanswer", action.get("target"),
			        action.get("text"));

			boolean found = false;
			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				if (p.getName().equals(action.get("target"))) {
					p.sendPrivateText("Support (" + player.getName() + ") tells you: " + action.get("text"));
					p.notifyWorldAboutChanges();
					found = true;
				}
				if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
					p.sendPrivateText(message);
					p.notifyWorldAboutChanges();
				}
			}

			if (!found) {
				player.sendPrivateText(action.get("target") + " is not currently logged in.");
			}
		}

		Log4J.finishMethod(logger, "supportanswer");
	}

	private void onTellEverybody(Player player, RPAction action) {
		Log4J.startMethod(logger, "onTellEverybody");

		if (action.has("text")) {
			String message = "Administrator SHOUTS: " + action.get("text");
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "tellall", action.get("text"));

			StendhalRPAction.shout(message);
		}

		Log4J.finishMethod(logger, "onTellEverybody");
	}

	private void onTeleport(Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleport");

		if (action.has("target") && action.has("zone") && action.has("x") && action.has("y")) {
			String name = action.get("target");
			Player teleported = StendhalRPRuleProcessor.get().getPlayer(name);

			if (teleported == null) {
				String text = "Player \"" + name + "\" not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			// validate the zone-name.
			IRPZone.ID zoneid = new IRPZone.ID(action.get("zone"));
			if (!StendhalRPWorld.get().hasRPZone(zoneid)) {
				String text = "Zone \"" + zoneid + "\" not found.";
				logger.debug(text);

				Set<String> zoneNames = new TreeSet<String>();
				Iterator itr = StendhalRPWorld.get().iterator();
				while (itr.hasNext()) {
					StendhalRPZone zone = (StendhalRPZone) itr.next();
					zoneNames.add(zone.getID().getID());
				}
				player.sendPrivateText(text + " Valid zones: " + zoneNames);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(zoneid);
			int x = action.getInt("x");
			int y = action.getInt("y");

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "teleport", action.get("target"),
			        zone.getID().getID(), Integer.toString(x), Integer.toString(y));
			teleported.teleport(zone, x, y, null, player);
		}

		Log4J.finishMethod(logger, "onTeleport");
	}

	private void onTeleportTo(Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleportTo");

		if (action.has("target")) {
			String name = action.get("target");
			RPEntity teleported = StendhalRPRuleProcessor.get().getPlayer(name);

			if (teleported == null) {
				teleported = NPCList.get().get(name);
				if (teleported == null) {

					String text = "Player \"" + name + "\" not found";
					player.sendPrivateText(text);
					logger.debug(text);
					return;
				}
			}

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(teleported.getID());
			int x = teleported.getX();
			int y = teleported.getY();

			player.teleport(zone, x, y, null, player);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "teleportto", action.get("target"),
			        zone.getID().getID(), Integer.toString(x), Integer.toString(y));
		}

		Log4J.finishMethod(logger, "onTeleportTo");
	}

	private void onAdminLevel(Player player, RPAction action) {
		Log4J.startMethod(logger, "onAdminLevel");

		if (action.has("target")) {

			String name = action.get("target");
			Player target = StendhalRPRuleProcessor.get().getPlayer(name);

			if (target == null) {
				logger.debug("Player \"" + name + "\" not found");
				player.sendPrivateText("Player \"" + name + "\" not found");
				return;
			}

			int oldlevel = target.getAdminLevel();
			String response = target.getName() + " has adminlevel " + oldlevel;

			if (action.has("newlevel")) {
				// verify newlevel is a number
				int newlevel;
				try {
					newlevel = Integer.parseInt(action.get("newlevel"));
				} catch (NumberFormatException e) {
					player.sendPrivateText("The new adminlevel needs to be an Integer");
					Log4J.finishMethod(logger, "onAdminLevel");
					return;
				}

				int mylevel = player.getAdminLevel();
				if (mylevel < REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
					response = "Sorry, but you need an adminlevel of " + REQUIRED_ADMIN_LEVEL_FOR_SUPER
					        + " to change adminlevel.";

					/*if (mylevel < oldlevel) {
					 response = "Sorry, but the adminlevel of " + target.getName() + " is " + oldlevel + ", and your level is only " + mylevel + ".";
					 } else if (mylevel < newlevel) {
					 response = "Sorry, you cannot set an adminlevel of " + newlevel + ", because your level is only " + mylevel + ".";
					 */
				} else {

					// OK, do the change
					StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "adminlevel", target.getName(),
					        "adminlevel", action.get("newlevel"));
					target.put("adminlevel", newlevel);
					target.update();
					target.notifyWorldAboutChanges();

					response = "Changed adminlevel of " + target.getName() + " from " + oldlevel + " to " + newlevel
					        + ".";
				}
			}

			player.sendPrivateText(response);
		}

		Log4J.finishMethod(logger, "onAdminLevel");
	}

	private void onAlter(Player player, RPAction action) {
		Log4J.startMethod(logger, "onChangePlayer");

		if (action.has("target") && action.has("stat") && action.has("mode") && action.has("value")) {
			Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			String stat = action.get("stat");

			if (stat.equals("name")) {
				logger.error("DENIED: Admin " + player.getName() + " trying to change player " + action.get("target")
				        + "'s name");
				player.sendPrivateText("Sorry, name cannot be changed.");
				return;
			}

			if (stat.equals("adminlevel")) {
				player.sendPrivateText("Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.");
				return;
			}

			RPClass clazz = changed.getRPClass();

			boolean isNumerical = false;

			byte type = clazz.getType(stat);
			if ((type == RPClass.BYTE) || (type == RPClass.SHORT) || (type == RPClass.INT)) {
				isNumerical = true;
			}

			if (changed.getRPClass().hasAttribute(stat)) {
				String value = action.get("value");
				String mode = action.get("mode");

				if (isNumerical) {
					int numberValue = Integer.parseInt(value);
					if (mode.equals("add")) {
						numberValue = changed.getInt(stat) + numberValue;
					}

					if (mode.equals("sub")) {
						numberValue = changed.getInt(stat) - numberValue;
					}

					if (stat.equals("hp") && (changed.getInt("base_hp") < numberValue)) {
						logger.error("DENIED: Admin " + player.getName() + " trying to set player "
						        + Grammar.suffix_s(action.get("target")) + " HP over its Base HP");
						return;
					}

					switch (type) {
						case RPClass.BYTE:
							if ((numberValue > Byte.MAX_VALUE) || (numberValue < Byte.MIN_VALUE)) {
								return;
							}
							break;
						case RPClass.SHORT:
							if ((numberValue > Short.MAX_VALUE) || (numberValue < Short.MIN_VALUE)) {
								return;
							}
							break;
						case RPClass.INT:
							if ((numberValue > Integer.MAX_VALUE) || (numberValue < Integer.MIN_VALUE)) {
								return;
							}
							break;
					}

					StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "alter", action.get("target"), stat,
					        Integer.toString(numberValue));
					changed.put(stat, numberValue);
				} else {
					// Can be only setif value is not a number
					if (mode.equals("set")) {
						StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "alter", action.get("target"),
						        stat, action.get("value"));
						changed.put(stat, action.get("value"));
					}
				}

				changed.update();
				changed.notifyWorldAboutChanges();
			}
		}

		Log4J.finishMethod(logger, "onChangePlayer");
	}

	private void onSummon(Player player, RPAction action) {
		Log4J.startMethod(logger, "onSummon");

		if (action.has("creature") && action.has("x") && action.has("y")) {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
			int x = action.getInt("x");
			int y = action.getInt("y");

			if (!zone.collides(player, x, y)) {
				EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();
				String type = action.get("creature");

				Entity entity = null;
				// Is the entity a creature
				if (manager.isCreature(type)) {
					StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "summon", type);
					entity = manager.getCreature(type);
					StendhalRPRuleProcessor.get().addNPC((Creature) entity);
				} else if (manager.isItem(type)) {
					StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "summon", type);
					entity = manager.getItem(type);
				}
				zone.assignRPObjectID(entity);
				StendhalRPAction.placeat(zone, entity, x, y);
				zone.add(entity);
			}
		}

		Log4J.finishMethod(logger, "onSummon");
	}

	private void onSummonAt(Player player, RPAction action) {
		Log4J.startMethod(logger, "onSummonAt");

		if (action.has("target") && action.has("slot") && action.has("item")) {
			String name = action.get("target");
			Player changed = StendhalRPRuleProcessor.get().getPlayer(name);

			if (changed == null) {
				logger.debug("Player \"" + name + "\" not found.");
				player.sendPrivateText("Player \"" + name + "\" not found.");
				return;
			}

			String slotName = action.get("slot");
//			if (!changed.hasSlot(slotName)) {
//				logger.debug("Player \"" + name + "\" does not have an RPSlot named \"" + slotName + "\".");
//				player.sendPrivateText("Player \"" + name + "\" does not have an RPSlot named \"" + slotName + "\".");
//				return;
//			}

//			RPSlot slot = changed.getSlot(slotName);

			EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();
			String type = action.get("item");

			// Is the entity an item
			if (manager.isItem(type)) {
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "summonat", changed.getName(),
				        slotName, type);
				Item item = manager.getItem(type);

				if (action.has("amount") && (item instanceof StackableItem)) {
					((StackableItem) item).setQuantity(action.getInt("amount"));
				}
				//slot.assignValidID(item);
				//slot.add(item);
				//changed.notifyWorldAboutChanges();

				if (! player.equip(slotName, item)) {
					player.sendPrivateText("The slot is full.");
				}
			} else {
				player.sendPrivateText("Not an item.");
			}

		}

		Log4J.finishMethod(logger, "onSummonAt");
	}

	private void onInvisible(Player player, RPAction action) {
		Log4J.startMethod(logger, "onInvisible");

		if (player.has("invisible")) {
			player.remove("invisible");
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "invisible", "off");
		} else {
			player.put("invisible", "");
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "invisible", "on");
		}
		Log4J.finishMethod(logger, "onInvisible");
	}

	private void onGhostMode(Player player, RPAction action) {
		Log4J.startMethod(logger, "onGhostMode");

		if (player.has("ghostmode")) {
			player.remove("ghostmode");
			player.returnToOriginalOutfit();
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "ghostmode", "off");
		} else {
			player.put("ghostmode", "");
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "ghostmode", "on");
			Outfit invisible = new Outfit(0, 98, 0, 98);
			player.setOutfit(invisible, true);
		}
		Log4J.finishMethod(logger, "onGhostMode");
	}

	private void onInspect(Player player, RPAction action) {
		Log4J.startMethod(logger, "onInspect");

		Entity target = getTarget(player, action);

		if (target == null) {
			String text = "Entity not found";
			player.sendPrivateText(text);
			return;
		}

		StringBuffer st = new StringBuffer();

		if (target instanceof RPEntity) {
			RPEntity inspected = (RPEntity) target;

			// It would be nice if the entity's type would be shown, but I don't
			// know if the type attribute is mandatory.
			//st.append("Inspected " + inspected.get("type") + " is called " + inspected.getName() + " and has attributes:");
			st.append("Inspected entity is called \"" + inspected.getName() + "\" and has the following attributes:");
			//			st.append(target.toString());
			//			st.append("\n===========================\n");
			st.append("\nID:     " + inspected.getID());
			st.append("\nATK:    " + inspected.getATK() + "(" + inspected.getATKXP() + ")");
			st.append("\nDEF:    " + inspected.getDEF() + "(" + inspected.getDEFXP() + ")");
			st.append("\nHP:     " + inspected.getHP() + " / " + inspected.getBaseHP());
			st.append("\nXP:     " + inspected.getXP());
			st.append("\nLevel:  " + inspected.getLevel());

			st.append("\nequips");
			for (RPSlot slot : inspected.slots()) {
				if (slot.getName().equals("!buddy") || slot.getName().equals("!ignore")) {
					continue;
				}
				st.append("\n    Slot " + slot.getName() + ": ");

				if (slot.getName().startsWith("!")) {
					for (RPObject object : slot) {
						st.append(object);
					}
				} else {
					for (RPObject object : slot) {
						if (!(object instanceof Item)) {
							continue;
						}

						String item = object.get("type");

						if (object.has("name")) {
							item = object.get("name");
						}
						if (object instanceof StackableItem) {
							st.append("[" + item + " Q=" + object.get("quantity") + "], ");
						} else {
							st.append("[" + item + "], ");
						}
					}
				}
			}
			if (inspected instanceof Player) {
				st.append("\r\n" + StendhalQuestSystem.get().listQuests((Player) inspected));
			}
		} else {
			st.append("Inspected entity has id " + action.getInt("targetid") + " and has attributes:\r\n");
			st.append(target.toString());
		}
		player.sendPrivateText(st.toString());
		Log4J.finishMethod(logger, "onInspect");
	}

	private void onDestroy(Player player, RPAction action) {
		Log4J.startMethod(logger, "onDestroy");

		Entity inspected = getTarget(player, action);

		if (inspected == null) {
			String text = "Entity not found";

			player.sendPrivateText(text);
			return;
		}

		if (inspected instanceof Player) {
			String text = "You can't remove players";
			player.sendPrivateText(text);
			return;
		}

		if (inspected instanceof RPEntity) {
			((RPEntity) inspected).onDead(player);
		} else if ((inspected instanceof Item) || (inspected instanceof Portal)) {
			StendhalRPWorld.get().remove(inspected.getID());
		}
		String name = inspected.getRPClass().getName();
		if (inspected.has("name")) {
			name = inspected.get("name");
		}
		StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "removed", name,
		        StendhalRPWorld.get().getRPZone(inspected.getID()).getID().getID(), Integer.toString(inspected.getX()),
		        Integer.toString(inspected.getY()));

		player.sendPrivateText("Removed entity " + action.get("targetid"));

		Log4J.finishMethod(logger, "onDestroy");
	}

	private void onJail(Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleport");

		if (action.has("target") && action.has("minutes")) {
			String target = action.get("target");
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				int minutes = action.getInt("minutes");
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "teleport", target,
				        Integer.toString(minutes), reason);
				Jail.get().imprison(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText("Usage: /jail name minutes reason");
			}
		} else {
			player.sendPrivateText("Usage: /jail name minutes reason");
		}

		Log4J.finishMethod(logger, "onTeleport");
	}

	/**
	 * get the Entity-object of the specified target
	 *
	 * @param player
	 * @param action
	 * @return the Entity or null if it does not exist
	 */
	private Entity getTarget(Player player, RPAction action) {

		String id = null;
		Entity target = null;

		// target contains a name unless it starts with #
		if (action.has("target")) {
			id = action.get("target");
		}
		if (id != null) {
			if (!id.startsWith("#")) {
				target = StendhalRPRuleProcessor.get().getPlayer(id);
				return target;
			} else {
				id = id.substring(1);
			}
		}

		// either target started with a # or it was not specified
		if (action.has("targetid")) {
			id = action.get("targetid");
		}

		// go for the id
		if (id != null) {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());

			RPObject.ID oid = new RPObject.ID(Integer.parseInt(id), zone.getID().getID());
			if (zone.has(oid)) {
				RPObject object = zone.get(oid);
				if (object instanceof Entity) {
					target = (Entity) object;
				}
			}
		}

		return target;
	}
}
