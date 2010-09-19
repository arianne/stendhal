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
package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.office.StoreableEntityList;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is there a storeable entity in the specified list that has name
 * of the current player as identifier? 
 */
public class PlayerHasStoreableEntityCondition implements ChatCondition {
	private final StoreableEntityList< ? > storeableEntityList;
	
	public PlayerHasStoreableEntityCondition(final StoreableEntityList< ? > storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return storeableEntityList.getByName(player.getName()) != null;
	}

	@Override
	public String toString() {
		return "in list <" + storeableEntityList + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
			PlayerHasStoreableEntityCondition.class);
	}
}
