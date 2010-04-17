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

/*
 * HealingSpell.java
 *
 * Created on March 29, 2007, 5:37 PM
 */

package games.stendhal.server.entity.spell;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.effect.HealingEffect;


/**
 * @author timothyb89, madmetzger A healing spell. It restores the user to full HP (for
 *         now).
 */
public class HealingSpell extends Spell {
	
	public HealingSpell(final String name, final int amount, final int atk, final int cooldown,
			final int def, final double lifesteal, final int mana, final int minimumlevel, final int range,
			final int rate, final int regen) {
		super(name, amount, atk, cooldown, def, lifesteal, mana, minimumlevel, range,
				rate, regen);
	}

	@Override
	protected void doEffects(Player caster, Entity target) {
		//implement healing here
		new HealingEffect(getAmount(), getAtk(), getDef(), getLifesteal(), getRate(), getRegen()).act(caster, target);
	}

	@Override
	protected boolean isTargetValid(Entity target) {
		if (target instanceof Player) {
			return true;
		}
		return false;
	}
	
}
