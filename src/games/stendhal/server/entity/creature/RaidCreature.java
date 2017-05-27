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
package games.stendhal.server.entity.creature;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.entity.item.RaidCreatureCorpse;

/**
 * A Raid creature is a creature that doesn't make players killed by it to lose
 * any XP, ATK or DEF.
 *
 * @author miguel
 *
 */
public class RaidCreature extends Creature {
	/**
	 * RaidCreature.
	 *
	 * @param copy
	 *            creature to wrap
	 */
	public RaidCreature(final Creature copy) {
		super(copy);
		removeAttackWeakestProfile();
	}

	/**
	 * Pity newbies taking part in raids.
	 */
	private void removeAttackWeakestProfile() {
		if (getAIProfiles().containsKey("attack weakest")) {
			Map<String, String> profiles = new HashMap<String, String>(getAIProfiles());
			profiles.remove("attack weakest");
			setAIProfiles(profiles);
		} else if (getAIProfiles().containsKey("strategy")) {
			// Replace the attack weakest subprofile with the default profile
			// (== HandToHand)
			Map<String, String> profiles = new HashMap<String, String>(getAIProfiles());
			String desc = profiles.get("strategy");
			desc = desc.replace("attack weakest", "");
			profiles.put("strategy", desc);
			setAIProfiles(profiles);
		}
	}

	@Override
	public Creature getNewInstance() {
		return new RaidCreature(this);
	}

	@Override
	protected Corpse makeCorpse(String killer) {
		// use a faster rotting corpse as raids get quite messy
		return new RaidCreatureCorpse(this, killer);
	}
}
