package games.stendhal.server.entity.npc.fsm;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * A transition brings a conversation from one state to another one (or to
 * the same one); while doing so, other actions can take place.
 */
public class Transition {
	// The state where this transition starts at
	private int state;

	// The state where this transition leads to
	private int nextState;

	// The string a player's text must either start with or equal to
	// in order to trigger this transition
	private String trigger;

	// The condition that has to be fulfilled so that the transition can
	// be triggered
	private PreTransitionCondition condition;

	// The text that the NPC will say when the transition is triggered
	private String reply;

	// The action that will take place when the transition is triggered
	private PostTransitionAction action;

	public Transition(int currentState, String trigger, PreTransitionCondition condition,
			int nextState, String reply, PostTransitionAction action) {
		this.state = currentState;
		this.condition = condition;
		this.nextState = nextState;
		this.trigger = trigger.toLowerCase();
		this.reply = reply;
		this.action = action;
	}

	/**
	 * Checks whether this is a "wildcard" transition (see class comment
	 * of SpeakerNPC) which can be fired by the given text.
	 * @param text The text that the player has said
	 * @return true iff this is a wildcard transition and the triggering
	 *         text has been said
	 */
	public boolean absoluteJump(String text) {
		return (state == ConversationStates.ANY) && trigger.equalsIgnoreCase(text);
	}

	/**
	 * @param state
	 * @param text
	 * @return
	 */
	public boolean matches(int state, String text) {
		return (state == this.state) && trigger.equalsIgnoreCase(text);
	}

	public boolean matchesBeginning(int state, String text) {
		text = text.toLowerCase();
		return (state == this.state) && text.startsWith(trigger);
	}

	/**
	 * Checks whether this transition's condition is fulfilled.
	 * 
	 * @param player
	 * @param text the text the player said
	 * @param npc
	 * @return true iff there is no condition or if there is one
	 *         which is fulfilled
	 */
	public boolean isConditionFulfilled(Player player, String text, SpeakerNPC npc) {
		if (condition != null) {
			return condition.fire(player, text, npc);
		} else {
			return true;
		}
	}

	

    public PostTransitionAction getAction() {
		return action;
	}

	public PreTransitionCondition getCondition() {
		return condition;
	}

	public int getNextState() {
		return nextState;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public int getState() {
		return state;
	}

	public String getTrigger() {
		return trigger;
	}

	@Override
	public String toString() {
		return "[" + state + "," + trigger + "," + nextState + ","
				+ condition + "]";
	}

}