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
package games.stendhal.server.core.rp;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.DataProvider;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.db.StendhalKillLogDAO;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.events.ZoneNotifier;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.core.rp.group.Group;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.DomesticAnimal;
import games.stendhal.server.entity.creature.Pet;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.AttackEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.net.message.TransferContent;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.rp.RPServerManager;

import org.apache.log4j.Logger;

/**
 * fighting and player teleport support
 */
public class StendhalRPAction {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalRPAction.class);

	/**
	 * The amount to weight ATK and DEF in player strength calculation. Higher
	 * means more weight to stats vs level. Value 0.73 has been obtained from
	 * a least square fit of players equally strong to an actual player killer.
	 */
	private static final double STRENGTH_STATS_MULTIPLIER = 0.73;
	/**
	 * Maximum strength ratio where it is still acceptable to attack another
	 * player otherwise than in a self defense situation.
	 */
	private static final double ACCEPTABLE_STRENGTH_RATIO = 0.75;

	/** server manager. */
	private static RPServerManager rpman;

	/**
	 * initializes the StendhalRPAction
	 *
	 * @param rpMan RPServerManager
	 */
	public static void initialize(final RPServerManager rpMan) {
		StendhalRPAction.rpman = rpMan;
	}


	/**
	 * Do logic for starting an attack on an entity.
	 *
	 * @param player
	 *            The player wanting to attack.
	 * @param victim
	 *            The target of attack.
	 */
	public static void startAttack(final Player player, final RPEntity victim) {
		// Player's can't attack themselves
		if (player.equals(victim)) {
			return;
		}

		// Disable attacking NPCS that are created as not attackable.
		if (!victim.isAttackable()) {
			if ((victim instanceof SpeakerNPC)) {
				((SpeakerNPC) victim).say(player.getName() + ", if you want my attention, just say #hi.");
			}
			logger.info("REJECTED. " + player.getName() + " is attacking " + victim.getName());
			return;
		}

		// Enabled PVP
		if ((victim instanceof Player) || (victim instanceof DomesticAnimal)) {
			final StendhalRPZone zone = player.getZone();

			// Make sure that you can't attack players or sheep (even wild
			// sheep) who are inside a protection area. Also prevent attacking
			// from such an area, in name of fairness
			if (zone.isInProtectionArea(victim) || (zone.isInProtectionArea(player))) {
				logger.info("REJECTED. " + victim.getName()
						+ " is protected by zone");

				final String name = getNiceVictimName(victim);

				player.sendPrivateText("The powerful protective aura in this place prevents you from attacking "
						+ name + ".");
				return;
			}

			if (victim instanceof Player) {
				// disable attacking much weaker players, except in/ self defense
				if (!mayAttackPlayer(player, (Player) victim)) {
					player.sendPrivateText("Your conscience would trouble you if you carried out this attack.");
					return;
				}
			} else {
				// Only allow owners, if there is one, to attack the pet
				final Player owner = ((DomesticAnimal) victim).getOwner();
				if ((owner != null) && (owner != player)) {
					player.sendPrivateText("You pity " + getNiceVictimName(victim) + " too much to kill it.");

					return;
				}
			}

			logger.info(player.getName() + " is attacking " + victim.getName());
		}

		StendhalKillLogDAO killLog = DAORegister.get().get(StendhalKillLogDAO.class);
		new GameEvent(player.getName(), "attack", victim.getName(), killLog.entityToType(player), killLog.entityToType(victim)).raise();

		player.setTarget(victim);
		player.faceToward(victim);
		player.applyClientDirection(false);
		player.notifyWorldAboutChanges();
	}

	/**
	 * checks whether a player may attack another player
	 *
	 * @param attacker attacker
	 * @param victim   victim
	 * @return true, if the attack is acceptable
	 */
	private static boolean mayAttackPlayer(final Player attacker, final Player victim) {

		// is the victim is of similar strength
		if (victimIsStrongEnough(attacker, victim)) {
			return true;
		}

		// allow self defence
		RPEntity victimsTarget = victim.getAttackTarget();
		if ((victimsTarget == null) || !(victimsTarget instanceof Player)) {
			return false;
		}
		if (victimsTarget == attacker) {
			return true;
		}

		// allow defence of group members
		Group group = SingletonRepository.getGroupManager().getGroup(victimsTarget.getName());
		if (group == null) {
			return false;
		}

		return group.hasMember(attacker.getName());
	}

	/**
	 * Check that the victim has high enough level compared to the attacker.
	 *
	 * @param player The player trying to attack
	 * @param victim The entity being attacked
	 * @return <code>true</code> if the victim is strong enough to allow
	 *  the attack to happen, <code>false</code> otherwise.
	 */
	private static boolean victimIsStrongEnough(final Player player, final Player victim) {
		return getPlayerStrength(victim) >= ACCEPTABLE_STRENGTH_RATIO * getPlayerStrength(player);
	}

	/**
	 * Get the relative strength of a player, ignoring equipment.
	 *
	 * @param player
	 * @return player strength
	 */
	private static double getPlayerStrength(final Player player) {
		int combatSum;
		
		/* FIXME: Ranged stat is disabled by default until fully implemented.
		 * Remove System.getProperty().
		 */
		if (System.getProperty("stat.ranged") != null) {
			combatSum = player.getAtk() + player.getDef() + player.getRng();
		} else {
			combatSum = player.getAtk() + player.getDef();
		}
		
		return STRENGTH_STATS_MULTIPLIER * combatSum + player.getLevel();
	}

	/**
	 * Get a nice target description string to be sent to the attacker in case
	 * the attacking action is forbidden.
	 *
	 * @param victim The attacked entity
	 * @return Description of the attacked pet or player
	 */
	private static String getNiceVictimName(final RPEntity victim) {
		String name = victim.getTitle();

		if (victim instanceof DomesticAnimal) {
			final Player owner = ((DomesticAnimal) victim).getOwner();

			if (owner != null) {
				name = Grammar.suffix_s(owner.getTitle()) + " " + name;
			} else {
				if (victim instanceof Sheep) {
					name = "that " + name;
				} else {
					name = "that poor little " + name;
				}
			}
		}

		return name;
	}

	/**
	 * Lets the attacker try to attack the defender.
	 * @param player
	 *
	 * @param defender
	 *            The defending RPEntity.
	 * @return true iff the attacker has done damage to the defender.
	 *
	 */
	public static boolean playerAttack(final Player player, final RPEntity defender) {
		boolean result = false;

		final StendhalRPZone zone = player.getZone();
		if (!zone.has(defender.getID()) || (defender.getHP() == 0)) {
			logger.debug("Attack from " + player + " to " + defender
					+ " stopped because target was lost("
					+ zone.has(defender.getID()) + ") or dead.");
			player.stopAttack();

			return false;
		}

		defender.rememberAttacker(player);
		if (defender instanceof Player) {
			player.storeLastPVPActionTime();

			// did the player or victim move into a protected area?
			if(zone.isInProtectionArea(defender) || zone.isInProtectionArea(player)) {
				logger.debug("Attack from " + player + " to " + defender
						+ " stopped because " + player + " or " + defender + " moved into protected area.");
				player.stopAttack();
				return false;
			}
		}

		boolean missileUsed = false;
		if (!player.nextTo(defender)) {
			// The attacker is not directly standing next to the defender.
			// Find out if he can attack from the distance.
			if (player.canDoRangeAttack(defender, player.getMaxRangeForArcher())) {

				// Check line of view to see if there is any obstacle.
				if (!player.hasLineOfSight(defender)) {
					return false;
				}

				missileUsed = true;
			} else {
				logger.debug("Attack from " + player + " to " + defender
						+ " failed because target is not near.");
				return false;
			}
		}
		
		// Weapon for the purpose of attack image
		Item attackWeapon = player.getWeapon();
		String weaponClass = null;
		if (attackWeapon != null) {
			weaponClass = attackWeapon.getWeaponType();
		}
		
		// Throw dices to determine if the attacker has missed the defender
		final boolean beaten = player.canHit(defender);

		// Player gets ATK XP from attack
		boolean getsAtkXP = false;
		// FIXME: replace conditional if alternate method added to game
		if (System.getProperty("atkxp.alt") != null) {
			// Alternate method allows player to recieve atk_xp on successfull
			// hit independent of whether has received damage.
			if (beaten) {
				getsAtkXP = true;
			}
		} else {
			getsAtkXP = player.getsFightXpFrom(defender);
		}

		// disabled attack xp for attacking NPC's
		if (!(defender instanceof SpeakerNPC)
				&& getsAtkXP) {
			/* FIXME: Ranged stat is disabled by default until fully implemented.
			 * Remove System.getProperty().
			 */
			if (!(player.nextTo(defender)) && (System.getProperty("stat.ranged") != null)) {
				player.incRngXP();
			} else {
				player.incAtkXP();
			}
		}

		if (beaten) {
			if ((defender instanceof Player)
					&& defender.getsFightXpFrom(player)) {
				defender.incDefXP();
			}

			final List<Item> weapons = player.getWeapons();

			int damage = player.damageDone(defender, player.getItemAtk(), player.getDamageType());
			if (damage > 0) {

				// limit damage to target HP
				damage = Math.min(damage, defender.getHP());
				player.handleLifesteal(player, weapons, damage);

				defender.onDamaged(player, damage);
				logger.debug("attack from " + player.getID() + " to "
						+ defender.getID() + ": Damage: " + damage);

				result = true;
			} else {
				// The attack was too weak, it was blocked
				logger.debug("attack from " + player.getID() + " to "
						+ defender.getID() + ": Damage: " + 0);
			}
			//deteriorate weapons of attacker
			for (Item weapon : weapons) {
				weapon.deteriorate();
			}
			//randomly choose one defensive item to deteriorate
			List<Item> defenseItems = defender.getDefenseItems();
			if(!defenseItems.isEmpty()) {
				Rand.rand(defenseItems).deteriorate();
			}
			
			player.addEvent(new AttackEvent(true, damage, player.getDamageType(), weaponClass, missileUsed));
			player.notifyWorldAboutChanges();
		} else {
			// Missed
			logger.debug("attack from " + player.getID() + " to "
					+ defender.getID() + ": Missed");
			player.addEvent(new AttackEvent(false, 0, player.getDamageType(), weaponClass, missileUsed));
			player.notifyWorldAboutChanges();
		}

		if (missileUsed) {
			// Removing the missile is deferred here so that the weapon
			// information is available when calculating the damage.
			useMissile(player);
		}

		player.notifyWorldAboutChanges();

		return result;
	}

	/**
	 * Remove an used up missile from an attacking player.
	 *
	 * @param player The player to remove the projectile from
	 */
	private static void useMissile(Player player) {
		// Get the projectile that will be thrown/shot.
		StackableItem projectilesItem = null;
		if (player.getRangeWeapon() != null) {
			projectilesItem = player.getAmmunition();
		}
		if (projectilesItem == null) {
			// no arrows... but maybe a spear?
			projectilesItem = player.getMissileIfNotHoldingOtherWeapon();
		}
		// Creatures can attack without having projectiles, but players
		// will lose a projectile for each shot.
		if (projectilesItem != null) {
			projectilesItem.removeOne();
		}
	}

	/**
	 * send the content of the zone the player is in to the client.
	 *
	 * @param player player
	 */
	public static void transferContent(final Player player) {
		final StendhalRPZone zone = player.getZone();
		transferContent(player, zone.getContents());
	}


	private static DataProvider dataProvider = new DataProvider();
	/**
	 * transfers arbritary content
	 *
	 * @param player   player
	 * @param contents content
	 */
	public static void transferContent(Player player, List<TransferContent> contents) {
		if (rpman != null) {
			List<TransferContent> allContent = new LinkedList<TransferContent>(contents);
			List<TransferContent> temp = dataProvider.getData(player.getClientVersion());
			if (temp != null) {
				allContent.addAll(temp);
			}
			rpman.transferContent(player, allContent);
		} else {
			logger.warn("rpmanager not found");
		}
	}

	/**
	 * Change an entity's zone based on it's global world coordinates.
	 *
	 * @param entity
	 *            The entity changing zones.
	 * @param x
	 *            The entity's old zone X coordinate.
	 * @param y
	 *            The entity's old zone Y coordinate.
	 */
	public static void decideChangeZone(final Entity entity, final int x, final int y) {
		final StendhalRPZone origin = entity.getZone();

		final int entity_x = x + origin.getX();
		final int entity_y = y + origin.getY();

		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZoneAt(
				origin.getLevel(), entity_x, entity_y, entity);

		if (zone != null) {
			final int nx = entity_x - zone.getX();
			final int ny = entity_y - zone.getY();

			if (logger.isDebugEnabled()) {
				logger.debug("Placing " + entity.getTitle() + " at "
						+ zone.getName() + "[" + nx + "," + ny + "]");
			}

			if (!placeat(zone, entity, nx, ny)) {
				logger.warn("Could not place " + entity.getTitle() + " at "
						+ zone.getName() + "[" + nx + "," + ny + "]");
			}
		} else {
			logger.warn("Unable to choose a new zone for entity: "
					+ entity.getTitle() + " at (" + entity_x + "," + entity_y
					+ ") source was " + origin.getName() + " at (" + x + ", "
					+ y + ")");
		}
	}

	/**
	 * Places an entity at a specified position in a specified zone. If this
	 * point is occupied the entity is moved slightly. This will remove the
	 * entity from any existing zone and add it to the target zone if needed.
	 *
	 * @param zone
	 *            zone to place the entity in
	 * @param entity
	 *            the entity to place
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @return true, if it was possible to place the entity, false otherwise
	 */
	public static boolean placeat(final StendhalRPZone zone, final Entity entity, final int x,
			final int y) {
		return placeat(zone, entity, x, y, null);
	}


	/**
	 * maximum walking distance from the center, determines the area checked.
	 * the total area checked is 2n(n+1) + 1
	 * 36 => 2665 squares
	 */
	private static final int maxDisplacement = 36;

	/**
	 * Places an entity at a specified position in a specified zone. This will
	 * remove the entity from any existing zone and add it to the target zone if
	 * needed.
	 *
	 * @param zone
	 *            zone to place the entity in
	 * @param entity
	 *            the entity to place
	 * @param x
	 *            x
	 * @param y
	 *            y
	 * @param allowedArea
	 *            only search within this area for a possible new position
	 * @return true, if it was possible to place the entity, false otherwise
	 */
	public static boolean placeat(final StendhalRPZone zone, final Entity entity, int x,
			int y, final Shape allowedArea) {
		if (zone == null) {
			return false;
		}

		// check in case of players that that they are still in game
		// because the entity is added to the world again otherwise.
		if (entity instanceof Player) {
			final Player player = (Player) entity;
			if (player.isDisconnected()) {
				return true;
			}
		}

		if (zone.collides(entity, x, y)) {
			boolean checkPath = true;
			if (zone.collides(entity, x, y, false) && (entity instanceof Player)) {
				// Trying to place a player on a spot with a real collision
				// (not caused by objects). Can happen with teleport.
				// Try to put him anywhere possible without checking the path.
				checkPath = false;
			}

			final Point newLocation = findLocation(zone, entity, allowedArea, x, y, checkPath);

			if (newLocation == null) {
				logger.info("Unable to place " + entity.getTitle() + " at "
						+ zone.getName() + "[" + x + "," + y + "]");
				return false;
			}

			x = newLocation.x;
			y = newLocation.y;
		}

		final StendhalRPZone oldZone = entity.getZone();
		final boolean zoneChanged = (oldZone != zone);

		if (entity instanceof RPEntity) {
			final RPEntity rpentity = (RPEntity) entity;

			rpentity.stop();
			rpentity.stopAttack();
			rpentity.clearPath();
		}

		Sheep sheep = null;
		Pet pet = null;

		// Remove from old zone (if any) during zone change
		if (oldZone != null) {
			// Player specific pre-remove handling
			if (entity instanceof Player) {
				final Player player = (Player) entity;

				// Remove and remember dependents
				sheep = player.getSheep();

				if (sheep != null) {
					sheep.clearPath();
					sheep.stop();

					player.removeSheep(sheep);
				}

				pet = player.getPet();

				if (pet != null) {
					pet.clearPath();
					pet.stop();

					player.removePet(pet);
				}
			}

			if (zoneChanged) {
				oldZone.remove(entity);
			}
		}

		// [Re]position (possibly while between zones)
		entity.setPosition(x, y);

		// Place in new zone (if needed)
		if (zoneChanged) {
			zone.add(entity);
		}

		// Player specific post-change handling
		if (entity instanceof Player) {
			final Player player = (Player) entity;

			//  Move and re-add removed dependents
			if (sheep != null) {
				if (placePet(zone, player, sheep)) {
					player.setSheep(sheep);
					sheep.setOwner(player);
				} else {
					// Didn't fit?
					player.sendPrivateText("You seemed to have lost your sheep while trying to squeeze in.");
				}
			}

			if (pet != null) {
				if (placePet(zone, player, pet)) {
					player.setPet(pet);
					pet.setOwner(player);
				} else {
					// Didn't fit?
					player.sendPrivateText("You seemed to have lost your pet while trying to squeeze in.");
				}
			}

			if (zoneChanged) {
				// Zone change notifications/updates
				transferContent(player);

				if (oldZone != null) {
					final String source = oldZone.getName();
					final String destination = zone.getName();

					new GameEvent(player.getName(), "change zone", destination).raise();

					TutorialNotifier.zoneChange(player, source, destination);
					ZoneNotifier.zoneChange(player, source, destination);
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Placed " + entity.getTitle() + " at "
					+ zone.getName() + "[" + x + "," + y + "]");
		}

		return true;
	}

	/**
	 * Finds a new place for entity.
	 * @param zone zone to place the entity in
	 * @param entity the entity to place
	 * @param allowedArea only search within this area for a possible new position,
	 * 	or null if the whole normal search area should be used
	 * @param x the x coordinate of the search center
	 * @param y the y coordinate of the search center
	 * @param checkPath if true, check that there's a valid path to the center
	 *
	 * @return location of the new placement, or null if no suitable place was found
	 */
	private static Point findLocation(final StendhalRPZone zone, final Entity entity,
			final Shape allowedArea, final int x, final int y, final boolean checkPath) {

		// Minimum Euclidean distance within minimum walking distance
		for (int totalShift = 1; totalShift <= maxDisplacement; totalShift++) {
			for (int tilt = (totalShift + 1) / 2; tilt > 0; tilt--) {
				final int spread = totalShift - tilt;

				int tmpx = x - tilt;
				int tmpy = y - spread;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
				tmpx = x + tilt;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
				tmpy = y + spread;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
				tmpx = x - tilt;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}

				// center spots of the equidistance rectangle.
				if (spread == tilt) {
					continue;
				}

				tmpx = x - spread;
				tmpy = y - tilt;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
				tmpx = x + spread;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
				tmpy = y + tilt;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
				tmpx = x - spread;
				if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
					return new Point(tmpx, tmpy);
				}
			}

			// Do tilt = 0 case here, since it takes only 4 checks
			int tmpx = x;
			int tmpy = y - totalShift;
			if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
				return new Point(tmpx, tmpy);
			}
			tmpy = y + totalShift;
			if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
				return new Point(tmpx, tmpy);
			}
			tmpy = y;
			tmpx = x - totalShift;
			if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
				return new Point(tmpx, tmpy);
			}
			tmpx = x + totalShift;
			if (isValidPlacement(zone, entity, allowedArea, x, y, tmpx, tmpy, checkPath)) {
				return new Point(tmpx, tmpy);
			}
		}

		return null;
	}

	/**
	 * Checks if a new placement for an entity is valid.
	 *
	 * @param zone the zone where the entity should be placed
	 * @param entity the entity to place
	 * @param allowedArea if specified, restrict placement within this area
	 * @param oldX the x coordinate from where the entity was displaced
	 * @param oldY the y coordinate from where the entity was displaced
	 * @param newX the x coordinate of the new placement
	 * @param newY the y coordinate of the new placement
	 * @param checkPath if true, check that there is a path from <code>(newX, newY)</code>
	 * to <code>(oldX, oldY)</code>
	 *
	 * @return true if placing is possible, false otherwise
	 */
	private static boolean isValidPlacement(final StendhalRPZone zone, final Entity entity,
			final Shape allowedArea, final int oldX, final int oldY,
			final int newX, final int newY, final boolean checkPath) {
		if (!zone.collides(entity, newX, newY)) {
			// Check the possibleArea now. This is a performance
			// optimization because the pathfinding is very expensive.
			if ((allowedArea != null) && (!allowedArea.contains(newX, newY))) {
				return false;
			}
			if (!checkPath) {
				return true;
			}

			// We verify that there is a walkable path between the original
			// spot and the new destination. This is to prevent players to
			// enter not allowed places by logging in on top of other players.
			// Or monsters to spawn on the other side of a wall.
			final List<Node> path = Path.searchPath(entity, zone,
					oldX, oldY, new Rectangle(newX, newY, 1, 1),
					400 /* maxDestination * maxDestination */, false);
			if (!path.isEmpty()) {
				// We found a place!
				return true;
			}
		}
		return false;
	}

	/**
	 * Place a pet near player in such a way that it likely does not block the
	 * player at normal zone switch. The pet will be placed so that it has a
	 * path to the player.
	 *
	 * @param zone
	 * @param player
	 * @param pet
	 * @return <code>true</code> if the pet could be placed properly, false
	 * 	otherwise
	 */
	private static boolean placePet(final StendhalRPZone zone, final Player player,
			final Entity pet) {
		// Shift the pet a bit, so that it does not usually end up exactly in
		// front of the player.
		if (placeat(zone, pet, player.getX() + 1, player.getY() + 1)) {
			if (!Path.searchPath(pet, player, 20).isEmpty()) {
				return true;
			}
		}
		// Failed to find a path from the new location. Just try to find
		// some location with a path to the player
		Point p = findLocation(zone, pet, null, player.getX(), player.getY(), true);
		if (p != null) {
			return placeat(zone, pet, p.x, p.y);
		}

		return false;
	}
}
