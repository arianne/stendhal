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
package games.stendhal.server.core.rule.defaultruleset.creator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;
import games.stendhal.server.entity.spell.Spell;

public class FullSpellCreator extends AbstractSpellCreator {

	public FullSpellCreator(DefaultSpell defaultSpell, Constructor<?> construct) {
		super(defaultSpell, construct);
	}

	@Override
	protected Spell createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return (Spell) construct.newInstance(defaultSpell.getName(), defaultSpell.getNature(), defaultSpell.getAmount(),
									defaultSpell.getAtk(), defaultSpell.getCooldown(),
									defaultSpell.getDef(), defaultSpell.getLifesteal(),
									defaultSpell.getMana(),
									defaultSpell.getMinimumLevel(), defaultSpell.getRange(),
									defaultSpell.getRate(), defaultSpell.getRegen(), defaultSpell.getModifier());
	}

}
