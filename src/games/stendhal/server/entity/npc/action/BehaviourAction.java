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

import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.impl.Behaviour;
import games.stendhal.server.entity.npc.behaviour.impl.BehaviourResult;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * BehaviourAction handles Behaviour requests.
 */
public abstract class BehaviourAction extends AbstractBehaviourAction<Behaviour> {

	public BehaviourAction(final Behaviour behaviour, String userAction, String npcAction) {
		super(behaviour, userAction, npcAction);
	}

	public void fireRequestError(final BehaviourResult res, final Player player, final Sentence sentence, final EventRaiser npc) {
		behaviour.sayError(res, userAction, npcAction, npc);
	}

	@Override
	public String toString() {
		return "BehaviourAction";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				BehaviourAction.class);
	}

}
