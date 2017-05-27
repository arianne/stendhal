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
package games.stendhal.server.core.account;

import java.util.Collection;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;
import marauroa.common.game.Result;

/**
 * validates name is not a Creature name
 *
 * @author kymara
 */
public class CreatureNameValidator implements AccountParameterValidator {
	private final String parameterValue;

	/**
	 * creates a CreatureNameValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 */
	public CreatureNameValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public Result validate() {
		final Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
		for (final Creature creature : creatures) {
			if (creature.getName().equals(parameterValue)) {
				return Result.FAILED_RESERVED_NAME;
			}
		}
		return null;
	}
}
