package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * A transition brings a conversation from one state to another one (or to the
 * same one); while doing so, other actions can take place.
 */
public class Transition {

	/** The state where this transition starts at */
	private int state;

	/** The state where this transition leads to */
	private int nextState;

	/**
	 * The string a player's text must either start with or equal to in order to
	 * trigger this transition The string is stored in lower case.
	 */
	private String trigger;

	/**
	 * The condition that has to be fulfilled so that the transition can be
	 * triggered
	 */
	private PreTransitionCondition condition;

	/** The text that the NPC will say when the transition is triggered */
	private String reply;

	/** The action that will take place when the transition is triggered */
	private PostTransitionAction action;

	/**
	 * Creates a new transition
	 * 
	 * @param currentState
	 *            old state
	 * @param trigger
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
	public Transition(int currentState, String trigger,
			PreTransitionCondition condition, int nextState, String reply,
			PostTransitionAction action) {
		this.state = currentState;
		this.condition = condition;
		this.nextState = nextState;
		this.trigger = trigger.toLowerCase();
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
				&& trigger.equals(sentence.getTrigger());
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment of
	 * SpeakerNPC) and the text beginning matches the trigger.
	 * 
	 * @param sentence
	 *            trigger (parsed user input)
	 * @return if the transition matches, false otherwise
	 */
	public boolean matchesWildBeginning(Sentence sentence) {
		return (state == ConversationStates.ANY)
				&& sentence.getTrigger().startsWith(trigger);
	}

	/**
	 * checks whether this transition is possible now.
	 * 
	 * @param state
	 *            old state
	 * @param sentence
	 *            trigger
	 * @return true if the transition matches, false otherwise
	 */
	public boolean matches(int state, Sentence sentence) {
		return matches(state, sentence.getTrigger());
	}

	/**
	 * checks whether this transition is possible now.
	 * 
	 * @param state
	 *            old state
	 * @param text
	 *            trigger
	 * @return true if the transition matches, false otherwise
	 */
	public boolean matches(int state, String text) {
		return (state == this.state) && trigger.equals(text);
	}

	/**
	 * checks whether this transition is possible now
	 * 
	 * @param state
	 *            old state
	 * @param sentence
	 *            trigger, parsed user input
	 * @return true if the transition matches, false otherwise
	 */
	public boolean matchesBeginning(int state, Sentence sentence) {
		return (state == this.state) && sentence.getTrigger().startsWith(trigger);
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
	 * Sets the output for this transition
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
	public String getTrigger() {
		return trigger;
	}

	@Override
	public String toString() {
		return "[" + state + "," + trigger + "," + nextState + "," + condition
				+ "]";
	}

}
