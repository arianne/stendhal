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
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * BehaviourAction handles ProducerBehaviour requests.
 */
public abstract class ProducerBehaviourAction extends AbstractBehaviourAction<ProducerBehaviour> {

	public ProducerBehaviourAction(final ProducerBehaviour behaviour) {
		this(behaviour, "produce");
	}

	public ProducerBehaviourAction(final ProducerBehaviour behaviour, String npcAction) {
		super(behaviour, behaviour.getProductionActivity(), npcAction);
	}

	@Override
	public void fireRequestError(final ProducerBehaviour behavior, final Player player, final Sentence sentence, final EventRaiser raiser) {
		behaviour.sayError(npcAction, raiser);
	}

	@Override
	public String toString() {
		return "ProducerBehaviourAction";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				ProducerBehaviourAction.class);
	}
}
