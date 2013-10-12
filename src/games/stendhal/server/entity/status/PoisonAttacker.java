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
package games.stendhal.server.entity.status;

import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;

public class PoisonAttacker {

	private ConsumableItem poison;
	private int probability;
	
	public PoisonAttacker(final int probability, final ConsumableItem poison) {
		this.probability = probability;
		this.poison = poison;
	}
	
	public boolean attemptToInflict(final RPEntity target, final RPEntity attacker) {

		/*
		 * Antipoison attributes
		 */
		double sumAll = 0.0;
		List<Item> defenderEquipment = target.getDefenseItems();
		if (target.hasRing()) {
			defenderEquipment.add(target.getRing());
		}

		for (final Item equipmentItem : defenderEquipment) {
			if (equipmentItem.has("antipoison")) {
				sumAll += equipmentItem.getDouble("antipoison");
			}
		}

		// Prevent antipoison attribute from surpassing 100%
		if (sumAll > 1) {
			sumAll = 1;
		}

		double myProbability = this.probability;
		if (sumAll > 0) {
			// invert the value for multiplying
			double myAntipoison = (1 - sumAll);
			myProbability *= myAntipoison;
		}

		final int roll = Rand.roll1D100();
		if (roll <= myProbability) {
			PoisonStatus status = new PoisonStatus(poison.getAmount(), poison.getFrecuency(), poison.getRegen());
			target.getStatusList().inflictStatus(status, poison);
			new GameEvent(attacker.getName(), "poison", target.getName()).raise();
			target.sendPrivateText("You have been poisoned by " + Grammar.a_noun(attacker.getName()) + ".");
			return true;
		}
		return false;
	}
}
