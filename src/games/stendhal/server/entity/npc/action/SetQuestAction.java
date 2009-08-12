package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * Sets the current state of a quest.
 */
public class SetQuestAction implements ChatAction {

	private final String questname;
	private final int index;
	private final String state;

	/**
	 * Creates a new SetQuestAction.
	 * 
	 * @param questname
	 *            name of quest-slot to change
	 * @param state
	 *            new value
	 */
	public SetQuestAction(final String questname, final String state) {
		this.questname = questname;
		this.index = -1;
		this.state = state;
	}

	/**
	 * Creates a new SetQuestAction.
	 * 
	 * @param questname
	 *            name of quest-slot to change
	 * @param index
	 *            index of sub state
	 * @param state
	 *            new value
	 */
	public SetQuestAction(final String questname, final int index, final String state) {
		this.questname = questname;
		this.index = index;
		this.state = state;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		if (index > -1) {
			player.setQuest(questname, index, state);
		} else {
			player.setQuest(questname, state);
		}
	}

	@Override
	public String toString() {
		return "SetQuest<" + questname + "[" + index + "] = " + state + ">";
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		if (questname == null) {
			result = PRIME;
		} else {
			result = PRIME	+ questname.hashCode();
		}
		if (state == null) {
			return PRIME * result;
		} else {
			return PRIME * result + state.hashCode();
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SetQuestAction other = (SetQuestAction) obj;
		if (questname == null) {
			if (other.questname != null) {
				return false;
			}
		} else if (!questname.equals(other.questname)) {
			return false;
		}
		if (state == null) {
			if (other.state != null) {
				return false;
			}
		} else if (!state.equals(other.state)) {
			return false;
		}
		return true;
	}

}
