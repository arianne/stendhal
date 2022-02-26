/*
 ***************************************************************************
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
package games.stendhal.server.entity.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import marauroa.common.game.RPObject;

/**
 * Represents a creature summon staff.
 */
public class NecroStaff extends Item {

	/** the max number of entities we want in a zone, for performance reasons. */
	private static final int MAX_ZONE_NPCS = 50;
	/** max level spawned by staff is player level * this factor.*/
	private static final double LEVEL_FACTOR = 0.25;
	/** How near the player must stand to the corpse. */
	private static final int SQUARED_RANGE = 16;
	/** HP_FACTOR*creature level HP is lost when a creature is summoned. */
	private static final int HP_FACTOR = 3;

	private static final Logger logger = Logger.getLogger(NecroStaff.class);

	/**
	 * Creates a new necromancer staff.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public NecroStaff(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public NecroStaff(final NecroStaff item) {
		super(item);
	}

	/**
	 * Picks a random undead creature below a level threshold.
	 *
	 * @param playerlevel
	 *            The level of the player who used the staff
	 * @return creature the chosen undead creature
	 *
	 */
	private AttackableCreature pickSuitableCreature(final int playerlevel) {
		final EntityManager manager = SingletonRepository.getEntityManager();

		final Collection<Creature> creatures = manager.getCreatures();
		final List<Creature> possibleCreatures = new ArrayList<Creature>();
		for (final Creature creature : creatures) {
			if (creature.getLevel() <= LEVEL_FACTOR * playerlevel && creature.get("class").equals("undead")
					&& !creature.isAbnormal()) {

				possibleCreatures.add(creature);
			}
		}

		//		 create it
		if (!possibleCreatures.isEmpty()) {
			return new AttackableCreature(Rand.rand(possibleCreatures));
		}
		return null;
	}

	/**
	 * Is invoked when the necromancer staff is used.
	 *
	 * @param user
	 *            The player who used the staff
	 * @return true iff summoning was successful
	 */
	@Override
	public boolean onUsed(final RPEntity user) {
		if (!this.isContained()) {
			user.sendPrivateText("The staff must be wielded.");
			return false;
		}
		final StendhalRPZone zone = user.getZone();

		if (zone.isInProtectionArea(user)) {
			user.sendPrivateText("The aura of protection in this area lets the dead sleep in peace.");
			return false;
		}

		if (zone.getNPCList().size() >= MAX_ZONE_NPCS) {
			user.sendPrivateText("Mysteriously, the staff does not function! Perhaps this area is too crowded...");
			logger.warn(user.getName() + " is trying to use the necromancer staff but there are too many npcs in " + zone.getName());
			return false;
		}

		//Pick a corpse within the staff's range.
		for (final RPObject inspected : zone) {
			if (inspected instanceof Corpse
					&& user.squaredDistance(Integer.parseInt(inspected.get("x")), Integer.parseInt(inspected.get("y"))) <= SQUARED_RANGE) {

				final AttackableCreature creature = pickSuitableCreature(user.getLevel());

				if (creature == null) {
					user.sendPrivateText("This staff does not seem to work. Maybe it has lost its unholy power.");
					return false;
				}

				StendhalRPAction.placeat(zone, creature, Integer.parseInt(inspected.get("x")), Integer.parseInt(inspected.get("y")));
				zone.remove(inspected);
				creature.init();
				creature.setMaster(user.getTitle());
				creature.clearDropItemList();
				creature.put("title_type", "friend");

				//Suck some of the summoners HP depending on the summoned creature's level.
				user.damage(HP_FACTOR * creature.getLevel(), this);
				user.notifyWorldAboutChanges();
				return true;
			}

		}

		// No corpses in range.
		user.sendPrivateText("Step closer to corpses to awake them.");
		return false;

	}
}
