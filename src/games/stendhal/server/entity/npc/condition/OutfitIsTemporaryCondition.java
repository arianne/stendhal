/***************************************************************************
 *                      (C) Copyright 2023 - Stendhal                      *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.condition;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;


public class OutfitIsTemporaryCondition extends AbstractChatCondition {

	private static final int hashModifier = AbstractChatCondition.getNextUniqueHashModifier();


	@Override
	public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
		return player.outfitIsTemporary();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public int hashCode() {
		return hashModifier;
	}

	@Override
	public boolean equals(final Object other) {
		return (other instanceof OutfitIsTemporaryCondition);
	}
}
