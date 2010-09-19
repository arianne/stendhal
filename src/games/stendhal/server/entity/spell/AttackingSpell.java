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
package games.stendhal.server.entity.spell;

import games.stendhal.common.constants.Nature;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.effect.DamageEffect;

public class AttackingSpell extends Spell {

	public AttackingSpell(String name, Nature nature, int amount, int atk,
			int cooldown, int def, double lifesteal, int mana,
			int minimumlevel, int range, int rate, int regen) {
		super(name, nature, amount, atk, cooldown, def, lifesteal, mana, minimumlevel,
				range, rate, regen);
	}

	@Override
	protected boolean isTargetValid(Entity caster, Entity target) {
		return target instanceof RPEntity;
	}

	@Override
	protected void doEffects(Player caster, Entity target) {
		new DamageEffect(getNature(),getAmount(),getAtk(),getDef(),getLifesteal(),getRate(),getRegen()).act(caster, target);
	}

}
