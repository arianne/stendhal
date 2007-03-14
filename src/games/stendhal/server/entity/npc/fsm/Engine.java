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
	private SpeakerNPC speakerNPC = null;
	private int maxState = 0;
	
	// FSM state transition table
	private List<Transition> stateTransitionTable = new LinkedList<Transition>();

	// current FSM state
	private int currentState = ConversationStates.IDLE;;

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

	public int getFreeState() {
		maxState++;
		return maxState;
	}

	/** Add a new transition to FSM */
	public void add(int state, String trigger, ChatCondition condition,
			int next_state, String reply, ChatAction action) {
		if (state > maxState) {
			maxState = state;
		}

		Transition existing = get(state, trigger, condition);
		if (existing != null) {
			// A previous state, trigger combination exist.
			logger.warn("Adding to " + existing + " the state [" + state + ","
					+ trigger + "," + next_state + "]");
			existing.setReply(existing.getReply() + " " + reply);
		}

		Transition item = new Transition(state, trigger, condition, next_state,
				reply, action);
		stateTransitionTable.add(item);
	}


	public int getCurrentState() {
		return currentState;
	}


	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}
	
	public boolean matchState(MatchType type, Player player, String text) {
		List<Transition> listCondition = new LinkedList<Transition>();
		List<Transition> listConditionLess = new LinkedList<Transition>();

		// First we try to match with stateless transitions.
		for (Transition transition : stateTransitionTable) {
			if (((type == MatchType.ABSOLUTE_JUMP) && (currentState != ConversationStates.IDLE)
					&& transition.absoluteJump(text))
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
			executeState(player, text, listCondition.get(i));
			return true;
		}

		if (listConditionLess.size() > 0) {
			int i = Rand.rand(listConditionLess.size());
			executeState(player, text, listConditionLess.get(i));
			return true;
		}

		return false;
	}
	
	private void executeState(Player player, String text, Transition state) {
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
	 */
	public List<Transition> getTransitions() {
		return new LinkedList<Transition>(stateTransitionTable);
	}
}
