/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.mapstuff.office.StorableEntityList;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * removes all storable entities from the specified list that
 * has the players name as identifier.
 */
@Dev(category=Category.IGNORE)
public class RemoveStorableEntityAction implements ChatAction {

	private final StorableEntityList<?> storeableEntityList;

	/**
	 * Creates a new RemoveStoreableEntity.
	 *
	 * @param storeableEntityList the list to removed entities from
	 */
	public RemoveStorableEntityAction(final StorableEntityList< ? > storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	@Override
	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		storeableEntityList.removeByName(player.getName());
	}

	@Override
	public String toString() {
		return "remove entity <" + storeableEntityList + ">";
	}

	@Override
	public int hashCode() {
		return 5387 * storeableEntityList.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof RemoveStorableEntityAction)) {
			return false;
		}
		RemoveStorableEntityAction other = (RemoveStorableEntityAction) obj;
		return storeableEntityList.equals(other.storeableEntityList);
	}
}
