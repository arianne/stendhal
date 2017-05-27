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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;
import marauroa.common.game.Result;

/**
 * validates name is not an NPC name
 *
 * @author kymara
 */
public class NPCNameValidator implements AccountParameterValidator {
	private final String parameterValue;

	/**
	 * creates a NPCNameValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 */
	public NPCNameValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public Result validate() {
		final NPCList npcs = SingletonRepository.getNPCList();
		for (final String name : npcs.getNPCs()) {
			if (name.equals(parameterValue)) {
				return Result.FAILED_RESERVED_NAME;
			}
		}
		return null;
	}
}
