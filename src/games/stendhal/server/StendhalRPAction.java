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
import games.stendhal.common.Grammar;
import games.stendhal.common.Line;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.pathfinder.Path.Node;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.List;
import java.util.Vector;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObjectNotFoundException;
import marauroa.server.game.NoRPZoneException;
import marauroa.server.game.RPServerManager;

import org.apache.log4j.Logger;

public class StendhalRPAction {
	/** the logger instance. */
	private static final Logger logger = Log4J.getLogger(StendhalRPAction.class);

	/** server manager */
	private static RPServerManager rpman;

	public static void initialize(RPServerManager rpman) {
		StendhalRPAction.rpman = rpman;
	}

	public static boolean riskToHit(RPEntity source, RPEntity target) {
		boolean result = false;

		int roll = Rand.roll1D20();
		int risk = 2 * source.getATK() - target.getDEF() + roll - 10;

		/*
		 * Apply some karma
		 */
		double karma = source.getKarma(0.3) - target.getKarma(0.3);

		if(karma > 0.2)
			risk += 4;
		else if(karma > 0.1)
			risk++;
		else if(karma < -0.2)
			risk -= 4;
		else if(karma < -0.1)
			risk--;

		if (logger.isDebugEnabled()) {
			logger.debug("attack from " + source + " to " + target + ": Risk to strike: " + risk);
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

		float weapon = source.getItemAtk();
		StackableItem projectileItem = source.getProjectilesIfRangeCombat();

		if (logger.isDebugEnabled()) {
			logger.debug("attacker has " + source.getATK() + " and uses a weapon of " + weapon);
		}

		int sourceAtk = source.getATK();
		float maxAttackerComponent = 0.8f * sourceAtk * sourceAtk + weapon * sourceAtk;
		float attackerComponent = (Rand.roll1D100() / 100.0f) * maxAttackerComponent;

		/*
		 * Account for karma (+/-10%)
		 */
		attackerComponent +=
			(attackerComponent * (float) source.getKarma(0.1));

		logger.debug("ATK MAX: " + maxAttackerComponent + "\t ATK VALUE: " + attackerComponent);


		float armor = target.getItemDef();
		int targetDef = target.getDEF();
		float maxDefenderComponent = 0.6f * targetDef * targetDef + armor * targetDef;

		float defenderComponent = (Rand.roll1D100() / 100.0f) * maxDefenderComponent;

		/*
		 * Account for karma (+/-10%)
		 */
		defenderComponent +=
			(defenderComponent * (float) target.getKarma(0.1));

		if (logger.isDebugEnabled()) {
			logger.debug("DEF MAX: " + maxDefenderComponent + "\t DEF VALUE: " + defenderComponent);
		}

		int damage = (int) (((attackerComponent - defenderComponent) / maxAttackerComponent) * (maxAttackerComponent / maxDefenderComponent) * (source.getATK() / 10.0f));

		if (projectileItem != null) {
			projectileItem.add(-1);

			if (projectileItem.getQuantity() == 0) {
				String[] slots = { "rhand", "lhand" };
				source.dropItemClass(slots, "projectiles");
			}

			double distance = source.squaredDistance(target);

			double minrange = 2 * 2;
			double maxrange = 7 * 7;
			int rangeDamage = (int) (damage * (1.0 - distance / maxrange) + (damage - damage * (1.0 - (minrange / maxrange))) * (1.0 - distance / maxrange));
			// limit damage to target hp
			return Math.min(rangeDamage, target.getHP());
		}
		// limit damage to target hp
		return Math.min(damage, target.getHP());
	}


	/**
	 * Do logic for starting an attack on an entity.
	 *
	 * @param	player		The player wanting to attack.
	 * @param	entity		The target of attack.
	 */
	public static void startAttack(Player player, RPEntity entity) {
		/*
		 * Player's can't attack themselves
		 */
		if (player.equals(entity)) {
			return;
		}

		// Disable attacking NPCS.
		// Just make sure no creature is instanceof SpeakerNPC...
		if (entity instanceof SpeakerNPC) {
			logger.info("REJECTED. " + player.getName()
				+ " is attacking " + entity.getName());
			return;
		}

		// Enabled PVP
		if (entity instanceof Player || entity instanceof Sheep) {
			StendhalRPZone zone = (StendhalRPZone)
				StendhalRPWorld.get().getRPZone(player.getID());

			if (zone.isInProtectionArea(entity)) {
				logger.info("REJECTED. " + entity.getName()
					+ " is in a protection zone");

				String name = entity.getName();

				if (entity instanceof Sheep) {
					Player owner = ((Sheep) entity).getOwner();
					if (name != null) {
						name = Grammar.suffix_s(owner.getName()) + " sheep";
					} else {
						name = "that sheep";
					}
				}

				player.sendPrivateText("The powerful protective aura in this place prevents you from attacking " + name + ".");
				return;
			}

			logger.info(player.getName() + " is attacking "
					+ entity.getName());
		}

		StendhalRPRuleProcessor.get().addGameEvent(
			player.getName(), "attack", entity.getName());

		player.attack(entity);
		player.faceTo(entity);
		player.applyClientDirection(false);
		player.notifyWorldAboutChanges();
	}


	public static boolean attack(RPEntity source, RPEntity target) throws AttributeNotFoundException, NoRPZoneException, RPObjectNotFoundException {
		//Log4J.startMethod(logger, "attack");
		boolean result = false;

		try {
			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(source.getID());
			if (!zone.has(target.getID()) || target.getHP() == 0) {
				logger.debug("Attack from " + source + " to " + target + " stopped because target was lost(" + zone.has(target.getID()) + ") or dead.");
				target.onAttack(source, false);
				source.notifyWorldAboutChanges();

				return false;
			}

			target.onAttack(source, true);

			if(source.nextTo(target)) {
				// Continue (skip range checks if next to)
			} else if(source.canDoRangeAttacks()) {
				// XXX - Should different weapons have different ranges??

				// Check Line of View to see if there is any obstacle.
				Vector<Point> points = Line.renderLine(source.getX(), source.getY(), target.getX(), target.getY());
				for (Point point : points) {
					if (zone.collides((int) point.getX(), (int) point.getY())) {
						/**
						 * NOTE: Disabled to ease ranged combat.
						 * target.onAttack(source, false);
						 * world.modify(source);
						 */
						return false;
					}
				}
			} else {
				logger.debug("Attack from " + source + " to " + target + " failed because target is not near.");
				return false;
			}

			// {lifesteal} uncomented following line, also changed name:
			List<Item> weapons = source.getWeapons();

			if (source instanceof Player && (target instanceof SpeakerNPC) == false && source.stillHasBlood(target)) {
				// disabled attack xp for attacking NPC's
				source.incATKXP();
			}

			boolean beaten = riskToHit(source, target);

			if (beaten) {
				if (target instanceof Player && target.stillHasBlood(source)) {
					target.incDEFXP();
				}

				int damage = damageDone(source, target);
				if (damage > 0) {
					damage = handleLivesteal(source, weapons, damage);
						
					target.onDamage(source, damage);
					source.put("damage", damage);
					logger.debug("attack from " + source.getID() + " to " + target.getID() + ": Damage: " + damage);

					target.bloodHappens(source);

					result = true;
				} else {
					// Blocked
					source.put("damage", 0);
					logger.debug("attack from " + source.getID() + " to " + target.getID() + ": Damage: " + 0);
				}
			} else { // Missed
				logger.debug("attack from " + source.getID() + " to " + target.getID() + ": Missed");
				source.put("damage", 0);
			}

			source.notifyWorldAboutChanges();

			return result;
		} finally {
			//	Log4J.finishMethod(logger, "attack");
		}
	}

	/**
	 * Calculate lifesteal and update hp of source
	 *
	 * @param source        the RPEntity doing the hit
	 * @param sourceWeapons the weapons of the RPEntity doing the hit
	 * @param damage        the damage done by this hit.
	 * @return damage (may be altered inside this method)
	 */
	private static int handleLivesteal(RPEntity source, List<Item> sourceWeapons, int damage) {

		// Calcualte the lifesteal value based on the configured factor
		// In case of a lifesteal weapon used together with a non-lifesteal weapon,
		// weight it based on the atk-values of the weapons.
		float sumAll = 0;
		float sumLifesteal = 0;

		// Creature with lifesteal profile?
		if (source instanceof Creature) {
			sumAll = 1;
			String temp = ((Creature) source).getAIProfile("lifesteal");
			if (temp == null) {
				return damage;
			}
			sumLifesteal = Float.parseFloat(temp);
		} else {

			// weapons with lifesteal attribute for players
			for (Item weaponItem : sourceWeapons) {
				sumAll += weaponItem.getAttack();
				if (weaponItem.has("lifesteal")) {
					sumLifesteal += weaponItem.getAttack() * weaponItem.getDouble("lifesteal");
				}
			}
		}

		// process the lifesteal
		if (sumLifesteal != 0) {
			// 0.5f is used for rounding
			int lifesteal = (int) (damage * sumLifesteal / sumAll + 0.5f);
			lifesteal = Math.min(lifesteal, source.getBaseHP() - source.getHP());
			int newHP = source.getHP() + lifesteal;
			if (newHP > 1) {
				source.setHP(newHP);
				if (lifesteal > 0) {
					source.put("heal", lifesteal);
				}
			} else {
				source.setHP(1);
				damage = damage / 2;
			}
			source.notifyWorldAboutChanges();
		}
		return damage;
	}

	public static void move(RPEntity entity) throws AttributeNotFoundException, NoRPZoneException {
		//Log4J.startMethod(logger, "move");
		try {
			if (entity.stopped()) {
				return;
			}

			int x = entity.getX();
			int y = entity.getY();

			Direction dir = entity.getDirection();
			int dx = dir.getdx();
			int dy = dir.getdy();

			int nx = x + dx;
			int ny = y + dy;

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(entity.getID());
			boolean collision = zone.collides(entity, nx, ny);
			boolean ignoreCollision = !entity.isObstacle();
			
			if (collision) {
				if (entity instanceof Player) {
					Player player = (Player) entity;

					// If we are too far from sheep skip zone change
					Sheep sheep = null;
					if (player.hasSheep()) {
						sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
					}

					if (!(sheep != null && player.squaredDistance(sheep) > 7 * 7)) {
						if (zone.leavesZone(player, nx, ny)) {
							logger.debug("Leaving zone from (" + x + "," + y + ") to (" + nx + "," + ny + ")");
							decideChangeZone(player, nx, ny);
							player.stop();
							player.notifyWorldAboutChanges();
							return;
						}

						for (Portal portal : zone.getPortals()) {
							if (player.nextTo(portal) && player.facingTo(portal)) {
								logger.debug("Using portal " + portal);
								portal.onUsed(player);
								return;
							}
						}
					}
				}
			}
			if (!collision || ignoreCollision) {
				if (!entity.isMoveCompleted()) {
					logger.debug(entity.get("type") + ") move not completed");
					return;
				}

				if(logger.isDebugEnabled())
					logger.debug("Moving from (" + x + "," + y + ") to (" + nx + "," + ny + ")");

				entity.setX(nx);
				entity.setY(ny);

				entity.setCollides(false);
				zone.notifyMovement(entity, x, y, nx, ny);

				entity.notifyWorldAboutChanges();
			} else {
				/* Collision */
				if(logger.isDebugEnabled())
					logger.debug("Collision at (" + nx + "," + ny + ")");
				entity.setCollides(true);

				entity.stop();
				entity.notifyWorldAboutChanges();
			}
		} finally {
			//	Log4J.finishMethod(logger, "move");
		}
	}

	public static void transferContent(Player player) throws AttributeNotFoundException {
		Log4J.startMethod(logger, "transferContent");

		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
		rpman.transferContent(player.getID(), zone.getContents());

		Log4J.finishMethod(logger, "transferContent");
	}

	public static void decideChangeZone(Player player, int x, int y) throws AttributeNotFoundException, NoRPZoneException {
		// String zoneid = player.get("zoneid");

		StendhalRPZone origin = (StendhalRPZone) StendhalRPWorld.get().getRPZone(player.getID());
		int player_x = x + origin.getX();
		int player_y = y + origin.getY();

		boolean found = false;

		for (IRPZone izone : StendhalRPWorld.get()) {
			StendhalRPZone zone = (StendhalRPZone) izone;
			if (zone.isInterior() == false && zone.getLevel() == origin.getLevel()) {
				if (zone.contains(player, origin.getLevel(), player_x, player_y)) {
					if (found) {
						logger.error("Already contained at :" + zone.getID());
					}

					found = true;
					logger.debug("Contained at :" + zone.getID());

					player.setX(player_x - zone.getX());
					player.setY(player_y - zone.getY());

					logger.debug(player.getName() + " pos would be (" + player.getX() + "," + player.getY() + ")");

					changeZone(player, zone.getID().getID(), false);
					transferContent(player);
				}
			}
		}

		if (!found) {
			logger.warn("Unable to choose a new zone for player " + player.getName() + " at (" + player_x + "," + player_y + ") source was " + origin.getID().getID() + " at (" + x + ", " + y + ")");
		}
	}

	public static boolean usePortal(Player player, Portal portal) throws AttributeNotFoundException, NoRPZoneException {
		Log4J.startMethod(logger, "usePortal");

		if (!player.nextTo(portal)) // Too far to use the portal
		{
			return false;
		}

		if (portal.getDestinationZone() == null) // This portal is incomplete
		{
			logger.error("Portal " + portal + " has no destination.");
			return false;
		}

		StendhalRPZone destZone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(portal.getDestinationZone()));

		Portal dest = destZone.getPortal(portal.getDestinationReference());
		if (dest == null) // This portal is incomplete
		{
			logger.error("Portal " + portal + " has invalid destination");
			return false;
		}

		player.teleport(destZone, dest.getX(), dest.getY(), null, null);
		player.stop();

		dest.onUsedBackwards(player);

		Log4J.finishMethod(logger, "usePortal");
		return true;
	}


	/**
	 * Places an entity at a specified position in a specified zone. If this point is 
	 * occupied the entity is moved slightly.
	 *
	 * @param zone   zone to place the entity in
	 * @param entity the entity to place
	 * @param x      x
	 * @param y      y
	 * @return       true, if it was possible to place the entity, false otherwise
	 */
	public static boolean placeat(StendhalRPZone zone, Entity entity, int x, int y) {
		return placeat(zone, entity, x, y, null);
	}

	/**
	 * Places an entity at a specified position in a specified zone
	 *
	 * @param zone   zone to place the entity in
	 * @param entity the entity to place
	 * @param x      x
	 * @param y      y
	 * @param allowedArea only search within this area for a possible new position
	 * @return       true, if it was possible to place the entity, false otherwise
	 */
	public static boolean placeat(StendhalRPZone zone, Entity entity, int x, int y, Shape allowedArea) {
		boolean found = false;
		boolean checkPath = true;

		int nx = x;
		int ny = y;

		if (zone.collides(entity, x, y)) {

			if (zone.collides(entity, x, y, false) && (entity instanceof Player)) {
				// something nasty happend. The player should be put on a spot
				// with a real collision (not caused by objects).
				// Try to put him anywhere possible without checking the path.
				checkPath = false;
			}

			// We cannot place the entity on the orginal spot. Let's search 
			// for a new destination up to maxDestination tiles in every way.
			final int maxDestination = 20;

			outerLoop: for (int k = 1; k <= maxDestination; k++) {
				for (int i = -k; i <= k; i++) {
					for (int j = -k; j <= k; j++) {
						if ((Math.abs(i) == k) || (Math.abs(j) == k)) {
							nx = x + i;
							ny = y + j;
							if (!zone.collides(entity, nx, ny)) {
	
								// OK, we may place the entity on this spot.
	
								// Check the possibleArea now. This is a performance
								// optimization because the next step (pathfinding)
								// is very expensive. (5 seconds for a unplaceable
								// black dragon in deathmatch on 0_ados_wall_n)
								if ((allowedArea != null) && (!allowedArea.contains(nx, ny))) {
									continue;
								}
	
								// We verify that there is a walkable path between the original
								// spot and the new destination. This is to prevent players to 
								// enter not allowed places by logging in on top of other players.
								// Or monsters to spawn on the other side of a wall.
	
								List<Node> path = Path.searchPath(entity, zone, x, y, new Rectangle(nx, ny, 1, 1), maxDestination * maxDestination, false);
								if (!checkPath || !path.isEmpty()) {
	
									// We found a place!
									entity.setX(nx);
									entity.setY(ny);
	
									found = true;
									break outerLoop; // break all for-loops
								}
							}
						}
					}
				}
			}

			if (!found) {
				logger.debug("Unable to place " + entity + " at (" + x + "," + y + ")");
			}
		} else {
			entity.setX(x);
			entity.setY(y);

			found = true;
		}

		if (found) {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				if (player.hasSheep()) {
					try {
						Sheep sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
						// Call placeat for the sheep on the same spot as the 
						// player to ensure that there will be a path between the
						// player and his/her sheep.
						placeat(zone, sheep, nx, ny);
						sheep.clearPath();
						sheep.stop();
					} catch (RPObjectNotFoundException e) {
						/*
						 * No idea how but some players get a sheep but
						 * they don't have it really. Me thinks that it
						 * is a player that has been running for a while
						 * the game and was kicked of server because
						 * shutdown on a pre 1.00 version of Marauroa.
						 * We shouldn't see this anymore.
						 */
						logger.error("Pre 1.00 Marauroa sheep bug. (player = " + player.getName() + ")", e);

						if (player.has("sheep")) {
							player.remove("sheep");
						}

						if (player.hasSlot("#flock")) {
							player.removeSlot("#flock");
						}
					}
				}
			}
		}
		return found;

	}

	public static void changeZone(Player player, String destination) throws AttributeNotFoundException, NoRPZoneException {
		changeZone(player, destination, true);
	}

	private static void changeZone(Player player, String destination, boolean placePlayer) throws AttributeNotFoundException, NoRPZoneException {
		Log4J.startMethod(logger, "changeZone");

		StendhalRPWorld world = StendhalRPWorld.get();

		StendhalRPRuleProcessor.get().addGameEvent(player.getName(), "change zone", destination);

		player.clearPath();

		String source = player.getID().getZoneID();

		StendhalRPZone oldzone = (StendhalRPZone) world.getRPZone(player.getID());
		StendhalRPZone zone = null;

		oldzone.removePlayerAndFriends(player);

		if (player.hasSheep()) {
			Sheep sheep = (Sheep) world.get(player.getSheep());

			player.removeSheep(sheep);

			world.changeZone(source, destination, sheep);
			world.changeZone(source, destination, player);
			zone = (StendhalRPZone) world.getRPZone(player.getID());

			player.setSheep(sheep);

			oldzone.removePlayerAndFriends(sheep);
			zone.addPlayerAndFriends(sheep);

		} else {
			world.changeZone(source, destination, player);
			zone = (StendhalRPZone) world.getRPZone(player.getID());
		}
		zone.addPlayerAndFriends(player);


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

	/**
	 * Tell this message all players
	 *
	 * @param message Message to tell all players
	 */
	public static void shout(String message) {
		for (Player player : StendhalRPRuleProcessor.get().getPlayers()) {
			player.sendPrivateText(message);
			player.notifyWorldAboutChanges();
		}
	}
}
