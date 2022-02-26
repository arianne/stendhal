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
package games.stendhal.server.entity.item.scroll;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;

/**
 * Represents a creature summon scroll.
 */
public class SummonScroll extends Scroll {

	private static final int MAX_ZONE_NPCS = 50;

	private static final Logger logger = Logger.getLogger(SummonScroll.class);

	/**
	 * Creates a new summon scroll.
	 *
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public SummonScroll(final String name, final String clazz, final String subclass,
			final Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * Copy constructor.
	 *
	 * @param item
	 *            item to copy
	 */
	public SummonScroll(final SummonScroll item) {
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
	protected boolean useScroll(final Player player) {
		final StendhalRPZone zone = player.getZone();

		if (zone.isInProtectionArea(player)) {
			player.sendPrivateText("The aura of protection in this area prevents the scroll from working!");
			return false;
		}

		if (zone.getNPCList().size() >= MAX_ZONE_NPCS) {
			player.sendPrivateText("Mysteriously, the scroll does not function! Perhaps this area is too crowded...");
			logger.info("Too many npcs to summon another creature");
			return false;
		}

		final int x = player.getInt("x");
		final int y = player.getInt("y");

		final EntityManager manager = SingletonRepository.getEntityManager();

		Creature pickedCreature = null;

		final String type = getInfoString();

		if (type != null) {
			// scroll for special monster
			pickedCreature = manager.getCreature(type);
		} else {
			// pick it randomly
			final Collection<Creature> creatures = manager.getCreatures();
			final int magiclevel = 4;
			final List<Creature> possibleCreatures = new ArrayList<Creature>();
			for (final Creature creature : creatures) {
				if (creature.getLevel() <= magiclevel && !creature.isAbnormal()) {
					possibleCreatures.add(creature);
				}
			}
			final int pickedIdx = (int) (Math.random() * possibleCreatures.size());
			pickedCreature = possibleCreatures.get(pickedIdx);
		}

		if (pickedCreature == null) {
			player.sendPrivateText("This scroll does not seem to work. You should talk to the magician who created it.");
			return false;
		}

		// create it
		final AttackableCreature creature = new AttackableCreature(pickedCreature);

		// remove cowardly profiles as the creature is supposed to fight on behalf of the player
		Map<String, String> profiles = new HashMap<String, String>(creature.getAIProfiles());
		if (profiles.containsKey("coward")) {
			profiles.remove("coward");
			creature.setAIProfiles(profiles);
		}
		if (profiles.containsKey("stupid coward")) {
			profiles.remove("stupid coward");
			creature.setAIProfiles(profiles);
		}

		StendhalRPAction.placeat(zone, creature, x, y);

		creature.init();
		creature.setMaster(player.getTitle());
		creature.clearDropItemList();
		creature.put("title_type", "friend");

		return true;
	}

	@Override
	public String describe() {
		String text = super.describe();

		final String infostring = getInfoString();

		if (infostring != null) {
			text += " It will summon a " + infostring  + ".";
		}
		return (text);
	}
}
