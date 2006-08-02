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

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPAction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
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
import marauroa.server.game.RPWorld;

import org.apache.log4j.Logger;

/**
 * Most /commands for admins are handled here. 
 */
public class AdministrationAction extends ActionListener {
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
		StendhalRPRuleProcessor.register("jail", administration);
		
		REQUIRED_ADMIN_LEVELS.put("adminlevel",   0);
		REQUIRED_ADMIN_LEVELS.put("support",    100);
		REQUIRED_ADMIN_LEVELS.put("supportanswer", 0);
		REQUIRED_ADMIN_LEVELS.put("tellall",    200);
		REQUIRED_ADMIN_LEVELS.put("teleportto", 300);
		REQUIRED_ADMIN_LEVELS.put("teleport",   400);
		REQUIRED_ADMIN_LEVELS.put("jail",       400);
		REQUIRED_ADMIN_LEVELS.put("invisible",  500);
		REQUIRED_ADMIN_LEVELS.put("inspect",    600);
		REQUIRED_ADMIN_LEVELS.put("destroy",    700);
		REQUIRED_ADMIN_LEVELS.put("summon",     800);
		REQUIRED_ADMIN_LEVELS.put("summonat",   800);
		REQUIRED_ADMIN_LEVELS.put("alter",      900);
		REQUIRED_ADMIN_LEVELS.put("super",     5000);
	}

    public static void registerCommandLevel (String command, int minLevel) {
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
				player.sendPrivateText("Sorry, command " + command + " is unknown.");
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
					player.sendPrivateText("Sorry, you need to be an admin to run " + command + ".");
				} else {
					player.sendPrivateText("Sorry, your admin level is "
						+ adminlevel + " but level " + required 
						+ " is required to run " + command + ".");
				}
			}
			return false;
		}

		// OK
		return true;
	}
	
	@Override
	public void onAction(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {

		String type = action.get("type");
		if (!isPlayerAllowedToExecuteAdminCommand(player, type, true)) {
			return;
		}

		if (type.equals("tellall")) {
			onTellEverybody(world, rules, player, action);
		} else if (type.equals("supportanswer")) {
			onSupportAnswer(world, rules, player, action);
		} else if (type.equals("teleport")) {
			onTeleport(world, rules, player, action);
		} else if (type.equals("teleportto")) {
			onTeleportTo(world, rules, player, action);
        } else if (type.equals("adminlevel")) {
            onAdminLevel(world, rules, player, action);
		} else if (type.equals("alter")) {
			onChangePlayer(world, rules, player, action);
		} else if (type.equals("summon")) {
			onSummon(world, rules, player, action);
		} else if (type.equals("summonat")) {
			onSummonAt(world, rules, player, action);
		} else if (type.equals("invisible")) {
			onInvisible(world, rules, player, action);
		} else if (type.equals("inspect")) {
			onInspect(world, rules, player, action);
		} else if (type.equals("destroy")) {
			onDestroy(world, rules, player, action);
		} else if (type.equals("jail")) {
			onJail(world, rules, player, action);
		}
	}

	private void onSupportAnswer(RPWorld world, StendhalRPRuleProcessor rules, Player player, RPAction action) {
		Log4J.startMethod(logger, "supportanswer");

		if (action.has("target") && action.has("text")) {
			String message = player.getName() + " answers " + action.get("target") + "'s support question: " + action.get("text");

			rules.addGameEvent(player.getName(), "supportanswer", action.get("target"), action.get("text"));

			boolean found = false;
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(action.get("target"))) {
					p.sendPrivateText("Support (" + player.getName() + ") tells you: " + action.get("text"));
					world.modify(p);
					found = true;
				}
				if (p.getAdminLevel() >= AdministrationAction.REQUIRED_ADMIN_LEVEL_FOR_SUPPORT) {
					p.sendPrivateText(message);
					world.modify(p);
				}
			}

			if (!found) {
				player.sendPrivateText(action.get("target")
						+ " is not currently logged in.");
			}
		}

		Log4J.finishMethod(logger, "supportanswer");
	}

	private void onTellEverybody(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onTellEverybody");

		if (action.has("text")) {
			String message = "Administrator SHOUTS: " + action.get("text");
			rules.addGameEvent(player.getName(), "tellall", action.get("text"));

			StendhalRPAction.shout(message);
		}

		Log4J.finishMethod(logger, "onTellEverybody");
	}

	private void onTeleport(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleport");

		if (action.has("target") && action.has("zone") && action.has("x")
				&& action.has("y")) {
			Player teleported = null;

			String name = action.get("target");
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(name)) {
					teleported = p;
					break;
				}
			}

			if (teleported == null) {
				String text = "Player " + name + " not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

            // validate the zone-name.
			IRPZone.ID zoneid = new IRPZone.ID(action.get("zone"));
			if (!world.hasRPZone(zoneid)) {
				String text = "Zone " + zoneid + " not found.";
                logger.debug(text);
                
                Set<String> zoneNames = new TreeSet<String>();
                Iterator itr = world.iterator();
                while (itr.hasNext()) {
                    StendhalRPZone zone = (StendhalRPZone) itr.next();
                    zoneNames.add(zone.getID().getID());
                }
				player.sendPrivateText(text + " Valid zones: " + zoneNames);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(zoneid);
			int x = action.getInt("x");
			int y = action.getInt("y");

			if (StendhalRPAction.placeat(zone, teleported, x, y)) {
				StendhalRPAction.changeZone(teleported, zone.getID().getID());
				StendhalRPAction.transferContent(teleported);

				rules.addGameEvent(player.getName(), "teleport", teleported
						.getName());

				world.modify(teleported);
			} else {
				player.sendPrivateText("Position [" + x + "," + y
						+ "] is occupied");
			}
		}

		Log4J.finishMethod(logger, "onTeleport");
	}

	private void onTeleportTo(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleportTo");

		if (action.has("target")) {
			Player teleported = null;

			String name = action.get("target");
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(name)) {
					teleported = p;
					break;
				}
			}

			if (teleported == null) {
				String text = "Player " + name + " not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(teleported
					.getID());
			int x = teleported.getx();
			int y = teleported.gety();

			if (StendhalRPAction.placeat(zone, player, x, y)) {
				rules.addGameEvent(player.getName(), "teleportto", teleported
						.getName());

				StendhalRPAction.changeZone(player, zone.getID().getID());
				StendhalRPAction.transferContent(player);
			}

			world.modify(player);
		}

		Log4J.finishMethod(logger, "onTeleportTo");
	}

    private void onAdminLevel(RPWorld world, StendhalRPRuleProcessor rules,
                    Player player, RPAction action) {
        Log4J.startMethod(logger, "onAdminLevel");
    
        if (action.has("target")) {

            Player target = null;
    
            String name = action.get("target");
            for (Player p : rules.getPlayers()) {
                if (p.getName().equals(name)) {
                    target = p;
                    break;
                }
            }
    
            if (target == null) {
                logger.debug("Player " + name + " not found");
                player.sendPrivateText("Player " + name + " not found");
                return;
            }
    
            int oldlevel = target.getAdminLevel();
            String response = target.getName() + " has adminlevel " + oldlevel;


            if (action.has("newlevel")) {
                // verify newlevel is a number
                int newlevel = Integer.parseInt(action.get("newlevel"));

                int mylevel = player.getAdminLevel();
                if (mylevel < REQUIRED_ADMIN_LEVEL_FOR_SUPER) {
                	response = "Sorry, but you need an adminlevel of " + REQUIRED_ADMIN_LEVEL_FOR_SUPER + " to change adminlevel";
                
                /*if (mylevel < oldlevel) {
                    response = "Sorry, but the adminlevel of " + target.getName() + " is " + oldlevel + " and your level is only " + mylevel;
                } else if (mylevel < newlevel) {
                    response = "Sorry, you cannot set an adminlevel of " + newlevel + " because your level is only " + mylevel;
                    */
                } else {
    
                    // OK, do the change
                    rules.addGameEvent(player.getName(), "adminlevel", target
                            .getName(), "adminlevel", action.get("newlevel"));
                    target.put("adminlevel", newlevel);
                    target.update();
                    world.modify(target);
        
                    response = "Changed adminlevel of " + target.getName() + " from " + oldlevel + " to " + newlevel;
                }
            }

            player.sendPrivateText(response);
        }
    
        Log4J.finishMethod(logger, "onAdminLevel");
    }

    
    private void onChangePlayer(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onChangePlayer");

		if (action.has("target") && action.has("stat") && action.has("mode")
				&& action.has("value")) {
			Player changed = null;

			String name = action.get("target");
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(name)) {
					changed = p;
					break;
				}
			}

			if (changed == null) {
				logger.debug("Player " + name + " not found");
				player.sendPrivateText("Player " + name + " not found");
				return;
			}

			String stat = action.get("stat");

			if (stat.equals("name")) {
				logger.error("DENIED: Admin " + player.getName()
						+ " trying to change player " + name + "'s name");
                player.sendPrivateText("name cannot be changed");
				return;
			}
            
            if (stat.equals("adminlevel")) {
                player.sendPrivateText("user /adminlevel <playername> [<newlevel>] to change adminlevel.");
                return;
            }

			RPClass clazz = changed.getRPClass();

			boolean isNumerical = false;

			byte type = clazz.getType(stat);
			if (type == RPClass.BYTE || type == RPClass.SHORT
					|| type == RPClass.INT) {
				isNumerical = true;
			}

			if (changed.getRPClass().hasAttribute(stat) && changed.has(stat)) {
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
							&& changed.getInt("base_hp") < numberValue) {
						logger.error("DENIED: Admin " + player.getName()
								+ " trying to set player " + name
								+ "'s HP over its Base HP");
						return;
					}

					switch (type) {
					case RPClass.BYTE:
						if (numberValue > Byte.MAX_VALUE
								|| numberValue < Byte.MIN_VALUE)
							return;
						break;
					case RPClass.SHORT:
						if (numberValue > Short.MAX_VALUE
								|| numberValue < Short.MIN_VALUE)
							return;
						break;
					case RPClass.INT:
						if (numberValue > Integer.MAX_VALUE
								|| numberValue < Integer.MIN_VALUE)
							return;
						break;
					}

					rules.addGameEvent(player.getName(), "alter", changed
							.getName(), stat, Integer.toString(numberValue));
					changed.put(stat, numberValue);
				} else {
					// Can be only setif value is not a number
					if (mode.equals("set")) {
						rules.addGameEvent(player.getName(), "alter", changed
								.getName(), stat, action.get("value"));
						changed.put(stat, action.get("value"));
					}
				}

				changed.update();
				world.modify(changed);
			}
		}

		Log4J.finishMethod(logger, "onChangePlayer");
	}

	private void onSummon(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onSummon");

		if (action.has("creature") && action.has("x") && action.has("y")) {
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player
					.getID());
			int x = action.getInt("x");
			int y = action.getInt("y");

			if (!zone.collides(player, x, y)) {
				EntityManager manager = ((StendhalRPWorld) world)
						.getRuleManager().getEntityManager();
				String type = action.get("creature");

				// Is the entity a creature
				if (manager.isCreature(type)) {
					rules.addGameEvent(player.getName(), "summon", type);
					Creature creature = manager.getCreature(type);

					zone.assignRPObjectID(creature);
					StendhalRPAction.placeat(zone, creature, x, y);
					zone.add(creature);

					rules.addNPC(creature);
				} else if (manager.isItem(type)) {
					rules.addGameEvent(player.getName(), "summon", type);
					Item item = manager.getItem(type);

					zone.assignRPObjectID(item);
					StendhalRPAction.placeat(zone, item, x, y);
					zone.add(item);
				}
			}
		}

		Log4J.finishMethod(logger, "onSummon");
	}

	private void onSummonAt(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onSummonAt");

		if (action.has("target") && action.has("slot") && action.has("item")) {
			Player changed = null;

			String name = action.get("target");
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(name)) {
					changed = p;
					break;
				}
			}

			if (changed == null) {
				logger.debug("Player " + name + " not found");
				return;
			}

			String slotName = action.get("slot");
			if (!changed.hasSlot(slotName)) {
				logger.debug("Player " + name + " has not RPSlot " + slotName);
				return;
			}

			RPSlot slot = changed.getSlot(slotName);

			if (!slot.isFull()) {
				EntityManager manager = ((StendhalRPWorld) world)
						.getRuleManager().getEntityManager();
				String type = action.get("item");

				// Is the entity an item
				if (manager.isItem(type)) {
					rules.addGameEvent(player.getName(), "summonat", changed
							.getName(), slot.getName(), type);
					Item item = manager.getItem(type);

					if (action.has("amount") && item instanceof StackableItem) {
						((StackableItem) item).setQuantity(action
								.getInt("amount"));
					}
					slot.assignValidID(item);
					slot.add(item);

					world.modify(changed);
				}
			}
		}

		Log4J.finishMethod(logger, "onSummonAt");
	}

	private void onInvisible(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onInvisible");

		if (player.has("invisible")) {
			player.remove("invisible");
		} else {
			player.put("invisible", "");
		}
		Log4J.finishMethod(logger, "onInvisible");
	}

	private void onInspect(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onInspect");

		RPEntity inspected = null;

		if (action.has("target")) {
			String name = action.get("target");
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(name)) {
					inspected = p;
					break;
				}
			}
		} else if (action.has("targetid")) {
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player
					.getID());

			RPObject.ID id = new RPObject.ID(action.getInt("targetid"), zone
					.getID().getID());
			if (zone.has(id)) {
				RPObject object = zone.get(id);
				if (object instanceof RPEntity) {
					inspected = (RPEntity) object;
				}
			}
		}

		if (inspected == null) {
			String text = "Entity not found";
			player.sendPrivateText(text);
			return;
		}

		StringBuffer st = new StringBuffer();
		// It would be nice if the entity's type would be shown, but I don't
		// know if the type attribute is mandatory.
		//st.append("Inspected " + inspected.get("type") + " is called " + inspected.getName() + " and has attributes:");
		st.append("Inspected entity is called " + inspected.getName() + " and has attributes:");
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
			if (slot.getName().equals("!buddy")) {
				continue;
			}
			st.append("\n    Slot " + slot.getName() + ": ");

			if (slot.getName().equals("!quests")
					|| slot.getName().equals("!kills")) {
				for (RPObject object : slot) {
					st.append(object);
				}
			} else {
				for (RPObject object : slot) {
					String item = object.get("type");
					if (object.has("name")) {
						item = object.get("name");
					}
					if (object instanceof StackableItem) {
						st.append("[" + item + " Q=" + object.get("quantity")
								+ "], ");
					} else {
						st.append("[" + item + "], ");
					}
				}
			}
		}
		player.sendPrivateText(st.toString());
		Log4J.finishMethod(logger, "onInspect");
	}

	private void onDestroy(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onDestroy");

		Entity inspected = null;

		if (action.has("targetid")) {
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player
					.getID());

			RPObject.ID id = new RPObject.ID(action.getInt("targetid"), zone
					.getID().getID());
			if (zone.has(id)) {
				RPObject object = zone.get(id);

				if (object instanceof Entity) {
					inspected = (Entity) object;
				}
			}
		}

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
		} else if (inspected instanceof Item) {
			world.remove(inspected.getID());
		}

		player.sendPrivateText("Removed entity " + action.get("targetid"));

		Log4J.finishMethod(logger, "onInspect");
	}

	private void onJail(RPWorld world, StendhalRPRuleProcessor rules,
			Player player, RPAction action) {
		Log4J.startMethod(logger, "onTeleport");

		if (action.has("target")) {
			Player teleported = null;

			String name = action.get("target");
			for (Player p : rules.getPlayers()) {
				if (p.getName().equals(name)) {
					teleported = p;
					break;
				}
			}

			if (teleported == null) {
				String text = "Player " + name + " not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			IRPZone.ID zoneid = new IRPZone.ID("-1_semos_jail");
			if (!world.hasRPZone(zoneid)) {
				String text = "Zone " + zoneid + " not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(zoneid);
			int x = 8;
			int y = 2;

			if (StendhalRPAction.placeat(zone, teleported, x, y)) {
				StendhalRPAction.changeZone(teleported, zone.getID().getID());
				StendhalRPAction.transferContent(teleported);
				teleported.setDirection(Direction.DOWN);

				rules.addGameEvent(player.getName(), "teleport", teleported
						.getName());

				world.modify(teleported);
			} else {
				player.sendPrivateText("Position [" + x + "," + y
						+ "] is occupied");
			}
		}

		Log4J.finishMethod(logger, "onTeleport");
	}

}
