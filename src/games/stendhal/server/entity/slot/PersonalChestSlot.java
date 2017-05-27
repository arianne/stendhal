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
package games.stendhal.server.entity.slot;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.chest.PersonalChest;

/**
 * a slot of a personal chest.
 *
 * @author hendrik
 */
public class PersonalChestSlot extends ChestSlot {
	private final PersonalChest chest;

	/**
	 * create a new PersonalChestSlot
	 *
	 * @param owner personal chest owning this slot
	 */
	public PersonalChestSlot(final PersonalChest owner) {
		super(owner);
		this.chest = owner;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(final Entity entity) {

		// first delegate to super method to check that the player
		// is next to the chest
		if (!super.isReachableForTakingThingsOutOfBy(entity)) {
			return false;
		}

		// Yes, this comparison of references is by design: Two player objects
		// are equal if they are for the same character but could be from two
		// different session. Marauroa is supposed to prevent two session
		// for the same character being active at the same time, but we should
		// not depend on this as the banks have had lots of bugs in the past.
		if (chest.getAttending() != entity) {
			if (chest.getAttending() != null) {
				setErrorMessage("You cannot take items out of " + Grammar.suffix_s(chest.getAttending().getName()) + " bank chest.");
			}
			return false;
		}
		return true;
	}

}
