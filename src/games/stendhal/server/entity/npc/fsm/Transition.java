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

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * A transition brings a conversation from one state to another one (or to the
 * same one); while doing so, other actions can take place.
 */
public class Transition {

	/** The state where this transition starts at .*/
	private final ConversationStates state;

	/** The state where this transition leads to. */
	private final ConversationStates nextState;

	/**
	 * The Word a player's text must either start with or equal to in order to trigger
	 * this transition. The trigger string is normalized by Sentence.getTriggerWord().
	 */
	private final Expression trigger;

	/**
	 * The condition that has to be fulfilled so that the transition can be
	 * triggered.
	 */
	private final PreTransitionCondition condition;

	/** The text that the NPC will say when the transition is triggered.*/
	private String reply;

	/** The action that will take place when the transition is triggered. */
	private final PostTransitionAction action;

	/**
	 * Creates a new transition.
	 * 
	 * @param currentState
	 *            old state
	 * @param triggerExpr
	 *            input trigger
	 * @param condition
	 *            additional precondition
	 * @param nextState
	 *            state after the transition
	 * @param reply
	 *            output
	 * @param action
	 *            additional action after the condition
	 */
	public Transition(final ConversationStates currentState, final Expression triggerExpr,
			final PreTransitionCondition condition, final ConversationStates nextState, final String reply,
			final PostTransitionAction action) {
		this.state = currentState;
		this.condition = condition;
		this.nextState = nextState;
		this.trigger = triggerExpr;
		this.reply = reply;
		this.action = action;
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment of
	 * SpeakerNPC) which can be fired by the given text.
	 * 
	 * @param sentence
	 *            The sentence that the player has said
	 * @return true iff this is a wildcard transition and the triggering text
	 *         has been said
	 */
	public boolean matchesWild(final Sentence sentence) {
		return (state == ConversationStates.ANY)
				&& sentence.getTriggerExpression().matches(trigger);
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment of
	 * SpeakerNPC) and the normalized text matches the trigger.
	 * 
	 * @param sentence
	 *            trigger (parsed user input)
	 * @return if the transition matches, false otherwise
	 */
	public boolean matchesWildNormalized(final Sentence sentence) {
		return (state == ConversationStates.ANY)
				&& sentence.getTriggerExpression().matchesNormalized(trigger);
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment of
	 * SpeakerNPC) and the normalized text is similar to the trigger.
	 * 
	 * @param sentence
	 *            trigger (parsed user input)
	 * @return if the transition matches, false otherwise
	 */
	public boolean matchesWildSimilar(final Sentence sentence) {
		if (state == ConversationStates.ANY) {
			// If the trigger is an empty string, match any text.
			//TODO find a better way to handle unconditional matching
			if (trigger.getNormalized().length() == 0) {
				return true;
			}

			if (sentence.getTriggerExpression().matchesNormalizedSimilar(trigger)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks whether this transition is possible now.
	 * 
	 * @param currentState
	 *            old state
	 * @param sentence
	 *            trigger
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matches(final ConversationStates currentState, final Sentence sentence) {
		return matches(currentState, sentence.getTriggerExpression());
	}

	/**
	 * Checks whether this transition is possible now.
	 * 
	 * @param state
	 *            old state
	 * @param trigger
	 *            trigger
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matches(final ConversationStates state, final Expression trigger) {
		return (state == this.state) && trigger.matches(this.trigger);
	}

	/**
	 * Checks whether this transition is possible now by using matching
	 * of the normalized expression.
	 * 
	 * @param state
	 *            old state
	 * @param sentence
	 *            trigger
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matchesNormalized(final ConversationStates state, final Sentence sentence) {
		return matchesNormalized(state, sentence.getTriggerExpression());
	}

	/**
	 * Checks whether this transition is possible now by using matching
	 * of the normalized expression.
	 * 
	 * @param state
	 *            old state
	 * @param trigger
	 *            trigger
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matchesNormalized(final ConversationStates state, final Expression trigger) {
		return (state == this.state) && trigger.matchesNormalized(this.trigger);
	}

	/**
	 * Checks whether this transition is possible now by checking the similarity of the
	 * normalized expression.
	 * 
	 * @param state
	 *            old state
	 * @param sentence
	 *            trigger, parsed user input
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matchesSimilar(final ConversationStates state, final Sentence sentence) {
		if (state == this.state) {
			// If the trigger is an empty string, match any text.
			//TODO find a better way to handle unconditional matching
			if (trigger.getNormalized().length() == 0) {
				return true;
			}

			if (sentence.getTriggerExpression().matchesNormalizedSimilar(trigger)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks for match with the given state/trigger/condition combination.
	 * 
	 * @param state
	 * @param trigger
	 * @param condition
	 * @return true if condition has been found
	 */
	public boolean matchesWithCondition(final ConversationStates state, final Expression trigger, final PreTransitionCondition condition) {
		if (matches(state, trigger)) {
			if (this.condition == condition) {
				return true;
			} else if ((this.condition != null) && this.condition.equals(condition)) {
				return true;
			}
		}

		// no match
		return false;
    }

	/**
	 * Checks for normalized match with the given state/trigger/condition combination.
	 * 
	 * @param state
	 * @param trigger
	 * @param condition
	 * @return true if condition has been found
	 */
	public boolean matchesNormalizedWithCondition(final ConversationStates state, final Expression trigger, final PreTransitionCondition condition) {
		if (matchesNormalized(state, trigger)) {
			if (this.condition == condition) {
				return true;
			} else if ((this.condition != null) && this.condition.equals(condition)) {
				return true;
			}
		}

		// no match
		return false;
    }

	/**
	 * Checks whether this transition's condition is fulfilled.
	 * 
	 * @param player
	 * @param sentence
	 *            the sentence the player said
	 * @param npc
	 * @return true iff there is no condition or if there is one which is
	 *         fulfilled
	 */
	public boolean isConditionFulfilled(final Player player, final Sentence sentence,
			final SpeakerNPC npc) {
		if (condition != null) {
			return condition.fire(player, sentence, npc);
		} else {
			return true;
		}
	}

	/**
	 * @return Action to execute after transition or null if there is none
	 */
	public PostTransitionAction getAction() {
		return action;
	}

	/**
	 * @return condition to check before doing the transition or null if there
	 *         is non
	 */
	public PreTransitionCondition getCondition() {
		return condition;
	}

	/**
	 * @return state after transition
	 */
	public ConversationStates getNextState() {
		return nextState;
	}

	/**
	 * @return output or null if there is none
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * Sets the output for this transition.
	 * 
	 * @param reply
	 *            output
	 */
	public void setReply(final String reply) {
		this.reply = reply;
	}

	/**
	 * @return the source state
	 */
	public ConversationStates getState() {
		return state;
	}

	/**
	 * @return input
	 */
	public Expression getTrigger() {
		return trigger;
	}

	/**
	 * Return a string representation of this Transition.
	 */
	@Override
	public String toString() {
		return "[" + state + "," + trigger + "," + nextState + "," + condition
				+ "]";
	}

}
