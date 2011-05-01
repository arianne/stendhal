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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ConversationStates;

/**
 * How strong possible transitions should be matched.
 */
public enum MatchType {

	/** a transition whose expected input matches exactly to the actual input, used to
	 * distinguish between words in singular and plural form, e.g. "cloak" and "cloaks" */
	EXACT_MATCH {
		@Override
		public boolean match(final Transition transition, final ConversationStates currentState,
				final Sentence sentence) {
			return transition.matches(currentState, sentence);
		}
	},

	/** a transition whose normalized expected input matches the normalized input. */
	NORMALIZED_MATCH {
		@Override
		public boolean match(final Transition transition, final ConversationStates currentState,
				final Sentence sentence) {
			return transition.matchesNormalized(currentState, sentence);
		}
	},

	/** a transition whose expected input is very similar to the actual input. */
	SIMILAR_MATCH {
		@Override
		public boolean match(final Transition transition, final ConversationStates currentState,
				final Sentence sentence) {
			return transition.matchesSimilar(currentState, sentence);
		}
	},

	/** a transition that can happen from any state with exact text match. */
	ABSOLUTE_JUMP {
		@Override
		public boolean match(final Transition transition, final ConversationStates currentState,
				final Sentence sentence) {
			return (currentState != ConversationStates.IDLE)
			&& transition.matchesWild(sentence);
		}
	},

	/** a transition that can happen from any state with normalized text match. */
	NORMALIZED_JUMP {
		@Override
		public boolean match(final Transition transition, final ConversationStates currentState,
				final Sentence sentence) {
			return (currentState != ConversationStates.IDLE)
			&& transition.matchesWildNormalized(sentence);
		}
	},

	/** a transition that can happen from any state with similar text match. */
	SIMILAR_JUMP {
		@Override
		public boolean match(final Transition transition, final ConversationStates currentState,
				final Sentence sentence) {
			return (currentState != ConversationStates.IDLE)
			&& transition.matchesWildSimilar(sentence);
		}
	};

	public boolean match(final Transition transition,
			final ConversationStates currentState, final Sentence sentence) {
		return false;
	}

}
