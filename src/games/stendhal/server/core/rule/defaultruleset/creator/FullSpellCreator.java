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

import games.stendhal.server.core.rule.defaultruleset.DefaultSpell;
import games.stendhal.server.entity.spell.Spell;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class FullSpellCreator extends AbstractSpellCreator {

	public FullSpellCreator(DefaultSpell defaultSpell, Constructor<?> construct) {
		super(defaultSpell, construct);
	}

	@Override
	protected Spell createObject() throws IllegalAccessException,
			InstantiationException, InvocationTargetException {
		return (Spell) construct.newInstance(defaultSpell.getName(), defaultSpell.getNature(), (Object) defaultSpell.getAmount(),
									(Object) defaultSpell.getAtk(), (Object) defaultSpell.getCooldown(),
									(Object) defaultSpell.getDef(), (Object) defaultSpell.getLifesteal(),
									(Object) defaultSpell.getMana(),
									(Object) defaultSpell.getMinimumLevel(), (Object) defaultSpell.getRange(),
									(Object) defaultSpell.getRate(), (Object) defaultSpell.getRegen());
	}

}
