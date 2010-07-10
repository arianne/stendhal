// $Id$
package games.stendhal.server.entity.npc.fsm;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.ConversationParser;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.ExpressionMatcher;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

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
			throw new IllegalArgumentException("speakerNpc must not be null");
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
	 * @param nextState
	 *            state after the transition
	 * @param reply
	 *            output
	 * @param action
	 *            additional action after the condition
	 */
	public void add(final ConversationStates state, final String triggerString, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		add(state, triggerString, null, condition, nextState, reply, action);
	}

	/**
	 * Adds a new transition with explicit ExpressionMatcher to FSM.
	 *
	 * @param state 
	 * @param triggerString
	 * @param matcher
	 * @param condition
	 * @param nextState
	 * @param reply
	 * @param action
	 */
	public void add(final ConversationStates state, final String triggerString, final ExpressionMatcher matcher, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		// normalize trigger expressions using the conversation parser
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


			if (((action == null) && (existingAction == null))
					|| ((action != null) && action.equals(existingAction))) {
				
				return;
			} else {
				logger.warn(speakerNPC.getName() + ": Adding ambiguous state transition: " + existing
				+ " existingAction='" + existingAction + "' newAction='" + action + "'");
			}
		}

		stateTransitionTable.add(new Transition(state, triggerExpression, condition, nextState, reply, action));
	}

	/**
	 * Adds a new set of transitions to the FSM.
	 * 
	 * @param state
	 *            the starting state of the FSM
	 * @param triggers
	 *            a list of inputs for this transition, must not be null
	 * @param condition
	 *            null or condition that has to return true for this transition
	 *            to be considered
	 * @param nextState
	 *            the new state of the FSM
	 * @param reply
	 *            a simple sentence reply (may be null for no reply)
	 * @param action
	 *            a special action to be taken (may be null)
	 */
	public void add(final ConversationStates state, final List<String> triggers, final ChatCondition condition,
			final ConversationStates nextState, final String reply, final ChatAction action) {
		if (triggers == null) {
			throw new IllegalArgumentException("triggers list must not be null");
		}
		for (final String trigger : triggers) {
			add(state, trigger, condition, nextState, reply, action);
		}
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
	private static class TransitionList extends LinkedList<Transition> {
        private static final long serialVersionUID = 1L;

		@Override
		public boolean add(final Transition otherTrans) {
			for (final Transition transition : this) {
				if (transition.matchesNormalizedWithCondition(otherTrans.getState(),
						otherTrans.getTrigger(), otherTrans.getCondition())) {
					return false;
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
		// We are using sets instead of lists to merge identical transitions.
		final TransitionList conditionTransitions = new TransitionList();
		final TransitionList conditionlessTransitions = new TransitionList();

		// match with all the registered transitions
		for (final Transition transition : stateTransitionTable) {
			if (matchesTransition(type, sentence, transition)) {
				if (transition.isConditionFulfilled(player, sentence, speakerNPC)) {
					if (transition.getCondition() == null) {
						conditionlessTransitions.add(transition);
					} else {
						conditionTransitions.add(transition);
					}
				}
			}
		}

		Iterator<Transition> it = null;

		// First we try to use a stateless transition.
		if (conditionTransitions.size() > 0) {
			it = conditionTransitions.iterator();

			if (conditionTransitions.size() > 1) {
				logger.info("Choosing random action because of "
						+ conditionTransitions.size() + " entries in conditionTransitions: "
						+ conditionTransitions);

				TransitionList.advance(it, Rand.rand(conditionTransitions.size()));
			}
		}

		// Then look for transitions without conditions.
		if ((it == null) && (conditionlessTransitions.size() > 0)) {
			it = conditionlessTransitions.iterator();

			if (conditionlessTransitions.size() > 1) {
				logger.warn("Choosing random action because of "
						+ conditionlessTransitions.size()
						+ " entries in conditionlessTransitions: " + conditionlessTransitions);

				TransitionList.advance(it, Rand.rand(conditionlessTransitions.size()));
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

		if (trans.getAction() != null) {
			trans.getAction().fire(player, sentence, new EventRaiser(speakerNPC));
		}
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
