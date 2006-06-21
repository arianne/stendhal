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
package games.stendhal.server;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.common.Line;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.item.*;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.pathfinder.Path.Node;
import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObjectNotFoundException;
import marauroa.server.game.NoRPZoneException;
import marauroa.server.game.RPServerManager;
import marauroa.server.game.RPWorld;

import java.util.List;
import java.util.Vector;
import java.awt.Point;
import java.awt.Rectangle;

import org.apache.log4j.Logger;

public class StendhalRPAction {
	/** the logger instance. */
	private static final Logger logger = Log4J
			.getLogger(StendhalRPAction.class);

	/** server manager */
	private static RPServerManager rpman;

	/** the rule processor. it is not used at the moment */
	@SuppressWarnings("unused")
	private static StendhalRPRuleProcessor rules;

	/** our world */
	private static RPWorld world;

	public static void initialize(RPServerManager rpman,
			StendhalRPRuleProcessor rules, RPWorld world) {
		StendhalRPAction.rpman = rpman;
		StendhalRPAction.rules = rules;
		StendhalRPAction.world = world;
	}

	public static boolean riskToHit(RPEntity source, RPEntity target) {
		boolean result = false;

		int roll = Rand.roll1D20();
		int risk = 2 * source.getATK() - target.getDEF() + roll - 10;

		if (logger.isDebugEnabled()) {
			logger.debug("attack from " + source + " to " + target
					+ ": Risk to strike: " + risk);
		}

		if (risk < 0) {
			risk = 0;
		}

		if (risk > 1) {
			risk = 1;
			result = true;
		}

		source.put("risk", risk);
		return result;
	}

	public static int damageDone(RPEntity source, RPEntity target) {
		int weapon = 0;
		int shield = 0;
		int armor = 0;
		int helmet = 0;
		int legs = 0;
		int boots = 0;
		int cloak = 0;
        int defWeapon = 0;

		Item weaponItem = source.getWeapon();
		StackableItem projectileItem = null;

		if (weaponItem != null) {
			weapon = weaponItem.getAttack();

			if (weaponItem.isOfClass("ranged")) {
				projectileItem = (StackableItem) source.getProjectiles();

				if (projectileItem != null) {
					weapon += projectileItem.getAttack();
				} else {
					// If there are no projectiles...
					return 0;
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("attacker has " + source.getATK()
					+ " and uses a weapon of " + weapon);
		}

		float maxAttackerComponent = 0.8f * source.getATK() * source.getATK()
				+ 4.0f * source.getATK() * weapon;
		float attackerComponent = (Rand.roll1D100() / 100.0f)
				* maxAttackerComponent;

		logger.debug("ATK MAX: " + maxAttackerComponent + "\t ATK VALUE: "
				+ attackerComponent);

		if (target.hasShield()) {
			shield = target.getShield().getDefense();
		}

		if (target.hasArmor()) {
			armor = target.getArmor().getDefense();
		}

		if (target.hasHelmet()) {
			helmet = target.getHelmet().getDefense();
		}

		if (target.hasLegs()) {
			legs = target.getLegs().getDefense();
		}

		if (target.hasBoots()) {
			boots = target.getBoots().getDefense();
		}

		if (target.hasCloak()) {
			cloak = target.getCloak().getDefense();
		}
        
        if (target.hasWeapon()) {
            defWeapon = target.getWeapon().getDefense();
        }

		if (logger.isDebugEnabled()) {
			logger.debug("defender has " + target.getDEF()
					+ " and uses shield of " + shield + " and armor of "
					+ armor);
		}

        int targetDef = target.getDEF();
		float maxDefenderComponent = 0.6f * target.getDEF() * target.getDEF()
				+ 4.0f * targetDef * shield
                + 2.0f * targetDef * armor
                + 1.5f * targetDef * cloak
				+ 1.0f * targetDef * helmet
                + 1.0f * targetDef * legs
				+ 1.0f * targetDef * boots
                + 1.0f * targetDef * defWeapon;
		
		float defenderComponent = (Rand.roll1D100() / 100.0f)
				* maxDefenderComponent;

		if (logger.isDebugEnabled()) {
			logger.debug("DEF MAX: " + maxDefenderComponent + "\t DEF VALUE: "
					+ defenderComponent);
		}

		int damage = (int) (((attackerComponent - defenderComponent) / maxAttackerComponent)
				* (maxAttackerComponent / maxDefenderComponent) * (source
				.getATK() / 10.0f));

		if (weaponItem != null && weaponItem.isOfClass("ranged")) {
			projectileItem.add(-1);

			if (projectileItem.getQuantity() == 0) {
				String[] slots = { "rhand", "lhand" };
				source.dropItemClass(slots, "projectiles");
			}

			double distance = source.distance(target);

			double minrange = 2 * 2;
			double maxrange = 7 * 7;
			int rangeDamage = (int) (damage * (1.0 - distance / maxrange) + (damage - damage
					* (1.0 - (minrange / maxrange)))
					* (1.0 - distance / maxrange));
			return rangeDamage;
		}

		return damage;
	}

	public static boolean attack(RPEntity source, RPEntity target)
			throws AttributeNotFoundException, NoRPZoneException,
			RPObjectNotFoundException {
		Log4J.startMethod(logger, "attack");
		boolean result = false;

		try {
			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(source
					.getID());
			if (!zone.has(target.getID()) || target.getHP() == 0) {
				logger.debug("Attack from " + source + " to " + target
						+ " stopped because target was lost("
						+ zone.has(target.getID()) + ") or dead.");
				target.onAttack(source, false);
				world.modify(source);

				return false;
			}

			target.onAttack(source, true);

			Item weaponItem = source.getWeapon();

			if (source.nextto(target, 1)
					|| (weaponItem != null && weaponItem.isOfClass("ranged"))) {
				if (weaponItem != null && weaponItem.isOfClass("ranged")) {
					// Check Line of View to see if there is any obstacle.
					Vector<Point> points = Line.renderLine(source.getx(),
							source.gety(), target.getx(), target.gety());
					for (Point point : points) {
						if (zone.collides((int) point.getX(), (int) point
								.getY())) {
							/**
							 * NOTE: Disabled to ease ranged combat.
							 * target.onAttack(source, false);
							 * world.modify(source);
							 */
							return false;
						}
					}
				}

				boolean hitted = riskToHit(source, target);

				if (source instanceof Player
						&& (target instanceof SpeakerNPC) == false
						&& source.stillHasBlood()) {
					// disabled attack xp for attacking NPC's
					source.incATKXP();
				}

				if (hitted) // Hit
				{
					if (target instanceof Player && target.stillHasBlood()) {
						target.incDEFXP();
					}

					int damage = damageDone(source, target);

					if (damage > 0) // Hit
					{
						target.onDamage(source, damage);
						source.put("damage", damage);
						logger.debug("attack from " + source.getID() + " to "
								+ target.getID() + ": Damage: " + damage);

						target.bloodHappens();

						result = true;
					} else // Blocked
					{
						source.put("damage", 0);
						logger.debug("attack from " + source.getID() + " to "
								+ target.getID() + ": Damage: " + 0);
					}
				} else // Missed
				{
					logger.debug("attack from " + source.getID() + " to "
							+ target.getID() + ": Missed");
					source.put("damage", 0);
				}

				world.modify(source);
				return result;
			} else {
				logger.debug("Attack from " + source + " to " + target
						+ " failed because target is not near.");
				return false;
			}
		} finally {
			Log4J.finishMethod(logger, "attack");
		}
	}

	public static void move(RPEntity entity) throws AttributeNotFoundException,
			NoRPZoneException {
		Log4J.startMethod(logger, "move");
		try {
			if (entity.stopped()) {
				return;
			}

			if (!entity.isMoveCompleted()) {
				logger.debug(entity.get("type") + ") move not completed");
				return;
			}

			int x = entity.getx();
			int y = entity.gety();

			Direction dir = entity.getDirection();
			int dx = dir.getdx();
			int dy = dir.getdy();

			StendhalRPZone zone = (StendhalRPZone) world.getRPZone(entity
					.getID());

			if (zone.collides(entity, x + dx, y + dy) == false) {
				logger.debug("Moving from (" + x + "," + y + ") to ("
						+ (x + dx) + "," + (y + dy) + ")");

				entity.setx(x + dx);
				entity.sety(y + dy);

				entity.setCollides(false);
				world.modify(entity);
			} else {
				if (entity instanceof Player) {
					Player player = (Player) entity;

					// If we are too far from sheep skip zone change
					Sheep sheep = null;
					if (player.hasSheep()) {
						sheep = (Sheep) world.get(player.getSheep());
					}

					if (!(sheep != null && player.distance(sheep) > 7 * 7)) {
						if (zone.leavesZone(player, x + dx, y + dy)) {
							logger.debug("Leaving zone from (" + x + "," + y
									+ ") to (" + (x + dx) + "," + (y + dy)
									+ ")");
							decideChangeZone(player, x + dx, y + dy);
							player.stop();
							world.modify(player);
							return;
						}

						for (Portal portal : zone.getPortals()) {
							if (player.nextto(portal, 0.25)
									&& player.facingto(portal)) {
								logger.debug("Using portal " + portal);
								portal.onUsed(player);
								// if(usePortal(player, portal))
								// {
								// transferContent(player);
								// }
								return;
							}
						}
					}
				}

				/* Collision */
				logger
						.debug("Collision at (" + (x + dx) + "," + (y + dy)
								+ ")");
				entity.setCollides(true);

				entity.stop();
				world.modify(entity);
			}
		} finally {
			Log4J.finishMethod(logger, "move");
		}
	}

	public static void transferContent(Player player)
			throws AttributeNotFoundException {
		Log4J.startMethod(logger, "transferContent");

		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());
		rpman.transferContent(player.getID(), zone.getContents());

		Log4J.finishMethod(logger, "transferContent");
	}

	public static void decideChangeZone(Player player, int x, int y)
			throws AttributeNotFoundException, NoRPZoneException {
		// String zoneid = player.get("zoneid");

		StendhalRPZone origin = (StendhalRPZone) world
				.getRPZone(player.getID());
		int player_x = x + origin.getx();
		int player_y = y + origin.gety();

		boolean found = false;

		for (IRPZone izone : world) {
			StendhalRPZone zone = (StendhalRPZone) izone;
			if (zone.isInterior() == false
					&& zone.getLevel() == origin.getLevel()) {
				if (zone
						.contains(player, origin.getLevel(), player_x, player_y)) {
					if (found) {
						logger.error("Already contained at :" + zone.getID());
					}

					found = true;
					logger.debug("Contained at :" + zone.getID());

					player.setx(player_x - zone.getx());
					player.sety(player_y - zone.gety());

					logger.debug(player.getName() + " pos would be ("
							+ player.getx() + "," + player.gety() + ")");

					changeZone(player, zone.getID().getID(), false);
					transferContent(player);
				}
			}
		}

		if (!found) {
			logger.warn("Unable to choose a new zone for player(" + player
					+ ")");
		}
	}

	public static boolean usePortal(Player player, Portal portal)
			throws AttributeNotFoundException, NoRPZoneException {
		Log4J.startMethod(logger, "usePortal");

		if (!player.nextto(portal, 0.25)) // Too far to use the portal
		{
			return false;
		}

		if (portal.getDestinationZone() == null) // This portal is incomplete
		{
			return false;
		}

		StendhalRPZone destZone = (StendhalRPZone) world
				.getRPZone(new IRPZone.ID(portal.getDestinationZone()));

		Portal dest = destZone.getPortal(portal.getDestinationNumber());
		player.setx(dest.getInt("x"));
		player.sety(dest.getInt("y"));
        dest.onUsedBackwards(player);

		player.stop();

		changeZone(player, portal.getDestinationZone(), true);

		Log4J.finishMethod(logger, "usePortal");
		return true;
	}

    
	public static boolean placeat(StendhalRPZone zone, Entity entity, int x,
			int y) {

		boolean res = placeatImpl(zone, entity, x, y, true);

		if (!res && entity instanceof Player) {
            // If no place can be found using strict collision checking,
            // we'll try a second time ignoring collisions caused by objects.
			// TODO: Is this realy a good idea?
			// res = placeatImpl(zone, entity, x, y, false);
		}
	    return res;
    }
    
    public static boolean placeatImpl(StendhalRPZone zone, Entity entity, int x,
                    int y, boolean checkObjectCollisition) {

        boolean found = false;

        if (zone.collides(entity, x, y)) {

            // We cannot place the entity on the orginal spot. Let's search 
        	// for a new destination up to maxDestination tiles in every way.
            int maxDestination = 20;
        	
            foundit:
            for (int k = 1; k <= maxDestination; k++) {
				for (int i = -k; i < k; i++) {
					for (int j = -k; j < k; j++) {

						int nx = x + i;
						int ny = y + j;
						if (!zone.collides(entity, nx, ny, checkObjectCollisition)) {
							
							// OK, we may place the entity on this spot.

				        	// We verify that there is a walkable path between the original
				        	// spot and the new destination. This is to prevent players to 
				        	// enter not allowed places by logging in on top of other players.
				        	// Or monsters to spawn on the other side of a wall.

							List<Node> path = Path.searchPath(entity, x, y, 
									new Rectangle (nx, ny, 1, 1), maxDestination*maxDestination);
							if (!path.isEmpty()) {

								// We found a place!
    							entity.setx(nx);
    							entity.sety(ny);
    
    							found = true;
                                break foundit; // break all for-loops
							}
						}
					}
				}
            }

            if (!found) {
                logger.debug("Unable to place " + entity + " at (" + x + "," + y + ")");
            }
		} else {
			entity.setx(x);
			entity.sety(y);

			found = true;
		}

        if (found) {
            if (entity instanceof Player) {
                Player player = (Player) entity;

                if (player.hasSheep()) {
                    Sheep sheep = (Sheep) world.get(player.getSheep());
                    placeat(zone, sheep, x + 1, y);
                    sheep.clearPath();
                    sheep.stop();
                }
            }
        }
        
		return found;
	}

	public static void changeZone(Player player, String destination)
			throws AttributeNotFoundException, NoRPZoneException {
		changeZone(player, destination, true);
	}

	private static void changeZone(Player player, String destination,
			boolean placePlayer) throws AttributeNotFoundException,
			NoRPZoneException {
		Log4J.startMethod(logger, "changeZone");

		rules.addGameEvent(player.getName(), "change zone", destination);

		player.clearPath();

		String source = player.getID().getZoneID();

		StendhalRPZone oldzone = (StendhalRPZone) world.getRPZone(player
				.getID());

		if (player.hasSheep()) {
			Sheep sheep = (Sheep) world.get(player.getSheep());

			player.removeSheep(sheep);

			world.changeZone(source, destination, sheep);
			world.changeZone(source, destination, player);

			player.setSheep(sheep);
		} else {
			world.changeZone(source, destination, player);
		}

		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(player.getID());

		if (placePlayer) {
			zone.placeObjectAtZoneChangePoint(oldzone, player);
		}

		placeat(zone, player, player.getInt("x"), player.getInt("y"));
		player.stop();
		player.stopAttack();

		if (player.hasSheep()) {
			Sheep sheep = (Sheep) world.get(player.getSheep());
			placeat(zone, sheep, player.getInt("x") + 1, player.getInt("y") + 1);
			sheep.clearPath();
			sheep.stop();
		}

		/*
		 * There isn't any world.modify because there is already considered
		 * inside the implicit world.add call at changeZone
		 */

		Log4J.finishMethod(logger, "changeZone");
	}
}
