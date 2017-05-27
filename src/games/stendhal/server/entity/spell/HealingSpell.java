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

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.effect.HealingEffect;
import marauroa.common.game.RPObject;


/**
 * A healing spell. It the HP of a player by the given amount
 *
 * @author timothyb89, madmetzger
 */
public class HealingSpell extends Spell {

	public HealingSpell(final String name, final Nature nature, final int amount, final int atk, final int cooldown,
			final int def, final double lifesteal, final int mana, final int minimumlevel, final int range,
			final int rate, final int regen, double modifier) {
		super(name, nature, amount, atk, cooldown, def, lifesteal, mana, minimumlevel, range,
				rate, regen, modifier);
	}

	public HealingSpell(RPObject object) {
		super(object);
	}

	@Override
	protected void doEffects(Player caster, Entity target) {
		//implement healing here
		new HealingEffect(getNature(), getAmount(), getAtk(), getDef(), getLifesteal(), getRate(), getRegen(), getModifier()).act(caster, target);
	}

	@Override
	protected boolean isTargetValid(Entity caster, Entity target) {
		if (target instanceof Player) {
			if (!caster.equals(target)) {
				return true;
			}
		}
		return false;
	}

}
