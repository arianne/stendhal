// $Id$
package games.stendhal.server.entity.npc.fsm;

import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * a finate state machine.
 */
public class Engine {
	private static final Logger logger = Log4J.getLogger(Engine.class);

	// TODO: remove this dependency cicle, this is just here to simplify refactoring
	// TODO: later: remove dependency on games.stendhal.server.entity.npc.* and Player
	private SpeakerNPC speakerNPC = null;
	private int maxState = 0;
	
	// FSM state transition table
	private List<Transition> stateTransitionTable = new LinkedList<Transition>();

	// current FSM state
	private int currentState = ConversationStates.IDLE;;

	/**
	 * Creates a new FSM.
	 *
	 * @param speakerNPC the speaker npc for which this FSM is created
	 */
	public Engine(SpeakerNPC speakerNPC) {
		this.speakerNPC = speakerNPC;
	}


	private Transition get(int state, String trigger, ChatCondition condition) {
		for (Transition transition : stateTransitionTable) {
			if (transition.matches(state, trigger)) {
				if (transition.getCondition() == condition) {
					return transition;
				}
			}
		}
		return null;
	}

	/**
	 * Calculates and returns an unused state
	 *
	 * @return unused state
	 */
	public int getFreeState() {
		maxState++;
		return maxState;
	}

	/**
	 * Adds a new transition to FSM
	 *
	 * @param state old state
	 * @param trigger      input trigger
	 * @param condition    additional precondition
	 * @param nextState    state after the transition
	 * @param reply        output
	 * @param action       additional action after the condition
	 */
	public void add(int state, String trigger, ChatCondition condition,
			int nextState, String reply, ChatAction action) {
		if (state > maxState) {
			maxState = state;
		}

		Transition existing = get(state, trigger, condition);
		if (existing != null) {
			// A previous state, trigger combination exist.
			logger.warn("Adding to " + existing + " the state [" + state + ","
					+ trigger + "," + nextState + "]");
			existing.setReply(existing.getReply() + " " + reply);
		}

		Transition item = new Transition(state, trigger, condition, nextState,
				reply, action);
		stateTransitionTable.add(item);
	}


	/**
	 * Gets the current state
	 *
	 * @return current state
	 */
	public int getCurrentState() {
		return currentState;
	}

	/**
	 * Sets the current State without doing a normal transition.
	 *
	 * @param currentState new state
	 */
	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}

	/**
	 * Do one transition of the finite state machine.
	 *
	 * @param player Player
	 * @param text   input
	 * @return true if a transition was made, false otherwise
	 */
	public boolean step(Player player, String text) {
		if (matchTransition(MatchType.ABSOLUTE_JUMP, player, text)) {
			return true;
		} else if (matchTransition(MatchType.EXACT_MATCH, player, text)) {
			return true;
		} else if (matchTransition(MatchType.SIMILAR_MATCH, player, text)) {
			return true;
		} else {
			// Couldn't match the text with the current FSM state
			logger.debug("Couldn't match any state: " + getCurrentState() + ":"	+ text);
			return false;
		}
	}
	
	private boolean matchTransition(MatchType type, Player player, String text) {
		List<Transition> listCondition = new LinkedList<Transition>();
		List<Transition> listConditionLess = new LinkedList<Transition>();

		// First we try to match with stateless transitions.
		for (Transition transition : stateTransitionTable) {
			if (((type == MatchType.ABSOLUTE_JUMP) && (currentState != ConversationStates.IDLE)
					&& transition.isAbsoluteJump(text))
					|| ((type == MatchType.EXACT_MATCH) && transition.matches(currentState, text))
					|| ((type == MatchType.SIMILAR_MATCH) && transition.matchesBeginning(currentState, text))) {
				if (transition.isConditionFulfilled(player, text, speakerNPC)) {
					if (transition.getCondition() == null) {
						listConditionLess.add(transition);
					} else {
						listCondition.add(transition);
					}
				}
			}
		}

		if (listCondition.size() > 0) {
			int i = Rand.rand(listCondition.size());
			executeTransition(player, text, listCondition.get(i));
			return true;
		}

		if (listConditionLess.size() > 0) {
			int i = Rand.rand(listConditionLess.size());
			executeTransition(player, text, listConditionLess.get(i));
			return true;
		}

		return false;
	}
	
	private void executeTransition(Player player, String text, Transition state) {
		int nextState = state.getNextState();
		if (state.getReply() != null) {
			speakerNPC.say(state.getReply());
		}

		currentState = nextState;

		if (state.getAction() != null) {
			state.getAction().fire(player, text, speakerNPC);
		}
	}
	

	/**
	 * Returns a copy of the transition table
	 *
	 * @return list of transitions
	 */
	public List<Transition> getTransitions() {

		// return a copy so that the caller cannot mess up our internal structure
		return new LinkedList<Transition>(stateTransitionTable);
	}
}
