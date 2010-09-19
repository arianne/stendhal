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
package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.mapstuff.office.StoreableEntityList;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * removes all storeable entities from the specified list that
 * has the players name as identifier.
 */
public class RemoveStoreableEntityAction implements ChatAction {

	private final StoreableEntityList< ? > storeableEntityList;

	/**
	 * Creates a new RemoveStoreableEntity.
	 *
	 * @param storeableEntityList the list to removed entities from
	 */
	public RemoveStoreableEntityAction(final StoreableEntityList< ? > storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
		storeableEntityList.removeByName(player.getName());
	}

	@Override
	public String toString() {
		return "remove entity <" + storeableEntityList + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, 
			RemoveStoreableEntityAction.class);
	}
}
