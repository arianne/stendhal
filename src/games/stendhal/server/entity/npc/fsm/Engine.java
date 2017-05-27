// $Id$
package games.stendhal.server.entity.npc.fsm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.ConversationParser;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.ExpressionMatcher;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * a finite state machine.
 */
public class Engine {

	private static final Logger logger = Logger.getLogger(Engine.class);

	// TODO remove this dependency cycle, this is just here to simplify refactoring
	// TODO later: remove dependency on games.stendhal.server.entity.npc.* and Player
	private final SpeakerNPC speakerNPC;

	// FSM state transition table
	private final List<Transition> stateTransitionTable = new LinkedList<Transition>();

	// current FSM state
	private ConversationStates currentState = ConversationStates.IDLE;

	/**
	 * Creates a new FSM.
	 *
	 * @param speakerNPC
	 *            the speaker NPC for which this FSM is created must not be null
	 */
	public Engine(final SpeakerNPC speakerNPC) {
		if (speakerNPC == null) {
			throw new IllegalArgumentException("speakerNPC must not be null");
		}

		this.speakerNPC = speakerNPC;
	}

	/**
	 * Looks for an already registered exactly matching transition.
	 *
	 * @param state
	 * @param trigger
	 * @param condition
	 * @return previous transition entry
	 */
	private Transition get(final ConversationStates state, final Expression trigger, final ChatCondition condition) {
		for (final Transition transition : stateTransitionTable) {
			if (transition.matchesWithCondition(state, trigger, condition)) {
				return transition;
			}
		}

		return null;
	}


	/**
	 * Adds a new transition to FSM.
	 *
	 * @param state
	 *            old state
	 * @param triggerString
	 *            input trigger
	 * @param condition
	 *            additional precondition
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            state after the transition
	 * @param reply
	 *            output
	 * @param action
	 *            additional action after the condition
	 */
	public void add(final ConversationStates state, final String triggerString, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action) {
		Collection<Expression> triggerExpressions = createUniqueTriggerExpressions(
				state, Arrays.asList(triggerString), null, condition, reply, action);

		add(triggerExpressions, state, condition, secondary, nextState, reply, action);
	}


	/**
	 * Adds a new transition to FSM.
	 *
	 * @param state
	 *            old state
	 * @param triggerString
	 *            input trigger
	 * @param condition
	 *            additional precondition
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            state after the transition
	 * @param reply
	 *            output
	 * @param action
	 *            additional action after the condition
	 * @param label
	 *            a label to find this transition at a later time
	 */
	public void add(final ConversationStates state, final String triggerString, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action,
			final String label) {
		Collection<Expression> triggerExpressions = createUniqueTriggerExpressions(
				state, Arrays.asList(triggerString), null, condition, reply, action);

		add(triggerExpressions, state, condition, secondary, nextState, reply, action, label);
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 *
	 * @param state
	 *            the starting state of the FSM
	 * @param triggerStrings
	 *            a list of inputs for this transition, must not be null
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(final ConversationStates state, final Collection<String> triggerStrings, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action) {
		if (triggerStrings == null) {
			throw new IllegalArgumentException("trigger list must not be null");
		}

		Collection<Expression> triggerExpressions = createUniqueTriggerExpressions(
				state, triggerStrings, null, condition, reply, action);

		add(triggerExpressions, state, condition, secondary, nextState, reply, action);
	}


	/**
	 * Adds a new set of transitions to the FSM.
	 *
	 * @param state
	 *            the starting state of the FSM
	 * @param triggerStrings
	 *            a list of inputs for this transition, must not be null
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 * @param label
	 *            a label to find this transition at a later time
	 */
	public void add(final ConversationStates state, final Collection<String> triggerStrings, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action, final String label) {
		if (triggerStrings == null) {
			throw new IllegalArgumentException("trigger list must not be null");
		}

		Collection<Expression> triggerExpressions = createUniqueTriggerExpressions(
				state, triggerStrings, null, condition, reply, action);

		add(triggerExpressions, state, condition, secondary, nextState, reply, action, label);
	}

	/**
	 * Adds a new transition with explicit ExpressionMatcher to FSM.
	 *
	 * @param state
	 *            the starting state of the FSM
	 * @param triggerString
	 *            input for this transition, must not be null
	 * @param matcher
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void addMatching(final ConversationStates state, final String triggerString, final ExpressionMatcher matcher, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action) {
		Collection<Expression> triggerExpressions = createUniqueTriggerExpressions(
				state, Arrays.asList(triggerString), matcher, condition, reply, action);

		add(triggerExpressions, state, condition, secondary, nextState, reply, action);
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 * @param state
	 *            the starting state of the FSM
	 * @param triggerStrings
	 *            a list of inputs for this transition, must not be null
	 * @param matcher
	 *			  Expression matcher
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void addMatching(final ConversationStates state, final Collection<String> triggerStrings, final ExpressionMatcher matcher, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action) {
		if (triggerStrings == null) {
			throw new IllegalArgumentException("trigger list must not be null");
		}

		Collection<Expression> triggerExpressions = createUniqueTriggerExpressions(
				state, triggerStrings, matcher, condition, reply, action);

		add(triggerExpressions, state, condition, secondary, nextState, reply, action);
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 * @param triggerExpressions
	 *            a list of trigger expressions for this transition, must not be null
	 * @param state
	 *            the starting state of the FSM
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 * @param label
	 *            a label to find this transition at a later time
	 */
	public void add(Collection<Expression> triggerExpressions, final ConversationStates state, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action, final String label) {
		if (triggerExpressions!=null && !triggerExpressions.isEmpty()) {
			stateTransitionTable.add(new Transition(state, triggerExpressions, condition, secondary, nextState, reply, action, label));
		}
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 * @param triggerExpressions
	 *            a list of trigger expressions for this transition, must not be null
	 * @param state
	 *            the starting state of the FSM
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param secondary
	 * 			  flag to mark secondary transitions to be taken into account after preferred transitions
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(Collection<Expression> triggerExpressions, final ConversationStates state, final ChatCondition condition,
			boolean secondary, final ConversationStates nextState, final String reply, final ChatAction action) {
		if (triggerExpressions!=null && !triggerExpressions.isEmpty()) {
			stateTransitionTable.add(new Transition(state, triggerExpressions, condition, secondary, nextState, reply, action));
		}
	}

	/**
	 * remove matches transition
	 *
	 * @param label the label of transitions to remove
	 * @return true, if at least one transition was removed
	 */
	public boolean remove(final String label) {
		if ((label == null) || (label.equals(""))) {
			logger.debug("will not remove transitions with empty label");
			return false;
		}

		boolean res = false;
		Iterator<Transition> itr = stateTransitionTable.iterator();
		while (itr.hasNext()) {
			Transition transition = itr.next();
			if (transition.checkLabel(label)) {
				itr.remove();
				res = true;
			}
		}
		return res;
	}

	/**
	 * Create a collection of trigger expressions from trigger strings
	 * while checking for duplicate transitions.
	 *
	 * @param state
	 * @param triggerStrings
	 * @param matcher
	 * @param condition
	 * @param reply
	 * @param action
	 * @return trigger expressions
	 */
	private Collection<Expression> createUniqueTriggerExpressions(
			final ConversationStates state,
			final Collection<String> triggerStrings,
			final ExpressionMatcher matcher, final ChatCondition condition,
			final String reply, final ChatAction action) {
		Collection<Expression> triggerExpressions = new ArrayList<Expression>();

		for(final String triggerString : triggerStrings) {
			// normalise trigger expressions using the conversation parser
			final Expression triggerExpression = ConversationParser.createTriggerExpression(triggerString, matcher);

			// look for already existing rule with identical input parameters
			final Transition existing = get(state, triggerExpression, condition);

			if (existing != null) {
				final String existingReply = existing.getReply();
				final PostTransitionAction existingAction = existing.getAction();

				// Concatenate the previous and the new reply texts if the new one is not there already.
				if ((existingReply != null) && (reply != null) && !existingReply.contains(reply)) {
					existing.setReply(existingReply + " " + reply);
				} else {
					existing.setReply(reply);
				}

				// check for ambiguous state transitions
				if (((action == null) && (existingAction == null))
						|| ((action != null) && action.equals(existingAction))) {
					return null; // no action or equal to an already existing action
				} else {
					logger.warn(speakerNPC.getName() + ": Adding ambiguous state transition: " + existing
					+ " existingAction='" + existingAction + "' newAction='" + action + "'");
				}
			}

			triggerExpressions.add(triggerExpression);
		}

		return triggerExpressions;
	}

	/**
	 * Gets the current state.
	 *
	 * @return current state
	 */
	public ConversationStates getCurrentState() {
		return currentState;
	}

	/**
	 * Sets the current State without doing a normal transition.
	 *
	 * @param currentState
	 *            new state
	 */
	public void setCurrentState(final ConversationStates currentState) {
		this.currentState = currentState;
	}

	/**
	 * Do one transition of the finite state machine.
	 *
	 * @param player
	 *            Player
	 * @param text
	 *            input
	 * @return true if a transition was made, false otherwise
	 */
	public boolean step(final Player player, final String text) {
		final Sentence sentence = ConversationParser.parse(text);

		if (sentence.hasError()) {
			logger.warn("problem parsing the sentence '" + text + "': "
					+ sentence.getErrorString());
		}

		return step(player, sentence);
	}

	/**
	 * Do one transition of the finite state machine.
	 *
	 * @param player
	 *            Player
	 * @param sentence
	 *            input
	 * @return true if a transition was made, false otherwise
	 */
	public boolean step(final Player player, final Sentence sentence) {
		if (sentence.isEmpty()) {
			logger.debug("empty input sentence: " + getCurrentState());
			return false;
		}

		if (matchTransition(MatchType.EXACT_MATCH, player, sentence)) {
			return true;
		} else if (matchTransition(MatchType.NORMALIZED_MATCH, player, sentence)) {
			return true;
		} else if (matchTransition(MatchType.SIMILAR_MATCH, player, sentence)) {
			return true;
		} else if (matchTransition(MatchType.ABSOLUTE_JUMP, player, sentence)) {
			return true;
		} else if (matchTransition(MatchType.NORMALIZED_JUMP, player, sentence)) {
			return true;
		} else if (matchTransition(MatchType.SIMILAR_JUMP, player, sentence)) {
			return true;
		} else {
			// Couldn't match the command with the current FSM state
			logger.debug("Couldn't match any state: " + getCurrentState() + ":"
					+ sentence);
			return false;
		}
	}

	/**
	 * Do one transition of the finite state machine with debugging output and
	 * reset of the previous response.
	 *
	 * @param player
	 *            Player
	 * @param text
	 *            input
	 * @return true if a transition was made, false otherwise
	 */
	public boolean stepTest(final Player player, final String text) {
		logger.debug(">>> " + text);
		speakerNPC.remove("text");

		final Sentence sentence = ConversationParser.parse(text);

		if (sentence.hasError()) {
			logger.warn("problem parsing the sentence '" + text + "': "
					+ sentence.getErrorString());
		}

		final boolean res = step(player, sentence);

		logger.debug("<<< " + speakerNPC.get("text"));
		return res;
	}

	/**
	 * List of Transition entries used to merge identical transitions in respect
	 * to Transitions.matchesNormalizedWithCondition().
	 */
	private static class TransitionSet extends LinkedList<Transition> {
        private static final long serialVersionUID = 1L;

		@Override
		public boolean add(final Transition otherTrans) {
			for(final Transition transition : this) {
				for(Expression otherTriggerExpr : otherTrans.getTriggers()) {
					if (transition.matchesNormalizedWithCondition(otherTrans.getState(),
							otherTriggerExpr, otherTrans.getCondition())) {
						return false;
					}
				}
			}

			// No match, so add the new transition entry.
			return super.add(otherTrans);
		}

		public static void advance(final Iterator<Transition> it, final int i) {
			for (int x = i; x > 0; --x) {
				it.next();
			}
		}
	}

	private boolean matchTransition(final MatchType type, final Player player,
			final Sentence sentence) {
		// We are using sets instead of plain lists to merge identical transitions.
		final TransitionSet preferredTransitions = new TransitionSet();
		final TransitionSet secondaryTransitions = new TransitionSet();

		// match with all the registered transitions
		for (final Transition transition : stateTransitionTable) {
			if (matchesTransition(type, sentence, transition)) {
				if (transition.isConditionFulfilled(player, sentence, speakerNPC)) {
					if (transition.isPreferred()) {
						preferredTransitions.add(transition);
					} else {
						secondaryTransitions.add(transition);
					}
				}
			}
		}

		Iterator<Transition> it = null;

		// First we try to use one of the a preferred transitions (mainly with existing condition).
		if (preferredTransitions.size() > 0) {
			it = preferredTransitions.iterator();

			if (preferredTransitions.size() > 1) {
				logger.info("Choosing random action because of "
						+ preferredTransitions.size() + " entries in preferredTransitions: "
						+ preferredTransitions);

				TransitionSet.advance(it, Rand.rand(preferredTransitions.size()));
			}
		}

		// Then look for the remaining transitions.
		if ((it == null) && (secondaryTransitions.size() > 0)) {
			it = secondaryTransitions.iterator();

			if (secondaryTransitions.size() > 1) {
				logger.info("Choosing random action because of "
						+ secondaryTransitions.size()
						+ " entries in secondaryTransitions: " + secondaryTransitions);

				TransitionSet.advance(it, Rand.rand(secondaryTransitions.size()));
			}
		}

		if (it != null) {
			final Transition transition = it.next();

			executeTransition(player, sentence, transition);

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Look for a match between given sentence and transition in the current state.
	 * TODO mf - refactor match type handling
	 *
	 * @param type
	 * @param sentence
	 * @param transition
	 * @return true if transition has been found
	 */
	private boolean matchesTransition(final MatchType type, final Sentence sentence, final Transition transition) {
		return type.match(transition, currentState, sentence);
	}

	private void executeTransition(final Player player, final Sentence sentence, final Transition trans) {
		final ConversationStates nextState = trans.getNextState();

		if (trans.getReply() != null) {
			speakerNPC.say(trans.getReply());
		}

		currentState = nextState;
        if (currentState == ConversationStates.ATTENDING) {
        	speakerNPC.setIdea("attending");
        } else if (currentState != ConversationStates.IDLE) {
        	speakerNPC.setIdea("awaiting");
        }
		if (trans.getAction() != null) {
			trans.getAction().fire(player, sentence, new EventRaiser(speakerNPC));
		}

		speakerNPC.notifyWorldAboutChanges();
	}

	/**
	 * Returns a copy of the transition table.
	 *
	 * @return list of transitions
	 */
	public List<Transition> getTransitions() {
		// return a copy so that the caller cannot mess up our internal
		// structure
		return new LinkedList<Transition>(stateTransitionTable);
	}


}
