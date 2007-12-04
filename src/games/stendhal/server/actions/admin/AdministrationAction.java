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
package games.stendhal.server.actions.admin;

import games.stendhal.common.Grammar;
import games.stendhal.server.GagManager;
import games.stendhal.server.Jail;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCentre;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import games.stendhal.server.rule.EntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import org.apache.log4j.Logger;
import marauroa.common.game.Definition;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;

/**
 * Most /commands for admins are handled here.
 */
public class AdministrationAction implements ActionListener {
	/*
	 * TODO: Refactor.
	 * This class is monstrously big.
	 * Split it in smaller more coherent classes.
	 */

	private static final Logger logger = Logger
			.getLogger(AdministrationAction.class);

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPPORT = 100;

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPER = 5000;

	private static final Map<String, Integer> REQUIRED_ADMIN_LEVELS = new HashMap<String, Integer>();

	public static void register() {
		/*
		 * TODO: Refactor.
		 * Make action definition and level a single event so there can't be one without the other.
		 */
		AdministrationAction administration = new AdministrationAction();
		CommandCentre.register("inspect", administration);
		CommandCentre.register("destroy", administration);
		CommandCentre.register("supportanswer", administration);
		CommandCentre.register("tellall", administration);
		CommandCentre.register("teleport", administration);
		CommandCentre.register("teleportto", administration);
		CommandCentre.register("adminlevel", administration);
		CommandCentre.register("alter", administration);
		CommandCentre.register("altercreature", administration);
		CommandCentre.register("summon", administration);
		CommandCentre.register("summonat", administration);
		CommandCentre.register("invisible", administration);
		CommandCentre.register("ghostmode", administration);
		CommandCentre.register("teleclickmode", administration);
		CommandCentre.register("jail", administration);
		CommandCentre.register("gag", administration);

		REQUIRED_ADMIN_LEVELS.put("adminlevel", 0);
		REQUIRED_ADMIN_LEVELS.put("support", 100);
		REQUIRED_ADMIN_LEVELS.put("supportanswer", 50);
		REQUIRED_ADMIN_LEVELS.put("tellall", 200);
		REQUIRED_ADMIN_LEVELS.put("teleportto", 300);
		REQUIRED_ADMIN_LEVELS.put("teleport", 400);
		REQUIRED_ADMIN_LEVELS.put("jail", 400);
		REQUIRED_ADMIN_LEVELS.put("gag", 400);
		REQUIRED_ADMIN_LEVELS.put("invisible", 500);
		REQUIRED_ADMIN_LEVELS.put("ghostmode", 500);
		REQUIRED_ADMIN_LEVELS.put("teleclickmode", 500);
		REQUIRED_ADMIN_LEVELS.put("inspect", 600);
		REQUIRED_ADMIN_LEVELS.put("destroy", 700);
		REQUIRED_ADMIN_LEVELS.put("summon", 800);
		REQUIRED_ADMIN_LEVELS.put("summonat", 800);
		REQUIRED_ADMIN_LEVELS.put("alter", 900);
		REQUIRED_ADMIN_LEVELS.put("altercreature", 900);
		REQUIRED_ADMIN_LEVELS.put("super", 5000);
	}

	public static void registerCommandLevel(String command, int minLevel) {
		REQUIRED_ADMIN_LEVELS.put(command, minLevel);
	}

	public static int getLevelForCommand(String command) {
		Integer val = REQUIRED_ADMIN_LEVELS.get(command);
		if (val == null) {
			return -1;
		}

		return val;
	}

	public static boolean isPlayerAllowedToExecuteAdminCommand(Player player,
			String command, boolean verbose) {
		// get adminlevel of player and required adminlevel for this command
		int adminlevel = player.getAdminLevel();
		Integer required = REQUIRED_ADMIN_LEVELS.get(command);

		// check that we know this command
		if (required == null) {
			logger.error("Unknown command " + command);
			if (verbose) {
				player.sendPrivateText("Sorry, command \"" + command
						+ "\" is unknown.");
			}
			return false;
		}

		if (adminlevel < required.intValue()) {
			// not allowed
			logger.warn("Player " + player.getName() + " with admin level "
					+ adminlevel + " tried to run admin command " + command
					+ " which requires level " + required + ".");

			// Notify the player if verbose is set.
			if (verbose) {

				// is this player an admin at all?
				if (adminlevel == 0) {
					player
							.sendPrivateText("Sorry, you need to be an admin to run \""
									+ command + "\".");
				} else {
					player.sendPrivateText("Your admin level is only "
							+ adminlevel + ", but a level of " + required
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

		/*
		 * Refactor.
		 * Bad smell but on the other hand the correct way of doing it may
		 * be even worse?
		 */
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
		} else if (type.equals("altercreature")) {
			onAlterCreature(player, action);
		} else if (type.equals("summon")) {
			onSummon(player, action);
		} else if (type.equals("summonat")) {
			onSummonAt(player, action);
		} else if (type.equals("invisible")) {
			onInvisible(player, action);
		} else if (type.equals("ghostmode")) {
			onGhostMode(player, action);
		} else if (type.equals("teleclickmode")) {
			onTeleClickMode(player, action);
		} else if (type.equals("inspect")) {
			onInspect(player, action);
		} else if (type.equals("destroy")) {
			onDestroy(player, action);
		} else if (type.equals("jail")) {
			onJail(player, action);
		} else if (type.equals("gag")) {
			onGag(player, action);
		}
	}

	private void onSupportAnswer(Player player, RPAction action) {
		if (action.has("target") && action.has("text")) {
			String message = player.getTitle() + " answers "
					+ Grammar.suffix_s(action.get("target"))
					+ " support question: " + action.get("text");

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"supportanswer", action.get("target"), action.get("text"));

			boolean found = false;
			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				if (p.getTitle().equals(action.get("target"))) {
					p.sendPrivateText("Support (" + player.getTitle()
							+ ") tells you: " + action.get("text"));
					p.notifyWorldAboutChanges();
					found = true;
				}
				if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
					p.sendPrivateText(message);
					p.notifyWorldAboutChanges();
				}
			}

			if (!found) {
				player.sendPrivateText(action.get("target")
						+ " is not currently logged in.");
			}
		}
	}

	private void onTellEverybody(Player player, RPAction action) {
		if (action.has("text")) {
			String message = "Administrator SHOUTS: " + action.get("text");
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"tellall", action.get("text"));

			StendhalRPAction.shout(message);
		}
	}

	private void onTeleport(Player player, RPAction action) {
		if (action.has("target") && action.has("zone") && action.has("x")
				&& action.has("y")) {
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
				for(IRPZone irpZone : StendhalRPWorld.get()) {
					StendhalRPZone zone = (StendhalRPZone) irpZone;
					zoneNames.add(zone.getName());
				}
				player.sendPrivateText(text + " Valid zones: " + zoneNames);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
					.getRPZone(zoneid);
			int x = action.getInt("x");
			int y = action.getInt("y");

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleport", action.get("target"), zone.getName(),
					Integer.toString(x), Integer.toString(y));
			teleported.teleport(zone, x, y, null, player);
		}
	}

	private void onTeleportTo(Player player, RPAction action) {
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

			StendhalRPZone zone = teleported.getZone();
			int x = teleported.getX();
			int y = teleported.getY();

			player.teleport(zone, x, y, null, player);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleportto", action.get("target"), zone.getName(),
					Integer.toString(x), Integer.toString(y));
		}
	}

	private void onAdminLevel(Player player, RPAction action) {

		if (action.has("target")) {

			String name = action.get("target");
			Player target = StendhalRPRuleProcessor.get().getPlayer(name);

			if (target == null) {
				logger.debug("Player \"" + name + "\" not found");
				player.sendPrivateText("Player \"" + name + "\" not found");
				return;
			}

			int oldlevel = target.getAdminLevel();
			String response = target.getTitle() + " has adminlevel " + oldlevel;

			if (action.has("newlevel")) {
				// verify newlevel is a number
				int newlevel;
				try {
					newlevel = Integer.parseInt(action.get("newlevel"));
				} catch (NumberFormatException e) {
					player
							.sendPrivateText("The new adminlevel needs to be an Integer");

					return;
				}

				// Check level is on the range
				int max = 0;

				for (int level : REQUIRED_ADMIN_LEVELS.values()) {
					if (level > max) {
						max = level;
					}
				}

				// If level is beyond max level, just set it to max.
				if (newlevel > max) {
					newlevel = max;
				}

				int mylevel = player.getAdminLevel();
				if (mylevel < REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
					response = "Sorry, but you need an adminlevel of "
							+ REQUIRED_ADMIN_LEVEL_FOR_SUPER
							+ " to change adminlevel.";

					/*
					 * if (mylevel < oldlevel) { response = "Sorry, but the
					 * adminlevel of " + target.getTitle() + " is " + oldlevel + ",
					 * and your level is only " + mylevel + "."; } else if
					 * (mylevel < newlevel) { response = "Sorry, you cannot set
					 * an adminlevel of " + newlevel + ", because your level is
					 * only " + mylevel + ".";
					 */
				} else {

					// OK, do the change
					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "adminlevel", target.getName(),
							"adminlevel", action.get("newlevel"));
					target.setAdminLevel(newlevel);
					target.update();
					target.notifyWorldAboutChanges();

					response = "Changed adminlevel of " + target.getTitle()
							+ " from " + oldlevel + " to " + newlevel + ".";
					target.sendPrivateText(player.getTitle()
							+ " changed your adminlevel from " + +oldlevel
							+ " to " + newlevel + ".");
				}
			}

			player.sendPrivateText(response);
		}
	}

	private void onAlter(Player player, RPAction action) {

		if (action.has("target") && action.has("stat") && action.has("mode")
				&& action.has("value")) {
			Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			String stat = action.get("stat");

			if (stat.equals("name") && (changed instanceof Player)) {
				logger.error("DENIED: Admin " + player.getName()
						+ " trying to change player " + action.get("target")
						+ "'s name");
				player.sendPrivateText("Sorry, name cannot be changed.");
				return;
			}

			if (stat.equals("adminlevel")) {
				player
						.sendPrivateText("Use #/adminlevel #<playername> #[<newlevel>] to display or change adminlevel.");
				return;
			}

			if (stat.equals("title") && (changed instanceof Player)) {
				player.sendPrivateText("The title attribute may not be changed directly.");
				return;
			}

			RPClass clazz = changed.getRPClass();

			boolean isNumerical = false;

			Definition type = clazz.getDefinition(DefinitionClass.ATTRIBUTE,
					stat);
			if (type == null) {
				player
						.sendPrivateText("Attribute you are altering is not defined in RPClass("
								+ changed.getRPClass().getName() + ")");
				return;
			}

			if ((type.getType() == Type.BYTE) || (type.getType() == Type.SHORT)
					|| (type.getType() == Type.INT)) {
				isNumerical = true;
			}

			if (changed.getRPClass().hasDefinition(DefinitionClass.ATTRIBUTE,
					stat)) {
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

					if (stat.equals("hp")
							&& (changed.getInt("base_hp") < numberValue)) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player "
								+ Grammar.suffix_s(action.get("target"))
								+ " HP over its Base HP");
						return;
					}

					if (stat.equals("hp") && numberValue == 0) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player "
								+ Grammar.suffix_s(action.get("target"))
								+ " HP to 0, making it so unkillable.");
						return;
					}

					switch (type.getType()) {
					case BYTE:
						if ((numberValue > Byte.MAX_VALUE)
								|| (numberValue < Byte.MIN_VALUE)) {
							return;
						}
						break;
					case SHORT:
						if ((numberValue > Short.MAX_VALUE)
								|| (numberValue < Short.MIN_VALUE)) {
							return;
						}
						break;
					case INT:
						/* as numberValue is currently of type integer, this is pointless:
						if ((numberValue > Integer.MAX_VALUE)
								|| (numberValue < Integer.MIN_VALUE)) {
							return;
						}*/
						break;
					}

					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "alter", action.get("target"),
							stat, Integer.toString(numberValue));
					changed.put(stat, numberValue);
				} else {
					// Can be only setif value is not a number
					if (mode.equals("set")) {
						StendhalRPRuleProcessor.get()
								.addGameEvent(player.getName(), "alter",
										action.get("target"), stat,
										action.get("value"));
						changed.put(stat, action.get("value"));
					}
				}

				changed.update();
				changed.notifyWorldAboutChanges();
			}
		}
	}

	private void onAlterCreature(Player player, RPAction action) {

		if (action.has("target") && action.has("text")) {
			Entity changed = getTarget(player, action);

			if (changed == null) {
				logger.debug("Entity not found");
				player.sendPrivateText("Entity not found");
				return;
			}

			/*
			 * It will contain a string like: name/atk/def/hp/xp
			 */
			String stat = action.get("text");

			String[] parts = stat.split("/");
			if (changed instanceof Creature && parts.length == 5) {
				Creature creature = (Creature) changed;
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"alter", action.get("target"), stat);

				creature.setName(parts[0]);
				creature.setATK(Integer.parseInt(parts[1]));
				creature.setDEF(Integer.parseInt(parts[2]));
				creature.initHP(Integer.parseInt(parts[3]));
				creature.setXP(Integer.parseInt(parts[4]));

				creature.update();
				creature.notifyWorldAboutChanges();
			}
		}
	}

	private void onSummon(Player player, RPAction action) {

		if (action.has("creature") && action.has("x") && action.has("y")) {
			StendhalRPZone zone = player.getZone();
			int x = action.getInt("x");
			int y = action.getInt("y");

			if (!zone.collides(player, x, y)) {
				EntityManager manager = StendhalRPWorld.get().getRuleManager()
						.getEntityManager();
				String type = action.get("creature");

				Entity entity = null;
				// Is the entity a creature
				if (manager.isCreature(type)) {
					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "summon", type);
					entity = new RaidCreature(manager.getCreature(type));
				} else if (manager.isItem(type)) {
					StendhalRPRuleProcessor.get().addGameEvent(
							player.getName(), "summon", type);
					entity = manager.getItem(type);
				}

				if (entity == null) {
					logger.info("onSummon: Entity \"" + type + "\" not found.");
					player.sendPrivateText("onSummon: Entity \"" + type
							+ "\" not found.");
					return;
				}

				StendhalRPAction.placeat(zone, entity, x, y);
			}
		}
	}

	private void onSummonAt(Player player, RPAction action) {

		if (action.has("target") && action.has("slot") && action.has("item")) {
			String name = action.get("target");
			Player changed = StendhalRPRuleProcessor.get().getPlayer(name);

			if (changed == null) {
				logger.debug("Player \"" + name + "\" not found.");
				player.sendPrivateText("Player \"" + name + "\" not found.");
				return;
			}

			String slotName = action.get("slot");
			if (!changed.hasSlot(slotName)) {
				logger.debug("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				player.sendPrivateText("Player \"" + name
						+ "\" does not have an RPSlot named \"" + slotName
						+ "\".");
				return;
			}

			EntityManager manager = StendhalRPWorld.get().getRuleManager()
					.getEntityManager();
			String type = action.get("item");

			// Is the entity an item
			if (manager.isItem(type)) {
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"summonat", changed.getName(), slotName, type);
				Item item = manager.getItem(type);

				if (action.has("amount") && (item instanceof StackableItem)) {
					((StackableItem) item).setQuantity(action.getInt("amount"));
				}

				if (!changed.equip(slotName, item)) {
					player.sendPrivateText("The slot is full.");
				}
			} else {
				player.sendPrivateText("Not an item.");
			}
		}
	}

	private void onInvisible(Player player, RPAction action) {

		if (player.isInvisible()) {
			player.setInvisible(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"invisible", "off");
		} else {
			player.setInvisible(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"invisible", "on");
		}
	}

	private void onGhostMode(Player player, RPAction action) {

		if (player.isGhost()) {
			player.setGhost(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"ghostmode", "off");

			player.setInvisible(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"invisible", "off");

			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				p.notifyOnline(player.getName());
			}
		} else {
			/*
			 * When we enter ghostmode we want our player to be also invisible.
			 */
			player.setInvisible(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"invisible", "on");

			player.setGhost(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"ghostmode", "on");

			for (Player p : StendhalRPRuleProcessor.get().getPlayers()) {
				p.notifyOffline(player.getName());
			}
		}

		player.notifyWorldAboutChanges();
	}

	private void onTeleClickMode(Player player, RPAction action) {

		if (player.isTeleclickEnabled()) {
			player.setTeleclickEnabled(false);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleclickmode", "off");
		} else {
			player.setTeleclickEnabled(true);
			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleclickmode", "on");
		}
	}

	private void onInspect(Player player, RPAction action) {

		Entity target = getTarget(player, action);

		if (target == null) {
			String text = "Entity not found";
			player.sendPrivateText(text);
			return;
		}

		StringBuilder st = new StringBuilder();

		if (target instanceof RPEntity) {
			RPEntity inspected = (RPEntity) target;

			 // display type and name of the entity if they are available

			String type = inspected.get("type");
			st.append("Inspected " + (type!=null? type: "entity") + " is ");

			String name = inspected.getName();
			st.append(name!=null? "called \""+name+"\"": "unnamed");

			st.append(" and has the following attributes:");

			// st.append(target.toString());
			// st.append("\n===========================\n");

			st.append("\nID:     " + inspected.getID());
			st.append("\nATK:    " + inspected.getATK() + "("
					+ inspected.getATKXP() + ")");
			st.append("\nDEF:    " + inspected.getDEF() + "("
					+ inspected.getDEFXP() + ")");
			st.append("\nHP:     " + inspected.getHP() + " / "
					+ inspected.getBaseHP());
			st.append("\nXP:     " + inspected.getXP());
			st.append("\nLevel:  " + inspected.getLevel());

			st.append("\nequips");
			for (RPSlot slot : inspected.slots()) {
				if (slot.getName().equals("!buddy")
						|| slot.getName().equals("!ignore")) {
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
							st.append("[" + item + " Q="
									+ object.get("quantity") + "], ");
						} else {
							st.append("[" + item + "], ");
						}
					}
				}
			}
		} else {
			st.append("Inspected entity has id " + action.getInt("targetid")
					+ " and has attributes:\r\n");
			st.append(target.toString());
		}

		player.sendPrivateText(st.toString());
	}

	private void onDestroy(Player player, RPAction action) {

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

		if (inspected instanceof SpeakerNPC) {
			String text = "You can't remove SpeakerNPCs";
			player.sendPrivateText(text);
			return;
		}

		StendhalRPZone zone = inspected.getZone();

		if (inspected instanceof RPEntity) {
			((RPEntity) inspected).onDead(player);
		} else if ((inspected instanceof Item) || (inspected instanceof Portal)) {
			zone.remove(inspected);
		} else {
			player.sendPrivateText("You can't remove this type of entity");
			return;
		}

		String name = inspected.getRPClass().getName();
		if (inspected.has("name")) {
			name = inspected.get("name");
		}

		StendhalRPRuleProcessor.get().addGameEvent(
				player.getName(),
				"removed",
				name,
				zone.getName(),
				Integer.toString(inspected.getX()),
				Integer.toString(inspected.getY()));

		player.sendPrivateText("Removed entity " + action.get("targetid"));
	}

	private void onJail(Player player, RPAction action) {

		if (action.has("target") && action.has("minutes")) {
			String target = action.get("target");
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				int minutes = action.getInt("minutes");
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"jail", target, Integer.toString(minutes), reason);
				Jail.get().imprison(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText("Usage: /jail name minutes reason");
			}
		} else {
			player.sendPrivateText("Usage: /jail name minutes reason");
		}

	}

	private void onGag(Player player, RPAction action) {

		if (action.has("target") && action.has("minutes")) {
			String target = action.get("target");
			String reason = "";
			if (action.has("reason")) {
				reason = action.get("reason");
			}
			try {
				int minutes = action.getInt("minutes");
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
						"gag", target, Integer.toString(minutes), reason);
				GagManager.get().gag(target, player, minutes, reason);
			} catch (NumberFormatException e) {
				player.sendPrivateText("Usage: /gag name minutes reason");
			}
		} else {
			player.sendPrivateText("Usage: /gag name minutes reason");
		}
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
			StendhalRPZone zone = player.getZone();

			RPObject.ID oid = new RPObject.ID(Integer.parseInt(id), zone.getName());
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
