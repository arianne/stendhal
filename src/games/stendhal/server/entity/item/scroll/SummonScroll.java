/* $Id$
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
package games.stendhal.server.entity.item.scroll;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Represents a creature summon scroll.
 */
public class SummonScroll extends InfoStringScroll {

	private static final int MAX_ZONE_NPCS = 50;

	private static final Logger logger = Logger.getLogger(SummonScroll.class);

	/**
	 * Creates a new summon scroll
	 * 
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public SummonScroll(String name, String clazz, String subclass,
			Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * copy constructor
	 * 
	 * @param item
	 *            item to copy
	 */
	public SummonScroll(SummonScroll item) {
		super(item);
	}

	/**
	 * Is invoked when a summon scroll is used.
	 * 
	 * @param player
	 *            The player who used the scroll
	 * @return true iff summoning was successful
	 */
	@Override
	protected boolean useScroll(Player player) {
		StendhalRPZone zone = player.getZone();

		if (zone.isInProtectionArea(player)) {
			player.sendPrivateText("The aura of protection in this area prevents the scroll from working!");
			return false;
		}

		if (zone.getNPCList().size() >= MAX_ZONE_NPCS) {
			player.sendPrivateText("Mysteriously, the scroll does not function! Perhaps this area is too crowded...");
			logger.error("too many npcs");
			return false;
		}

		int x = player.getInt("x");
		int y = player.getInt("y");

		EntityManager manager = StendhalRPWorld.get().getRuleManager().getEntityManager();

		Creature pickedCreature = null;

		String type = getInfoString();

		if (type != null) {
			// scroll for special monster
			pickedCreature = manager.getCreature(type);
		} else {
			// pick it randomly
			Collection<Creature> creatures = manager.getCreatures();
			int magiclevel = 4;
			List<Creature> possibleCreatures = new ArrayList<Creature>();
			for (Creature creature : creatures) {
				if (creature.getLevel() <= magiclevel) {
					possibleCreatures.add(creature);
				}
			}
			int pickedIdx = (int) (Math.random() * possibleCreatures.size());
			pickedCreature = possibleCreatures.get(pickedIdx);
		}

		if (pickedCreature == null) {
			player.sendPrivateText("This scroll does not seem to work. You should talk to the magician who created it.");
			return false;
		}

		// create it
		AttackableCreature creature = new AttackableCreature(pickedCreature);

		StendhalRPAction.placeat(zone, creature, x, y);

		creature.init();
		creature.setMaster(player);
		creature.clearDropItemList();
		creature.put("title_type", "friend");

		return true;
	}
}
