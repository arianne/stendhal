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
import java.util.LinkedList;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
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
	private final Collection<Expression> triggers;

	/**
	 * The condition that has to be fulfilled so that the transition can be
	 * triggered.
	 */
	private final PreTransitionCondition condition;

	/** Flag to mark secondary transitions to be taken into account after preferred transitions */
	private final boolean secondary;

	/** The text that the NPC will say when the transition is triggered.*/
	private String reply;

	/** The action that will take place when the transition is triggered. */
	private final PostTransitionAction action;

	/** Label for search through installed transitions */
	private final String label;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Creates a new transition.
	 *
	 * @param currentState
	 *            old state
	 * @param triggers
	 *            input triggers
	 * @param condition
	 *            additional precondition
	 * @param secondary
	 *			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            state after the transition
	 * @param reply
	 *            output
	 * @param action
	 *            additional action after the condition
	 * @param label
	 *            string label to handle transitions in NPC's FSM table
	 */
	public Transition(final ConversationStates currentState, final Collection<Expression> triggers,
			final PreTransitionCondition condition, final boolean secondary, final ConversationStates nextState,
			final String reply, final PostTransitionAction action, final String label) {
		this.state = currentState;
		this.condition = condition;
		this.secondary = secondary;
		this.nextState = nextState;
		this.triggers = triggers;
		this.reply = reply;
		this.action = action;
		this.label = TransitionContext.getWithFallback(label);
	}

	/**
	 * Creates a new transition.
	 *
	 * @param currentState
	 *            old state
	 * @param triggers
	 *            input triggers
	 * @param condition
	 *            additional precondition
	 * @param secondary
	 *			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            state after the transition
	 * @param reply
	 *            output
	 * @param action
	 *            additional action after the condition
	 */
	public Transition(final ConversationStates currentState, final Collection<Expression> triggers,
			final PreTransitionCondition condition, final boolean secondary, final ConversationStates nextState,
			final String reply, final PostTransitionAction action) {
		this.state = currentState;
		this.condition = condition;
		this.secondary = secondary;
		this.nextState = nextState;
		this.triggers = triggers;
		this.reply = reply;
		this.action = action;
		this.label = TransitionContext.getWithFallback("");
	}

	/**
	 * Create transition and copy values from existing transition
	 *
	 * @param tr - source transition, must not be null
	 */
	public Transition(Transition tr) {
		this.state = tr.state;
		this.condition = tr.condition;
		this.secondary = tr.secondary;
		this.nextState = tr.nextState;
		this.triggers = new LinkedList<Expression>(tr.triggers);
		this.reply = tr.reply;
		this.action = tr.action;
		this.label = TransitionContext.getWithFallback(tr.label);
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
		if (state == ConversationStates.ANY) {
			for(Expression triggerExpr : triggers) {
				if (sentence.getTriggerExpression().matches(triggerExpr)) {
					return true;
				}
			}
		}

		return false;
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
		if (state == ConversationStates.ANY) {
			for(Expression triggerExpr : triggers) {
				if (sentence.getTriggerExpression().matchesNormalized(triggerExpr)) {
					return true;
				}
			}
		}

		return false;
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
			for(Expression triggerExpr : triggers) {
	            // If the trigger is an empty string, match any text.
	            //TODO find a better way to handle unconditional matching; perform JokerMatch comparisons here, so that they can catch all not yet recognized text
	            if (triggerExpr.getNormalized().length() == 0) {
	                return true;
	            }

				if (sentence.getTriggerExpression().matchesNormalizedSimilar(triggerExpr)) {
					return true;
				}
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
		if (state == this.state) {
			for(Expression triggerExpr : this.triggers) {
				if (trigger.matches(triggerExpr)) {
					return true;
				}
			}
		}

		return false;
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
		if (state == this.state) {
			for(Expression triggerExpr : this.triggers) {
				if (trigger.matchesNormalized(triggerExpr)) {
					return true;
				}
			}
		}

		return false;
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
			for(Expression triggerExpr : triggers) {
				// If the trigger is an empty string, match any text.
				//TODO find a better way to handle unconditional matching; perform JokerMatch comparisons here, so that they can catch all not yet recognized text
				if (triggerExpr.getNormalized().length() == 0) {
					return true;
				}

				if (sentence.getTriggerExpression().matchesNormalizedSimilar(triggerExpr)) {
					return true;
				}
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
	 * Checks for labels equality
	 *
	 * @param aLabel label to compare
	 * @return check result
	 */
	public boolean checkLabel(final String aLabel) {
		if (aLabel == null || aLabel.equals("")) {
			logger.debug("Never match an empty label");
			return false;
		}
		return aLabel.equals(this.label);
	}

	/**
	 * check if label is empty string
	 * @return - check result
	 */
	public boolean isEmptyLabel() {
		return this.label.isEmpty();
	}

	/**
	 * Checks whether this transition's condition is fulfilled.
	 *
	 * @param player Player
	 * @param sentence
	 *            the sentence the player said
	 * @param npc SpeakerNPC
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
	 * @return true if this transition should be preferred over others,
	 * which also have a matching condition or no condition at all
	 */
	public boolean isPreferred() {
		if (secondary) {
			return false;
		} else if (condition != null) {
			return true;
		} else {
			return false;
		}
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
	public Collection<Expression> getTriggers() {
		return triggers;
	}

	/**
	 * Return a string representation of this Transition.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[" + state + ",");

		int i = 0;
		for(Expression triggerExpr : triggers) {
			if (i++ > 0) {
				sb.append('|');
			}
			sb.append(triggerExpr);
		}
		sb.append(',');

		return sb.append(nextState + "," + condition + ",\"" + label + "\"]").toString();
	}

}
