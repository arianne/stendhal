/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * Calculates item rarity metric for every dropped item. The rarity is
 * <code>1/p</code>, where <code>p</code> is the probability of a creature
 * that would drop (taking in account the creature drop probability)
 * the item spawning anywhere in an empty world.
 */
public class ItemRarity extends ScriptImpl {
	/**
	 * A map of probabilities. Probability for at least one item being dropped
	 */
	private final Map<String, Double> rarity = new HashMap<String, Double>();

	@Override
	public void execute(final Player admin, final List<String> args) {
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		for (final IRPZone irpZone : world) {
			final StendhalRPZone zone = (StendhalRPZone) irpZone;
			for (CreatureRespawnPoint point : zone.getRespawnPointList()) {
				CountCreature creature = new CountCreature(point.getPrototypeCreature());
				processCreature(creature);
			}
		}

		sendResults(admin);
	}

	/**
	 * Process the drop probabilities of a creature.
	 *
	 * @param creature the creature to be processed
	 */
	private void processCreature(CountCreature creature) {
		for (DropItem item : creature.getDropList()) {
			/*
			 * Probability for a creature with that item spawning at a given
			 * turn
			 */
			double probability = item.probability / 100 / creature.getRespawnTime();
			addToProbability(item.name, probability);
		}
	}

	/**
	 * Add drop probability to items old probability.
	 *
	 * @param item the item to add to
	 * @param probability additional probability
	 */
	private void addToProbability(String item, double probability) {
		double old = 0.0;
		Double oldProbability = rarity.get(item);
		if (oldProbability != null) {
			old = oldProbability;
		}

		// Calculate through complements to add them up properly
		double newProb = 1 - (1 - old) * (1 - probability);
		rarity.put(item, newProb);
	}

	/**
	 * Send the admin a nicely formatted version of the results.
	 *
	 * @param admin recipient
	 */
	private void sendResults(Player admin) {
		admin.sendPrivateText("Item\tRarity");
		List<Entry<String, Double>> items = new ArrayList<Entry<String, Double>>(rarity.entrySet());

		java.util.Collections.sort(items, new EntryComparator());
		StringBuilder msg = new StringBuilder();
		for (Entry<String, Double> entry : items) {
			msg.append(entry.getKey());
			msg.append('\t');
			msg.append(1 / entry.getValue());
			msg.append('\n');
		}
		admin.sendPrivateText(msg.toString());
	}

	/**
	 * Comparator for sorting the item list.
	 */
	private static class EntryComparator implements Comparator<Entry<String, Double>> {
		@Override
		public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
			return o1.getValue().compareTo(o2.getValue());
		}
	}

	/**
	 * A creature for counting item probabilities. Needed for gaining
	 * access to the protected fields of <code>Creature</code>
	 */
	private static class CountCreature extends Creature {
		public CountCreature(Creature copy) {
			super(copy);
		}

		/**
		 * Get list of droppable items
		 *
		 * @return list of droppable items
		 */
		public List<DropItem> getDropList() {
			return dropsItems;
		}
	}
}
