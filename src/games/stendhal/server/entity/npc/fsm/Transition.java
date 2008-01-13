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
	private int state;

	/** The state where this transition leads to. */
	private int nextState;

	/**
	 * The Word a player's text must either start with or equal to in order to trigger
	 * this transition. The trigger string is normalized by Sentence.getTriggerWord().
	 */
	private Expression trigger;

	/**
	 * The condition that has to be fulfilled so that the transition can be
	 * triggered.
	 */
	private PreTransitionCondition condition;

	/** The text that the NPC will say when the transition is triggered.*/
	private String reply;

	/** The action that will take place when the transition is triggered. */
	private PostTransitionAction action;

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
	public Transition(int currentState, Expression triggerExpr,
			PreTransitionCondition condition, int nextState, String reply,
			PostTransitionAction action) {
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
	public boolean matchesWild(Sentence sentence) {
		return (state == ConversationStates.ANY)
				&& trigger.matches(sentence.getTriggerExpression());
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment of
	 * SpeakerNPC) and the normalized text matches the trigger.
	 * 
	 * @param sentence
	 *            trigger (parsed user input)
	 * @return if the transition matches, false otherwise
	 */
	public boolean matchesWildNormalized(Sentence sentence) {
		return (state == ConversationStates.ANY)
				&& sentence.getTriggerExpression().matchesNormalized(trigger);
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment of
	 * SpeakerNPC) and the normalized text beginning matches the trigger.
	 * 
	 * @param sentence
	 *            trigger (parsed user input)
	 * @return if the transition matches, false otherwise
	 */
	public boolean matchesWildBeginning(Sentence sentence) {
		return (state == ConversationStates.ANY)
				&& sentence.getTriggerExpression().matchesNormalizedBeginning(trigger);
	}

	/**
	 * Checks whether this transition is possible now.
	 * 
	 * @param state
	 *            old state
	 * @param sentence
	 *            trigger
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matches(int state, Sentence sentence) {
		return matches(state, sentence.getTriggerExpression());
	}

	/**
	 * Checks whether this transition is possible now.
	 * 
	 * @param state
	 *            old state
	 * @param text
	 *            trigger
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matches(int state, Expression trigger) {
		return (state == this.state) && this.trigger.matches(trigger);
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
	public boolean matchesNormalized(int state, Sentence sentence) {
		return matchesNormalized(state, sentence.getTriggerExpression());
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
	public boolean matchesNormalized(int state, Expression trigger) {
		return (state == this.state) && this.trigger.matchesNormalized(trigger);
	}

	/**
	 * Checks whether this transition is possible now by only looking at
	 * the beginning of the normalized expression.
	 * 
	 * @param state
	 *            old state
	 * @param sentence
	 *            trigger, parsed user input
	 * @return true if the Transition matches, false otherwise
	 */
	public boolean matchesBeginning(int state, Sentence sentence) {
		return (state == this.state) && sentence.getTriggerExpression().matchesNormalizedBeginning(trigger);
	}

	/**
	 * Check for match with the given state/trigger/condition combination
	 * 
	 * @param state
	 * @param trigger
	 * @param condition
	 * @return
	 */
	public boolean matchesWithCondition(int state, Expression trigger, PreTransitionCondition condition) {
		if (matches(state, trigger)) {
			if (this.condition == condition) {
				return true;
			} else if (this.condition != null && this.condition.equals(condition)) {
				return true;
			}
		}

		// no match
		return false;
    }

	/**
	 * Check for normalized match with the given state/trigger/condition combination
	 * 
	 * @param state
	 * @param trigger
	 * @param condition
	 * @return
	 */
	public boolean matchesNormalizedWithCondition(int state, Expression trigger, PreTransitionCondition condition) {
		if (matchesNormalized(state, trigger)) {
			if (this.condition == condition) {
				return true;
			} else if (this.condition != null && this.condition.equals(condition)) {
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
	public boolean isConditionFulfilled(Player player, Sentence sentence,
			SpeakerNPC npc) {
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
	public int getNextState() {
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
	public void setReply(String reply) {
		this.reply = reply;
	}

	/**
	 * @return the source state
	 */
	public int getState() {
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
