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
package games.stendhal.server.entity.npc.fsm;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import games.stendhal.common.parser.Expression;
import games.stendhal.server.entity.npc.ConversationStates;

/**
 * easy access to a list of transitions for debugging.
 *
 * @author hendrik
 */
public class TransitionList {
	private final List<Transition> transitions;

	/**
	 * Creates a new TransitionList.
	 *
	 * @param transitions
	 *            list of transitions
	 */
	public TransitionList(final List<Transition> transitions) {
		this.transitions = transitions;
	}

	/**
	 * gets all source states.
	 *
	 * @return Set of source states
	 */
	public Set<ConversationStates> getSourceStates() {
		final Set<ConversationStates> res = EnumSet.noneOf(ConversationStates.class);
		for (final Transition transition : transitions) {
			res.add(transition.getState());
		}
		return res;
	}

	/**
	 * returns a set of triggers for a given source state.
	 *
	 * @param state
	 *            source state
	 * @return set of triggers
	 */
	public Collection<Expression> getTriggersForState(final ConversationStates state) {
		final Set<Expression> res = new HashSet<Expression>();
		for (final Transition transition : transitions) {
			if (transition.getState() == state) {
				for(Expression triggerExpr : transition.getTriggers()) {
					res.add(triggerExpr);
				}
			}
		}
		return res;
	}

	/**
	 * returns a list of transitions for this state-trigger pair.
	 *
	 * @param state
	 *            source state
	 * @param trigger
	 *            trigger
	 * @return list of transitions
	 */
	public List<Transition> getTransitionsForStateAndTrigger(final ConversationStates state,
			final Expression trigger) {
		final List<Transition> res = new LinkedList<Transition>();
		for (final Transition transition : transitions) {
			if (transition.getState() == state) {
				for(Expression triggerExpr : transition.getTriggers()) {
					if (triggerExpr.matches(trigger)) {
						res.add(transition);
					}
				}
			}
		}
		return res;
	}
}
