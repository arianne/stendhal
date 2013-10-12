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

import games.stendhal.common.Rand;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;

public class PoisonAttacker {
    final String name = "poison";
    
	ConsumableItem poison;
	private int probability;
	
	public PoisonAttacker(final int probability, final ConsumableItem poison) {
		this.probability = probability;
		this.poison = poison;
	}

	public PoisonAttacker() {
		// standard constructor
	}

	public void applyAntistatus(double antipoison) {
		// invert the value for multiplying
		antipoison = (1 - antipoison);
		this.probability *= antipoison;
	}
	
	public boolean attemptToInflict(final RPEntity target) {
		final int roll = Rand.roll1D100();
		if (roll <= probability) {
			PoisonStatus status = new PoisonStatus(poison.getAmount(), poison.getFrecuency(), poison.getRegen());
			target.getStatusList().inflictStatus(status, poison);
			return true;
		}
	return false;
	}

	public void clearConsumables(RPEntity target) {
		target.getStatusList().removeAll(PoisonStatus.class);
	}

	public String getName() {
		return name;
	}

	public int getProbability() {
		return this.probability;
	}
	
	public void setProbability(int p) {
		this.probability = p;
	}

	/**
	 * returns the status type
	 *
	 * @return StatusType
	 */
	public StatusType getStatusType() {
		return StatusType.POISONED;
	}
}
