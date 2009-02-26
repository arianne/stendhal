/*
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
package games.stendhal.server.entity.item;

import marauroa.common.game.RPObject;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Represents a creature summon staff.
 */
public class NecroStaff extends Item implements UseListener {

	/** the max number of entities we want in a zone, for performance reasons */
	private static final int MAX_ZONE_NPCS = 50;
	/** max level of creature the staff can summon */
	private static final int MAX_LEVEL = 26;
	/** How near the player must stand to the corpse */
	private static final int SQUARED_RANGE = 16;
	/** HP_FACTOR*creature level HP is lost when a creature is summoned */
	private static final int HP_FACTOR = 1;
	
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
	 * Pick a random undead creature below a level threshold
	 * 
	 */
	private AttackableCreature pickSuitableCreature() {
		final EntityManager manager = SingletonRepository.getEntityManager();

		Creature pickedCreature = null;

		final Collection<Creature> creatures = manager.getCreatures();
			final List<Creature> possibleCreatures = new ArrayList<Creature>();
			for (final Creature creature : creatures) {
				if (creature.getLevel() <= MAX_LEVEL && creature.get("class").equals("undead") 
						&& !creature.isRare()) {
					
					possibleCreatures.add(creature);
				}
			}
			
		final int pickedIdx = (int) (Math.random() * possibleCreatures.size());
		pickedCreature = possibleCreatures.get(pickedIdx);
			
		//		 create it
		final AttackableCreature creature = new AttackableCreature(pickedCreature);
		
		return creature;
	}
	
	/**
	 * Is invoked when the necromancer staff is used.
	 * 
	 * @param player
	 *            The player who used the staff
	 * @return true iff summoning was successful
	 */
	
	//@Override
	public boolean onUsed(final RPEntity user) {
		final StendhalRPZone zone = user.getZone();

		if (zone.isInProtectionArea(user)) {
			user.sendPrivateText("The aura of protection in this area lets the dead sleep in peace.");
			return false;
		}

		if (zone.getNPCList().size() >= MAX_ZONE_NPCS) {
			user.sendPrivateText("Mysteriously, the staff does not function! Perhaps this area is too crowded...");
			logger.warn(user.getName() + " is trying to use the necromancer staff but there are too many npcs in " + zone.getName() );
			return false;
		}

		//Pick a corpse within the staff's range.
		for (RPObject inspected : zone) {
			if (inspected instanceof Corpse 
					&& user.squaredDistance(Integer.parseInt(inspected.get("x")), Integer.parseInt(inspected.get("y"))) <= SQUARED_RANGE) {
				
				AttackableCreature creature = pickSuitableCreature();
				
				if (creature == null) {
					user.sendPrivateText("This staff does not seem to work. Maybe it has lost its unholy power.");
					return false;
				}
				
				StendhalRPAction.placeat(zone, creature, Integer.parseInt(inspected.get("x")), Integer.parseInt(inspected.get("y")));
				zone.remove(inspected);
				creature.init();
				creature.setMaster(user);
				creature.clearDropItemList();
				creature.put("title_type", "friend");
				
				//Suck some of the summoners HP depending on the summoned creature's level.
				user.setHP(user.getHP()-HP_FACTOR*creature.getLevel());
				return true;
			}

		}
		
		// No corpses in range.
		user.sendPrivateText("Step closer to corpses to awake them.");
		return false;
		
	}
}

